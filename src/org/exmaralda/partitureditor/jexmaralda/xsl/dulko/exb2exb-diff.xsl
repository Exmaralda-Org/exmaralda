<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-diff.xsl -->
<!-- Version 7.4 -->
<!-- Andreas Nolda 2025-08-26 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb-tiers.xsl"/>

<xsl:template match="basic-body">
  <xsl:variable name="base-id"
                select="tier[following-sibling::tier[@id=$reference-id]]
                            [@category='orig' or
                             @category=$word or
                             @category='ZH'][last()]/@id"/>
  <xsl:variable name="s-id"
                select="tier[preceding-sibling::tier[@category=$word or
                                                     @category='ZH'][1]
                                                    [@id=$reference-id]]
                            [ends-with(@category,'S')]/@id"/>
  <xsl:choose>
    <xsl:when test="string-length($base-id)=0">
      <xsl:message terminate="yes">Error: There is no tier to compare with the reference tier.</xsl:message>
    </xsl:when>
    <xsl:when test="string-length($s-id)=0">
      <xsl:message terminate="yes">Error: There is no (ZH)S tier for the reference tier.</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <xsl:variable name="preceding-tiers">
        <!-- tiers preceding the reference tier -->
        <xsl:apply-templates select="tier[following-sibling::tier[@id=$reference-id]]"/>
        <!-- reference tier -->
        <xsl:apply-templates select="tier[@id=$reference-id]"/>
      </xsl:variable>
      <xsl:variable name="preceding-tier-number"
                    select="count($preceding-tiers/tier)"/>
      <xsl:variable name="old-current-tier-number"
                    select="count(tier[preceding-sibling::tier[@category=$word or
                                                               @category='ZH'][1]
                                                              [@id=$reference-id]]
                                      [ends-with(@category,'Diff')])"/>
      <basic-body>
        <xsl:apply-templates select="common-timeline"/>
        <xsl:copy-of select="$preceding-tiers"/>
        <!-- current tier -->
        <xsl:call-template name="tier">
          <xsl:with-param name="preceding-tier-number"
                          select="$preceding-tier-number"/>
          <xsl:with-param name="category">
            <xsl:if test="tier[@id=$reference-id]/@category='ZH'">
              <xsl:text>ZH</xsl:text>
            </xsl:if>
            <xsl:text>Diff</xsl:text>
          </xsl:with-param>
          <xsl:with-param name="type">a</xsl:with-param>
          <xsl:with-param name="events">
            <!-- process title, if any -->
            <xsl:for-each select="tier[@id=$s-id]/ud-tier-information[ud-information[@attribute-name='title-start'] and
                                                                      ud-information[@attribute-name='title-end']]">
              <xsl:call-template name="diff-events">
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
              <xsl:call-template name="diff-events">
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
        <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                                  @category='ZH'][1]
                                                                 [@id=$reference-id]]
                                         [not(ends-with(@category,'Diff'))]">
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

<xsl:template name="diff-events">
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
    <xsl:choose>
      <!-- At the current timeline item there is the start of an event on reference tier 1
           and the start of an event on reference tier 2. -->
      <xsl:when test="@id=$reference-tier1/event/@start and
                      @id=$reference-tier2/event/@start">
        <xsl:variable name="start1"
                      select="$reference-tier1/event[@start=current()/@id]/@start"/><!-- @id -->
        <xsl:variable name="start2"
                      select="$reference-tier2/event[@start=current()/@id]/@start"/><!-- @id -->
        <xsl:variable name="end1"
                      select="$reference-tier1/event[@start=current()/@id]/@end"/>
        <xsl:variable name="end2"
                      select="$reference-tier2/event[@start=current()/@id]/@end"/>
        <xsl:choose>
          <!-- Both events also have the same end, but different content. -->
          <xsl:when test="$end1=$end2 and
                          not(normalize-space($reference-tier1/event[@start=$start1])=
                              normalize-space($reference-tier2/event[@start=$start2]))">
            <event>
              <xsl:attribute name="start">
                <xsl:value-of select="$start1"/>
              </xsl:attribute>
              <xsl:attribute name="end">
                <xsl:value-of select="$end1"/>
              </xsl:attribute>
              <xsl:text>CHA</xsl:text>
            </event>
          </xsl:when>
          <!-- The event on reference tier 1 corresponds to several events on reference tier 2. -->
          <xsl:when test="$reference-tier2/event[@end=$end1] and
                          not($reference-tier2/event[@start=$start1] is
                              $reference-tier2/event[@end=$end1])">
            <event>
              <xsl:attribute name="start">
                <xsl:value-of select="$start1"/>
              </xsl:attribute>
              <xsl:attribute name="end">
                <xsl:value-of select="$end1"/>
              </xsl:attribute>
              <xsl:text>SPLIT</xsl:text>
            </event>
          </xsl:when>
          <!-- The event on reference tier 2 corresponds to several events on reference tier 1. -->
          <xsl:when test="$reference-tier1/event[@end=$end2] and
                          not($reference-tier1/event[@start=$start2] is
                              $reference-tier1/event[@end=$end2])">
            <!-- On reference tier 1, with category "orig", there is no "-" event
                 being contained in the event on reference tier 2. -->
            <xsl:if test="not($reference-tier1[@category='orig']/event[@start=$subtimeline/tli[preceding-sibling::tli/@id=$start2]/@id]
                                                                      [@end=$subtimeline/tli[following-sibling::tli/@id=$end2]/@id]
                                                                      [normalize-space(.)='-'])">
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start2"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end2"/>
                </xsl:attribute>
                  <xsl:text>MERGE</xsl:text>
              </event>
            </xsl:if>
          </xsl:when>
        </xsl:choose>
      </xsl:when>
      <!-- At the current timeline item there is the start of an event on reference tier 1
           (but not the start of an event on reference tier 2). -->
      <xsl:when test="@id=$reference-tier1/event/@start">
        <xsl:variable name="start1"
                      select="$reference-tier1/event[@start=current()/@id]/@start"/><!-- @id -->
        <xsl:variable name="end1"
                      select="$reference-tier1/event[@start=current()/@id]/@end"/>
        <!-- There is no event on reference tier 2 starting before
             and ending after the current timeline item. -->
        <xsl:if test="not($reference-tier2/event[@start=current()/preceding-sibling::tli/@id]
                                                  [@end=current()/following-sibling::tli/@id])">
          <!-- On reference tier 1, with category "orig", there is no "_", "|", or "¶" event. -->
          <xsl:if test="not($reference-tier1[@category='orig']/event[@start=$start1]
                                                                    [matches(normalize-space(.),'^_+$') or
                                                                     normalize-space(.)='|' or
                                                                     normalize-space(.)='&#xB6;'])">
            <xsl:variable name="ins"
                          select="count($reference-tier1/event[@start=$subtimeline/tli/@id and
                                                               @end=$subtimeline/tli/@id]
                                                              [not(@start=$reference-tier2/event/@start or
                                                                   @end=$reference-tier2/event/@end)]
                                                              [normalize-space(.)=
                                                               normalize-space($reference-tier1/event[@start=$start1]
                                                                                                     [@end=$end1])])"/>
            <xsl:variable name="del"
                          select="count($reference-tier2/event[@start=$subtimeline/tli/@id and
                                                               @end=$subtimeline/tli/@id]
                                                              [not(@start=$reference-tier1/event/@start or
                                                                   @end=$reference-tier1/event/@end)]
                                                              [normalize-space(.)=
                                                               normalize-space($reference-tier1/event[@start=$start1]
                                                                                                     [@end=$end1])])"/>
            <xsl:choose>
              <!-- There is an event on reference tier 2 at a different position
                   on the same sub-timeline with the same content. -->
              <xsl:when test="$del&gt;0">
                <event>
                  <xsl:attribute name="start">
                    <xsl:value-of select="$start1"/>
                  </xsl:attribute>
                  <xsl:attribute name="end">
                    <xsl:value-of select="$end1"/>
                  </xsl:attribute>
                  <xsl:text>MOVS</xsl:text>
                  <xsl:if test="$ins&gt;$del">
                    <xsl:text>/DEL</xsl:text>
                  </xsl:if>
                </event>
              </xsl:when>
              <!-- There is no event on reference tier 2 at a different position
                   on the same sub-timeline with the same content. -->
              <xsl:otherwise>
                <event>
                  <xsl:attribute name="start">
                    <xsl:value-of select="$start1"/>
                  </xsl:attribute>
                  <xsl:attribute name="end">
                    <xsl:value-of select="$end1"/>
                  </xsl:attribute>
                  <xsl:text>DEL</xsl:text>
                </event>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:if>
        </xsl:if>
      </xsl:when>
      <!-- At the current timeline item there is the start of an event on reference tier 2
           (but not the start of an event on reference tier 1). -->
      <xsl:when test="@id=$reference-tier2/event/@start">
        <xsl:variable name="start2"
                      select="$reference-tier2/event[@start=current()/@id]/@start"/><!-- @id -->
        <xsl:variable name="end2"
                      select="$reference-tier2/event[@start=current()/@id]/@end"/>
        <xsl:variable name="ins"
                      select="count($reference-tier1/event[@start=$subtimeline/tli/@id and
                                                           @end=$subtimeline/tli/@id]
                                                          [not(@start=$reference-tier2/event/@start or
                                                               @end=$reference-tier2/event/@end)]
                                                          [normalize-space(.)=
                                                           normalize-space($reference-tier2/event[@start=$start2]
                                                                                                 [@end=$end2])])"/>
        <xsl:variable name="del"
                      select="count($reference-tier2/event[@start=$subtimeline/tli/@id and
                                                           @end=$subtimeline/tli/@id]
                                                          [not(@start=$reference-tier1/event/@start or
                                                               @end=$reference-tier1/event/@end)]
                                                          [normalize-space(.)=
                                                           normalize-space($reference-tier2/event[@start=$start2]
                                                                                                 [@end=$end2])])"/>
        <!-- There is no event on reference tier 1 starting before
             and ending after the current timeline item. -->
        <xsl:if test="not($reference-tier1/event[@start=current()/preceding-sibling::tli/@id]
                                                [@end=current()/following-sibling::tli/@id])">
          <xsl:choose>
            <!-- There is an event on reference tier 1 at a different position
                 on the same sub-timeline with the same content. -->
            <xsl:when test="$ins&gt;0">
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start2"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end2"/>
                </xsl:attribute>
                <xsl:text>MOVT</xsl:text>
                <xsl:if test="$del&gt;$ins">
                  <xsl:text>/INS</xsl:text>
                </xsl:if>
              </event>
            </xsl:when>
            <!-- There is no event on reference tier 1 at a different position
                 on the same sub-timeline with the same content. -->
            <xsl:otherwise>
              <event>
                <xsl:attribute name="start">
                  <xsl:value-of select="$start2"/>
                </xsl:attribute>
                <xsl:attribute name="end">
                  <xsl:value-of select="$end2"/>
                </xsl:attribute>
                <xsl:text>INS</xsl:text>
              </event>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:if>
      </xsl:when>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
