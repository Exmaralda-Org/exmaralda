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
    
    <xsl:template match="tei:span[@from or @to]">
        <!-- the current value of the from attribute -->
        <xsl:variable name="FROM_SOURCE" select="substring-after(@from, '#')"/>
        <!-- the element that it refers to -->
        <xsl:variable name="FROM_REF_ELEMENT" select="//*[@xml:id=$FROM_SOURCE]/name()"/>
        <xsl:variable name="FROM_TARGET">
            <xsl:choose>
                <xsl:when test="$FROM_REF_ELEMENT='w' or not(ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat('#',$FROM_SOURCE)]/following-sibling::tei:w)">
                    <xsl:value-of select="$FROM_SOURCE"/>
                </xsl:when>
                <!-- <anchor synch="#T342"/> -->
                <xsl:otherwise>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat('#',$FROM_SOURCE)]/following-sibling::tei:w[1]/@xml:id"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="TO_SOURCE" select="substring-after(@to, '#')"/>
        <xsl:variable name="TO_REF_ELEMENT" select="//*[@xml:id=$TO_SOURCE]/name()"/>
        <xsl:variable name="TO_TARGET">
            <xsl:choose>
                <xsl:when test="$TO_REF_ELEMENT='w' or not(ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat('#',$TO_SOURCE)]/preceding-sibling::tei:w)">
                    <xsl:value-of select="$TO_SOURCE"/>
                </xsl:when>
                <!-- <anchor synch="#T342"/> -->
                <xsl:otherwise>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat('#',$TO_SOURCE)]/preceding-sibling::tei:w[1]/@xml:id"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        

        <xsl:copy>
            <xsl:attribute name="from" select="concat('#', $FROM_TARGET)"/>
            <xsl:attribute name="to" select="concat('#', $TO_TARGET)"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>