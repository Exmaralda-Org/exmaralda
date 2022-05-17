<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <html>
            <head>
                
            </head>
            <body>
                <xsl:for-each select="//tli">
                    <xsl:variable name="ID" select="@id"/>
                    <xsl:variable name="NEXT_ID" select="following-sibling::tli[1]/@id"/>
                    <!-- for all start points -->
                    <xsl:if test="//tier[@type='t']/event[@start=$ID and (not(preceding-sibling::event) or @start!=preceding-sibling::event[1]/@end)]">
                        <!-- take care of events starting at that start point -->
                        <xsl:apply-templates select="//tier[@type='t']/event[@start=$ID and (not(preceding-sibling::event) or @start!=preceding-sibling::event[1]/@end)]"/> 
                        <xsl:if test="count(//tier[@type='t']/event[@start=$ID])&gt;1">
                            <xsl:apply-templates select="//tier[@type='t']/event[@start=$NEXT_ID and @start=preceding-sibling::event[1]/@end]"/>
                        </xsl:if>
                    </xsl:if>                                     
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="event">
        <xsl:variable name="THIS_END" select="@end"/>
        <p>                    
            <b><xsl:value-of select="../@speaker"/>: </b><xsl:value-of select="text()"/>
            <xsl:if test="following-sibling::event[1]/@start=$THIS_END and count(//tier[@type='t']/event[@start=$THIS_END])=1">
                <xsl:apply-templates select="following-sibling::event[1]" mode="swallow"/>
            </xsl:if>
        </p>
    </xsl:template>

    
    <xsl:template mode="swallow" match="event">
        <xsl:variable name="THIS_END" select="@end"/>
        <xsl:value-of select="text()"/>
        <xsl:choose>
            <xsl:when test="following-sibling::event[1]/@start=$THIS_END and count(//tier[@type='t']/event[@start=$THIS_END])=1">
                <xsl:apply-templates select="following-sibling::event[1]" mode="swallow"/>
            </xsl:when>
            <xsl:when test="following-sibling::event[1]/@start=$THIS_END">
                <xsl:value-of select="following-sibling::event[1]/text()"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    
</xsl:stylesheet>