<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb.xsl -->
<!-- Version 3.0 -->
<!-- Andreas Nolda 2019-01-26 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="metadata.xsl"/>

<xsl:output method="xml"
            indent="yes"/>

<xsl:strip-space elements="*"/>

<xsl:template match="*">
  <xsl:copy>
    <xsl:apply-templates select="* |
                                 @* |
                                 text()"/>
  </xsl:copy>
</xsl:template>

<xsl:template match="@*">
  <xsl:copy/>
</xsl:template>

<xsl:template match="text()">
  <xsl:value-of select="."/>
</xsl:template>

<xsl:variable name="corpus-metadata">
  <xsl:call-template name="get-corpus-metadata"/>
</xsl:variable>

<!-- get updated annotation-related metadata -->
<xsl:template match="ud-information">
  <xsl:choose>
    <xsl:when test="@attribute-name='annotation' or
                    @attribute-name='annotation_other' or
                    @attribute-name='corpus_size' or
                    @attribute-name='error_annotated' or
                    @attribute-name='error_annotation_tool' or
                    @attribute-name='pos_tagged' or
                    @attribute-name='pos_tagset'">
      <ud-information>
        <xsl:copy-of select="@attribute-name"/>
        <xsl:value-of select="$corpus-metadata/meta[@variable=current()/@attribute-name]"/>
      </ud-information>
    </xsl:when>
    <xsl:otherwise>
      <xsl:copy-of select="."/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- clean up -->
<xsl:template match="ud-information[@attribute-name='AutoSave']"/>
</xsl:stylesheet>
