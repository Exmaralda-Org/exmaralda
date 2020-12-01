<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-txttag-txts-format.xsl -->
<!-- Version 3.1 -->
<!-- Andreas Nolda 2019-05-05 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://saxon.sf.net/">

<xsl:import href="exb2exb-tag.xsl"/>

<xsl:output method="xml"
            indent="yes"
            saxon:next-in-chain="exb2exb-txts-format.xsl"/>

<xsl:param name="zh-number">0</xsl:param>
</xsl:stylesheet>
