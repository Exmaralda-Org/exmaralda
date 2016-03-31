<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <transana-simplified-xml>
            <xsl:apply-templates select="//*:paragraph[following-sibling::*:paragraph[count(*:text)&gt;2]]"/>
        </transana-simplified-xml>
    </xsl:template>
    
    <xsl:template match="*:paragraph">
        <transana-line>
            <xsl:apply-templates select="*:text|*:symbol"/>
        </transana-line>
    </xsl:template>
    
    <!-- *************************************** -->
    <!--               Dummy Symbol              -->
    <!-- *************************************** -->
    <!-- <text textcolor="#FF0000" bgcolor="#FFFFFF" fontpointsize="14" fontstyle="90" fontweight="90" fontunderlined="0" fontface="Courier New">&#164;</text> -->
    <xsl:template match="*:text[position()=1]">
        <xsl:choose>
            <xsl:when test="text()='&#164;'">
                <xsl:comment>Dummy symbol was there</xsl:comment>                
            </xsl:when>
            <xsl:otherwise>
                <problem>No dummy symbol!</problem>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- *************************************** -->
    <!--                 Time code               -->
    <!-- *************************************** -->
    <!-- <text textcolor="#FFFFFF" bgcolor="#FFFFFF" fontpointsize="1" fontstyle="90" fontweight="90" fontunderlined="0" fontface="Times New Roman">"&lt;288012&gt; "</text> -->
    <xsl:template match="*:text[position()=2]">
        <xsl:choose>
            <xsl:when test="starts-with(text(),'&quot;&lt;') and ends-with(normalize-space(text()),'&gt; &quot;')">
                <time><xsl:value-of select="normalize-space(substring-after(substring-before(text(),'&gt;'),'&lt;'))"></xsl:value-of></time>                
            </xsl:when>
            <xsl:otherwise>
                <problem>No time code!</problem>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <!-- *************************************** -->
    <!--                 Speaker                 -->
    <!-- *************************************** -->
    <!-- <text textcolor="#000000" bgcolor="#FFFFFF" fontpointsize="10" fontstyle="90" fontweight="90" fontunderlined="0" fontface="Courier New">"Leh   "</text> -->
    <!-- <text textcolor="#000000" bgcolor="#FFFFFF" fontpointsize="10" fontstyle="90" fontweight="90" fontunderlined="0" fontface="Courier New">"Leh   Erste Frage a: Bei welchem Film wurde bislang mehr Gigabyte heruntergeladen? Yani &#351;imdi hangi filmde &#351;imdiye kadar daha fazla gigabyte indirilmi&#351;? "</text> -->
    <xsl:template match="*:text[position()=3]">
        <xsl:variable name="NORMAL_TEXT">
            <xsl:choose>
                <xsl:when test="starts-with(text(),'&quot;') and ends-with(text(),'&quot;')">
                    <xsl:value-of select="concat(normalize-space(substring(text(),2,string-length(text())-2)), ' ')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat(normalize-space(text()),' ')"/>
                </xsl:otherwise>
            </xsl:choose>                
        </xsl:variable>
        <speaker><xsl:value-of select="substring-before($NORMAL_TEXT,' ')"/></speaker>
        <!-- possibly first transcription segment glued behind speaker code -->
        <xsl:if test="string-length(substring-after($NORMAL_TEXT,' '))&gt;0">
            <segment>
                <xsl:attribute name="type" select="@textcolor"/>
                <xsl:value-of select="substring-after($NORMAL_TEXT,' ')"/>
            </segment>
        </xsl:if>
    </xsl:template>
    
    
    <!-- *************************************** -->
    <!--      Transcription segments             -->
    <!-- *************************************** -->
    <xsl:template match="*:text[position()&gt;3]">
        <segment>
            <xsl:attribute name="type" select="@textcolor"/>
            <xsl:choose>            
                <xsl:when test="starts-with(text(),'&quot;') and ends-with(text(),'&quot;')">
                    <xsl:variable name="NORMAL_TEXT" select="concat(normalize-space(substring(text(),2,string-length(text())-2)), ' ')"/>
                    <xsl:value-of select="$NORMAL_TEXT"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="text()"/>
                </xsl:otherwise>
            </xsl:choose>                    
            <!-- this one's blue, following is also blue (=translation) -->
            <xsl:if test="@textcolor='#0000FF' and following-sibling::*[1][@textcolor='#0000FF']">
                <xsl:comment>Condition #1</xsl:comment>
                <xsl:apply-templates select="following-sibling::*[1]" mode="grab"/>
            </xsl:if>
            <!-- this one's blue, following is just a space, then follows another blue (=translation) -->
            <xsl:if test="@textcolor='#0000FF' and 
                following-sibling::*[1][string-length(normalize-space(translate(text(),'&quot;',' ')))&lt;=1] and 
                following-sibling::*[2][@textcolor='#0000FF']">
                <xsl:comment>Condition #2</xsl:comment>
                <xsl:text> </xsl:text>
                <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
            </xsl:if>
            <!-- this one's black, following is just a space, then follows another black (=transcription) -->
            <xsl:if test="@textcolor='#000000' and 
                following-sibling::*[1][string-length(normalize-space(translate(text(),'&quot;',' ')))&lt;=1] and 
                following-sibling::*[2][@textcolor='#000000']">
                <xsl:comment>Condition #3</xsl:comment>
                <xsl:text> </xsl:text>
                <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
            </xsl:if>
            <!-- this one's black, following is just a symbol, then follows another black (=transcription) -->
            <xsl:if test="@textcolor='#000000' and 
                following-sibling::*[1][self::*:symbol] and 
                following-sibling::*[2][@textcolor='#000000']">
                <xsl:comment>Condition #4</xsl:comment>
                <xsl:value-of select="codepoints-to-string(following-sibling::*[1]/text())"/>
                <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
            </xsl:if>
        </segment>
    </xsl:template>
    
    <!-- OMG! This - symbol 34 - is used to encode a quote (sic!) -->
    <xsl:template match="*:symbol">
        <segment>
            <xsl:attribute name="type" select="@textcolor"/>
            <xsl:value-of select="codepoints-to-string(text())"/>
            <xsl:if test="@textcolor='#0000FF' and following-sibling::*[1][@textcolor='#0000FF']">
                <xsl:apply-templates select="following-sibling::*[1]" mode="grab"/>
            </xsl:if>
        </segment>
    </xsl:template>
    
    <!-- *************************************************************** -->
    
    <xsl:template match="*:text[position()&gt;3]" mode="grab">
        <xsl:choose>            
            <xsl:when test="starts-with(text(),'&quot;') and ends-with(text(),'&quot;')">
                <xsl:variable name="NORMAL_TEXT" select="concat(normalize-space(substring(text(),2,string-length(text())-2)), ' ')"/>
                <xsl:value-of select="$NORMAL_TEXT"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="text()"/>
            </xsl:otherwise>
        </xsl:choose>                    
        <xsl:if test="@textcolor='#0000FF' and following-sibling::*[1][@textcolor='#0000FF']">
            <xsl:apply-templates select="following-sibling::*[1]" mode="grab"/>
        </xsl:if>
        <xsl:if test="@textcolor='#0000FF' and following-sibling::*[1][string-length(normalize-space(translate(text(),'&quot;',' ')))&lt;=1] and following-sibling::*[2][@textcolor='#0000FF']">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
        </xsl:if>
        <xsl:if test="@textcolor='#000000' and following-sibling::*[1][string-length(normalize-space(translate(text(),'&quot;',' ')))&lt;=1] and following-sibling::*[2][@textcolor='#000000']">
            <xsl:text> </xsl:text>
            <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
        </xsl:if>        
        <!-- this one's black, following is just a symbol, then follows another black (=transcription) -->
        <xsl:if test="@textcolor='#000000' and 
            following-sibling::*[1][self::*:symbol] and 
            following-sibling::*[2][@textcolor='#000000']">
            <xsl:value-of select="codepoints-to-string(following-sibling::*[1]/text())"/>
            <xsl:apply-templates select="following-sibling::*[2]" mode="grab"/>
        </xsl:if>
        
    </xsl:template>
    
    <xsl:template match="*:symbol" mode="grab">
        <xsl:value-of select="codepoints-to-string(text())"/>
        <xsl:if test="@textcolor='#0000FF' and following-sibling::*[1][@textcolor='#0000FF']">
            <xsl:apply-templates select="following-sibling::*[1]" mode="grab"/>
        </xsl:if>
    </xsl:template>
    
    <!-- following nodes have already been visited in grab mode -->
    <xsl:template match="*:symbol[@textcolor='#0000FF' and preceding-sibling::*[1][@textcolor='#0000FF']]"/>
    <!-- <xsl:template match="*:text[position()&gt;4 and @textcolor='#0000FF' and preceding-sibling::*[1][@textcolor='#0000FF' or string-length(normalize-space(translate(text(),'&quot;',' ')))=0]]"/> -->
    <xsl:template match="*:text[position()&gt;4 and @textcolor='#0000FF' and preceding-sibling::*[1][@textcolor='#0000FF']]"/>
    
    <!-- <xsl:template match="*:text[position()&gt;3  and string-length(normalize-space(translate(text(),'&quot;',' ')))=0 and preceding-sibling::*[1][@textcolor='#0000FF']]"/> -->
    <!-- <xsl:template match="*:text[position()&gt;3  and @textcolor='#0000FF' and preceding-sibling::*[1]]"/> -->
    
    <xsl:template match="*:text[position()&gt;4  and string-length(normalize-space(translate(text(),'&quot;',' ')))=0 and preceding-sibling::*[1][@textcolor='#000000']]"/>
    <xsl:template match="*:text[position()&gt;4  and @textcolor='#000000' and preceding-sibling::*[1][string-length(normalize-space(translate(text(),'&quot;',' ')))=0]]"/>
    
    <xsl:template match="*:text[position()&gt;4  and @textcolor='#000000' and preceding-sibling::*[1][self::*:symbol]]"/>
    <xsl:template match="*:symbol[preceding-sibling::*[1][@textcolor='#000000'] and following-sibling::*[1][@textcolor='#000000']]"/>
    
    
    
    
</xsl:stylesheet>