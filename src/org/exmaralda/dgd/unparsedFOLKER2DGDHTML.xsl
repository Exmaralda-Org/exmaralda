<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"/>
    
    <!-- Transforms a FOLKER transcript to a HTML document for integration into DGD2.0 -->
    <!-- The stylesheet assumes that the FOLKER transcript was saved on parse level 0 [sic!] -->

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
    
    <!-- the variable for the audio file - this is hardcoded now but needs to change later -->
    <!-- <xsl:variable name="AUDIO_FILE">/audio/MountStagingArea/media/audio/FOLK/FOLK_E_00002_A_01_DF_01.WAV</xsl:variable> -->
     <xsl:variable name="AUDIO_FILE">
         <!-- <xsl:value-of select="//recording/@path"/> -->
         <xsl:value-of select="tokenize(//recording/@path, '/')[last()]"/>
     </xsl:variable> 
    
    
    <!-- root template -->
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <link rel="stylesheet" type="text/css" href="http://hypermedia.ids-mannheim.de/pragdb/dgd_extern/css/folk_transcripts.css"/>
                <script src="http://hypermedia.ids-mannheim.de/pragdb/dgd_extern/js/folk_transcripts.js" type="text/javascript"></script>
                <xsl:call-template name="INSERT_JAVA_SCRIPT"/>
            </head>
            <body>
                <div class="top-transcription">
                    <!-- the audio player -->
                    <!-- controls="controls" -->                
                    <img src="stop.png" alt="Stop" name="stop_button" onclick="stop();" />
                    <audio 
                        src="./audio/dummy.wav" 
                        type="audio/wav"
                        autoplay="true"></audio>
                    <div id="current_position">0.0</div>
                    <div id="instructions">
                        <div class="instruction"><b>Doppelklick</b> im Transkript startet alignierte Aufnahme / <b>Stop-Button</b> zum Anhalten</div>
                        <div class="instruction">
                            Länge des Ausschnitts: 
                            <input id="length_input" type="text" size="4" maxlength="4" value="15.0"/>
                            s (max. 15.0s) 
                            / Audio-Format: 
                            <select id="format_input">
                                <option value="WAV">wav</option>
                                <option value="OGG">ogg</option>
                                <option value="MP3">mp3</option>
                            </select>
                        </div>
                        <!-- <div class="instruction">Klick auf den <b>Stop-Button</b> zum Anhalten der alignierten Aufnahme</div> -->
                    </div>
                    <div style="font-size:6pt;display:none;">
                        <b>Audio file: </b><span id="audio_filename">--- unknown ---</span><br/>
                        <b>Start: </b><span id="audio_start"></span><br/>
                        <b>End: </b><span id="audio_end"></span>
                    </div>
                </div>
                <div class="main-transcription">
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
                <xsl:value-of select="@speaker-reference"/>
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
            <!-- Start timestamp (needed for highlighting current audio position) -->
            <xsl:attribute name="data-start" select="round-half-to-even($TIME,2)"/>
            <!-- Start timestamp (needed for highlighting current audio position) -->
            <xsl:attribute name="data-end" select="round-half-to-even(//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time,2)"/>
            <xsl:attribute name="title">Doppelklick zum Abspielen von <xsl:value-of select="exmaralda:format_time($TIME, $INCLUDE_HOURS)"/></xsl:attribute>
            <!-- AJAX call to the audio cutting servlet -->
            <xsl:attribute name="ondblclick"><xsl:text>cutAudio2(audioID,'</xsl:text><xsl:value-of select="format-number($TIME, '#.##')"/><xsl:text>');</xsl:text></xsl:attribute>
            
            <xsl:attribute name="name"><xsl:value-of select="@start-reference"/></xsl:attribute>
            
            <!-- highlighting for overlaps via javascript -->
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
    
    <!-- javascript specific to this file -->
    <xsl:template name="INSERT_JAVA_SCRIPT">
         <script type="text/javascript">                                
            window.addEventListener("DOMContentLoaded", registerAudioListener, false);
            var audioID = '<xsl:value-of select="$AUDIO_FILE"/>'; 
            var currentStart = <xsl:value-of select="0 + //timepoint[1]/@absolute-time"/>
            <!-- removed 09-02-2012: do not automatically play audio after transcript is loaded -->
            <!-- cutAudio2(audioID, '<xsl:value-of select="0 + //timepoint[1]/@absolute-time"/>') -->
        </script>                       
    </xsl:template>
    
    
</xsl:stylesheet>
