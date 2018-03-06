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
    
    <xsl:template match="annotationBlock">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
            <xsl:if test="u/p[text()='[']">
                <spanGrp type="transfer">
                    <xsl:for-each select="u/p[text()='[']">
                        <span>
                            <xsl:attribute name="from"><xsl:text>#</xsl:text><xsl:value-of select="current()/following-sibling::w[1]/@xml:id"/></xsl:attribute>
                            <xsl:attribute name="to"><xsl:text>#</xsl:text><xsl:value-of select="current()/following-sibling::p[text()=']'][1]/preceding-sibling::w[1]/@xml:id"/></xsl:attribute>
                            <xsl:text>en</xsl:text>
                        </span>
                    </xsl:for-each>
                </spanGrp>
            </xsl:if>
            <spanGrp type="normalisation">
                <xsl:for-each select="u/w[matches(text(),'[A-ZÄÖÜ]{2,}')]">
                <!-- <xsl:for-each select="u/w"> -->
                    <span>
                        <xsl:attribute name="from"><xsl:text>#</xsl:text><xsl:value-of select="@xml:id"/></xsl:attribute>
                        <xsl:attribute name="to"><xsl:text>#</xsl:text><xsl:value-of select="@xml:id"/></xsl:attribute>
                        <xsl:value-of select="lower-case(text())"/>
                    </span>
                </xsl:for-each>
            </spanGrp>
        </xsl:copy>
        
        
    </xsl:template>
    
    <xsl:template match="p[text()='[' or text()=']']"/>
    
</xsl:stylesheet>