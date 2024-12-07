<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-displayname.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2019-10-21 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exb2exb.xsl"/>

<xsl:template match="tier/@display-name">
  <xsl:param name="category"/>
  <xsl:attribute name="display-name">
    <xsl:choose>
      <xsl:when test="/basic-transcription/head/speakertable/speaker[1]/abbreviation[string-length()&gt;4]">
        <xsl:value-of select="substring(/basic-transcription/head/speakertable/speaker[1]/abbreviation,1,4)"/>
        <xsl:text>... </xsl:text>
      </xsl:when>
      <xsl:when test="/basic-transcription/head/speakertable/speaker[1]/abbreviation[string-length()&gt;0]">
        <xsl:value-of select="/basic-transcription/head/speakertable/speaker[1]/abbreviation"/>
        <xsl:text> </xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:text>[</xsl:text>
    <xsl:value-of select="../@category"/>
    <xsl:text>]</xsl:text>
  </xsl:attribute>
</xsl:template>
</xsl:stylesheet>
