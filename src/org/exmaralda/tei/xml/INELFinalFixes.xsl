<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:variable name="TOKENIZE_REGEX">[=\-]</xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- mb, mp, mc, ge, gr, and gg. The last one is optional, if it matters for the processing - only 2 corpora use it. -->
    <!-- 
        This is my mistake - we don't need to break words into morphemes on the mc layer for Tsakorpus purposes. Could you remove it from the list?
    -->
    <xsl:template match="tei:spanGrp[
        @type='mb' or @type='mp' or @type='ge' or
        @type='gr' or @type='gg'
        ]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="@from"/>
            <xsl:attribute name="to" select="@from"/>
            <xsl:variable name="AB_ID" select="ancestor::tei:annotationBlock/@xml:id"/>
            <xsl:variable name="COUNT" select="count(preceding-sibling::tei:span) + 1"/>
            <xsl:variable name="TYPE" select="parent::tei:spanGrp/@type"/>
            <xsl:for-each select="tokenize(text(), $TOKENIZE_REGEX)">
                <tei:span>
                    <xsl:choose>
                        <xsl:when test="$TYPE='mb'">
                            <xsl:attribute name="xml:id" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                            
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="from" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                            
                            <xsl:attribute name="to" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                                                        
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="."/>
                </tei:span>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    
    <!-- Sentence-level annotations (tier types ref, st, stl, ts, fe, fr, ltr, nt in the example, often there are others) refer to words:
        <span from="TIE4.e0.w" to="TIE4.e1.2.1">ASS_ChND_190725_Batu_conv.ASS.001 (001)</span>
            ...when they should refer to the segment instead:
        <span from="TIE4.u1" to="TIE4.u1">ASS_ChND_190725_Batu_conv.ASS.001 (001)</span>
    -->
    <xsl:template match="tei:spanGrp[
        @type='ref' or @type='st' or @type='stl' or
        @type='ts' or @type='fe' or @type='fr' or
        @type='ltr' or @type='nt' or @type='fr'
        ]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="id(@from)/ancestor::tei:seg[1]/@xml:id"/>
            <xsl:attribute name="to" select="id(@to)/ancestor::tei:seg[1]/@xml:id"/>
            <xsl:apply-templates select="@*[not(name()='from' or name()='to')]|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- incident nodes are lacking ids:
        <incident><desc>ChND:</desc></incident>
        The old converter created them:
        <incident xml:id="inc7"><desc>ChND:</desc></incident> 
    -->    
    <xsl:template match="tei:incident[not(@xml:id)]">
        <xsl:copy>
            <xsl:attribute name="xml:id" select="generate-id(.)"/>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>        
    </xsl:template>
        
    

    
</xsl:stylesheet>