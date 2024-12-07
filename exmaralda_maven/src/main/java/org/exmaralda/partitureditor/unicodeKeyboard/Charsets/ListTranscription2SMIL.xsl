<?xml version="1.0" encoding="UTF-8"?>
<!-- STYLESHEET "HIAT_Plain_UtteranceList_Tbl", Thomas Schmidt, 14.04.2004 -->
<!-- Stylsheet zur Anwendung auf eine EXMARaLDA List-Transcription -->
<!-- (im Partitur-Editor: Segmentation-HIAT Segmentation-Utterance List (HTML)) -->
<!-- Generiert eine HTML-Datei mit einer Darstellung der List-Transcription als zeitlich geordnete Liste von Sprecherbeiträgen -->
<!-- In einer zweiten Spalte werden, falls vorhanden, die deutschen Übersetzungen hinzugefügt (Kategorie 'de) -->
<!-- andere Annotationen und Deskriptionen werden ausgeblendet -->
<!-- Die Ausgabe erfolgt in einer Tabelle -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- match any element in the document -->
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<style>
				  <!-- Bind the IE timing behavior to the needed elements -->
				  t\:*, span { behavior:url(#default#time2); }
				</style>
				<XML:NAMESPACE PREFIX="t"/>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
			<xsl:element name="t:audio">
				<xsl:attribute name="begin">0s</xsl:attribute>
				<xsl:attribute name="src">
					<xsl:value-of select="/list-transcription/head/meta-information/referenced-file/@url"></xsl:value-of>
				</xsl:attribute>
				<xsl:attribute name="syncBehavior">locked</xsl:attribute>
			</xsl:element>
			<p>
				<xsl:apply-templates select="list-transcription/list-body/speaker-contribution/main/ts"/>
			</p>
			</body>
		</html>
	</xsl:template>
	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution/main/ts">
		<xsl:element name="span">
			<xsl:attribute name="begin">
				<xsl:value-of select="/list-transcription/list-body/common-timeline/tli[@id=@s]/@time"></xsl:value-of>
				<xsl:text>s</xsl:text>
			</xsl:attribute>
			<xsl:apply-templates></xsl:apply-templates>
		</xsl:element>
		<br/>
	</xsl:template>
	<!-- for the mains... -->
	<xsl:template match="main">
		<!-- ... simply make a table row with one cell containing the text -->
		<xsl:apply-templates/>
	</xsl:template>

</xsl:stylesheet>
