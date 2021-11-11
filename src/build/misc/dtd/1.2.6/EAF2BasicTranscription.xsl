<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name/>
					<transcription-name/>
					<xsl:element name="referenced-file">
						<xsl:attribute name="url">
							<xsl:value-of select="/ANNOTATION_DOCUMENT/HEADER/@MEDIA_FILE"/>
						</xsl:attribute>
					</xsl:element>
					<ud-meta-information/>
					<comment/>
					<transcription-convention/>
				</meta-information>
				<xsl:call-template name="generateSpeakertable"/>
			</head>
			<body>
				<common-timeline>
					<xsl:for-each select="//TIME_ORDER/TIME_SLOT">
						<xsl:element name="tli">
							<xsl:attribute name="id">
								<xsl:value-of select="@TIME_SLOT_ID"/>
							</xsl:attribute>
							<xsl:attribute name="time">
								<xsl:value-of select="@TIME_VALUE"></xsl:value-of>
							</xsl:attribute>
							<xsl:attribute name="type">
								<xsl:text>appl</xsl:text>
							</xsl:attribute>
						</xsl:element>
					</xsl:for-each>
				</common-timeline>
				<xsl:for-each select="//TIER">
					<xsl:element name="tier">
						<xsl:attribute name="id">
							<xsl:value-of select="@TIER_ID"/>
						</xsl:attribute>
						<xsl:attribute name="speaker">
							<xsl:text>SPK_</xsl:text>
							<xsl:value-of select="@PARTICIPANT"/>
						</xsl:attribute>
						<xsl:attribute name="category">
							<xsl:value-of select="@LINGUISTIC_TYPE_REF"/>
						</xsl:attribute>
						<xsl:attribute name="type">
							<xsl:choose>
								<xsl:when test="string-length(@PARENT_REF)>0">
									<xsl:text>a</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>t</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="display-name">
							<xsl:value-of select="@TIER_ID"/>
						</xsl:attribute>
						<xsl:apply-templates select="ANNOTATION"/>
					</xsl:element>					
				</xsl:for-each>
			</body>
		</basic-transcription>
	</xsl:template>

	<xsl:template name="generateSpeakertable">
		<xsl:variable name="speakertableVariable">
			<dummy-element>
				<xsl:for-each select="//ANNOTATION_DOCUMENT/TIER">
					<xsl:sort select="@id" data-type="text"/>
						<xsl:element name="speaker">
							<xsl:attribute name="id">
								<xsl:text>SPK_</xsl:text>
								<xsl:value-of select="@PARTICIPANT"></xsl:value-of>
							</xsl:attribute>
							<xsl:element name="abbreviation">
								<xsl:value-of select="@PARTICIPANT"></xsl:value-of>
							</xsl:element>
							<sex value="u"/>
							<languages-used/>
							<l1/>
							<l2/>
							<ud-speaker-information/>
							<comment/>
						</xsl:element>							
				</xsl:for-each>
			</dummy-element>
		</xsl:variable>
		<xsl:element name="speakertable">
				<xsl:copy-of select="xalan:nodeset($speakertableVariable)//speaker[not(@id=following-sibling::*/@id)]"/>
		</xsl:element>
		<xsl:message>Speakertable generated from participant names</xsl:message>
	</xsl:template>
	
	
	<xsl:template match="ANNOTATION">
		<!-- <xsl:apply-templates select="ALIGNABLE_ANNOTATION | REF_ANNOTATION"/> -->
		<xsl:apply-templates select="ALIGNABLE_ANNOTATION"/>
	</xsl:template>
	
	<xsl:template match="ALIGNABLE_ANNOTATION">
		<xsl:element name="event">
			<xsl:attribute name="start">
				<xsl:value-of select="@TIME_SLOT_REF1"/>
			</xsl:attribute>
			<xsl:attribute name="end">
				<xsl:value-of select="@TIME_SLOT_REF2"/>
			</xsl:attribute>
			<xsl:apply-templates select="ANNOTATION_VALUE"/>
		</xsl:element>
	</xsl:template>

<!--	<xsl:template match="REF_ANNOTATION">
		<xsl:element name="event">
			<xsl:attribute name="start">
				<xsl:value-of select="ALIGNABLE_ANNOTATION[@ANNOTATION_ID=@ANNOTATION_REF]/@TIME_SLOT_REF1"/>
			</xsl:attribute>
			<xsl:attribute name="end">
				<xsl:value-of select="ALIGNABLE_ANNOTATION[@ANNOTATION_ID=@ANNOTATION_REF]/@TIME_SLOT_REF2"/>
			</xsl:attribute>
			<xsl:apply-templates select="ANNOTATION_VALUE"/>
		</xsl:element>
	</xsl:template>-->
	
	<xsl:template match="ANNOATION_VALUE">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
