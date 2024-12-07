<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" omit-xml-declaration="yes" encoding="UTF-8"/>
	<xsl:template match="/">
		<html>
			<head>			
				<xsl:call-template name="generate-css"/>
			</head>
			<body>
				<table>
					<xsl:for-each select="//common-timeline/tli">
						<xsl:for-each select="//segmented-tier[@type='t']/segmentation[@name='SpeakerContribution_Utterance_Word']/ts/ts[@n='HIAT:u' and @s=current()/@id]">
							<xsl:sort select="count(//tli[@id=current()/@e]/preceding-sibling::tli)" data-type="number" order="descending"/>
							<xsl:apply-templates select="."></xsl:apply-templates>
						</xsl:for-each>
						<!-- <xsl:apply-templates select="//segmented-tier[@type='t']/segmentation[@name='SpeakerContribution_Event']/ts[@s=current()/@id]"/> -->
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="ts[@n='HIAT:u']">
		<tr>
			<td valign="top" nowrap="nowrap">
				<xsl:element name="a">
					<xsl:attribute name="name">
						<xsl:value-of select="@s"/>
					</xsl:attribute>
					<xsl:text> </xsl:text>
				</xsl:element>				
				<b><xsl:value-of select="//speaker[@id=current()/../../../@speaker]/abbreviation"/></b>
			</td>
			<td valign="top"><span><xsl:apply-templates></xsl:apply-templates></span></td>
		</tr>
	</xsl:template>	
	<xsl:template name="generate-css">
		<style type="text/css">
			* {font-family:'Arial Unicode MS',sans-serif,'Arial'; font-size:10pt}
			table {margin-left:20px; margin-right:20px}
			td {padding-right:15px}
		</style>				
	</xsl:template>
</xsl:stylesheet>
