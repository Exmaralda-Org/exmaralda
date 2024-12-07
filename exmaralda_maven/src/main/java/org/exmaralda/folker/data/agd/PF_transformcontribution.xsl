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
    
    <xsl:template match="PF_WORD">
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
    
    <xsl:template match="PF_BOUNDARY">
        <boundary/>
        <xsl:apply-templates select="time"/>
    </xsl:template>

    <xsl:template match="PF_HESITATE">
        <xsl:element name="hesitation"/>
    </xsl:template>

    <xsl:template match="PF_INTERRUPT">
        <xsl:element name="interruption"/>
    </xsl:template>

    <xsl:template match="PF_FALSE_START">
        <xsl:element name="false-start"/>
    </xsl:template>
                    
    <xsl:template match="PF_INLINE_OVERLAP">
        <xsl:element name="overlap">
            <xsl:attribute name="position">
                <xsl:if test="starts-with(text(),'(')">start</xsl:if>
                <xsl:if test="not(starts-with(text(),'('))">end</xsl:if>                
            </xsl:attribute> 
            <xsl:attribute name="type">inline</xsl:attribute>
        </xsl:element>
    </xsl:template>
                    
    
    <xsl:template match="PF_PUNCTUATION">
        <xsl:element name="p">
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="time">
        <xsl:copy>
            <xsl:copy-of select="@timepoint-reference"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="PF_SPACE">
        <xsl:apply-templates select="*"/>
    </xsl:template>

   
    <xsl:template match="PF_NON_PHO_1">
        <overlap>
            <xsl:attribute name="position">
                <xsl:if test="starts-with(text(),'((')">start</xsl:if>
                <xsl:if test="not(starts-with(text(),'(('))">end</xsl:if>                
            </xsl:attribute>
        </overlap>
    </xsl:template>
    
    <xsl:template match="PF_NON_PHO_2">
        <non-phonological>
            <xsl:attribute name="description">
                <xsl:value-of select="substring-before(substring-after(text(),')'), '(')"/>
            </xsl:attribute>
        </non-phonological>
    </xsl:template>

    <xsl:template match="PF_NON_PHO_3">
        <comment>
            <xsl:attribute name="description">
                <xsl:value-of select="substring-before(substring-after(text(),')'), '(')"/>
            </xsl:attribute>
        </comment>
    </xsl:template>

    
</xsl:stylesheet>
