<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:string="https://corpora.uni-hamburg.de/xmlns/string">
    <xsl:output method="text" encoding="UTF-8" omit-xml-declaration="yes"/>
    
    <xsl:variable name="base-fields" select="('file', 'xpath', 'communication', 'speaker', 'left_context', 'match', 'right_context')" as="xs:string+"/>
    <xsl:variable name="meta-fields" select="search-result-list/search-result[1]/meta/@name" as="xs:string*"/>
    <xsl:variable name="analysis-fields" select="search-result-list/analyses/analysis/@name" as="xs:string*"/>
    
    <xsl:variable name="NEWLINE"><xsl:text>
</xsl:text>
    </xsl:variable>
    
    <xsl:template match="/">
        <!-- first line of CSV - keys -->
        <xsl:value-of select="string-join(($base-fields, $meta-fields, $analysis-fields), ',')"/>
        <xsl:value-of select="$NEWLINE"/>
        
        <!-- further lines if CSV - values -->
        <xsl:for-each select="//search-result">
            <xsl:value-of select="string:glue-as-csv-line(
                    (
                    locator/@file,
                    locator/@xpath,
                    @communication,
                    @speaker,
                    substring(left-context,string-length(left-context)-40),
                    match,
                    substring(right-context,1,40),
                    meta/text(),
                    data[position()&gt;3]
                    )
                , '')"/>
            <xsl:value-of select="$NEWLINE"/>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:function name="string:glue-as-csv-line" as="xs:string">
        <xsl:param name="items" as="xs:string*"/>
        <xsl:param name="result" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="$items[1]">
                <xsl:value-of select="string:glue-as-csv-line(subsequence($items,2), concat($result, ','[not($result='')], '&quot;'[contains($items[1], ',')], replace($items[1], '&quot;', '&amp;quot;'), '&quot;'[contains($items[1], ',')]))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$result"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
</xsl:stylesheet>
