<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <!-- META-INFORMATION -->
                <meta-information>
                    <project-name></project-name>
                    <transcription-name></transcription-name>
                    <referenced-file>
                        <xsl:attribute name="url">
                            <xsl:value-of select="/Trans/@audio_filename"/><xsl:text>.wav</xsl:text>
                        </xsl:attribute>
                    </referenced-file>
                    <ud-meta-information>
                    </ud-meta-information>
                    <comment>
                        <xsl:text>Imported from a transcriber file on </xsl:text><xsl:value-of select="current-dateTime()"></xsl:value-of>
                    </comment>
                    <transcription-convention></transcription-convention>
                </meta-information>
                <!-- SPEAKERTABLE -->
                <speakertable>
                    <xsl:apply-templates select="//Speaker"/>
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <xsl:for-each-group select="//@time|//@startTime|//@endTime" group-by=".">
                        <xsl:sort select="." data-type="number"/>
                        <tli>
                            <xsl:attribute name="id"><xsl:text>T</xsl:text><xsl:value-of select="translate(current-grouping-key(), '.', '-')"/></xsl:attribute>
                            <xsl:attribute name="time"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        </tli>
                    </xsl:for-each-group>
                </common-timeline>
                <xsl:for-each select="//Speaker">
                    <tier>
                        <xsl:attribute name="id"><xsl:text>TIE</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
                        <xsl:attribute name="speaker"><xsl:value-of select="@id"/></xsl:attribute>
                        <xsl:attribute name="category">v</xsl:attribute>
                        <xsl:attribute name="type">t</xsl:attribute>
                        <xsl:attribute name="display-name"><xsl:value-of select="@name"/><xsl:text> [v]</xsl:text></xsl:attribute>
                        <xsl:apply-templates select="//Turn[contains(@speaker, current()/@id)]"> 
                            <xsl:with-param name="SPEAKER"><xsl:value-of select="current()/@id"/></xsl:with-param>
                        </xsl:apply-templates>
                    </tier>                        
                </xsl:for-each>
                <tier>
                    <xsl:attribute name="id"><xsl:text>TIE_NN</xsl:text></xsl:attribute>
                    <xsl:attribute name="category">nn</xsl:attribute>
                    <xsl:attribute name="type">d</xsl:attribute>
                    <xsl:attribute name="display-name"><xsl:text>[nn]</xsl:text></xsl:attribute>
                    <xsl:apply-templates select="//Turn[not(@speaker)]"> 
                        <xsl:with-param name="SPEAKER"><xsl:text>###NO_SPEAKER###</xsl:text></xsl:with-param>
                    </xsl:apply-templates>                    
                </tier>
            </basic-body>
         </basic-transcription>
    </xsl:template>
    
    <!-- SPEAKERS -->
    <xsl:template match="Speaker">
        <speaker>
            <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
            <abbreviation><xsl:value-of select="@name"/></abbreviation>
            <sex value="u"/>
            <languages-used/>
            <l1/>
            <l2/>
            <ud-speaker-information>
                <ud-information attribute-name="Dialect"><xsl:value-of select="@dialect"/></ud-information>
                <ud-information attribute-name="Accent"><xsl:value-of select="@accent"/></ud-information>
                <ud-information attribute-name="Check"><xsl:value-of select="@check"/></ud-information>
                <ud-information attribute-name="Scope"><xsl:value-of select="@scope"/></ud-information>
            </ud-speaker-information>
            <comment/>            
        </speaker>
    </xsl:template>
    
    <xsl:template match="//Turn">
        <xsl:param name="SPEAKER"/>
        <xsl:if test="@speaker=$SPEAKER or not(@speaker)">
            <xsl:for-each select="text()">
                <xsl:if test="string-length(normalize-space())>0">
                    <xsl:variable name="START">
                        <xsl:choose>
                            <xsl:when test="preceding-sibling::Sync"><xsl:text>T</xsl:text><xsl:value-of select="translate(preceding-sibling::Sync[1]/@time,'.','-')"/></xsl:when>
                            <xsl:otherwise><xsl:text>T</xsl:text><xsl:value-of select="translate(ancestor-or-self::Turn[1]/@startTime,'.','-')"/></xsl:otherwise>
                        </xsl:choose>                                                
                    </xsl:variable>
                    <xsl:variable name="END">
                        <xsl:choose>
                            <xsl:when test="following-sibling::Sync"><xsl:text>T</xsl:text><xsl:value-of select="translate(following-sibling::Sync[1]/@time,'.','-')"/></xsl:when>
                            <xsl:otherwise><xsl:text>T</xsl:text><xsl:value-of select="translate(ancestor-or-self::Turn[1]/@endTime,'.','-')"/></xsl:otherwise>
                        </xsl:choose>                                                
                    </xsl:variable>
                    <xsl:if test="not($START=$END)">
                        <event>
                            <xsl:attribute name="start"><xsl:value-of select="$START"/></xsl:attribute>
                            <xsl:attribute name="end"><xsl:value-of select="$END"/></xsl:attribute>
                            <xsl:value-of select="normalize-space()"/>
                        </event>                            
                    </xsl:if>
                </xsl:if>
            </xsl:for-each>
        </xsl:if> 
        
        <xsl:if test="not(@speaker=$SPEAKER)">
            <xsl:variable name="SPEAKER_NUMBER">
                <xsl:choose>
                    <xsl:when test="starts-with(@speaker,$SPEAKER)">1</xsl:when>
                    <xsl:otherwise><xsl:value-of select="count(tokenize(substring-before(@speaker, $SPEAKER), ' '))"></xsl:value-of></xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:for-each select="descendant::Who[@nb=$SPEAKER_NUMBER]">
                <event>
                    <xsl:attribute name="start">
                        <xsl:choose>
                            <xsl:when test="preceding-sibling::Sync"><xsl:text>T</xsl:text><xsl:value-of select="translate(preceding-sibling::Sync[1]/@time,'.','-')"/></xsl:when>
                            <xsl:otherwise><xsl:text>T</xsl:text><xsl:value-of select="translate(ancestor-or-self::Turn[1]/@startTime,'.','-')"/></xsl:otherwise>
                        </xsl:choose>                        
                    </xsl:attribute>
                    <xsl:attribute name="end">
                        <xsl:choose>
                            <xsl:when test="following-sibling::Sync"><xsl:text>T</xsl:text><xsl:value-of select="translate(following-sibling::Sync[1]/@time,'.','-')"/></xsl:when>
                            <xsl:otherwise><xsl:text>T</xsl:text><xsl:value-of select="translate(ancestor-or-self::Turn[1]/@endTime,'.','-')"/></xsl:otherwise>
                        </xsl:choose>                        
                    </xsl:attribute>
                    <xsl:value-of select="normalize-space(following-sibling::text()[1])"/>
                </event>                
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
