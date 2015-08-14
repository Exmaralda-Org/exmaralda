<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:exmaralda="http://www.exmaralda.org" 
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head></head>
            <body>
                <table>
                    <tr>
                        <th>Start</th>
                        <th>Ende</th>
                        <th>Maskierung</th>
                    </tr>
                    <xsl:apply-templates select="//mask-segment"/>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="mask-segment">
        <tr>
            <td><xsl:value-of select="exmaralda:format_time(@start, 'true')"/></td>
            <td><xsl:value-of select="exmaralda:format_time(@end, 'true')"/></td>
            <td><xsl:value-of select="text()"/></td>
        </tr>
    </xsl:template>
    
    <xsl:function name="exmaralda:format_time">
        <xsl:param name="time_sec"/>
        <xsl:param name="include_hours"/>
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
        <xsl:value-of select="format-number($seconds, '00.00')"/>
    </xsl:function>
    
    
</xsl:stylesheet>