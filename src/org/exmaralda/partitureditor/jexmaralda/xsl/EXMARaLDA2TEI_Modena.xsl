<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:exmaralda="http://www.exmaralda.org/">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<!-- top level element / main structure -->
	<xsl:template match="/">
		<!-- change: TEI, not TEI.2 -->
		<xsl:element name="TEI">
			<xsl:element name="teiHeader">
				<xsl:element name="fileDesc">
					<!-- change: added content to the fileDesc -->
					<titleStmt>
						<title>Esmeralda</title>
						<date value="000000"/>
						<programme number="00"/>
					</titleStmt>
				</xsl:element>
				<xsl:element name="profileDesc">
					<xsl:element name="particDesc">
						<xsl:element name="listPerson">
							<xsl:apply-templates
								select="/list-transcription/head/speakertable/speaker"/>
						</xsl:element>
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
			<xsl:attribute name="id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:if test="@time">
				<xsl:attribute name="absolute">
					<xsl:value-of select="@time"/>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>
	</xsl:template>
	<!-- map speaker elements onto person elements -->
	<xsl:template match="speaker">
		<xsl:element name="person">
			<!-- change: who, not id -->
			<xsl:attribute name="who">
				<xsl:value-of select="abbreviation/text()"/>
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

        <!-- process speaker-contribution elements *without* speaker-->
	<xsl:template match="speaker-contribution[not(@speaker)]">
            <xsl:variable name="DESC_TEXT">
                <xsl:value-of select="main/ts/ats/text()"/>
            </xsl:variable>
            <xsl:if test="starts-with($DESC_TEXT,'&lt;') and ends-with($DESC_TEXT,'&gt;')">
                <!-- this is a pause -->
                <xsl:element name="pause">
                    <xsl:attribute name="dur">
                        <xsl:if test="starts-with($DESC_TEXT,'&lt;dur=') and ends-with($DESC_TEXT,'&gt;')">
                            <xsl:value-of select="substring($DESC_TEXT, 6, string-length($DESC_TEXT)-6)"/>
                        </xsl:if>
                        <xsl:if test="not(starts-with($DESC_TEXT,'&lt;dur=') and ends-with($DESC_TEXT,'&gt;'))">
                            <xsl:value-of select="substring($DESC_TEXT, 2, string-length($DESC_TEXT)-2)"/>
                        </xsl:if>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
            <!-- <xsl:if test="not(starts-with($DESC_TEXT,'&lt;dur=') and ends-with($DESC_TEXT,'&gt;'))">-->
            <!-- changed 07-03-2011: this is not complimentary to the above -->
            <xsl:if test="not(starts-with($DESC_TEXT,'&lt;') and ends-with($DESC_TEXT,'&gt;'))">
                <xsl:element name="event">
                    <xsl:attribute name="start">
                            <xsl:value-of select="main/ts[1]/@s"/>
                    </xsl:attribute>
                    <xsl:attribute name="end">
                            <xsl:value-of select="main/ts[1]/@e"/>
                    </xsl:attribute>
                    <xsl:attribute name="desc">
                        <xsl:value-of select="$DESC_TEXT"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:if>
        </xsl:template>

        <!-- process speaker-contribution elements *with* speakers-->
	<xsl:template match="speaker-contribution[@speaker]">
		<xsl:element name="u">
			<xsl:attribute name="who">
				<xsl:value-of select="//speaker[@id=current()/@speaker]/abbreviation/text()"/>
			</xsl:attribute>
			<xsl:attribute name="start">
				<xsl:value-of select="main/ts[1]/@s"/>
			</xsl:attribute>
			<xsl:attribute name="end">
				<xsl:value-of select="main/ts[1]/@e"/>
			</xsl:attribute>
			<!-- change: seg, not div -->
			<xsl:element name="seg">
				<xsl:attribute name="type">segmental</xsl:attribute>
				<xsl:apply-templates select="main"/>
			</xsl:element>
			<xsl:if test="annotation[@name='p']">
				<!-- change: seg, not div -->
				<xsl:element name="seg">
					<xsl:attribute name="type">prosody</xsl:attribute>
					<xsl:apply-templates select="annotation[@name='p']"/>
				</xsl:element>
			</xsl:if>
			<!-- added 30-08-2010: Modena has added a function tier (see email by Natacha Niemants) -->
                        <xsl:if test="annotation[@name='f']">
				<!-- change: seg, not div -->
				<xsl:element name="seg">
					<xsl:attribute name="type">function</xsl:attribute>
					<xsl:apply-templates select="annotation[@name='f']"/>
				</xsl:element>
			</xsl:if>
		</xsl:element>
		<xsl:apply-templates select="dependent[@name='Event']"/>
	</xsl:template>
	<!-- make character data and anchors inside <div type="segemental"> elements -->
	<xsl:template match="main">
                <xsl:for-each select="ts/ts[@n='e']">
			<!-- <xsl:call-template name="replaceBrackets"/> -->
			<xsl:value-of select="text()"/>
			<xsl:if test="not (position()=last())">
				<xsl:element name="anchor">
					<xsl:attribute name="synch">
						<xsl:value-of select="@e"/>
					</xsl:attribute>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!-- make prosody elements -->
	<xsl:template match="annotation[@name='p']">
		<xsl:for-each select="ta">
			<xsl:element name="prosody">
				<xsl:attribute name="start">
					<xsl:value-of select="@s"/>
				</xsl:attribute>
				<xsl:attribute name="end">
					<xsl:value-of select="@e"/>
				</xsl:attribute>
				<xsl:attribute name="feature">
					<xsl:value-of select="substring-before(text(),':')"/>
				</xsl:attribute>
				<xsl:attribute name="desc">
					<xsl:value-of select="normalize-space(substring-after(text(),':'))"/>
				</xsl:attribute>
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
						<xsl:attribute name="start">
							<xsl:value-of select="@s"/>
						</xsl:attribute>
						<xsl:attribute name="end">
							<xsl:value-of select="@e"/>
						</xsl:attribute>
						<xsl:attribute name="who">
							<xsl:value-of select="$SPEAKER"/>
						</xsl:attribute>
						<xsl:attribute name="desc">
							<xsl:value-of select="substring(text(),2,string-length(text())-2)"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='['">
					<xsl:element name="vocal">
						<xsl:attribute name="start">
							<xsl:value-of select="@s"/>
						</xsl:attribute>
						<xsl:attribute name="end">
							<xsl:value-of select="@e"/>
						</xsl:attribute>
						<xsl:attribute name="who">
							<xsl:value-of select="$SPEAKER"/>
						</xsl:attribute>
						<xsl:attribute name="desc">
							<xsl:value-of select="substring(text(),2,string-length(text())-2)"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='('">
					<xsl:element name="kinesic">
						<xsl:attribute name="start">
							<xsl:value-of select="@s"/>
						</xsl:attribute>
						<xsl:attribute name="end">
							<xsl:value-of select="@e"/>
						</xsl:attribute>
						<xsl:attribute name="who">
							<xsl:value-of select="$SPEAKER"/>
						</xsl:attribute>
						<xsl:attribute name="desc">
							<xsl:value-of select="substring(text(),2,string-length(text())-2)"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:when>
				<xsl:when test="substring(text(),1,1)='&lt;'">
					<xsl:element name="pause">
						<xsl:attribute name="start">
							<xsl:value-of select="@s"/>
						</xsl:attribute>
						<xsl:attribute name="end">
							<xsl:value-of select="@e"/>
						</xsl:attribute>
						<xsl:attribute name="who">
							<xsl:value-of select="$SPEAKER"/>
						</xsl:attribute>
						<xsl:attribute name="desc">
							<xsl:value-of select="substring(text(),2,string-length(text())-2)"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:when>
			</xsl:choose>
		</xsl:for-each>
	</xsl:template>



</xsl:stylesheet>
