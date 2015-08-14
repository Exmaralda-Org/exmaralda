<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text" encoding="UTF-8"/>
	<!-- match any element in the document -->
	<xsl:template match="/">
			<xsl:apply-templates select="list-transcription/list-body/speaker-contribution"/>
	</xsl:template>
	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution">
                            <xsl:variable name="id">
                                    <xsl:value-of select="@speaker"/>
                            </xsl:variable>
                            <xsl:value-of select="//speaker[@id = $id]/abbreviation"/>
                            <xsl:text>: </xsl:text>					 
                            <xsl:apply-templates select="main"/>
		<xsl:text>&#x000D;</xsl:text>		
	</xsl:template>
	<!-- for the mains... -->
	<xsl:template match="main">
		<xsl:apply-templates/>
	</xsl:template>
</xsl:stylesheet>
