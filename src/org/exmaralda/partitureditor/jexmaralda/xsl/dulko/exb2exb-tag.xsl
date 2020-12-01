<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-tag.xsl -->
<!-- Version 11.0 -->
<!-- Andreas Nolda 2020-12-01 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:tt="java:org.exmaralda.dulko.treetagger.TreeTagger"
                exclude-result-prefixes="xs tt">

<xsl:include href="exb2exb-tiers.xsl"/>

<xsl:param name="lang">
  <!-- Currently, only German is supported. -->
  <xsl:choose>
    <xsl:when test="/basic-transcription/head/speakertable/speaker/languages-used/language[@lang='deu']">de</xsl:when>
    <xsl:when test="/basic-transcription/head/speakertable/speaker/languages-used[not(language)]">de</xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="yes">Error: The language(s) used in the speakertable are unsupported.</xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:param>

<xsl:template match="basic-body">
  <xsl:variable name="preceding-tiers">
    <!-- tiers preceding the reference tier -->
    <xsl:apply-templates select="tier[following-sibling::tier[@id=$reference-id]]"/>
    <!-- reference tier -->
    <xsl:apply-templates select="tier[@id=$reference-id]"/>
    <!-- other tiers preceding the current tiers -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][1]
                                                             [@id=$reference-id]]
                                     [ends-with(@category,'Diff') or
                                      @category='Layout' or
                                      @category='Graph' or
                                      ends-with(@category,'S')]"/>
  </xsl:variable>
  <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
  <xsl:variable name="old-current-tier-number"
                select="count(tier[preceding-sibling::tier[@category=$word or
                                                           @category='ZH'][1]
                                                          [@id=$reference-id]]
                                  [ends-with(@category,'pos') or
                                   ends-with(@category,'lemma')])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <xsl:copy-of select="$preceding-tiers"/>
    <!-- current tiers -->
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number"/>
      <xsl:with-param name="category">
        <xsl:if test="tier[@id=$reference-id]/@category='ZH'">
          <xsl:text>ZH</xsl:text>
        </xsl:if>
        <xsl:text>pos</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:call-template name="pos-events">
          <xsl:with-param name="reference-tier"
                          select="tier[@id=$reference-id]"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number + 1"/>
      <xsl:with-param name="category">
        <xsl:if test="tier[@id=$reference-id]/@category='ZH'">
          <xsl:text>ZH</xsl:text>
        </xsl:if>
        <xsl:text>lemma</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:call-template name="lemma-events">
          <xsl:with-param name="reference-tier"
                          select="tier[@id=$reference-id]"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the current tiers -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][1]
                                                             [@id=$reference-id]]
                                     [not(ends-with(@category,'Diff') or
                                          @category='Layout' or
                                          @category='Graph' or
                                          ends-with(@category,'S') or
                                          ends-with(@category,'pos') or
                                          ends-with(@category,'lemma'))]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">2</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][position()&gt;1]
                                                             [@id=$reference-id]]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">2</xsl:with-param>
    </xsl:apply-templates>
  </basic-body>
</xsl:template>

<xsl:template name="pos-events">
  <xsl:param name="reference-tier"/>
  <xsl:variable name="words" as="xs:string *">
    <xsl:sequence select="$reference-tier/event"/>
  </xsl:variable>
  <xsl:variable name="tags" select="tt:pos(tt:new($lang),$words)"/>
  <xsl:for-each select="$tags">
    <xsl:variable name="position"
                  select="position()"/>
    <event>
      <xsl:copy-of select="$reference-tier/event[position()=$position]/@start"/>
      <xsl:copy-of select="$reference-tier/event[position()=$position]/@end"/>
      <xsl:value-of select="."/>
    </event>
  </xsl:for-each>
</xsl:template>

<xsl:template name="lemma-events">
  <xsl:param name="reference-tier"/>
  <xsl:variable name="words" as="xs:string *">
    <xsl:sequence select="$reference-tier/event"/>
  </xsl:variable>
  <xsl:variable name="tags" select="tt:lemma(tt:new($lang),$words)"/>
  <xsl:for-each select="$tags">
    <xsl:variable name="position"
                  select="position()"/>
    <event>
      <xsl:copy-of select="$reference-tier/event[position()=$position]/@start"/>
      <xsl:copy-of select="$reference-tier/event[position()=$position]/@end"/>
      <xsl:value-of select="."/>
    </event>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
