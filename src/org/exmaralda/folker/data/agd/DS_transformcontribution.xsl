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
    

    <xsl:template match="DS_WORD">
        <xsl:element name="w">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="PF_UNINTELLIGIBLE">
        <xsl:element name="non-phonological">
            <xsl:attribute name="description">
                <xsl:text>unverständlich</xsl:text>
            </xsl:attribute>            
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="DS_BOUNDARY">
        <boundary/>
        <xsl:apply-templates select="time"/>
    </xsl:template>

    <xsl:template match="DS_PAUSE">
        <xsl:element name="pause">
            <xsl:attribute name="duration">
                <xsl:choose>
                    <xsl:when test="normalize-space()='1'">short</xsl:when>
                    <xsl:when test="normalize-space()='11'">medium</xsl:when>
                    <xsl:when test="normalize-space()='111'">long</xsl:when>
                    <xsl:otherwise><xsl:value-of select="normalize-space()"/></xsl:otherwise>         
               </xsl:choose>
            </xsl:attribute>   
        </xsl:element>
    </xsl:template>

    <xsl:template match="DS_FILLED_PAUSE">
        <annotation category="filled-pause"/>
    </xsl:template>

    <xsl:template match="DS_FRAGMENT">
        <annotation category="fragment"/>
    </xsl:template>
    <xsl:template match="DS_DIALECT">
        <annotation category="dialect"/>
    </xsl:template>

    <xsl:template match="DS_OVERLAP">
        <xsl:element name="overlap"/>
    </xsl:template>
                    
    
    <xsl:template match="DS_PUNCTUATION">
        <xsl:element name="p">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="time">
        <xsl:copy>
            <xsl:copy-of select="@timepoint-reference"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="DS_SPACE">
        <xsl:apply-templates select="*"/>
    </xsl:template>

   
    <xsl:template match="DS_COMMENT">
        <comment>
            <xsl:attribute name="description">
                <xsl:value-of select="normalize-space(substring-before(substring-after(text(),'('), ')'))"/>
            </xsl:attribute>
        </comment>
    </xsl:template>
    
    <xsl:template match="DS_LISTENER_SIGNAL">
        <annotation category="listener-signal"/>
    </xsl:template>
    
    <xsl:template match="DS_TEMPO">
        <annotation category="tempo">
            <xsl:attribute name="value">                
                <xsl:if test="text()='4s4'">faster</xsl:if>
                <xsl:if test="text()='4l4'">slower</xsl:if>
            </xsl:attribute>    
        </annotation>
    </xsl:template>
    
    <xsl:template match="DS_INTONATION">
        <annotation category="intonation">
            <xsl:attribute name="value">                
                <xsl:if test="text()='5s5'">rising</xsl:if>
                <xsl:if test="text()='5f5'">falling</xsl:if>
                <xsl:if test="text()='5g5'">level</xsl:if>
            </xsl:attribute>    
        </annotation>
    </xsl:template>

    <xsl:template match="DS_LOUDNESS">
        <annotation category="loudness">
            <xsl:attribute name="value">                
                <xsl:if test="text()='9s9'">more</xsl:if>
                <xsl:if test="text()='9l9'">less</xsl:if>
            </xsl:attribute>    
        </annotation>
    </xsl:template>
    
    <xsl:template match="DS_LENGTHENING">
        <xsl:element name="lengthening">
            <xsl:attribute name="degree">
                <xsl:value-of select="string-length()"/>
            </xsl:attribute>            
        </xsl:element>
    </xsl:template>
    

    
</xsl:stylesheet>
