<?xml version="1.0" encoding="utf-8"?>
<!-- metadata.xsl -->
<!-- Version 3.1 -->
<!-- Andreas Nolda 2020-12-07 -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs">

<xsl:strip-space elements="head
                           meta-information
                           ud-meta-information
                           speakertable
                           speaker
                           ud-speaker-information"/>

<xsl:variable name="word">
  <xsl:choose>
    <xsl:when test="/basic-transcription/basic-body/tier[@category='tok']">tok</xsl:when><!-- deprecated -->
    <xsl:otherwise>word</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<!-- directory relative to the current EXB file -->
<xsl:param name="corpus"/>

<xsl:variable name="corpus-variables">
  <variable>L1_language</variable>
  <variable>L2_language</variable>
  <variable>L2_target</variable>
  <variable>annotation</variable>
  <variable>annotation_complete</variable>
  <variable>annotation_other</variable>
  <variable>availability</variable>
  <variable>character_markup</variable>
  <variable>comp_data</variable>
  <variable>corpus_acronym</variable>
  <variable>corpus_mode</variable>
  <variable>corpus_size</variable>
  <variable>corpus_title</variable>
  <variable>distributor</variable>
  <variable>editorial_decisions</variable>
  <variable>error_annotated</variable>
  <variable>error_annotation_tool</variable>
  <variable>field</variable>
  <variable>license</variable>
  <variable>longitudinal</variable>
  <variable>markup_language</variable>
  <variable>official_language_testing</variable>
  <variable>parsed</variable>
  <variable>pos_tagged</variable>
  <variable>pos_tagset</variable>
  <variable>proficiency_level</variable>
  <variable>proficiency_level_descriptors</variable>
  <variable>proficiency_level_type</variable>
  <variable>transcription_guidelines</variable>
  <variable>written_process</variable>
</xsl:variable>

<xsl:template name="get-corpus-metadata">
  <xsl:call-template name="get-corpus-metadata-in-meta-information"/>
  <xsl:call-template name="get-corpus-metadata-in-speakertable"/>
</xsl:template>

<xsl:template name="get-corpus-metadata-in-meta-information">
  <xsl:apply-templates select="/basic-transcription/head/meta-information"
                       mode="corpus-metadata"/>
</xsl:template>

<xsl:template name="get-corpus-metadata-in-speakertable">
  <xsl:apply-templates select="/basic-transcription/head/speakertable"
                       mode="corpus-metadata"/>
</xsl:template>

<!-- <xsl:variable name="document-variables">
  <variable>L1</variable>
  <variable>L2_other_1</variable>
  <variable>L2_region</variable>
  <variable>L2_study_institutions</variable>
  <variable>L2_study_years</variable>
  <variable>age</variable>
  <variable>country</variable>
  <variable>date</variable>
  <variable>gender</variable>
  <variable>institution</variable>
  <variable>learner_ID</variable>
  <variable>learner_level_CEFR_conversion</variable>
  <variable>learner_proficiency</variable>
  <variable>learner_proficiency_level_type</variable>
  <variable>learner_proficiency_rating_scale</variable>
  <variable>learner_status</variable>
  <variable>study_area</variable>
  <variable>study_level</variable>
  <variable>task_document</variable>
  <variable>task_document_ID</variable>
  <variable>task_instructions</variable>
  <variable>task_type</variable>
  <variable>text_ID</variable>
  <variable>text_title</variable>
  <variable>time_spent_L2_country</variable>
  <variable>written_ref_tools</variable>
  <variable>written_task</variable>
</xsl:variable> -->

<xsl:template name="get-document-metadata">
  <xsl:call-template name="get-document-metadata-in-meta-information"/>
  <xsl:call-template name="get-document-metadata-in-speakertable"/>
</xsl:template>

<xsl:template name="get-document-metadata-in-meta-information">
  <xsl:apply-templates select="/basic-transcription/head/meta-information"
                       mode="document-metadata"/>
</xsl:template>

<xsl:template name="get-document-metadata-in-speakertable">
  <xsl:apply-templates select="/basic-transcription/head/speakertable"
                       mode="document-metadata"/>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='annotation']"
              mode="corpus-metadata">
  <meta variable="annotation">
    <xsl:choose>
      <xsl:when test="/basic-transcription/basic-body/tier[@type='a']">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='pos_tagged']"
              mode="corpus-metadata">
  <meta variable="pos_tagged">
    <xsl:choose>
      <xsl:when test="/basic-transcription/basic-body/tier[ends-with(@category,'pos')]">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='pos_tagset']"
              mode="corpus-metadata">
  <meta variable="pos_tagset">
    <xsl:if test="/basic-transcription/basic-body/tier[ends-with(@category,'pos')]">STTS</xsl:if>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='error_annotated']"
              mode="corpus-metadata">
  <meta variable="error_annotated">
    <xsl:choose>
      <xsl:when test="/basic-transcription/basic-body/tier[contains(@category,'Fehler')]">yes</xsl:when>
      <xsl:otherwise>no</xsl:otherwise>
    </xsl:choose>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='error_annotation_tool']"
              mode="corpus-metadata">
  <meta variable="error_annotation_tool">
    <xsl:if test="/basic-transcription/basic-body/tier[contains(@category,'Fehler')]">EXMARaLDA (Dulko)</xsl:if>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='annotation_other']"
              mode="corpus-metadata">
  <xsl:variable name="annotations">
    <xsl:if test="/basic-transcription/basic-body/tier[ends-with(@category,'lemma')]">
      <annotation>lemmata</annotation>
    </xsl:if>
    <xsl:if test="/basic-transcription/basic-body/tier[ends-with(@category,'S')]">
      <annotation>sentence spans</annotation>
    </xsl:if>
    <xsl:if test="/basic-transcription/basic-body/tier[@category='Diff']">
      <annotation>editorial changes by the learner</annotation>
    </xsl:if>
    <xsl:if test="/basic-transcription/basic-body/tier[@category='trans']">
      <annotation>translated text</annotation>
    </xsl:if>
    <xsl:if test="/basic-transcription/basic-body/tier[@category='ZH']">
      <annotation>target hypotheses</annotation>
    </xsl:if>
    <xsl:if test="/basic-transcription/basic-body/tier[@category='ZHDiff']">
      <annotation>differences</annotation>
    </xsl:if>
  </xsl:variable>
  <meta variable="annotation_other">
    <xsl:value-of select="string-join($annotations/annotation,', ')"/>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='corpus_size']"
              mode="corpus-metadata">
  <xsl:variable name="number">
    <xsl:choose>
      <!-- sum up the tokens in the corpus, if specified -->
      <xsl:when test="string-length($corpus)&gt;0">
        <xsl:variable name="files"
                      select="collection(concat(resolve-uri($corpus,base-uri()),
                                                '?select=*.exb;recurse=yes'))"/>
        <xsl:variable name="numbers" as="xs:integer*">
          <xsl:for-each select="$files">
            <xsl:sequence select="count(/basic-transcription/basic-body/tier[@category=$word]/event)"/>
          </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="sum($numbers)"/>
      </xsl:when>
      <!-- only count the tokens in the current file -->
      <xsl:otherwise>
        <xsl:value-of select="count(/basic-transcription/basic-body/tier[@category=$word]/event)"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <meta variable="corpus_size">
    <xsl:choose>
      <xsl:when test="$number=1">
        <xsl:value-of select="$number"/>
        <xsl:text> token</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$number"/>
        <xsl:text> tokens</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </meta>
</xsl:template>

<xsl:template match="ud-information[@attribute-name='AutoSave']"
              mode="corpus-metadata"/>

<xsl:template match="ud-information"
              mode="corpus-metadata">
  <xsl:if test="@attribute-name=$corpus-variables/variable">
    <meta>
      <xsl:attribute name="variable"
                     select="@attribute-name"/>
      <xsl:value-of select="."/>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="ud-information"
              mode="document-metadata">
  <xsl:if test="not(@attribute-name=$corpus-variables/variable)">
    <meta>
      <xsl:attribute name="variable"
                     select="@attribute-name"/>
      <xsl:value-of select="."/>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="project-name"
              mode="corpus-metadata">
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='corpus_title'][string-length()&gt;0])">
    <meta variable="corpus_title">
      <xsl:value-of select="substring-before(.,'(')"/>
    </meta>
  </xsl:if>
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='corpus_acronym'][string-length()&gt;0])">
    <meta variable="corpus_acronym">
      <xsl:value-of select="substring-before(substring-after(.,'('),')')"/>
    </meta>
  </xsl:if>
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='distributor'][string-length()&gt;0])">
    <meta variable="distributor">
      <xsl:value-of select="substring-after(.,', ')"/>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="transcription-name"
              mode="document-metadata">
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='text_ID'][string-length()&gt;0])">
  <meta variable="text_ID">
    <xsl:value-of select="."/>
  </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="transcription-convention"
              mode="corpus-metadata">
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='editorial_decisions'][string-length()&gt;0])">
    <meta variable="editorial_decisions">
      <xsl:value-of select="concat(substring-before(.,'. '),'.')"/>
    </meta>
  </xsl:if>
  <xsl:if test="not(../ud-meta-information/ud-information[@attribute-name='transcription_guidelines'][string-length()&gt;0])">
    <meta variable="transcription_guidelines">
      <xsl:value-of select="substring-after(.,'. ')"/>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="abbreviation"
              mode="document-metadata">
  <xsl:if test="not(../ud-speaker-information/ud-information[@attribute-name='learner_ID'][string-length()&gt;0])">
    <meta variable="learner_ID">
      <xsl:value-of select="."/>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="sex"
              mode="document-metadata">
  <xsl:if test="not(../ud-speaker-information/ud-information[@attribute-name='gender'][string-length()&gt;0])">
    <meta variable="gender">
      <xsl:choose>
        <xsl:when test="@value='f'">
          <xsl:text>female</xsl:text>
        </xsl:when>
        <xsl:when test="@value='m'">
          <xsl:text>male</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>unkown</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="languages-used"
              mode="corpus-metadata">
  <xsl:if test="not(../ud-speaker-information/ud-information[@attribute-name='L2_language'][string-length()&gt;0])">
    <meta variable="L2_language">
      <xsl:for-each select="language">
        <xsl:if test="preceding-sibling::language">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:value-of select="@lang"/>
      </xsl:for-each>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="l1"
              mode="corpus-metadata">
  <xsl:if test="not(../ud-speaker-information/ud-information[@attribute-name='L1_language'][string-length()&gt;0])">
    <meta variable="L1_language">
      <xsl:for-each select="language[1]"><!-- The corpus L1 should come first. -->
        <xsl:value-of select="@lang"/>
      </xsl:for-each>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="l1"
              mode="document-metadata">
  <xsl:if test="not(../ud-speaker-information/ud-information[@attribute-name='L1'][string-length()&gt;0])">
    <meta variable="L1">
      <xsl:for-each select="language">
        <xsl:if test="preceding-sibling::language">
          <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:value-of select="@lang"/>
      </xsl:for-each>
    </meta>
  </xsl:if>
</xsl:template>

<xsl:template match="l2"
              mode="document-metadata">
  <xsl:choose>
    <xsl:when test="not(../ud-speaker-information/ud-information[@attribute-name='L2'][string-length()&gt;0])">
      <meta variable="L2_other">
        <xsl:text>yes</xsl:text>
      </meta>
      <xsl:for-each select="language[not(@lang=../../languages-used/language/@lang)]">
        <meta>
          <xsl:attribute name="variable">
            <xsl:text>L2_other_</xsl:text>
            <xsl:value-of select="position()"/>
          </xsl:attribute>
          <xsl:value-of select="@lang"/>
        </meta>
      </xsl:for-each>
    </xsl:when>
    <xsl:otherwise>
      <meta variable="L2_other">
        <xsl:text>no</xsl:text>
      </meta>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>
</xsl:stylesheet>
