<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:template match="/">
        <wordlist>
        <xsl:for-each select="//ts[substring-after(@n, ':')='w']">
            <word>
                <xsl:attribute name="speaker">
                    <xsl:value-of select="ancestor::*[@speaker][1]/@speaker"/>
                </xsl:attribute>
                <xsl:attribute name="start">
                    <xsl:choose>
                        <xsl:when test="//common-timeline/tli[@id=current()/@s]">
                            <xsl:value-of select="@s"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="//timeline-fork[tli[@id=current()/@s]]/@start"/>
                        </xsl:otherwise>
                    </xsl:choose>                    
                </xsl:attribute>
                <xsl:attribute name="tier">
                    <xsl:value-of select="ancestor::*[@tierref][1]/@tierref"/>                    
                </xsl:attribute>
                <xsl:value-of select="text()"/>
            </word>
        </xsl:for-each>
        </wordlist>
    </xsl:template>
</xsl:stylesheet>
