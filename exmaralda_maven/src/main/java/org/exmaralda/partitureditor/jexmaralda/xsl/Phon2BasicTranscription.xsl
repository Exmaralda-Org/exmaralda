<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"  xmlns="http://phon.ling.mun.ca/ns/phonbank" xpath-default-namespace="http://phon.ling.mun.ca/ns/phonbank">
    <xsl:output method="xml" encoding="UTF-8"/>    
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name></project-name>
                    <transcription-name></transcription-name>
                    <referenced-file>
                        <xsl:attribute name="url"><xsl:value-of select="//header/media"/></xsl:attribute>
                    </referenced-file>
                    <ud-meta-information>
                        <!-- <header>
                            <date>2003-02-27+01:00</date>
                            <language></language>
                            <media>DemoVideo.mov</media>
                        </header> -->
                        <xsl:for-each select="//header/*">
                            <xsl:element name="ud-information">
                                <xsl:attribute name="attribute-name"><xsl:value-of select="name()"/></xsl:attribute>
                                <xsl:value-of select="text()"/>
                            </xsl:element>                            
                        </xsl:for-each>
                    </ud-meta-information>
                    <comment></comment>
                    <transcription-convention></transcription-convention>
                </meta-information>
                <speakertable>
                    <xsl:apply-templates select="//participant"></xsl:apply-templates>
                </speakertable>
            </head>
            <basic-body>
                   <xsl:call-template name="GENERATE-TIMELINE"/>
                   <xsl:call-template name="GENERATE-TIERS"/>
            </basic-body>
     </basic-transcription>
    </xsl:template>
    
    <xsl:template match="participant">
        <!-- <participant id="p0">
            <role></role>
            <name>Anne</name>
            <group></group>
            <sex>female</sex>
            <education></education>
            <birthday>2001-01-10+01:00</birthday>
            <language>English-French</language>
        </participant> -->
        <speaker>
            <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
            <abbreviation><xsl:value-of select="name"/></abbreviation>
            <sex>
                <xsl:attribute name="value">
                    <xsl:choose>
                        <xsl:when test="sex/text()='female'">f</xsl:when>
                        <xsl:when test="sex/text()='male'">m</xsl:when>
                        <xsl:otherwise>u</xsl:otherwise>
                    </xsl:choose>                    
                </xsl:attribute>
            </sex>
            <languages-used/>
            <l1/>
            <l2/>
            <ud-speaker-information>
                <xsl:for-each select="*">
                    <xsl:element name="ud-information">
                        <xsl:attribute name="attribute-name"><xsl:value-of select="name()"/></xsl:attribute>
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                </xsl:for-each>
            </ud-speaker-information>
            <comment></comment>
        </speaker>        
    </xsl:template>
    
    <xsl:template name="GENERATE-TIMELINE">
        <xsl:variable name="UNORDERED_TIMELINE">
            <timeline>
                <xsl:for-each select="//segment">
                    <xsl:element name="tli">
                        <xsl:attribute name="time"><xsl:value-of select="@startTime"/></xsl:attribute>
                    </xsl:element>
                    <xsl:element name="tli">
                        <xsl:attribute name="time"><xsl:value-of select="@startTime + @duration"/></xsl:attribute>
                    </xsl:element>
                </xsl:for-each>
            </timeline>
        </xsl:variable>
        
        <common-timeline>
            <xsl:for-each-group select="$UNORDERED_TIMELINE/descendant::tli" group-by="@time">
                <xsl:sort select="@time" data-type="number"/>
                    <xsl:element name="tli">
                        <xsl:attribute name="id">T<xsl:value-of select="translate(@time,'.','_')"/></xsl:attribute>
                        <xsl:attribute name="time"><xsl:value-of select="@time div 1000"/></xsl:attribute>
                    </xsl:element>
                </xsl:for-each-group>
        </common-timeline>        
    </xsl:template>
    
    <xsl:template name="GENERATE-TIERS">
        <xsl:for-each select="//participant">
            <xsl:variable name="SPEAKER_ID" select="@id"/>

            <!-- orthographic tier -->
            <xsl:element name="tier">
                <xsl:attribute name="id">TIE_<xsl:value-of select="@id"/>_ORTH</xsl:attribute>
                <xsl:attribute name="category">orth</xsl:attribute>
                <xsl:attribute name="type">t</xsl:attribute>
                <xsl:attribute name="speaker"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:for-each select="//u[@speaker=$SPEAKER_ID]">
                    <xsl:element name="event">
                        <xsl:attribute name="start">T<xsl:value-of select="translate(segment/@startTime, '.', '_')"></xsl:value-of></xsl:attribute>
                        <xsl:attribute name="end">T<xsl:value-of select="translate(string(segment/@startTime + segment/@duration), '.', '_')"></xsl:value-of></xsl:attribute> 
                        <xsl:apply-templates select="orthography/g"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>
            
            <!-- ipa model tier -->
            <xsl:element name="tier">
                <xsl:attribute name="id">TIE_<xsl:value-of select="@id"/>_IPAMODEL</xsl:attribute>
                <xsl:attribute name="category">ipa-model</xsl:attribute>
                <xsl:attribute name="type">a</xsl:attribute>
                <xsl:attribute name="speaker"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:for-each select="//u[@speaker=$SPEAKER_ID]">
                    <xsl:element name="event">
                        <xsl:attribute name="start">T<xsl:value-of select="translate(segment/@startTime, '.', '_')"></xsl:value-of></xsl:attribute>
                        <xsl:attribute name="end">T<xsl:value-of select="translate(string(segment/@startTime + segment/@duration), '.', '_')"></xsl:value-of></xsl:attribute> 
                        <xsl:apply-templates select="ipaTier[@form='model']/descendant::pg"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>            

            <!-- ipa actual tier -->
            <xsl:element name="tier">
                <xsl:attribute name="id">TIE_<xsl:value-of select="@id"/>_IPAACTUAL</xsl:attribute>
                <xsl:attribute name="category">ipa-actual</xsl:attribute>
                <xsl:attribute name="type">a</xsl:attribute>
                <xsl:attribute name="speaker"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:for-each select="//u[@speaker=$SPEAKER_ID]">
                    <xsl:element name="event">
                        <xsl:attribute name="start">T<xsl:value-of select="translate(segment/@startTime, '.', '_')"></xsl:value-of></xsl:attribute>
                        <xsl:attribute name="end">T<xsl:value-of select="translate(string(segment/@startTime + segment/@duration), '.', '_')"></xsl:value-of></xsl:attribute> 
                        <xsl:apply-templates select="ipaTier[@form='actual']/descendant::pg"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>            
            
            <!-- notes tier -->
            <xsl:element name="tier">
                <xsl:attribute name="id">TIE_<xsl:value-of select="@id"/>_NOTES</xsl:attribute>
                <xsl:attribute name="category">notes</xsl:attribute>
                <xsl:attribute name="type">a</xsl:attribute>
                <xsl:attribute name="speaker"><xsl:value-of select="@id"/></xsl:attribute>
                <xsl:for-each select="//u[@speaker=$SPEAKER_ID and string-length(notes/text())&gt;0]">
                    <xsl:element name="event">
                        <xsl:attribute name="start">T<xsl:value-of select="translate(segment/@startTime, '.', '_')"></xsl:value-of></xsl:attribute>
                        <xsl:attribute name="end">T<xsl:value-of select="translate(string(segment/@startTime + segment/@duration), '.', '_')"></xsl:value-of></xsl:attribute> 
                        <xsl:value-of select="notes/text()"/>
                    </xsl:element>
                </xsl:for-each>
            </xsl:element>            
            
        </xsl:for-each>
    </xsl:template>
    

    
    <xsl:template match="g|pg">
        <xsl:text>[</xsl:text>
        <xsl:apply-templates select="w"/>
        <xsl:text>] </xsl:text>
    </xsl:template>
    
    <xsl:template match="w">
        <xsl:value-of select="text()"/>
        <xsl:text> </xsl:text>
    </xsl:template>
    
    
</xsl:stylesheet>
