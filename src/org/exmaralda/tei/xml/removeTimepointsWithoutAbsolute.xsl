<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="@*|node()">
        <xsl:message>DEFAULT TEMPLATE</xsl:message>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:variable name="ITEM-COUNT">
        <xsl:message>ITEM-COUNT START</xsl:message>
        <items>
            <xsl:for-each-group select="//@*[name()='start' or name()='end' or name()='from' or name()='to']" group-by=".">
                <item>
                    <xsl:attribute name="id" select="current-grouping-key()"/>
                    <xsl:attribute name="item-count" select="count(current-group())"/>
                </item>
            </xsl:for-each-group>
        </items>
        <xsl:message>ITEM-COUNT END</xsl:message>
    </xsl:variable>
    
    <xsl:variable name="TIMELINE_MAP">
        <xsl:message>TIMELINE START</xsl:message>
        <timeline>
            <xsl:for-each select="//tei:when[not(@interval) and not(@since)]">
                <xsl:variable name="ID" select="@xml:id"/>
                <when>
                    <xsl:attribute name="id" select="$ID"/>
                    <xsl:attribute name="anchor-count" select="count(//tei:anchor[@synch=$ID])"/>
                    <!-- <xsl:attribute name="item-count" select="count(//*[@start=$ID or @end=$ID or @from=$ID or @to=$ID or contains(@corresp, $ID)])"/> -->
                    <xsl:attribute name="item-count">
                        <xsl:choose>
                            <xsl:when test="$ITEM-COUNT/descendant::item[@id=$ID]">
                                <xsl:value-of select="$ITEM-COUNT/descendant::item[@id=$ID]/@item-count"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="0"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </when>
            </xsl:for-each>
        </timeline>
        <xsl:message>TIMELINE END</xsl:message>
    </xsl:variable>
    
    <xsl:variable name="ANCHOR_MAP">
        <xsl:message>ANCHOR START</xsl:message>
        <anchors>
            <xsl:for-each-group select="//tei:anchor" group-by="@synch">
                <anchor>
                    <xsl:attribute name="synch" select="current-grouping-key()"/>
                    <xsl:attribute name="anchor-count" select="count(current-group())"/>
                    <xsl:attribute name="span-count" select="count(//tei:span[@from=current-grouping-key() or @to=current-grouping-key()])"/>
                </anchor>
            </xsl:for-each-group>
        </anchors>    
        <xsl:message>ANCHOR END</xsl:message>
    </xsl:variable>    
    
    
    <xsl:template match="tei:when[not(@interval) and not(@since)]">
        <xsl:variable name="ID" select="@xml:id"/>
        <xsl:message>WHEN TEMPLATE <xsl:value-of select="$ID"/></xsl:message>
        <xsl:choose>
            <!-- <xsl:when test="count(//tei:anchor[@synch=$ID])&gt;1"> -->
            <xsl:when test="$TIMELINE_MAP/descendant::when[@id=$ID] and $TIMELINE_MAP/descendant::when[@id=$ID]/@anchor-count &gt; 1">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>                                
            </xsl:when>
            <!-- <xsl:when test="//tei:when[@since=$ID]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                                                
            </xsl:when> -->
            <!-- <xsl:when test="//*[@start=$ID or @end=$ID or @from=$ID or @to=$ID or contains(@corresp, $ID)]"> -->
            <xsl:when test="$TIMELINE_MAP/descendant::when[@id=$ID] and $TIMELINE_MAP/descendant::when[@id=$ID]/@item-count &gt; 0">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>                                                
            </xsl:when>
            <xsl:otherwise/>            
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:anchor">
        <xsl:variable name="ID" select="@synch"/>
        <xsl:message>ANCHOR TEMPLATE <xsl:value-of select="$ID"/></xsl:message>
        <xsl:choose>
            <xsl:when test="//tei:when[@xml:id=$ID][@interval or @since]">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>                                 
            </xsl:when>
            <!-- <xsl:when test="count(//tei:anchor[@synch=$ID])&lt;1"> -->
            <xsl:when test="$ANCHOR_MAP/descendant::anchor[@synch=$ID]/@anchor-count &gt; 1">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>
            </xsl:when>
            <!-- <xsl:when test="//tei:span[@from=$ID or @to=$ID]"> -->
            <xsl:when test="$ANCHOR_MAP/descendant::anchor[@synch=$ID]/@span-count &gt; 0">
                <xsl:copy>
                    <xsl:apply-templates select="@*"/>
                </xsl:copy>                
            </xsl:when>
            <xsl:otherwise/>          
        </xsl:choose>
    </xsl:template>
    
    <!-- we need to keep the redundant <anchor>s in the <seg>s -->
    <xsl:template match="//tei:seg/*[1][@synch] | //tei:seg/*[last()][@synch]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>                
    </xsl:template>    
    
    
</xsl:stylesheet>