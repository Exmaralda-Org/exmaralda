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
    
    <xsl:template match="tei:when[not(@interval) and not(@since)]">
        <xsl:variable name="ID" select="@xml:id"/>
        <xsl:choose>
            <xsl:when test="count(//tei:anchor[@synch=$ID])&gt;1">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                                
            </xsl:when>
            <xsl:when test="//tei:when[@since=$ID]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                                                
            </xsl:when>
            <xsl:when test="//*[@start=$ID or @end=$ID or @from=$ID or @to=$ID or contains(@corresp, $ID)]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                                                
            </xsl:when>
            <xsl:otherwise/>            
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:anchor">
        <xsl:variable name="ID" select="@synch"/>
        <xsl:choose>
            <xsl:when test="//tei:when[@xml:id=$ID][@interval or @since]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                                 
            </xsl:when>
            <xsl:when test="count(//tei:anchor[@synch=$ID])&lt;1">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>
            </xsl:when>
            <xsl:when test="//tei:span[@from=$ID or @to=$ID]">
                <xsl:copy>
                    <xsl:apply-templates select="@*|node()"/>
                </xsl:copy>                
            </xsl:when>
            <xsl:otherwise/>          
        </xsl:choose>
    </xsl:template>
    
    <!-- we need to keep the redundant <anchor>s in the <seg>s -->
    <xsl:template match="//tei:seg/*[1][@synch] | //tei:seg/*[last()][@synch]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>                
    </xsl:template>    
    
    
</xsl:stylesheet>