<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <!-- changes on 04-02-2009: -->
    <!-- symbol for clitics is now _ instead of + -->
    <!-- symbol for alternative is now / instaed of | -->
    <!-- symbol for breathe is now ° instead of _ -->
    <!-- symbol for unintelligible is now + instead of * -->
    <!-- changes on 06-03-2009: -->
    <!-- removed template for UNINTELLIGIBLE -->

    <xsl:template match="contribution">
        <xsl:element name="contribution">
            <xsl:copy-of select="@speaker-reference"/>
            <xsl:copy-of select="@start-reference"/>
            <xsl:copy-of select="@end-reference"/>
            <xsl:copy-of select="@parse-level"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="IS_WORD">
        <xsl:element name="w">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="IS_BOUNDARY">
        <boundary/>
        <xsl:apply-templates select="time"/>
    </xsl:template>
    
    
    <xsl:template match="IS_INTERRUPT">
        <xsl:element name="interruption"/>
    </xsl:template>
    
    
    <xsl:template match="IS_PUNCTUATION">
        <xsl:element name="p">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="time">
        <xsl:copy>
            <xsl:copy-of select="@timepoint-reference"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="IS_SPACE">
        <xsl:apply-templates select="*"/>
    </xsl:template>

   
    <xsl:template match="IS_COMMENT">
        <xsl:variable name="stripped" select="normalize-space(substring-before(substring-after(text(),'{'), '}'))"/>
        <xsl:choose>
            <xsl:when test="$stripped='-'">
                <p>-</p>
            </xsl:when>
            <xsl:when test="$stripped='('">
                <p>(</p>
            </xsl:when>
            <xsl:when test="$stripped='„' or $stripped='”'">
                <p>"</p>
            </xsl:when>    
            <xsl:when test="$stripped='” -'">
                <p>"</p><p>-</p>
            </xsl:when>    
            <xsl:when test="$stripped='| -'">
                <overlap/><p>-</p>
            </xsl:when>                
            <xsl:when test="$stripped='|'">
                <overlap/>
            </xsl:when>    
            <xsl:when test="$stripped='” „'">
                <p>"</p><p>"</p>
            </xsl:when>    
            <xsl:when test="$stripped='- „'">
                <p>-</p><p>"</p>
            </xsl:when>    
            <xsl:when test="$stripped='| **'">
                <overlap/><pause duration="medium"/>
            </xsl:when>                
            <xsl:otherwise>
                <comment>
                    <xsl:attribute name="description">
                        <xsl:value-of select="normalize-space(substring-before(substring-after(text(),'{'), '}'))"/>
                    </xsl:attribute>
                </comment>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="IS_OVERLAP | IS_WORD_INTERNAL_OVERLAP">
        <xsl:element name="overlap"/>
    </xsl:template>
    
    <xsl:template match="IS_INCOMPREHENSIBLE">
        <xsl:element name="incomprehensible">
            <xsl:attribute name="duration">
                <xsl:choose>
                    <xsl:when test="starts-with(text(),'(?')">long</xsl:when>
                    <xsl:otherwise>short</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>

    <xsl:template match="IS_PAUSE">
        <!-- 
            * kurze Pause (bis max. ½ Sekunde)
            ** etwas längere Pause (bis max. 1 Sekunde)
            *3,5* längere Pause mit Zeitangabe in Sekunden
            *4:30* lange Pause mit Zeitangabe in Minuten und Sekunden
            *+* "gefüllte Pause"        
        -->
        <xsl:variable name="stripped" select="normalize-space(substring-before(substring-after(text(),'{'), '}'))"/>
        <xsl:choose>
            <xsl:when test="$stripped='*+*'">
                <hesitation/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="pause">
                    <xsl:attribute name="duration">
                        <xsl:choose>
                            <xsl:when test="$stripped='*'">short</xsl:when>
                            <xsl:when test="$stripped='**'">medium</xsl:when>
                            <xsl:when test="$stripped='**'">long</xsl:when>
                            <xsl:otherwise><xsl:value-of select="substring-before(substring-after(text(),'*'), '*')"/></xsl:otherwise>
                        </xsl:choose>                
                    </xsl:attribute>
                </xsl:element>
            </xsl:otherwise>    
        </xsl:choose>
    </xsl:template>    
    
</xsl:stylesheet>
