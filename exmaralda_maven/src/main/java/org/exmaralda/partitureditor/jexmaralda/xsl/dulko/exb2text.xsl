<?xml version="1.0" encoding="utf-8"?>
<!-- exb2text.xsl -->
<!-- Version 2.0 -->
<!-- Andreas Nolda 2023-09-27 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exb2exb-tiers.xsl"/>

<xsl:output method="text"/>

<xsl:template match="/basic-transcription">
  <xsl:value-of select="string-join(basic-body/tier[@id=$reference-id]/event,' ')"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>
</xsl:stylesheet>
