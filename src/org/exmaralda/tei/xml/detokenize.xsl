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

    <xsl:template match="tei:w">
        <xsl:apply-templates/>
        <xsl:choose>
            <xsl:when test="x"></xsl:when>
            <xsl:otherwise><xsl:text> </xsl:text></xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template match="tei:seg//descendant::tei:pause">
        <xsl:choose>
            <!-- <pause xml:id="p495" rend="(0.71)" dur="PT0.71S" start="TLI_950" end="TLI_951"/>  -->
            <xsl:when test="@rend"><xsl:value-of select="@rend"/></xsl:when>
            <xsl:otherwise>
                <xsl:variable name="DURATION" select="substring-after(substring-before(@dur,'S'), 'PT')"/>
                <xsl:choose>
                    <xsl:when test="$TRANSCRIPTION_SYSTEM='cGAT' or $TRANSCRIPTION_SYSTEM='GAT'">(<xsl:value-of select="$DURATION"/>)</xsl:when>
                    <xsl:when test="$TRANSCRIPTION_SYSTEM='HIAT'">((<xsl:value-of select="$DURATION"/>s))</xsl:when>
                    <xsl:otherwise>(<xsl:value-of select="$DURATION"/>)</xsl:otherwise>
                </xsl:choose>                
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- <vocal xml:id="b1">
        <desc rend="°h">short breathe in</desc>
    </vocal> -->
    <xsl:template match="tei:seg/descendant::tei:vocal | tei:seg/descendant::tei:incident">
        <xsl:text>((</xsl:text>
        <xsl:choose>
            <xsl:when test="tei:desc/@rend"><xsl:value-of select="tei:desc/@rend"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="tei:desc"/></xsl:otherwise>
        </xsl:choose>
        <xsl:text>))</xsl:text>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each-group select="tei:span" group-by="concat(@from, '_____', @to)">
                <!-- 
                    <tei:span from="TLI_1" to="TLI_2_1">so</tei:span>
                    <tei:span from="TLI_1" to="TLI_2_1">ich</tei:span>
                -->
                <tei:span>
                    <xsl:attribute name="from" select="substring-before(current-grouping-key(), '_____')"/>
                    <xsl:attribute name="to" select="substring-after(current-grouping-key(), '_____')"/>
                    <xsl:apply-templates select="current-group()"/>
                </tei:span>
            </xsl:for-each-group>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:span">
        <!-- <xsl:value-of select="text()"/><xsl:text> </xsl:text> -->
        <xsl:apply-templates/>
        <xsl:if test="not(parent::tei:span and not(following-sibling::*))">
            <xsl:text> </xsl:text>
        </xsl:if>
    </xsl:template>
    
    
    
    
    
    
    
    
</xsl:stylesheet>