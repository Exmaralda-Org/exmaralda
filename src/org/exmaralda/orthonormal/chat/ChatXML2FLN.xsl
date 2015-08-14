<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <folker-transcription>
            <head></head>
            <xsl:call-template name="MAKE_SPEAKERTABLE"/>
            <recording path="dummy.wav"/>
            <xsl:call-template name="MAKE_TIMELINE"/>
            <xsl:apply-templates select="//message"/>
        </folker-transcription>
    </xsl:template>
    
    <xsl:template match="message">
        <!--      <message color="red" creator="system" id="m6" type="system"> -->            
        <!--     <contribution speaker-reference="GS" start-reference="TLI_745" end-reference="TLI_746" parse-level="2" id="c661">  -->
        <contribution>
            <xsl:attribute name="speaker-reference" select="@creator"/>
            <xsl:attribute name="start-reference"><xsl:text>TLI_</xsl:text><xsl:value-of select="position()-1"/></xsl:attribute>
            <xsl:attribute name="end-reference"><xsl:text>TLI_</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
            <xsl:attribute name="parse-level">2</xsl:attribute>
            <xsl:attribute name="id" select="@id"/>
            <xsl:apply-templates/>
        </contribution>
    </xsl:template>
    
    <xsl:template match="token">
        <!-- <token id="m11.t1" lemma="." pos="$." sentence_start="true">.</token> -->
        <!-- <w id="w2443" pos="ADV" lemma="ja" i="y">ja</w> -->
        <w>
            <xsl:attribute name="id" select="@id"/>
            <xsl:attribute name="lemma" select="@lemma"/>
            <xsl:attribute name="pos" select="@pos"/>
            <xsl:if test="@n">
                <xsl:attribute name="n" select="@n"/>                
            </xsl:if>
            <xsl:if test="@i">
                <xsl:attribute name="i" select="@i"/>                
            </xsl:if>
            <xsl:value-of select="text()"/>            
        </w>                    
    </xsl:template>
    
    <!-- <messageBody>
        <nickname>Rapper9137</nickname> verläßt diesen Channel. 
    </messageBody> -->
    
    <xsl:template match="messageBody/text()">
        <text><xsl:value-of select="normalize-space(.)"/></text>
    </xsl:template>
    
    <xsl:template match="messageBody/nickname[not(descendant::token)]">
        <nickname><xsl:value-of select="text()"/></nickname>
    </xsl:template>
        
    <xsl:template match="messageBody//*[not(self::token)]/text()">
        <!-- <non-phonological description="hustet, 0.8 sek" id="n1"/> -->
        <non-phonological>
            <xsl:attribute name="description" select="normalize-space(.)"/>
        </non-phonological>
    </xsl:template>
    
    <xsl:template name="MAKE_SPEAKERTABLE">
        <speakers>
            <xsl:for-each-group select="//message" group-by="@creator">
                <!-- <speaker speaker-id="ML">
                    <name>Michael Lengefeld</name>
                </speaker> -->                
                <speaker>
                    <xsl:attribute name="speaker-id" select="current-grouping-key()"/>
                    <name></name>
                </speaker>
            </xsl:for-each-group>
        </speakers>        
    </xsl:template>
    
    <xsl:template name="MAKE_TIMELINE">
        <timeline>
            <timepoint timepoint-id="TLI_0" absolute-time="0.0"/>
            <xsl:for-each select="//message">
                <timepoint>
                    <xsl:attribute name="timepoint-id"><xsl:text>TLI_</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
                    <xsl:attribute name="absolute-time"><xsl:value-of select="position() + 0.0"/></xsl:attribute>
                </timepoint>
            </xsl:for-each>            
        </timeline>
    </xsl:template>
</xsl:stylesheet>