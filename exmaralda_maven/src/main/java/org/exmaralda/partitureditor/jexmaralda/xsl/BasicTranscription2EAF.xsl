<?xml version="1.0" encoding="UTF-8"?>
<!-- Revision History -->
<!-- 0      -->
<!-- 1      09-Feb-2004: Adaptions to new format (ELAN 2.0.2.) -->
<!-- 2      12-May-2009: Set mime type at least for wav files -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<!-- the root element -->
		<xsl:element name="ANNOTATION_DOCUMENT">
			<xsl:attribute name="DATE"/>
			<xsl:attribute name="AUTHOR"/>
			<xsl:attribute name="VERSION">2.1</xsl:attribute>
			<xsl:attribute name="FORMAT">2.1</xsl:attribute>
			<!-- the header element -->
			<xsl:element name="HEADER">
                <xsl:attribute name="MEDIA_FILE"><!-- taken out in revision 1 --><!-- <xsl:value-of select="//meta-information/ud-meta-information/ud-information[@attribute-name='ELAN-Media-File']"/> --></xsl:attribute>
                <xsl:attribute name="TIME_UNITS">milliseconds</xsl:attribute>
                <xsl:for-each select="//referenced-file">
                    <!--  added in revision 1 -->
                    <xsl:element name="MEDIA_DESCRIPTOR">
                        <xsl:attribute name="MEDIA_URL">
                            <xsl:text>file:///</xsl:text>
                            <xsl:value-of select="@url"/>
                            <!-- <xsl:value-of select="//meta-information/ud-meta-information/ud-information[@attribute-name='ELAN-Media-File']"/> -->
                        </xsl:attribute>
                        <!-- added in revision 2-->
                        <xsl:attribute name="MIME_TYPE">
                            <xsl:variable name="suffix">
                                <xsl:value-of select="substring(@url, string-length(@url)-2)"/>
                            </xsl:variable>
                            <xsl:if test="$suffix='wav' or $suffix='WAV'">
                                <xsl:text>audio/x-wav</xsl:text>
                            </xsl:if>
                        </xsl:attribute>

                        <!-- <xsl:attribute name="MIME_TYPE"><xsl:value-of select="//meta-information/ud-meta-information/ud-information[@attribute-name='ELAN-Mime-Type']"/></xsl:attribute> -->
                    </xsl:element>
                </xsl:for-each>
			</xsl:element>
			<!-- call a template to generate the time_order element -->
			<xsl:call-template name="makeTimeOrder"/>
			<!-- call a template to generate the tier elements -->
			<xsl:call-template name="makeTiers"/>
			<!-- added in revision 1 -->
			<!-- call a template to generate the linguistic types -->
			<xsl:call-template name="makeLinguisticTypes"/>
		</xsl:element>
	</xsl:template>
	<!-- generate the time_order -->
	<xsl:template name="makeTimeOrder">
		<xsl:element name="TIME_ORDER">
                    <!-- need to change this - every annotation gets its own time slot element -->
                    <!-- even if they have the exact same time value -->
                    <xsl:for-each select="//common-timeline/tli">
                            <xsl:element name="TIME_SLOT">
                                    <xsl:attribute name="TIME_SLOT_ID"><xsl:value-of select="@id"/></xsl:attribute>
                                    <xsl:attribute name="TIME_VALUE"><xsl:value-of select="round(@time*1000)"/></xsl:attribute>
                            </xsl:element>
                    </xsl:for-each>
		</xsl:element>
	</xsl:template>
	<!-- generate tiers -->
	<xsl:template name="makeTiers">
		<xsl:for-each select="//tier">
			<xsl:element name="TIER">
				<xsl:attribute name="TIER_ID">
					<!-- changed in revision 1 -->
					<xsl:value-of select="@display-name"/>
					<xsl:text> (</xsl:text>
					<xsl:value-of select="@id"/>
					<xsl:text>)</xsl:text>
				</xsl:attribute>
				<xsl:attribute name="PARTICIPANT"><xsl:value-of select="@speaker"/></xsl:attribute>
				<xsl:attribute name="LINGUISTIC_TYPE_REF"><xsl:value-of select="@category"/></xsl:attribute>
				<xsl:attribute name="DEFAULT_LOCALE">en</xsl:attribute>
				<!-- if the tier is an annotation tier, assign it to its parent tier -->
				<xsl:if test="@type='a'">
					<xsl:attribute name="PARENT_REF">
                                            <xsl:value-of select="//tier[@speaker=current()/@speaker and @type='t']/@display-name"/>
                                            <xsl:text> (</xsl:text>
                                            <xsl:value-of select="//tier[@speaker=current()/@speaker and @type='t']/@id"/>
                                            <xsl:text>)</xsl:text>
                                        </xsl:attribute>
				</xsl:if>
				<xsl:for-each select="event">
					<xsl:element name="ANNOTATION">
						<xsl:element name="ALIGNABLE_ANNOTATION">
							<xsl:attribute name="ANNOTATION_ID"><!-- generate a unique ID from the tier ID and the position of the event --><xsl:value-of select="../@id"/><xsl:text>_</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
							<xsl:attribute name="TIME_SLOT_REF1"><xsl:value-of select="@start"/></xsl:attribute>
							<xsl:attribute name="TIME_SLOT_REF2"><xsl:value-of select="@end"/></xsl:attribute>
							<xsl:element name="ANNOTATION_VALUE">
								<!-- copy the PCDATA -->
                                <!-- changed -->
								<xsl:apply-templates select="text()"/>
							</xsl:element>
						</xsl:element>
					</xsl:element>
				</xsl:for-each>
			</xsl:element>
		</xsl:for-each>
	</xsl:template>
	<!-- added in revision 1 -->
	<xsl:template name="makeLinguisticTypes">
		<xsl:for-each select="//tier">
			<xsl:sort select="@category"/>
			<xsl:if test="not(@category=following-sibling::*/@category)">
				<xsl:element name="LINGUISTIC_TYPE">
					<xsl:if test="@type='a'">
						<xsl:attribute name="CONSTRAINTS">Time_Subdivision</xsl:attribute>
					</xsl:if>
					<xsl:attribute name="GRAPHIC_REFERENCES">false</xsl:attribute>
					<xsl:attribute name="LINGUISTIC_TYPE_ID"><xsl:value-of select="@category"></xsl:value-of></xsl:attribute>
					<xsl:attribute name="TIME_ALIGNABLE">true</xsl:attribute>
				</xsl:element>
			</xsl:if>
		</xsl:for-each>
		<LOCALE COUNTRY_CODE="EN" LANGUAGE_CODE="en"/>
		<CONSTRAINT DESCRIPTION="Time subdivision of parent annotation&apos;s time interval, no time gaps allowed within this interval" STEREOTYPE="Time_Subdivision"/>
		<CONSTRAINT DESCRIPTION="Symbolic subdivision of a parent annotation. Annotations refering to the same parent are ordered" STEREOTYPE="Symbolic_Subdivision"/>
		<CONSTRAINT DESCRIPTION="1-1 association with a parent annotation" STEREOTYPE="Symbolic_Association"/>
	</xsl:template>
</xsl:stylesheet>
