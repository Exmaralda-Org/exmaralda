<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-layout.xsl -->
<!-- Version 1.0 -->
<!-- Andreas Nolda 2019-05-06 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- We cannot include the master stylesheet because we want to change the $zh-number value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- change the user-visible parameter into a variable (actually, a constant) -->
<xsl:variable name="zh-number">0</xsl:variable>

<xsl:template match="basic-body">
  <xsl:variable name="base-id"
                select="tier[following-sibling::tier[@id=$reference-id]]
                            [@category='orig']/@id"/>
  <xsl:variable name="s-id"
                select="tier[preceding-sibling::tier[@category=$word or
                                                     @category='ZH'][1]
                                                    [@id=$reference-id]]
                            [@category='S']/@id"/>
  <xsl:choose>
    <xsl:when test="string-length($base-id)=0">
      <xsl:message terminate="yes">Error: There is no tier to compare with the reference tier.</xsl:message>
    </xsl:when>
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
                                         [@category='Diff']"/>
      </xsl:variable>
      <xsl:variable name="preceding-tier-number"
                    select="count($preceding-tiers/tier)"/>
      <xsl:variable name="old-current-tier-number"
                    select="count(tier[@category='Layout'])"/>
      <basic-body>
        <xsl:apply-templates select="common-timeline"/>
        <xsl:copy-of select="$preceding-tiers"/>
        <!-- current tier -->
        <xsl:call-template name="tier">
          <xsl:with-param name="preceding-tier-number"
                          select="$preceding-tier-number"/>
          <xsl:with-param name="category">Layout</xsl:with-param>
          <xsl:with-param name="type">a</xsl:with-param>
          <xsl:with-param name="events">
            <!-- process title, if any -->
            <xsl:for-each select="tier[@id=$s-id]/ud-tier-information[ud-information[@attribute-name='title-start'] and
                                                                      ud-information[@attribute-name='title-end']]">
              <xsl:call-template name="layout-events">
                <xsl:with-param name="start">
                  <xsl:value-of select="ud-information[@attribute-name='title-start']"/>
                </xsl:with-param>
                <xsl:with-param name="end">
                  <xsl:value-of select="ud-information[@attribute-name='title-end']"/>
                </xsl:with-param>
                <xsl:with-param name="reference-tier1"
                                select="../../tier[@id=$base-id]"/>
                <xsl:with-param name="reference-tier2"
                                select="../../tier[@id=$reference-id]"/>
              </xsl:call-template>
            </xsl:for-each>
            <!-- iterate on sentence-span events -->
            <xsl:for-each select="tier[@id=$s-id]/event">
              <xsl:call-template name="layout-events">
                <xsl:with-param name="start"
                                select="@start"/>
                <xsl:with-param name="end"
                                select="@end"/>
                <xsl:with-param name="reference-tier1"
                                select="../../tier[@id=$base-id]"/>
                <xsl:with-param name="reference-tier2"
                                select="../../tier[@id=$reference-id]"/>
              </xsl:call-template>
            </xsl:for-each>
          </xsl:with-param>
        </xsl:call-template>
        <!-- tiers following the current tier -->
        <xsl:apply-templates select="tier[preceding-sibling::tier[@id=$reference-id]]
                                         [not(@category='Diff' or
                                              @category='Layout')]">
          <xsl:with-param name="old-current-tier-number"
                          select="$old-current-tier-number"/>
          <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
        </xsl:apply-templates>
      </basic-body>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="layout-events">
  <xsl:param name="start"/>
  <xsl:param name="end"/>
  <xsl:param name="reference-tier1"/>
  <xsl:param name="reference-tier2"/>
  <xsl:variable name="subtimeline">
    <xsl:copy-of select="../../common-timeline/tli[@id=$start]"/>
    <xsl:copy-of select="../../common-timeline/tli[preceding-sibling::tli[@id=$start] and
                                                   following-sibling::tli[@id=$end]]"/>
    <xsl:copy-of select="../../common-timeline/tli[@id=$end]"/>
  </xsl:variable>
  <xsl:for-each select="$subtimeline/tli[not(position()=last())]">
    <!-- At the current timeline item there is the start of an event on reference tier 1,
         but not the start of an event on reference tier 2. -->
    <xsl:if test="@id=$reference-tier1/event/@start and
                    not(@id=$reference-tier2/event/@start)">
      <xsl:variable name="start1"
                    select="$reference-tier1/event[@start=current()/@id]/@start"/><!-- @id -->
      <xsl:variable name="end1"
                    select="$reference-tier1/event[@start=current()/@id]/@end"/>
      <xsl:choose>
        <!-- There is no event on reference tier 2 starting before
             and ending after the current timeline item. -->
        <xsl:when test="not($reference-tier2/event[@start=current()/preceding-sibling::tli/@id]
                                                  [@end=current()/following-sibling::tli/@id])">
          <xsl:choose>
            <!-- On reference tier 1, with category "orig", there is a "¶" event. -->
            <xsl:when test="$reference-tier1[@category='orig']/event[@start=$start1]
                                                                    [normalize-space(.)='&#xB6;']">
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start1"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end1"/>
                </xsl:attribute>
                <xsl:text>PARB</xsl:text>
              </event>
            </xsl:when>
            <!-- On reference tier 1, with category "orig", there is a "|" event. -->
            <xsl:when test="$reference-tier1[@category='orig']/event[@start=$start1]
                                                                    [normalize-space(.)='|']">
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start1"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end1"/>
                </xsl:attribute>
                <xsl:text>LB</xsl:text>
              </event>
            </xsl:when>
            <!-- On reference tier 1, with category "orig", there is a "_" event. -->
            <xsl:when test="$reference-tier1[@category='orig']/event[@start=$start1]
                                                                    [matches(normalize-space(.),'^_+$')]">
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start1"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end1"/>
                </xsl:attribute>
                <xsl:text>SPACE</xsl:text>
              </event>
            </xsl:when>
          </xsl:choose>
        </xsl:when>
        <!-- On reference tier 1, with category "orig", there is a "-" event
             (and there is an event on reference tier 2 starting before and
             ending after the current timeline item). -->
        <xsl:when test="$reference-tier1[@category='orig']/event[@start=$start1]
                                                                [normalize-space(.)='|']">
          <event>
            <xsl:attribute name="start">
              <xsl:value-of select="$start1"/>
            </xsl:attribute>
            <xsl:attribute name="end">
              <xsl:value-of select="$end1"/>
            </xsl:attribute>
              <xsl:text>LB</xsl:text>
          </event>
        </xsl:when>
        <!-- On reference tier 1, with category "orig", there is a "-" event
             (and there is an event on reference tier 2 starting before and
             ending after the current timeline item). -->
        <xsl:when test="$reference-tier1[@category='orig']/event[@start=$start1]
                                                                [normalize-space(.)='-']">
          <event>
            <xsl:attribute name="start">
              <xsl:value-of select="$start1"/>
            </xsl:attribute>
            <xsl:attribute name="end">
              <xsl:value-of select="$end1"/>
            </xsl:attribute>
              <xsl:text>HYPH</xsl:text>
          </event>
        </xsl:when>
      </xsl:choose>
    </xsl:if>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
