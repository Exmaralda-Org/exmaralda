<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8"/>
	<xsl:template match="/">
		<html>
			<head> </head>
			<body>
				<xsl:for-each select="//transcription">
					<xsl:sort select="@transcription-name"/>
					<h1><xsl:value-of select="@transcription-name"/></h1>
					<table>
						<tr>
							<th>Speaker</th>
							<th>Contributions</th>
							<th>Utterances</th>
							<th>Words</th>
							<th>MLU</th>
							<th>Non-Pho</th>
						</tr>
						<xsl:for-each select="speaker">
							<tr>
								<td>
									<xsl:value-of select="@abbreviation"/>
								</td>
								<td>
									<xsl:value-of
										select="count[@segment-name='Speaker Contributions']/@count"
									/>
								</td>
								<td>
									<xsl:value-of select="count[@segment-name='Utterances']/@count"
									/>
								</td>
								<td>
									<xsl:value-of select="count[@segment-name='Words']/@count"/>
								</td>
								<td>
									<xsl:value-of
										select="round(100*(count[@segment-name='Words']/@count div count[@segment-name='Utterances']/@count)) div 100"
									/>
								</td>
								<td>
									<xsl:value-of
										select="count[@segment-name='Non-Phonological']/@count"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:for-each>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
