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

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:seg/tei:seg">
        <!-- select all but the ending anchor before the utterance etc. end symbol is inserted -->
        <xsl:apply-templates select="*[not(self::tei:anchor and not(following-sibling::*))]"/>
        <!-- insert utterance etc. end symbol -->
        <xsl:choose>
            <xsl:when test="$TRANSCRIPTION_SYSTEM='HIAT'">
                <tei:pc>
                    <xsl:choose>
                        <xsl:when test="@subtype='declarative'">. </xsl:when>
                        <xsl:when test="@subtype='interrogative'">? </xsl:when>
                        <xsl:when test="@subtype='exclamative'">! </xsl:when>
                        <xsl:when test="@subtype='interrupted'">&#x2026; </xsl:when>
                        <xsl:when test="@subtype='not_classified'">&#x02D9; </xsl:when>                        
                    </xsl:choose>
                </tei:pc>
            </xsl:when>
        </xsl:choose>      
        <!-- now select the ending anchor -->
        <xsl:apply-templates select="*[self::tei:anchor and not(following-sibling::*)]"/>        
    </xsl:template>
    
    
    
</xsl:stylesheet>