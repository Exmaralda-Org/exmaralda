<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    <xsl:variable name="CODES_TO_NAMES">
        <codes2Names>
            <mapping code="s" name=""/>
            <mapping code="g" name=""/>
            <mapping code="n" name=""/>
            <mapping code="v"  name=""/>
            <mapping code="ld"  name=""/>
            <mapping code="nl"  name=""/>
        </codes2Names>
    </xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp[@type='comment']">
        <xsl:for-each-group select="tei:span" group-by="@type">
            <tei:spanGrp>
                <xsl:attribute name="type" select="$CODES_TO_NAMES/descendant::mapping[@code=current-grouping-key()]/@name"/>
                <xsl:apply-templates select="current-group()"/>
            </tei:spanGrp>
        </xsl:for-each-group>
    </xsl:template>
    
    <xsl:template match="tei:span/@type"/>
    
    
    
    
</xsl:stylesheet>