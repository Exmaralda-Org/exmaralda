<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"    
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- 
        Converting from ISO/TEI to EXB is done in several steps:
        (1) move <w> attributes to <span> elements : attributes2spans.xsl
        ==> (2) transform token references in <span> to time references : token2timeSpanReferences.xsl
        (3) turn explicit token markup into implicit charachter markup : detokenize.xsl
        (4) transform this TEI document to EXB : isotei2exmaralda
        The assumption is that the input to (1) conforms to ZuMult's ISO/TEI schema
        Intermediate steps will still conform to ISO/TEI in general.
    -->
    
    
    <!-- if this parameter is set to TRUE, XPointers will be used instead of IDREFs -->
    <xsl:param name="USE_XPOINTER">FALSE</xsl:param>
    <xsl:param name="SPAN_GRP_TYPE">.*</xsl:param>
    
    <xsl:variable name="XPOINTER_HASH">
        <xsl:choose>
            <xsl:when test="$USE_XPOINTER='TRUE'">#</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>            
    </xsl:variable>
    
    <xsl:variable name="TOKEN_TIME_MAP">
        <map>
            <xsl:for-each select="//tei:seg/*[@xml:id]">
                <token>
                    <xsl:attribute name="id" select="@xml:id"/>
                    <xsl:attribute name="synch-before" select="preceding-sibling::tei:anchor[1]/@synch"/>
                    <xsl:attribute name="synch-after" select="following-sibling::tei:anchor[1]/@synch"/>
                </token>
            </xsl:for-each>
            <xsl:for-each select="//tei:when">
                <token>
                    <xsl:attribute name="id" select="@xml:id"/>
                    <xsl:attribute name="synch-before" select="@xml:id"/>                    
                    <xsl:attribute name="synch-after" select="@xml:id"/>                    
                </token>                
            </xsl:for-each>
        </map>
    </xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp">
        <xsl:copy>
            <xsl:attribute name="subtype">time-based</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp[matches(@type, $SPAN_GRP_TYPE)]/tei:span[@from or @to]">
        <!-- the current value of the from attribute -->
        <xsl:variable name="FROM_SOURCE" select="substring-after(@from, $XPOINTER_HASH)"/>
        <!-- get the corresponding anchor -->
        <xsl:variable name="FROM_TARGET" select="$TOKEN_TIME_MAP/*/token[@id=$FROM_SOURCE]/@synch-before"/>
        
        <xsl:variable name="TO_SOURCE" select="substring-after(@to, $XPOINTER_HASH)"/>        
        <xsl:variable name="TO_TARGET" select="$TOKEN_TIME_MAP/*/token[@id=$TO_SOURCE]/@synch-after"/>
        
        
        <xsl:copy>
            <xsl:attribute name="from" select="concat($XPOINTER_HASH, $FROM_TARGET)"/>
            <xsl:attribute name="to" select="concat($XPOINTER_HASH, $TO_TARGET)"/>            
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    
</xsl:stylesheet>