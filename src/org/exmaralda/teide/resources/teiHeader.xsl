<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"  xpath-default-namespace="http://www.tei-c.org/ns/1.0">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	
	<xsl:template match="/">
		<html>
			<head>
				<title>
					<xsl:value-of select="//teiHeader[1]/fileDesc/titleStmt/title[1]"/>
				</title>
			</head>
			<body>
				<pre style="color:#ffffff;">teiHeader.xsl (internal TEIDE-Stylesheet)</pre>
				<xsl:apply-templates select="//teiCorpus/teiHeader"/>
				<xsl:apply-templates select="//TEI/teiHeader"/>
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="TEI/teiHeader">
		<h1>
			<xsl:for-each select="fileDesc/titleStmt/title">
				<xsl:value-of select="text()"/><br/>
			</xsl:for-each>
			<xsl:value-of select="fileDesc/titleStmt/title"/> (<xsl:value-of select="//idno"/>)</h1>
		<div style="background-color: #ffffbb; padding: 10px; spacing: 10px;">
			<h2>File:</h2>
			<xsl:apply-templates select="fileDesc"/>
		</div><br/>
		<div style="background-color: #ffffbb; padding: 10px; spacing: 10px;">
			<h2>Encoding:</h2>
			<xsl:apply-templates select="encodingDesc"/>
		</div><br/>
		<div style="background-color: #ffffbb; padding: 10px; spacing: 10px;">
			<h2>Revisionen:</h2>
			<xsl:apply-templates select="revisionDesc"/>
		</div><br/>
	</xsl:template>
	<xsl:template match="fileDesc">
		<b>Erstellt von:</b><xsl:text> </xsl:text><xsl:value-of select="publicationStmt/authority/text()"/><br/>
		<b>Verfügbarkeit:</b><xsl:text> </xsl:text><xsl:value-of select="publicationStmt/availability/node()"/><br/>
		<b>Quelle:</b><xsl:text> </xsl:text><xsl:value-of select="sourceDesc/node()"/><br/>
		<b>Umfang:</b><xsl:text> </xsl:text><xsl:value-of select="extent/text()"/><br/>
	</xsl:template>
	
	<xsl:template match="encodingDesc">
		<xsl:apply-templates/> 
	</xsl:template>
	
	<xsl:template match="revisionDesc">
		<ul>
			<xsl:for-each select="change">
				<li><xsl:value-of select="@n"/> (<xsl:value-of select="date/text()"/>) : <xsl:value-of select="item"/> von <xsl:value-of select="respStmt/name[not(@type='affiliation')]"/></li>
			</xsl:for-each>
		</ul>
	</xsl:template>
</xsl:stylesheet>
