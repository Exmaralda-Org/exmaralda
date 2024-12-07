<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes" method="xml"/>
    <xsl:template match="/ | @* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="//nts[text()='(' and following-sibling::*[1][self::nts and text()='('] and following-sibling::*[2][self::ats and starts-with(text(),'unv')] and following-sibling::*[3][self::nts and text()=')']  and following-sibling::*[4][self::nts and text()=')'] ]">
        <xsl:element name="ts">
            <xsl:attribute name="n">HIAT:w</xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="following-sibling::*[2]/@id"/></xsl:attribute>
            <xsl:attribute name="s"><xsl:value-of select="following-sibling::*[2]/@s"/></xsl:attribute>
            <xsl:attribute name="e"><xsl:value-of select="following-sibling::*[2]/@e"/></xsl:attribute>
            <xsl:text>xxx</xsl:text>
        </xsl:element>    
    </xsl:template>
    
    <xsl:template match="//nts[text()='(' and preceding-sibling::*[1][self::nts and text()='('] and following-sibling::*[1][self::ats and starts-with(text(),'unv')] and following-sibling::*[2][self::nts and text()=')']  and following-sibling::*[3][self::nts and text()=')'] ]">
        <!-- do nothing -->        
    </xsl:template>
        
    <xsl:template match="//ats[starts-with(text(),'unv') and preceding-sibling::*[1][self::nts and text()='('] and preceding-sibling::*[1][self::nts and text()='('] and following-sibling::*[1][self::nts and text()=')']  and following-sibling::*[2][self::nts and text()=')'] ]">
        <!-- do nothing -->        
    </xsl:template>

    <xsl:template match="//nts[text()=')' and following-sibling::*[1][self::nts and text()=')'] and preceding-sibling::*[1][self::ats and starts-with(text(),'unv')] and preceding-sibling::*[2][self::nts and text()='(']  and preceding-sibling::*[3][self::nts and text()='('] ]">
        <!-- do nothing -->                
    </xsl:template>
        
    <xsl:template match="//nts[text()=')' and preceding-sibling::*[1][self::nts and text()=')'] and preceding-sibling::*[2][self::ats and starts-with(text(),'unv')] and preceding-sibling::*[3][self::nts and text()='(']  and preceding-sibling::*[4][self::nts and text()='('] ]">
        <!-- do nothing -->                
    </xsl:template>
    
</xsl:stylesheet>
