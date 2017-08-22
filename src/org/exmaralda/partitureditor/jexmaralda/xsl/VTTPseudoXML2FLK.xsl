<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <folker-transcription>
            <head/>
            <speakers>
                <speaker speaker-id="S1">
                    <name/>
                </speaker>
            </speakers>
            <recording path="dummy.wav"/>
            <xsl:call-template name="MAKE_TIMELINE"></xsl:call-template>
            <xsl:apply-templates select="//cue"/>
        </folker-transcription>                
    </xsl:template>
    
    <xsl:template name="MAKE_TIMELINE">
        <timeline>
            <xsl:for-each-group select="//@start|//@end" group-by=".">
                <xsl:sort/>
                <timepoint>
                    <xsl:attribute name="timepoint-id" select="concat('T_', replace(replace(current-grouping-key(),':', '_'),'\.','_'))"/>
                    <!-- 00:00:07.500 -->
                    <xsl:attribute name="absolute-time">
                        <xsl:value-of select="xs:double(substring-before(current-grouping-key(), ':'))*60*60 
                            + xs:double(substring-before(substring-after(current-grouping-key(),':'), ':'))*60 
                            + xs:double(substring(current-grouping-key(), 7))"/>
                    </xsl:attribute>
                </timepoint>
            </xsl:for-each-group>                
        </timeline>
    </xsl:template>
    
    <xsl:template match="cue">
        <contribution speaker-reference="S1" parse-level="1">
            <xsl:attribute name="start-reference" select="concat('T_', replace(replace(@start,':', '_'),'\.','_'))"/>
            <xsl:variable name="END_REFERENCE">
                <xsl:choose>
                    <xsl:when test="following-sibling::cue"><xsl:value-of select="concat('T_', replace(replace(following-sibling::cue[1]/@start,':', '_'),'\.','_'))"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="concat('T_', replace(replace(@end,':', '_'),'\.','_'))"/></xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:attribute name="end-reference" select="$END_REFERENCE"/>
            <unparsed>
                <xsl:variable name="TEXT">
                    <xsl:apply-templates select="content/descendant::text()"/>    
                </xsl:variable>
                <xsl:value-of select="concat(normalize-space($TEXT), ' ')"/>
            </unparsed>
        </contribution>
            
    </xsl:template>
    
    
</xsl:stylesheet>