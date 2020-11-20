<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"    
    exclude-result-prefixes="xs"
    version="2.0">
    
    
    <!-- if this parameter is set to TRUE, XPointers will be used instead of IDREFs -->
    <xsl:param name="USE_XPOINTER">FALSE</xsl:param>
    <xsl:param name="SPAN_GRP_TYPE">.*</xsl:param>
    
    <xsl:variable name="XPOINTER_HASH">
        <xsl:choose>
            <xsl:when test="$USE_XPOINTER='TRUE'">#</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>            
    </xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp">
        <xsl:copy>
            <xsl:attribute name="subtype">time-based</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp[matches(@type, $SPAN_GRP_TYPE)]/tei:span[@from or @to]">
        <!-- the current value of the from attribute -->
        <xsl:variable name="FROM_SOURCE" select="substring-after(@from, $XPOINTER_HASH)"/>
        <!-- the element that it refers to -->
        <xsl:variable name="FROM_REF_ELEMENT" select="//*[@xml:id=$FROM_SOURCE]/name()"/>
        <xsl:variable name="FROM_TARGET">
            <xsl:choose>                
                <xsl:when test="$FROM_REF_ELEMENT='when'">
                    <!-- it already points to a when element -->
                    <xsl:value-of select="$FROM_SOURCE"/>
                </xsl:when>
                <xsl:when test="//*[@xml:id=$FROM_SOURCE]/preceding-sibling::tei:anchor">
                    <xsl:value-of select="//*[@xml:id=$FROM_SOURCE]/preceding-sibling::tei:anchor[1]/@synch"/>                    
                </xsl:when>
                <xsl:when test="//*[@xml:id=$FROM_SOURCE]/ancestor-or-self::*[@start]">
                    <xsl:value-of select="//*[@xml:id=$FROM_SOURCE]/ancestor-or-self::*[@start]/@start"/>                                        
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="TO_SOURCE" select="substring-after(@to, $XPOINTER_HASH)"/>
        <xsl:variable name="TO_REF_ELEMENT" select="//*[@xml:id=$TO_SOURCE]/name()"/>
        <xsl:variable name="TO_TARGET">
            <xsl:choose>                
                <xsl:when test="$TO_REF_ELEMENT='when'">
                    <!-- it already points to a when element -->
                    <xsl:value-of select="$TO_SOURCE"/>
                </xsl:when>
                <xsl:when test="//*[@xml:id=$TO_SOURCE]/following-sibling::tei:anchor">
                    <xsl:value-of select="//*[@xml:id=$TO_SOURCE]/following-sibling::tei:anchor[1]/@synch"/>                    
                </xsl:when>
                <xsl:when test="//*[@xml:id=$TO_SOURCE]/ancestor-or-self::*[@end]">
                    <xsl:value-of select="//*[@xml:id=$TO_SOURCE]/ancestor-or-self::*[@end]/@end"/>                                        
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        
        <xsl:copy>
            <xsl:attribute name="from" select="concat($XPOINTER_HASH, $FROM_TARGET)"/>
            <xsl:attribute name="to" select="concat($XPOINTER_HASH, $TO_TARGET)"/>            
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    
</xsl:stylesheet>