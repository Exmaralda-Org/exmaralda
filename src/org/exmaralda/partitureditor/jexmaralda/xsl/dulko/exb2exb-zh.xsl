<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-zh.xsl -->
<!-- Version 9.2 -->
<!-- Andreas Nolda 2023-09-27 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- We cannot include the master stylesheet because we want to change the $zh-decrement value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- decrement $zh-number by 1 -->
<xsl:variable name="zh-decrement">1</xsl:variable>

<xsl:template match="basic-body">
  <xsl:variable name="preceding-tiers">
    <!-- tiers preceding the reference tier -->
    <xsl:apply-templates select="tier[following-sibling::tier[@id=$reference-id]]"/>
    <!-- reference tier -->
    <xsl:apply-templates select="tier[@id=$reference-id]"/>
    <!-- other tiers preceding the current tier -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][1]
                                                             [@id=$reference-id]]
                                     [not(@category='ZH')]"/>
  </xsl:variable>
  <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
  <xsl:variable name="old-current-tier-number"
                select="count(tier[preceding-sibling::tier[@category=$word or
                                                           @category='ZH'][1]
                                                          [@id=$reference-id]]
                                  [@category='ZH'])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <xsl:copy-of select="$preceding-tiers"/>
    <!-- current tier -->
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number"/>
      <xsl:with-param name="category">ZH</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:call-template name="zh-events"/>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the current tier -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                              @category='ZH'][position()&gt;1]
                                                             [@id=$reference-id]]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
    </xsl:apply-templates>
  </basic-body>
</xsl:template>

<xsl:template name="zh-events">
  <xsl:for-each select="common-timeline/tli">
    <xsl:choose>
      <!-- preserve events on existing title or sentence spans -->
      <xsl:when test="../following-sibling::tier[@category='ZHS']
                                                [preceding-sibling::tier[@category=$word or
                                                                         @category='ZH'][2]
                                                                        [@id=$reference-id]]
                                                [ud-tier-information[ud-information[@attribute-name='title-start']=current()/@id or
                                                                     ud-information[@attribute-name='title-start']=current()/preceding-sibling::tli/@id]
                                                                    [ud-information[@attribute-name='title-end']=current()/following-sibling::tli/@id] or
                                                 event[@start=current()/@id or
                                                       @start=current()/preceding-sibling::tli/@id]
                                                      [@end=current()/following-sibling::tli/@id]]">
        <xsl:copy-of select="../following-sibling::tier[@category='ZH']
                                                       [preceding-sibling::tier[@category=$word or
                                                                                @category='ZH'][1]
                                                                               [@id=$reference-id]]/event[@start=current()/@id]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="../following-sibling::tier[@id=$reference-id]/event[@start=current()/@id]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
