<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-spans.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2018-01-09 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb.xsl"/>

<xsl:template match="tier[ends-with(@category,'S')]/event">
  <event>
    <xsl:copy-of select="@*"/>
    <xsl:text>s</xsl:text>
    <xsl:number/>
  </event>
</xsl:template>
</xsl:stylesheet>
