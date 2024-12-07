<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-timeline.xsl -->
<!-- Version 7.0 -->
<!-- Andreas Nolda 2020-08-05 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb.xsl"/>

<xsl:template match="/">
  <xsl:choose>
    <xsl:when test="/basic-transcription/basic-body/tier[contains(@category,'::')]">
      <xsl:message terminate="yes">Error: There are tiers with namespace prefixes.</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="tli-id">
  <xsl:text>T</xsl:text>
  <xsl:value-of select="count(preceding-sibling::tli[@id=../../tier[@category='orig' or
                                                                    @category=$word or
                                                                    @category='ZH']/event/@start or
                                                     @id=../../tier[@category='orig' or
                                                                    @category=$word or
                                                                    @category='ZH']/event[last()]/@end])"/>
</xsl:template>

<xsl:template match="tli">
  <xsl:if test="@id=../../tier[@category='orig' or
                               @category=$word or
                               @category='ZH']/event/@start or
                @id=../../tier[@category='orig' or
                               @category=$word or
                               @category='ZH']/event[last()]/@end">
    <tli>
      <xsl:attribute name="id">
        <xsl:call-template name="tli-id"/>
      </xsl:attribute>
    </tli>
  </xsl:if>
</xsl:template>

<xsl:template match="event">
  <xsl:choose>
    <xsl:when test="@end=../../tier[@category='orig' or
                                    @category=$word or
                                    @category='ZH']/event/@start or
                    @end=../../tier[@category='orig' or
                                    @category=$word or
                                    @category='ZH']/event[last()]/@end">
      <event>
        <xsl:copy-of select="@*[not(name()='start' or
                                    name()='end')]"/>
        <xsl:attribute name="start">
          <xsl:for-each select="../../common-timeline/tli[@id=current()/@start]">
            <xsl:call-template name="tli-id"/>
          </xsl:for-each>
        </xsl:attribute>
        <xsl:attribute name="end">
          <xsl:for-each select="../../common-timeline/tli[@id=current()/@end]">
            <xsl:call-template name="tli-id"/>
          </xsl:for-each>
        </xsl:attribute>
        <xsl:apply-templates/>
      </event>
    </xsl:when>
    <xsl:otherwise>
      <event>
        <xsl:copy-of select="@*[not(name()='start' or
                                    name()='end')]"/>
        <xsl:attribute name="start">
          <xsl:for-each select="../../common-timeline/tli[@id=current()/@start]">
            <xsl:call-template name="tli-id"/>
          </xsl:for-each>
        </xsl:attribute>
        <xsl:attribute name="end">
          <xsl:for-each select="../../common-timeline/tli[@id=current()/@end]/following-sibling::tli[@id=../../tier[@category='orig' or
                                                                                                                    @category=$word or
                                                                                                                    @category='ZH']/event/@start
or
                                                                                                     @id=../../tier[@category='orig' or
                                                                                                                    @category=$word or
                                                                                                                    @category='ZH']/event[last()]/@end][1]">
            <xsl:call-template name="tli-id"/>
          </xsl:for-each>
        </xsl:attribute>
        <xsl:apply-templates/>
      </event>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="ud-tier-information/ud-information[@attribute-name='title-start' or
                                                        @attribute-name='title-end']">
  <ud-information>
    <xsl:copy-of select="@*"/>
    <xsl:for-each select="../../../common-timeline/tli[@id=current()]">
      <xsl:call-template name="tli-id"/>
    </xsl:for-each>
  </ud-information>
</xsl:template>
</xsl:stylesheet>
