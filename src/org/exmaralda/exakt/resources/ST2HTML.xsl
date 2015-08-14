<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<xsl:apply-templates select="segmented-transcription/segmented-body/segmented-tier"/>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="segmented-tier">
		<p>
			<big>
				<b>
					<xsl:text>TIER: </xsl:text>
					<xsl:variable name="id">
						<xsl:value-of select="@speaker"/>
					</xsl:variable>
					<xsl:value-of select="//speaker[@id = $id]/abbreviation"/>
					<xsl:text> [</xsl:text>
					<xsl:value-of select="@category"/>
					<xsl:text>]</xsl:text>
				</b>
			</big>
		</p>
		<xsl:apply-templates select="segmentation | annotation"/>
		<hr/>
	</xsl:template>
	<xsl:template match="segmentation">
		<p>
			<i>
				<xsl:text>SEGMENTATION: </xsl:text>
				<xsl:value-of select="@name"/>
			</i>
		</p>
		<table>
			<tr>
				<xsl:apply-templates select="ts"/>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="annotation">
		<p>
			<i>
				<xsl:text>ANNOTATION: </xsl:text>
				<xsl:value-of select="@name"/>
			</i>
		</p>
		<table>
			<tr>
				<xsl:apply-templates select="ta"/>
			</tr>
		</table>
	</xsl:template>
	<xsl:template match="ts">
			<xsl:choose>
				<xsl:when test="*">
					<td>
						<table style="border:3px solid black">
							<thead>
								<tr>
									<b>
										<small>
											<xsl:value-of select="@n"/>
											<sub>
												<xsl:text> (</xsl:text>
												<xsl:value-of select="@s"></xsl:value-of>
												<xsl:text>/</xsl:text>
												<xsl:value-of select="@e"></xsl:value-of>
												<xsl:text>)</xsl:text>
											</sub>
										</small>
									</b>
								</tr>
							</thead>
							<tr>
								<xsl:apply-templates select="ts | ats | nts"/>
							</tr>
						</table>
					</td>
				</xsl:when>
				<xsl:otherwise>
						<td valign="top" nowrap="nowrap" style="border:1px solid black; background-color: red">
							<b>
								<small>
									<xsl:value-of select="@n"/>
									<sub>
										<xsl:text> (</xsl:text>
										<xsl:value-of select="@s"></xsl:value-of>
										<xsl:text>/</xsl:text>
										<xsl:value-of select="@e"></xsl:value-of>
										<xsl:text>)</xsl:text>
									</sub>
								</small>
							</b>
							<br/>
							<xsl:apply-templates/>
						</td>
				</xsl:otherwise>
			</xsl:choose>
	</xsl:template>
	<xsl:template match="ats">
		<td valign="top" nowrap="nowrap" style="border:1px solid black; background-color: aqua">
			<b>
				<small>
					<xsl:value-of select="@n"/>
					<sub>
						<xsl:text> (</xsl:text>
						<xsl:value-of select="@s"></xsl:value-of>
						<xsl:text>/</xsl:text>
						<xsl:value-of select="@e"></xsl:value-of>
						<xsl:text>)</xsl:text>
					</sub>
				</small>
			</b>
			<br/>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	<xsl:template match="nts">
		<td  valign="top" nowrap="nowrap" style="border:1px solid black; background-color: yellow">
			<b>
				<small>
					<xsl:value-of select="@n"/>
				</small>
			</b>
			<br/>
			<xsl:apply-templates/>
		</td>
	</xsl:template>
	<xsl:template match="ta">
		<td  valign="top" nowrap="nowrap" style="border:1px solid black; background-color: lime">
			<xsl:apply-templates/>
		</td>
	</xsl:template>
</xsl:stylesheet>
