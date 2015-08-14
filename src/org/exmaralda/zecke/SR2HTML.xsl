<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
				<title>Search Result</title>
				<style>
					td.NUMBER{
		 				font-family: "Times New Roman";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: bold;
						color: black;
						background-color: white;
						text-align: Left;}				
					td.TRANS{
		 				font-family: "Times New Roman";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: normal;
						color: black;
						background-color: yellow;
						text-align: Left;}				
					td.SPEAKER{
		 				font-family: "Times New Roman";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: bold;
						color: blue;
						background-color: white;
						text-align: Left;}				
					td.LEFT{
		 				font-family: "Arial Unicode MS";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: normal;
						color: black;
						background-color: white;
						text-align: Right;}				
					td.MATCH{
		 				font-family: "Arial Unicode MS";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: normal;
						color: red;
						background-color: white;
						text-align: Center;}				
					td.RIGHT{
		 				font-family: "Arial Unicode MS";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: normal;
						color: black;
						background-color: white;
						text-align: Left;}			
					td.ANNOTATION{
		 				font-family: "Arial Unicode MS";
  					 	font-size: 10pt;
						font-style: normal;
						font-weight: normal;
						color: blue;
						background-color: white;
						text-align: Left;}			
					}	
				</style>
			</head>
			<body>
				<table>
					<xsl:apply-templates select="/result/match"/>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="/result/match">
		<tr>
			<td class="NUMBER">
				<xsl:value-of select="position()"/>
			</td>
			<xsl:element name="td">
				<xsl:attribute name="class">TRANS</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="./transcription-path"/></xsl:attribute>
				<xsl:value-of select="./transcription-name"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">SPEAKER</xsl:attribute>
				<xsl:attribute name="title"><xsl:value-of select="./speakerID"/></xsl:attribute>
				<xsl:value-of select="./speaker-abbreviation"/>
			</xsl:element>
			<xsl:variable name="matchStart">
				<xsl:value-of select="./start"/>
			</xsl:variable>
			<xsl:variable name="matchLength">
				<xsl:value-of select="./length"/>
			</xsl:variable>
			<xsl:variable name="annotationMatchStart">
				<xsl:value-of select="./annotation-match-start"/>
			</xsl:variable>
			<xsl:variable name="annotationMatchLength">
				<xsl:value-of select="./annotation-match-length"/>
			</xsl:variable>
			<xsl:variable name="text">
				<xsl:apply-templates select="./segment"/>
			</xsl:variable>
			<xsl:variable name="annotationText">
				<xsl:apply-templates select="./annotation"/>
			</xsl:variable>
			<td class="LEFT">
				<xsl:variable name="leftContext">
					<xsl:value-of select="substring($text, 1, $matchStart)"/>
				</xsl:variable>
				<xsl:value-of select="substring($leftContext, string-length($leftContext)-50)"/>
			</td>
			<td class="MATCH">
				<xsl:value-of select="substring($text, $matchStart+1, $matchLength)"/>
			</td>
			<td class="RIGHT">
				<xsl:variable name="rightContext">
					<xsl:value-of select="substring($text, $matchStart + $matchLength +1)"/>
				</xsl:variable>
				<xsl:value-of select="substring($rightContext, 1, 50)"/>
			</td>
			<xsl:if test="./category">
				<td class="NUMBER">
					<xsl:value-of select="./category"/>
				</td>
			</xsl:if>
			<xsl:if test="./annotation">
				<td class="LEFT">
					<xsl:variable name="leftAnnotationContext">
						<xsl:value-of select="substring($annotationText, 1, $annotationMatchStart)"/>
					</xsl:variable>
					<xsl:value-of select="substring($leftAnnotationContext, string-length($leftAnnotationContext)-50)"/>
				</td>
				<td class="ANNOTATION">
					<xsl:value-of select="substring($annotationText, $annotationMatchStart+1, $annotationMatchLength)"/>
				</td>
				<td class="RIGHT">
					<xsl:variable name="rightAnnotationContext">
						<xsl:value-of select="substring($annotationText, $annotationMatchStart + $annotationMatchLength +1)"/>
					</xsl:variable>
					<xsl:value-of select="substring($rightAnnotationContext, 1, 50)"/>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>
	<xsl:template match="/result/match/segment">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
