<?xml version="1.0" encoding="UTF-8"?>
<!-- STYLESHEET "BT2PartiturHTML", Thomas Schmidt, 14.04.2004 -->
<!-- Stylsheet zur Anwendung auf eine EXMARaLDA Basic-Transcription -->
<!-- (im Partitur-Editor: File-Visualize-Free Stylesheet Visualization) -->
<!-- Generiert eine HTML-Datei mit einer Darstellung der Basic-Transcription in Partiturnotation -->
<!-- (eine Endlos-Partitur ohne Umbrüche) -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="html" encoding="UTF-8"/>
	<!-- copy the timeline into a variable timelineCopy -->
	<xsl:variable name="timelineCopy">
		<xsl:element name="tlis">
			<xsl:for-each select="//tli">
				<xsl:element name="tli">
					<xsl:copy-of select="@*"/>
					<xsl:copy-of select="node()"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:variable>
	<!-- copy the tiers into a variable tiersCopy -->
	<xsl:variable name="tiersCopy">
		<xsl:element name="tiers">
			<xsl:for-each select="//tier">
				<xsl:element name="tier">
					<!-- copy the tier attributes -->
					<xsl:copy-of select="@*"/>
					<!-- make an empty event if there isn't one at the beginning of the tier -->
					<xsl:if test="not(./event[1]/@start=$timelineCopy//tli[1]/@id)">
						<xsl:element name="event">
							<xsl:attribute name="start"><xsl:value-of select="$timelineCopy//tli[1]/@id"/></xsl:attribute>
							<xsl:attribute name="end"><xsl:value-of select="./event[1]/@start"/></xsl:attribute>
						</xsl:element>
					</xsl:if>
					<!-- copy all events -->
					<xsl:for-each select="./event">
						<xsl:element name="event">
							<xsl:copy-of select="@*"/>
							<xsl:copy-of select="text()"/>
						</xsl:element>
						<!-- make empty events for gaps in the tier -->
						<xsl:if test="not(@end=following-sibling::event/@start)">
							<xsl:element name="event">
								<xsl:attribute name="start"><xsl:value-of select="@end"/></xsl:attribute>
								<xsl:attribute name="end"><xsl:value-of select="following-sibling::event/@start"/></xsl:attribute>
							</xsl:element>
						</xsl:if>
					</xsl:for-each>
					<!-- make an empty event if there isn't one at the end of the tier -->
					<xsl:if test="not(./event[position() = last()]/@end=$timelineCopy//tli[position() = last()]/@id)">
						<xsl:element name="event">
							<xsl:attribute name="start"><xsl:value-of select="./event[position() = last()]/@end"/></xsl:attribute>
							<xsl:attribute name="end"><xsl:value-of select="$timelineCopy//tli[position() = last()]/@id"/></xsl:attribute>
						</xsl:element>
					</xsl:if>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:variable>
	<!-- copy the speakers into a variable speakersCopy -->
	<xsl:variable name="speakersCopy">
		<xsl:element name="speakertable">
			<xsl:for-each select="//speaker">
				<xsl:element name="speaker">
					<xsl:copy-of select="@*"/>
					<xsl:copy-of select="node()"/>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:variable>
	<!-- ************************************************************************************************* -->
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
			</head>
			<body>
				<table border="1">
					<!-- the top row contains the timeline -->
					<tr>
						<td/>
						<xsl:for-each select="$timelineCopy//tli">
							<td>
								<small>
									<xsl:value-of select="position()-1"/>
								</small>
							</td>
						</xsl:for-each>
					</tr>
					<!-- one table row for each tier -->
					<xsl:for-each select="$tiersCopy//tier">
						<tr>
							<xsl:variable name="speaker">
								<xsl:value-of select="@speaker"/>
							</xsl:variable>
							<xsl:variable name="tierID">
								<xsl:value-of select="@id"/>
							</xsl:variable>
							<!-- make the tier label -->
							<td nowrap="nowrap">
								<b>
									<xsl:value-of select="$speakersCopy//speaker[@id=$speaker]/abbreviation/text()"/>
									<xsl:text> [</xsl:text>
									<xsl:value-of select="@category"/>
									<xsl:text>] : </xsl:text>
								</b>
							</td>
							<!-- fill the rows with cells corresponding to events -->
							<xsl:for-each select="$timelineCopy//tli">
								<xsl:variable name="timeID">
									<xsl:value-of select="@id"/>
								</xsl:variable>
								<xsl:if test="$tiersCopy//tier[@id=$tierID]/event[@start=$timeID]">
									<xsl:variable name="matchingEvent" select="$tiersCopy//tier[@id=$tierID]/event[@start=$timeID]"/>
									<xsl:variable name="endPosition">
										<xsl:for-each select="$timelineCopy//tli">
											<xsl:if test="@id=$matchingEvent/@end">
												<xsl:value-of select="position()"/>
											</xsl:if>
										</xsl:for-each>
									</xsl:variable>
									<xsl:element name="td">
										<xsl:attribute name="nowrap">nowrap</xsl:attribute>
										<xsl:attribute name="colspan"><xsl:value-of select="$endPosition - position()"/></xsl:attribute>
										<xsl:value-of select="$matchingEvent/text()"/>
									</xsl:element>
								</xsl:if>
							</xsl:for-each>
						</tr>
					</xsl:for-each>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
