<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:apply-templates select="head"></xsl:apply-templates>
	</xsl:template>
	<xsl:template match="head">
		<xsl:call-template name="Überschrift"></xsl:call-template>
		<xsl:apply-templates select="meta-information | speakertable"/>
		<xsl:call-template name="Inhaltsangabe"></xsl:call-template>
	</xsl:template>
	<xsl:template match="meta-information">
		<!-- "Legende" -->
		<span style="font-family:'Times New Roman',Times,serif; font-size='12'; text-decoration:underline">
			<xsl:value-of select="//ud-information[@attribute-name='Aufnahmedatum']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Transkription']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='T-Dauer']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Kontrolle']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='K-Dauer']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Übersetzung']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Ü-Dauer']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Ü-Kontrolle']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='C-Dauer']"/>
			<xsl:text>/</xsl:text>
			<xsl:value-of select="//ud-information[@attribute-name='Transkriptionsbeginn']"/>
			<xsl:text>/</xsl:text>			
			<xsl:value-of select="//ud-information[@attribute-name='Aufnahmegerät']"/>					
			<br/>
			<br/>
		</span>		
	</xsl:template>
	<xsl:template match="speakertable">
		<table cellspacing="2">
			<xsl:apply-templates select="speaker"></xsl:apply-templates>
		</table>
		<br/>
	</xsl:template>
	<xsl:template match="speaker">
		<tr>
			<td>
				<span style="font-family:'Times New Roman',Times,serif; font-size='12'">
					<xsl:value-of select="abbreviation"></xsl:value-of>
					<xsl:text> (</xsl:text>
					<xsl:value-of select="sex/@value"></xsl:value-of>
					<xsl:text>)</xsl:text>
				</span>
			</td>
			<td>
				<span style="font-family:'Times New Roman',Times,serif; font-size='12'">
					<xsl:value-of select="ud-speaker-information/ud-information[@attribute-name='Alter']"/>					
				</span>
			</td>
			<td>
				<span style="font-family:'Times New Roman',Times,serif; font-size='12'">
					<xsl:value-of select="comment"/>					
				</span>
			</td>
		</tr>
	</xsl:template>
	<xsl:template name="Inhaltsangabe">
		<span style="font-family:'Times New Roman',Times,serif; font-size='12'">
			<xsl:value-of select="meta-information/comment"></xsl:value-of>
		</span>		
		<br/>
	</xsl:template>
	<xsl:template name="Überschrift">
		<span style="font-family:'Times New Roman',Times,serif; font-size='14'; font-weight:bold">
			<xsl:value-of select="meta-information/transcription-name"></xsl:value-of>
		</span>	
		<br/>	
		<br/>
	</xsl:template>
</xsl:stylesheet>
