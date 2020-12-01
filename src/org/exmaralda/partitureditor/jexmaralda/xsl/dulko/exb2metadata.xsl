<?xml version="1.0" encoding="utf-8"?>
<!-- exb2metadata.xsl -->
<!-- Version 2.0 -->
<!-- Andreas Nolda 2019-01-26 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="metadata.xsl"/>

<xsl:output method="text"/>

<xsl:variable name="corpus-metadata">
  <xsl:call-template name="get-corpus-metadata"/>
</xsl:variable>

<xsl:template match="/">
  <xsl:for-each select="$corpus-metadata/meta[string-length()&gt;0]">
    <xsl:sort select="@variable"/>
    <xsl:value-of select="@variable"/>
    <xsl:text>=</xsl:text>
    <xsl:value-of select="."/>
    <xsl:text>&#xA;</xsl:text>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
