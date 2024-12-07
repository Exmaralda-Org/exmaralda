<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="text" encoding="UTF-8" byte-order-mark="no"/>
    <!-- *********************************** -->
    <!-- Parameters passed to the stylesheet -->
    <!-- *********************************** -->
    <xsl:param name="output-type"/>    
    
    <xsl:template match="/">
        <xsl:for-each select="//tli">
            <xsl:variable name="tliID" select="@id"/>
            <xsl:apply-templates select="//event[@start=$tliID and parent::tier[1][@type='t']]"/>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="event">        
        <xsl:variable name="speakerID" select="parent::tier/@speaker"/>
        <xsl:variable name="endID" select="@end"/>
        <xsl:value-of select="//speaker[@id=$speakerID]/abbreviation"/>
        <xsl:text>: </xsl:text>
        <xsl:apply-templates/>
        <xsl:text> #</xsl:text>
        <xsl:value-of select="exmaralda:format_f4_time(//tli[@id=$endID]/@time,'true')"/>
        <xsl:text>#</xsl:text>
        <xsl:call-template name="Newline"/>        
    </xsl:template>



    <xsl:template name="Newline"><xsl:text>
</xsl:text></xsl:template>    
    
    <xsl:function name="exmaralda:format_f4_time">
        <xsl:param name="time_sec"/>
        <xsl:param name="include_hours"/>
        <xsl:if test="string-length($time_sec)=0">
            <xsl:text></xsl:text>
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
                <xsl:variable name="rounded" select="round($seconds*10) div 10"/>
                <xsl:value-of select="$rounded"/><xsl:if test="$rounded=round($seconds)"><xsl:text>.0</xsl:text></xsl:if>
            </xsl:variable>
            <xsl:if test="$roundsec+0 &lt; 10">
                <xsl:text>0</xsl:text>
            </xsl:if>
            <xsl:value-of select="translate($roundsec, '.', '-')"/>
        </xsl:if>
    </xsl:function>
    
    
    
</xsl:stylesheet>