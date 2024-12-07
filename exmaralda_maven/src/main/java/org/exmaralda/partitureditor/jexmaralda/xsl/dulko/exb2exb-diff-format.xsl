<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-diff-format.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2017-12-29 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://saxon.sf.net/">

<xsl:import href="exb2exb-diff.xsl"/>

<xsl:output method="xml"
            indent="yes"
            saxon:next-in-chain="exb2exb-format.xsl"/>
</xsl:stylesheet>
