<?xml version="1.0" encoding="utf-8"?>
<!-- exb2exb-word.xsl -->
<!-- Version 12.0 -->
<!-- Andreas Nolda 2020-12-07 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:tt="java:org.exmaralda.dulko.treetagger.TreeTagger"
                exclude-result-prefixes="tt">

<!-- We cannot include the master stylesheet because we want to change the $zh-number value. -->
<xsl:import href="exb2exb-tiers.xsl"/>

<!-- Changing the user-visible parameter into a variable. -->
<xsl:variable name="zh-number">0</xsl:variable>

<xsl:include href="lang.xsl"/>

<xsl:variable name="tagger"
              select="tt:new()"/>

<xsl:param name="tagger-home"
           select="tt:getHome($tagger)"/>

<xsl:param name="tagger-abbreviations-uri">
  <xsl:if test="string-length($tagger-lang)&gt;0">
    <xsl:text>file://</xsl:text>
    <xsl:choose>
      <!-- Microsoft Windows: -->
      <xsl:when test="matches($tagger-home,'^[A-Z]:\\')">
        <xsl:text>/</xsl:text>
        <xsl:value-of select="translate($tagger-home,'\','/')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$tagger-home"/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>/lib/</xsl:text>
    <xsl:value-of select="$tagger-lang"/>
    <xsl:text>-abbreviations</xsl:text>
  </xsl:if>
</xsl:param>

<xsl:variable name="abbreviations">
  <xsl:if test="string-length($tagger-abbreviations-uri)&gt;0">
    <xsl:if test="unparsed-text-available($tagger-abbreviations-uri)">
      <xsl:analyze-string select="unparsed-text($tagger-abbreviations-uri)"
                          regex="\n">
        <xsl:non-matching-substring>
          <abbr>
            <xsl:value-of select="."/>
          </abbr>
        </xsl:non-matching-substring>
      </xsl:analyze-string>
    </xsl:if>
  </xsl:if>
</xsl:variable>

<xsl:template match="basic-body">
  <xsl:if test="$abbreviations/abbr">
    <xsl:message>
      <xsl:text>Using abbreviations in </xsl:text>
      <xsl:value-of select="$tagger-abbreviations-uri"/>
    </xsl:message>
  </xsl:if>
  <xsl:variable name="potential-events">
    <xsl:for-each select="common-timeline/tli">
      <xsl:choose>
        <!-- There is a corresponding non-annotated event on the reference tier. -->
        <xsl:when test="../following-sibling::tier[@id=$reference-id]/event[@start=current()/@id]
                                                                           [not(@start=../following-sibling::tier/event/@start)]/@start">
          <event>
            <xsl:attribute name="start"
                           select="@id"/>
            <xsl:for-each select="../following-sibling::tier[@id=$reference-id]/event[@start=current()/@id]">
              <xsl:copy-of select="@end"/>
              <!-- Tokenize it. -->
              <xsl:call-template name="tokenize"/>
            </xsl:for-each>
          </event>
        </xsl:when>
        <!-- There is a no such event on the reference tier. -->
        <xsl:otherwise>
          <!-- Don't tokenize it. -->
          <event>
            <xsl:attribute name="start"
                           select="@id"/>
            <xsl:for-each select="../following-sibling::tier[@id=$reference-id]/event[@start=current()/@id]">
              <xsl:copy-of select="@end"/>
              <!-- Don't tokenize it. -->
              <xsl:value-of select="."/>
          </xsl:for-each>
          </event>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </xsl:variable>
  <xsl:variable name="preceding-tiers">
    <!-- tiers preceding the reference tier -->
    <xsl:apply-templates select="tier[following-sibling::tier[@id=$reference-id]]"/>
  </xsl:variable>
  <xsl:variable name="preceding-tier-number"
                select="count($preceding-tiers/tier)"/>
  <basic-body>
    <xsl:call-template name="timeline">
      <xsl:with-param name="potential-events"
                      select="$potential-events"/>
    </xsl:call-template>
    <xsl:copy-of select="$preceding-tiers"/>
    <!-- reference tier -->
    <xsl:call-template name="tier">
      <xsl:with-param name="preceding-tier-number"
                      select="$preceding-tier-number"/>
      <xsl:with-param name="category"
                      select="$word"/>
      <xsl:with-param name="type">t</xsl:with-param>
      <xsl:with-param name="events">
        <xsl:call-template name="word-events">
          <xsl:with-param name="potential-events"
                          select="$potential-events"/>
        </xsl:call-template>
      </xsl:with-param>
    </xsl:call-template>
    <!-- tiers following the reference tier -->
    <xsl:apply-templates select="tier[preceding-sibling::tier[@id=$reference-id]]"/>
  </basic-body>
</xsl:template>

<xsl:template name="tokenize-word">
  <token>
    <xsl:value-of select="."/>
  </token>
</xsl:template>

<xsl:template name="tokenize-range">
  <!-- range dash -->
  <xsl:analyze-string select="."
                      regex="{'-'}"><!-- "-" in e.g. "1-3" or "1.-3." -->
    <xsl:matching-substring>
      <token>
        <xsl:value-of select="."/>
      </token>
    </xsl:matching-substring>
    <xsl:non-matching-substring>
      <token>
        <xsl:value-of select="."/>
      </token>
    </xsl:non-matching-substring>
  </xsl:analyze-string>
</xsl:template>

<xsl:template name="tokenize-apostrophe">
  <!-- internal apostrophe -->
  <xsl:analyze-string select="."
                      regex="{'''\p{L}+'}"><!-- e.g. "'s" -->
    <xsl:matching-substring>
      <token>
        <xsl:value-of select="."/>
      </token>
    </xsl:matching-substring>
    <xsl:non-matching-substring>
      <token>
        <xsl:value-of select="."/>
      </token>
    </xsl:non-matching-substring>
  </xsl:analyze-string>
</xsl:template>

<xsl:template name="tokenize-punctuation">
  <xsl:choose>
    <!-- ellipses -->
    <xsl:when test="matches(.,'\.{3,}')"><!-- "..." -->
      <token>
        <xsl:value-of select="."/>
      </token>
    </xsl:when>
    <xsl:otherwise>
      <!-- punctuation characters -->
      <xsl:analyze-string select="."
                          regex="{'\p{P}'}">
        <xsl:matching-substring>
          <token>
            <xsl:value-of select="."/>
          </token>
        </xsl:matching-substring>
      </xsl:analyze-string>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template name="tokenize">
  <!-- break normalized string at white space -->
  <xsl:analyze-string select="replace(translate(.,'&#xA0;–—„“”‚‘’',
                                                  '&#x20;--&#x22;&#x22;&#x22;'''''''),'…',
                                                                                 '...')"
                      regex="{'\s+'}">
    <xsl:non-matching-substring>
      <xsl:choose>
        <!-- abbreviations -->
        <xsl:when test="$abbreviations/abbr[contains(current(),.) and
                                            matches(substring-before(current(),.),'^\p{P}*$') and
                                            matches(substring-after(current(),.),'^\p{P}*$')]">
          <xsl:analyze-string select="."
                              regex="{'\p{L}+(\.?-\p{L}+)*\.'}">
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-word"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- simple URLs: -->
        <xsl:when test="matches(.,'^\p{P}*(\p{L}+://)?[\p{L}\p{N}]+([.-][\p{L}\p{N}]+)*\.\p{L}+\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'(\p{L}+://)?[\p{L}\p{N}]+([.-][\p{L}\p{N}]+)*\.\p{L}+'}">
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-word"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- e-mail addresses: -->
        <xsl:when test="matches(.,'^\p{P}*[\p{L}\p{N}]+(.[\p{L}\p{N}]+)@[\p{L}\p{N}]+([.-][\p{L}\p{N}]+)*\.\p{L}+\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'[\p{L}\p{N}]+(.[\p{L}\p{N}]+)@[\p{L}\p{N}]+([.-][\p{L}\p{N}]+)*\.\p{L}+'}">
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-word"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- ordinal number ranges -->
        <xsl:when test="matches(.,'^\p{P}*\d+\.-\d+\.\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'\d+\.-\d+\.'}"><!-- e.g. "9.-12." -->
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-range"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- cardinal number ranges -->
        <xsl:when test="matches(.,'^\p{P}*\d+-\d+\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'\d+-\d+'}"><!-- e.g. "9-12" -->
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-range"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- ordinal numbers -->
        <xsl:when test="matches(.,'^\p{P}*\d+\.\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'\d+\.'}"><!-- e.g. "12." -->
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-word"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <!-- internal apostrophes -->
        <xsl:when test="matches(.,'^\p{P}*\p{L}+''\p{L}+\p{P}*$')">
          <xsl:analyze-string select="."
                              regex="{'\p{L}+''\p{L}+'}"><!-- e.g. "hat's" -->
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-apostrophe"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:when>
        <xsl:otherwise>
          <!-- tokenize words -->
          <xsl:analyze-string select="."
                              regex="{'\p{L}+(-\p{L}+)*'}"><!-- e.g. "Mecklenburg" or "Mecklenburg-Vorpommern" -->
            <xsl:matching-substring>
              <xsl:call-template name="tokenize-word"/>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
              <xsl:call-template name="tokenize-punctuation"/>
            </xsl:non-matching-substring>
          </xsl:analyze-string>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:non-matching-substring>
  </xsl:analyze-string>
</xsl:template>

<xsl:template name="tli-id">
  <xsl:text>T</xsl:text>
  <xsl:value-of select="max(/event/@start/number(substring-after(.,'T')))+
                        count(preceding::token[preceding-sibling::token])+1"/>
</xsl:template>

<xsl:template name="timeline">
  <xsl:param name="potential-events"/>
  <common-timeline>
    <xsl:for-each select="$potential-events/event">
      <xsl:choose>
        <xsl:when test="token">
          <xsl:for-each select="token">
            <tli>
              <xsl:attribute name="id">
                <xsl:choose>
                  <xsl:when test="position()=1">
                    <xsl:value-of select="../@start"/>
                  </xsl:when>
                  <xsl:otherwise>
                    <xsl:call-template name="tli-id"/>
                  </xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>
            </tli>
          </xsl:for-each>
        </xsl:when>
        <xsl:otherwise>
          <tli>
            <xsl:attribute name="id"
                           select="@start"/>
          </tli>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:for-each>
  </common-timeline>
</xsl:template>

<xsl:template name="word-events">
  <xsl:param name="potential-events"/>
  <xsl:for-each select="$potential-events/event[string-length()&gt;0]">
    <xsl:choose>
      <xsl:when test="token">
        <xsl:for-each select="token">
          <event>
            <xsl:choose>
              <xsl:when test="position()=1">
                <xsl:copy-of select="../@start"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="start">
                  <xsl:call-template name="tli-id"/>
                </xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:choose>
              <xsl:when test="position()=last()">
                <xsl:copy-of select="../@end"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:attribute name="end">
                  <xsl:for-each select="following-sibling::token[1]">
                    <xsl:call-template name="tli-id"/>
                  </xsl:for-each>
                </xsl:attribute>
              </xsl:otherwise>
            </xsl:choose>
            <xsl:value-of select="."/>
          </event>
        </xsl:for-each>
      </xsl:when>
      <xsl:otherwise>
        <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:for-each>
</xsl:template>
</xsl:stylesheet>
