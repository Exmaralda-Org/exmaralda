<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"    
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- new 25-06-2018 -->
    <!-- if this parameter is set to TRUE, XPointers will be used instead of IDREFs -->
    <xsl:param name="USE_XPOINTER">FALSE</xsl:param>
    <xsl:param name="SPAN_GRP_TYPE">.*</xsl:param>

    <xsl:variable name="XPOINTER_HASH">
        <xsl:choose>
            <xsl:when test="$USE_XPOINTER='TRUE'">#</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>            
    </xsl:variable>
    

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp[matches(@type, $SPAN_GRP_TYPE)]/tei:span[@from or @to]">
        <!-- the current value of the from attribute -->
        <xsl:variable name="FROM_SOURCE" select="substring-after(@from, $XPOINTER_HASH)"/>
        <!-- the element that it refers to -->
        <xsl:variable name="FROM_REF_ELEMENT" select="//*[@xml:id=$FROM_SOURCE]/name()"/>
        <xsl:variable name="FROM_TARGET">
            <xsl:choose>                
                <xsl:when test="$FROM_REF_ELEMENT='w'">
                    <!-- it already points to a w element -->
                    <xsl:value-of select="$FROM_SOURCE"/>
                </xsl:when>
                <!-- <xsl:when test="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$FROM_SOURCE)]/following-sibling::tei:w"> -->
                <xsl:when test="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$FROM_SOURCE)]/following-sibling::*[@xml:id]">
                    <!-- it points to an anchor -->
                    <!-- pick the first w element which follows the anchor with that timepoint id -->
                    <!-- <anchor synch="#T342"/> -->
                    <!-- <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$FROM_SOURCE)]/following-sibling::tei:w[1]/@xml:id"/> -->                    
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[following-sibling::tei:w and @synch=concat($XPOINTER_HASH,$FROM_SOURCE)]/following-sibling::*[@xml:id][1]/@xml:id"/>                    
                </xsl:when>
                <xsl:when test="ancestor::tei:annotationBlock[@start=concat($XPOINTER_HASH,$FROM_SOURCE) and descendant::tei:w]">
                    <!-- it points to the start of an annotation block -->
                    <!-- pick the first w element which is a child of that annotation block -->
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:w[1]/@xml:id"/>  
                </xsl:when>
                <xsl:otherwise>
                    <!-- it points to an anchor, but there is no w element which follows the anchor with that timepoint id -->
                    <xsl:value-of select="$FROM_SOURCE"/>                    
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="TO_SOURCE" select="substring-after(@to, $XPOINTER_HASH)"/>
        <xsl:variable name="TO_REF_ELEMENT" select="//*[@xml:id=$TO_SOURCE]/name()"/>
        <xsl:variable name="TO_TARGET">
            <xsl:choose>
                <xsl:when test="$TO_REF_ELEMENT='w'">
                    <!-- it already points to a w element -->
                    <xsl:value-of select="$TO_SOURCE"/>
                </xsl:when>
                <!-- <xsl:when test="not(ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$TO_SOURCE)]/preceding-sibling::tei:w)"> -->
                <xsl:when test="not(ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$TO_SOURCE)]/preceding-sibling::*[@xml:id])">
                    <!-- it points to a timepoint, but there is no anchor for it, so it may be the end point of the respective annotationBlock? -->
                    <xsl:choose>
                        <xsl:when test="ancestor::tei:annotationBlock[descendant::tei:w]/@end=concat($XPOINTER_HASH, $TO_SOURCE)">
                            <!-- yes, it is, so choose the id of the last word in that annotation block -->
                            <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:w[last()]/@xml:id"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- no, it isn't, so give up -->
                            <xsl:value-of select="$TO_SOURCE"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <!-- <anchor synch="#T342"/> -->
                <xsl:otherwise>
                    <!-- it points to a timepoint -->
                    <!-- pick the rightmost w element which precedes the anchor with that timepoint id -->
                    <!-- <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[@synch=concat($XPOINTER_HASH,$TO_SOURCE)]/preceding-sibling::tei:w[1]/@xml:id"/> -->
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:anchor[preceding-sibling::tei:w and @synch=concat($XPOINTER_HASH,$TO_SOURCE)]/preceding-sibling::*[@xml:id][1]/@xml:id"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        

        <xsl:copy>
            <xsl:attribute name="from" select="concat($XPOINTER_HASH, $FROM_TARGET)"/>
            <xsl:attribute name="to" select="concat($XPOINTER_HASH, $TO_TARGET)"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>