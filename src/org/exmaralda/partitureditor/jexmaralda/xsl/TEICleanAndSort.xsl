<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/ | @* | node()">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="text">
		<xsl:copy>
			<xsl:apply-templates select="timeline"/>
			<xsl:for-each select="u | event | pause">
				<xsl:sort select="count(//when[@id=current()/@start]/preceding-sibling::*)" data-type="number"/>
				<xsl:sort select="count(//when[@id=current()/@end]/following-sibling::*)" data-type="number"/>
				<xsl:sort select="count(//person[@id=current()/@who]/preceding-sibling::*)" data-type="number"/>
				<xsl:copy><xsl:apply-templates  select="@* | node()"/></xsl:copy>
			</xsl:for-each>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="//seg[@type='utterance' and name(child::*[last()])='anchor']">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
		<xsl:copy-of select="child::*[last()]"/>
	</xsl:template>
	
	<xsl:template match="//seg[@type='utterance']/anchor[last()]">
		<!-- do nothing -->
	</xsl:template>
	
	
	
</xsl:stylesheet>
