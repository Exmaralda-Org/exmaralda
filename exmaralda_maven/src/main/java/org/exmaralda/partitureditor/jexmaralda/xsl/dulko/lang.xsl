<?xml version="1.0" encoding="utf-8"?>
<!-- lang.xsl -->
<!-- Version 1.2 -->
<!-- Andreas Nolda 2024-06-17 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:tt="java:org.exmaralda.dulko.treetagger.TreeTagger"
                exclude-result-prefixes="xs tt">

<!-- code of language used in the speaker table -->
<xsl:param name="lang">
  <xsl:if test="/basic-transcription/head/speakertable/speaker/languages-used/language[@lang]">
    <!-- Select the first language used in the speaker table. -->
    <xsl:value-of select="/basic-transcription/head/speakertable/speaker/languages-used/language[1]/@lang"/>
  </xsl:if>
</xsl:param>

<!-- TreeTagger language name -->
<!-- matches basenames of TreeTagger parameter files and abbreviation files (if any) -->
<xsl:param name="tagger-lang">
  <xsl:choose>
    <xsl:when test="$lang='als'">albanian</xsl:when><!-- sic! -->
    <xsl:when test="$lang='bel'">belarusian</xsl:when>
    <xsl:when test="$lang='bul'">bulgarian</xsl:when>
    <xsl:when test="$lang='csc'">catalan</xsl:when><!-- sic! -->
    <xsl:when test="$lang='cse'">czech</xsl:when><!-- sic! -->
    <xsl:when test="$lang='dan'">danish</xsl:when>
    <xsl:when test="$lang='deu'">german</xsl:when>
    <xsl:when test="$lang='ell'">greek</xsl:when>
    <xsl:when test="$lang='eng'">english</xsl:when>
    <xsl:when test="$lang='est'">estonian</xsl:when>
    <xsl:when test="$lang='fin'">finnish</xsl:when>
    <xsl:when test="$lang='fra'">french</xsl:when>
    <xsl:when test="$lang='glg'">galician</xsl:when>
    <xsl:when test="$lang='hun'">hungarian</xsl:when>
    <xsl:when test="$lang='ita'">italian</xsl:when>
    <xsl:when test="$lang='lat'">latin</xsl:when>
    <xsl:when test="$lang='nld'">dutch</xsl:when>
    <xsl:when test="$lang='nor'">norwegian</xsl:when>
    <xsl:when test="$lang='pol'">polish</xsl:when>
    <xsl:when test="$lang='por'">portuguese</xsl:when>
    <xsl:when test="$lang='rms'">romanian</xsl:when><!-- sic! -->
    <xsl:when test="$lang='spa'">spanish</xsl:when>
    <xsl:when test="$lang='swe'">swedish</xsl:when>
    <xsl:when test="$lang='ukr'">ukranian</xsl:when>
  </xsl:choose>
</xsl:param>
</xsl:stylesheet>
