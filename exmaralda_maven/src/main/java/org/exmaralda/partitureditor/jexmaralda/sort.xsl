<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <languages>
        <xsl:for-each select="//language">
            <xsl:sort select="@desc"/>
            <xsl:copy-of select="."/>
        </xsl:for-each>
        </languages>
    </xsl:template>
</xsl:stylesheet>
