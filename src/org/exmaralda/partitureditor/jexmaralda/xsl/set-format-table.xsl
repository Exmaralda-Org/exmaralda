<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:key name="tier-format-by-tierref" match="tier-format" use="@tierref"/>
    
    <!-- if no reference to a TIERFORMAT-TABLE-FILE is given, then the definitions from $DEFAULT-TIERFORMAT-TABLE are taken -->
    <xsl:param name="TIERFORMAT-TABLE-FILE" as="xs:string?" required="no"/>
    
    
    <xsl:variable name="speakers" select="distinct-values(//tier/@speaker)" as="xs:string*"/>
    <xsl:variable name="current-text" select="()"/>
    <xsl:variable name="set-format-table.tiers">
        <xsl:copy-of select="//tier"/>
    </xsl:variable>
    
    <!-- copy contents of EXB except tierformat-table which is newly generated if supplied  -->
    <xsl:template match="/">
        <xsl:comment select="' (c) http://www.rrz.uni-hamburg.de/exmaralda '"/>        
        <!-- <xsl:comment select="' set tier format table via set-format-table.xsl '"/> -->
        <basic-transcription>
            <xsl:copy-of select="/basic-transcription/@*"/>
            <xsl:copy-of select="/basic-transcription/head"/>
            <xsl:copy-of select="/basic-transcription/basic-body"/>
            <xsl:call-template name="set-format-table"/>
        </basic-transcription>
    </xsl:template>
        
    <!-- insert the tierformat-table (copied a formatting template) -->
    <xsl:template name="set-format-table">
        <xsl:param name="tiers-to-format" select="$set-format-table.tiers//tier" as="element()*"/>
        <xsl:variable name="TIERFORMAT-TABLE">
            <xsl:choose>
                <xsl:when test="doc-available($TIERFORMAT-TABLE-FILE)">
                    <xsl:copy-of select="document($TIERFORMAT-TABLE-FILE)//*:tierformat-table"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$DEFAULT-TIERFORMAT-TABLE//*:tierformat-table"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="exists($TIERFORMAT-TABLE//*:tier-format)">
                <tierformat-table>
                    <timeline-item-format><xsl:copy-of select="$TIERFORMAT-TABLE//*:timeline-item-format/@*"/></timeline-item-format>
                    <xsl:for-each select="$tiers-to-format">
                        <xsl:variable name="tier-id" select="@id" as="xs:string"/>
                        <xsl:variable name="TIERFORMAT" select="key('tier-format-by-tierref', @category, $TIERFORMAT-TABLE)" as="element()*"/>
                        <xsl:choose>
                            <xsl:when test="$TIERFORMAT[2]">
                                <xsl:message select="concat('***WARNING: two tierformats found for tier with id ''', $tier-id, ''' - chose first one.')" terminate="no"/>
                            </xsl:when>
                            <xsl:when test="empty($TIERFORMAT)">
                                <xsl:message select="concat('***WARNING: no tierformat found for tier with id ''', $tier-id, ''' - ignored.')" terminate="no"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <tier-format tierref="{$tier-id}">
                                    <xsl:for-each select="$TIERFORMAT//*:property">
                                        <property><xsl:copy-of select="@*,text()"/></property>
                                    </xsl:for-each>
                                </tier-format>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </tierformat-table>       
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="concat('***ERROR: no tierformat-table found (supplied file: ''', $TIERFORMAT-TABLE-FILE, ''').')"/>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>     
    
    
    <xsl:variable name="DEFAULT-TIERFORMAT-TABLE">
        <tierformat-table>
            <timeline-item-format show-every-nth-numbering="1" show-every-nth-absolute="1"
                absolute-time-format="time" miliseconds-digits="1"/>
            <tier-format tierref="ref">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="st">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="stl">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ts">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#R00G99B33</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="tx">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#R00G00B99</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mb">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mp">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="gr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ge">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="gg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="go">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mc">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ps">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="SeR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="SyF">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="IST">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="Top">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="Foc">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Typ">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Phon">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Morph">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="CS">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            
            <tier-format tierref="#">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fe">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#RccG00B00</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fr_ed">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#RccG00B00</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fo">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ltr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="lto">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ltg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="src">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="srl">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="nt">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="nto">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">brown</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="EMPTY">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">white</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">2</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="ROW-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Times New Roman</property>
            </tier-format>
            <tier-format tierref="SUB-ROW-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Right</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">8</property>
                <property name="font-name">Times New Roman</property>
            </tier-format>
            <tier-format tierref="EMPTY-EDITOR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">white</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">lightGray</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">2</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="COLUMN-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="TIE0">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE4">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE3">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE2">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE1">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
        </tierformat-table>
    </xsl:variable>
    
</xsl:stylesheet>