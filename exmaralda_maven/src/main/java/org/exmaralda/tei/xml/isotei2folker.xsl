<?xml version="1.0" encoding="UTF-8"?>
    <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:tei="http://www.tei-c.org/ns/1.0"            
        exclude-result-prefixes="xs"
        version="2.0">
        <xsl:output method="xml" encoding="UTF-8"/>
        <xsl:strip-space elements="*"/>
        
        <!-- <transcriptionDesc ident="HIAT" version="2004"> -->
        <xsl:param name="TRANSCRIPTION_SYSTEM" select="//tei:transcriptionDesc/@ident"/>
        
        <xsl:template match="/">
            <folker-transcription>
                <xsl:if test="/tei:TEI/tei:idno[@type='AGD-ID']">
                    <xsl:attribute name="dgd-id" select="/tei:TEI/tei:idno[@type='AGD-ID']"/>
                    <xsl:attribute name="corpus-dgd-id" select="substring(/tei:TEI/tei:idno[@type='AGD-ID'],1, 4)"/>
                </xsl:if>
                <head></head>
                <xsl:call-template name="MAKE_SPEAKERTABLE"/>
                <recording>
                    <xsl:attribute name="path"><xsl:value-of select="//tei:media[ends-with(upper-case(@url), 'WAV')]/@url"/></xsl:attribute>
                </recording>
                <xsl:call-template name="MAKE_TIMELINE"/>
                <xsl:apply-templates select="//tei:body/*"/>
            </folker-transcription>
        </xsl:template>
        
        <xsl:template match="tei:annotationBlock">
            <xsl:variable name="WHO" select="@who"/>
            <contribution>
                <xsl:attribute name="speaker-reference" select="//tei:person[@xml:id=$WHO]/@n"/>
                <xsl:attribute name="start-reference" select="@start"/>
                <xsl:attribute name="end-reference" select="@end"/>
                <xsl:attribute name="parse-level">2</xsl:attribute>
                <xsl:attribute name="id" select="@xml:id"/>
                <xsl:apply-templates select="tei:u"/>
            </contribution>
        </xsl:template>
        
        <xsl:template match="tei:seg">
            <xsl:apply-templates select="*[not(self::tei:anchor and not(following-sibling::*))]"/>
            <xsl:if test="$TRANSCRIPTION_SYSTEM='HIAT'">
                <xsl:choose>
                    <xsl:when test="@subtype='declarative'"><p>.</p></xsl:when>
                    <xsl:when test="@subtype='interrogative'"><p>?</p></xsl:when>
                    <xsl:when test="@subtype='exclamative'"><p>!</p></xsl:when>
                    <xsl:when test="@subtype='interrupted'"><p>&#x2026;</p></xsl:when>
                    <xsl:when test="@subtype='not_classified'"><p>&#x02D9;</p></xsl:when>
                </xsl:choose>
            </xsl:if>
            <xsl:apply-templates select="*[self::tei:anchor and not(following-sibling::*)]"/>
        </xsl:template>
        
        <!-- is it true that I like totally forgot anchors??? -->
        <xsl:template match="tei:anchor">
            <xsl:variable name="PARENT-ID" select="ancestor-or-self::tei:annotationBlock/@xml:id"/>
            <xsl:variable name="SYNCH" select="@synch"/>
            <xsl:if test="not(preceding::tei:anchor[@synch=$SYNCH and ancestor::tei:annotationBlock/@xml:id=$PARENT-ID])">
                <!--         <time timepoint-reference="TLI_0" time="0.0"/> -->
                <time>
                    <xsl:attribute name="timepoint-reference" select="$SYNCH"/>
                </time>
            </xsl:if>
        </xsl:template>
        
        <xsl:template match="tei:w">
            <xsl:variable name="WORD_ID" select="@xml:id"/>
            <w>
                <xsl:attribute name="id" select="@xml:id"/>
                <xsl:if test="@norm">
                    <xsl:attribute name="n" select="@norm"/>
                </xsl:if>
                <xsl:if test="@lemma">
                    <xsl:attribute name="lemma" select="@lemma"/>
                </xsl:if>
                <xsl:if test="@pos">
                    <xsl:attribute name="pos" select="@pos"/>
                </xsl:if>                
                <xsl:value-of select="text()"/>            
            </w>                    
        </xsl:template>
        
        <xsl:template match="tei:pc">
            <p>
                <xsl:attribute name="id" select="@xml:id"/>
                <xsl:value-of select="text()"/>            
            </p>                    
        </xsl:template>
        
        <xsl:template match="tei:seg/tei:pause">
            <pause>
                <xsl:attribute name="id" select="@xml:id"/>
                <xsl:choose>
                    <!-- <pause xml:id="p495" rend="(0.71)" dur="PT0.71S" start="TLI_950" end="TLI_951"/>  -->
                    <!--         <pause duration="0.56" id="p90"/> -->
                    <!-- <pause xml:id="p4" rend="(.)" type="micro"/> -->
                    <xsl:when test="@type">
                        <!-- <pause duration="micro" id="p21"/> -->
                        <xsl:attribute name="duration" select="@type"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="DURATION" select="substring-after(substring-before(@dur,'S'), 'PT')"/>
                        <xsl:attribute name="duration" select="$DURATION"/>
                    </xsl:otherwise>
                </xsl:choose>
            </pause>
        </xsl:template>
        
        <!-- <vocal xml:id="b1">
            <desc rend="°h">short breathe in</desc>
        </vocal> -->
        <xsl:template match="tei:seg/tei:vocal | tei:seg/tei:incident">
            <!-- <non-phonological description="Stimmengewirr, 4.1s" id="n1"/> -->
            <!--         <breathe type="in" length="1" id="b1"/>  -->            
            <xsl:choose>
                <xsl:when test="matches(tei:desc/@rend, '(°(h){1,3}|(h){1,3}°)')">
                    <breathe>
                        <xsl:attribute name="length" select="string-length(tei:desc/@rend) - 1"/>
                        <xsl:attribute name="id" select="@xml:id"/>
                        <xsl:attribute name="type">
                            <xsl:choose>
                                <xsl:when test="starts-with(tei:desc/@rend, '°')">in</xsl:when>
                                <xsl:otherwise>out</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </breathe>                
                </xsl:when>
                <xsl:otherwise>
                    <non-phonological>
                        <xsl:attribute name="id" select="@xml:id"/>
                        <xsl:attribute name="description" select="tei:desc"/>
                    </non-phonological>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:template>
        
        <xsl:template match="tei:body/tei:incident">
            <contribution parse-level="2">
                <xsl:attribute name="id" select="concat('c_NP_', @xml:id)"></xsl:attribute>
                <xsl:attribute name="start-reference" select="@start"/>
                <xsl:attribute name="end-reference" select="@end"/>
                <time><xsl:attribute name="timepoint-reference" select="@start"/></time>
                <non-phonological>
                    <xsl:attribute name="id" select="@xml:id"/>
                    <xsl:attribute name="description" select="tei:desc"/>
                </non-phonological>
                <time><xsl:attribute name="timepoint-reference" select="@end"/></time>
            </contribution>
        </xsl:template>
        
        <xsl:template match="tei:body/tei:pause">
            <!--  
                <pause xml:id="p2" rend="(0.36)" dur="PT0.36S" start="TLI_2" end="TLI_3"/>
                <contribution start-reference="TLI_39" end-reference="TLI_40" parse-level="2" id="c37"
                    time="44.199">
                    <time timepoint-reference="TLI_39" time="44.199"/>
                    <pause duration="4.15" id="p23"/>
                    <time timepoint-reference="TLI_40" time="48.35"/>
                </contribution>
            -->
            <contribution parse-level="2">
                <xsl:attribute name="id" select="concat('c_P_', @xml:id)"></xsl:attribute>
                <xsl:attribute name="start-reference" select="@start"/>
                <xsl:attribute name="end-reference" select="@end"/>
                <time><xsl:attribute name="timepoint-reference" select="@start"/></time>
                <pause>
                    <xsl:attribute name="id" select="@xml:id"/>
                    <xsl:choose>
                        <xsl:when test="@type">
                            <xsl:attribute name="duration" select="@type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:variable name="DURATION" select="substring-after(substring-before(@dur,'S'), 'PT')"/>
                            <xsl:attribute name="duration" select="$DURATION"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </pause>
                <time><xsl:attribute name="timepoint-reference" select="@end"/></time>
            </contribution>
        </xsl:template>
        
        
        <xsl:template name="MAKE_SPEAKERTABLE">
            <speakers>
                <xsl:for-each select="//tei:person">
                    <speaker>
                        <xsl:if test="tei:idno[@type='AGD-ID']">
                            <xsl:attribute name="dgd-id" select="tei:idno[@type='AGD-ID']"/>
                        </xsl:if>
                        <xsl:attribute name="speaker-id" select="@n"/>
                        <name></name>
                    </speaker>
                </xsl:for-each>
            </speakers>        
        </xsl:template>
        
        <xsl:template name="MAKE_TIMELINE">
            <timeline>
                <!-- <timepoint timepoint-id="TLI_0" absolute-time="0.0"/> -->
                <xsl:for-each select="//tei:when[@interval]">
                    <timepoint>
                        <xsl:attribute name="timepoint-id" select="@xml:id"/>
                        <xsl:attribute name="absolute-time" select="@interval"/>
                    </timepoint>
                </xsl:for-each>            
            </timeline>
        </xsl:template>
</xsl:stylesheet>