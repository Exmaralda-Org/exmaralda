<?xml version="1.0" encoding="UTF-8"?>
<!-- edited with XML Spy v3.5 NT (http://www.xmlspy.com) by Thomas Schmidt (Universität Hamburg) -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name></project-name>
					<transcription-name></transcription-name>
					<referenced-file url=""></referenced-file>
					<ud-meta-information></ud-meta-information>
					<comment></comment>
					<transcription-convention></transcription-convention>
				</meta-information>
				<speakertable>
					<xsl:apply-templates select="CHAT/Participants"/>
				</speakertable>
			</head>
			<basic-body>
				<common-timeline>
					<xsl:call-template name="generateTimeline"></xsl:call-template>
				</common-timeline>
				<xsl:call-template name="generateVerbalTiers"></xsl:call-template>
				<xsl:call-template name="generatePhoneticTiers"></xsl:call-template>
				<xsl:call-template name="generateSituationTiers"></xsl:call-template>
			</basic-body>
		</basic-transcription>
	</xsl:template>

	<xsl:template match="CHAT/Participants">
		<xsl:apply-templates select="participant"/>
	</xsl:template>
	<xsl:template match="participant">
		<xsl:element name="speaker">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:element name="abbreviation">
				<xsl:value-of select="@id"/>
			</xsl:element>
			<xsl:element name="sex">
					<xsl:attribute name="value"><xsl:value-of select="substring(@sex,1,1)"/></xsl:attribute>
			</xsl:element>
			<languages-used></languages-used>
			<l1></l1> 
			<l2></l2>
			<xsl:element name="ud-speaker-information">
				<xsl:element name="ud-information">
					<xsl:attribute name="attribute-name">name</xsl:attribute>
					<xsl:value-of select="@name"></xsl:value-of>
				</xsl:element>
				<xsl:element name="ud-information">
					<xsl:attribute name="attribute-name">role</xsl:attribute>
					<xsl:value-of select="@role"></xsl:value-of>
				</xsl:element>
				<xsl:element name="ud-information">
					<xsl:attribute name="attribute-name">age</xsl:attribute>
					<xsl:value-of select="@age"></xsl:value-of>
				</xsl:element>
			</xsl:element>
			<comment></comment>
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="generateTimeline">
		<xsl:element name="tli">
			<xsl:attribute name="id">
				<xsl:text>T0</xsl:text>
			</xsl:attribute>
		</xsl:element>
		<xsl:for-each select="//u">
			<xsl:element name="tli">
				<xsl:attribute name="id">
					<xsl:text>T</xsl:text>
					<xsl:value-of select="position()"></xsl:value-of>
				</xsl:attribute>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="generateVerbalTiers">
		<xsl:for-each select="//participant">
			<xsl:element name="tier">
				<xsl:variable name="id">
					<xsl:value-of select="@id"/>
				</xsl:variable>
				<xsl:attribute name="id">
					<xsl:text>TIE_</xsl:text>
					<xsl:value-of select="$id"></xsl:value-of>
					<xsl:text>_V</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="speaker">
					<xsl:value-of select="$id"></xsl:value-of>
				</xsl:attribute>
				<xsl:attribute name="category">
					<xsl:text>v</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>t</xsl:text>
				</xsl:attribute>
				<xsl:for-each select="/CHAT/u">
					<xsl:if test="@who=$id">
						<xsl:element name="event">
							<xsl:attribute name="start"><xsl:text>T</xsl:text><xsl:value-of select="position()-1"></xsl:value-of></xsl:attribute>
							<xsl:attribute name="end"><xsl:text>T</xsl:text><xsl:value-of select="position()"></xsl:value-of></xsl:attribute>
							<xsl:value-of select="./text"></xsl:value-of>
						</xsl:element>
					</xsl:if>
				</xsl:for-each>
			</xsl:element>		
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template name="generatePhoneticTiers">
		<xsl:for-each select="//participant">
			<xsl:element name="tier">
				<xsl:variable name="id">
					<xsl:value-of select="@id"/>
				</xsl:variable>
				<xsl:attribute name="id">
					<xsl:text>TIE_</xsl:text>
					<xsl:value-of select="$id"></xsl:value-of>
					<xsl:text>_PHO</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="speaker">
					<xsl:value-of select="$id"></xsl:value-of>
				</xsl:attribute>
				<xsl:attribute name="category">
					<xsl:text>pho</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>a</xsl:text>
				</xsl:attribute>
				<xsl:for-each select="/CHAT/u">
					<xsl:if test="@who=$id">
						<xsl:if test="./annotation[@type='phonetic']">
							<xsl:element name="event">
								<xsl:attribute name="start"><xsl:text>T</xsl:text><xsl:value-of select="position()-1"></xsl:value-of></xsl:attribute>
								<xsl:attribute name="end"><xsl:text>T</xsl:text><xsl:value-of select="position()"></xsl:value-of></xsl:attribute>
								<xsl:value-of select="./annotation[@type='phonetic']">
								</xsl:value-of>
							</xsl:element>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:element>		
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="generateSituationTiers">
		<xsl:for-each select="//participant">
			<xsl:element name="tier">
				<xsl:variable name="id">
					<xsl:value-of select="@id"/>
				</xsl:variable>
				<xsl:attribute name="id">
					<xsl:text>TIE_</xsl:text>
					<xsl:value-of select="$id"></xsl:value-of>
					<xsl:text>_SIT</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="speaker">
					<xsl:value-of select="$id"></xsl:value-of>
				</xsl:attribute>
				<xsl:attribute name="category">
					<xsl:text>sit</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="type">
					<xsl:text>a</xsl:text>
				</xsl:attribute>
				<xsl:for-each select="/CHAT/u">
					<xsl:if test="@who=$id">
						<xsl:if test="./annotation[@type='situation']">
							<xsl:element name="event">
								<xsl:attribute name="start"><xsl:text>T</xsl:text><xsl:value-of select="position()-1"></xsl:value-of></xsl:attribute>
								<xsl:attribute name="end"><xsl:text>T</xsl:text><xsl:value-of select="position()"></xsl:value-of></xsl:attribute>
								<xsl:value-of select="./annotation[@type='situation']">
								</xsl:value-of>
							</xsl:element>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>
			</xsl:element>		
		</xsl:for-each>
	</xsl:template>
	
	
</xsl:stylesheet>
