<?xml version="1.0" encoding="UTF-8"?>
<!-- edited with XMLSPY v2004 rel. 3 U (http://www.xmlspy.com) by eval (eval) -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:hzsk="http://www.hzsk.de/" xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="#all">
	<xsl:output method="html" encoding="UTF-8" indent="yes"/>
	<xsl:key name="get-speaker-by-id" match="Speaker" use="@Id"/>
	<xsl:template match="/">
		<html>
			<head>
				<xsl:call-template name="GENERATE_STYLES"/>
			</head>
			<body>
				<xsl:apply-templates/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="//Corpus">
		<h1>
			<xsl:value-of select="@Name"/>
		</h1>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="Communication">
		<xsl:variable name="communication-time" as="xs:dateTime">
			<xsl:choose>
				<xsl:when test="string-length(string(Location/Period/PeriodStart))>0">
					<xsl:value-of select="Location/Period/PeriodStart"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="current-dateTime()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<h2>Communication <xsl:value-of select="@Name"/> (<xsl:value-of select="@Id"/>) on
				<xsl:value-of select="day-from-dateTime($communication-time)"/>.<xsl:value-of
				select="month-from-dateTime($communication-time)"/>.<xsl:value-of
				select="year-from-dateTime($communication-time)"/></h2>
		<!--		<xsl:apply-templates select="Description"/> -->
		<xsl:for-each select="key('get-speaker-by-id', Setting/Person)">
			<xsl:variable name="birth-time" as="xs:dateTime">
				<xsl:choose>
					<xsl:when
						test="string-length(string(Location[@type='Geburt' or @type='birth'][1]/Period/PeriodStart/text()))>0">
						<xsl:value-of select="Location[@type='Geburt' or @type='birth'][1]/Period/PeriodStart"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="current-dateTime()"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<h3><xsl:value-of select="Pseudo"/>: <xsl:choose>
					<xsl:when
						test="xs:integer(days-from-duration(xs:dateTime($communication-time)-xs:dateTime($birth-time)))>0">
						<xsl:value-of
							select="xs:integer(days-from-duration(xs:dateTime($communication-time)-xs:dateTime($birth-time)) div 365.25)"
						/> Years, <xsl:value-of
							select="xs:integer(days-from-duration(xs:dateTime($communication-time)-xs:dateTime($birth-time)) mod 365.25 div 30.4375)"
						/> Months </xsl:when>
				</xsl:choose>
			</h3>
			<xsl:apply-templates/>
		</xsl:for-each>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="Description">
		<table>
			<tr>
				<th colspan="2">Description for <xsl:value-of select="name(parent::node())"/></th>
			</tr>
			<xsl:apply-templates/>

		</table>
	</xsl:template>
	<xsl:template match="Key">
		<tr>
			<td>
				<xsl:value-of select="@Name"/>
			</td>
			<td>
				<xsl:value-of select="text()"/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="Sigle|Sex|Pseudo|KnownHuman|PeriodStart|PeriodExact|PostalCode|City|Country">
		<table>
			<tr>
				<td>
					<xsl:value-of select="name(.)"/>
				</td>
				<td>
					<xsl:value-of select="text()"/>
				</td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="Setting"/>
	<xsl:template match="Person"/>
	<xsl:template match="Speaker"/>
	<xsl:template match="Transcription"/>
	<xsl:template match="Recording"/>
	<xsl:template match="File"/>
	<xsl:template match="Period">
		<xsl:if test="node()">
			<table bgcolor="#ccccff">
				<tr>
					<th>Period</th>
				</tr>
				<tr>
					<td><xsl:apply-templates/></td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template match="Location">
		<table bgcolor="#ccffcc">
			<tr>
				<th colspan="2">Location (type: <xsl:value-of select="@type"/>)</th>
			</tr>
			<tr>
				<td><xsl:apply-templates/></td>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="Language"/>
	<xsl:template name="GENERATE_STYLES">
		<style type="text/css">
			#page {
			border-collapse: collapse;
			}
			body{
			    font-family:calibri, helvetica, sans-serif;
			    font-size:10pt;
			}
			table{
			    width:500px;
			    margin: 0px;
			    padding: 0px;
			    border: 0px;
			    border-collapse: collapse;
			    }
			th{
			    font-size:9pt;
			    background-color:silver;
			}
			td{
			    width:50%;
			    font-size:9pt;
			    vertical-align:top;
			}
			h1{
			    font-size:11pt;
			    border:1px solid black;
			    padding:3px;
			    max-width:500px;
			}</style>
	</xsl:template>

</xsl:stylesheet>
