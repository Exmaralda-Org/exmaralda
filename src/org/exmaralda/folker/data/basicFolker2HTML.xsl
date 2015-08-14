<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="2.0"
    xmlns:tesla="http://www.exmaralda.org"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    >

    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes" indent="no"/>
    
    
    <!-- ****************************************** -->
    <!-- **************** VARIABLES *************** -->
    <!-- ****************************************** -->
    
    <xsl:variable name="timeline-positions">
        <xsl:for-each select="//timepoint">
            <item>
                <xsl:attribute name="id" select="@timepoint-id"/>
                <xsl:attribute name="position" select="count(preceding-sibling::*)"/>
            </item>
        </xsl:for-each>
    </xsl:variable>
    
    <xsl:function name="tesla:timeline-position" as="xs:integer">
        <xsl:param name="timepoint-id"/>
        <xsl:value-of select="$timeline-positions/descendant::item[@id=$timepoint-id]/@position"/>
    </xsl:function>
    
    
    <!-- hold all line elements in a variable -->
    <xsl:variable name="lines">
        <transcription>
            <xsl:for-each select="//line">
                <xsl:copy>
                    <xsl:attribute name="start-reference">
                        <xsl:choose>
                            <xsl:when test="descendant::*[1][self::time]"><xsl:value-of select="descendant::time[1]/@timepoint-reference"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="parent::contribution/@start-reference"/></xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="end-reference">
                        <xsl:choose>
                            <xsl:when test="descendant::*[last()][self::time]"><xsl:value-of select="descendant::time[last()]/@timepoint-reference"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="parent::contribution/@end-reference"/></xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <xsl:attribute name="speaker-reference">
                        <xsl:value-of select="parent::contribution/@speaker-reference"/>
                    </xsl:attribute>
                    <!-- insert redundant time element -->
                    <!-- changed 03-09-2012 -->
                    <xsl:if test="not(descendant::*[1][self::time]) or (descendant::*[1][self::time and not(parent::*[self::line])])">
                        <xsl:element name="time">
                            <xsl:attribute name="timepoint-reference">
                                <!-- TODO! -->
                                <xsl:choose>
                                    <xsl:when test="preceding-sibling::line"><xsl:value-of select="preceding-sibling::line[1]/descendant::time[last()]/@timepoint-reference"/></xsl:when>
                                    <xsl:otherwise><xsl:value-of select="parent::contribution/@start-reference"/></xsl:otherwise>
                                </xsl:choose>                                
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:if>
                    <xsl:apply-templates mode="copy"/>                
                    <!-- insert redundant time element -->
                    <!-- changed 03-09-2012 -->
                    <xsl:if test="not(descendant::*[last()][self::time]) or (descendant::*[last()][self::time and not(parent::*[self::line])])">
                        <xsl:element name="time">
                            <xsl:attribute name="timepoint-reference">
                                <!-- TODO -->
                                <xsl:choose>
                                    <xsl:when test="following-sibling::line"><xsl:value-of select="following-sibling::line[1]/descendant::time[1]/@timepoint-reference"/></xsl:when>
                                    <xsl:otherwise><xsl:value-of select="parent::contribution/@end-reference"/></xsl:otherwise>
                                </xsl:choose>                                
                            </xsl:attribute>
                        </xsl:element>
                    </xsl:if>
                </xsl:copy>
            </xsl:for-each>
        </transcription>
    </xsl:variable>
    
    <xsl:template match="* | @* | text()" mode="copy">
        <xsl:copy><xsl:apply-templates select="* | @* | text()" mode="copy"/></xsl:copy>
    </xsl:template>
    
    <!-- ****************************************** -->
    <!-- **************** TOP LEVEL *************** -->
    <!-- ****************************************** -->

    <xsl:template match="/">
        <xsl:message select="$lines"></xsl:message>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <style type="text/css">
                        body {font-family:'Courier New', 'Courier', monospace;}
                        td.numbering {vertical-align:top;  font-weight:bold; color: gray;}
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px;}
                        td.contribution {vertical-align:top;}
                        td.odd {background-color:rgb(230,230,230);}
                        td.playback {background-color:rgb(0,0,128); color:white; font-weight:bold; font-size:smaller; font-family:sans-serif;}
                        span.nv{
                            /*font-size:smaller;*/
                            color:rgb(100,100,100);
                            /*color:red;*/
                        }
                    </style>
            </head>
            <body>
                <table>
                    <xsl:apply-templates select="$lines/descendant::line" mode="output">
                        <xsl:sort select="tesla:timeline-position(@start-reference)" order="ascending"/>
                        <xsl:sort select="tesla:timeline-position(@end-reference)" order="descending"/>
                    </xsl:apply-templates>
                </table>
                
            </body>
        </html>
    </xsl:template>
    
    <!-- ****************************************** -->

    <xsl:template match="line" mode="output">
        <tr>
            <xsl:element name="td">
                <xsl:attribute name="class">
                    <xsl:text>numbering</xsl:text>
                </xsl:attribute>
                <xsl:variable name="NUMBER"><xsl:value-of select="position()"/></xsl:variable>
                <xsl:if test="($NUMBER + 0) &lt; 10"><xsl:text>0</xsl:text></xsl:if>
                <xsl:if test="($NUMBER + 0) &lt; 100"><xsl:text>0</xsl:text></xsl:if>
                <xsl:value-of select="$NUMBER"/>                
            </xsl:element>
            <xsl:element name="td">
                <xsl:attribute name="class">
                    <xsl:text>speaker</xsl:text>
                </xsl:attribute>
                <xsl:value-of select="@speaker-reference"/>
            </xsl:element>
            <xsl:element name="td">
                <xsl:attribute name="class">
                    <xsl:text>contribution</xsl:text>
                    <xsl:if test="@position mod 2=0"> odd</xsl:if>
                </xsl:attribute>
                <xsl:apply-templates mode="output"/>
            </xsl:element>
        </tr>
    </xsl:template>
    
    
    <!-- ****************************************** -->

    <xsl:template match="w" mode="output">
        <xsl:choose>
            <xsl:when test="@transition='assimilated'">
                <!-- change 04-02-2009 -->
                <xsl:text>_</xsl:text>
            </xsl:when>
            <!-- changed 03-09-2012 -->
            <!-- changed again 10-09-2012 -->
            <!-- <xsl:when test="preceding-sibling::*[1][not(self::time)]">-->
            <!-- <boundary type="pseudo" movement="rise" latching="yes"/> -->
            <xsl:when test="preceding-sibling::*[1][not(self::time or self::latching or (self::boundary and@latching='yes'))] and not(parent::*[self::uncertain] and not(preceding-sibling::*))">
                    <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>
        
        <xsl:apply-templates mode="output"/>
        <!-- This is saying: if the following sibling is not an assimilated word, -->
        <!-- AND if (a timepoint immediately follows OR this is the end of the contribution): put a space -->        
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and not(parent::uncertain and not(following-sibling::*)) and ((following-sibling::*[1][self::time] or (parent::line and not(following-sibling::*))))">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/text()" mode="output">
        <xsl:value-of select="."/>
    </xsl:template>
    

    <!-- ****************************************** -->
    
    <xsl:template match="time" mode="output">
        <!-- overlap bracketing -->
        <xsl:variable name="thisID" select="@timepoint-reference"/>
        <xsl:variable name="previousID" select="preceding::time[1]/@timepoint-reference"/>
        <xsl:variable name="nextID" select="following::time[1]/@timepoint-reference"/>
        
        <xsl:variable name="startOverlap2">
            <xsl:for-each select="$lines/descendant::line">
                <xsl:for-each select="descendant::time[@timepoint-reference=$thisID and not(position()=last())]">
                        <xsl:if test="following::time[1]/@timepoint-reference=$nextID">*</xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:variable name="endOverlap2">
            <xsl:for-each select="$lines/descendant::line">
                <xsl:for-each select="descendant::time[@timepoint-reference=$thisID and not(position()=1)]">
                    <xsl:if test="preceding::time[1]/@timepoint-reference=$previousID">*</xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        
        <xsl:if test="string-length($endOverlap2)&gt;1"><span class="nv">]</span></xsl:if>
        <xsl:if test="string-length($startOverlap2)&gt;1"><span class="nv">[</span></xsl:if>
    </xsl:template>
    
    <!-- ****************************************** -->

    <xsl:template match="pause" mode="output">
        <xsl:if test="preceding-sibling::* and not(preceding::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nv">
            <xsl:choose>
                <xsl:when test="@duration='micro'"><xsl:text>(.)</xsl:text></xsl:when>
                <xsl:when test="@duration='short'"><xsl:text>(-)</xsl:text></xsl:when>
                <xsl:when test="@duration='medium'"><xsl:text>(--)</xsl:text></xsl:when>
                <xsl:when test="@duration='long'"><xsl:text>(---)</xsl:text></xsl:when>
                <xsl:otherwise><xsl:text>(</xsl:text><xsl:value-of select="@duration"></xsl:value-of><xsl:text>)</xsl:text></xsl:otherwise>
            </xsl:choose>
        </span>
        <xsl:if test="following-sibling::*[1][self::time] or not(following::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <!-- ****************************************** -->
    
    <xsl:template match="non-phonological" mode="output">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nv">        
            <xsl:text>((</xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text>))</xsl:text>
        </span>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>

    <!-- ****************************************** -->
    
    <xsl:template match="uncertain" mode="output">
        <!-- changed 10-09-2012 -->
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time or self::latching or (self::boundary and@latching='yes')])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nv"><xsl:text>(</xsl:text></span>
        <xsl:apply-templates select="w" mode="output"/>
        <xsl:for-each select="alternative">
            <!-- change 04-02-2009 -->
            <xsl:text>/</xsl:text>
            <xsl:apply-templates select="w" mode="output"/>
        </xsl:for-each>
        <span class="nv"><xsl:text>)</xsl:text></span>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <!-- ****************************************** -->

    <xsl:template match="lengthening" mode="output">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <!-- ****************************************** -->
    
    <xsl:template match="boundary" mode="output">
        <!-- <boundary type="final" movement="high-rise" latching="no"/> -->
        <xsl:choose>
            <xsl:when test="@movement='low-fall'"><xsl:text>.</xsl:text></xsl:when>
            <xsl:when test="@movement='fall'"><xsl:text>;</xsl:text></xsl:when>
            <xsl:when test="@movement='steady'"><xsl:text>–</xsl:text></xsl:when>
            <xsl:when test="@movement='rise'"><xsl:text>,</xsl:text></xsl:when>
            <xsl:when test="@movement='high-rise'"><xsl:text>?</xsl:text></xsl:when>
        </xsl:choose>
        <xsl:if test="@latching='yes'">=</xsl:if>
        <xsl:if test="@type='final' and @latching='no'"><xsl:text>&#x0020;</xsl:text></xsl:if>        
    </xsl:template>
    
    <!-- ****************************************** -->

    <xsl:template match="latching" mode="output">
        <span class="nv"><xsl:text>=</xsl:text></span>
    </xsl:template>
    
    <!-- ****************************************** -->

    <xsl:template match="comment" mode="output">        
        <xsl:choose>
            <xsl:when test="@position='start'">
                <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])"><xsl:text>&#x0020;</xsl:text></xsl:if>
                <span class="nv">&lt;&lt;<xsl:value-of select="@description"/>&gt;</span>
                <xsl:if test="not(following-sibling::*[1][self::uncertain])"><xsl:text>&#x0020;</xsl:text></xsl:if>
            </xsl:when>
            <xsl:otherwise><span class="nv">&#x0020;&gt;<xsl:if test="not(following-sibling::*[1][self::boundary or self::pause])"><xsl:text>&#x0020;</xsl:text></xsl:if></span></xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <!-- ****************************************** -->    
    
    <xsl:template match="stress/text()" mode="output">        
        <xsl:if test="parent::stress/@type='strong'"><xsl:text>!</xsl:text></xsl:if>
        <xsl:value-of select="translate(., 'abcdefghijklmnopqrstuvwxyzäöü', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
        <xsl:if test="parent::stress/@type='strong'"><xsl:text>!</xsl:text></xsl:if>
    </xsl:template>
        
    
    <!-- ****************************************** -->

    <xsl:template match="breathe" mode="output">
        <!-- changed 10-09-2012 -->
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time or self::latching or (self::boundary and@latching='yes')])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nv">
            <xsl:if test="@type='in'">
                <!-- change 04-02-2009 -->
                <xsl:text>°</xsl:text>
            </xsl:if>
            <xsl:for-each select="(1 to @length)">
                <xsl:text>h</xsl:text>
            </xsl:for-each>
            <xsl:if test="@type='out'">
                <!-- change 04-02-2009 -->
                <xsl:text>°</xsl:text>
            </xsl:if>
        </span>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="text()" mode="output">
        <!-- do nothing -->
    </xsl:template>
    

    <!-- ****************************************** -->
    

    <xsl:template match="unintelligible" mode="output">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:for-each select="(1 to @length)">
            <!-- change 04-02-2009 -->
            <xsl:text>+++</xsl:text>
        </xsl:for-each>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    
    
    
</xsl:stylesheet>