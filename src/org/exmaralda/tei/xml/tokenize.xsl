<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    
    <xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:seg/text()">
        <xsl:variable name="SEG_ID" select="parent::tei:seg/@xml:id"/>
        <xsl:analyze-string select="." regex="[\w]+ *">
            <xsl:matching-substring>
                <tei:w>
                    <xsl:attribute name="xml:id" select="concat($SEG_ID, '_w_', position())"/>
                    <xsl:value-of select="normalize-space(.)"/>
                </tei:w>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <tei:pc>
                    <xsl:attribute name="xml:id" select="concat($SEG_ID, '_p_', position())"/>
                    <xsl:value-of select="normalize-space(.)"/>
                </tei:pc>
            </xsl:non-matching-substring>
        </xsl:analyze-string>        
    </xsl:template>
    

    
</xsl:stylesheet>