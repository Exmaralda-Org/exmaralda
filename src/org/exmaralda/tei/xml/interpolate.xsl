<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="TIMELINE_START" select="//tei:when[1]/@xml:id"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:when[not(@interval) and preceding-sibling::tei:when[@interval] and following-sibling::tei:when[@interval]]">
        <xsl:variable name="TIME_BEFORE" select="preceding-sibling::tei:when[@interval][1]/@interval"/>
        <xsl:variable name="TIME_AFTER" select="following-sibling::tei:when[@interval][1]/@interval"/>
        <xsl:variable name="POSITION_BEFORE" select="count(preceding-sibling::tei:when[@interval][1]/preceding-sibling::tei:when)"/>
        <xsl:variable name="POSITION_AFTER" select="count(following-sibling::tei:when[@interval][1]/preceding-sibling::tei:when)"/>
        <xsl:variable name="POSITION_THIS" select="count(preceding-sibling::tei:when)"/>
        <xsl:variable name="INTERPOLATED_TIME" select="$TIME_BEFORE + ($TIME_AFTER - $TIME_BEFORE) * ($POSITION_THIS - $POSITION_BEFORE) div ($POSITION_AFTER - $POSITION_BEFORE)"/>
        <!-- <xsl:comment>
            Interpolated : <xsl:value-of select="$TIME_BEFORE"/> (<xsl:value-of select="$POSITION_BEFORE"/>) / <xsl:value-of select="$TIME_AFTER"/> (<xsl:value-of select="$POSITION_AFTER"/>)/ <xsl:value-of select="$POSITION_THIS"/> 
        </xsl:comment> -->
        <xsl:copy>
            <xsl:attribute name="xml:id" select="@xml:id"/>
            <xsl:attribute name="interval" select="$INTERPOLATED_TIME"/>
            <xsl:attribute name="since" select="$TIMELINE_START"/>
        </xsl:copy>
    </xsl:template>
        
</xsl:stylesheet>