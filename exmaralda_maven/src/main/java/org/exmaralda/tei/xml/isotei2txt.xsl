<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0" 
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
         <xsl:apply-templates select="//tei:body/*"/>
    </xsl:template>
    
    <xsl:template match="tei:annotationBlock">
        <xsl:variable name="SPEAKER_ID" select="@who"/>
            <xsl:value-of select="//tei:person[@xml:id=$SPEAKER_ID]/@n"/>
            <xsl:text>: </xsl:text>
            <xsl:apply-templates select="tei:u/child::tei:*"/>
            <xsl:text>&#10;</xsl:text>
    </xsl:template>
    
    <xsl:template match="tei:u"/>
    
    <xsl:template match="tei:w">
         <xsl:value-of select="."/><xsl:text> </xsl:text>
    </xsl:template>
    
    <xsl:template match="tei:pause">
            <xsl:choose>
                <xsl:when test="@rend">
                    <xsl:value-of select="@rend"/>
                    <xsl:text> </xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:choose>
                        <xsl:when test="@type='micro'">(.) </xsl:when>
                        <xsl:otherwise><xsl:text>(</xsl:text><xsl:value-of select="substring-before(substring-after(@dur, 'PT'), 'S')"/><xsl:text>) </xsl:text></xsl:otherwise>
                    </xsl:choose>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="not(ancestor::tei:u)"><xsl:text>&#10;</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="tei:desc">
        <xsl:choose>
            <xsl:when test="@rend">
                <xsl:value-of select="@rend"/>
                <xsl:text> </xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>((</xsl:text>
                <xsl:value-of select="."/>
                <xsl:text>)) </xsl:text>                    
            </xsl:otherwise>
        </xsl:choose>
        <xsl:if test="not(ancestor::tei:u)"><xsl:text>&#10;</xsl:text></xsl:if>
        
    </xsl:template>

</xsl:stylesheet>