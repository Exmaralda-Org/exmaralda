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
	
	<!-- New 09-05-201 -->
	<!-- An implicit pause inside a turn -->
	<xsl:template match="//Turn/Sync[string-length(normalize-space(following-sibling::text()[1]))=0 and following-sibling::*[1][self::Sync]]">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		<xsl:text>(</xsl:text>
		<xsl:value-of select="format-number(following-sibling::Sync[1]/@time - @time, '#0.00')"/>
		<xsl:text>) </xsl:text>
	</xsl:template>
	
</xsl:stylesheet>
