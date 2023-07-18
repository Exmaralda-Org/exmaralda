<?xml version="1.0" encoding="utf-8"?>
<!-- exb2text.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2021-07-10 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="text"/>

<xsl:template match="/">
  <xsl:value-of select="string-join(/basic-transcription/basic-body/tier[@category='word']/event,' ')"/>
  <xsl:text>&#xA;</xsl:text>
</xsl:template>
</xsl:stylesheet>
