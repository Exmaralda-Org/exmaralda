<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:template match="/">
		<html>
		<head/>
			<body>
				<table border="1" rules="all">
				<tr>
					<th>Symbol</th>
					<th>Praat</th>
					<th></th>
				</tr>
					<xsl:apply-templates select="//mapping"/>
			</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="mapping">
		<tr>
			<td style="font-size:12pt; font-weight:bold">
				<xsl:value-of select="@unicode"/>
			</td>
			<td style="font-size:12pt; font-weight:bold">
				<xsl:value-of select="@praat"/>				
			</td>
			<td>
				<xsl:value-of select="@desc"/>				
			</td>
		</tr>
	</xsl:template>
</xsl:stylesheet>
