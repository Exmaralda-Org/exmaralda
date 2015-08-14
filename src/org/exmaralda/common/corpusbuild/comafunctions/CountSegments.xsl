<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<!-- detect the segmentation -->
	<xsl:variable name="SEGMENTATION">
		<xsl:choose>
			<xsl:when test="//ts[@n='cGAT:w']">cGAT</xsl:when>
			<xsl:when test="//ts[@n='GEN:w']">GEN</xsl:when>
			<xsl:otherwise>HIAT</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:variable name="SEGMENTATION_NAME">
		<xsl:choose>
			<xsl:when test="$SEGMENTATION='cGAT'">SpeakerContribution_Word</xsl:when>
			<xsl:when test="$SEGMENTATION='GEN'">SpeakerContribution_Word</xsl:when>
			<xsl:otherwise>SpeakerContribution_Utterance_Word</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>
	<xsl:template match="/">
		<xsl:element name="transcription">
			<xsl:for-each select="//segmented-tier[@type='t']/segmentation[@name=$SEGMENTATION_NAME]">
				<xsl:element name="speaker">
					<xsl:attribute name="abbreviation">
						<xsl:value-of select="//speaker[@id=current()/../@speaker]/abbreviation"/>
					</xsl:attribute>
					<xsl:attribute name="sigle">
						<xsl:value-of select="//speaker[@id=current()/../@speaker]/ud-speaker-information/ud-information[@attribute-name='Sigle']"/>						
					</xsl:attribute>
					<xsl:attribute name="id">
						<xsl:value-of select="../@speaker"/>
					</xsl:attribute>
					<!-- ********** FOR ALL *************** -->
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Speaker Contributions</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(ts[@n='sc'])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Words</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//ts[@n=concat($SEGMENTATION, ':w')])"/>
						</xsl:attribute>
					</xsl:element>
					<xsl:element name="count">
						<xsl:attribute name="segment-name">Non-Phonological</xsl:attribute>
						<xsl:attribute name="count">
							<xsl:value-of select="count(*//ats[@n=concat($SEGMENTATION, ':non-pho')])"/>
						</xsl:attribute>
					</xsl:element>
					

					<!-- ********** FOR HIAT ONLY *************** -->

					<xsl:if test="$SEGMENTATION='HIAT'">
						<xsl:element name="count">
							<xsl:attribute name="segment-name">Utterances</xsl:attribute>
							<xsl:attribute name="count">
								<xsl:value-of select="count(*//ts[@n='HIAT:u'])"/>
							</xsl:attribute>
						</xsl:element>
						<xsl:element name="count">
							<xsl:attribute name="segment-name">Punctuation</xsl:attribute>
							<xsl:attribute name="count">
								<xsl:value-of select="count(*//nts[@n='HIAT:ip'])"/>
							</xsl:attribute>
						</xsl:element>
					</xsl:if>

					<!-- ********** FOR cGAT ONLY *************** -->
					
					<xsl:if test="$SEGMENTATION='cGAT'">
						<xsl:element name="count">
							<xsl:attribute name="segment-name">Pauses</xsl:attribute>
							<xsl:attribute name="count">
								<xsl:value-of select="count(*//ats[@n='cGAT:pause'])"/>
							</xsl:attribute>
						</xsl:element>
						<xsl:element name="count">
							<xsl:attribute name="segment-name">Breathe</xsl:attribute>
							<xsl:attribute name="count">
								<xsl:value-of select="count(*//nts[@n='cGAT:b'])"/>
							</xsl:attribute>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>
</xsl:stylesheet>
