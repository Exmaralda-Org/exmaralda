<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:text>***** FOLKER changes *******</xsl:text>
        <xsl:text>&#x0D;&#x0A;</xsl:text>
        <xsl:text>&#x0D;&#x0A;</xsl:text>
        <xsl:for-each-group select="//change[@tool='folker']" group-by="@tool">
            <xsl:text>=================</xsl:text>
            <xsl:text>&#x0D;&#x0A;</xsl:text>
            <xsl:value-of select="current-grouping-key()"/>
                    <xsl:text>&#x0D;&#x0A;</xsl:text>
                    <xsl:text>=================</xsl:text>
                    <xsl:text>&#x0D;&#x0A;</xsl:text>
            <xsl:for-each select="current-group()">
                        <xsl:value-of select="@date"/>
                        <xsl:text>                 </xsl:text>
                        <xsl:value-of select="text()"/>
                        <xsl:text>&#x0D;&#x0A;</xsl:text>
                    </xsl:for-each>
                </xsl:for-each-group>
    </xsl:template>
</xsl:stylesheet>
