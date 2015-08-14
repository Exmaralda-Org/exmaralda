<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<!-- top level element / main structure -->
	<xsl:template match="/">
		<xsl:element name="TEI.2">
			<xsl:element name="teiHeader">
				<xsl:element name="fileDesc">
					<titleStmt></titleStmt>
					<sourceDesc>
						<xsl:value-of select="//meta-information/comment"/>
					</sourceDesc>
				</xsl:element>
				<xsl:element name="profileDesc">
					<xsl:element name="particDesc">
						<xsl:apply-templates select="//speakertable/speaker"/>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="text">
				<xsl:element name="timeline">
					<xsl:apply-templates select="//common-timeline/tli"/>
				</xsl:element>
				<!-- <xsl:apply-templates select="/list-transcription/list-body/speaker-contribution"/> -->
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- map tli elements onto when elements -->
	<xsl:template match="tli">
		<xsl:element name="when">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:if test="@time">
				<xsl:attribute name="absolute"><xsl:value-of select="@time"/></xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- map speaker elements onto person elements -->
	<xsl:template match="speaker">
		<xsl:element name="person">
			<xsl:attribute name="id"><xsl:value-of select="abbreviation/text()"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
