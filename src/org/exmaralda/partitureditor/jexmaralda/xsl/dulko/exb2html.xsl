<?xml version="1.0" encoding="utf-8"?>
<!-- exb2html.xsl -->
<!-- Version 8.5 -->
<!-- Andreas Nolda 2019-05-06 -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:include href="metadata.xsl"/>

<xsl:output doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
            encoding="utf-8"
            indent="yes"
            method="xml"/>

<xsl:strip-space elements="basic-transcription
                           head
                           speaker
                           tier
                           ud-speaker-information"/>

<xsl:param name="css">
  <xsl:text>dt {
  display: inline-block;
}
dt:after {
  content: ":";
}
dt.empty {
  color: red;
}
dd {
  display: inline;
  margin-left: 0.5em;
}
dd:after {
  display: block;
  content: "";
}
table {
  border-collapse: collapse;
}
td,
th {
  border: 1px solid lightgrey;
  padding-left: 0.5ex;
  padding-right: 0.5ex;
  text-align: left;
  white-space: nowrap;
  font-weight: normal;
}
.timeline th {
  border: none;
  padding-left: 0px;
}
.orig td {
  font-weight: bold;
}
.</xsl:text>
  <!-- for backward compatibility -->
  <xsl:value-of select="$word"/>
  <xsl:text> td {
  font-weight: bold;
}
.ZH td {
  font-weight: bold;
}
.Diff td,
.ZHDiff td {
  color: green;
}
.Layout td,
.Graph td {
  color: blue;
}
.Fehler td {
  color: red;
}
td.empty {
  background-color: lightgrey;
}
@media print {
  h3 {
    page-break-before: always;
  }
  table {
    page-break-inside: avoid;
  }
}</xsl:text>
</xsl:param>

<xsl:variable name="corpus-metadata">
  <xsl:call-template name="get-corpus-metadata"/>
</xsl:variable>

<xsl:variable name="document-metadata">
  <xsl:call-template name="get-document-metadata"/>
</xsl:variable>

<xsl:variable name="corpus-name">
  <xsl:choose>
    <xsl:when test="string-length($corpus-metadata/meta[@variable='corpus_acronym'])&gt;0">
      <xsl:value-of select="$corpus-metadata/meta[@variable='corpus_acronym']"/>
    </xsl:when>
    <xsl:when test="string-length($corpus-metadata/meta[@variable='corpus_title'])&gt;0">
      <xsl:value-of select="$corpus-metadata/meta[@variable='corpus_title']"/>
    </xsl:when>
    <xsl:otherwise>Unbenanntes Korpus</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:variable name="text">
  <xsl:choose>
    <xsl:when test="string-length($document-metadata/meta[@variable='text_ID'])&gt;0">
      <xsl:value-of select="$document-metadata/meta[@variable='text_ID']"/>
    </xsl:when>
    <xsl:otherwise>Unbenannter Text</xsl:otherwise>
  </xsl:choose>
</xsl:variable>

<xsl:template match="/basic-transcription">
  <xsl:choose>
    <xsl:when test="basic-body/tier[contains(@category,'::')]">
      <xsl:message terminate="yes">Error: Tiers with namespace prefixes. Remove namespace prefixes first.</xsl:message>
    </xsl:when>
    <xsl:when test="not(basic-body/tier[@category='S'])">
      <xsl:message terminate="yes">Error: No S tier. Create a S tier first.</xsl:message>
    </xsl:when>
    <xsl:otherwise>
      <html xmlns="http://www.w3.org/1999/xhtml">
        <xsl:apply-templates/>
      </html>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="head">
  <head>
    <title>
      <xsl:value-of select="$corpus-name"/>
      <xsl:text>: </xsl:text>
      <xsl:value-of select="$text"/>
    </title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <style type="text/css">
      <xsl:value-of select="$css"/>
    </style>
  </head>
</xsl:template>

<xsl:template name="table">
  <xsl:param name="start"/>
  <xsl:param name="end"/>
  <xsl:variable name="subtimeline">
    <xsl:copy-of select="../../common-timeline/tli[@id=$start]"/>
    <xsl:copy-of select="../../common-timeline/tli[preceding-sibling::tli[@id=$start] and
                                                                             following-sibling::tli[@id=$end]]"/>
    <xsl:copy-of select="../../common-timeline/tli[@id=$end]"/>
  </xsl:variable>
  <table>
    <xsl:apply-templates select="../../common-timeline">
      <xsl:with-param name="subtimeline"
                      select="$subtimeline"/>
    </xsl:apply-templates>
    <xsl:apply-templates select="../../tier">
      <xsl:with-param name="subtimeline"
                      select="$subtimeline"/>
    </xsl:apply-templates>
  </table>
</xsl:template>

<xsl:template match="basic-body">
  <body>
    <h1>
      <xsl:value-of select="$corpus-name"/>
    </h1>
    <h2>
      <xsl:value-of select="$text"/>
    </h2>
    <xsl:if test="$corpus-metadata/meta or
                  $document-metadata/meta">
      <h3>Metadaten</h3>
      <xsl:if test="$corpus-metadata/meta">
        <h4>Korpus</h4>
        <dl>
          <xsl:for-each select="$corpus-metadata/meta">
            <xsl:sort select="@variable"/>
            <dt>
              <xsl:if test="string-length()=0">
                <xsl:attribute name="class">empty</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="@variable"/>
            </dt>
            <dd>
              <xsl:value-of select="."/>
              <xsl:if test="@variable='corpus_size' and
                            string-length($corpus)=0">
                <xsl:text> (current file)</xsl:text>
              </xsl:if>
            </dd>
          </xsl:for-each>
        </dl>
      </xsl:if>
      <xsl:if test="$document-metadata/meta">
        <h4>Dokument</h4>
        <dl>
          <xsl:for-each select="$document-metadata/meta">
            <xsl:sort select="@variable"/>
            <dt>
              <xsl:if test="string-length()=0">
                <xsl:attribute name="class">empty</xsl:attribute>
              </xsl:if>
              <xsl:value-of select="@variable"/>
            </dt>
            <dd>
              <xsl:value-of select="."/>
            </dd>
          </xsl:for-each>
        </dl>
      </xsl:if>
    </xsl:if>
    <xsl:for-each select="tier[@category='S']/ud-tier-information[ud-information[@attribute-name='title-start'] and
                                                                  ud-information[@attribute-name='title-end']]">
      <br/><!-- ? -->
      <h3>Überschrift</h3>
      <xsl:call-template name="table">
        <xsl:with-param name="start">
          <xsl:value-of select="ud-information[@attribute-name='title-start']"/>
        </xsl:with-param>
        <xsl:with-param name="end">
          <xsl:value-of select="ud-information[@attribute-name='title-end']"/>
        </xsl:with-param>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:for-each select="tier[@category='S']/event">
      <br/><!-- ? -->
      <h3>Satzspanne&#xA0;<xsl:value-of select="position()"/></h3>
      <xsl:call-template name="table">
        <xsl:with-param name="start"
                        select="@start"/>
        <xsl:with-param name="end"
                        select="@end"/>
      </xsl:call-template>
    </xsl:for-each>
  </body>
</xsl:template>

<xsl:template match="common-timeline">
  <xsl:param name="subtimeline"/>
  <tr>
    <xsl:attribute name="class">timeline</xsl:attribute>
    <th/>
    <xsl:for-each select="tli[@id=$subtimeline/tli[not(position()=last())]/@id]">
      <th>
        <xsl:value-of select="count(preceding-sibling::tli)"/>
      </th>
    </xsl:for-each>
  </tr>
</xsl:template>

<xsl:template name="cell">
  <xsl:param name="start"/>
  <xsl:param name="end"/>
  <xsl:param name="content"/>
  <xsl:variable name="length"
                select="$end - $start"/>
  <td>
    <xsl:if test="$length&gt;1">
      <xsl:attribute name="colspan">
        <xsl:value-of select="$length"/>
      </xsl:attribute>
    </xsl:if>
    <xsl:if test="string-length($content)=0">
      <xsl:attribute name="class">empty</xsl:attribute>
    </xsl:if>
    <xsl:copy-of select="$content"/>
  </td>
</xsl:template>

<xsl:template name="event">
  <xsl:param name="subtimeline"/>
  <xsl:if test="position()=1 and
                index-of($subtimeline/tli/@id,@start)&gt;1">
    <!-- empty cell at the start of the row -->
    <xsl:call-template name="cell">
      <xsl:with-param name="start"
                      select="1"/>
      <xsl:with-param name="end"
                      select="index-of($subtimeline/tli/@id,@start)"/>
      <xsl:with-param name="content"/>
    </xsl:call-template>
  </xsl:if>
  <!-- this cell -->
  <xsl:call-template name="cell">
    <xsl:with-param name="start"
                    select="index-of($subtimeline/tli/@id,@start)"/>
    <xsl:with-param name="end"
                    select="index-of($subtimeline/tli/@id,@end)"/>
    <xsl:with-param name="content">
      <xsl:apply-templates/>
    </xsl:with-param>
  </xsl:call-template>
  <xsl:if test="@end!=following-sibling::event[1]/@start">
    <!-- empty cell before the next cell -->
    <xsl:call-template name="cell">
      <xsl:with-param name="start"
                      select="index-of($subtimeline/tli/@id,@end)"/>
      <xsl:with-param name="end"
                      select="index-of($subtimeline/tli/@id,following-sibling::event[1]/@start)"/>
      <xsl:with-param name="content"/>
    </xsl:call-template>
  </xsl:if>
  <xsl:if test="position()=last() and
                index-of($subtimeline/tli/@id,@end)&lt;count($subtimeline/tli)">
    <!-- empty cell at the end of the row -->
    <xsl:call-template name="cell">
      <xsl:with-param name="start"
                         select="index-of($subtimeline/tli/@id,@end)"/>
      <xsl:with-param name="end"
                      select="count($subtimeline/tli)"/>
      <xsl:with-param name="content"/>
    </xsl:call-template>
  </xsl:if>
</xsl:template>

<xsl:template match="tier">
  <xsl:param name="subtimeline"/>
  <xsl:variable name="subtier">
    <xsl:copy-of select="event[@start=$subtimeline/tli/@id and
                               @end=$subtimeline/tli/@id]"/>
  </xsl:variable>
  <tr>
    <xsl:attribute name="class">
      <xsl:choose>
        <xsl:when test="starts-with(@category,'Fehler')">
          <xsl:text>Fehler</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="@category"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:attribute>
    <th>
      <xsl:value-of select="@display-name"/>
    </th>
    <xsl:for-each select="$subtier/event">
      <xsl:call-template name="event">
        <xsl:with-param name="subtimeline"
                        select="$subtimeline"/>
      </xsl:call-template>
    </xsl:for-each>
    <xsl:if test="not($subtier/event)">
      <!-- empty tier -->
      <xsl:call-template name="cell">
        <xsl:with-param name="start"
                        select="1"/>
        <xsl:with-param name="end"
                        select="count($subtimeline/tli)"/>
        <xsl:with-param name="content"/>
      </xsl:call-template>
    </xsl:if>
  </tr>
</xsl:template>

<xsl:template match="tierformat-table"/>
</xsl:stylesheet>
