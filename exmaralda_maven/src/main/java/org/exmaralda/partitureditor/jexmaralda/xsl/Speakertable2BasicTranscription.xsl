<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name/>
					<transcription-name/>
					<referenced-file url=""/>
					<ud-meta-information/>
					<comment/>
					<transcription-convention/>
				</meta-information>
				<xsl:call-template name="EchoSpeakertable"/>
			</head>
			<xsl:call-template name="MakeTiers"></xsl:call-template>
		</basic-transcription>
	</xsl:template>
	
	<xsl:template name="EchoSpeakertable">
		<xsl:copy-of select="node()">
		</xsl:copy-of>
	</xsl:template>
	
	<xsl:template name="MakeTiers">
		<body>
			<common-timeline>
				<tli id="TLI0"/>
				<tli id="TLI1"/>
			</common-timeline>
			<xsl:for-each select="/speakertable/speaker">
				<xsl:element name="tier">
					<xsl:attribute name="id"><xsl:text>TIE_</xsl:text><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="speaker"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="type">t</xsl:attribute>
					<xsl:attribute name="category">v</xsl:attribute>
				</xsl:element>
			</xsl:for-each>
		</body>
	</xsl:template>

</xsl:stylesheet>
