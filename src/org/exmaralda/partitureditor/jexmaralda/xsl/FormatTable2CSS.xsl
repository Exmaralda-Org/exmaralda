<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<style type="text/css">
			<xsl:apply-templates select="//tier-format"></xsl:apply-templates>
		</style>
	</xsl:template>
	<xsl:template match="tier-format">
		<xsl:text>span.</xsl:text>
		<xsl:value-of select="@tierref"></xsl:value-of>
		<xsl:text>{</xsl:text>		
		<xsl:apply-templates select="property"></xsl:apply-templates>
		<xsl:text>}</xsl:text>
	</xsl:template>	
	<xsl:template match="property">
		<xsl:choose>
			<xsl:when test="@name='font-name'">
				<xsl:text>font-family: "</xsl:text>
				<xsl:value-of select="text()"></xsl:value-of>
				<xsl:text>";</xsl:text>
			</xsl:when>
			<xsl:when test="@name='font-size'">
				<xsl:text>font-size: </xsl:text>
				<xsl:value-of select="text()"></xsl:value-of>
				<xsl:text>pt;</xsl:text>
			</xsl:when>
			<xsl:when test="@name='font-face'">
				<xsl:text>font-style: </xsl:text>
				<xsl:choose>
					<xsl:when test="text()='Italic'">
						<xsl:text>italic</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>normal</xsl:text>					
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>;</xsl:text>				
				<xsl:text>font-weight: </xsl:text>
				<xsl:choose>
					<xsl:when test="text()='Bold'">
						<xsl:text>bold</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>normal</xsl:text>					
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>;</xsl:text>				
			</xsl:when>
			<xsl:when test="@name='text-alignment'">
				<xsl:text>text-align: </xsl:text>
				<xsl:value-of select="text()"></xsl:value-of>
				<xsl:text>;</xsl:text>
			</xsl:when>
			<xsl:when test="@name='font-color'">
				<xsl:text>color: </xsl:text>
				<xsl:choose>
					<xsl:when test="text()='white'"><xsl:text>rgb(255,255,255)</xsl:text></xsl:when>
					<xsl:when test="text()='lightGray'"><xsl:text>rgb(192,192,192)</xsl:text></xsl:when>
					<xsl:when test="text()='gray'"><xsl:text>rgb(128,128,128)</xsl:text></xsl:when>
					<xsl:when test="text()='darkGray'"><xsl:text>rgb(64,64,64)</xsl:text></xsl:when>
					<xsl:when test="text()='black'"><xsl:text>rgb(0,0,0)</xsl:text></xsl:when>
					<xsl:when test="text()='red'"><xsl:text>rgb(255,0,0)</xsl:text></xsl:when>
					<xsl:when test="text()='pink'"><xsl:text>rgb(255,175,175)</xsl:text></xsl:when>
					<xsl:when test="text()='orange'"><xsl:text>rgb(255,200,0)</xsl:text></xsl:when>
					<xsl:when test="text()='yellow'"><xsl:text>rgb(255,255,0)</xsl:text></xsl:when>
					<xsl:when test="text()='yellow'"><xsl:text>rgb(0,255,0)</xsl:text></xsl:when>
  				      <xsl:when test="text()='green'"><xsl:text>rgb(0,255,0)</xsl:text></xsl:when>
					<xsl:when test="text()='magenta'"><xsl:text>rgb(0,255,255)</xsl:text></xsl:when>
					<xsl:when test="text()='blue'"><xsl:text>rgb(0,0,255)</xsl:text></xsl:when>
					<xsl:otherwise>
						<!-- color is of the form #RxxGxxBxx where xx is a hexadecimal number -->
						<xsl:text>rgb(</xsl:text>
						<xsl:value-of select="substring(text(),2,4)"></xsl:value-of>
						<xsl:text>,</xsl:text>
						<xsl:value-of select="substring(text(),5,7)"></xsl:value-of>
						<xsl:text>,</xsl:text>
						<xsl:value-of select="substring(text(),8)"></xsl:value-of>
						<xsl:text>)</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>;</xsl:text>
			</xsl:when>
			<xsl:when test="@name='bg-color'">
				<xsl:text>background-color: </xsl:text>
				<xsl:choose>
					<xsl:when test="text()='white'"><xsl:text>rgb(255,255,255)</xsl:text></xsl:when>
					<xsl:when test="text()='lightGray'"><xsl:text>rgb(192,192,192)</xsl:text></xsl:when>
					<xsl:when test="text()='gray'"><xsl:text>rgb(128,128,128)</xsl:text></xsl:when>
					<xsl:when test="text()='darkGray'"><xsl:text>rgb(64,64,64)</xsl:text></xsl:when>
					<xsl:when test="text()='black'"><xsl:text>rgb(0,0,0)</xsl:text></xsl:when>
					<xsl:when test="text()='red'"><xsl:text>rgb(255,0,0)</xsl:text></xsl:when>
					<xsl:when test="text()='pink'"><xsl:text>rgb(255,175,175)</xsl:text></xsl:when>
					<xsl:when test="text()='orange'"><xsl:text>rgb(255,200,0)</xsl:text></xsl:when>
					<xsl:when test="text()='yellow'"><xsl:text>rgb(255,255,0)</xsl:text></xsl:when>
					<xsl:when test="text()='yellow'"><xsl:text>rgb(0,255,0)</xsl:text></xsl:when>
  				      <xsl:when test="text()='green'"><xsl:text>rgb(0,255,0)</xsl:text></xsl:when>
					<xsl:when test="text()='magenta'"><xsl:text>rgb(0,255,255)</xsl:text></xsl:when>
					<xsl:when test="text()='blue'"><xsl:text>rgb(0,0,255)</xsl:text></xsl:when>
					<xsl:otherwise>
						<!-- color is of the form #RxxGxxBxx where xx is a hexadecimal number -->
						<xsl:text>rgb(</xsl:text>
						<xsl:value-of select="substring(text(),2,4)"></xsl:value-of>
						<xsl:text>,</xsl:text>
						<xsl:value-of select="substring(text(),5,7)"></xsl:value-of>
						<xsl:text>,</xsl:text>
						<xsl:value-of select="substring(text(),8)"></xsl:value-of>
						<xsl:text>)</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:text>;</xsl:text>
			</xsl:when>
			
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
