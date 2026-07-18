<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:tei="http://www.tei-c.org/ns/1.0"    
    exclude-result-prefixes="xs math"
    version="3.0">
    
    <xsl:variable name="ANCHOR_MAPPING">
        <problems>
           <xsl:for-each select="//*[@synch and not(@synch = //@xml:id)]">
               <problem>
                   <xsl:attribute name="origin" select="@synch"/>
                   <xsl:attribute name="preceding" select="preceding-sibling::*[@synch][1]/@synch"/>
                   <xsl:attribute name="following" select="following-sibling::*[@synch][1]/@synch"/>
               </problem>
           </xsl:for-each>
        </problems>
    </xsl:variable>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[@synch and not(@synch = //@xml:id)]"/>
    <xsl:template match="tei:span[not(@to=//@xml:id)]">
        <xsl:variable name="FROM" select="@from"/>
        <xsl:variable name="NEXT_TO" select="following-sibling::tei:span[@to=//@xml:id][1]/@to"/>
        <!-- eat em up -->
        <tei:span>
            <xsl:attribute name="from" select="$FROM"/>
            <xsl:attribute name="to" select="$NEXT_TO"/>
            <xsl:value-of select="text()"/>
            <xsl:for-each select="following-sibling::tei:span[@to=$NEXT_TO or following-sibling::tei:span[@to=$NEXT_TO]]">
                <xsl:value-of select="text()"/>
            </xsl:for-each>
        </tei:span>
    </xsl:template>
    
    <xsl:template match="tei:span[not(@from=//@xml:id)]"/>
        
    
    
</xsl:stylesheet>