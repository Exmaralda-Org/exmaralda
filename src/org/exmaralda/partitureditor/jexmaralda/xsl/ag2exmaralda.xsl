<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:xlink="http://www.w3.org/1999/xlink" xpath-default-namespace="http://www.ldc.upenn.edu/atlas/ag/">
	<xsl:output method="xml" encoding="UTF-8"/>
	
	<!-- top level template -->
	<!-- generates the file skeleton and calls the appropriate templates -->
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name/>
					<transcription-name/>
					<!-- get the referenced file URL from the Signal URL -->
					<xsl:element name="referenced-file">
						<xsl:attribute name="url">
							<xsl:value-of select="//Signal[1]/@xlink:href"/>
						</xsl:attribute>						
					</xsl:element>
					<ud-meta-information/>	
					<comment/>
					<transcription-convention/>
				</meta-information>
					<!-- reconstruct the speaker table from the feature values in annotations -->
					<!-- <xsl:call-template name="GenerateSpeakers"/> -->
					<speakertable>
						<speaker id="SPK0">
							<abbreviation>X</abbreviation>
							<sex value="u"/>
							<languages-used/>
							<l1/>
							<l2/>
							<ud-speaker-information> </ud-speaker-information>
							<comment/>
						</speaker>
					</speakertable>
			</head>
			<basic-body>
				<common-timeline>
						<!-- reconstruct the timeline from the anchors -->
						<xsl:apply-templates select="//Anchor">
							<xsl:sort select="@offset" data-type="number"/>
						</xsl:apply-templates>
				</common-timeline>
				<xsl:for-each-group select="//Annotation" group-by="@type">
					<xsl:for-each-group select="current-group()" group-by="Feature/@name">
						<!-- make a tier for each unique combination of type attribute and feature name -->
						<xsl:element name="tier">
							<!-- make an ID for this tier by concatenating the type attribute and the feature name -->
							<!-- this is guaranteed to be unique -->
							<xsl:attribute name="id">
								<xsl:value-of  select="@type"/>
								<xsl:text>_</xsl:text>
								<xsl:value-of select="current-grouping-key()"></xsl:value-of>
							</xsl:attribute>
							<xsl:attribute name="speaker">SPK0</xsl:attribute>
							<xsl:attribute name="category">v</xsl:attribute>
							<xsl:attribute name="type">t</xsl:attribute>
							<xsl:attribute name="display-name"><xsl:value-of select="@type"/></xsl:attribute>
							<!-- get all the currently selected annotations ... -->
							<xsl:for-each select="current-group()">
								<!-- ... and transform them into EXMARaLDA events... -->
								<xsl:element name="event">
									<!-- .. with appropriate start and end points -->
									<xsl:attribute name="start">
										<xsl:value-of select="@start"/>
									</xsl:attribute>
									<xsl:attribute name="end">
										<xsl:value-of select="@end"/>
									</xsl:attribute>
									<xsl:value-of select="Feature[@name=current-grouping-key()]/text()"/>
								</xsl:element>								
							</xsl:for-each>
						</xsl:element>												
					</xsl:for-each-group>				
				</xsl:for-each-group>
			</basic-body>
		</basic-transcription>
	</xsl:template>
	

	<!-- transform anchors into EXMARaLDA timeline items -->
	<xsl:template match="Anchor">
		<xsl:element name="tli">
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="time">
				<!-- EXMARaLDA wants offsets in seconds with decimals rather than in integer miliseconds -->
				<xsl:value-of select="@offset div 1000"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	
		
</xsl:stylesheet>
