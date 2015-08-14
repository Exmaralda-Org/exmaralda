<?xml version="1.0" encoding="UTF-8"?>
<!-- <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"> -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="xml" indent="yes" encoding="UTF-8"/>
	<xsl:param name="language" select="'de'"/>
	<xsl:variable name="common-timeline">
		<xsl:call-template name="generateTimeLine"/>
	</xsl:variable>
	<xsl:template name="generateTimeLine">
		<xsl:variable name="ctmlin">
			<xsl:element name="common-timeline">
				<xsl:message>starting time line</xsl:message>
				<xsl:for-each select="//event/@start|//event/@end">
					<xsl:sort select="." data-type="number"/>
					<xsl:element name="tli">
						<xsl:attribute name="id">T<xsl:value-of select="position()"/></xsl:attribute>
						<xsl:attribute name="time"><xsl:value-of select="."/></xsl:attribute>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:variable>
		<xsl:element name="common-timeline">
			<xsl:copy-of select="xalan:nodeset($ctmlin)//tli[not(@time=following-sibling::*/@time)]"/>
		</xsl:element>
		<xsl:message>ready  time line</xsl:message>
	</xsl:template>
	<xsl:template match="/">
		<xsl:element name="basic-transcription">
			<xsl:element name="head">
				<xsl:element name="meta-information">
					<xsl:element name="project-name"/>
					<xsl:element name="transcription-name"/>
					<xsl:element name="referenced-file">
						<xsl:attribute name="url"/>
					</xsl:element>
					<xsl:element name="ud-meta-information"/>
					<xsl:element name="comment"/>
					<xsl:element name="transcription-convention"/>
				</xsl:element>
				<xsl:element name="speakertable">
					<xsl:for-each select="//layer/@l-id">
						<xsl:element name="speaker">
							<xsl:attribute name="id"><xsl:value-of select="translate(., ' ','')"/></xsl:attribute>
							<xsl:element name="abbreviation">
								<xsl:value-of select="."/>
							</xsl:element>
							<xsl:element name="sex">
								<xsl:attribute name="value">u</xsl:attribute>
							</xsl:element>
							<xsl:element name="languages-used">
								<xsl:element name="language">
									<xsl:attribute name="lang"><xsl:value-of select="$language"/></xsl:attribute>
								</xsl:element>
							</xsl:element>
							<xsl:element name="l1"/>
							<xsl:element name="l2"/>
							<xsl:element name="ud-speaker-information"/>
							<xsl:element name="comment"/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:element>
			<xsl:apply-templates select="session"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="tasx">
		<xsl:apply-templates select="session">
		</xsl:apply-templates>
	</xsl:template>
	<xsl:template match="session">
		<xsl:element name="basic-body">
			<xsl:copy-of select="xalan:nodeset($common-timeline)"/>
			<xsl:apply-templates select="layer"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="layer">
		<xsl:message>layer</xsl:message>
		<xsl:element name="tier">
			<xsl:attribute name="id">TIE<xsl:value-of select="position()"/></xsl:attribute>
			<xsl:attribute name="speaker"><xsl:value-of select="translate(@l-id, ' ', '')"/></xsl:attribute>
			<xsl:attribute name="category"></xsl:attribute>
			<xsl:attribute name="type">t</xsl:attribute>
			<xsl:apply-templates select="event"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="event">
		<xsl:variable name="st" select="@start"/>
		<xsl:variable name="et" select="@end"/>
		<xsl:variable name="tc">
			<xsl:for-each select="xalan:nodeset($common-timeline)//tli[@time=$st]">
				<xsl:value-of select="@id"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="tc1">
			<xsl:for-each select="xalan:nodeset($common-timeline)//tli[@time=$et]">
				<xsl:value-of select="@id"/>
			</xsl:for-each>
		</xsl:variable>
		<xsl:element name="event">
			<xsl:attribute name="start"><xsl:value-of select="$tc"/></xsl:attribute>
			<xsl:attribute name="end"><xsl:value-of select="$tc1"/></xsl:attribute>
			<xsl:value-of select="./text()"/>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
