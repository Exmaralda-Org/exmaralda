<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes"/>
    <xsl:template match="/">    
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
                        span.nv{font-size:smaller;color:rgb(100,100,100);}
                    </style>
                </head>
                <body>
                    <table>
                        <xsl:apply-templates select="//contribution"/>
                    </table>
                </body>
        </html>
    </xsl:template>
    
    <xsl:template match="contribution[not(unparsed) and not(segment)]">
        <xsl:variable name="TIME_START_ID" select="@start-reference"/>
        <xsl:variable name="TIME_END_ID" select="@end-reference"/>
        <xsl:variable name="transformed">
            <contribution>
                <xsl:copy-of select="@speaker-reference"/>
                <xsl:copy-of select="@start-reference"/>
                <xsl:copy-of select="@end-reference"/>
                <xsl:attribute name="position"><xsl:value-of select="position()"/></xsl:attribute>
                <unparsed>
                    <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
                </unparsed>
            </contribution>
        </xsl:variable>
        <tr>
            <xsl:element name="td">
                <xsl:attribute name="class">playback</xsl:attribute>
                <xsl:attribute name="title">Klicken für Wiedergabe der Aufnahme <xsl:value-of select="//recording/@path"/> von <xsl:value-of select="//timepoint[@timepoint-id=$TIME_START_ID]/@absolute-time"/> bis <xsl:value-of select="//timepoint[@timepoint-id=$TIME_END_ID]/@absolute-time"/> </xsl:attribute>
                <xsl:attribute name="onclick">play_audio(<xsl:value-of select="//recording/@path"/>,<xsl:value-of select="//timepoint[@timepoint-id=$TIME_START_ID]/@absolute-time"/>,<xsl:value-of select="//timepoint[@timepoint-id=$TIME_END_ID]/@absolute-time"/>)</xsl:attribute>
                <xsl:text> [&gt;] </xsl:text>
            </xsl:element>
            <xsl:call-template name="TRANSFORM_TRANSFORMED">
                <xsl:with-param name="contribution" select="$transformed"/>
            </xsl:call-template>
            <xsl:apply-templates select="$transformed/unparsed/*"/>
            <!-- <xsl:apply-templates select="$transformed/unparsed/text()"/> -->
         </tr>
    </xsl:template>
    
    <xsl:template name="TRANSFORM_TRANSFORMED">
        <xsl:param name="contribution"/>
        <xsl:apply-templates select="$contribution/*"/>
    </xsl:template>
    
    
    <xsl:template match="contribution">
                <xsl:element name="td">
                <xsl:attribute name="class">
                    <xsl:text>numbering</xsl:text>
                </xsl:attribute>
                <xsl:variable name="NUMBER"><xsl:value-of select="@position"/></xsl:variable>
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
                <!-- <xsl:apply-templates mode="unparsed"/> -->
                <xsl:apply-templates/>
            </xsl:element>
    </xsl:template>
    
    <xsl:template match="contribution/segment" mode="unparsed">
        <xsl:if test="count(//segment[@start-reference=current()/@start-reference])>1">
            <xsl:text>[</xsl:text>
        </xsl:if>    
        <xsl:value-of select="text()"/>
        <xsl:if test="count(//segment[@end-reference=current()/@end-reference])>1">
            <xsl:text>]</xsl:text>
        </xsl:if>    
    </xsl:template>    
    
    <xsl:template match="w" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <!-- <xsl:when test="not(preceding-sibling::*)">
                </xsl:when> -->
            <xsl:when test="@transition='assimilated'">
                <!-- change 04-02-2009 -->
                <xsl:text>_</xsl:text>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time)]">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>
        
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <!-- <xsl:if test="not(@transition='assimilated') and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))"> -->
        <!-- This is saying: if the following sibling is not assimilated word, -->
        <!-- AND if (a timepoint immediately follows OR this is the end of the contribution): put a space -->
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/text()" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="time" mode="transform-parsed-to-unparsed">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="pause" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
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
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="unintelligible" mode="transform-parsed-to-unparsed">
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
    
    <xsl:template match="non-phonological" mode="transform-parsed-to-unparsed">
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
        
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="uncertain" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nv"><xsl:text>(</xsl:text></span>
        <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        <xsl:for-each select="alternative">
            <!-- change 04-02-2009 -->
            <xsl:text>/</xsl:text>
            <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        </xsl:for-each>
        <span class="nv"><xsl:text>)</xsl:text></span>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="lengthening" mode="transform-parsed-to-unparsed">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="phrase" mode="transform-parsed-to-unparsed">
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <xsl:choose>
            <xsl:when test="@type='intonation-phrase'">
                <xsl:choose>
                    <xsl:when test="@boundary-intonation='low-fall'"><xsl:text>.</xsl:text></xsl:when>
                    <xsl:when test="@boundary-intonation='fall'"><xsl:text>,</xsl:text></xsl:when>
                    <xsl:when test="@boundary-intonation='steady'"><xsl:text>-</xsl:text></xsl:when>
                    <xsl:when test="@boundary-intonation='rise'"><xsl:text>;</xsl:text></xsl:when>
                    <xsl:when test="@boundary-intonation='high-rise'"><xsl:text>?</xsl:text></xsl:when>
                </xsl:choose>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- <xsl:template match="time" mode="transform-parsed-to-unparsed"></xsl:template> -->
    
    <xsl:template match="stress" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="translate(text(), 'abcdefghijklmnopqrstuvwxyzäöü', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
    </xsl:template>
    
    <xsl:template match="breathe" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
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
        
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="text()" mode="transform-parsed-to-unparsed">
        <!-- do nothing -->
    </xsl:template>
    
</xsl:stylesheet>
