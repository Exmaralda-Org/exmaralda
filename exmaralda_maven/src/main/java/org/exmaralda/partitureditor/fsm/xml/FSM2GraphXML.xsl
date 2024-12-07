<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<GraphXML>
			<graph>
				<xsl:apply-templates select="//transitions"></xsl:apply-templates>
			</graph>
		</GraphXML>
	</xsl:template>
	<xsl:template match="transitions">
		<xsl:element name="node">
			<xsl:attribute name="name">
				<xsl:value-of select="@source"></xsl:value-of>
			</xsl:attribute>
		</xsl:element>
		<xsl:for-each select="transition/target">
			<xsl:element name="edge">
				<xsl:attribute name="source">
					<xsl:value-of select="../../@source"></xsl:value-of>
				</xsl:attribute>
				<xsl:attribute name="target">
					<xsl:value-of select="@id"></xsl:value-of>
				</xsl:attribute>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
</xsl:stylesheet>
