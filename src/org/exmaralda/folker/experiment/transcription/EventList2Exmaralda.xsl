<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name/>
					<transcription-name/>
					<referenced-file url=""/>
					<ud-meta-information/>
					<comment/>
					<transcription-convention/>
				</meta-information>
				<speakertable>
					<xsl:for-each-group select="//event" group-by="@speaker-ID">
						<speaker>
							<xsl:attribute name="id">
								<xsl:value-of select="current-grouping-key()"/>
							</xsl:attribute>
							<abbreviation>
								<xsl:value-of select="current-grouping-key()"/>								
							</abbreviation>
							<sex value="m"/>
							<languages-used/>
							<l1/>
							<l2/>
							<ud-meta-information/>
							<comment/>
						</speaker>
					</xsl:for-each-group>
				</speakertable>
			</head>
			<basic-body>
				<common-timeline>
					<xsl:for-each-group select="//event/@start-time | //event/@end-time" group-by=".">
						<xsl:sort data-type="number"/>
						<tli>
							<xsl:attribute name="id">
								<xsl:text>T_</xsl:text><xsl:value-of select="translate(current-grouping-key(),'.','_')"/>
							</xsl:attribute>
							<xsl:attribute name="time">
								<xsl:value-of select="current-grouping-key()"/>
							</xsl:attribute>
						</tli>
					</xsl:for-each-group>
				</common-timeline>
				
				<xsl:for-each-group select="//event" group-by="@speaker-ID">
					<tier>
						<xsl:attribute name="id">
							<xsl:text>TIER_</xsl:text><xsl:value-of select="current-grouping-key()"/>
						</xsl:attribute>
						<xsl:attribute name="speaker">
							<xsl:value-of select="current-grouping-key()"/>
						</xsl:attribute>
						<xsl:attribute name="type">t</xsl:attribute>
						<xsl:attribute name="category">v</xsl:attribute>
						<xsl:attribute name="display-name">
							<xsl:value-of select="current-grouping-key()"/><xsl:text> [v]</xsl:text>
						</xsl:attribute>
						<xsl:for-each select="current-group()">
							<event>
								<xsl:attribute name="start">
									<xsl:text>T_</xsl:text><xsl:value-of select="translate(@start-time,'.','_')"/>
								</xsl:attribute>
								<xsl:attribute name="end">
									<xsl:text>T_</xsl:text><xsl:value-of select="translate(@end-time,'.','_')"/>
								</xsl:attribute>
								<xsl:value-of select="text()"/>
							</event>
						</xsl:for-each>
					</tier>
				</xsl:for-each-group>				
			</basic-body>
		</basic-transcription>
	</xsl:template>
</xsl:stylesheet>
