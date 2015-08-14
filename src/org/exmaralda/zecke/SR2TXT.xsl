<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="text" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:apply-templates select="/result/match"/>
	</xsl:template>
	<xsl:template match="/result/match">
		<xsl:value-of select="position()"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:value-of select="./transcription-path"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:value-of select="./transcription-name"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:value-of select="./speakerID"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:value-of select="./speaker-abbreviation"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:variable name="matchStart">
			<xsl:value-of select="./start"/>
		</xsl:variable>
		<xsl:variable name="matchLength">
			<xsl:value-of select="./length"/>
		</xsl:variable>
		<xsl:variable name="text">
			<xsl:apply-templates select="./segment"/>
		</xsl:variable>
		<xsl:variable name="annotationMatchStart">
			<xsl:value-of select="./annotation-match-start"/>
		</xsl:variable>
		<xsl:variable name="annotationMatchLength">
			<xsl:value-of select="./annotation-match-length"/>
		</xsl:variable>
		<xsl:variable name="annotationText">
			<xsl:apply-templates select="./annotation"/>
		</xsl:variable>
		<xsl:variable name="leftContext">
			<xsl:value-of select="substring($text, 1, $matchStart)"/>
		</xsl:variable>
		<xsl:value-of select="substring($leftContext, string-length($leftContext)-50)"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:value-of select="substring($text, $matchStart+1, $matchLength)"/>
		<xsl:text>&#0009;</xsl:text>
		<xsl:variable name="rightContext">
			<xsl:value-of select="substring($text, $matchStart + $matchLength +1)"/>
		</xsl:variable>
		<xsl:value-of select="substring($rightContext, 1, 50)"/>
		<xsl:if test="./category">
			<xsl:text>&#0009;</xsl:text>
			<xsl:value-of select="./category"/>
		</xsl:if>
		<xsl:if test="./annotation">
			<xsl:variable name="leftAnnotationContext">
				<xsl:value-of select="substring($annotationText, 1, $annotationMatchStart)"/>
			</xsl:variable>
			<xsl:text>&#0009;</xsl:text>
			<xsl:value-of select="substring($leftAnnotationContext, string-length($leftAnnotationContext)-50)"/>
			<xsl:text>&#0009;</xsl:text>
			<xsl:value-of select="substring($annotationText, $annotationMatchStart+1, $annotationMatchLength)"/>
			<xsl:variable name="rightAnnotationContext">
				<xsl:value-of select="substring($annotationText, $annotationMatchStart + $annotationMatchLength +1)"/>
			</xsl:variable>
			<xsl:text>&#0009;</xsl:text>
			<xsl:value-of select="substring($rightAnnotationContext, 1, 50)"/>
		</xsl:if>
		<xsl:text>&#x000D;</xsl:text>
	</xsl:template>
	<xsl:template match="/result/match/segment">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="/result/match/annotation">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
