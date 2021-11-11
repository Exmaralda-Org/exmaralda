<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:apply-templates select="head"></xsl:apply-templates>
	</xsl:template>
	<xsl:template match="head">
		<xsl:apply-templates select="meta-information | speakertable"/>
	</xsl:template>
	<xsl:template match="meta-information">
		<xsl:apply-templates select="transcription-name"/>
		<h2>Meta-Information</h2>
		<table cellspacing="10px">
			<xsl:apply-templates select="project-name | referenced-file | comment | ud-meta-information"/>
		</table>
	</xsl:template>
	<xsl:template match="transcription-name">
		<h1>
			<xsl:apply-templates/>
		</h1>
	</xsl:template>
	<xsl:template match="project-name | comment">
		<tr>
			<td>
				<b>
					<xsl:choose>
						<xsl:when test="self::project-name">Projektname</xsl:when>
						<xsl:when test="self::comment">Kommentar</xsl:when>
					</xsl:choose>
				</b>
			</td>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="referenced-file | sex">
		<tr>
			<td>
				<b>
					<xsl:choose>
						<xsl:when test="self::referenced-file">Medien-Datei</xsl:when>
						<xsl:when test="self::sex">Geschlecht</xsl:when>
					</xsl:choose>
				</b>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="self::referenced-file">
						<xsl:value-of select="@url"/>
					</xsl:when>
					<xsl:when test="self::sex">
						<xsl:value-of select="@value"/>
					</xsl:when>
				</xsl:choose>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="ud-information">
		<tr>
			<td>
				<b>
					<xsl:value-of select="@attribute-name"/>
				</b>
			</td>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="speakertable">
		<h2>Sprechertabelle</h2>
		<table>
			<xsl:apply-templates select="speaker"/>
		</table>
	</xsl:template>
	<xsl:template match="speaker">
		<tr>
			<xsl:element name="td">
				<xsl:attribute name="bgcolor">#c0c0c0</xsl:attribute>
				<xsl:element name="a">
					<xsl:attribute name="name"><xsl:value-of select="@id"/></xsl:attribute>
				</xsl:element>
				<big>
					<b>
						<xsl:apply-templates select="abbreviation"/>
					</b>
				</big>
			</xsl:element>
			<td>
				<table cellspacing="7px">
					<xsl:apply-templates select="sex | languages-used | l1 | l2 | ud-speaker-information/ud-information | comment"/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<xsl:template match="languages-used | l1 |  l2">
		<xsl:if test="count(child::*) > 0">
			<tr>
				<td>
					<b>
						<xsl:choose>
							<xsl:when test="self::languages-used">Benutzte Sprachen</xsl:when>
							<xsl:when test="self::l1">L1</xsl:when>
							<xsl:when test="self::l2">L2</xsl:when>
						</xsl:choose>
					</b>
				</td>
				<td>
					<xsl:apply-templates select="language"/>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	<xsl:template match="language">
		<xsl:value-of select="@lang"/>
		<xsl:if test="not(position()=last())">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
