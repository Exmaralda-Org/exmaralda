<?xml version="1.0" encoding="UTF-8"?>
<!-- 22. Oktober 2003, Thomas Schmidt -->
<!-- Stylesheet, das aus einer Sprechertabelle eine Basic-Transcription generiert, -->
<!-- die für jeden Sprecher folgende konventionsgemäß typisierten und kategorierten Spuren enthält: --> 
<!-- Spur für Sprechgeschwindigkeit, Lautstärke und Sprechweise -->
<!-- Verbale Spur -->
<!-- Spur für besondere Betonung -->
<!-- Kommentarspur -->
<!-- (Kommentarspuren werden am Ende eingeordnet) -->
<!-- Und: eine Spur für akustische Phänomene ohne Autorenschaft -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- Basic Transcription mit leerer Meta-Information -->
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
				<!-- Sprechertabelle kopieren -->
				<xsl:call-template name="EchoSpeakertable"/>
			</head>
			<!-- Spuren erzeugen -->
			<xsl:call-template name="MakeTiers"></xsl:call-template>
		</basic-transcription>
	</xsl:template>
	
	<!-- Sprechertabelle kopieren -->
	<xsl:template name="EchoSpeakertable">
		<xsl:copy-of select="node()">
		</xsl:copy-of>
	</xsl:template>
	
	<!-- Spuren erzeugen -->
	<xsl:template name="MakeTiers">
		<body>
			<!-- Zeitachse mit zwei Zeitpunkten -->
			<common-timeline>
				<tli id="TLI0"/>
				<tli id="TLI1"/>
			</common-timeline>
			<!-- für jeden Sprecher in der Sprechertabelle -->
			<xsl:for-each select="/speakertable/speaker">
				<!-- Spur für Sprechgeschwindigkeit, Lautstärke und Sprechweise -->
				<xsl:element name="tier">
					<!-- Generieren einer eindeutigen ID ("TIE_SUP_" gefolgt von der Sprecher-ID, die per Definition eindeutig ist) -->
					<xsl:attribute name="id"><xsl:text>TIE_SUP_</xsl:text><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<!-- Zuordnen des Sprechers -->
					<xsl:attribute name="speaker"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<!-- Spur erhalten den Typ 'a' -->
					<xsl:attribute name="type">a</xsl:attribute>
					<!-- und die Kategorie 'sup' -->
					<xsl:attribute name="category">sup</xsl:attribute>
					<!-- Anzeigenamen bleibt leer -->
					<xsl:attribute name="display-name">
						<xsl:text></xsl:text>
					</xsl:attribute>
				</xsl:element>
				<!-- Verbale Spur -->
				<xsl:element name="tier">
					<xsl:attribute name="id"><xsl:text>TIE_V_</xsl:text><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="speaker"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="type">t</xsl:attribute>
					<xsl:attribute name="category">v</xsl:attribute>
					<xsl:attribute name="display-name">
						<xsl:value-of select="abbreviation"/>
					</xsl:attribute>
				</xsl:element>
				<!-- Spur für besondere Betonung -->
				<xsl:element name="tier">
					<xsl:attribute name="id"><xsl:text>TIE_AKZ_</xsl:text><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="speaker"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="type">a</xsl:attribute>
					<xsl:attribute name="category">akz</xsl:attribute>
					<xsl:attribute name="display-name">
						<xsl:text></xsl:text>
					</xsl:attribute>
				</xsl:element>
			</xsl:for-each>
			
			<!-- Spur für akustische Phänomene ohne Autorenschaft -->
			<xsl:element name="tier">
				<xsl:attribute name="id"><xsl:text>TIE_NN</xsl:text></xsl:attribute>
				<!-- keine Sprecherzuordnung! -->
				<!-- geändert am 26.11.2003: Typ D statt T -->
				<xsl:attribute name="type">d</xsl:attribute>
				<xsl:attribute name="category">nn</xsl:attribute>
				<xsl:attribute name="display-name">
					<xsl:text> [nn]</xsl:text>
				</xsl:attribute>
			</xsl:element>
			
			<xsl:for-each select="/speakertable/speaker">
				<!-- Für jeden Sprecher eine Kommentarspur -->
				<xsl:element name="tier">
					<xsl:attribute name="id"><xsl:text>TIE_K_</xsl:text><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="speaker"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="type">a</xsl:attribute>
					<xsl:attribute name="category">k</xsl:attribute>
					<xsl:attribute name="display-name">
						<xsl:value-of select="abbreviation"/>
						<xsl:text> [k]</xsl:text>
					</xsl:attribute>
				</xsl:element>
			</xsl:for-each>
		</body>
	</xsl:template>

</xsl:stylesheet>
