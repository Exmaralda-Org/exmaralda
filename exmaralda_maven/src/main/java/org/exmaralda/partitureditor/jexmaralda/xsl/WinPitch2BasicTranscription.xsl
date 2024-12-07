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
                            <xsl:value-of select="//Trans/@audioFilename"/>
                        </xsl:attribute>
                    </referenced-file>
                    <ud-meta-information>
                        <ud-information attribute-name="Creation Date"><xsl:value-of select="//Trans/@creationDate"/></ud-information>
                        
                    </ud-meta-information>
                    <comment>
                        <xsl:text>Imported from a WinPitch file on </xsl:text><xsl:value-of select="current-dateTime()"></xsl:value-of>
                    </comment>
                    <transcription-convention></transcription-convention>
                </meta-information>
                <!-- SPEAKERTABLE -->
                <speakertable>
                    <xsl:for-each select="//*[starts-with(name(), 'Layer')]">
                        <speaker>
                            <xsl:attribute name="id"><xsl:value-of select="@ID"/></xsl:attribute>
                            <abbreviation><xsl:value-of select="@Short"/></abbreviation>
                            <sex value="u"/>
                            <languages-used/>
                            <l1/>
                            <l2/>
                            <ud-speaker-information>
                                <ud-information attribute-name="Name"><xsl:value-of select="@Name"/></ud-information>
                                <ud-information attribute-name="Color"><xsl:value-of select="@Color"/></ud-information>
                            </ud-speaker-information>
                            <comment/>            
                        </speaker>                        
                    </xsl:for-each>
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <!-- <xsl:for-each-group select="//@time|//@startTime|//@endTime" group-by="."> -->
                    <xsl:for-each-group select="//@startTime|//@endTime" group-by=".">
                            <xsl:sort select="." data-type="number"/>
                        <tli>
                            <xsl:attribute name="id"><xsl:text>T</xsl:text><xsl:value-of select="translate(current-grouping-key(), '.', '-')"/></xsl:attribute>
                            <xsl:attribute name="time"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        </tli>
                    </xsl:for-each-group>
                </common-timeline>
                <xsl:for-each select="//*[starts-with(name(), 'Layer')]">
                    <tier>
                        <xsl:attribute name="id"><xsl:text>TIE</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
                        <xsl:attribute name="speaker"><xsl:value-of select="@ID"/></xsl:attribute>
                        <xsl:attribute name="category">v</xsl:attribute>
                        <xsl:attribute name="type">t</xsl:attribute>
                        <xsl:attribute name="display-name"><xsl:value-of select="@Short"/><xsl:text> [v]</xsl:text></xsl:attribute>
                        <!-- changed 20-12-2012: in C-ORAL-ROM BRASIL, it is the Name of the Layer which corresponds to the speaker of a unit... -->
                        <xsl:apply-templates select="//UNIT[@speaker=current()/@ID or @speaker = current()/@Name]"/>                         
                    </tier>                        
                </xsl:for-each>
                <!-- <tier>
                    <xsl:attribute name="id"><xsl:text>TIE_NN</xsl:text></xsl:attribute>
                    <xsl:attribute name="category">nn</xsl:attribute>
                    <xsl:attribute name="type">d</xsl:attribute>
                    <xsl:attribute name="display-name"><xsl:text>[nn]</xsl:text></xsl:attribute>
                    <xsl:apply-templates select="//Turn[not(@speaker)]"> 
                        <xsl:with-param name="SPEAKER"><xsl:text>###NO_SPEAKER###</xsl:text></xsl:with-param>
                    </xsl:apply-templates>                    
                </tier> -->
            </basic-body>
         </basic-transcription>
    </xsl:template>
    
    
    <xsl:template match="//UNIT">
                <xsl:if test="string-length(normalize-space())>0">
                    <xsl:variable name="START">
                            <xsl:text>T</xsl:text><xsl:value-of select="translate(@startTime,'.','-')"/>
                    </xsl:variable>
                    <xsl:variable name="END">
                            <xsl:text>T</xsl:text><xsl:value-of select="translate(@endTime,'.','-')"/>
                    </xsl:variable>
                        <event>
                            <xsl:attribute name="start"><xsl:value-of select="$START"/></xsl:attribute>
                            <xsl:attribute name="end"><xsl:value-of select="$END"/></xsl:attribute>
                            <xsl:value-of select="normalize-space()"/>
                        </event>                            
               </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
