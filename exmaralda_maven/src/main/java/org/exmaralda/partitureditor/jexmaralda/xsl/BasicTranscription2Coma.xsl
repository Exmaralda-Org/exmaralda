<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <Corpus xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" Name="DEMO-KORPUS"
            Id="IDA6876531-A9D8-3C38-9BAA-BB30AFE13DB4"
            xsi:noNamespaceSchemaLocation="http://www.exmaralda.org/xml/CoMaCorpus.xsd"
            uniqueSpeakerDistinction="//speaker/abbreviation">
            <DBNode/>
            <CorpusData>
                <xsl:element name="Communication">
                    <xsl:attribute name="Id">x</xsl:attribute>
                    <xsl:attribute name="Name"><xsl:value-of select="//transcription-name"/></xsl:attribute>
                    <Description>
                        <Key Name="project-name"><xsl:value-of select="//project-name"/></Key>
                        <Key Name="transcription-convention"><xsl:value-of select="//transcription-convention"/></Key>
                        <Key Name="comment"><xsl:value-of select="//comment"/></Key>
                        <xsl:apply-templates select="//ud-meta-information/ud-information"/>
                    </Description>
                    <Setting>
                        <xsl:for-each select="//speaker">
                            <Person>
                                <xsl:value-of select="@id"/>
                            </Person>
                        </xsl:for-each>
                    </Setting>
                    <Transcription Id="y">
                        <Name><xsl:value-of select="//transcription-name"/></Name>
                        <Filename id="fillinfilename"></Filename>
                        <NSLink id="fillinfilename"></NSLink>
                        <Description>
                            <Key Name="segmented">true</Key>                            
                        </Description>
                        <Availability>
                            <Available>false</Available>
                            <ObtainingInformation/>
                        </Availability>                        
                     </Transcription>                        
                </xsl:element>
                
                <xsl:for-each select="//speaker">
                    <xsl:element name="Speaker">
                        <xsl:attribute name="Id"><xsl:value-of select="@id"/></xsl:attribute>
                        <Sigle><xsl:value-of select="abbreviation"/></Sigle>
                        <KnownHuman>true</KnownHuman>
                        <Pseudo><xsl:value-of select="abbreviation"/></Pseudo>
                        <Sex>
                            <xsl:if test="sex/@value='m'">male</xsl:if>
                            <xsl:if test="sex/@value='f'">female</xsl:if>
                        </Sex>
                        <Description>
                            <xsl:apply-templates select="ud-speaker-information/ud-information"/>
                        </Description>
                    </xsl:element>
                </xsl:for-each>
                
            </CorpusData>    
           </Corpus>
    </xsl:template>
    
    <xsl:template match="ud-information">
        <xsl:element name="Key">
            <xsl:attribute name="Name"><xsl:value-of select="@attribute-name"/></xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
