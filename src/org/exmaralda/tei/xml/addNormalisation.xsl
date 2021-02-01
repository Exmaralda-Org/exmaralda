<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:exmaralda="http://www.exmaralda.org"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="normalisationLexicon" 
        select="document('https://raw.githubusercontent.com/Exmaralda-Org/exmaralda/master/src/org/exmaralda/orthonormal/lexicon/FOLK_Normalization_Lexicon_MAY_2020.xml')"/>
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:w[not(@norm)]">
        <xsl:variable name="WORD_TEXT">
            <xsl:apply-templates select="descendant::text()"/>
        </xsl:variable>
        <xsl:copy>
            <xsl:attribute name="norm">
                <xsl:value-of select="exmaralda:normalise($WORD_TEXT)"/>
            </xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>        
    </xsl:template>
    
    <xsl:function name="exmaralda:normalise">
        <xsl:param name="WORD_TEXT"/>
        <!-- <entry form="aufbau">
            <n corr="&amp;" freq="1"/>
            <n corr="Aufbau-" freq="1"/>
            <n corr="Aufbau" freq="23"/>
            <n corr="aufbaue" freq="1"/>
        </entry> -->
        <xsl:choose>
            <xsl:when test="$normalisationLexicon/descendant::entry[@form=$WORD_TEXT]">
                <xsl:variable name="MAX" select="max($normalisationLexicon/descendant::entry[@form=$WORD_TEXT]/descendant::n/@freq)"/>
                <xsl:value-of select="$normalisationLexicon/descendant::entry[@form=$WORD_TEXT]/descendant::n[@freq=$MAX]/@corr"/>                
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$WORD_TEXT"/>                
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
</xsl:stylesheet>