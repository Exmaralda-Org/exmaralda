<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exmaralda="http://www.exmaralda.org/xml" version="2.0">
    <xsl:output encoding="UTF-8" method="html" omit-xml-declaration="yes"/>

    <xsl:variable name="INCLUDE_HOURS">
        <xsl:choose>
            <xsl:when test="//tli[@time+0.0&gt;3599.99]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <!-- ************************ -->
    <!--    Top level template   -->
    <!-- ************************ -->
    
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <style type="text/css">
                        td {
                            white-space:nowrap;
                            font-family:Courier New, monospaced;
                            font-size:10pt;
                            vertical-align:top; 
                        }   
                        table.p {
                            border-collapse:collapse;
                            border-spacing:0px;
                            empty-cells:show;
                            margin-bottom:10px;
                        }                 
                        td.v {
                        }
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px;} 
                        td.numbering {font-weight:bold; color: gray;}
                        td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100); font-size:8pt;}                         
                        /*.odd {background-color:rgb(240,240,240);}*/
                        /*.even {background-color:rgb(255,255,255);}                        */
                        table.p tr:nth-child(even) {
                            background-color:rgb(240,240,240);
                        }
                        table.p tr:nth-child(odd) {
                            background-color:rgb(255,255,255);
                        }
                        div.main {border:2px solid rgb(100,100,100); width:950px; float:left; margin-top:40px;}
                        div.top {position:fixed; top:0px; left:0px; background-color:rgb(50,50,50); width:100%; }                        
                </style>
                <xsl:call-template name="INSERT_JAVA_SCRIPT"/>
            </head>
            <body ondblclick="stop();">
                <div class="top">
                    <xsl:variable name="AUDIO_FILE">
                        <xsl:value-of select="//referenced-file/@url"/>
                    </xsl:variable>
                    <audio>
                        <xsl:element name="source">
                            <xsl:attribute name="src"><xsl:value-of select="$AUDIO_FILE"/></xsl:attribute>
                            <xsl:attribute name="type">audio/wav</xsl:attribute>
                        </xsl:element>
                    </audio>
                    <span style="font-family: Calibri, Arial, sans-serif; color:white;">
                        Audiodatei: <xsl:value-of select="$AUDIO_FILE"/><br/>
                        Anleitung: [Einfacher Klick] - Audio an der betreffenden Stelle abspielen / [Doppelklick] - Audio stoppen 
                    </span>
                </div>
                <div class="main">
                            <xsl:apply-templates select="//it-bundle"/>                            
                </div>
            </body>
        </html>
    </xsl:template>

    <!-- ************************ -->
    <!--     one partitur area       -->
    <!-- ************************ -->
    
    <xsl:template match="it-bundle">
        <!-- the table representing the actual partitur area -->
        <xsl:element name="table">
            <xsl:attribute name="class">p</xsl:attribute>
            <xsl:attribute name="width"><xsl:value-of select="round(1.4*//table-width/@table-width)"/></xsl:attribute>
            <xsl:apply-templates select="it-line"/>
        </xsl:element>
    </xsl:template>
    

    <!-- ********************************* -->
    <!-- syncpoints aka timeline items -->
    <!-- ********************************* -->
    
    <xsl:template match="sync-points">        
        <!-- do nothing: we do not want syncpoints in the compact partitur -->
    </xsl:template>

    <!-- ********************************************* -->
    <!-- an individual syncpoint aka timeline item -->
    <!-- ********************************************* -->
    <xsl:template match="sync-point">
        <!-- do nothing: we do not want syncpoints in the compact partitur -->
    </xsl:template>

    <!-- ****************************** -->
    <!-- an indivudual it-line aka tier -->
    <!-- ****************************** -->
    <xsl:template match="it-line">
        <xsl:element name="tr">
            
            <!-- class of the table row equals the category of the tier -->
            <xsl:attribute name="class">
                <xsl:value-of select="//tier[@id=current()/@formatref]/@category"/>
            </xsl:attribute>
            
            <xsl:attribute name="name">
                <xsl:value-of select="//tier[@id=current()/@formatref]/@category"/>
            </xsl:attribute>
            
            <xsl:variable name="itLinePosition">
                <xsl:value-of select="position()"/>
            </xsl:variable>
            
            <xsl:variable name="NUMBER">
                <xsl:value-of select="1 + count(../preceding-sibling::it-bundle/descendant::it-line) + count(preceding-sibling::it-line)"/>
            </xsl:variable>
            
            <td class="time">
                <xsl:text>{</xsl:text>
                <xsl:value-of select="exmaralda:format_time(//tli[@id=current()/descendant::it-chunk[1]/@start-sync]/@time, $INCLUDE_HOURS)"/>
                <xsl:text>}</xsl:text>
            </td>
            
            <td class="numbering">
                <xsl:if test="($NUMBER + 0) &lt; 10">
                    <xsl:text>0</xsl:text>
                </xsl:if>
                <xsl:if test="($NUMBER + 0) &lt; 100">
                    <xsl:text>0</xsl:text>
                </xsl:if>
                <xsl:value-of select="$NUMBER"/>                
            </td>
            
            <!-- aply the template for the tier label -->
            <xsl:apply-templates select="it-label"/>
            
            <xsl:for-each select="../sync-points/sync-point">
                
                <xsl:variable name="Pos">
                    <xsl:value-of select="1+count(preceding-sibling::*)"/>
                </xsl:variable>
                
                <xsl:variable name="interval_is_covered">
                    <xsl:for-each select="../../it-line[$itLinePosition+0]/it-chunk">
                        <xsl:variable name="startPos">
                            <xsl:value-of select="1+count(../../sync-points/sync-point[@id=current()/@start-sync]/preceding-sibling::*)"/>
                        </xsl:variable>
                        <xsl:variable name="endPos">
                            <xsl:value-of select="1+count(../../sync-points/sync-point[@id=current()/@end-sync]/preceding-sibling::*)"/>
                        </xsl:variable>
                        <xsl:if test="$startPos+0&lt;=$Pos+0 and $endPos+0&gt;$Pos+0">X</xsl:if>
                    </xsl:for-each>                    
                </xsl:variable>
                
                <xsl:choose>

                    <!-- case where there is no event at or across the current timepoint -->
                    <xsl:when test="not(contains($interval_is_covered,'X'))">
                        <xsl:element name="td">
                            <xsl:if test="count(../../it-line)&gt;1">
                                <xsl:attribute name="style">
                                    <xsl:if test="$itLinePosition=1">border-top:1px gray solid;</xsl:if>
                                </xsl:attribute>
                            </xsl:if>
                            <xsl:attribute name="class">
                                <xsl:text>emp</xsl:text>
                                <xsl:if test="$NUMBER mod 2 = 0"> even</xsl:if>
                                <xsl:if test="not($NUMBER mod 2 = 0)"> odd</xsl:if>
                            </xsl:attribute>
                            <!-- if this is the last entry in that row: stretch it! -->
                            <xsl:if test="count(current()/following-sibling::*)=0">
                                <xsl:attribute name="width">100%</xsl:attribute>
                            </xsl:if>
                        </xsl:element>
                    </xsl:when>
                    
                    <!-- case where there IS an event at the current timepoint -->
                    <xsl:otherwise>
                        <xsl:apply-templates select="../../it-line[$itLinePosition+0]/it-chunk[@start-sync=current()/@id]">
                            <xsl:with-param name="number" select="$NUMBER"/>
                        </xsl:apply-templates>
                    </xsl:otherwise>
                    
                </xsl:choose>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="it-label">
        <xsl:element name="td">
            <xsl:attribute name="class">speaker</xsl:attribute>
            <xsl:if test="count(../../it-line)&gt;1">
                <xsl:attribute name="style">
                    border-left:1px gray solid;
                    <xsl:if test="count(../preceding-sibling::it-line)=0">border-top:1px gray solid;</xsl:if>
                    <xsl:if test="count(../following-sibling::it-line)=0">border-bottom:1px gray solid;</xsl:if>
                </xsl:attribute>
             </xsl:if>
            
            <!-- the tooltip title for this tier -->
            <xsl:attribute name="title">                
                <xsl:variable name="SPEAKER_ID">
                    <xsl:value-of select="//tier[@id=current()/../@formatref]/@speaker"/>
                </xsl:variable>
                
            </xsl:attribute>
            
            <xsl:value-of select="run/text()"/>
            
            <!-- two non-breaking spaces behind the tier-label -->
            <xsl:if test="not(string-length(run/text())=0)">
                <xsl:text>&#x00A0;&#x00A0;</xsl:text>
            </xsl:if>
        
        </xsl:element>
    </xsl:template>

    <xsl:template match="it-chunk">
        <xsl:param name="number"/>
        <xsl:variable name="cellspan">
            <xsl:value-of select="count(../../sync-points/sync-point[@id=current()/@end-sync]/preceding-sibling::*)-count(../../sync-points/sync-point[@id=current()/@start-sync]/preceding-sibling::*)"/>
        </xsl:variable>
        <xsl:variable name="tiercategory">
            <xsl:value-of select="//tier[@id=current()/@formatref]/@category"/>
        </xsl:variable>

        <xsl:element name="td">
            <xsl:if test="count(../../it-line)&gt;1">
                <xsl:attribute name="style">
                    <xsl:if test="count(../preceding-sibling::it-line)=0">border-top:1px gray solid;</xsl:if>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="colspan">
                <xsl:value-of select="$cellspan"/>
            </xsl:attribute>
            <xsl:attribute name="class">
                <xsl:value-of select="$tiercategory"/>
                <xsl:if test="$number mod 2 = 0"> even</xsl:if>
                <xsl:if test="not($number mod 2 = 0)"> odd</xsl:if>                
            </xsl:attribute>
            <xsl:variable name="TIME">
                <xsl:value-of select="0 + //tli[@id=current()/@start-sync]/@time"/>
            </xsl:variable>            
            
            <!-- TODO !! -->
            <xsl:attribute name="data-start" select="$TIME"/>
            <xsl:attribute name="data-end">
                <xsl:if test="not(@end-sync='END')">
                    <xsl:value-of select="//tli[@id=current()/@end-sync]/@time"/>
                </xsl:if>
                <xsl:if test="@end-sync='END'">
                    <xsl:value-of select="//tli[@id=current()/@start-sync]/following-sibling::tli[1]/@time"/>
                </xsl:if>
            </xsl:attribute>
            
            <xsl:attribute name="onclick">
                <xsl:text>jump('</xsl:text>
                <xsl:value-of select="format-number($TIME, '#.##')"/><xsl:text>');</xsl:text>                
            </xsl:attribute>            
            <xsl:choose>
                <xsl:when test="ends-with(run/text(),' ')"><xsl:value-of select="substring(run/text(),1,string-length(run/text())-1)"></xsl:value-of><xsl:text>&#x00A0;</xsl:text></xsl:when>
                <xsl:otherwise><xsl:value-of select="run/text()"/></xsl:otherwise>
            </xsl:choose>            
        </xsl:element>
    
    </xsl:template>

    <xsl:template name="INSERT_JAVA_SCRIPT">
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
            
            function registerAudioListener(){
                document.getElementsByTagName('audio')[0].addEventListener("timeupdate", updateTime, true);                     
                document.getElementsByTagName('audio')[0].addEventListener("onpause", updateTime, true);                     
            }
            
            function updateTime(){
                var player = document.getElementsByTagName('audio')[0]; 
                var elapsedTime = player.currentTime;                        
                var elements = document.getElementsByTagName('td');
                for (var i = 0; i &lt; elements.length; i++) {
                    element = elements[i];
                    if (element.getAttribute('data-start')!=null){
                        start = element.getAttribute('data-start') + 0;
                        end = element.getAttribute('data-end') + 0;
                        if ((!player.paused) &amp;&amp; (start &lt;= elapsedTime) &amp;&amp; (end &gt;= elapsedTime)){
                            element.style.borderBottom='1px dashed blue';
                        } else {
                                element.style.borderBottom='none';
                        }
                    }
                }                        
            }
            
            window.addEventListener("DOMContentLoaded", registerAudioListener, false);
        </script>                
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
