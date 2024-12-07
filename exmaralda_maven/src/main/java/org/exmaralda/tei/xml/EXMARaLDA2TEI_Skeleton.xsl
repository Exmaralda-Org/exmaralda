<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"  xmlns:tesla="http://www.exmaralda.org"   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tei="http://www.tei-c.org/ns/1.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

	<xsl:function name="tesla:determine-recording-type">
		<xsl:param name="path"/>
		<xsl:choose>
			<xsl:when test="ends-with(lower-case($path), '.wav')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.mp3')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.aif')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.snd')">audio</xsl:when>
			<xsl:otherwise>video</xsl:otherwise>
		</xsl:choose>
		
	</xsl:function>
	
	<xsl:function name="tesla:seconds-to-timestring">
		<xsl:param name="seconds"/>
		<xsl:variable name="totalseconds">
			<xsl:value-of select="0 + $seconds"/>
		</xsl:variable>
		<xsl:variable name="hours">
			<xsl:value-of select="0 + floor($totalseconds div 3600)"/>
		</xsl:variable>
		<xsl:variable name="minutes">
			<xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
		</xsl:variable>
		<xsl:variable name="seconds">
			<xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
		</xsl:variable>
		<xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
			<xsl:text>0</xsl:text>
			<xsl:value-of select="$hours"/>
		</xsl:if>
		<xsl:if test="$hours + 0 = 0">
			<xsl:text>00</xsl:text>                            
		</xsl:if>
		<xsl:text>:</xsl:text>
		<xsl:if test="$minutes+0 &lt; 10">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="$minutes"/>
		<xsl:text>:</xsl:text>
		<xsl:if test="$seconds+0 &lt; 10">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="$seconds"/>            
	</xsl:function>
	
	
	<!-- top level element / main structure -->
	<xsl:template match="/">
		<TEI xmlns="http://www.tei-c.org/ns/1.0">
			<teiHeader>
				<fileDesc>
					<titleStmt><title></title></titleStmt>
					<publicationStmt><p></p></publicationStmt>
					<sourceDesc>
						<recordingStmt>
							<xsl:apply-templates select="//referenced-file"/>
						</recordingStmt>                                        
					</sourceDesc>
					<!-- ... -->
				</fileDesc>
				<profileDesc>
					<!-- ... -->
					<particDesc>
						<xsl:apply-templates select="//speaker"/>
					</particDesc>                    
				</profileDesc>
				<revisionDesc>
					<!-- ... -->                    
					<xsl:element name="change">
						<xsl:attribute name="when">
							<xsl:value-of select="current-dateTime()"/>
						</xsl:attribute>
						<xsl:text>Created by XSL transformation from an EXMARaLDA segmented transcription</xsl:text>
					</xsl:element>
				</revisionDesc>                
			</teiHeader>
			<text>
				<timeline unit="s">
					<xsl:attribute name="origin">
						<xsl:text>#</xsl:text><xsl:value-of select="//common-timeline/tli[1]/@id"></xsl:value-of>
					</xsl:attribute>
					<xsl:apply-templates select="//common-timeline/tli"/>
				</timeline>
				<body>
				</body>
			</text>
		</TEI>            
	</xsl:template>

	<xsl:template match="referenced-file">
		<recording xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type"><xsl:value-of select="tesla:determine-recording-type(@url)"/></xsl:attribute>
			<xsl:attribute name="url"><xsl:value-of select="@url"/></xsl:attribute>
		</recording>        
	</xsl:template>
	
	<xsl:template match="speaker">
		<xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="xml:id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="sex">
				<xsl:choose>
					<xsl:when test="sex/@value='m'">1</xsl:when>
					<xsl:when test="sex/@value='f'">2</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>                
			</xsl:attribute>
			<xsl:element name="persName">
				<xsl:element name="abbr">
					<xsl:value-of select="abbreviation"/>
				</xsl:element>
			</xsl:element>
		</xsl:element>        
	</xsl:template>
	
	<xsl:template match="tli">
		<xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="xml:id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:if test="@time">
				<xsl:attribute name="absolute">
					<xsl:value-of select="tesla:seconds-to-timestring(@time)"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>        
	</xsl:template>
	
</xsl:stylesheet>
