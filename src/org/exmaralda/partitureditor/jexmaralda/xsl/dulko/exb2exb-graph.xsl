<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-graph.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2019-05-06 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- We cannot include the master stylesheet because we want to change the $zh-number value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- change the user-visible parameter into a variable (actually, a constant) -->
<xsl:variable name="zh-number">0</xsl:variable>

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
                                     [@category='Diff' or
                                      @category='Layout']"/>
  </xsl:variable>
  <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
  <xsl:variable name="old-current-tier-number"
                select="count(tier[@category='Graph'])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <xsl:copy-of select="$preceding-tiers"/>
    <!-- current tiers -->
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number"/>
      <xsl:with-param name="category">Graph</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:copy-of select="tier[preceding-sibling::tier[@category=$word or
                                                          @category='ZH'][1]
                                                         [@id=$reference-id]]
                                 [@category='Graph']/event"/>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the current tiers -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@id=$reference-id]]
                                     [not(@category='Diff' or
                                          @category='Layout' or
                                          @category='Graph')]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
    </xsl:apply-templates>
  </basic-body>
</xsl:template>
</xsl:stylesheet>
