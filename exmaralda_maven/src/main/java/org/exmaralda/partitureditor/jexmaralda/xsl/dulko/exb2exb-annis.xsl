<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-annis.xsl -->
<!-- Version 12.0 -->
<!-- Andreas Nolda 2023-07-18 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:import href="exb2exb-tiers.xsl"/>

<!-- remove <project-name> in order to let Pepper construct ANNIS document names
     from source file names instead of the project name -->
<xsl:template match="project-name"/>

<xsl:variable name="document-metadata-in-meta-information">
  <xsl:call-template name="get-document-metadata-in-meta-information"/>
</xsl:variable>

<xsl:template match="ud-meta-information">
  <ud-meta-information>
    <!-- ignore corpus metadata -->
    <xsl:for-each select="$document-metadata-in-meta-information/meta">
      <ud-information>
        <xsl:attribute name="attribute-name"
                       select="@variable"/>
        <xsl:value-of select="."/>
      </ud-information>
    </xsl:for-each>
  </ud-meta-information>
</xsl:template>

<!-- shorten speaker ID and add "SPK" prefix in order to make ANNIS metadata tables
     more readable -->
<xsl:template match="speakertable/speaker/abbreviation">
  <abbreviation>
    <xsl:text>SPK_</xsl:text>
    <xsl:choose>
      <xsl:when test="string-length()&gt;4">
        <xsl:value-of select="substring(.,1,4)"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </abbreviation>
</xsl:template>

<xsl:variable name="document-metadata-in-speakertable">
  <xsl:call-template name="get-document-metadata-in-speakertable"/>
</xsl:variable>

<xsl:template match="ud-speaker-information">
  <ud-speaker-information>
    <!-- ignore corpus metadata -->
    <xsl:for-each select="$document-metadata-in-speakertable/meta">
      <ud-information>
        <xsl:attribute name="attribute-name"
                       select="@variable"/>
        <xsl:value-of select="."/>
      </ud-information>
    </xsl:for-each>
  </ud-speaker-information>
</xsl:template>

<xsl:template match="basic-body">
  <xsl:variable name="old-current-tier-number"
                select="count(tier[@category='orig'])"/>
  <basic-body>
    <xsl:apply-templates select="common-timeline"/>
    <!-- insert an empty tok tier of type "t[ranscription]" -->
    <xsl:call-template name="tier">
      <xsl:with-param name="category">tok</xsl:with-param>
      <xsl:with-param name="type">t</xsl:with-param>
    </xsl:call-template>
    <xsl:apply-templates select="tier"/>
  </basic-body>
</xsl:template>

<xsl:template match="tier">
  <xsl:variable name="category">
    <xsl:choose>
      <xsl:when test="@category='ZH'">
        <xsl:text>ZH</xsl:text>
        <xsl:value-of select="count(preceding-sibling::tier[@category='ZH']) + 1"/>
        <xsl:text>::</xsl:text>
      </xsl:when>
      <xsl:when test="preceding-sibling::tier[@category='ZH']">
        <xsl:text>ZH</xsl:text>
        <xsl:value-of select="count(preceding-sibling::tier[@category='ZH'])"/>
        <xsl:text>::</xsl:text>
      </xsl:when>
      <xsl:when test="following-sibling::tier[@category=$word]">
        <xsl:text>txt</xsl:text>
        <xsl:text>::</xsl:text>
      </xsl:when>
      <xsl:when test="@category=$word">
        <xsl:text>txt</xsl:text>
        <xsl:text>::</xsl:text>
      </xsl:when>
      <xsl:when test="preceding-sibling::tier[@category=$word]">
        <xsl:text>txt</xsl:text>
        <xsl:text>::</xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:choose>
      <!-- for backward compatibility -->
      <xsl:when test="@category='tok'">word</xsl:when>
      <xsl:otherwise>
    <xsl:value-of select="@category"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:copy>
    <xsl:apply-templates select="@category |
                                 @display-name">
      <xsl:with-param name="category"
                      select="$category"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="@*[not(name()='category' or
                                        name()='display-name')]"/>
    <xsl:choose>
      <xsl:when test="@category='ZH'">
        <xsl:variable name="s-id"
                      select="following-sibling::tier[@category='ZHS'][1]/@id"/>
        <xsl:if test="string-length($s-id)=0">
          <xsl:message terminate="yes">Error: There is no ZHS tier for some ZH tier.</xsl:message>
        </xsl:if>
        <!-- process title, if any -->
        <xsl:for-each select="../tier[@id=$s-id]/ud-tier-information[ud-information[@attribute-name='title-start'] and
                                                                     ud-information[@attribute-name='title-end']]">
          <xsl:call-template name="zh-annis-events">
            <xsl:with-param name="start">
              <xsl:value-of select="ud-information[@attribute-name='title-start']"/>
            </xsl:with-param>
            <xsl:with-param name="end">
              <xsl:value-of select="ud-information[@attribute-name='title-end']"/>
            </xsl:with-param>
          </xsl:call-template>
        </xsl:for-each>
        <!-- iterate on sentence-span events -->
        <xsl:for-each select="../tier[@id=$s-id]/event">
          <xsl:call-template name="zh-annis-events">
            <xsl:with-param name="start"
                            select="@start"/>
            <xsl:with-param name="end"
                            select="@end"/>
          </xsl:call-template>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates select="event"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:copy>
</xsl:template>

<xsl:template match="tier/@category">
  <xsl:param name="category"/>
  <xsl:attribute name="category">
    <xsl:value-of select="$category"/>
  </xsl:attribute>
</xsl:template>

<xsl:template match="tier/@display-name">
  <xsl:param name="category"/>
  <xsl:attribute name="display-name">
    <xsl:choose>
      <xsl:when test="/basic-transcription/head/speakertable/speaker[1]/abbreviation[string-length()&gt;4]">
        <xsl:value-of select="substring(/basic-transcription/head/speakertable/speaker[1]/abbreviation,1,4)"/>
        <xsl:text>... </xsl:text>
      </xsl:when>
      <xsl:when test="/basic-transcription/head/speakertable/speaker[1]/abbreviation[string-length()&gt;0]">
        <xsl:value-of select="/basic-transcription/head/speakertable/speaker[1]/abbreviation"/>
        <xsl:text> </xsl:text>
      </xsl:when>
    </xsl:choose>
    <xsl:text>[</xsl:text>
    <xsl:value-of select="$category"/>
    <xsl:text>]</xsl:text>
  </xsl:attribute>
</xsl:template>

<!-- increment the tier ID number -->
<xsl:template match="tier/@id">
  <xsl:attribute name="id">
    <xsl:text>TIE</xsl:text>
    <xsl:value-of select="count(../preceding-sibling::tier) + 1"/>
  </xsl:attribute>
</xsl:template>

<!-- change the category of the word tier to "a[nnotation]" -->
<xsl:template match="tier[@category=$word]/@type">
  <xsl:attribute name="type">a</xsl:attribute>
</xsl:template>

<!-- for backward compatibility -->
<xsl:template match="tier[@category='ZH']/@type">
  <xsl:attribute name="type">a</xsl:attribute>
</xsl:template>

<xsl:template name="zh-annis-events">
  <xsl:param name="start"/>
  <xsl:param name="end"/>
  <xsl:variable name="subtimeline">
    <xsl:copy-of select="../../common-timeline/tli[@id=$start]"/>
    <xsl:copy-of select="../../common-timeline/tli[preceding-sibling::tli[@id=$start] and
                                                                             following-sibling::tli[@id=$end]]"/>
    <xsl:copy-of select="../../common-timeline/tli[@id=$end]"/>
  </xsl:variable>
  <xsl:for-each select="../preceding-sibling::tier[@category='ZH'][1]">
    <!-- ignore duplicate sentence-spans on ZH -->
    <xsl:if test="not(deep-equal(event[@start=$subtimeline/tli[not(position()=last())]/@id],
                                 preceding-sibling::tier[@category='ZH'][1]/event[@start=$subtimeline/tli[not(position()=last())]/@id]))">
      <xsl:copy-of select="event[@start=$subtimeline/tli[not(position()=last())]/@id]"/>
    </xsl:if>
  </xsl:for-each>
</xsl:template>

<xsl:template match="tier[@category='ZHS' or
                          @category='ZHpos' or
                          @category='ZHlemma']/event">
  <!-- ignore duplicate tags on ZHS, ZHpos, and ZHlemma -->
  <xsl:if test="not(deep-equal(.,
                               ../preceding-sibling::tier[@category=current()/../@category][1]/event[@start=current()/@start]))">
    <xsl:copy-of select="."/>
  </xsl:if>
</xsl:template>

<xsl:template match="tierformat-table"/>
</xsl:stylesheet>
