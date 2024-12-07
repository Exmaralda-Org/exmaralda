<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exmaralda="http://www.exmaralda.org" xmlns:tei="http://www.tei-c.org/ns/1.0" xpath-default-namespace="http://www.tei-c.org/ns/1.0" version="2.0">
    <xsl:template match="/">
        <html>
            <head> 
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <style type="text/css">
                    body {font-family:'Calibri', 'Arial', sans-serif;}
                    span.non-pho {font-weight:normal; color:rgb(100,100,100);}
                    span.time {width:12px; vertical-align:sub; font-weight:normal; color:white; background-color:rgb(100,100,100); font-size:6pt;}
                    td {border-top:1px dotted rgb(100,100,100);}
                    td.time {vertical-align:center; font-weight:normal; color:rgb(100,100,100); font-size:6pt; padding:4px;}
                    td.numbering {vertical-center:top;  font-weight:bold; color: gray; padding-left:10px; text-align:right;}
                    td.speaker {vertical-align:center; font-weight:bold; padding-left:3px; padding-right:3px;}
                    td.contribution {vertical-align:center;}
                    span.anno{font-size:8pt;}
                    td.anno{border-style:none; font-size:8pt;}
                    td.anno-type{border-style:none; font-weight:bold; color:blue; font-size:8pt;}
                </style>
            </head>
            <body>
                <table>
                    <colgroup>
                        <col/>
                        <col/>
                        <col/>
                        <col width="50%"/>
                        <col/>
                    </colgroup>
                    <xsl:apply-templates select="//div"/>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="div">
        <xsl:variable name="START_TIME">
            <xsl:value-of select="//when[@xml:id=current()/u/child::anchor[1]/@synch]/@absolute div 1000"/>
        </xsl:variable>
        <xsl:variable name="END_TIME">
            <xsl:value-of
                select="//when[@xml:id=current()/u/child::anchor[last()]/@synch]/@absolute div 1000"/>
        </xsl:variable>
        <tr>
            <td class="time">
                <!-- start time -->
                <xsl:value-of select="exmaralda:format_time($START_TIME, 'true')"/>
                <br/>
                <!-- end time -->
                <xsl:value-of select="exmaralda:format_time($END_TIME, 'true')"/>
            </td>
            <td class="numbering">
                 <xsl:value-of select="position()"/>   
            </td>
            <td class="speaker">
                <xsl:value-of select="//person[@xml:id=current()/@who]/descendant::abbr"/>
            </td>
            <td class="text">
                <xsl:apply-templates select="u"/>
            </td>
            <td>
                <table>
                    <xsl:apply-templates select="spanGrp"/>
                </table>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template match="spanGrp">
        <tr>
            <td class="anno-type"><xsl:value-of select="@type"/></td>
            <td class="anno"><xsl:apply-templates select="span"/></td>
        </tr>
    </xsl:template>
    
    <xsl:template match="span">
        <span class="time">
            <xsl:value-of select="count(//when[@xml:id=current()/@from]/preceding-sibling::when)"/>
        </span>                
        <span class="anno"><xsl:value-of select="text()"/></span>
        <span class="time">
            <xsl:value-of select="count(//when[@xml:id=current()/@to]/preceding-sibling::when)"/>
        </span>                
        <br/>
    </xsl:template>

    <xsl:template match="anchor">
        <xsl:variable name="TIME">
            <xsl:value-of select="//when[@xml:id=current()/@synch]/@absolute"/>
        </xsl:variable>
        <span class="time">
            <xsl:attribute name="title"><xsl:value-of select="$TIME"/></xsl:attribute>
            <xsl:value-of select="count(//when[@xml:id=current()/@synch]/preceding-sibling::when)"/>
        </span>        
    </xsl:template>
    
    <xsl:template match="desc">
        <span class="non-pho">
            <xsl:text>[</xsl:text><xsl:apply-templates/><xsl:text>]</xsl:text>
        </span>
    </xsl:template>
    
    <xsl:template match="pause">
        <span class="non-pho">
            <xsl:text>[</xsl:text><xsl:value-of select="@dur"/><xsl:text>]</xsl:text>
        </span>        
    </xsl:template>

    <xsl:template match="w">
        <xsl:text> </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:function name="exmaralda:format_time">
        <xsl:param name="time_sec"/>
        <xsl:param name="include_hours"/>
        <xsl:if test="string-length($time_sec)=0 or $time_sec='NaN'">
            <xsl:text/>
        </xsl:if>
        <xsl:if test="not(string-length($time_sec)=0 or $time_sec='NaN')">
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
                <!-- <xsl:value-of select="round($seconds*100) div 100"/> -->
                <xsl:value-of select="round($seconds)"/>
            </xsl:variable>
            <!-- changed 04-03-2010 -->
            <!-- <xsl:if test="$seconds+0 &lt; 10"> -->
            <xsl:if test="$roundsec+0 &lt; 10">
                <xsl:text>0</xsl:text>
            </xsl:if>
            <xsl:value-of select="$roundsec"/>
            <xsl:if test="string-length(substring-after($roundsec,'.'))=1">0</xsl:if>
             <xsl:if test="not(contains($roundsec,'.'))">.00</xsl:if> 
        </xsl:if>
    </xsl:function>

</xsl:stylesheet>
