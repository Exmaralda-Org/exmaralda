<?xml version="1.0" encoding="UTF-8"?>
<!-- STYLESHEET "BT2EventListHTML", Thomas Schmidt, 14.04.2004 -->
<!-- Stylsheet zur Anwendung auf eine EXMARaLDA Basic-Transcription -->
<!-- (im Partitur-Editor: File-Visualize-Free Stylesheet Visualization) -->
<!-- Generiert eine HTML-Datei mit einer Darstellung der Basic-Transcription als zeitlich geordnete Liste von Ereignissen -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="html" encoding="UTF-8"/>


	<!-- copy the tiers into a variable tiersCopy -->
	<xsl:variable name="tiersCopy">
		<xsl:element name="tiers">
			<xsl:for-each select="//tier">
				<xsl:element name="tier">
					<xsl:copy-of select="@*"/>
					<xsl:copy-of select="node()"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:variable>

	<!-- copy the speakers into a variable speakersCopy -->
	<xsl:variable name="speakersCopy">
		<xsl:element name="speakertable">
			<xsl:for-each select="//speaker">
				<xsl:element name="speaker">
					<xsl:copy-of select="@*"/>
					<xsl:copy-of select="node()"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:variable>

<!-- ************************************************************************************************* -->

	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<!-- one paragraph for each timeline item -->
				<xsl:for-each select="//common-timeline/tli">
					<p>
						<b>
							<xsl:value-of select="position()"></xsl:value-of>
						</b>
						<br/>
						<xsl:variable name="id">
							<xsl:value-of select="@id"/>
						</xsl:variable>
						<!-- events starting at the current timeline item in the order of the tiers -->
						<xsl:for-each select="$tiersCopy//tier">
							<xsl:variable name="speaker">
								<xsl:value-of select="@speaker"/>
							</xsl:variable>
							<xsl:if test="./event[@start=$id]">
								<b>
									<xsl:value-of select="$speakersCopy//speaker[@id=$speaker]/abbreviation/text()"></xsl:value-of>
									<xsl:text> [</xsl:text>
									<xsl:value-of select="@category"></xsl:value-of>
									<xsl:text>] : </xsl:text>
								</b>
							</xsl:if>
							<xsl:value-of select="./event[@start=$id]/text()"/>
							<xsl:if test="./event[@start=$id]">
								<br></br>
							</xsl:if>
						</xsl:for-each>
					</p>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
