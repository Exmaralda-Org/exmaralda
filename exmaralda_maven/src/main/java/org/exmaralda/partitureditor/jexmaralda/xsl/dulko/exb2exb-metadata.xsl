<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-metadata.xsl -->
<!-- Version 3.0 -->
<!-- Andreas Nolda 2020-12-01 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb.xsl"/>

<xsl:param name="template-file">/org/exmaralda/dulko/resources/dulko-template.exb</xsl:param>

<xsl:param name="template-head"
           select="document($template-file)/basic-transcription/head"/>

<xsl:template name="import-content">
  <xsl:param name="source"/>
  <xsl:choose>
    <xsl:when test="string-length()=0">
      <xsl:value-of select="$source"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="project-name">
  <project-name>
    <xsl:call-template name="import-content">
      <xsl:with-param name="source"
                      select="$template-head/meta-information/project-name"/>
    </xsl:call-template>
  </project-name>
</xsl:template>

<xsl:template match="transcription-convention">
  <transcription-convention>
    <xsl:call-template name="import-content">
      <xsl:with-param name="source"
                      select="$template-head/meta-information/transcription-convention"/>
    </xsl:call-template>
  </transcription-convention>
</xsl:template>

<xsl:template match="head/meta-information/comment">
  <comment>
    <xsl:call-template name="import-content">
      <xsl:with-param name="source"
                      select="$template-head/meta-information/comment"/>
    </xsl:call-template>
  </comment>
</xsl:template>

<xsl:template match="head/speakertable/speaker/comment">
  <comment>
    <xsl:call-template name="import-content">
      <xsl:with-param name="source"
                      select="$template-head/speakertable/speaker[1]/comment"/>
    </xsl:call-template>
  </comment>
</xsl:template>

<xsl:template name="import-ud-information">
  <xsl:param name="source"/>
  <xsl:for-each select="ud-information">
    <xsl:choose>
      <xsl:when test="string-length()=0">
        <xsl:apply-templates select="$source/ud-information[@attribute-name=current()/@attribute-name]"/>
      </xsl:when>
      <xsl:when test=".='---unknown---'">
        <xsl:apply-templates select="$source/ud-information[@attribute-name=current()/@attribute-name]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
  <xsl:for-each select="$source/ud-information[not(@attribute-name=current()/ud-information/@attribute-name)]">
    <xsl:apply-templates select="."/>
  </xsl:for-each>
</xsl:template>

<xsl:template match="ud-meta-information">
  <ud-meta-information>
    <xsl:call-template name="import-ud-information">
      <xsl:with-param name="source"
                      select="$template-head/meta-information/ud-meta-information"/>
    </xsl:call-template>
  </ud-meta-information>
</xsl:template>

<xsl:template match="ud-speaker-information">
  <ud-speaker-information>
    <xsl:call-template name="import-ud-information">
      <xsl:with-param name="source"
                      select="$template-head/speakertable/speaker[1]/ud-speaker-information"/>
    </xsl:call-template>
  </ud-speaker-information>
</xsl:template>

<xsl:template name="import-languages">
  <xsl:param name="source"/>
  <xsl:choose>
    <xsl:when test="language">
      <xsl:apply-templates select="language"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates select="$source"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="languages-used">
  <languages-used>
    <xsl:call-template name="import-languages">
      <xsl:with-param name="source"
                      select="$template-head/speakertable/speaker[1]/languages-used/language"/>
    </xsl:call-template>
  </languages-used>
</xsl:template>

<xsl:template match="l1">
  <l1>
    <xsl:call-template name="import-languages">
      <xsl:with-param name="source"
                      select="$template-head/speakertable/speaker[1]/l1/language"/>
    </xsl:call-template>
  </l1>
</xsl:template>

<xsl:template match="l2">
  <l2>
    <xsl:call-template name="import-languages">
      <xsl:with-param name="source"
                      select="$template-head/speakertable/speaker[1]/l2/language"/>
    </xsl:call-template>
  </l2>
</xsl:template>
</xsl:stylesheet>
