<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-org.xsl -->
<!-- Version 1.1 -->
<!-- Andreas Nolda 2019-06-23 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!-- We cannot include the master stylesheet because we want to change the $zh-number value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- Changing the user-visible parameter into a variable. -->
<xsl:variable name="zh-number">0</xsl:variable>

<xsl:template match="basic-body">
  <xsl:variable name="old-current-tier-number"
                select="count(tier[@category='orig'])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <!-- current tier -->
    <xsl:call-template name="tier">
      <xsl:with-param name="category">orig</xsl:with-param>
      <xsl:with-param name="type">a</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:call-template name="orig-events"/>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the current tier -->
    <xsl:apply-templates select="tier[@id=$reference-id]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
    </xsl:apply-templates>
    <xsl:apply-templates select="tier[preceding-sibling::tier[@id=$reference-id]]">
      <xsl:with-param name="old-current-tier-number"
                      select="$old-current-tier-number"/>
      <xsl:with-param name="new-current-tier-number">1</xsl:with-param>
    </xsl:apply-templates>
  </basic-body>
</xsl:template>

<xsl:template name="orig-events">
  <xsl:for-each select="common-timeline/tli">
    <xsl:choose>
      <!-- preserve events on existing title or sentence spans
           if there already is an "orig" tier -->
      <xsl:when test="../following-sibling::tier[@category='orig'] and
                      ../following-sibling::tier[@category='S']
                                                [ud-tier-information[ud-information[@attribute-name='title-start']=current()/@id or
                                                                     ud-information[@attribute-name='title-start']=current()/preceding-sibling::tli/@id]
                                                                    [ud-information[@attribute-name='title-end']=current()/following-sibling::tli/@id] or
                                                 event[@start=current()/@id or
                                                       @start=current()/preceding-sibling::tli/@id]
                                                      [@end=current()/following-sibling::tli/@id]]">
        <xsl:copy-of select="../following-sibling::tier[@category='orig']/event[@start=current()/@id]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="../following-sibling::tier[@id=$reference-id]/event[@start=current()/@id]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
