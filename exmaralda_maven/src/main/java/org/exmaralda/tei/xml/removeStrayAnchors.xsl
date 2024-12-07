<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:anchor">
        <xsl:variable name="ID" select="@synch"/>
        <xsl:if test="//tei:when[@xml:id=$ID]">
            <xsl:copy>
                <xsl:apply-templates select="@*|node()"/>
            </xsl:copy>                
        </xsl:if>
    </xsl:template>
    
    
</xsl:stylesheet>