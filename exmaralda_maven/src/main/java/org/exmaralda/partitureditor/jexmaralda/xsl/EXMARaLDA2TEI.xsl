<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<!-- top level element / main structure -->
	<xsl:template match="/">
		<xsl:element name="TEI.2">
			<xsl:element name="teiHeader">
				<xsl:element name="fileDesc"/>
				<xsl:element name="profileDesc">
					<xsl:element name="particDesc">
						<xsl:apply-templates select="/list-transcription/head/speakertable/speaker"/>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="text">
				<xsl:element name="timeline">
					<xsl:apply-templates select="/list-transcription/list-body/common-timeline/tli"/>
				</xsl:element>
				<xsl:apply-templates select="/list-transcription/list-body/speaker-contribution"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	<!-- map tli elements onto when elements -->
	<xsl:template match="tli">
		<xsl:element name="when">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:if test="@time">
				<xsl:attribute name="absolute"><xsl:value-of select="@time"/></xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- map speaker elements onto person elements -->
	<xsl:template match="speaker">
		<xsl:element name="person">
			<xsl:attribute name="id"><xsl:value-of select="abbreviation/text()"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
	<!-- process speaker-contribution elements -->
	<xsl:template match="speaker-contribution">
		<xsl:element name="u">
			<xsl:attribute name="who"><xsl:value-of select="//speaker[@id=current()/@speaker]/abbreviation/text()"/></xsl:attribute>
			<xsl:attribute name="start"><xsl:value-of select="main/ts[1]/@s"/></xsl:attribute>
			<xsl:attribute name="end"><xsl:value-of select="main/ts[1]/@e"/></xsl:attribute>
			<xsl:element name="div">
				<xsl:attribute name="type">segmental</xsl:attribute>
				<xsl:apply-templates select="main"/>
			</xsl:element>
			<xsl:if test="annotation[@name='p']">
				<xsl:element name="div">
					<xsl:attribute name="type">prosody</xsl:attribute>
					<xsl:apply-templates select="annotation[@name='p']"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
		<xsl:apply-templates select="dependent[@name='Event']"/>
	</xsl:template>
	<!-- make character data and anchors inside <div type="segemental"> elements -->
	<xsl:template match="main">
		<xsl:for-each select="ts/ts[@n='e']">
			<xsl:call-template name="replaceBrackets"/>
			<xsl:if test="not (position()=last())">
				<xsl:element name="anchor">
					<xsl:attribute name="synch"><xsl:value-of select="@e"/></xsl:attribute>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- make prosody elements -->
	<xsl:template match="annotation[@name='p']">
		<xsl:for-each select="ta">
			<xsl:element name="prosody">
				<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
				<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
				<xsl:attribute name="feature"><xsl:value-of select="substring-before(text(),':')"/></xsl:attribute>
				<xsl:attribute name="desc"><xsl:value-of select="normalize-space(substring-after(text(),':'))"/></xsl:attribute>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- make events etc. outside u elements -->
	<xsl:template match="dependent[@name='Event']">
		<xsl:variable name="SPEAKER">
			<xsl:value-of select="//speaker[@id=current()/../@speaker]/abbreviation/text()"/>		
		</xsl:variable>
		<xsl:for-each select="ats">
			<xsl:choose>				
				<xsl:when test="substring(text(),1,1)='{'">
					<xsl:element name="event">
						<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
						<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
						<xsl:attribute name="who"><xsl:value-of select="$SPEAKER"/></xsl:attribute>
						<xsl:attribute name="desc"><xsl:value-of select="substring(text(),2,string-length(text())-2)"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='['">
					<xsl:element name="vocal">
						<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
						<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
						<xsl:attribute name="who"><xsl:value-of select="$SPEAKER"/></xsl:attribute>
						<xsl:attribute name="desc"><xsl:value-of select="substring(text(),2,string-length(text())-2)"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='('">
					<xsl:element name="kinesic">
						<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
						<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
						<xsl:attribute name="who"><xsl:value-of select="$SPEAKER"/></xsl:attribute>
						<xsl:attribute name="desc"><xsl:value-of select="substring(text(),2,string-length(text())-2)"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='&lt;'">
					<xsl:element name="pause">
						<xsl:attribute name="start"><xsl:value-of select="@s"/></xsl:attribute>
						<xsl:attribute name="end"><xsl:value-of select="@e"/></xsl:attribute>
						<xsl:attribute name="who"><xsl:value-of select="$SPEAKER"/></xsl:attribute>
						<xsl:attribute name="desc"><xsl:value-of select="substring(text(),2,string-length(text())-2)"/></xsl:attribute>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>

	<!-- takes care of replacing bracketed parts with the corresponding vocal, kinesic etc. elements -->
	<!-- For the moment, this assumes that each event only has one such part -->
	<!-- This should be changed for future versions -->
	<xsl:template name="replaceBrackets">
		<xsl:variable name="TEXT">
			<xsl:value-of select="text()"/>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($TEXT, '[')">
				<xsl:value-of select="substring-before($TEXT,'[')"/>
				<xsl:element name="vocal">
					<xsl:attribute name="desc">
						<xsl:value-of select="substring-after(substring-before($TEXT,']'),'[')"></xsl:value-of>
					</xsl:attribute>
				</xsl:element>
				<xsl:value-of select="substring-after($TEXT,']')"/>
			</xsl:when>
			<xsl:when test="contains($TEXT, '{')">
				<xsl:value-of select="substring-before($TEXT,'{')"/>
				<xsl:element name="event">
					<xsl:attribute name="desc">
						<xsl:value-of select="substring-after(substring-before($TEXT,'}'),'{')"></xsl:value-of>
					</xsl:attribute>
				</xsl:element>
				<xsl:value-of select="substring-after($TEXT,'}')"/>
			</xsl:when>
			<xsl:when test="contains($TEXT, '(')">
				<xsl:value-of select="substring-before($TEXT,'(')"/>
				<xsl:element name="kinesic">
					<xsl:attribute name="desc">
						<xsl:value-of select="substring-after(substring-before($TEXT,')'),'(')"></xsl:value-of>
					</xsl:attribute>
				</xsl:element>
				<xsl:value-of select="substring-after($TEXT,')')"/>
			</xsl:when>
			<xsl:when test="contains($TEXT, '&lt;')">
				<xsl:value-of select="substring-before($TEXT,'&lt;')"/>
				<xsl:element name="pause">
					<xsl:attribute name="desc">
						<xsl:value-of select="substring-after(substring-before($TEXT,'&gt;'),'&lt;')"></xsl:value-of>
					</xsl:attribute>
				</xsl:element>
				<xsl:value-of select="substring-after($TEXT,'&gt;')"/>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:value-of select="$TEXT"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
