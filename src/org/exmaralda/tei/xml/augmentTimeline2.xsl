<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    
    <!-- Make an individual timeline for each speaker -->
    <!-- this will contain all the when elements for existing anchors -->
    <!-- plus a when element for each token with xml:id that has a preceding token with xml:id -->
    <!-- The whole thing looks as follows -->
    <!-- 
            <timelines xmlns:tei="http://www.tei-c.org/ns/1.0">
                <timeline who="SPK1">
                    <when id="T0"/>
                    <when token-id="w2"/>
                    <when token-id="w3"/>
                    <when token-id="w4"/>
                    <when token-id="w5"/>
                    <when token-id="w6"/>
                    <when id="T1"/>
                    <when id="T2"/>
                    <when id="T3"/>
                    <when id="T4"/>
                    <when id="T5"/>
                </timeline>
                <timeline who="SPK0">
                    <when id="T1"/>
                    <when token-id="w8"/>
                    <when id="T2"/>
                    <when token-id="w9"/>
                    <when token-id="w10"/>
                    <when token-id="w11"/>
                    <when id="T3"/>
                    <when id="T4"/>
                    <when id="T5"/>
                    <when token-id="w13"/>
                    <when id="T6"/>
                    <when token-id="w15"/>
                    <when token-id="w16"/>
                    <when token-id="pc1"/>
                    <when token-id="w17"/>
                    <when token-id="w18"/>
                    <when id="T7"/>
                </timeline>
            </timelines>
    
    
    -->
    <xsl:variable name="SPEAKER_TIMELINES">
        <timelines>
            <xsl:for-each-group select="//tei:annotationBlock" group-by="@who">
                <timeline>
                    <xsl:attribute name="who" select="current-grouping-key()"/>
                    <xsl:for-each select="current-group()">
                        <xsl:variable name="AB_START" select="@start"/>
                        <xsl:variable name="AB_END" select="@end"/>
                        <xsl:if test="not(descendant::tei:anchor[@synch=$AB_START])">
                            <when id="{$AB_START}"/>
                        </xsl:if>
                        <xsl:for-each select="descendant::*[not(child::*)]">
                            <xsl:choose>
                                <xsl:when test="self::tei:anchor">
                                    <when>
                                        <xsl:attribute name="id" select="@synch"/>
                                    </when>
                                </xsl:when>
                                <xsl:when test="@xml:id and preceding-sibling::*[@xml:id] and not(preceding-sibling::*[1][self::tei:anchor])">
                                    <when>
                                        <xsl:attribute name="token-id" select="@xml:id"/>
                                    </when>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:for-each>
                        <xsl:if test="not(descendant::tei:anchor[@synch=$AB_END])">
                            <when id="{$AB_END}"/>
                        </xsl:if>
                    </xsl:for-each>
                </timeline>
            </xsl:for-each-group>
        </timelines>
    </xsl:variable>
    
    
    <!-- now construct the common timeline which is defined as
        all of the whens of the original timeline plus those
        from the individual timelines that lie between such whens iff
        there is only one such "timeline" fork
    -->
    <xsl:variable name="COMMON_TIMELINE">
        <common-timeline>
            <xsl:for-each select="//tei:when[following-sibling::tei:when]">
                <xsl:variable name="THIS_ID" select="@xml:id"/>
                <xsl:variable name="NEXT_ID" select="following-sibling::tei:when[1]/@xml:id"/>
                <xsl:copy-of select="current()"/>
                <xsl:for-each-group select="$SPEAKER_TIMELINES/descendant::when[
                    preceding-sibling::when[@id=$THIS_ID] and following-sibling::when[@id=$NEXT_ID]]"
                    group-by="@token-id"
                    >
                    <xsl:if test="count(current-group()=1)">
                        <xsl:copy-of select="current()"/>
                    </xsl:if>
                </xsl:for-each-group>
            </xsl:for-each>
            <xsl:copy-of select="//tei:when[last()]"/>
        </common-timeline>
    </xsl:variable>   
    
    <!-- this one copies everything except those elements with more specific templates -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- the output timeline is more or less a copy of the common timeline 
        with some ID magic being done -->
    <xsl:template match="tei:timeline">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each select="$COMMON_TIMELINE/descendant::*:when">
                <xsl:choose>
                    <xsl:when test="@xml:id">
                        <xsl:copy>
                            <xsl:apply-templates select="@*"/>
                        </xsl:copy>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:element name="when" namespace="http://www.tei-c.org/ns/1.0">
                            <xsl:attribute name="xml:id">
                                <xsl:value-of select="preceding-sibling::tei:when[@xml:id][1]/@xml:id"/>
                                <xsl:text>_</xsl:text>
                                <xsl:value-of select="@token-id"/>
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>    
    
    <!-- this is about leaf descendants of annotation blocks, i.e. w, pc etc.
        they are copied in all cases and ... -->
    <xsl:template match="*[@xml:id and preceding-sibling::*[@xml:id] and ancestor::tei:annotationBlock and not(child::*)]">
        <xsl:variable name="THIS_ID" select="@xml:id"/>
        <!-- ... if their id figures in the common timeline, a corresponding anchor is inserted --> 
        <xsl:if test="$COMMON_TIMELINE/descendant::when[@token-id=$THIS_ID]">
            <xsl:element name="anchor" namespace="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="synch">
                    <xsl:value-of select="$COMMON_TIMELINE/descendant::when[@token-id=$THIS_ID]/preceding-sibling::tei:when[@xml:id][1]/@xml:id"/>
                    <xsl:text>_</xsl:text>
                    <xsl:value-of select="@xml:id"/>                    
                </xsl:attribute>
            </xsl:element>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>            
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>