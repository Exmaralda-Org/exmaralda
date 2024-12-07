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
                            <xsl:value-of select="//video/@src"/>
                        </xsl:attribute>
                    </referenced-file>
                    <ud-meta-information>
                    </ud-meta-information>
                    <comment>
                        <xsl:text>Imported from an ANVIL file on </xsl:text><xsl:value-of select="current-dateTime()"></xsl:value-of>
                    </comment>
                    <transcription-convention></transcription-convention>
                </meta-information>
                <!-- SPEAKERTABLE -->
                <speakertable>
                    <!-- ANVIL does not define speakers -->
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <xsl:for-each-group select="//@start|//@end" group-by=".">
                        <xsl:sort select="." data-type="number"/>
                        <tli>
                            <xsl:attribute name="id"><xsl:text>T</xsl:text><xsl:value-of select="translate(current-grouping-key(), '.', '-')"/></xsl:attribute>
                            <xsl:attribute name="time"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        </tli>
                    </xsl:for-each-group>
                </common-timeline>
                <xsl:apply-templates select="//track"/>
            </basic-body>
         </basic-transcription>
    </xsl:template>
    
    <xsl:template match="track[not(@type='span')]">
        <xsl:element name="tier">
            <xsl:attribute name="id"><xsl:text>TIE</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
            <xsl:attribute name="display-name"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:attribute name="category"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:attribute name="type">
                <xsl:choose>
                    <xsl:when test="@type='primary'">t</xsl:when>
                    <xsl:otherwise>d</xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:apply-templates select="el"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="track[@type='span']">
        <!-- do nothing, for now -->
    </xsl:template>
        
    <xsl:template match="el">
        <!-- <el index="80" start="37.078414916" end="37.315021514">
            <attribute name="token">ich</attribute>
            <attribute name="emphasis">strong</attribute>
        </el> -->
        <xsl:for-each select="attribute">
            <xsl:element name="event">
                <xsl:attribute name="start">T<xsl:value-of select="translate(../@start, '.', '-')"/></xsl:attribute>
                <xsl:attribute name="end">T<xsl:value-of select="translate(../@end, '.', '-')"/></xsl:attribute>
                <xsl:element name="ud-information">
                    <xsl:attribute name="attribute-name">attribute-name</xsl:attribute>
                    <xsl:value-of select="@name"/>
                </xsl:element>
                <xsl:value-of select="text()"/>
            </xsl:element>                
        </xsl:for-each>            
    </xsl:template>
    
    
</xsl:stylesheet>
