<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"/>

    <xsl:strip-space elements="*"/>
    <!-- COUNT_START specifies at which number to start the contribution count -->
    <!-- this is important when outputting a selection of the whole transcript-->
    <xsl:variable name="COUNT_START">
        <xsl:choose>
            <xsl:when test="//@count-start">
                <xsl:value-of select="number(//@count-start)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="number('0')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <!-- whether or not to include hours in times, TRUE iff there is an absolute time above one hour-->
    <xsl:variable name="INCLUDE_HOURS">
        <xsl:choose>
            <xsl:when test="//timepoint[@absolute-time+0.0&gt;3599.99]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <style type="text/css"> 
                        body {font-family:'Courier New', 'Courier', monospace;}
                        td {padding:2px;}
                        td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100); font-size:8pt;} 
                        td.numbering {vertical-align:top; font-weight:bold; color: gray;} 
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px;} 
                        td.contribution {vertical-align:top;} 
                        .odd {background-color:rgb(240,240,240);}
                        .even {background-color:rgb(255,255,255);}
                        div.main {border:2px solid rgb(100,100,100); width:800px; float:left; margin-top:40px;}
                        div.top {position:fixed; top:0px; left:0px; background-color:rgb(50,50,50); width:100%; font-size:smaller;}
                        span.breathe {color:rgb(150,150,150); }
                        span.pause {color:rgb(150,150,150); }
                        span.unintelligible {color:rgb(150,150,150); }
                        span.alternative {color:rgb(150,150,150); }
                        span.nonpho {color:rgb(150,150,150); }
                </style>
                <script type="text/javascript">
                    function jump(time){
                        document.getElementsByTagName('audio')[0].currentTime=time;
                        document.getElementsByTagName('audio')[0].play();
                    }			
                    function stop(){
                        document.getElementsByTagName('audio')[0].pause();
                    }
                    function highlight(id){
                        var elements = document.getElementsByName(id);
                        for (var i = 0; i &lt; elements.length; i++) {
                            element = elements[i];
                            element.style.border='2px solid blue';
                        }
                    }
                    function lowlight(id){
                    var elements = document.getElementsByName(id);
                        for (var i = 0; i &lt; elements.length; i++) {
                            element = elements[i];
                            element.style.border='none';
                        }
                    }
                </script>                
            </head>
            <body ondblclick="stop();">
                <div class="top">
                    <xsl:variable name="AUDIO_FILE">
                        <xsl:value-of select="//recording/@path"/>
                    </xsl:variable>
                    <audio>
                        <xsl:element name="source">
                            <xsl:attribute name="src"><xsl:value-of select="$AUDIO_FILE"/></xsl:attribute>
                            <xsl:attribute name="type">audio/wav</xsl:attribute>
                        </xsl:element>
                    </audio>
                    <span style="font-family: Calibri, Arial, sans-serif; color:white;">
                        Audiodatei: <xsl:value-of select="$AUDIO_FILE"/><br/>
                        Anleitung: [Einfacher Klick] - Audio an der betreffenden Stelle abspielen / [Doppelklick] - Audio stoppen / [Mouseover] - Überlappungen hervorheben
                    </span>
                </div>
                <div class="main">
                    <table>
                        <xsl:apply-templates select="//contribution"> </xsl:apply-templates>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="contribution">
        <tr>
            <td class="time">
                <xsl:text>{</xsl:text>
                <xsl:value-of select="exmaralda:format_time(//timepoint[@timepoint-id=current()/@start-reference]/@absolute-time, $INCLUDE_HOURS)"/>
                <xsl:text>}</xsl:text>
            </td>
            <td class="numbering">
                <xsl:variable name="NUMBER">
                    <xsl:value-of select="$COUNT_START + position()"/>
                </xsl:variable>
                <xsl:if test="($NUMBER + 0) &lt; 10">
                    <xsl:text>0</xsl:text>
                </xsl:if>
                <xsl:if test="($NUMBER + 0) &lt; 100">
                    <xsl:text>0</xsl:text>
                </xsl:if>
                <xsl:value-of select="$NUMBER"/>
            </td>
            <td class="speaker">
                <xsl:attribute name="title"><xsl:value-of select="//speaker[@speaker-id=current()/@speaker-reference]/name"></xsl:value-of></xsl:attribute>
                <xsl:value-of select="@speaker-reference"/>
            </td>
            <td>
                <xsl:attribute name="class">
                    <xsl:text>contribution</xsl:text>
                    <xsl:if test="position() mod 2=0"> even</xsl:if>
                    <xsl:if test="not(position() mod 2=0)"> odd</xsl:if>
                </xsl:attribute>                
                <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
            </td>
        </tr>
    </xsl:template>
    
    
    <xsl:template match="w" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@transition='assimilated'">
                <!-- change 04-02-2009 -->
                <xsl:text>_</xsl:text>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time) and not(self::latching) and not(self::boundary) and not(self::comment)]">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>
        
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <xsl:if test="@n">
            <b> [<xsl:value-of select="@n"/>]</b>
        </xsl:if>
        <!-- <xsl:if test="not(@transition='assimilated') and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))"> -->
        <!-- This is saying: if the following sibling is not assimilated word, -->
        <!-- AND if (a timepoint immediately follows OR this is the end of the contribution): put a space -->
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/text()" mode="transform-parsed-to-unparsed">
        <xsl:if test="string-length(normalize-space())&gt;0">
            <xsl:value-of select="."/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="time" mode="transform-parsed-to-unparsed">
        <!-- <xsl:copy-of select="."/> -->
    </xsl:template>
    
    <xsl:template match="pause" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="pause">
            <xsl:attribute name="title">Pause</xsl:attribute>
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
    
    <!-- 06-03-2009: this is retained for backwards compatibility -->
    <!-- the element 'unintelligible' will not be written any more -->
    <!-- instead the sequence of plus signs is encoded as a word -->
    <xsl:template match="unintelligible" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="unintelligible">
            <xsl:attribute name="title">Unverständliche Passage</xsl:attribute>
            <xsl:for-each select="(1 to @length)">
                <!-- change 04-02-2009 -->
                <xsl:text>+++</xsl:text>
            </xsl:for-each>
            <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:if>
        </span>
        
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="non-phonological" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="nonpho">
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
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        <span class="alternative">
            <xsl:for-each select="alternative">
                <!-- change 04-02-2009 -->
                <xsl:text>/</xsl:text>
                <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
            </xsl:for-each>
        </span>
        <xsl:text>)</xsl:text>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="breathe" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <span class="breathe">
            <xsl:attribute name="title">Ein-/Ausatmen</xsl:attribute>
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
    
    <!-- *************** START TEMPLATES FOR BASIC TRANSCRIPT ************** -->
    
    <xsl:template match="lengthening" mode="transform-parsed-to-unparsed">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="line" mode="transform-parsed-to-unparsed">
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
    </xsl:template>
    
    <xsl:template match="stress" mode="transform-parsed-to-unparsed">
        <xsl:if test="@type='strong'"><xsl:text>!</xsl:text></xsl:if>
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <xsl:if test="@type='strong'"><xsl:text>!</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="stress/text()" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="translate(., 'abcdefghijklmnopqrstuvwxyzäöü', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
    </xsl:template>
    
    <xsl:template match="latching" mode="transform-parsed-to-unparsed">
        <xsl:text>=</xsl:text>
    </xsl:template>
    
    <xsl:template match="boundary" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@movement='low-fall'">.</xsl:when>
            <xsl:when test="@movement='fall'">;</xsl:when>
            <xsl:when test="@movement='steady'">–</xsl:when>
            <xsl:when test="@movement='rise'">,</xsl:when>
            <xsl:when test="@movement='high-rise'">?</xsl:when>
            <xsl:otherwise>|</xsl:otherwise>
        </xsl:choose>
        <xsl:if test="@latching='yes'">=</xsl:if>
        <xsl:if test="@type='final' and @latching='no'"><xsl:text>&#x0020;</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="comment" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@position='start'">&#x0020;&lt;&lt;<xsl:value-of select="@description"/>&gt;&#x0020;</xsl:when>
            <xsl:otherwise>&#x0020;&gt;&#x0020;</xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <!-- *************** END TEMPLATES FOR BASIC TRANSCRIPT ************** -->

    <xsl:template match="contribution/segment" mode="unparsed" xml:space="default">
        <xsl:element name="span">
            <xsl:variable name="TIME">
                <xsl:value-of select="0 + //timepoint[@timepoint-id=current()/@start-reference]/@absolute-time"/>                
            </xsl:variable>
            <xsl:attribute name="title">Klicken zum Abspielen von <xsl:value-of select="exmaralda:format_time($TIME, $INCLUDE_HOURS)"/></xsl:attribute>
            <xsl:attribute name="onclick">
                <xsl:text>jump('</xsl:text>
                <xsl:value-of select="format-number($TIME, '#.##')"/><xsl:text>');</xsl:text>                
            </xsl:attribute>
            <xsl:if test="count(//segment[@start-reference=current()/@start-reference])>1">
                <xsl:attribute name="name">Overlap_<xsl:value-of select="@start-reference"/></xsl:attribute>
                <xsl:attribute name="onmouseover">highlight('Overlap_<xsl:value-of select="@start-reference"/>')</xsl:attribute>
                <xsl:attribute name="onmouseout">lowlight('Overlap_<xsl:value-of select="@start-reference"/>')</xsl:attribute>
                <b><xsl:text>[</xsl:text></b>
            </xsl:if>
            
            <!--- this is to switch spaces at the end of brackted overlaps -->
            <xsl:choose>
                <xsl:when test="count(//segment[@end-reference=current()/@end-reference])>1">
                    <xsl:if test="ends-with(text(),' ')">
                        <xsl:value-of select="normalize-space(text())"/>
                        <b><xsl:text>]</xsl:text></b>
                        <xsl:text>&#x00A0;</xsl:text>
                    </xsl:if>
                    <xsl:if test="not(ends-with(text(),' '))">
                        <xsl:value-of select="text()"/>
                        <b><xsl:text>]</xsl:text></b>
                    </xsl:if>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="text()"/>
                </xsl:otherwise>
            </xsl:choose>
            
        </xsl:element>
    </xsl:template>

    <xsl:function name="exmaralda:format_time">
        <xsl:param name="time_sec"/>
        <xsl:param name="include_hours"/>
        <xsl:if test="string-length($time_sec)=0">
            <xsl:text/>
        </xsl:if>
        <xsl:if test="string-length($time_sec)&gt;0">
            <xsl:variable name="totalseconds">
                <xsl:value-of select="0 + $time_sec"/>
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
            <xsl:if test="$include_hours='true'">
                <xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
                    <xsl:text>0</xsl:text>
                    <xsl:value-of select="$hours"/>
                </xsl:if>
                <xsl:if test="$hours + 0 = 0">
                    <xsl:text>00</xsl:text>
                </xsl:if>
                <xsl:text>:</xsl:text>
            </xsl:if>
            <xsl:if test="$minutes+0 &lt; 10">
                <xsl:text>0</xsl:text>
            </xsl:if>
            <xsl:value-of select="$minutes"/>
            <xsl:text>:</xsl:text>
            <xsl:variable name="roundsec">
                <xsl:value-of select="round($seconds)"/>
            </xsl:variable>
            <!-- changed 04-03-2010 -->
            <xsl:if test="$roundsec+0 &lt; 10">
                <xsl:text>0</xsl:text>
            </xsl:if>
            <xsl:value-of select="$roundsec"/>
        </xsl:if>
    </xsl:function>
    
    
</xsl:stylesheet>
