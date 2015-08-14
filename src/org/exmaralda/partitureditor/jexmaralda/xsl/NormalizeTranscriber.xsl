<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:output method="xml" indent="no"/>
	
	<xsl:template match="/ | @* | node()">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="Event">
		<xsl:text>[</xsl:text>
		<xsl:if test="@extent='end'"><xsl:text>...</xsl:text></xsl:if>
		<xsl:value-of select="@desc"/>
		<xsl:if test="@extent='begin'"><xsl:text>...</xsl:text></xsl:if>
		<xsl:text>]</xsl:text>		
	</xsl:template>

	<xsl:template match="Background">
		<xsl:element name="Sync"><xsl:attribute name="time"><xsl:value-of select="@time"/></xsl:attribute></xsl:element>
		<xsl:text>{</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>}</xsl:text>		
	</xsl:template>
	
	<xsl:template match="Comment">
		<xsl:text>(</xsl:text>
		<xsl:value-of select="@desc"/>
		<xsl:text>)</xsl:text>		
	</xsl:template>
	
</xsl:stylesheet>
