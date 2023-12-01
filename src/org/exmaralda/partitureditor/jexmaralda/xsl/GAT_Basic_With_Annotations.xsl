<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:apply-templates select="//speaker-contribution"/>
    </xsl:template>
    
    <xsl:template match="speaker-contribution">
        <xsl:variable name="COUNT" select="count(preceding-sibling::speaker-contribution) + 1"/>
        <xsl:variable name="SPEAKER_ID" select="@speaker"/>
        <xsl:variable name="SPEAKER_ABB" select="//speaker[@id=$SPEAKER_ID]/abbreviation"/>
        <xsl:value-of select="format-number($COUNT, '0000')" />
        <xsl:text>&#x09;</xsl:text> <!-- tab character -->
        <xsl:value-of select="$SPEAKER_ABB" />
        <xsl:text>:&#x09;</xsl:text> <!-- tab character -->        
        <xsl:value-of select="main/ts/ts/text()"/>
        <xsl:text>&#13;</xsl:text> <!-- carriage return character -->
        <xsl:for-each select="annotation">
            <xsl:text>    </xsl:text>
            <xsl:text>&#x09;</xsl:text> <!-- tab character -->
            <xsl:text>   </xsl:text>
            <xsl:text>&#x09;</xsl:text> <!-- tab character -->
            <xsl:for-each select="ta">
                <xsl:value-of select="text()"/>
                <xsl:if test="following-sibling::ta">
                    <xsl:text> | </xsl:text>
                </xsl:if>
            </xsl:for-each>
            
            <xsl:text>&#13;</xsl:text> <!-- carriage return character -->
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>