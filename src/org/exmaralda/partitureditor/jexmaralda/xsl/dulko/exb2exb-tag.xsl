<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-tag.xsl -->
<!-- Version 12.0 -->
<!-- Andreas Nolda 2020-12-07 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:tt="java:org.exmaralda.dulko.treetagger.TreeTagger"
                exclude-result-prefixes="xs tt">

<xsl:include href="exb2exb-tiers.xsl"/>

<xsl:include href="lang.xsl"/>

<xsl:variable name="tagger"
              select="tt:new()"/>

<xsl:param name="tagger-model"
           select="tt:getModel($tagger)"/>

<xsl:param name="tagger-model-uri">
  <xsl:if test="string-length($tagger-lang)&gt;0">
    <xsl:text>file://</xsl:text>
    <xsl:choose>
      <!-- Microsoft Windows: -->
      <xsl:when test="matches($tagger-model,'^[A-Z]:\\')">
        <xsl:text>/</xsl:text>
        <xsl:value-of select="translate($tagger-model,'\','/')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$tagger-model"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:if>
</xsl:param>

<xsl:template match="basic-body">
  <xsl:choose>
    <xsl:when test="string-length($tagger-lang)=0">
      <xsl:message terminate="yes">Error: The first language used in the speakertable is unsupported by the tagger.</xsl:message>
    </xsl:when>
    <xsl:when test="not(matches($tagger-model-uri,concat('/[^/.]*',$tagger-lang)))">
      <xsl:message terminate="yes">Error: The first language used in the speakertable does not match the parameter file used by TreeTagger.</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message>
        <xsl:text>Using TreeTagger parameter file </xsl:text>
        <xsl:value-of select="$tagger-model-uri"/>
      </xsl:message>
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
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="pos-events">
  <xsl:param name="reference-tier"/>
  <xsl:variable name="words" as="xs:string*">
    <xsl:sequence select="$reference-tier/event"/>
  </xsl:variable>
  <xsl:variable name="tags" select="tt:pos($tagger,$words)"/>
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
  <xsl:variable name="words" as="xs:string*">
    <xsl:sequence select="$reference-tier/event"/>
  </xsl:variable>
  <xsl:variable name="tags" select="tt:lemma($tagger,$words)"/>
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
