<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0"> 
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name></project-name>
                    <transcription-name></transcription-name>
                    <referenced-file url=""/>
                    <ud-meta-information>
                        <ud-information attribute-name="X">Y</ud-information>
                    </ud-meta-information>
                    <comment>
                        <!-- inconsistency problems here -->
                    </comment>
                    <transcription-convention/>
                </meta-information>
                <speakertable>
                    <xsl:for-each-group select="//transana-line[not(problem)]/speaker" group-by="text()">
                        <speaker>
                            <xsl:attribute name="id" select="upper-case(current-grouping-key())"/>
                            <abbreviation><xsl:value-of select="current-grouping-key()"/></abbreviation>
                            <sex value="u"/>
                            <languages-used/>
                            <l1/>
                            <l2/>
                            <ud-speaker-information> </ud-speaker-information>
                            <comment/>
                        </speaker>                        
                    </xsl:for-each-group>
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <!-- <tli id="T0" time="0.0"/> -->
                    <xsl:for-each select="//transana-line">
                            <tli>
                                <xsl:attribute name="id"><xsl:text>T_</xsl:text><xsl:value-of select="count(preceding-sibling::transana-line[speaker])"/><xsl:text>_0</xsl:text></xsl:attribute>
                                <xsl:if test="time">                                    
                                    <xsl:attribute name="time"><xsl:value-of select="substring(time,1,string-length(time)-3)"/><xsl:text>.</xsl:text><xsl:value-of select="substring(time,string-length(time)-2)"/></xsl:attribute>
                                </xsl:if>                                
                            </tli>
                            <xsl:for-each select="segment[not(@type='#0000FF') and not(position()=1)]">
                                <tli>
                                    <xsl:attribute name="id">
                                        <xsl:text>T_</xsl:text>
                                        <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])"/>
                                        <xsl:text>_</xsl:text>
                                        <xsl:value-of select="count(preceding-sibling::segment[not(@type='#0000FF')])"/>
                                    </xsl:attribute>                                    
                                </tli>                                
                            </xsl:for-each>
                    </xsl:for-each>
                    <tli id="T_LAST"/>
                </common-timeline>
                <xsl:for-each-group select="//transana-line[not(problem)]/speaker" group-by="text()">
                    <tier category="v" type="t">
                        <xsl:attribute name="id"><xsl:text>TIE_</xsl:text><xsl:value-of select="upper-case(current-grouping-key())"/><xsl:text>_V</xsl:text></xsl:attribute>
                        <xsl:attribute name="speaker" select="upper-case(current-grouping-key())"/>
                        <xsl:apply-templates select="//transana-line[speaker=current-grouping-key() and (not(problem))]/segment[not(@type='#0000FF')]"/>
                    </tier>
                    <tier category="de" type="a">
                        <xsl:attribute name="id"><xsl:text>TIE_</xsl:text><xsl:value-of select="upper-case(current-grouping-key())"/><xsl:text>_DE</xsl:text></xsl:attribute>
                        <xsl:attribute name="speaker" select="upper-case(current-grouping-key())"/>                            
                        <xsl:apply-templates select="//transana-line[speaker=current-grouping-key() and (not(problem))]/segment[@type='#0000FF']"/>
                    </tier>
                </xsl:for-each-group>
            </basic-body>
        </basic-transcription>                    
    </xsl:template>
    
    <xsl:template match="segment[not(@type='#0000FF')]">
        <event>
            <xsl:attribute name="start">
                <xsl:text>T_</xsl:text>
                <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="count(preceding-sibling::segment[not(@type='#0000FF')])"/>
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:choose>
                    <xsl:when test="following-sibling::segment[not(@type='#0000FF')]">
                        <xsl:text>T_</xsl:text>
                        <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])"/>
                        <xsl:text>_</xsl:text>
                        <xsl:value-of select="count(preceding-sibling::segment[not(@type='#0000FF')])+1"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="../following-sibling::transana-line[speaker]">
                                <xsl:text>T_</xsl:text>
                                <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])+1"/>
                                <xsl:text>_0</xsl:text>                                
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>T_LAST</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </event>
    </xsl:template>
    
    <xsl:template match="segment[@type='#0000FF']">
        <event>
            <xsl:attribute name="start">
                <xsl:text>T_</xsl:text>
                <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="count(preceding-sibling::segment[not(@type='#0000FF')])-1"/>
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:choose>
                    <xsl:when test="following-sibling::segment[not(@type='#0000FF')]">
                        <xsl:text>T_</xsl:text>
                        <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])"/>
                        <xsl:text>_</xsl:text>
                        <xsl:value-of select="count(preceding-sibling::segment[not(@type='#0000FF')])"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="../following-sibling::transana-line[speaker]">
                                <xsl:text>T_</xsl:text>
                                <xsl:value-of select="count(../preceding-sibling::transana-line[speaker])+1"/>
                                <xsl:text>_0</xsl:text>                                
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>T_LAST</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </event>
    </xsl:template>
    
</xsl:stylesheet>