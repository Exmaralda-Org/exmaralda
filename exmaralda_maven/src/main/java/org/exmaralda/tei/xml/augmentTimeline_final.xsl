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
    
    
    <!-- 
            <u xml:id="u15">
               <seg xml:id="seg16" type="utterance" subtype="declarative">
                  <tei:anchor synch="T1"/>
                  vom Stuhl gefallen er hat mein Auge verletzt ü/ ü/ über dem Auge.
                  <tei:anchor synch="T19_seg16"/>
               </seg>
               <seg xml:id="seg17" type="utterance"
                  subtype="interrupted">
                  <tei:anchor synch="T19_seg16"/>
                  Die Frau hat das 
                  <anchor synch="T2"/>
                  mit dem… 
                  <tei:anchor synch="T19_seg17"/>
               </seg>
             </u>
    
        In a constellation like the above, we want a new <when xml:id='T19_seg16'> between T1 and T2
    -->
    
    <xsl:template match="tei:when[following-sibling::tei:when]">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
        <xsl:variable name="THIS_ID" select="@xml:id"/>
        <xsl:variable name="NEXT_ID" select="following-sibling::tei:when[1]/@xml:id"/>
        <xsl:variable name="THIS_TIME" select="@interval"/>
        <xsl:variable name="NEXT_TIME" select="following-sibling::tei:when[1]/@interval"/>
        <xsl:variable name="NEXT_ID" select="following-sibling::tei:when[1]/@xml:id"/>
        <xsl:if test="//tei:seg[not(tei:seg)]/tei:anchor[@synch=$THIS_ID 
            and following-sibling::tei:anchor]">
            <xsl:variable name="FOLLOWING_ID" select="//tei:seg[not(tei:seg)]/tei:anchor[@synch=$THIS_ID and following-sibling::tei:anchor][1]/following-sibling::tei:anchor[1]/@synch"/>
            <xsl:if test="not(//tei:when[@xml:id=$FOLLOWING_ID])">
                <xsl:if test="//tei:seg[not(tei:seg) and tei:anchor[1]/@synch=$FOLLOWING_ID and tei:anchor[2]/@synch=$NEXT_ID]">
                    <tei:when>
                        <xsl:attribute name="xml:id" select="$FOLLOWING_ID"/>
                        <xsl:attribute name="interval" select="$THIS_TIME + (($NEXT_TIME - $THIS_TIME) div 2)"></xsl:attribute>
                        <xsl:attribute name="since" select="//tei:when[1]/@xml:id"/>
                    </tei:when>
                </xsl:if>
            </xsl:if>
            
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>