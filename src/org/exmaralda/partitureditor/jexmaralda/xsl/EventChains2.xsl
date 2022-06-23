<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <xsl:variable name="SPICED_UP_TIMELINE" as="node()">
            <timeline>
                <xsl:for-each select="//tli">
                    <xsl:variable name="THIS_ID" select="@id"/>
                    <xsl:variable name="NEXT_ID" select="following-sibling::tli[1]/@id"/>
                    <xsl:copy>
                        <xsl:attribute name="id" select="@id"/>
                        <xsl:attribute name="count_start" select="count(//tier[@type='t']/event[@start=$THIS_ID and (not(preceding-sibling::event) or @start!=preceding-sibling::event[1]/@end)])"/>
                        <xsl:attribute name="count_end" select="count(//tier[@type='t']/event[@end=$THIS_ID and (not(following-sibling::event) or @end!=following-sibling::event[1]/@start)])"/>
                        <xsl:attribute name="count_continued" select="count(//tier[@type='t']/event[@start=$THIS_ID and (@start=preceding-sibling::event[1]/@end)])"/>
                    </xsl:copy>
                    
                </xsl:for-each>
            </timeline>
        </xsl:variable>
        <html>
            <head>
                
            </head>
            <body>
                                
                <xsl:for-each select="//tli">
                    <xsl:variable name="THIS_ID" select="@id"/>
                    <xsl:variable name="COUNT_START" select="$SPICED_UP_TIMELINE//tli[@id=$THIS_ID]/@count_start"/>
                    <xsl:variable name="ID" select="@id"/>
                    <xsl:variable name="NEXT_ID" select="following-sibling::tli[1]/@id"/>
                    <!-- for all start points -->
                    <xsl:if test="$COUNT_START='1'">
                        <!-- take care of events starting at that start point -->
                        <xsl:apply-templates select="//tier[@type='t']/event[@start=$ID and (not(preceding-sibling::event) or @start!=preceding-sibling::event[1]/@end)]"/> 
                        <xsl:if test="count(//tier[@type='t']/event[@start=$ID])&gt;1">
                            <xsl:apply-templates select="//tier[@type='t']/event[@start=$NEXT_ID and @start=preceding-sibling::event[1]/@end
                                and preceding-sibling::event[1]/@start=preceding-sibling::event[2]/@end]"/>
                        </xsl:if>
                    </xsl:if>                                     
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="event">
        <xsl:variable name="THIS_START" select="@start"/>
        <xsl:variable name="THIS_END" select="@end"/>
        <p>                    
            <b><xsl:value-of select="../@speaker"/>: </b>
            <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_START])&gt;1">
                <xsl:text>[</xsl:text>
            </xsl:if>
            <xsl:value-of select="normalize-space()"/>
            <xsl:if test="count(//tier[@type='t']/event[@end=$THIS_END])&gt;1">
                <xsl:text>]</xsl:text>
            </xsl:if>
            <xsl:if test="ends-with(text(),' ')"><xsl:text> </xsl:text></xsl:if>
            <xsl:choose>
                <xsl:when test="following-sibling::event[1]/@start=$THIS_END and count(//tier[@type='t']/event[@start=$THIS_END])=1">
                    <xsl:apply-templates select="following-sibling::event[1]" mode="swallow"/>
                </xsl:when>
                <xsl:when test="following-sibling::event[1]/@start=$THIS_END">
                    <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_END])&gt;1">
                        <xsl:text>[</xsl:text>
                    </xsl:if>
                    <xsl:value-of select="following-sibling::event[1]/normalize-space()"/>
                    <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_END])&gt;1">
                        <xsl:text>]</xsl:text>
                    </xsl:if>
                    <xsl:if test="ends-with(following-sibling::event[1]/text(),' ')"><xsl:text> </xsl:text></xsl:if>
                </xsl:when>
            </xsl:choose>
        </p>
    </xsl:template>

    
    <xsl:template mode="swallow" match="event">
        <xsl:variable name="THIS_START" select="@start"/>
        <xsl:variable name="THIS_END" select="@end"/>
        <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_START])&gt;1">
            <xsl:text>[</xsl:text>
        </xsl:if>
        <xsl:value-of select="normalize-space()"/>
        <xsl:if test="count(//tier[@type='t']/event[@end=$THIS_END])&gt;1">
            <xsl:text>]</xsl:text>
        </xsl:if>
        <xsl:if test="ends-with(text(),' ')"><xsl:text> </xsl:text></xsl:if>
        <xsl:choose>
            <xsl:when test="following-sibling::event[1]/@start=$THIS_END and count(//tier[@type='t']/event[@start=$THIS_END])=1">
                <xsl:apply-templates select="following-sibling::event[1]" mode="swallow"/>
            </xsl:when>
            <xsl:when test="following-sibling::event[1]/@start=$THIS_END">               
                <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_END])&gt;1">
                    <xsl:text>[</xsl:text>
                </xsl:if>
                <xsl:value-of select="following-sibling::event[1]/normalize-space()"/>
                <xsl:if test="count(//tier[@type='t']/event[@start=$THIS_END])&gt;1">
                    <xsl:text>]</xsl:text>
                </xsl:if>
                <xsl:if test="ends-with(following-sibling::event[1]/text(),' ')"><xsl:text> </xsl:text></xsl:if>                
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
</xsl:stylesheet>