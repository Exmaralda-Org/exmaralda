<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes"/>
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
                <style type="text/css"> body {font-family:'Courier New', 'Courier', monospace;}
                    td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100);
                    font-size:8pt;} td.numbering {vertical-align:top; font-weight:bold; color:
                    gray;} td.speaker {vertical-align:top; font-weight:bold; padding-left:3px;
                    padding-right:3px;} td.contribution {vertical-align:top;} </style>
            </head>
            <body>
                <table>
                    <xsl:apply-templates select="//contribution"> </xsl:apply-templates>
                </table>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="contribution">
        <tr>
            <td class="time">
                <xsl:text>{</xsl:text>
                <xsl:value-of
                    select="exmaralda:format_time(//timepoint[@timepoint-id=current()/@start-reference]/@absolute-time, $INCLUDE_HOURS)"/>
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
            <td class="contribution">
                <xsl:apply-templates mode="unparsed"/>
            </td>
        </tr>
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
                <!-- <xsl:value-of select="round($seconds*100) div 100"/> -->
                <xsl:value-of select="round($seconds)"/>
            </xsl:variable>
            <!-- changed 04-03-2010 -->
            <!-- <xsl:if test="$seconds+0 &lt; 10"> -->
            <xsl:if test="$roundsec+0 &lt; 10">
                <xsl:text>0</xsl:text>
            </xsl:if>
            <xsl:value-of select="$roundsec"/>
            <!-- <xsl:if test="string-length(substring-after($roundsec,'.'))=1">0</xsl:if>
			<xsl:if test="not(contains($roundsec,'.'))">.00</xsl:if> -->
        </xsl:if>
    </xsl:function>



</xsl:stylesheet>
