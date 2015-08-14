<?xml version="1.0" encoding="UTF-8"?>
<!-- changes on 14-May-2007 -->
<!-- timestampts in anchors now integer values -->
<!-- interpolation -->
<!-- changes on 04-June-2007 -->
<!-- put tier reference into 'type' attribute of annotation -->
<!-- commented out redundant info about speaker, type, category --> 

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:xlink="http://www.w3.org/1999/xlink" >
	<!-- output parameters -->
	<!-- <xsl:output method="xml" encoding="UTF-8" doctype-public="http://agtk.sourceforge.net/doc/xml/ag-1.1.dtd" doctype-system="ag-1.1.dtd"/> -->
	<xsl:output method="xml" encoding="UTF-8"/>
	<!-- top level template -->
	<!-- generates the file skeleton and calls the appropriate templates -->
	<xsl:template match="/">
		<AGSet id="exmaralda" version="1.0" xmlns="http://www.ldc.upenn.edu/atlas/ag/">
			<Metadata xmlns="">
				<xsl:for-each select="//tier">
					<xsl:element name="MetadataElement">
						<xsl:attribute name="name">Tier</xsl:attribute>
						<xsl:element name="MetadataElement">
							<xsl:attribute name="name">TierIdentifier</xsl:attribute>
							<xsl:value-of select="@id"/>
						</xsl:element>
						<xsl:for-each select="@*">
							<xsl:element name="MetadataElement">
								<xsl:attribute name="name">TierAttribute</xsl:attribute>
								<xsl:element name="MetadataElement">
									<xsl:attribute name="name">Source</xsl:attribute>
									<xsl:text>EXMARaLDA</xsl:text>
								</xsl:element>	
								<xsl:element name="MetadataElement">
									<xsl:attribute name="name">Name</xsl:attribute>
									<xsl:value-of select="name(.)"/>
								</xsl:element>	
								<xsl:element name="MetadataElement">
									<xsl:attribute name="name">Value</xsl:attribute>
									<xsl:value-of select="."/>
								</xsl:element>	
							</xsl:element>
						</xsl:for-each>
					</xsl:element>
				</xsl:for-each>
			</Metadata>
			<Timeline id="exmaralda_Timeline1">
				<xsl:element name="Signal">
					<xsl:attribute name="id">exmaralda_Timeline1_Signal1</xsl:attribute>
					<xsl:attribute name="unit">miliseconds</xsl:attribute>
					<!-- cannot determine the mimeClass via XSL -->
					<xsl:attribute name="mimeClass"></xsl:attribute>
					<!-- cannot determine the mimeType via XSL -->
					<xsl:attribute name="mimeType"></xsl:attribute>
					<!-- cannot determine the encoding via XSL -->
					<xsl:attribute name="encoding"></xsl:attribute>
					<xsl:attribute name="xlink:href">
						<xsl:apply-templates select="//referenced-file[1]"/>
					</xsl:attribute>					
				</xsl:element>
			</Timeline>
			<AG id="exmaralda_AG1" timeline="exmaralda_Timeline1" xmlns="">
				<xsl:call-template name="GenerateAGMetaData"/>
				<xsl:apply-templates select="//tli"/>
				<xsl:apply-templates select="//event"/>
			</AG>
		</AGSet>
	</xsl:template>
	
	<!-- puts the URL of the underlying media into the URL attribute of the signal --> 
	<xsl:template match="referenced-file">
		<!--<xsl:text>file://./</xsl:text> -->
		<xsl:value-of select="@url"/>
	</xsl:template>
	
	<!-- maps EXMARaLDA's timeline item's onto AG's anchor elements -->
	<xsl:template match="tli">
		<Anchor>
			<xsl:attribute name="id">
				<xsl:text>exmaralda_AG1_</xsl:text>
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="offset">
				<xsl:choose>
					<xsl:when test="@time">
						<!-- i.e. this timeline item has an absolute timestamp -->
						<xsl:value-of select="round(1000*@time)"/>						
					</xsl:when>
					<xsl:otherwise>
						<!-- i.e. this timeline item does NOT have an absolute timestamp -->
						<xsl:variable name="timeBefore" select="preceding-sibling::tli[@time][1]/@time"/>
						<xsl:variable name="timeAfter" select="following-sibling::tli[@time][1]/@time"/>
						<xsl:variable name="countBefore" select="count(preceding-sibling::tli[@time][1]/preceding-sibling::tli)"/>
						<xsl:variable name="countThis" select="position()"/>
						<xsl:variable name="countAfter" select="count(following-sibling::tli[@time][1]/preceding-sibling::tli)"/>
						<xsl:variable name="interpolatedTime" select="$timeBefore + ($timeAfter - $timeBefore) div ($countAfter - $countBefore) * ($countThis - $countBefore - 1)"/>
						<xsl:value-of select="round(1000*$interpolatedTime)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			
			<xsl:attribute name="unit">milliseconds</xsl:attribute>
		</Anchor>
	</xsl:template>
	
	<!-- maps EXMARaLDA's events onto AG's annotation elements -->
	<xsl:template match="event">		
		<Annotation>
			<xsl:attribute name="id">
				<xsl:text>exmaralda_AG1_</xsl:text>
				<!-- use a combination of the tier ID and the ID of the start point -->
				<!-- This will be unique -->
				<xsl:value-of select="../@id"/>
				<xsl:text>_</xsl:text>
				<xsl:value-of select="@start"/>
			</xsl:attribute>
			<!-- map the category of the tier to the type of the annotation -->
			<xsl:attribute name="type">
				<xsl:value-of select="../@id"/>
			</xsl:attribute>
			<!-- the start attribute of an event is the start attribute of an annotation -->
			<xsl:attribute name="start">
				<xsl:text>exmaralda_AG1_</xsl:text>
				<xsl:value-of select="@start"/>
			</xsl:attribute>
			<!-- the end attribute of an event is the end attribute of an annotation -->
			<xsl:attribute name="end">
				<xsl:text>exmaralda_AG1_</xsl:text>
				<xsl:value-of select="@end"/>
			</xsl:attribute>
			<!-- this is the actual text value of the annotation -->
			<!-- same Feature name as in Jeff's ELAN conversion -->
			<xsl:element name="Feature">
				<xsl:attribute name="name">description</xsl:attribute>
				<xsl:value-of select="text()"/>
			</xsl:element>
			<!-- put properties of the superordinate tiers into Feature elements of the annotation: -->
			<!-- i.e. the tier ID, ... -->
			<!--<xsl:element name="Feature">
				<xsl:attribute name="name">tier</xsl:attribute>
				<xsl:value-of select="../@id"/>
			</xsl:element> -->
			<!-- ...the tier speaker, ... -->
			<!-- <xsl:element name="Feature">
				<xsl:attribute name="name">speaker</xsl:attribute>
				<xsl:value-of select="../@speaker"/>
			</xsl:element> -->
			<!-- ...the tier category, ... -->
			<!-- <xsl:element name="Feature">
				<xsl:attribute name="name">category</xsl:attribute>
				<xsl:value-of select="../@category"/>
			</xsl:element> -->
			<!-- ...and (again) the tier type-->
			<!-- <xsl:element name="Feature">
				<xsl:attribute name="name">type</xsl:attribute>
				<xsl:value-of select="../@type"/>
			</xsl:element> -->
		</Annotation>
	</xsl:template>
	
	<!-- Generate the meta data for the document -->
	<xsl:template name="GenerateAGMetaData">		
	</xsl:template>
	
	
</xsl:stylesheet>
