<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="text" omit-xml-declaration="yes" />
<xsl:template match="/"># exmaralda-versions 
comaversion=<xsl:value-of select="//change[@tool='coma'][1]/@version" />
comareleased=<xsl:value-of select="//change[@tool='coma'][released='true'][1]/@version" />
partitureditorversion=<xsl:value-of select="//change[@tool='partitureditor'][1]/@version" />
exaktversion=<xsl:value-of select="//change[@tool='exakt'][1]/@version" />
</xsl:template>
<!-- new -->
</xsl:stylesheet>