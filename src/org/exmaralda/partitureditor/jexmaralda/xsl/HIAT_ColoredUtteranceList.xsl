<?xml version="1.0" encoding="UTF-8"?>
<!-- STYLESHEET "HIAT_ColoredUtteranceList", Thomas Schmidt, 14.04.2004 -->
<!-- Stylsheet zur Anwendung auf eine EXMARaLDA List-Transcription -->
<!-- (im Partitur-Editor: Segmentation-HIAT Segmentation-Utterance List (HTML)) -->
<!-- Generiert eine HTML-Datei mit einer Darstellung der List-Transcription als zeitlich geordnete Liste von Sprecherbeiträgen -->
<!-- Annotationen und Deskriptionen werden ausgeblendet -->
<!-- Äußerungen werden je nach Typ verschieden farblich hinterlegt -->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<!-- match any element in the document -->
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<!-- ... with one table... -->
				<table>
					<!-- ... and process the speaker contributions -->
					<xsl:apply-templates select="list-transcription/list-body/speaker-contribution"/>
				</table>
			</body>
		</html>
	</xsl:template>

	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution">
		<xsl:variable name="text">
			<xsl:value-of select="translate(./main/ts[position()=last()], ' ', '')"></xsl:value-of>
		</xsl:variable>
		<xsl:message><xsl:value-of select="$text"></xsl:value-of></xsl:message>
		<xsl:variable name="lastChar">
			<xsl:value-of select="substring($text, string-length($text))"></xsl:value-of>
		</xsl:variable>
		<xsl:message><xsl:value-of select="$lastChar"></xsl:value-of></xsl:message>
		<!-- determine the color according to the utterance end symbol -->
		<xsl:variable name="color">
			<xsl:choose>
				<!-- yellow for assertions -->
				<xsl:when test="$lastChar='.'">yellow</xsl:when>
				<!-- red for exclamations -->
				<xsl:when test="$lastChar='!'">red</xsl:when>
				<!-- lime for questions -->
				<xsl:when test="$lastChar='?'">lime</xsl:when>
				<!-- aqua for interrupted utterances -->
				<xsl:when test="$lastChar='&#x2026;'">aqua</xsl:when>
				<!-- white for the rest -->
				<xsl:otherwise>white</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<span>
			<xsl:value-of select="position()"></xsl:value-of>
		</span>
		<span>			
			<xsl:text><![CDATA[       ]]></xsl:text>
		</span>
		<b>
			<xsl:variable name="id">
				<xsl:value-of select="@speaker"/>
			</xsl:variable>
			<xsl:value-of select="//speaker[@id = $id]/abbreviation"/>
			<xsl:text>: </xsl:text>					
		</b>
		<xsl:element name="span">
			<xsl:attribute name="style">
				<xsl:text>background-color:</xsl:text>
				<xsl:value-of select="$color"></xsl:value-of>
			</xsl:attribute>
			<xsl:apply-templates select="./main"></xsl:apply-templates>
		</xsl:element>
		<br></br>
	</xsl:template>

	<!-- for the mains... -->
	<xsl:template match="main">
		<xsl:apply-templates/>
	</xsl:template>

</xsl:stylesheet>
