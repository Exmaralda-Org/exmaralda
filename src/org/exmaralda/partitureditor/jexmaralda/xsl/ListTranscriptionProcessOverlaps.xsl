<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
   
    <!-- Identity template : copy all text nodes, elements and attributes -->   
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="ts[@n='e.pe' and not(*)]">
        <!-- <ts n="e.pe" s="T1927" e="T1928">heute war auch alles komplett ausgebucht. </ts> -->
        <xsl:element name="ts">
            <xsl:attribute name="s" select="@s"/>
            <xsl:attribute name="e" select="@e"/>
            <xsl:attribute name="n" select="@n"/>
            
            <xsl:variable name="START" select="@s"/>
            <xsl:variable name="END" select="@e"/>
            <xsl:variable name="ID" select="generate-id()"/>
            <xsl:if test="//ts[@n='e.pe' and not(*) and @s=$START and not(generate-id()=$ID)]">
                <!-- <xsl:attribute name="start-is-overlap">true</xsl:attribute> -->
                <xsl:variable name="CHAR-COUNT" select="sum(string-length(preceding-sibling::ts/text()))"/>
                <xsl:attribute name="start-offset" select="$CHAR-COUNT"/>
            </xsl:if>
            <xsl:if test="//ts[@n='e.pe' and not(*) and @e=$END and not(generate-id()=$ID)]">
                <!-- <xsl:attribute name="end-is-overlap">true</xsl:attribute> -->
                <xsl:variable name="CHAR-COUNT" select="sum(string-length(preceding-sibling::ts/text())) + string-length(text())"/>
                <xsl:attribute name="end-offset" select="$CHAR-COUNT"/>
            </xsl:if>
            
            
            <xsl:value-of select="text()"/>
        </xsl:element>
        
    </xsl:template>
    
</xsl:stylesheet>