<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-displayname-format.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2019-10-21 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:saxon="http://saxon.sf.net/">

<xsl:import href="exb2exb-displayname.xsl"/>

<xsl:output method="xml"
            indent="yes"
            saxon:next-in-chain="exb2exb-format.xsl"/>
</xsl:stylesheet>
