<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="TRANSCRIPTION_SYSTEM">
        <!-- <transcriptionDesc ident="cGAT" version="2014"> -->
        <xsl:choose>
            <xsl:when test="//tei:transcriptionDesc/@ident">
                <xsl:value-of select="//tei:transcriptionDesc/@ident"/>
            </xsl:when>
            <xsl:otherwise>GENERIC</xsl:otherwise>
        </xsl:choose>
    </xsl:param>
    
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name></project-name>
                    <transcription-name><xsl:value-of select="//tei:title"/></transcription-name>
                    <xsl:for-each select="//tei:recording">
                        <xsl:for-each select="tei:media">
                            <referenced-file>
                                <xsl:attribute name="url" select="@url"/>
                            </referenced-file>
                        </xsl:for-each>
                    </xsl:for-each>
                    <ud-meta-information>
                        <xsl:if test="//tei:TEI/tei:idno">
                            <ud-information>
                                <xsl:attribute name="attribute-name"><xsl:value-of select="//tei:TEI/tei:idno/@type"/></xsl:attribute>
                                <xsl:value-of select="//tei:TEI/tei:idno"/>
                            </ud-information>                            
                        </xsl:if>                           
                    </ud-meta-information>
                    <comment/>
                    <transcription-convention><xsl:value-of select="//tei:transcriptionDesc/@ident"/></transcription-convention>
                </meta-information>
                <speakertable>
                    <xsl:for-each select="//tei:person">
                        <!--                 
                            <person xml:id="JS" n="JS">
                                <idno type="AGD-ID">FOLK_S_00060</idno>
                                <persName><forename>Jana Schmidtchen</forename><abbr>JS</abbr></persName>
                            </person>
                            <speaker id="SPK0">
                                <abbreviation>AW</abbreviation>
                                <sex value="f"/>
                                <languages-used/>
                                <l1/>
                                <l2/>
                                <ud-speaker-information> </ud-speaker-information>
                                <comment/>
                            </speaker>                            
                        -->
                        <speaker>
                            <xsl:attribute name="id" select="@xml:id"/>
                            <abbreviation><xsl:value-of select="@n"/></abbreviation>
                            <sex value="u"/>
                            <languages-used/>
                            <l1/>
                            <l2/>
                            <ud-speaker-information>
                                <xsl:if test="tei:idno">
                                    <ud-information>
                                        <xsl:attribute name="attribute-name"><xsl:value-of select="tei:idno/@type"/></xsl:attribute>
                                        <xsl:value-of select="tei:idno"/>
                                    </ud-information>
                                </xsl:if>
                            </ud-speaker-information>
                            <comment/>                            
                        </speaker>
                    </xsl:for-each>
                </speakertable>
            </head>
            <basic-body>
                <!-- ******************************** -->
                <!-- ******************************** -->
                <!-- ******************************** -->
                <common-timeline>
                    <xsl:apply-templates select="//tei:when"/>
                </common-timeline>
                <!-- ******************************** -->
                <!-- ******************************** -->
                <!-- ******************************** -->
                <xsl:for-each-group select="//tei:annotationBlock" group-by="@who">
                    <tier type="t" category="v">
                        <xsl:attribute name="id" select="concat('TIE_V_', current-grouping-key())"/>
                        <xsl:attribute name="speaker" select="current-grouping-key()"></xsl:attribute>
                        <xsl:for-each select="current-group()">
                            <xsl:apply-templates select="descendant::tei:u"/>
                        </xsl:for-each>
                    </tier>
                    
                    <xsl:for-each select="distinct-values(current-group()/descendant::tei:spanGrp/@type)">
                        <xsl:variable name="TYPE" select="current()"/>
                        <tier type="a">
                            <xsl:attribute name="category" select="current()"/>
                            <xsl:attribute name="id" select="concat('TIE_' , current(), '_', current-grouping-key())"/>
                            <xsl:attribute name="speaker" select="current-grouping-key()"></xsl:attribute>
                            <xsl:for-each select="current-group()">
                                <xsl:apply-templates select="descendant::tei:spanGrp[@type=$TYPE]/tei:span"/>
                            </xsl:for-each>
                        </tier>                            
                    </xsl:for-each>
                </xsl:for-each-group>
                <!-- ******************************** -->
                <!-- ******************************** -->
                <!-- ******************************** -->
                <tier type="d" category="nn" id="TIE_GLOBAL_NN">
                    <xsl:apply-templates select="//tei:body/tei:incident | //tei:body/tei:pause"></xsl:apply-templates>
                </tier>    
                <!-- ******************************** -->
                <!-- ******************************** -->
                <!-- ******************************** -->
                <xsl:for-each select="//tei:body/tei:spanGrp">
                    <tier type="d">
                        <xsl:attribute name="id" select="concat('TIE_GLOBAL_', @type)"/>
                        <xsl:attribute name="category" select="@type"/>
                        <xsl:apply-templates select="tei:span"/>
                    </tier>
                        
                </xsl:for-each>                
            </basic-body>
        </basic-transcription>                
    </xsl:template>
    
    <xsl:template match="tei:when">
        <!-- 
            <when interval="0.0" xml:id="TLI_0" since="TLI_0"/>
            <tli id="T1" time="3.906663956865139"/>            
        -->
        <tli>
            <xsl:attribute name="id" select="@xml:id"/>
            <xsl:if test="@interval">
                <xsl:attribute name="time" select="@interval"/>
            </xsl:if>
        </tli>
        
    </xsl:template>
    
    <xsl:template match="tei:seg/tei:anchor[following-sibling::tei:anchor]">
        <event>
            <xsl:attribute name="start" select="@synch"/>
            <xsl:attribute name="end" select="following-sibling::tei:anchor[1]/@synch"/>
            <xsl:value-of select="following-sibling::text()[1]"/>
        </event>
    </xsl:template>
    
    <xsl:template match="tei:span">
        <!-- <tei:span from="TLI_1224" to="TLI_1225">fʏnf vɔlt </tei:span> -->
        <event>
            <xsl:attribute name="start" select="@from"/>
            <xsl:attribute name="end" select="@to"/>
            <xsl:value-of select="text()"/>            
        </event>
    </xsl:template>
    
    <xsl:template match="tei:seg/text()"/>
    
    <xsl:template match="tei:body/tei:incident">
        <event>
            <xsl:attribute name="start" select="@start"/>
            <xsl:attribute name="end" select="@end"/>
            <xsl:choose>
                <xsl:when test="tei:desc/@rend"><xsl:value-of select="tei:desc/@rend"/></xsl:when>
                <xsl:otherwise>
                    <xsl:text>((</xsl:text>
                    <xsl:value-of select="tei:desc"/>
                    <xsl:text>))</xsl:text>
                </xsl:otherwise>
            </xsl:choose>            
        </event>
    </xsl:template>
    
    <xsl:template match="tei:body/tei:pause">
        <event>
            <xsl:attribute name="start" select="@start"/>
            <xsl:attribute name="end" select="@end"/>
            <xsl:choose>
                <!-- <pause xml:id="p495" rend="(0.71)" dur="PT0.71S" start="TLI_950" end="TLI_951"/>  -->
                <xsl:when test="@rend"><xsl:value-of select="@rend"/></xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="DURATION" select="substring-after(substring-before(@dur,'S'), 'PT')"/>
                    <xsl:choose>
                        <xsl:when test="$TRANSCRIPTION_SYSTEM='cGAT' or $TRANSCRIPTION_SYSTEM='GAT'">(<xsl:value-of select="$DURATION"/>)</xsl:when>
                        <xsl:when test="$TRANSCRIPTION_SYSTEM='HIAT'">((<xsl:value-of select="$DURATION"/>s))</xsl:when>
                        <xsl:otherwise>(<xsl:value-of select="$DURATION"/>)</xsl:otherwise>
                    </xsl:choose>                
                </xsl:otherwise>
            </xsl:choose>
        </event>
    </xsl:template>
    
    
    
    
    
    
</xsl:stylesheet>