<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output encoding="UTF-8" method="text" byte-order-mark="yes"/>
    
    <xsl:variable name="TOTAL_CONTRIBUTIONS" select="count(//speaker-contribution)"/>
    <xsl:variable name="MAX_SIGLE_LENGTH" select="xs:integer(max(//speaker/abbreviation/string-length()))"/>
    
    <xsl:template match="/">
        <xsl:apply-templates select="//speaker-contribution"/>
    </xsl:template>
    
    <xsl:template match="speaker-contribution">
        
        <!-- NUMBERING -->
        <xsl:variable name="LINE-NUMBER" select="position()"/>
        <xsl:if test="position()&lt;10">0</xsl:if>
        <xsl:if test="position()&lt;100">0</xsl:if>
        <xsl:if test="$TOTAL_CONTRIBUTIONS&gt;999 and position()&lt;1000">0</xsl:if>
        <xsl:value-of select="position()"/>
        
        <xsl:text>  </xsl:text>
        
        <!-- SPEAKER -->
        <xsl:variable name="SPEAKER" select="@speaker"/>
        <xsl:choose>
            <xsl:when test="preceding-sibling::speaker-contribution[1]/@speaker=$SPEAKER">
                <xsl:for-each select="1 to ($MAX_SIGLE_LENGTH+3)"><xsl:text> </xsl:text></xsl:for-each>                                
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="//speaker[@id=$SPEAKER]/abbreviation"/>
                <xsl:for-each select="1 to ($MAX_SIGLE_LENGTH - //speaker[@id=$SPEAKER]/abbreviation/string-length())"><xsl:text> </xsl:text></xsl:for-each>                                
                <xsl:text>:  </xsl:text>                
            </xsl:otherwise>
        </xsl:choose>
        
        <xsl:apply-templates select="descendant::ts[@n='e.pe' and not(*)]"/>
        
        <xsl:text>
</xsl:text>
    </xsl:template>
    
    <xsl:template match="ts">
        <xsl:if test="@start-offset">
            <xsl:variable name="THIS_START" select="@s"/>
            <xsl:variable name="MAX-OFFSET-START" select="xs:integer(max(//ts[@s=$THIS_START]/@start-offset))"/>
            <xsl:variable name="CURRENT-TEXT-POSITION-START" select="sum(string-length(preceding-sibling::ts/text())) + count(preceding-sibling::ts[@start-offset]) + count(preceding-sibling::ts[@end-offset])"/>
            <xsl:if test="$MAX-OFFSET-START &gt; $CURRENT-TEXT-POSITION-START">
                <xsl:for-each select="1 to ($MAX-OFFSET-START - $CURRENT-TEXT-POSITION-START)"><xsl:text> </xsl:text></xsl:for-each>                
            </xsl:if>
            <xsl:text>[</xsl:text>
        </xsl:if>
        <xsl:value-of select="text()"/>
        <xsl:if test="@end-offset">
            <xsl:text>]</xsl:text>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>