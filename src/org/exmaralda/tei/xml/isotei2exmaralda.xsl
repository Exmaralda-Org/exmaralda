<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="TRANSCRIPTION_SYSTEM">
        <!-- <transcriptionDesc ident="cGAT" version="2014"> -->
        <xsl:choose>
            <xsl:when test="//tei:transcriptionDesc/@ident">
                <xsl:value-of select="//tei:transcriptionDesc/@ident"/>
            </xsl:when>
            <xsl:otherwise>GENERIC</xsl:otherwise>
        </xsl:choose>
    </xsl:param>
    
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name>EXMARaLDA DemoKorpus</project-name>
                    <transcription-name>Anne Will: Halbes Wahlrecht</transcription-name>
                    <referenced-file url="AnneWill.mp4"/>
                    <referenced-file url="AnneWill.wav"/>
                    <referenced-file url="AnneWill.mpg"/>
                    <ud-meta-information>
                        <ud-information attribute-name=""/>
                    </ud-meta-information>
                    <comment/>
                    <transcription-convention>HIAT (simplified)</transcription-convention>
                </meta-information>
                <speakertable>
                </speakertable>
            </head>
            <basic-body>
                <xsl:for-each-group select="//tei:annotationBlock" group-by="@who">
                    <tier type="t" category="v">
                        <xsl:attribute name="id" select="concat('TIE_V_', current-grouping-key())"/>
                        <xsl:for-each select="current-group()">
                            <xsl:apply-templates select="descendant::tei:u"/>
                        </xsl:for-each>
                    </tier>
                </xsl:for-each-group>
            </basic-body>
        </basic-transcription>                
    </xsl:template>
    
    <xsl:template match="tei:seg/tei:anchor[following-sibling::tei:anchor]">
        <event>
            <xsl:attribute name="start" select="@synch"/>
            <xsl:attribute name="end" select="following-sibling::tei:anchor[1]/@synch"/>
            <xsl:value-of select="following-sibling::text()[1]"/>
        </event>
    </xsl:template>
    
    <xsl:template match="tei:seg/text()"/>
    
    
    
    
    
    
    
    
</xsl:stylesheet>