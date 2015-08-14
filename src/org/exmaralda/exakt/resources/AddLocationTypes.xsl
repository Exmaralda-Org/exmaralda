<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="no"/>
	
	<xsl:template match="/ | @* | node()">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
	</xsl:template>

	<xsl:template match="/Corpus/CorpusData/Speaker/Location">
		<xsl:copy>
			<xsl:if test="Description/Key[@Name='Typ']">
				<xsl:attribute name="type">
					<xsl:value-of select="Description/Key[@Name='Typ']"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
		
	<xsl:template match="/Corpus/CorpusData/Speaker/Language">
		<xsl:copy>
			<xsl:if test="Description/Key[@Name='Sprachstatus']">
				<xsl:attribute name="type">
					<xsl:value-of select="Description/Key[@Name='Sprachstatus']"/>
				</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>



</xsl:stylesheet>
