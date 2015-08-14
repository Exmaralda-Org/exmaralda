<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- make a html document... -->
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<h1>
					<xsl:value-of select="//fsm/@name"></xsl:value-of>
				</h1>
				<h2>Character Sets</h2>
				<table>
					<xsl:apply-templates select="//char-set"></xsl:apply-templates>
				</table>
				<p></p>
				<h2>Transitions</h2>
				<xsl:apply-templates select="//transitions"></xsl:apply-templates>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="char-set">
		<tr>
		<td style="font-family:'Times New Roman'; font-weight:bold">
			<xsl:element name="a">
				<xsl:attribute name="name">
					<xsl:value-of select="@id"></xsl:value-of>
				</xsl:attribute>
				<xsl:value-of select="@id"></xsl:value-of>
			</xsl:element>
		</td>
		<td>
			<xsl:for-each select="char">
				<b>
					<xsl:value-of select="text()"></xsl:value-of>
				</b>
				<span style="color:silver">
					<xsl:text> | </xsl:text>
				</span>
			</xsl:for-each>
		</td>
		</tr>
	</xsl:template>
	<xsl:template match="transitions">
		<span style="font-family:'Times New Roman'; font-weight:bold">
			<xsl:element name="a">
				<xsl:attribute name="name">
					<xsl:value-of select="@source"></xsl:value-of>
				</xsl:attribute>
				<xsl:value-of select="@source"></xsl:value-of>
			</xsl:element>
		</span>
		<br></br>
		<span style="font-family:'Times New Roman'; font-weight:normal">
			<xsl:value-of select="./comment/text()"></xsl:value-of>
		</span>
		<br></br>
		<span style="font-family:'Times New Roman'; font-weight:normal; font-style:italic">
			<xsl:value-of select="./forbidden/text()"></xsl:value-of>
		</span>
		<br></br>
		<br></br>
		<table align="center" style="border:3px solid black">
			<tr>
				<th style="border:1px solid black">Input</th>
				<th style="border:1px solid black">Output</th>
				<th style="border:1px solid black">Target</th>
			</tr>
			<xsl:for-each select="transition">
				<tr>
					<td style="font-size:8pt; border:1px solid black">
						<xsl:apply-templates select="input-char | input-char-set | input-other | input-end"></xsl:apply-templates>
					</td>
					<td style="font-size:8pt; border:1px solid black">
						<b>
							<xsl:value-of select="./output/prefix/text()"></xsl:value-of>
							<br></br>
							<xsl:choose>
								<xsl:when test="./output[@oo='yes']">
									<xsl:text>-</xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:text>x</xsl:text>
								</xsl:otherwise>
							</xsl:choose>
							<br></br>
							<xsl:value-of select="./output/suffix/text()"></xsl:value-of>
						</b>
					</td>
					<td style="font-size:8pt; border:1px solid black">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text>
								<xsl:value-of select="./target/@id"></xsl:value-of>
							</xsl:attribute>
							<xsl:value-of select="./target/@id"></xsl:value-of>
						</xsl:element>
					</td>
				</tr>
			</xsl:for-each>
		</table>				
		<br></br>
		<hr></hr>
	</xsl:template>
	<xsl:template match="input-char-set">
		<span style="background-color:yellow">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:text>#</xsl:text>
					<xsl:value-of select="@id"></xsl:value-of>
				</xsl:attribute>
				<xsl:value-of select="@id"></xsl:value-of>
			</xsl:element>	
		</span>	
	</xsl:template>
	<xsl:template match="input-other">
		<span style="background-color:lime">
			<xsl:text>OTHER INPUT</xsl:text>
		</span>	
	</xsl:template>
	<xsl:template match="input-end">
		<span style="background-color:red">
			<xsl:text>END OF INPUT</xsl:text>
		</span>	
	</xsl:template>
</xsl:stylesheet>
