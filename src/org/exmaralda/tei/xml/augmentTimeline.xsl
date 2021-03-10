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
    
    <xsl:template match="tei:when">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
        </xsl:copy>
        <xsl:variable name="ID" select="@xml:id"/>
        <!-- <xsl:variable name="OCCURENCES" select="count(//tei:anchor[@synch=$ID and not(parent::*[self::tei:seg] and position()=last())])"/> -->
        <xsl:variable name="OCCURENCES" select="count(//tei:seg/tei:anchor[@synch=$ID and not(position()=last())])"/>
        <!-- <xsl:variable name="OCCURENCES" select="count(//tei:seg/descendant::tei:anchor[@synch=$ID and not(position()=last())])"/> -->
        <xsl:variable name="NEXT_ID" select="following-sibling::tei:when[1]/@xml:id"/>
        <!-- <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:anchor[@synch=$NEXT_ID and not(parent::*[self::tei:seg] and position()=last())])"/> -->
        <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:seg/tei:anchor[@synch=$NEXT_ID and not(position()=last())])"/> 
<!--        <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:seg/descendant::tei:anchor[@synch=$NEXT_ID and not(position()=last())])"/> -->
        <xsl:variable name="SINCE" select="@since"/>
        <xsl:if test="$OCCURENCES=1 and $NEXT_OCCURENCES&lt;=1">
            <!-- <xsl:for-each select="//tei:anchor[@synch=$ID]/following-sibling::*[@xml:id and not(self::anchor) and not(preceding-sibling::tei:anchor[@synch=$NEXT_ID])]"> -->
            <xsl:for-each select="//tei:anchor[@synch=$ID]/following-sibling::*[@xml:id and not(self::anchor) and not(preceding::tei:anchor[@synch=$NEXT_ID])]">
                <xsl:element name="when" namespace="http://www.tei-c.org/ns/1.0">
                    <xsl:attribute name="xml:id" select="concat($ID, '_', ./@xml:id)"/>
                </xsl:element>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="tei:seg/*[@xml:id and not((preceding-sibling::*[1])[self::tei:anchor])]">
        <!-- watch out! there is confusion as to the direction of preceding schnibbling -->
        <!-- <xsl:variable name="PREV_ANCHOR_ID" select="((preceding-sibling::tei:anchor)[last()])/@synch"/> -->
        <xsl:variable name="PREV_ANCHOR_ID" select="((preceding::tei:anchor)[last()])/@synch"/>
        <!-- <xsl:variable name="PREV_OCCURENCES" select="count(//tei:anchor[@synch=$PREV_ANCHOR_ID and not(parent::*[self::tei:seg] and position()=last())])"/>-->
        <!-- <xsl:variable name="PREV_OCCURENCES" select="count(//tei:seg/tei:anchor[@synch=$PREV_ANCHOR_ID and not(position()=last())])"/> -->
        <xsl:variable name="PREV_OCCURENCES" select="count(//tei:seg/descendant::tei:anchor[@synch=$PREV_ANCHOR_ID and not(position()=last())])"/>
        <!-- <xsl:variable name="NEXT_ANCHOR_ID" select="((following-sibling::tei:anchor)[1])/@synch"/> -->
        <xsl:variable name="NEXT_ANCHOR_ID" select="((following::tei:anchor)[1])/@synch"/>
        <!-- <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:anchor[@synch=$NEXT_ANCHOR_ID and not(parent::*[self::tei:seg] and position()=last())])"/> -->
        <!-- <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:seg/tei:anchor[@synch=$NEXT_ANCHOR_ID and not(position()=last())])"/> -->
        <xsl:variable name="NEXT_OCCURENCES" select="count(//tei:seg/descendant::tei:anchor[@synch=$NEXT_ANCHOR_ID and not(position()=last())])"/>
        <xsl:if test="$PREV_OCCURENCES=1 and $NEXT_OCCURENCES&lt;=1">
            <xsl:element name="anchor" namespace="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="synch" select="concat($PREV_ANCHOR_ID, '_', @xml:id)"/>
            </xsl:element>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
        
        
    </xsl:template>
    
    
</xsl:stylesheet>