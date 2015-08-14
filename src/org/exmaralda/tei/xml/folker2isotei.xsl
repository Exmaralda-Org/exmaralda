<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:tesla="http://www.exmaralda.org" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:isoSpoken="http://iso-tei-spoken.org/ns/1.0"
    xmlns:standoff="http://standoff.proposal">
    <xsl:output method="xml" encoding="UTF-8"/>

    <!-- ******************************************************* -->
    <!-- *************       FUNCTIONS         ***************** -->
    <!-- ******************************************************* -->
    
    <xsl:function name="tesla:determine-recording-type">
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="ends-with(lower-case($path), '.wav')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.mp3')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.aif')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.snd')">audio</xsl:when>
            <xsl:otherwise>video</xsl:otherwise>
        </xsl:choose>        
    </xsl:function>

    <xsl:function name="tesla:seconds-to-timestring">
        <xsl:param name="seconds"/>
        <xsl:variable name="totalseconds">
            <xsl:value-of select="0 + $seconds"/>
        </xsl:variable>
        <xsl:variable name="hours">
            <xsl:value-of select="0 + floor($totalseconds div 3600)"/>
        </xsl:variable>
        <xsl:variable name="minutes">
            <xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
        </xsl:variable>
        <xsl:variable name="seconds">
            <xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
        </xsl:variable>
        <xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
            <xsl:text>0</xsl:text>
            <xsl:value-of select="$hours"/>
        </xsl:if>
        <xsl:if test="$hours + 0 = 0">
            <xsl:text>00</xsl:text>                            
        </xsl:if>
        <xsl:text>:</xsl:text>
        <xsl:if test="$minutes+0 &lt; 10">
            <xsl:text>0</xsl:text>
        </xsl:if>
        <xsl:value-of select="$minutes"/>
        <xsl:text>:</xsl:text>
        <xsl:if test="$seconds+0 &lt; 10">
            <xsl:text>0</xsl:text>
        </xsl:if>
        <xsl:value-of select="$seconds"/>            
    </xsl:function>
    
    
    <!-- ****************************************************************** -->
    <!-- ************************ ROOT TEMPLATE    ************************ -->
    <!-- ****************************************************************** -->
    <xsl:template match="/">
        <TEI xmlns="http://www.tei-c.org/ns/1.0">
            <teiHeader>
                <!-- ***************************************************** -->
                <fileDesc>
                    <titleStmt>
                        <title/>
                    </titleStmt>
                    <publicationStmt>
                        <authority><!--Fill me in--></authority>
                        <availability>
                            <licence target="someurl"/>
                            <p><!--Fill me in--></p>
                        </availability>
                        <distributor><!--Fill me in--></distributor>
                        <address>
                           <addrLine><!--Fill me in--></addrLine>
                        </address>
                    </publicationStmt>
                    <!-- ***************************************************** -->
                    <sourceDesc>
                        <recordingStmt>
                            <recording type="audio">
                                <!-- element from TEI P5, but not allowed there as a child of recording -->
                                <xsl:apply-templates select="//recording"/>
                                <broadcast>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                </broadcast>
                                <!-- information about the equipment used for creating the recording -->
                                <!-- where recordings are made by the researcher, this would be -->
                                <!-- place to specify the recording equipment (e.g. Camcorder) -->
                                <equipment>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                </equipment>                  
                            </recording>
                        </recordingStmt>                                        
                    </sourceDesc>
                    <!-- ... -->
                </fileDesc>
                <!-- ***************************************************** -->                
                <profileDesc>
                    <!-- ... -->
                    <particDesc>
                        <xsl:apply-templates select="//speaker"/>
                    </particDesc>        
                    <!-- ***************************************************** -->                
                    <settingDesc>
                        <place><xsl:comment>Fill me in</xsl:comment></place>
                        <setting>
                            <activity><xsl:comment>Fill me in</xsl:comment></activity>
                        </setting>
                    </settingDesc>                    
                </profileDesc>
                <encodingDesc>
                    <appInfo>
                        <!-- information about the application with which -->
                        <!-- the transcription was created -->
                        <application ident="EXMARaLDA" version="1.5.3">
                            <label>EXMARaLDA Partitur-Editor</label>
                            <desc>Transcription Tool providing a TEI Export</desc>
                        </application>
                    </appInfo>       
                    <!-- information about the transcription convention used -->
                    <isoSpoken:transcriptionDesc ident="cGAT" version="2009">
                        <desc><xsl:comment>Fill me in</xsl:comment></desc>
                        <label><xsl:comment>Fill me in</xsl:comment></label>
                    </isoSpoken:transcriptionDesc>
                </encodingDesc>
                <revisionDesc>
                    <!-- ... -->                    
                    <xsl:element name="change">
                        <xsl:attribute name="when"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                        <xsl:text>Created by XSL transformation from a FOLKER transcription</xsl:text>
                    </xsl:element>
                </revisionDesc>                
            </teiHeader>
            <text>
                <xsl:call-template name="MAKE_TIMELINE"/>
                <body>
                    <xsl:apply-templates select="//contribution"/>
                </body>
            </text>
        </TEI>            
    </xsl:template>


       
    <xsl:template name="MAKE_TIMELINE">
        <timeline unit="s" xmlns="http://www.tei-c.org/ns/1.0">
            <!-- <xsl:attribute name="origin">
                <xsl:choose>
                    <xsl:when test="(//timepoint[1]/@absolute-time + 0.0)=0.0">#<xsl:value-of select="//timepoint[1]/@timepoint-id"/></xsl:when>
                    <xsl:otherwise>#T_START</xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute> -->
            <xsl:choose>
                <xsl:when test="(//timepoint[1]/@absolute-time + 0.0)&gt;0.0">
                    <!-- <when xml:id="T_START" absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0"/> -->     
                    <when xml:id="T_START" xmlns="http://www.tei-c.org/ns/1.0"/> 
                </xsl:when>
                <xsl:otherwise>
                    <!-- <when xmlns="http://www.tei-c.org/ns/1.0" absolute="00:00:00.0"> -->
                    <when xmlns="http://www.tei-c.org/ns/1.0">
                        <xsl:attribute name="xml:id" select="//timepoint[1]/@timepoint-id"/>                        
                    </when>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="//timepoint[position()>1]"/>
        </timeline>        
    </xsl:template>
    
    
    <xsl:template match="contribution[@speaker-reference or count(child::*[not(self::pause or self::time)])&gt;0]">
        <xsl:element name="annotationGrp" xmlns="http://standoff.proposal">            
            <xsl:if test="@speaker-reference">
                <xsl:attribute name="who">
                    <xsl:text>#</xsl:text><xsl:value-of select="translate(@speaker-reference, ' ', '_')"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:attribute name="start">
                <xsl:text>#</xsl:text><xsl:value-of select="@start-reference"/>                
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:text>#</xsl:text><xsl:value-of select="@end-reference"/>                
            </xsl:attribute>
            <xsl:element name="u" xmlns="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="xml:id">
                    <xsl:choose>
                        <xsl:when test="@id"><xsl:value-of select="@id"></xsl:value-of></xsl:when>
                        <xsl:otherwise><xsl:text>u_</xsl:text><xsl:value-of select="generate-id()"/></xsl:otherwise>
                    </xsl:choose>                    
                </xsl:attribute>
                <xsl:apply-templates/>
            </xsl:element> <!-- end u -->
            
            <!-- take care of annotations belonging to this u -->            
            <!-- normalisations, if any -->
            <xsl:if test="descendant::w[@n]">
                <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="n">
                    <xsl:for-each select="descendant::w[@n]">
                        <span>
                            <xsl:attribute name="from">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:attribute name="to">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="contains(@n,' ')">
                                    <xsl:for-each select="tokenize(@n, ' ')">
                                        <span><xsl:value-of select="current()"/></span>
                                    </xsl:for-each>                                    
                                </xsl:when>
                                <xsl:otherwise><xsl:value-of select="@n"/></xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:for-each>
                </spanGrp>
            </xsl:if>
            
            <!-- lemmatisations, if any -->
            <xsl:if test="descendant::w[@lemma]">
                <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="lemma">
                    <xsl:for-each select="descendant::w[@lemma]">
                        <span>
                            <xsl:attribute name="from">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:attribute name="to">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="contains(@lemma,' ')">
                                    <xsl:for-each select="tokenize(@lemma, ' ')">
                                        <span><xsl:value-of select="current()"/></span>
                                    </xsl:for-each>                                    
                                </xsl:when>
                                <xsl:otherwise><xsl:value-of select="@lemma"/></xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:for-each>
                </spanGrp>
            </xsl:if>
            
            <!-- POS tags, if any -->
            <xsl:if test="descendant::w[@pos]">
                <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="pos">
                    <xsl:for-each select="descendant::w[@pos]">
                        <span>
                            <xsl:attribute name="from">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:attribute name="to">#<xsl:value-of select="@id"/></xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="contains(@pos,' ')">
                                    <xsl:for-each select="tokenize(@pos, ' ')">
                                        <span><xsl:value-of select="current()"/></span>
                                    </xsl:for-each>                                    
                                </xsl:when>
                                <xsl:otherwise><xsl:value-of select="@pos"/></xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:for-each>
                </spanGrp>
            </xsl:if>
            
            
        </xsl:element> <!-- end annotationGrp -->        
    </xsl:template>

    <xsl:template match="contribution[not(@speaker-reference) and pause and count(child::*[not(self::pause or self::time)])=0]">
        <xsl:apply-templates/>
    </xsl:template>
            
    <xsl:template match="time">
        <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="synch">
                <xsl:text>#</xsl:text><xsl:value-of select="@timepoint-reference"/>
            </xsl:attribute>            
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="unparsed">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="unparsed/text()">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="w">
        <xsl:element name="w" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:choose>
                    <xsl:when test="@id"><xsl:value-of select="@id"/></xsl:when>
                    <xsl:otherwise>w<xsl:value-of select="generate-id()"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="@transition='assimilated'">
                <xsl:attribute name="type">assimilated</xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="pause">
        <xsl:element name="pause"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
                <xsl:when test="not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')">
                    <xsl:attribute name="dur">PT<xsl:value-of select="@duration"/>S</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="type"><xsl:value-of select="@duration"/></xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>            
            <xsl:if test="not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')">
                <xsl:attribute name="dur">PT<xsl:value-of select="@duration"/>S</xsl:attribute>
            </xsl:if>
            <xsl:if test="not(ancestor::*[@speaker-reference])">
                <xsl:attribute name="start">#<xsl:value-of select="ancestor-or-self::*/@start-reference"/></xsl:attribute>
                <xsl:attribute name="end">#<xsl:value-of select="ancestor-or-self::*/@end-reference"/></xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="non-phonological">
        <xsl:element name="incident"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:element name="desc">
                <xsl:value-of select="@description"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="breathe">
        <xsl:element name="vocal"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:element name="desc">
                <xsl:if test="@length='1'">short</xsl:if>
                <xsl:if test="@length='2'">medium</xsl:if>
                <xsl:if test="@length='3'">long</xsl:if>
                <xsl:text> breathe </xsl:text>
                <xsl:value-of select="@type"/>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <!-- 04-05-2015: TODO! -->
    <xsl:template match="uncertain">
        <unclear xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
                <xsl:when test="not(alternative)">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <choice xmlns="http://www.tei-c.org/ns/1.0">
                        <seg xmlns="http://www.tei-c.org/ns/1.0">
                            <xsl:apply-templates select="child::*[not(self::alternative)]"/>
                        </seg>
                        <xsl:apply-templates select="alternative"/>
                    </choice>
                </xsl:otherwise>
            </xsl:choose>
        </unclear>
        <!-- <xsl:choose>
            <xsl:when test="not(alternative)">
                <unclear xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:apply-templates/>
                </unclear>
            </xsl:when>
            <xsl:otherwise>
                <choice xmlns="http://www.tei-c.org/ns/1.0">
                    <unclear><xsl:apply-templates select="child::*[not(self::alternative)]"/></unclear>
                    <xsl:apply-templates select="alternative"/>
                </choice>
            </xsl:otherwise>
        </xsl:choose>         -->
    </xsl:template>
    
    <xsl:template match="alternative">
        <!-- <unclear  xmlns="http://www.tei-c.org/ns/1.0"><xsl:apply-templates/></unclear> -->
        <seg  xmlns="http://www.tei-c.org/ns/1.0"><xsl:apply-templates/></seg>
    </xsl:template>
            
    <xsl:template match="recording">
        <media  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="mimeType"><xsl:value-of select="tesla:determine-recording-type(@url)"/>/xxx</xsl:attribute>
            <xsl:attribute name="url"><xsl:value-of select="@path"/></xsl:attribute>
        </media>        
    </xsl:template> 
    
    <xsl:template match="speaker">
        <xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="translate(@speaker-id, ' ', '_')"/> 
            </xsl:attribute>
            <xsl:attribute name="n">
                <xsl:value-of select="@speaker-id"/>
            </xsl:attribute>
            <xsl:element name="persName">
                <xsl:element name="forename">
                    <xsl:value-of select="name"/>
                </xsl:element>
                <xsl:element name="abbr">
                    <xsl:value-of select="@speaker-id"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="timepoint[position()&gt;1]">
        <xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@timepoint-id"/>
            </xsl:attribute>
            <xsl:if test="@absolute-time">
                <xsl:attribute name="interval">
                    <xsl:value-of select="@absolute-time"/>
                </xsl:attribute>
                <xsl:attribute name="since">
                    <xsl:choose>
                        <xsl:when test="(//timepoint[1]/@absolute-time + 0.0)=0.0">#<xsl:value-of select="//timepoint[1]/@timepoint-id"/></xsl:when>
                        <xsl:otherwise>#T_START</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>                
    </xsl:template>
    
    
</xsl:stylesheet>
