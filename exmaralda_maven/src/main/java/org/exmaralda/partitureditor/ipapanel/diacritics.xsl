<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <doc>
            <xsl:for-each select="//rect">
                <xsl:sort select="@x" data-type="number"/>
                <xsl:sort select="@y" data-type="number"/>
                <phoneme>
                     <xsl:attribute name="x"><xsl:value-of select="round((@x + 5526) * (20 div 67))"/></xsl:attribute>
                    <xsl:attribute name="y"><xsl:value-of select="round((@y + 90) * (20 div 67))"/></xsl:attribute>
                    <unicode>o</unicode>
                    <description> XXX </description>
                    <display> AAA </display>
                </phoneme>                
            </xsl:for-each>
        </doc>
    </xsl:template>
</xsl:stylesheet>
