<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:exmaralda="https://www.exmaralda.org"
    exclude-result-prefixes="xs math"
    version="3.0">
    <xsl:variable name="TIMELINE_POSITIONS">
        <timeline>
            <xsl:for-each select="//common-timeline/tli">
                <xsl:copy>
                    <xsl:attribute name="id" select="@id"/>
                    <xsl:attribute name="position" select="position()"/>                    
                </xsl:copy>
            </xsl:for-each>
        </timeline>
    </xsl:variable>
    
    <xsl:function name="exmaralda:timelinePosition" as="xs:integer">
        <xsl:param name="ID"/>
        <xsl:sequence select="$TIMELINE_POSITIONS/descendant::tli[@id=$ID][1]/@position"/>
    </xsl:function>
    
    <xsl:template match="/">
        <tei:TEI>
            <tei:idno xmlns:tei="http://www.tei-c.org/ns/1.0" type="RANDOM-ID"><xsl:value-of select="generate-id()"/></tei:idno>
            <xsl:call-template name="INSERT_HEADER"/>
            <tei:text xml:lang="xx">
                <tei:timeline unit="s">
                    <tei:when xml:id="T_ORIGIN" since="T_ORIGIN" interval="0.0"/>
                    <xsl:for-each select="//common-timeline/tli">
                        <tei:when xml:id="{@id}" since="T_ORIGIN" interval="{@time}"/>
                    </xsl:for-each>
                </tei:timeline>
                <tei:body>
                    <xsl:apply-templates select="//segmentation[@name='SpeakerContribution_Utterance_Word']/ts"/>
                </tei:body>
            </tei:text>
        </tei:TEI>
    </xsl:template>
    
    <xsl:template match="ts[@n='sc']">
        <tei:annotationBlock>
            <xsl:variable name="SPEAKER_ID" select="ancestor::segmented-tier/@speaker"/>
            <xsl:variable name="START_POSITION" select="exmaralda:timelinePosition(@s)"/>
            <xsl:variable name="END_POSITION" select="exmaralda:timelinePosition(@e)"/>
            <xsl:attribute name="xml:id" select="@id"/>
            <xsl:attribute name="who" select="$SPEAKER_ID"/>
            <xsl:attribute name="start" select="@s"/>
            <xsl:attribute name="end" select="@e"/>
            <tei:u>
                <xsl:attribute name="xml:id" select="concat('u_', @id)"/>
                <xsl:apply-templates select="ts"/>
            </tei:u>
            
            <!-- new 12-07-2025 -->
            <xsl:for-each select="ancestor::segmented-tier[1]/descendant::annotation">
                <xsl:if test="ta[exmaralda:timelinePosition(@s) &gt;= $START_POSITION and exmaralda:timelinePosition(@e) &lt;= $END_POSITION]">
                    <tei:spanGrp>
                        <xsl:attribute name="type" select="@name"/>
                        <xsl:for-each select="ta[exmaralda:timelinePosition(@s) &gt;= $START_POSITION and exmaralda:timelinePosition(@e) &lt;= $END_POSITION]">
                            <tei:span>
                                <xsl:attribute name="from" select="@s"/>
                                <xsl:attribute name="to" select="@e"/>
                                <xsl:value-of select="text()"/>
                            </tei:span>
                        </xsl:for-each>
                    </tei:spanGrp>
                </xsl:if>
            </xsl:for-each>
            
        </tei:annotationBlock>
    </xsl:template>
    
    <xsl:template match="ts[@n='INEL:u']">
        <tei:anchor>
            <xsl:attribute name="synch" select="@s"/>
        </tei:anchor>
        <tei:seg>
            <xsl:attribute name="xml:id" select="@id"/>
            <xsl:apply-templates select="*"/>
        </tei:seg>        
        <xsl:if test="not(following-sibling::ts)">
            <tei:anchor>
                <xsl:attribute name="synch" select="@e"/>
            </tei:anchor>            
        </xsl:if>
    </xsl:template>
    
    
    <xsl:template match="ts[@n='INEL:w']">
        <xsl:if test="preceding-sibling::ts">
            <tei:anchor>
                <xsl:attribute name="synch" select="@s"/>
            </tei:anchor>
        </xsl:if>
        <tei:w>
            <xsl:attribute name="xml:id" select="@id"/>
            <xsl:value-of select="text()"/>
        </tei:w>
    </xsl:template>
        
    <xsl:template match="nts[string-length(normalize-space())&gt;0]">
        <tei:pc>
            <xsl:attribute name="xml:id" select="@id"/>
            <xsl:value-of select="text()"/>
        </tei:pc>        
    </xsl:template>
    
    <xsl:template match="ats">
        <tei:incident>
            <tei:desc><xsl:value-of select="text()"/></tei:desc>
        </tei:incident>
    </xsl:template>
    
    
        
    <xsl:template name="INSERT_HEADER">
        <tei:teiHeader>
            <tei:fileDesc>
                <tei:titleStmt>
                    <tei:title/>
                </tei:titleStmt>
                <tei:publicationStmt>
                    <tei:authority/>
                    <tei:availability>
                        <tei:licence target="someurl"/>
                        <tei:p/>
                    </tei:availability>
                    <tei:distributor/>
                    <tei:address>
                        <tei:addrLine/>
                    </tei:address>
                </tei:publicationStmt>
            
                <tei:sourceDesc>
                    <tei:recordingStmt>
                    <tei:recording type="audio">
                        <tei:media mimeType="audio/wav">
                            <xsl:attribute name="url" select="//referenced-file[1]/@url"></xsl:attribute>
                        </tei:media>
                    </tei:recording>
                    </tei:recordingStmt>
                </tei:sourceDesc>
            
            </tei:fileDesc>
            <tei:profileDesc>
                <tei:particDesc>
                    <xsl:for-each select="//speaker">
                        <tei:person>
                            <xsl:attribute name="xml:id" select="@id"/>
                            <xsl:attribute name="n" select="abbreviation"/>
                            <xsl:attribute name="sex" select="upper-case(sex/@value)"/>
                            <tei:persName>
                                <tei:forename></tei:forename>
                                <tei:abbr><xsl:value-of select="abbreviation"/></tei:abbr>
                            </tei:persName>
                        </tei:person>
                    </xsl:for-each>
                </tei:particDesc>
            </tei:profileDesc>
            
            <tei:encodingDesc>
                <tei:appInfo>
                    <tei:application ident="EXMARaLDA" version="1.8.x">
                        <tei:label>EXMARaLDA Partitur-Editor</tei:label>
                        <tei:desc>Transcription Tool providing a TEI Export</tei:desc>
                    </tei:application>
                </tei:appInfo>
                <tei:transcriptionDesc xmlns:tei="http://www.tei-c.org/ns/1.0" ident="INEL" version="1">
                    <tei:desc>INEL Conventions</tei:desc>
                    <tei:label/>
                </tei:transcriptionDesc>
            </tei:encodingDesc>
            <tei:revisionDesc>
                <tei:change when="2025-03-30T14:12:10.625+02:00">Created by XSL transformation from an
                    EXMARaLDA basic transcription</tei:change>
            </tei:revisionDesc>
            
            
            
        </tei:teiHeader>
    </xsl:template>

</xsl:stylesheet>

