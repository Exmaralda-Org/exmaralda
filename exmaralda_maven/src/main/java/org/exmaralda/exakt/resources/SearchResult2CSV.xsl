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
        <!-- <xsl:value-of select="$base-fields"/> -->
        
        <!-- first line of CSV - keys -->
        <xsl:value-of select="string-join(($base-fields, $meta-fields, $analysis-fields), ',')"/>
        <xsl:value-of select="$NEWLINE"/>
        
        <!-- further lines if CSV - values -->
        <xsl:for-each select="//search-result">
            
            <!-- 
                This is my (TS) awkward fix for issue #277:
                If left or right context is empty, make it non-empty, i.e. a space
            -->
            <xsl:variable name="LEFT_CONTEXT" select="substring(left-context,string-length(left-context)-40)"/>
            <xsl:variable name="LEFT_CONTEXT_FIX">
                <xsl:choose>
                    <xsl:when test="string-length($LEFT_CONTEXT) &gt; 0"><xsl:value-of select="$LEFT_CONTEXT"/></xsl:when>
                    <xsl:otherwise><xsl:text> </xsl:text></xsl:otherwise>
                </xsl:choose>
            </xsl:variable> 
            <xsl:variable name="RIGHT_CONTEXT" select="substring(right-context,1,40)"/>
            <xsl:variable name="RIGHT_CONTEXT_FIX">
                <xsl:choose>
                    <xsl:when test="string-length($RIGHT_CONTEXT) &gt; 0"><xsl:value-of select="$RIGHT_CONTEXT"/></xsl:when>
                    <xsl:otherwise><xsl:text> </xsl:text></xsl:otherwise>
                </xsl:choose>
            </xsl:variable> 
            <!-- end of my fix -->
            
            
            
            <xsl:value-of select="string:glue-as-csv-line(
                    (
                    locator/@file,
                    locator/@xpath,
                    @communication,
                    @speaker,
                    $LEFT_CONTEXT_FIX,
                    match,
                    $RIGHT_CONTEXT_FIX,
                    meta/text(),
                    data[position()&gt;3]
                    )
                , '')"/>
            <xsl:value-of select="$NEWLINE"/>
        </xsl:for-each>
    </xsl:template>
    
    <!-- issue #277 : this is maybe not doing what it is supposed to do when left_context ist empty -->
    <xsl:function name="string:glue-as-csv-line" as="xs:string">
        <xsl:param name="items" as="xs:string*"/>
        <xsl:param name="result" as="xs:string"/>
        <xsl:choose>
            <!-- issue #277 : I would fix it here if only I understood what an expression like ','[not($result='')] means -->
            <xsl:when test="$items[1]">
                <!-- issue #277 : this is maybe not doing what it is supposed to do when left_context ist empty -->
                <xsl:value-of select="string:glue-as-csv-line(subsequence($items,2), 
                    concat($result, 
                        ','[not($result='')], 
                        '&quot;'[contains($items[1], ',')], 
                        replace($items[1], '&quot;', '&amp;quot;'), 
                        '&quot;'[contains($items[1], ',')])
                    )"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$result"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
</xsl:stylesheet>
