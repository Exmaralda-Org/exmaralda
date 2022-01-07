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
    <!-- 15-02-2021 : something's rotten here -->
    <!-- <xsl:template match="tei:u[*[not(self::tei:seg)]]"> -->
    <!-- 22-02-2021 : something is still rotten here -->
    <!-- <xsl:template match="tei:u[*[not(self::tei:seg or self::tei:anchor)]]"> -->
    <!-- 21-04-2021 : something is still rotten here -->
    <xsl:template match="tei:u[*[not(self::tei:seg or self::tei:anchor)] or text()]">            
            <xsl:copy>
                <xsl:apply-templates select="@*"/>
                <!-- make them a <seg>  -->
                <tei:seg type="contribution">
                    <xsl:attribute name="xml:id" select="generate-id()"/>
                    <!-- check if it needs a redundant start anchor -->
                    <!-- if this is untokenized ISO/TEI there might be text before the <anchor> -->
                    <xsl:if test="not(node()[self::tei:anchor and position()=1])">
                        <tei:anchor>
                            <xsl:attribute name="synch" select="ancestor-or-self::*[@start][1]/@start"/>
                        </tei:anchor>
                    </xsl:if>    
                    <!-- collect the descendants, but not the <seg>s -->
                    <xsl:apply-templates select="descendant::*[not(self::seg)]|text()"/>
                    <!-- check if it needs a redundant end anchor -->
                    <xsl:if test="not(node()[self::tei:anchor and position()=last()])">
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
                    <!-- changed 15-02-2021 -->
                    <xsl:attribute name="synch">
                        <xsl:choose>
                            <xsl:when test="preceding-sibling::*[1][self::tei:anchor]">
                                <xsl:value-of select="preceding-sibling::*[1][self::tei:anchor]/@synch"/>
                            </xsl:when>
                            <xsl:when test="(preceding-sibling::tei:seg)[1]/*[last()][self::tei:anchor]">
                                <xsl:value-of select="(preceding-sibling::tei:seg)[1]/tei:anchor[last()]/@synch"/>                                
                            </xsl:when>
                            <!-- if the previous <seg> has no <anchor> as its last element, it gets complicated --> 
                            <xsl:when test="preceding-sibling::tei:seg and not((preceding-sibling::tei:seg)[1]/*[1][self::tei:anchor])">
                                <xsl:variable name="start" select="ancestor-or-self::*[@start][1]/@start"/>
                                <xsl:choose>
                                    <xsl:when test="ancestor-or-self::*[@start][1][(following-sibling::*|preceding-sibling::*)[descendant-or-self::tei:seg[2]][@start=$start]]">
                                        <!-- we won't be able to sort the inserted anchor>s, for now, we don't like that -->
                                        <xsl:message terminate="yes">UNSOLVABLE TIME PROBLEM</xsl:message> 
                                    </xsl:when>   
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat($start, '_', @xml:id)"/>                                        
                                    </xsl:otherwise>
                                </xsl:choose>                            
                            </xsl:when>
                            <xsl:when test="not(preceding-sibling::tei:seg)">
                                <xsl:value-of select="ancestor-or-self::*[@start][1]/@start"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:message terminate="yes">UNSOLVABLE TIME PROBLEM</xsl:message>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <!-- <xsl:attribute name="synch" select="ancestor-or-self::*[@start][1]/@start"/> -->
                </tei:anchor>
            </xsl:if>
            <xsl:apply-templates select="node()"/>
            <xsl:if test="not(*[last()][self::tei:anchor])">
                <tei:anchor>
                    <!-- changed 15-02-2021 -->
                    <xsl:attribute name="synch">
                        <xsl:choose>
                            <!-- is the element following this <seg> an <anchor> ? If yes, copy that --> 
                            <xsl:when test="following-sibling::*[1][self::tei:anchor]">
                                <xsl:value-of select="following-sibling::*[1][self::tei:anchor]/@synch"/>
                            </xsl:when>
                            <!-- does the <seg> element following this <seg> have an <anchor> as its first element ? If yes, copy that --> 
                            <xsl:when test="(following-sibling::tei:seg)[1]/*[1][self::tei:anchor]">
                                <xsl:value-of select="(following-sibling::tei:seg)[1]/tei:anchor[1]/@synch"/>                                
                            </xsl:when>
                            <!-- if the next <seg> has no <anchor> as its first element, it gets complicated --> 
                            <xsl:when test="following-sibling::tei:seg and not((following-sibling::tei:seg)[1]/*[1][self::tei:anchor])">
                                <xsl:variable name="end" select="ancestor-or-self::*[@end][1]/@end"/>
                                <xsl:choose>
                                    <xsl:when test="ancestor-or-self::*[@end][1][(following-sibling::*|preceding-sibling::*)[descendant-or-self::tei:seg[2]][@end=$end]]">                                         
                                        <!-- we won't be able to sort the <seg> <anchor>s, for now, we don't like that -->
                                        <xsl:message terminate="yes">UNSOLVABLE TIME PROBLEM</xsl:message>
                                    </xsl:when>   
                                    <xsl:otherwise>
                                        <xsl:value-of select="concat($end, '_', @xml:id)"/>
                                    </xsl:otherwise>                            
                                </xsl:choose>
                            </xsl:when>
                            <!-- if this is the last <seg> sibling, it's safe to take the @end from the ancestor -->
                            <xsl:when test="not(following-sibling::tei:seg)">
                                <xsl:value-of select="ancestor-or-self::*[@end][1]/@end"/>
                            </xsl:when>
                            <!-- in all other cases : go up the document tree until you find an element with an @end attribute and copy that -->
                            <!-- BUT (2021-04-21) : this is wrong!! -->
                            <xsl:otherwise>
                               <!-- <xsl:value-of select="ancestor-or-self::*[@end][1]/@end"/> -->
                                <xsl:message terminate="yes">UNSOLVABLE TIME PROBLEM</xsl:message>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <!-- <xsl:attribute name="synch" select="ancestor-or-self::*[@end][1]/@end"/> -->
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