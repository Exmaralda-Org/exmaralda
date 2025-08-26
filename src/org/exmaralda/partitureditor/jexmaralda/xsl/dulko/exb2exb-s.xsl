<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-s.xsl -->
<!-- Version 13.1 -->
<!-- Andreas Nolda 2025-08-26 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="exb2exb-tiers.xsl"/>

<xsl:template match="basic-body">
  <xsl:variable name="pos-id"
                select="tier[preceding-sibling::tier[@category=$word or
                                                     @category='ZH'][1]
                                                    [@id=$reference-id]]
                            [ends-with(@category,'pos')]/@id"/>
  <xsl:choose>
    <xsl:when test="string-length($pos-id)=0">
      <xsl:message terminate="yes">Error: There is no (ZH)pos tier for the reference tier.</xsl:message>
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
                                         [ends-with(@category,'Diff') or
                                          @category='Layout' or
                                          @category='Graph']"/>
      </xsl:variable>
      <xsl:variable name="preceding-tier-number"
                    select="count($preceding-tiers/tier)"/>
      <xsl:variable name="old-current-tier-number"
                    select="count(tier[preceding-sibling::tier[@category=$word or
                                                               @category='ZH'][1]
                                                              [@id=$reference-id]]
                                      [ends-with(@category,'S')])"/>
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
            <xsl:text>S</xsl:text>
          </xsl:with-param>
          <xsl:with-param name="type">a</xsl:with-param>
          <xsl:with-param name="events">
            <xsl:call-template name="s-events">
              <xsl:with-param name="pos-id"
                              select="$pos-id"/>
            </xsl:call-template>
          </xsl:with-param>
        </xsl:call-template>
        <!-- tiers following the current tier -->
        <xsl:apply-templates select="tier[preceding-sibling::tier[@category=$word or
                                                                  @category='ZH'][1]
                                                                 [@id=$reference-id]]
                                         [not(ends-with(@category,'Diff') or
                                              @category='Layout' or
                                              @category='Graph' or
                                              ends-with(@category,'S'))]">
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

<xsl:template name="get-end-tli">
  <xsl:choose>
    <!-- closing parenthesis -->
    <xsl:when test="following-sibling::event[1][.=')']">
      <xsl:for-each select="following-sibling::event[1]">
        <xsl:call-template name="get-end-tli"/>
      </xsl:for-each>
    </xsl:when>
    <!-- closing quotation mark -->
    <xsl:when test="following-sibling::event[1][.='&#x22;'] and
                    count(preceding-sibling::event[.='&#x22;']) mod 2 = 1">
      <xsl:for-each select="following-sibling::event[1]">
        <xsl:call-template name="get-end-tli"/>
      </xsl:for-each>
    </xsl:when>
    <!-- "¶" or "|" event on the "orig" tier -->
    <xsl:when test="not(following-sibling::event[1][@start=current()/@end]) and
                    ../../tier[@category='orig']/event[normalize-space(.)='&#xB6;' or
                                                       normalize-space(.)='|'][@start=current()/@end]">
      <xsl:for-each select="../../tier[@category='orig']/event[normalize-space(.)='&#xB6;' or
                                                               normalize-space(.)='|'][@start=current()/@end]">
        <xsl:call-template name="get-end-tli"/>
      </xsl:for-each>
    </xsl:when>
    <!-- no closing parenthesis or quotation mark -->
    <xsl:otherwise>
      <xsl:copy-of select="../../common-timeline/tli[@id=current()/@end]"/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:param name="sentence-final-tags">
  <xsl:choose>
    <xsl:when test="$lang='bul'">
      <tag>PT_SENT</tag>
    </xsl:when>
    <xsl:when test="$lang='csc'">
      <tag>PUNCT.Final</tag>
    </xsl:when>
    <xsl:when test="$lang='deu'">
      <tag>$.</tag>
    </xsl:when>
    <xsl:when test="$lang='est'">
      <tag>Z.Sent</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='glg'">
      <tag>Q.</tag>
      <tag>Q!</tag>
      <tag>Q?</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='hun'">
      <tag>PUNCT.period</tag>
      <tag>PUNCT.!</tag>
      <tag>PUNCT.?</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='nld'">
      <tag>$.</tag>
    </xsl:when>
    <xsl:when test="$lang='nor'">
      <tag>PUNCT.Sent</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='pol'">
      <tag>interp:sent</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='por'">
      <tag>Fp</tag>
      <tag>Fat</tag>
      <tag>Fit</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='spa'">
      <tag>FS</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:when test="$lang='swe'">
      <tag>MAD</tag>
      <!-- ... -->
    </xsl:when>
    <xsl:otherwise>
      <tag>SENT</tag>
    </xsl:otherwise>
  </xsl:choose>
</xsl:param>

<xsl:variable name="sentence-final-abbreviations">
  <xsl:choose>
    <xsl:when test="$lang='deu'">
      <abbr><!-- u. -->a.</abbr>
      <abbr><!-- u. -->ä.</abbr>
      <abbr>etc.</abbr>
      <abbr>usw.</abbr>
      <!-- ... -->
    </xsl:when>
    <!-- ... -->
    <xsl:otherwise>
      <abbr>etc.</abbr>
      <!-- ... -->
    </xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:template name="s-events">
  <xsl:param name="pos-id"/>
  <!-- cf. Michael Kay, "XSLT 2.0 and XPath 2.0", 4th ed., p. 895 -->
  <xsl:variable name="title-words"
                select="tokenize(replace(normalize-space(/basic-transcription/head/meta-information/ud-meta-information/ud-information[@attribute-name='text_title']),
                                         '(\p{L}+(-\p{L}+)*|\d+|\p{P})\s*','$1#'),
                                 '#')[.]"/>
  <xsl:variable name="starts-with-title"
                select="count($title-words)&gt;0 and
                        starts-with(string-join(tier[@category=$word]/event/text(),'&#x20;'),
                                    string-join($title-words,'&#x20;'))"/>
  <xsl:variable name="first-tli" as="element()">
    <xsl:choose>
      <xsl:when test="$starts-with-title">
        <xsl:copy-of select="common-timeline/tli[@id=../../tier[@category=$word]/event[position()=count($title-words)]/@end]"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="common-timeline/tli[1]"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="last-tli"
                select="common-timeline/tli[last()]"/>
  <xsl:variable name="end-tlis">
    <xsl:choose>
      <xsl:when test="$starts-with-title">
        <xsl:for-each select="tier[@id=$reference-id]/event[position()&gt;count($title-words)]
                                                           [@start=../../tier[@id=$pos-id]/event[.=$sentence-final-tags/tag][not(following-sibling::event[1][.=$sentence-final-tags/tag])]/@start or
                                                            .=$sentence-final-abbreviations/abbr and
                                                              following-sibling::event[1][matches(substring(.,1,1),'\p{Lu}')]]">
          <xsl:call-template name="get-end-tli"/>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:for-each select="tier[@id=$reference-id]/event[@start=../../tier[@id=$pos-id]/event[.=$sentence-final-tags/tag][not(following-sibling::event[1][.=$sentence-final-tags/tag])]/@start or
                                                            .=$sentence-final-abbreviations/abbr and
                                                              following-sibling::event[1][matches(substring(.,1,1),'\p{Lu}')]]">
          <xsl:call-template name="get-end-tli"/>
        </xsl:for-each>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:if test="$starts-with-title">
    <ud-tier-information>
      <ud-information attribute-name="title-start">
        <xsl:value-of select="tier[@category=$word]/event[1]/@start"/>
      </ud-information>
      <ud-information attribute-name="title-end">
        <xsl:value-of select="tier[@category=$word]/event[position()=count($title-words)]/@end"/>
      </ud-information>
    </ud-tier-information>
  </xsl:if>
  <xsl:choose>
    <xsl:when test="$end-tlis/tli">
      <xsl:for-each select="$end-tlis/tli">
        <event>
          <xsl:attribute name="start">
            <xsl:choose>
              <xsl:when test="preceding-sibling::tli">
                <xsl:value-of select="preceding-sibling::tli[1]/@id"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="$first-tli/@id"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:attribute>
          <xsl:attribute name="end">
            <xsl:value-of select="@id"/>
          </xsl:attribute>
          <xsl:text>s</xsl:text>
          <xsl:value-of select="count(preceding-sibling::tli)+1"/>
        </event>
      </xsl:for-each>
      <xsl:if test="not($end-tlis/tli[last()]/@id=$last-tli/@id)">
        <event>
          <xsl:attribute name="start">
            <xsl:value-of select="$end-tlis/tli[last()]/@id"/>
          </xsl:attribute>
          <xsl:attribute name="end">
            <xsl:value-of select="$last-tli/@id"/>
          </xsl:attribute>
          <xsl:text>s</xsl:text>
          <xsl:value-of select="count($end-tlis/tli)+1"/>
        </event>
      </xsl:if>
    </xsl:when>
    <xsl:otherwise>
      <event>
        <xsl:attribute name="start">
          <xsl:value-of select="$first-tli/@id"/>
        </xsl:attribute>
        <xsl:attribute name="end">
          <xsl:value-of select="$last-tli/@id"/>
        </xsl:attribute>
        <xsl:text>s1</xsl:text>
      </event>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
</xsl:stylesheet>
