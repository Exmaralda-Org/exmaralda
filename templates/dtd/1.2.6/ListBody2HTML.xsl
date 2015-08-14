<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<!-- match any element in the document -->
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<!-- ... with one table... -->
				<table>
					<!-- ... and process the speaker contributions -->
					<xsl:apply-templates select="list-transcription/list-body/speaker-contribution"/>
				</table>
			</body>
		</html>
	</xsl:template>
	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution">
		<!-- ... make a table row ... -->
		<tr>
			<!-- ... with one cell for numbering ... -->
			<td valign="top">
				<pre>
				<xsl:value-of select="position()"></xsl:value-of></pre>
			</td>			
			<!-- ... one cell for the speaker abbreviation ... -->
			<td valign="top">
				<b>
					<xsl:variable name="id">
						<xsl:value-of select="@speaker"/>
					</xsl:variable>
					<xsl:value-of select="//speaker[@id = $id]/abbreviation"/>
					<xsl:text>: </xsl:text>					
				</b>
			</td>
			<!-- ... and one cell containing a nested table with the main, dependent and annotation tiers ... -->
			<td valign="top">
				<table cellspacing="0px" cellpadding="0px">
					<xsl:apply-templates select="main | dependent | annotation"/>
				</table>
			</td>
		</tr>
	</xsl:template>
	<!-- for the mains... -->
	<xsl:template match="main">
		<!-- ... simply make a table row with one cell containing the text -->
		<tr>
			<td>
				<xsl:apply-templates/>
			</td>
		</tr>
	</xsl:template>
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
