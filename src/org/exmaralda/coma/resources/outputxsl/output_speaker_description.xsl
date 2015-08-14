<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:output method="html" encoding="UTF-8"/>
	<xsl:template match="/">
		<html>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/> 
			<body>
				<table border="1">
					<tr>
						<xsl:for-each-group select="//Speaker/Description/Key/@Name"
							group-by=".">
							<xsl:sort select="."/>
							<td>
								<xsl:value-of select="current-grouping-key()"/>
							</td>
						</xsl:for-each-group>
					</tr>
					<xsl:apply-templates select="//Speaker"/>
				</table>
			</body>
		</html>
	</xsl:template>


	<xsl:template match="Speaker">
		<xsl:variable name="thisSpeaker">
			<xsl:value-of select="@Id"/>
		</xsl:variable>
		<tr>
			<xsl:for-each-group select="//Speaker/Description/Key/@Name" group-by=".">
				<xsl:sort select="."/>
				<td>
					<xsl:value-of
						select="//Speaker[@Id=$thisSpeaker]/descendant::Key[@Name=current-grouping-key()]/text()"
					/>
				</td>
			</xsl:for-each-group>
		</tr>
	</xsl:template>
</xsl:stylesheet>
