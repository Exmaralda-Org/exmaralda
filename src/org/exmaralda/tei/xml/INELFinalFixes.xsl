<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:variable name="TOKENIZE_REGEX">[=\-]</xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- mb, mp, mc, ge, gr, and gg. The last one is optional, if it matters for the processing - only 2 corpora use it. -->
    <xsl:template match="tei:spanGrp[
        @type='mb' or @type='mp' or @type='mc' or @type='ge' or
        @type='gr' or @type='gg'
        ]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="@from"/>
            <xsl:attribute name="to" select="@from"/>
            <xsl:variable name="COUNT" select="count(preceding-sibling::tei:span) + 1"/>
            <xsl:for-each select="tokenize(text(), $TOKENIZE_REGEX)">
                <tei:span>
                    <xsl:attribute name="xml:id" select="concat('mb', $COUNT, '_', position())"/>
                    <xsl:value-of select="."/>
                </tei:span>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    

    
</xsl:stylesheet>