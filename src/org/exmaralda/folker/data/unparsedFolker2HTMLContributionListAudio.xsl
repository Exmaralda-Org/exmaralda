<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes"/>

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
                        body {font-family:'Calibri', 'Arial', sans-serif;}
                        td {padding:2px;}
                        td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100); font-size:8pt;} 
                        td.numbering {vertical-align:top; font-weight:bold; color: gray;} 
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px;} 
                        td.contribution {vertical-align:top;} 
                        .odd {background-color:rgb(240,240,240);}
                        .even {background-color:rgb(255,255,255);}
                        div.main {border:2px solid rgb(100,100,100); width:800px; float:left; margin-top:40px;}
                        div.top {position:fixed; top:0px; left:0px; background-color:rgb(50,50,50); width:100%; }
                </style>
                <xsl:call-template name="INSERT_JAVA_SCRIPT"/>
            </head>
            <body ondblclick="stop();">
                <div class="top">
                    <xsl:variable name="AUDIO_FILE">
                        <xsl:value-of select="//recording/@path"/>
                    </xsl:variable>
                    <audio>
                        <xsl:element name="source">
                            <xsl:attribute name="src"><xsl:text>./</xsl:text><xsl:value-of select="$AUDIO_FILE"/></xsl:attribute>
                            <xsl:attribute name="type">audio/wav</xsl:attribute>
                        </xsl:element>
                    </audio>
                    <span style="font-family: Calibri, Arial, sans-serif; color:white;">
                        Audiodatei: <xsl:value-of select="$AUDIO_FILE"/><br/>
                        Anleitung: [einfacher Klick] - Audio an der betreffenden Stelle abspielen / [Doppelklick] - Audio stoppen / [Mouseover] - Überlappungen hervorheben
                    </span>
                </div>
                <div class="main">
                    <table>
                        <xsl:apply-templates select="//segment"> </xsl:apply-templates>
                    </table>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="segment">
        <tr>
            <td class="time">
                <xsl:text>[</xsl:text>
                <xsl:value-of select="exmaralda:format_time(//timepoint[@timepoint-id=current()/@start-reference]/@absolute-time, $INCLUDE_HOURS)"/>
                <xsl:text>]</xsl:text>
            </td>
            <td class="time">
                <xsl:text>[</xsl:text>
                <xsl:value-of select="exmaralda:format_time(//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time, $INCLUDE_HOURS)"/>
                <xsl:text>]</xsl:text>
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
                <xsl:value-of select="ancestor::contribution/@speaker-reference"/>
            </td>
            <td>
                <xsl:attribute name="class">
                    <xsl:text>contribution</xsl:text>
                    <xsl:if test="position() mod 2=0"> even</xsl:if>
                    <xsl:if test="not(position() mod 2=0)"> odd</xsl:if>
                </xsl:attribute>                
                <xsl:apply-templates mode="unparsed"/>
            </td>
        </tr>
    </xsl:template>

    <xsl:template match="contribution/segment" mode="unparsed" xml:space="default">
        <xsl:element name="span">
            <xsl:variable name="TIME">
                <xsl:value-of select="0 + //timepoint[@timepoint-id=current()/@start-reference]/@absolute-time"/>                
            </xsl:variable>
            <xsl:attribute name="data-start" select="$TIME"/>
            <xsl:attribute name="data-end" select="//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time"/>
            <xsl:attribute name="title">Klicken zum Abspielen von <xsl:value-of select="exmaralda:format_time($TIME, $INCLUDE_HOURS)"/></xsl:attribute>
            <xsl:attribute name="onclick">
                <xsl:text>jump('</xsl:text>
                <xsl:value-of select="format-number($TIME, '#.##')"/><xsl:text>');</xsl:text>                
            </xsl:attribute>

            <xsl:attribute name="name"><xsl:value-of select="@start-reference"/></xsl:attribute>
            
            <xsl:if test="count(//segment[@start-reference=current()/@start-reference])>1">
                <xsl:attribute name="onmouseover">highlight('<xsl:value-of select="@start-reference"/>')</xsl:attribute>
                <xsl:attribute name="onmouseout">lowlight('<xsl:value-of select="@start-reference"/>')</xsl:attribute>
                <b><xsl:text>[</xsl:text></b>
            </xsl:if>
            
            <!--- this is to switch spaces at the end of bracketed overlaps -->
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
    
    <xsl:template name="INSERT_JAVA_SCRIPT">
        <script type="text/javascript">                    
            <xsl:text disable-output-escaping="yes" >
            <![CDATA[
            function jump(time){
                document.getElementsByTagName('audio')[0].currentTime=time;
                document.getElementsByTagName('audio')[0].play();
            }			
            
            function stop(){
                document.getElementsByTagName('audio')[0].pause();
            }
            
            function highlight(id){
                var elements = document.getElementsByName(id);
                for (var i = 0; i < elements.length; i++) {
                    element = elements[i];
                    element.style.border='2px solid blue';
                }
            }
            function lowlight(id){
            var elements = document.getElementsByName(id);
                for (var i = 0; i < elements.length; i++) {
                    element = elements[i];
                    element.style.border='none';
                }
            }
            
            function registerAudioListener(){
                document.getElementsByTagName('audio')[0].addEventListener("timeupdate", updateTime, true);                     
                document.getElementsByTagName('audio')[0].addEventListener("onpause", updateTime, true);                     
            }
            
            function updateTime(){
                var player = document.getElementsByTagName('audio')[0]; 
                var elapsedTime = player.currentTime;                        
                var elements = document.getElementsByTagName('span');
                for (var i = 0; i < elements.length; i++) {
                    element = elements[i];
                    start = element.getAttribute('data-start');
                    end = element.getAttribute('data-end');
                    if ((!player.paused) && (start < elapsedTime) && (end < elapsedTime)){
                        element.style.borderBottom='1px dashed blue';
                    } else {
                        element.style.borderBottom='none';                           
                    }
                }                        
            }
            window.addEventListener("DOMContentLoaded", registerAudioListener, false);
            ]]>
            </xsl:text>
        </script>                
        
    </xsl:template>
    
    
</xsl:stylesheet>
