<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!-- changes on 04-02-2009: -->
    <!-- symbol for clitics is now _ instead of + -->
    <!-- symbol for alternative is now / instaed of | -->
    <!-- symbol for breathe is now ° instead of _ -->
    <!-- symbol for unintelligible is now + instead of * -->
    <!-- changes on 06-03-2009: -->
    <!-- removed template for UNINTELLIGIBLE -->

    <xsl:template match="contribution">
        <xsl:element name="contribution">
            <xsl:copy-of select="@speaker-reference"/>
            <xsl:copy-of select="@start-reference"/>
            <xsl:copy-of select="@end-reference"/>
            <xsl:copy-of select="@parse-level"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="AD_WORD">
        <xsl:element name="w">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    
    <xsl:template match="AD_PUNCTUATION">
        <xsl:element name="p">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="time">
        <xsl:copy>
            <xsl:copy-of select="@timepoint-reference"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="AD_SPACE">
        <xsl:apply-templates select="*"/>
    </xsl:template>

   
    <xsl:template match="AD_NONVERBAL">
        <non-phonological>
            <xsl:attribute name="description">
                <xsl:choose>
                    <xsl:when test="starts-with(normalize-space(),'((')"><xsl:value-of select="normalize-space(substring-before(substring-after(text(),'(('), '))'))"/></xsl:when>
                    <xsl:when test="starts-with(normalize-space(), '{')"><xsl:value-of select="normalize-space(substring-before(substring-after(text(),'{'), '}'))"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="normalize-space()"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </non-phonological>
    </xsl:template>

    
</xsl:stylesheet>
