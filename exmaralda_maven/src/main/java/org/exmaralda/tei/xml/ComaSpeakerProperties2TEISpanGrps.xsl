<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <fragment>
            <xsl:apply-templates select="//Speaker/*[not(self::Language or self::Description or self::Location)]"/>
            <xsl:apply-templates select="//Speaker/Description"/>
            <xsl:for-each-group select="//Speaker/Language" group-by="@Type">
                <xsl:variable name="ALL_LANGS">
                    <xsl:for-each select="current-group()">
                        <xsl:value-of select="LanguageCode"/><xsl:text> </xsl:text>
                    </xsl:for-each>
                </xsl:variable>
                <spanGrp>
                    <xsl:attribute name="type">speaker:language:<xsl:value-of select="translate(current-grouping-key(),' ', '_')"/></xsl:attribute>
                    <span>
                        <xsl:value-of select="translate(normalize-space($ALL_LANGS), ' ', '; ')"/>
                    </span>
                </spanGrp>
            </xsl:for-each-group>
        </fragment>
    </xsl:template>
    <xsl:template match="//Speaker/*[not(self::Language or self::Description or self::Location)]">
        <spanGrp>
            <xsl:attribute name="type"><xsl:text>speaker:</xsl:text><xsl:value-of select="name()"/></xsl:attribute>
            <span><xsl:value-of select="text()"/></span>
        </spanGrp>
    </xsl:template>
    <xsl:template match="//Speaker/Description">
        <xsl:for-each select="Key">
            <spanGrp>
                <xsl:attribute name="type"><xsl:text>speaker:</xsl:text><xsl:value-of select="translate(@Name, ' ', '_')"/></xsl:attribute>
                <span><xsl:value-of select="text()"/></span>
            </spanGrp>
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="//Speaker/Location"/>
    
</xsl:stylesheet>