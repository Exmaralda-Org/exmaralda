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
    
    <!-- <u> which are not wrapped in an annotation block: wrap them! -->
    <xsl:template match="tei:u[not(ancestor::tei:annotationBlock)]">
        <tei:annotationBlock>
            <xsl:attribute name="start" select="@start"/>
            <xsl:attribute name="end" select="@end"/>
            <xsl:attribute name="who" select="@who"/>
            <xsl:attribute name="xml:id" select="concat(ab_, @xml:id)"/>
            <xsl:copy>
                <xsl:apply-templates select="@*[not(name()='start' or name()='end' or name()='who')]|node()"/>                
            </xsl:copy>
        </tei:annotationBlock>
    </xsl:template>
    
    
    <!-- <u> which do not have exlusively <seg> as children -->
    <xsl:template match="tei:u[*[not(self::tei:seg)]]">
        <xsl:copy>
            <!-- make them a <seg>  -->
            <tei:seg type="contribution">
                <xsl:attribute name="xml:id" select="generate-id()"/>
                <!-- check if it needs a redundant start anchor -->
                <xsl:if test="not(*[1][self::tei:anchor])">
                    <tei:anchor>
                        <xsl:attribute name="synch" select="ancestor-or-self::*[@start][1]/@start"/>
                    </tei:anchor>
                </xsl:if>    
                <!-- collect the descendants, but not the <seg>s -->
                <xsl:apply-templates select="descendant::*[not(self::seg)]"/>
                <!-- check if it needs a redundant end anchor -->
                <xsl:if test="not(*[last()][self::tei:anchor])">
                    <tei:anchor>
                        <xsl:attribute name="synch" select="ancestor-or-self::*[@end][1]/@end"/>
                    </tei:anchor>
                </xsl:if>                                    
            </tei:seg>
        </xsl:copy>
    </xsl:template>
    
    <!-- seg without a redundant start and end anchor -->
    <xsl:template match="tei:seg[not(*[1][self::tei:anchor]) or not(*[last()][self::tei:anchor])]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:if test="not(*[1][self::tei:anchor])">
                <tei:anchor>
                    <xsl:attribute name="synch" select="ancestor-or-self::*[@start][1]/@start"/>
                </tei:anchor>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="not(*[last()][self::tei:anchor])">
                <tei:anchor>
                    <xsl:attribute name="synch" select="ancestor-or-self::*[@end][1]/@end"/>
                </tei:anchor>
            </xsl:if>                                                
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:timeline[not(@unit)]">
        <xsl:copy>
            <xsl:attribute name="unit">s</xsl:attribute>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:person[not(@n)]">
        <xsl:copy>
            <xsl:attribute name="n" select="@xml:id"/>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:TEI[not(tei:idno)]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <tei:idno type="RANDOM-ID"><xsl:value-of select="generate-id()"/></tei:idno>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:person[not(tei:idno)]">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <tei:idno type="RANDOM-ID"><xsl:value-of select="generate-id()"/></tei:idno>
            <xsl:apply-templates select="node()"/>
        </xsl:copy>
    </xsl:template>
    
    
</xsl:stylesheet>