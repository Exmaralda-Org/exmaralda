<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs tei"
    version="2.0">
    
    
    <xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:seg/text()">
        <xsl:variable name="SEG_ID" select="parent::tei:seg/@xml:id"/>
        <!-- The first parse is for non-verbal descriptions such as ((laughs)) -->
        <xsl:analyze-string select="." regex="\(\(([\p{{L}}]+)( *([\p{{L}}]+))*\)\)">
            <xsl:matching-substring>
                <tei:incident>
                    <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                    <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                    <tei:desc>
                        <xsl:attribute name="rend" select="."/>
                        <xsl:value-of select="substring-before(substring-after(., '(('), '))')"/>                        
                    </tei:desc>
                </tei:incident>
            </xsl:matching-substring>
            <!-- ****************************** -->
            <xsl:non-matching-substring>
                <!-- Now the rest can be parsed for "real" words, defined here as any sequence of letter symbols -->
                <xsl:analyze-string select="." regex="([\p{{L}}]+) *">
                    <xsl:matching-substring>
                        <tei:w>
                            <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                            <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                            <xsl:value-of select="normalize-space(.)"/>
                        </tei:w>
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <!-- Now it is neither a non-verbal description nor a word which leaves us with... -->
                        <xsl:choose>
                            <!-- a measured pause like so : (0.9) (this is cGAT style) -->
                            <xsl:when test="matches(normalize-space(.), '\(\d+\.\d+\)')">
                                <tei:pause>
                                    <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                                    <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                                    <xsl:attribute name="dur">PT<xsl:value-of select="substring-before(substring-after(., '('), ')')"/>S</xsl:attribute>
                                    <xsl:attribute name="rend" select="."/>
                                </tei:pause>
                            </xsl:when>
                            <!-- a micro pause like so : (.) (this is cGAT style) -->
                            <xsl:when test="normalize-space(.)='(.)'">
                                <tei:pause>
                                    <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                                    <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                                    <xsl:attribute name="type">micro</xsl:attribute>
                                    <xsl:attribute name="rend" select="."/>
                                </tei:pause>
                            </xsl:when>
                            <!-- a short pause like so : (-) (this is cGAT style) -->
                            <xsl:when test="normalize-space(.)='(-)'">
                                <tei:pause>
                                    <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                                    <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                                    <xsl:attribute name="type">short</xsl:attribute>
                                    <xsl:attribute name="rend" select="."/>
                                </tei:pause>
                            </xsl:when>
                            <!-- a nothing of the above -->
                            <xsl:otherwise>
                                <!-- make each remaining character a <pc> -->
                                <xsl:analyze-string select="normalize-space(.)" regex=".">
                                    <xsl:matching-substring>
                                        <xsl:if test="string-length(normalize-space(.))&gt;0">
                                            <tei:pc>
                                                <xsl:variable name="DUMMY_W"><w/></xsl:variable>
                                                <xsl:attribute name="xml:id" select="generate-id($DUMMY_W)"/>
                                                <xsl:value-of select="normalize-space(.)"/>
                                            </tei:pc>                                             
                                        </xsl:if>
                                    </xsl:matching-substring>
                                </xsl:analyze-string>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>                
            </xsl:non-matching-substring>            
        </xsl:analyze-string>
       
    </xsl:template>
    

    
</xsl:stylesheet>