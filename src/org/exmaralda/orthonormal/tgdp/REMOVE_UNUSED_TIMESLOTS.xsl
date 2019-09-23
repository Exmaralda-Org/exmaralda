<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="TIME_SLOT">
        <xsl:variable name="THIS_ID" select="@TIME_SLOT_ID"/>
        <xsl:choose>
            <xsl:when test="//*[@TIME_SLOT_REF1=$THIS_ID] or //*[@TIME_SLOT_REF2=$THIS_ID]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                
            </xsl:when>
            <xsl:otherwise><!-- do nothing --></xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>