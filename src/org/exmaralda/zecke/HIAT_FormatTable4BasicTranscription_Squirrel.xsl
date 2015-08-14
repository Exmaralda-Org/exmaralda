<?xml version="1.0" encoding="UTF-8"?>
<!-- Generiert eine Formatierungstabelle für eine Basic-Transcription -->
<!-- Spuren, die Kategorien aus den HIAT-Konventionen zugeordnet sind -->
<!-- (also: v / akz / k / nv / sup / pho / nn) -->
<!-- erhalten eine konventionsgemäße Formatierung -->
<!-- die übrigen Spuren erhalten eine Default-Formatierung -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<tierformat-table>
			<!-- Format für Zeitangaben in der Zeitachse -->
			<timeline-item-format show-every-nth-numbering="1" show-every-nth-absolute="5" absolute-time-format="decimal" miliseconds-digits="1"/>
			<!-- Format für leere Ereignisse im Editor (weiß hinterlegt) -->
			<tier-format tierref="EMPTY-EDITOR">
				<property name="font-face">Plain</property>
				<property name="font-color">white</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">white</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">2</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<!-- Format für leere Ereignisse in der Ausgabe (weiß hinterlegt) -->
			<tier-format tierref="EMPTY">
				<property name="font-face">Plain</property>
				<property name="font-color">white</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">white</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">2</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<!-- Format für Spur-Label -->
			<tier-format tierref="ROW-LABEL">
				<property name="font-face">Bold</property>
				<property name="font-color">black</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">yellow</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">14</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<!-- Format für Zeitachsenlabel -->
			<tier-format tierref="COLUMN-LABEL">
				<property name="font-face">Plain</property>
				<property name="font-color">black</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">white</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">10</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<xsl:apply-templates select="//tier"/>
		</tierformat-table>
	</xsl:template>
	<xsl:template match="tier">
		<xsl:choose>
			<!-- Format für verbale Spuren -->
			<xsl:when test="@category='v'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Arial Unicode MS</property>
					<property name="font-face">Plain</property>
					<property name="font-size">14</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für Spuren für besondere Betonung -->
			<xsl:when test="@category='akz'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Plain</property>
					<property name="font-size">1</property>
					<property name="font-color">gray</property>
					<property name="bg-color">black</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
					<property name="row-height-calculation">Fixed</property>
					<property name="fixed-row-width">1</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für Kommentarspuren -->
			<xsl:when test="@category='k'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Italic</property>
					<property name="font-size">10</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für Spuren für Sprechgeschwindigkeit, Lautstärke und Sprechweise -->
			<xsl:when test="@category='sup'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Italic</property>
					<property name="font-size">6</property>
					<property name="font-color">black</property>
					<property name="bg-color">lightGray</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
					<property name="row-height-calculation">Miserly</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für non-verbale Spuren -->
			<xsl:when test="@category='nv'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Italic</property>
					<property name="font-size">10</property>
					<property name="font-color">black</property>
					<property name="bg-color">lightGray</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
					<property name="row-height-calculation">Generous</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für Spuren für phonetische Transkription -->
			<xsl:when test="@category='pho'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Arial Unicode MS</property>
					<property name="font-face">Plain</property>
					<property name="font-size">10</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
					<property name="row-height-calculation">Generous</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für Spuren für akustische Phänomene ohne Autorenschaft -->
			<xsl:when test="@category='nn'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Plain</property>
					<property name="font-size">10</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
					<property name="row-height-calculation">Generous</property>
				</xsl:element>
			</xsl:when>
			<!-- Format für übrige Spuren -->
			<xsl:otherwise>
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"/></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Plain</property>
					<property name="font-size">10</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
