<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<tierformat-table>
			<timeline-item-format show-every-nth-numbering="1" show-every-nth-absolute="1" absolute-time-format="decimal" miliseconds-digits="1"/>
			<tier-format tierref="EMPTY-EDITOR">
				<property name="font-face">Plain</property>
				<property name="font-color">white</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">lightGray</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">2</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
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
			<tier-format tierref="SUB-ROW-LABEL">
				<property name="font-face">Plain</property>
				<property name="font-color">black</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">white</property>
				<property name="text-alignment">Right</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">8</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<tier-format tierref="ROW-LABEL">
				<property name="font-face">Bold</property>
				<property name="font-color">black</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">white</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">10</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>
			<tier-format tierref="COLUMN-LABEL">
				<property name="font-face">Plain</property>
				<property name="font-color">black</property>
				<property name="chunk-border-style">solid</property>
				<property name="bg-color">lightGray</property>
				<property name="text-alignment">Left</property>
				<property name="chunk-border-color">#R00G00B00</property>
				<property name="font-size">7</property>
				<property name="chunk-border"/>
				<property name="font-name">Times New Roman</property>
			</tier-format>			
			<xsl:apply-templates select="//tier"></xsl:apply-templates>
		</tierformat-table>
	</xsl:template>
	
	<xsl:template match="tier">
		<xsl:choose>
			<!-- format for tiers of type 't' -->
			<xsl:when test="@type='t'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
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
			</xsl:when>
			<!-- format for tiers of type 'a' -->
			<xsl:when test="@type='a'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Plain</property>
					<property name="font-size">7</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>					
				</xsl:element>
			</xsl:when>
			<!-- format for tiers of type 'd' -->
			<xsl:when test="@type='d'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Italic</property>
					<property name="font-size">7</property>
					<property name="font-color">black</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>					
				</xsl:element>
			</xsl:when>
			<!-- format for tiers of type 'l' -->
			<xsl:when test="@type='l'">
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Bold</property>
					<property name="font-size">7</property>
					<property name="font-color">blue</property>
					<property name="bg-color">white</property>
					<property name="chunk-border"/>
					<property name="chunk-border-style">solid</property>
					<property name="chunk-border-color">#R00G00B00</property>
					<property name="text-alignment">Left</property>					
				</xsl:element>
			</xsl:when>
			<!-- format for remaining tiers -->
			<xsl:otherwise>
				<xsl:element name="tier-format">
					<xsl:attribute name="tierref"><xsl:value-of select="@id"></xsl:value-of></xsl:attribute>
					<property name="font-name">Times New Roman</property>
					<property name="font-face">Plain</property>
					<property name="font-size">7</property>
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
