<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-fehler.xsl -->
<!-- Version 2.2 -->
<!-- Andreas Nolda 2019-05-05 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb-tiers.xsl"/>

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
                                      ends-with(@category,'S') or
                                      ends-with(@category,'pos') or
                                      ends-with(@category,'lemma')]"/>
  </xsl:variable>
  <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
  <xsl:variable name="old-current-tier-number"
                select="count(tier[preceding-sibling::tier[@category=$word or
                                                           @category='ZH'][1]
                                                          [@id=$reference-id]]
                                  [starts-with(@category,'Fehler')])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <xsl:copy-of select="$preceding-tiers"/>
    <!-- current tiers -->
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number"/>
      <xsl:with-param name="category">FehlerOrth</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='FehlerOrth']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number + 1"/>
      <xsl:with-param name="category">FehlerMorph</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='FehlerMorph']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number + 2"/>
      <xsl:with-param name="category">FehlerSyn</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='FehlerSyn']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number + 3"/>
      <xsl:with-param name="category">FehlerLex</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='FehlerLex']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number + 4"/>
      <xsl:with-param name="category">FehlerSem</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='FehlerSem']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the current tiers -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][1]
                                                             [@id=$reference-id]]
                                     [@category='ZH']">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">5</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][position()&gt;1]
                                                             [@id=$reference-id]]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">5</xsl:with-param>
    </xsl:apply-templates>
  </basic-body>
</xsl:template>
</xsl:stylesheet>
