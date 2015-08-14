<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        
        <doc>        
            <!-- Attributes must be checked/ filled in in JAVA -->
            <Communication Id="xxx">
                <xsl:attribute name="Name">
                    <xsl:value-of select="//transcription-name"/>
                </xsl:attribute>
                <Description>
                    <Key Name="project-name"><xsl:value-of select="//project-name"/></Key>
                    <Key Name="comment"><xsl:value-of select="//comment"/></Key>
                    <xsl:apply-templates select="//ud-meta-information//ud-information"/>                                            
                </Description>
                <Setting>
                    <xsl:for-each select="//speaker">
                        <!-- <Person> must be filled in in JAVA </Person> -->
                    </xsl:for-each>
                </Setting>
                <Transcription Id="xxx">
                    <Name><xsl:value-of select="//transcription-name"/></Name>
                    <Filename><!-- must be filled in in JAVA --></Filename>
                    <NSLink><!-- must be filled in in JAVA --></NSLink>
                    <Description>
                        <Key Name="segmented">false</Key>
                    </Description>
                    <Availability>
                        <Available>false</Available>
                        <ObtainingInformation/>
                    </Availability>
                </Transcription>
                <xsl:for-each select="//referenced-file">
                    <Recording Id="xxx">
                        <Name></Name>
                        <Media Id="xxx">
                            <Filename></Filename>
                            <!-- must be relativized in JAVA -->
                            <NSLink><xsl:value-of select="@url"/></NSLink>
                        </Media>
                        <RecordingDuration>0<!-- must be filled in in Java --></RecordingDuration>
                    </Recording>
                </xsl:for-each>
            </Communication>
            
            <xsl:for-each select="//speaker">
                <Speaker Id="xxx">
                    <Sigle>
                        <!-- must be changed according to unique speaker distinction -->
                        <xsl:value-of select="abbreviation/text()"/>
                    </Sigle>
                    <Pseudo></Pseudo>
                    <KnownHuman>true</KnownHuman>
                    <Sex>
                        <xsl:choose>
                            <xsl:when test="sex/@value='m'">male</xsl:when>
                            <xsl:when test="sex/@value='f'">female</xsl:when>
                            <xsl:otherwise>unknown</xsl:otherwise>
                        </xsl:choose>
                    </Sex>
                    <Description>
                        <xsl:apply-templates select="ud-speaker-information/ud-information"/>
                    </Description>
                </Speaker>
            </xsl:for-each>            
        </doc>
    </xsl:template>
    
    <xsl:template match="//ud-information">
        <Key>
            <xsl:attribute name="Name">
                <xsl:value-of select="@attribute-name"/>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </Key>        
    </xsl:template>
</xsl:stylesheet>
