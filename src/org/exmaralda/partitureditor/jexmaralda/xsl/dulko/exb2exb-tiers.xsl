<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-tiers.xsl -->
<!-- Version 1.4 -->
<!-- Andreas Nolda 2019-05-05 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb.xsl"/>

<xsl:param name="zh-number">
  <xsl:value-of select="count(/basic-transcription/basic-body/tier[@category='ZH'])"/>
</xsl:param>
<!-- "0": word tier, "1": first ZH tier, etc. -->

<xsl:variable name="reference-id">
  <xsl:choose>
    <xsl:when test="string(number($zh-number))='NaN'">
      <xsl:message terminate="yes">Error: The $zh-number value "<xsl:value-of select="$zh-number"/>" is not a number.</xsl:message>
    </xsl:when>
    <xsl:when test="$zh-number=0">
      <xsl:choose>
        <xsl:when test="/basic-transcription/basic-body/tier[@category=$word]">
          <xsl:value-of select="/basic-transcription/basic-body/tier[@category=$word]/@id"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="/basic-transcription/basic-body/tier[1]/@id"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:when>
    <xsl:when test="$zh-number&gt;0">
      <xsl:value-of select="/basic-transcription/basic-body/tier[@category='ZH'][position()=$zh-number]/@id"/>
    </xsl:when>
  </xsl:choose>
</xsl:variable>

<xsl:template match="/">
  <xsl:choose>
    <xsl:when test="/basic-transcription/basic-body/tier[contains(@category,'::')]">
      <xsl:message terminate="yes">Error: There are tiers with namespace prefixes.</xsl:message>
    </xsl:when>
    <xsl:when test="string-length($reference-id)=0">
      <xsl:message terminate="yes">Error: There is no reference tier for the $zh-number value "<xsl:value-of select="$zh-number"/>".</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="tier">
  <xsl:param name="old-current-tier-number">0</xsl:param>
  <xsl:param name="new-current-tier-number">0</xsl:param>
  <tier>
    <xsl:attribute name="id">
      <xsl:text>TIE</xsl:text>
      <xsl:value-of select="count(preceding-sibling::tier) -
                            $old-current-tier-number +
                            $new-current-tier-number"/>
    </xsl:attribute>
    <xsl:copy-of select="@*[not(name()='id')]"/>
    <xsl:apply-templates/>
  </tier>
</xsl:template>

<xsl:template name="tier">
  <xsl:param name="preceding-tier-number">0</xsl:param>
  <xsl:param name="category"/>
  <xsl:param name="type"/>
  <xsl:param name="events"/>
  <tier>
    <xsl:attribute name="id">
      <xsl:text>TIE</xsl:text>
      <xsl:value-of select="$preceding-tier-number"/>
    </xsl:attribute>
    <xsl:if test="/basic-transcription/head/speakertable/speaker[1]/@id">
      <xsl:attribute name="speaker">
        <xsl:value-of select="/basic-transcription/head/speakertable/speaker[1]/@id"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="string-length($category)&gt;0">
      <xsl:attribute name="category">
        <xsl:value-of select="$category"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="string-length($type)&gt;0">
      <xsl:attribute name="type">
        <xsl:value-of select="$type"/>
      </xsl:attribute>
    </xsl:if>
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
      <xsl:value-of select="$category"/>
      <xsl:text>]</xsl:text>
    </xsl:attribute>
    <xsl:copy-of select="$events"/>
  </tier>
</xsl:template>
</xsl:stylesheet>
