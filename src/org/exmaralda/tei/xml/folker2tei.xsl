<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tesla="http://www.exmaralda.org"  version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>

    <xsl:function name="tesla:seconds-to-timestring">
        <xsl:param name="seconds"/>
        <xsl:variable name="totalseconds">
            <xsl:value-of select="0 + $seconds"/>
        </xsl:variable>
        <xsl:variable name="hours">
            <xsl:value-of select="0 + floor($totalseconds div 3600)"/>
        </xsl:variable>
        <xsl:variable name="minutes">
            <xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
        </xsl:variable>
        <xsl:variable name="seconds">
            <xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
        </xsl:variable>
        <xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
            <xsl:text>0</xsl:text>
            <xsl:value-of select="$hours"/>
        </xsl:if>
        <xsl:if test="$hours + 0 = 0">
            <xsl:text>00</xsl:text>                            
        </xsl:if>
        <xsl:text>:</xsl:text>
        <xsl:if test="$minutes+0 &lt; 10">
            <xsl:text>0</xsl:text>
        </xsl:if>
        <xsl:value-of select="$minutes"/>
        <xsl:text>:</xsl:text>
        <xsl:if test="$seconds+0 &lt; 10">
            <xsl:text>0</xsl:text>
        </xsl:if>
        <xsl:value-of select="$seconds"/>            
    </xsl:function>
    
    
    <xsl:template match="/">
        <TEI xmlns="http://www.tei-c.org/ns/1.0">
            <teiHeader>
                <fileDesc>
                    <titleStmt><title></title></titleStmt>
                    <publicationStmt><p></p></publicationStmt>
                    <sourceDesc>
                        <recordingStmt>
                            <xsl:apply-templates select="//recording"/>
                        </recordingStmt>                                        
                    </sourceDesc>
                    <!-- ... -->
                </fileDesc>
                <profileDesc>
                    <!-- ... -->
                    <particDesc>
                        <xsl:apply-templates select="//speaker"/>
                    </particDesc>                    
                </profileDesc>
                <revisionDesc>
                    <!-- ... -->                    
                    <xsl:element name="change">
                        <xsl:attribute name="when">
                            <xsl:value-of select="current-dateTime()"/>
                        </xsl:attribute>
                        <xsl:text>Created by XSL transformation from a FOLKER transcription</xsl:text>
                    </xsl:element>
                </revisionDesc>                
            </teiHeader>
            <text>
                <timeline unit="s">
                    <xsl:attribute name="origin">
                        <xsl:text>#</xsl:text><xsl:value-of select="//timepoint[1]/@timepoint-id"></xsl:value-of>
                    </xsl:attribute>                    
                    <xsl:apply-templates select="//timepoint"/>
                </timeline>
                <body>
                    <xsl:apply-templates select="//contribution"/>
                </body>
            </text>
        </TEI>            
    </xsl:template>
    
    <xsl:template match="contribution[@speaker-reference or count(child::*[not(self::pause)])&gt;0]">
        <xsl:element name="div" xmlns="http://www.tei-c.org/ns/1.0">            
            <!-- <xsl:attribute name="type">u_with_annotations</xsl:attribute> -->
            <xsl:element name="u">
                <xsl:if test="@speaker-reference">
                    <xsl:attribute name="who">
                        <xsl:text>#</xsl:text><xsl:value-of select="@speaker-reference"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:element name="anchor">
                    <xsl:attribute name="synch">
                        <xsl:text>#</xsl:text><xsl:value-of select="@start-reference"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:apply-templates/>
                <xsl:element name="anchor">
                    <xsl:attribute name="synch">
                        <xsl:text>#</xsl:text><xsl:value-of select="@end-reference"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:element> <!-- end u -->
        </xsl:element> <!-- end div -->        
    </xsl:template>

    <xsl:template match="contribution[not(@speaker-reference) and pause and count(child::*[not(self::pause)])=0]">
        <xsl:apply-templates/>
    </xsl:template>
            
    <xsl:template match="time">
        <xsl:element name="anchor"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="synch">
                <xsl:text>#</xsl:text><xsl:value-of select="@timepoint-reference"/>
            </xsl:attribute>            
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="unparsed">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="unparsed/text()">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="w">
        <xsl:element name="w"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:if test="@transition='assimilated'">
                <xsl:attribute name="type">assimilated</xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="pause">
        <xsl:element name="pause"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
                <xsl:when test="not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')">
                    <xsl:attribute name="dur">PT<xsl:value-of select="@duration"/>S</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="type"><xsl:value-of select="@duration"/></xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>            
            <xsl:if test="not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')">
                <xsl:attribute name="dur">PT<xsl:value-of select="@duration"/>S</xsl:attribute>
            </xsl:if>
            <xsl:if test="not(ancestor::*[@speaker])">
                <xsl:attribute name="start"><xsl:value-of select="ancestor-or-self::*/@start-reference"/></xsl:attribute>
                <xsl:attribute name="end"><xsl:value-of select="ancestor-or-self::*/@end-reference"/></xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="non-phonological">
        <xsl:element name="incident"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:element name="desc">
                <xsl:value-of select="@description"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="breathe">
        <xsl:element name="vocal"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:element name="desc">
                <xsl:if test="@length='1'">short</xsl:if>
                <xsl:if test="@length='2'">medium</xsl:if>
                <xsl:if test="@length='3'">long</xsl:if>
                <xsl:text> breathe </xsl:text>
                <xsl:value-of select="@type"/>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="uncertain">
        <xsl:choose>
            <xsl:when test="not(alternative)">
                <unclear   xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:apply-templates/>
                </unclear>
            </xsl:when>
            <xsl:otherwise>
                <choice xmlns="http://www.tei-c.org/ns/1.0">
                    <unclear><xsl:apply-templates select="child::*[not(self::alternative)]"/></unclear>
                    <xsl:apply-templates select="alternative"/>
                </choice>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <xsl:template match="alternative">
        <unclear  xmlns="http://www.tei-c.org/ns/1.0"><xsl:apply-templates/></unclear>
    </xsl:template>
    
    
    
    <xsl:template match="recording">
        <recording xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="type">audio</xsl:attribute>
            <xsl:attribute name="url"><xsl:value-of select="@path"/></xsl:attribute>
        </recording>        
    </xsl:template>
    
    <xsl:template match="speaker">
        <xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@speaker-id"/>
            </xsl:attribute>
            <xsl:element name="persName">
                <xsl:element name="forename">
                    <xsl:value-of select="name"/>
                </xsl:element>
                <xsl:element name="abbr">
                    <xsl:value-of select="@speaker-id"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="timepoint">
        <xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@timepoint-id"/>
            </xsl:attribute>
            <xsl:attribute name="absolute">
                <xsl:value-of select="tesla:seconds-to-timestring(@absolute-time)"/>                
            </xsl:attribute>                
        </xsl:element>        
    </xsl:template>
    
    
</xsl:stylesheet>
