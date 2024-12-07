<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<xsl:element name="transcription">
			<xsl:for-each
				select="//segmented-tier[@type='t']/segmentation[@name='SpeakerContribution_Utterance_Word']">
				<xsl:element name="speaker">
					<xsl:attribute name="abbreviation">
						<xsl:value-of select="//speaker[@id=current()/../@speaker]/abbreviation"/>
					</xsl:attribute>
					<xsl:attribute name="id">
						<xsl:value-of select="../@speaker"/>
					</xsl:attribute>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Speaker Contributions</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(ts[@n='sc'])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Utterances</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//ts[@n='HIAT:u'])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Words</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//ts[@n='HIAT:w'])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Non-Phonological</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//ats[@n='HIAT:non-pho'])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Punctuation</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//nts[@n='HIAT:ip'])"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
