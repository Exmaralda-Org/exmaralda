<?xml version="1.0" encoding="utf-8"?>
<!-- format-table.xsl -->
<!-- Version 4.0 -->
<!-- Andreas Nolda 2023-09-19 -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!-- ApplyFormatStylesheetAction.java only supports XSLT 1.0 -->

<xsl:include href="format.xsl"/>

<xsl:output method="xml"/>

<xsl:template match="/">
  <tierformat-table>
    <xsl:call-template name="format-table"/>
    <xsl:apply-templates select="//tier"/>
  </tierformat-table>
</xsl:template>

<xsl:template match="tier">
  <xsl:call-template name="format-tiers"/>
</xsl:template>
</xsl:stylesheet>
