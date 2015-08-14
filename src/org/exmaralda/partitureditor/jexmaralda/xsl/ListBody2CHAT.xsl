<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text" encoding="UTF-8"/>
	<xsl:variable name="mediafile">
		<xsl:value-of select="substring-before(//referenced-file/@url,'.')"/>
	</xsl:variable>
	<!-- match any element in the document -->
	<xsl:template match="/">
		<!-- Byte Order Mark to indicate UTF-8 -->
		<xsl:text>&#xFEFF;</xsl:text>
		<!-- generate the header information -->
		<!-- file is UTF-8 encoded -->
		<xsl:text>@UTF8&#x000D;</xsl:text>
		<xsl:text>@Begin&#x000D;</xsl:text>
		<xsl:text>@Participants:</xsl:text>
		<!--process the speakers -->
		<xsl:apply-templates select="//speaker"/>
		<xsl:text>&#x000D;</xsl:text>
		<!-- process the speaker contributions one by one -->
		<xsl:apply-templates select="list-transcription/list-body/speaker-contribution"/>
	</xsl:template>
	<!-- for the speakers -->
	<xsl:template match="speaker">
		<xsl:value-of select="abbreviation"/>
		<xsl:if test="not(position()=last())">
			<xsl:text>, </xsl:text>
		</xsl:if>
	</xsl:template>	
	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution">
		<xsl:text>*</xsl:text>
		<xsl:variable name="id">
			<xsl:value-of select="@speaker"/>
		</xsl:variable>
		<xsl:value-of select="//speaker[@id = $id]/abbreviation"/>
		<xsl:text>: </xsl:text>		
		<xsl:apply-templates select="main"/>
		<xsl:text>&#x000D;</xsl:text>
	</xsl:template>
	<!-- for the mains... -->
	<xsl:template match="main">
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="ts[not(*)]/text() | nts/text() | ats/text()">
		<xsl:value-of select="."/>
	</xsl:template>
	<xsl:template match="ts | ats">
		<!-- if this element has a start point with an absolute time value... -->
		<xsl:if test="//tli[@id=current()/@s]/@time">
			<xsl:variable name="timeValue">
				<xsl:value-of select="//tli[@id=current()/@s]/@time"/>				
			</xsl:variable>
			<xsl:variable name="ancestorTimeValue">
				<xsl:value-of select="//tli[@id=current()/ancestor::ts[1]/@s]/@time"/>
			</xsl:variable>
			<xsl:if test="string-length($ancestorTimeValue)=0 or $timeValue!=$ancestorTimeValue">
				<xsl:value-of select="$timeValue"/>
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates/>
	</xsl:template>
	<xsl:template match="text()"></xsl:template>
	<!-- for the dependents and annotations... -->
	<xsl:template match="dependent | annotation">
		<!-- ... simply make a table row with one cell  in small print... -->
		<!-- ... prefixed by the name of the annotation/dependent tier-->
		<tr>
			<td>
				<small>
					<b>
						<xsl:value-of select="@name">
						</xsl:value-of>
						<xsl:text>: </xsl:text>
					</b>
					<xsl:apply-templates select="ta"/>
				</small>
			</td>
		</tr>
	</xsl:template>

	<!-- for children of dependents and annotations... -->
	<xsl:template match="//dependent/* | //annotation/*">
		<xsl:apply-templates></xsl:apply-templates>
		<!-- ...separate them by a pipe sign -->
		<xsl:if test="not(position()=last())">		
			<xsl:text> | </xsl:text>
		</xsl:if>
	
	</xsl:template>
</xsl:stylesheet>
