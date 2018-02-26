<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <folker-transcription>
            <head></head>
            <xsl:call-template name="MAKE_SPEAKERTABLE"/>
            <recording>
                <xsl:attribute name="path"><xsl:value-of select="//media/@url"/></xsl:attribute>
            </recording>
            <xsl:call-template name="MAKE_TIMELINE"/>
            <xsl:apply-templates select="//annotationBlock"/>
        </folker-transcription>
    </xsl:template>
    
    <xsl:template match="annotationBlock">
        <contribution>
            <xsl:attribute name="speaker-reference" select="@who"/>
            <xsl:attribute name="start-reference" select="@start"/>
            <xsl:attribute name="end-reference" select="@end"/>
            <xsl:attribute name="parse-level">2</xsl:attribute>
            <xsl:attribute name="id" select="@xml:id"/>
            <xsl:apply-templates select="u"/>
        </contribution>
    </xsl:template>
    
    <xsl:template match="w">
        <xsl:variable name="WORD_ID" select="@xml:id"/>
        <w>
            <xsl:attribute name="id" select="@xml:id"/>
            <xsl:if test="ancestor::annotationBlock/descendant::spanGrp[@type='normalisation']">
                <xsl:attribute name="n">
                    <xsl:for-each select="ancestor::annotationBlock/descendant::spanGrp[@type='normalisation']/span[@from=concat('#',$WORD_ID)]">
                        <xsl:value-of select="text()"/>
                    </xsl:for-each>
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="text()"/>            
        </w>                    
    </xsl:template>
    
    <xsl:template match="p">
        <p>
            <xsl:attribute name="id" select="@xml:id"/>
            <xsl:value-of select="text()"/>            
        </p>                    
    </xsl:template>
    
    <xsl:template name="MAKE_SPEAKERTABLE">
        <speakers>
            <xsl:for-each select="//person">
                <speaker>
                    <xsl:attribute name="speaker-id" select="@n"/>
                    <name></name>
                </speaker>
            </xsl:for-each>
        </speakers>        
    </xsl:template>
    
    <xsl:template name="MAKE_TIMELINE">
        <timeline>
            <timepoint timepoint-id="TLI_0" absolute-time="0.0"/>
            <xsl:for-each select="//when[@interval]">
                <timepoint>
                    <xsl:attribute name="timepoint-id" select="@xml:id"/>
                    <xsl:attribute name="absolute-time" select="@interval"/>
                </timepoint>
            </xsl:for-each>            
        </timeline>
    </xsl:template>
</xsl:stylesheet>