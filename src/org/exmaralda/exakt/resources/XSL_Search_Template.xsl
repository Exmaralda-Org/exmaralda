<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" encoding="UTF-8"/>
	<xsl:template match="/">
		<search-result-list>
			<xsl:apply-templates select="//*[@n='HIAT:w' and text()='Ja']"/>
		</search-result-list>
	</xsl:template>
	<xsl:template match="*">
		<xsl:variable name="LeftContext">
			<xsl:for-each select="../preceding-sibling::*/*">
				<xsl:value-of select="text()"/>				
			</xsl:for-each>
			<xsl:for-each select="preceding-sibling::*">
				<xsl:value-of select="text()"/>
			</xsl:for-each>			
		</xsl:variable>
		<xsl:variable name="RightContext">
			<xsl:for-each select="following-sibling::*">
				<xsl:value-of select="text()"/>
			</xsl:for-each>		
			<xsl:for-each select="../following-sibling::*/*">
				<xsl:value-of select="text()"/>				
			</xsl:for-each>			
		</xsl:variable>		
		<xsl:element name="search-result">
			<xsl:attribute name="selected">true</xsl:attribute>
			<xsl:attribute name="communication"></xsl:attribute>
			<xsl:attribute name="speaker"></xsl:attribute>
			<xsl:element name="locator">
				<xsl:attribute name="file"><!-- assigned by the search algorithm --></xsl:attribute>
				<xsl:attribute name="xpath">
					<!-- specifiy an XPath to the matching component -->
					<xsl:text>//ts[@id='</xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text>']</xsl:text>
				</xsl:attribute>
			</xsl:element>
			<xsl:element name="left-context"><xsl:value-of select="$LeftContext"/></xsl:element>
			<xsl:element name="match">
				<xsl:attribute name="original-match-start"><xsl:value-of select="string-length($LeftContext)"/></xsl:attribute>
				<xsl:value-of select="."/>
			</xsl:element>
			<xsl:element name="right-context"><xsl:value-of select="$RightContext"/></xsl:element>
			<xsl:element name="data">
				<xsl:value-of select="../../../../@id"/>
			</xsl:element>
			<xsl:element name="data">
				<xsl:value-of select="../../../../@speaker"/>
			</xsl:element>
			<xsl:element name="data">
				<xsl:value-of select="../../@s"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
