<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- the root element -->
		<xsl:element name="ANNOTATION_DOCUMENT">
			<xsl:attribute name="DATE"></xsl:attribute>
			<xsl:attribute name="AUTHOR"></xsl:attribute>
			<xsl:attribute name="VERSION">2.0</xsl:attribute>
			<xsl:attribute name="FORMAT">2.0</xsl:attribute>
			<!-- the header element -->
			<xsl:element name="HEADER">
				<xsl:attribute name="MEDIA_FILE">
					<xsl:value-of select="//meta-information/ud-meta-information/ud-information[@attribute-name='ELAN-Media-File']"/>
				</xsl:attribute>
				<xsl:attribute name="TIME_UNITS">milliseconds</xsl:attribute>
			</xsl:element>
			<!-- call a template to generate the time_order element -->
			<xsl:call-template name="makeTimeOrder"/>
			<!-- call a template to generate the tier elements -->
			<xsl:call-template name="makeTiers"/>
		</xsl:element>
	</xsl:template>

	<!-- generate the time_order -->
	<xsl:template name="makeTimeOrder">
		<xsl:element name="TIME_ORDER">
				<xsl:for-each select="//common-timeline/tli">
					<xsl:element name="TIME_SLOT">
						<xsl:attribute name="TIME_SLOT_ID"><xsl:value-of select="@id"/></xsl:attribute>
						<xsl:attribute name="TIME_VALUE"><xsl:value-of select="round(@time*1000)"/></xsl:attribute>
					</xsl:element>
				</xsl:for-each>
		</xsl:element>
	</xsl:template>
	
	<!-- generate tiers -->
	<xsl:template name="makeTiers">
		<xsl:for-each select="//tier">
			<xsl:element name="TIER">
				<xsl:attribute name="TIER_ID"><xsl:value-of select="@id"/></xsl:attribute>
				<xsl:attribute name="PARTICIPANT"><xsl:value-of select="@speaker"/></xsl:attribute>
				<xsl:attribute name="LINGUISTIC_TYPE_REF"><xsl:value-of select="@category"/></xsl:attribute>
				<xsl:attribute name="DEFAULT_LOCALE">en</xsl:attribute>
				<!-- if the tier is an annotation tier, assign it to its parent tier -->
				<xsl:if test="@type='a'">
					<xsl:attribute name="PARENT_REF">
						<xsl:value-of select="//tier[@speaker=current()/@speaker and @type='t']/@id"></xsl:value-of>
					</xsl:attribute>
				</xsl:if>
				<xsl:for-each select="event">
					<xsl:element name="ANNOTATION">
						<xsl:element name="ALIGNABLE_ANNOTATION">
							<xsl:attribute name="ANNOTATION_ID">
								<!-- generate a unique ID from the tier ID and the position of the event -->
								<xsl:value-of select="../@id"/>
								<xsl:text>_</xsl:text>
								<xsl:value-of select="position()"/>
							</xsl:attribute>
							<xsl:attribute name="TIME_SLOT_REF1"><xsl:value-of select="@start"/></xsl:attribute>
							<xsl:attribute name="TIME_SLOT_REF2"><xsl:value-of select="@end"/></xsl:attribute>
							<xsl:element name="ANNOTATION_VALUE">
								<!-- copy the PCDATA -->
								<xsl:apply-templates/>
							</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>			
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
