<?xml version="1.0" encoding="utf-8"?>
<!-- format.xsl -->
<!-- Version 1.7 -->
<!-- Andreas Nolda 2023-07-17 -->

<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"><!-- ApplyFormatStylesheetAction.java only supports XSLT 1.0 -->

<xsl:param name="empty-editor-properties">
  <property name="bg-color">#REEGEEBEE</property><!-- grey -->
</xsl:param>

<xsl:param name="row-label-properties">
  <property name="font-size">18</property>
  <property name="font-name">Sans</property>
  <property name="bg-color">#REEGEEBEE</property><!-- grey -->
  <property name="text-alignment">Left</property>
</xsl:param>

<xsl:param name="column-label-properties">
  <property name="font-size">12</property>
  <property name="font-name">Sans</property>
  <property name="bg-color">#REEGEEBEE</property><!-- grey -->
</xsl:param>

<xsl:param name="transcription-tier-properties">
  <property name="font-name">Sans</property>
  <property name="font-face">Bold</property>
  <property name="font-size">18</property>
</xsl:param>

<xsl:param name="annotation-tier-properties">
  <property name="font-name">Sans</property>
  <property name="font-face">Plain</property>
  <property name="font-size">18</property>
</xsl:param>

<xsl:param name="diff-tier-properties">
  <property name="font-name">Sans</property>
  <property name="font-face">Plain</property>
  <property name="font-size">18</property>
  <property name="font-color">#R22GCCBAA</property><!-- green -->
</xsl:param>

<xsl:param name="layout-tier-properties">
  <property name="font-name">Sans</property>
  <property name="font-face">Plain</property>
  <property name="font-size">18</property>
  <property name="font-color">#R00G33B99</property><!-- blue -->
</xsl:param>

<xsl:param name="error-tier-properties">
  <property name="font-name">Sans</property>
  <property name="font-face">Plain</property>
  <property name="font-size">18</property>
  <property name="font-color">#RFFG22B66</property><!-- red -->
</xsl:param>

<xsl:template name="format-table">
  <tier-format tierref="EMPTY-EDITOR">
    <xsl:copy-of select="$empty-editor-properties"/>
  </tier-format>
  <tier-format tierref="ROW-LABEL">
    <xsl:copy-of select="$row-label-properties"/>
  </tier-format>
  <tier-format tierref="COLUMN-LABEL">
    <xsl:copy-of select="$column-label-properties"/>
  </tier-format>
</xsl:template>

<xsl:template name="format-tiers">
  <xsl:choose>
    <xsl:when test="@type='t' or
                    @category='orig' or
                    substring-after(@category,'::')='orig' or
                    @category='word' or
                    substring-after(@category,'::')='word' or
                    @category='ZH' or
                    substring-after(@category,'::')='ZH'">
      <tier-format>
        <xsl:attribute name="tierref">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:copy-of select="$transcription-tier-properties"/>
      </tier-format>
    </xsl:when>
    <xsl:when test="@category='Diff' or
                    substring-after(@category,'::')='Diff' or
                    @category='ZHDiff' or
                    substring-after(@category,'::')='ZHDiff'">
      <tier-format>
        <xsl:attribute name="tierref">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:copy-of select="$diff-tier-properties"/>
      </tier-format>
    </xsl:when>
    <xsl:when test="@category='Layout' or
                    substring-after(@category,'::')='Layout' or
                    @category='Graph' or
                    substring-after(@category,'::')='Graph'">
      <tier-format>
        <xsl:attribute name="tierref">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:copy-of select="$layout-tier-properties"/>
      </tier-format>
    </xsl:when>
    <xsl:when test="starts-with(@category,'Fehler') or
                    starts-with(substring-after(@category,'::'),'Fehler')">
      <tier-format>
        <xsl:attribute name="tierref">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:copy-of select="$error-tier-properties"/>
      </tier-format>
    </xsl:when>
    <xsl:otherwise>
      <tier-format>
        <xsl:attribute name="tierref">
          <xsl:value-of select="@id"/>
        </xsl:attribute>
        <xsl:copy-of select="$annotation-tier-properties"/>
      </tier-format>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
</xsl:stylesheet>
