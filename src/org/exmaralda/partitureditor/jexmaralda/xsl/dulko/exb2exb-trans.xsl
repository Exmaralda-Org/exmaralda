<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-trans.xsl -->
<!-- Version 8.2 -->
<!-- Andreas Nolda 2019-05-05 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- We cannot include the master stylesheet because we want to change the $zh-number value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- Changing the user-visible parameter into a variable (actually, a constant). -->
<xsl:variable name="zh-number">0</xsl:variable>

<xsl:template match="basic-body">
  <xsl:variable name="s-id"
                select="tier[@category='S']/@id"/>
  <xsl:choose>
    <xsl:when test="string-length($s-id)=0">
      <xsl:message terminate="yes">Error: There is no S tier for the reference tier.</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="preceding-tiers">
        <!-- tiers preceding the reference tier -->
        <xsl:apply-templates select="tier[following-sibling::tier[@id=$reference-id]]"/>
        <!-- reference tier -->
        <xsl:apply-templates select="tier[@id=$reference-id]"/>
        <!-- other tiers preceding the current tier -->
        <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                                  @category='ZH'][1]
                                                                 [@id=$reference-id]]
                                         [not(@category='trans' or
                                              @category='ZH')]"/>
      </xsl:variable>
      <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
      <xsl:variable name="old-current-tier-number"
                    select="count(tier[@category='trans'])"/>
      <basic-body>
        <xsl:apply-templates select="common-timeline"/>
        <xsl:copy-of select="$preceding-tiers"/>
        <!-- current tier -->
        <xsl:call-template name="tier">
          <xsl:with-param name="preceding-tier-number"
                          select="$preceding-tier-number"/>
          <xsl:with-param name="category">trans</xsl:with-param>
          <xsl:with-param name="type">a</xsl:with-param>
          <xsl:with-param name="events">
            <!-- process title, if any -->
            <xsl:for-each select="tier[@id=$s-id]/ud-tier-information[ud-information[@attribute-name='title-start'] and
                                                                      ud-information[@attribute-name='title-end']]">
              <xsl:call-template name="trans-events">
                <xsl:with-param name="start">
                  <xsl:value-of select="ud-information[@attribute-name='title-start']"/>
                </xsl:with-param>
                <xsl:with-param name="end">
                  <xsl:value-of select="ud-information[@attribute-name='title-end']"/>
                </xsl:with-param>
              </xsl:call-template>
            </xsl:for-each>
            <!-- iterate on sentence-span events -->
            <xsl:for-each select="tier[@id=$s-id]/event">
              <xsl:call-template name="trans-events">
                <xsl:with-param name="start"
                                select="@start"/>
                <xsl:with-param name="end"
                                select="@end"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:with-param>
        </xsl:call-template>
        <!-- tiers following the current tier -->
        <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                                  @category='ZH'][1]
                                                                 [@id=$reference-id]]
                                         [@category='ZH']">
          <xsl:with-param name="old-current-tier-number"
                          select="$old-current-tier-number"/>
          <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
        </xsl:apply-templates>
        <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                                  @category='ZH'][position()&gt;1]
                                                                 [@id=$reference-id]]">
          <xsl:with-param name="old-current-tier-number"
                          select="$old-current-tier-number"/>
          <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
        </xsl:apply-templates>
      </basic-body>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="trans-events">
  <xsl:param name="start"/>
  <xsl:param name="end"/>
  <xsl:variable name="existing-event"
                select="../../tier[@category='trans']/event[@start=$start and
                                                            @end=$end]"/>
  <xsl:choose>
    <xsl:when test="$existing-event">
      <xsl:copy-of select="$existing-event"/>
    </xsl:when>
    <xsl:otherwise>
      <event>
        <xsl:attribute name="start">
          <xsl:value-of select="$start"/>
        </xsl:attribute>
        <xsl:attribute name="end">
          <xsl:value-of select="$end"/>
        </xsl:attribute>
      </event>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
</xsl:stylesheet>
