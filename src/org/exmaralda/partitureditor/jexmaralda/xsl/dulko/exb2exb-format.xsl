<?xml version="1.0" encoding="utf-8"?>
<!-- format.xsl -->
<!-- Version 2.1 -->
<!-- Andreas Nolda 2018-10-14 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb.xsl"/>

<xsl:include href="format.xsl"/>

<xsl:template match="/basic-transcription">
  <basic-transcription>
    <xsl:apply-templates select="*[not(self::tierformat-table)]"/>
    <tierformat-table>
      <xsl:call-template name="format-table"/>
      <xsl:apply-templates select="basic-body/tier"
                                   mode="format"/>
    </tierformat-table>
  </basic-transcription>
</xsl:template>

<xsl:template match="tier"
              mode="format">
  <xsl:call-template name="format-tiers"/>
</xsl:template>
</xsl:stylesheet>
