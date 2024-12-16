<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs math"
    version="3.0">
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:seg[@type='contribution']">
        <xsl:variable name="TOKENS">
            <seg>
                <xsl:for-each select="*">
                    <xsl:copy>
                        <xsl:attribute name="count-iu">
                            <xsl:choose>
                                <xsl:when test="not(self::tei:anchor)">
                                    <xsl:value-of select="count(preceding-sibling::tei:pc[matches(text(),'[\.\?\-;,]')]) + 1"/>        
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="count(preceding-sibling::tei:pc[matches(text(),'[\.\?\-;,]')])"/>                                            
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>                             
                        <xsl:apply-templates select="@*|node()"/>
                    </xsl:copy>                
                </xsl:for-each>
            </seg>
        </xsl:variable>
        
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each-group select="$TOKENS/seg/*" group-by="@count-iu">
                <xsl:apply-templates select="current-group()[1]/preceding-sibling::tei:anchor[1]"/>
                <xsl:if test="not(current-grouping-key()='0')">
                    <tei:seg type="intonation-unit">
                        <xsl:attribute name="xml:id" select="generate-id()"/>
                        <xsl:attribute name="subtype">
                            <xsl:choose>
                                <xsl:when test="current-group()/descendant-or-self::tei:pc[last()]/text()='.'">low-falling</xsl:when>
                                <xsl:when test="current-group()/descendant-or-self::tei:pc[last()]/text()=';'">mid-falling</xsl:when>
                                <xsl:when test="current-group()/descendant-or-self::tei:pc[last()]/text()='-'">level</xsl:when>
                                <xsl:when test="current-group()/descendant-or-self::tei:pc[last()]/text()=','">mid-rising</xsl:when>
                                <xsl:when test="current-group()/descendant-or-self::tei:pc[last()]/text()='?'">high-rising</xsl:when>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:apply-templates select="current-group()[not(self::tei:anchor)]"/>
                    </tei:seg>
                </xsl:if>
            </xsl:for-each-group>
            <xsl:copy-of select="tei:anchor[last()]"/>
        </xsl:copy>
        

    </xsl:template>
    
    <xsl:template match="@count-iu"/>
    
</xsl:stylesheet>