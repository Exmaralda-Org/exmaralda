<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output method="xml" indent="no"/>
	
	<xsl:template match="/ | @* | node()">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
	</xsl:template>
	
	<xsl:template match="ts[@n='sc']">
		<xsl:element name="u">
			<xsl:attribute name="who"><xsl:value-of select="@speaker"/></xsl:attribute>
			<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
			<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="ts[@n='HIAT:u']">
		<xsl:element name="seg">
			<xsl:attribute name="type">utterance</xsl:attribute>
			<xsl:attribute name="mode">
				<xsl:variable name="utteranceEndSymbol">
					<xsl:value-of select="child::nts[not(text()=' ')][last()]"/>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$utteranceEndSymbol='?'">interrogative</xsl:when>
					<xsl:when test="$utteranceEndSymbol='!'">exclamative</xsl:when>
					<xsl:when test="$utteranceEndSymbol='.'">declarative</xsl:when>
					<xsl:when test="$utteranceEndSymbol='&#x2026;'">interrupted</xsl:when>
					<xsl:when test="$utteranceEndSymbol='&#x02D9;'">modeless</xsl:when>
					<xsl:otherwise>not_classified</xsl:otherwise>
				</xsl:choose>
				
			</xsl:attribute>
			<xsl:apply-templates></xsl:apply-templates>
		</xsl:element>
	</xsl:template>

	<xsl:template match="ts[@n='HIAT:w']">
		<xsl:element name="w">
			<xsl:apply-templates></xsl:apply-templates>
		</xsl:element>
	</xsl:template>
		
	<xsl:template match="nts">
		<xsl:choose>
			<xsl:when test="string-length(translate(text(),'&#x0020;&#x2026;&#x02D9;.?!',''))=0">
				<!-- do nothing -->
			</xsl:when>
			<xsl:when test="name(preceding-sibling::*[1])='nts' and preceding-sibling::*[1]/text()='(' and text()='('">
				<!-- do nothing -->				
			</xsl:when>
			<xsl:when test="name(following-sibling::*[1])='nts' and following-sibling::*[1]/text()='(' and text()='('">
				<!-- do nothing -->				
			</xsl:when>
			<xsl:when test="name(preceding-sibling::*[1])='nts' and preceding-sibling::*[1]/text()=')' and text()=')'">
				<!-- do nothing -->				
			</xsl:when>
			<xsl:when test="name(following-sibling::*[1])='nts' and following-sibling::*[1]/text()=')' and text()=')'">
				<!-- do nothing -->				
			</xsl:when>
			<xsl:when test="text()='('">
				<xsl:element name="uncertain-start"/>
			</xsl:when>
			<xsl:when test="text()=')'">
				<xsl:element name="uncertain-end"/>
			</xsl:when>
			<xsl:when test="text()='/'">
				<xsl:element name="repair"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="c">
					<xsl:apply-templates/>					
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="ats">
		<xsl:choose>
			<xsl:when test="string-length(translate(text()[1],'0123456789,s',''))=0">
				<xsl:element name="pause">
					<xsl:attribute name="dur">
						<xsl:value-of select="text()"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:when test="text()='&#x2022;'">
				<xsl:element name="pause">
					<xsl:attribute name="dur">short</xsl:attribute>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="event">
					<xsl:attribute name="desc">
						<xsl:value-of select="text()"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>
		
</xsl:stylesheet>
