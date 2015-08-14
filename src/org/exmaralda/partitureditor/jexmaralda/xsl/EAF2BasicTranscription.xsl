<?xml version="1.0" encoding="UTF-8"?>
<!-- Revision History -->
<!-- 0      -->
<!-- 1      09-Feb-2004: Adaptations to new format (ELAN 2.0.2.) -->
<!-- 2      19-Nov-2008: Removed ud-information about ELAN-IDs in events -->
<!-- 3	 21-Sep-2010: changed method for getting the speakertable, seemed to be buggy -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xalan="http://xml.apache.org/xalan">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<basic-transcription>
			<head>
				<meta-information>
					<project-name/>
					<transcription-name/>
					<xsl:apply-templates select="/ANNOTATION_DOCUMENT/HEADER/MEDIA_DESCRIPTOR"/>
					<ud-meta-information>
						<!-- added in revision 1 -->
						<xsl:element name="ud-information">
							<xsl:attribute name="attribute-name"><xsl:text>ELAN-Media-File</xsl:text></xsl:attribute>
							<xsl:value-of select="/ANNOTATION_DOCUMENT/HEADER/MEDIA_DESCRIPTOR/@MEDIA_URL"/>
						</xsl:element>
						<xsl:element name="ud-information">
							<xsl:attribute name="attribute-name"><xsl:text>ELAN-Mime-Type</xsl:text></xsl:attribute>
							<xsl:value-of select="/ANNOTATION_DOCUMENT/HEADER/MEDIA_DESCRIPTOR/@MIME_TYPE"/>
						</xsl:element>
					</ud-meta-information>
					<comment/>
					<transcription-convention/>
				</meta-information>
				<xsl:call-template name="generateSpeakertable"/>
			</head>
			<body>
				<common-timeline>
					<xsl:for-each select="//TIME_ORDER/TIME_SLOT">
						<xsl:element name="tli">
							<xsl:attribute name="id"><xsl:value-of select="@TIME_SLOT_ID"/></xsl:attribute>
							<xsl:attribute name="time"><xsl:value-of select="@TIME_VALUE div 1000"/></xsl:attribute>
							<xsl:attribute name="type"><xsl:text>appl</xsl:text></xsl:attribute>
						</xsl:element>
					</xsl:for-each>
					<tli id="TO_BE_INFERRED"/>
				</common-timeline>
				<xsl:for-each select="//TIER">
					<xsl:element name="tier">
						<xsl:attribute name="id"><xsl:value-of select="@TIER_ID"/></xsl:attribute>
						<xsl:attribute name="speaker"><xsl:text>SPK_</xsl:text><xsl:value-of select="@PARTICIPANT"/></xsl:attribute>
						<xsl:attribute name="category"><xsl:value-of select="@LINGUISTIC_TYPE_REF"/></xsl:attribute>
						<xsl:attribute name="type"><xsl:choose><xsl:when test="string-length(@PARENT_REF)>0"><xsl:text>a</xsl:text></xsl:when><xsl:otherwise><xsl:text>t</xsl:text></xsl:otherwise></xsl:choose></xsl:attribute>
						<xsl:attribute name="display-name"><xsl:value-of select="@TIER_ID"/></xsl:attribute>
						<xsl:element name="ud-tier-information">
							<xsl:element name="ud-information">
								<xsl:attribute name="attribute-name">ELAN-TimeAlignable</xsl:attribute>
								<xsl:value-of select="//LINGUISTIC_TYPE[@LINGUISTIC_TYPE_ID=current()/@LINGUISTIC_TYPE_REF]/@TIME_ALIGNABLE"/>
							</xsl:element>
							<xsl:element name="ud-information">
								<xsl:attribute name="attribute-name">ELAN-Constraints</xsl:attribute>
								<xsl:value-of select="//LINGUISTIC_TYPE[@LINGUISTIC_TYPE_ID=current()/@LINGUISTIC_TYPE_REF]/@CONSTRAINTS"/>
							</xsl:element>
							<xsl:element name="ud-information">
								<xsl:attribute name="attribute-name">ELAN-ParentRef</xsl:attribute>
								<xsl:value-of select="@PARENT_REF"/>
							</xsl:element>
						</xsl:element>
						<xsl:apply-templates select="ANNOTATION"/>
					</xsl:element>
				</xsl:for-each>
			</body>
		</basic-transcription>
	</xsl:template>
	<xsl:template match="MEDIA_DESCRIPTOR">
        <xsl:element name="referenced-file">
            <xsl:attribute name="url">
                <!-- changed in revision 1 -->
                <!-- file:/// -->
                <xsl:variable name="MEDIA_URL">
                    <xsl:value-of select="@MEDIA_URL"/>
                </xsl:variable>
                <xsl:value-of select="substring($MEDIA_URL,9, string-length($MEDIA_URL)-8)"/>
            </xsl:attribute>
        </xsl:element>        
    </xsl:template>

    <xsl:template name="generateSpeakertable">
		<xsl:for-each-group select="//TIER/@PARTICIPANT" group-by=".">
			<xsl:element name="speaker">
				<xsl:attribute name="id"><xsl:text>SPK_</xsl:text><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
				<xsl:element name="abbreviation">
					<xsl:value-of select="current-grouping-key()"/>
				</xsl:element>
				<sex value="u"/>
				<languages-used/>
				<l1/>
				<l2/>
				<ud-speaker-information/>
				<comment/>
			</xsl:element>		
		</xsl:for-each-group>	
	</xsl:template>

	<xsl:template match="ANNOTATION">
		<!-- changed in revision 1 -->
		<xsl:apply-templates select="ALIGNABLE_ANNOTATION | REF_ANNOTATION"/>
		<!-- <xsl:apply-templates select="ALIGNABLE_ANNOTATION"/> -->
	</xsl:template>
	<xsl:template match="ALIGNABLE_ANNOTATION">
		<xsl:element name="event">
			<xsl:attribute name="start"><xsl:value-of select="@TIME_SLOT_REF1"/></xsl:attribute>
			<xsl:attribute name="end"><xsl:value-of select="@TIME_SLOT_REF2"/></xsl:attribute>
			<!-- removed in revision 2 -->
            <!-- but removal causes a bug! -->
            <!-- so put it back in again -->
            <xsl:element name="ud-information">
            <xsl:attribute name="attribute-name">ELAN-ID</xsl:attribute>
            <xsl:value-of select="@ANNOTATION_ID"/>
			</xsl:element>
			<xsl:apply-templates select="ANNOTATION_VALUE"/>
		</xsl:element>
	</xsl:template>
	<!-- added in revision 1 -->
	<xsl:template match="REF_ANNOTATION">
		<xsl:element name="event">
			<xsl:attribute name="start">TO_BE_INFERRED</xsl:attribute>
			<xsl:attribute name="end">TO_BE_INFERRED</xsl:attribute>
			<xsl:element name="ud-information">
				<xsl:attribute name="attribute-name">ELAN-ID</xsl:attribute>
				<xsl:value-of select="@ANNOTATION_ID"/>
			</xsl:element>
			<xsl:element name="ud-information">
				<xsl:attribute name="attribute-name">ELAN-REF</xsl:attribute>
				<xsl:value-of select="@ANNOTATION_REF"/>
			</xsl:element>
			<xsl:element name="ud-information">
				<xsl:attribute name="attribute-name">ELAN-PREV</xsl:attribute>
				<xsl:value-of select="@PREVIOUS_ANNOTATION"/>
			</xsl:element>
			<xsl:apply-templates select="ANNOTATION_VALUE"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="ANNOTATION_VALUE">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
