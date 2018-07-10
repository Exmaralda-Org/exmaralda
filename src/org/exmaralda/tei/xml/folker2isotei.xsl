<?xml version="1.0" encoding="UTF-8"?>
<!-- change 03-03-2016: additional namespaces no longer necessary 
        xmlns:isoSpoken="http://iso-tei-spoken.org/ns/1.0"
        xmlns:standoff="http://standoff.proposal"
-->        
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:exmaralda="http://www.exmaralda.org" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema">

    <!-- new 08-07-2016 -->
    <!-- A parameter can be passed spcifying the language of the document -->
    <!-- xx is used as a default value if no such parameter is passed -->
    <xsl:param name="LANGUAGE">xx</xsl:param>
    <!-- new 22-06-2018 -->
    <!-- if this parameter is set to TRUE, <w> elements will get inline attributes @norm, @lemma and @pos -->
    <xsl:param name="MAKE_INLINE_ATTRIBUTES">TRUE</xsl:param>
    <!-- new 22-06-2018 -->
    <!-- if this parameter is set to TRUE, <span> elements will be produced for normalisation, lemmatisation and POS tags -->
    <xsl:param name="MAKE_STANDOFF_ANNOTATIONS">FALSE</xsl:param>
    <!-- new 25-06-2018 -->
    <!-- if this parameter is set to TRUE, XPointers will be used instead of IDREFs -->
    <xsl:param name="USE_XPOINTER">FALSE</xsl:param>
    <!-- new 05-07-2018 -->
    <!-- if this parameter is set to TRUE, a <seg type='contribution'> will be introduced -->
    <xsl:param name="ENFORCE_SEG">TRUE</xsl:param>
    
    <xsl:output method="xml" encoding="UTF-8"/>
    
    <xsl:variable name="ANONYMOUS_SPEAKER_ID" select="generate-id()"/>
    
    <xsl:variable name="XPOINTER_HASH">
        <xsl:choose>
            <xsl:when test="$USE_XPOINTER='TRUE'">#</xsl:when>
            <xsl:otherwise></xsl:otherwise>
        </xsl:choose>            
    </xsl:variable>

    <!-- ******************************************************* -->
    <!-- *************       FUNCTIONS         ***************** -->
    <!-- ******************************************************* -->
    
    <xsl:function name="exmaralda:determine-recording-type">
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="ends-with(lower-case($path), '.wav')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.mp3')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.aif')">audio</xsl:when>
            <xsl:when test="ends-with(lower-case($path), '.snd')">audio</xsl:when>
            <xsl:otherwise>video</xsl:otherwise>
        </xsl:choose>        
    </xsl:function>

    <xsl:function name="exmaralda:seconds-to-timestring">
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
            <!-- new 25-04-2018: this is for pointing to the corresponding AGD-ID -->
            <xsl:if test="/*/@dgd-id ">
                <idno type="AGD-ID"><xsl:value-of select="/*/@dgd-id"/></idno>
            </xsl:if>            
            <teiHeader>
                <!-- ***************************************************** -->
                <fileDesc>
                    <titleStmt>
                        <title><xsl:value-of select="/*/@dgd-id"/></title>
                    </titleStmt>
                    <!-- <publicationStmt>
                        <authority/>
                        <availability>
                            <licence target="someurl"/>
                            <p></p>
                        </availability>
                        <distributor></distributor>
                        <address>
                           <addrLine></addrLine>
                        </address>
                    </publicationStmt> -->
                    <!-- ***************************************************** -->
                    <sourceDesc>
                        <recordingStmt>
                            <recording type="audio">
                                <!-- element from TEI P5, but not allowed there as a child of recording -->
                                <xsl:apply-templates select="//recording"/>
                                <!-- <broadcast>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                </broadcast> -->
                                <!-- information about the equipment used for creating the recording -->
                                <!-- where recordings are made by the researcher, this would be -->
                                <!-- place to specify the recording equipment (e.g. Camcorder) -->
                                <!-- <equipment>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                    <ab><xsl:comment>Fill me in</xsl:comment></ab>
                                </equipment> -->                  
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
                        <xsl:if test="//contribution[@speaker-reference or count(child::*[not(self::pause or self::non-phonological or self::time)])&gt;0]">
                            <!-- generate an anonymous speaker -->
                            <person>
                                <xsl:attribute name="xml:id" select="$ANONYMOUS_SPEAKER_ID"/>
                                <xsl:attribute name="n" select="$ANONYMOUS_SPEAKER_ID"/>
                                <persName>
                                    <!-- <forename>Anonymous</forename> -->
                                    <abbr><xsl:value-of select="$ANONYMOUS_SPEAKER_ID"/></abbr>
                                </persName>
                            </person>                            
                        </xsl:if>
                    </particDesc>        
                    <!-- ***************************************************** -->                
                    <!-- <settingDesc>
                        <place><xsl:comment>Fill me in</xsl:comment></place>
                        <setting>
                            <activity><xsl:comment>Fill me in</xsl:comment></activity>
                        </setting>
                    </settingDesc> -->                    
                </profileDesc>
                <encodingDesc>
                    <appInfo>
                        <!-- information about the application with which -->
                        <!-- the transcription was created -->
                        <application ident="FOLKER" version="1.1">
                            <label>FOLK Editor</label>
                            <desc>Transcription Tool providing a TEI Export</desc>
                        </application>
                    </appInfo>       
                    <!-- information about the transcription convention used -->
                    <!-- change 03-03-2016: namespace switch no longer necessary -->
                    <transcriptionDesc ident="cGAT" version="2014">
                        <desc><xsl:comment>Fill me in</xsl:comment></desc>
                        <label><xsl:comment>Fill me in</xsl:comment></label>
                    </transcriptionDesc>
                </encodingDesc>
                <revisionDesc>
                    <!-- ... -->                    
                    <xsl:element name="change">
                        <xsl:attribute name="when">
                            <xsl:value-of select="current-dateTime()"/>
                        </xsl:attribute>
                        <xsl:text>Created by XSL transformation from a FOLKER transcription</xsl:text>
                    </xsl:element>
                </revisionDesc>                
            </teiHeader>
            <text>
                <!-- new 08-07-2016 -->
                <!-- encode the language here -->
                <xsl:attribute name="xml:lang" select="$LANGUAGE"/>
                <xsl:call-template name="MAKE_TIMELINE"/>
                <body>
                    <xsl:apply-templates select="//contribution"/>
                </body>
            </text>
        </TEI>            
    </xsl:template>


       
    <xsl:template name="MAKE_TIMELINE">
        <timeline unit="s" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:choose>
                <xsl:when test="(//timepoint[1]/@absolute-time + 0.0)&gt;0.0">
                    <!-- <when xml:id="T_START" absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0"/> -->     
                    <when xml:id="T_START" interval="0.0" xmlns="http://www.tei-c.org/ns/1.0">
                        <xsl:attribute name="since"><xsl:value-of select="$XPOINTER_HASH"/>T_START</xsl:attribute>                                                
                    </when> 
                    <xsl:apply-templates select="//timepoint"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- <when xmlns="http://www.tei-c.org/ns/1.0" absolute="00:00:00.0"> -->
                    <when xmlns="http://www.tei-c.org/ns/1.0" interval="0.0">
                        <xsl:attribute name="xml:id" select="//timepoint[1]/@timepoint-id"/>                        
                        <xsl:attribute name="since"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="//timepoint[1]/@timepoint-id"/></xsl:attribute>                        
                    </when>
                    <xsl:apply-templates select="//timepoint[position()>1]"/>
                </xsl:otherwise>
            </xsl:choose>
        </timeline>        
    </xsl:template>
    
    
    <!-- contributions which have a speaker or something other than a pause, a non-pho, a breathe or a time -->
    <xsl:template match="contribution[@speaker-reference or count(child::*[not(self::pause or self::non-phonological or self::breathe or self::time)])&gt;0]">
        <!-- change 03-03-2016: element renamed, namespace switch no longer necessary -->        
        <xsl:element name="annotationBlock" xmlns="http://www.tei-c.org/ns/1.0">            
            <xsl:attribute name="xml:id">
                <xsl:choose>
                    <xsl:when test="@id"><xsl:value-of select="@id"></xsl:value-of></xsl:when>
                    <xsl:otherwise><xsl:text>ab_</xsl:text><xsl:value-of select="generate-id()"/></xsl:otherwise>
                </xsl:choose>                    
            </xsl:attribute>
            <xsl:attribute name="who">
                <xsl:choose>
                    <xsl:when test="@speaker-reference">
                        <xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="translate(@speaker-reference, ' ', '_')"/>                        
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="$ANONYMOUS_SPEAKER_ID"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="start">
                <xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@start-reference"/>                
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@end-reference"/>                
            </xsl:attribute>
            <xsl:element name="u" xmlns="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="xml:id">
                     <xsl:text>u_</xsl:text><xsl:value-of select="generate-id()"/>
                </xsl:attribute>
                <!-- changed 05-07-2018 - enforce a seg if so desired -->
                <xsl:choose>
                    <xsl:when test="$ENFORCE_SEG='TRUE' and not(child::line)">
                        <seg xmlns="http://www.tei-c.org/ns/1.0" type="contribution">
                            <xsl:attribute name="xml:id">seg_<xsl:value-of select="generate-id()"/></xsl:attribute>                                        
                            <xsl:apply-templates/>
                        </seg>                           
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates/>                        
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:element> <!-- end u -->
            
            <!-- take care of annotations belonging to this u -->            
            
            <!-- alternative transcriptions if any (new 01-06-2018) -->
            <xsl:if test="descendant::alternative">
                <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="alternative">
                    <xsl:for-each select="descendant::alternative">
                        <!-- <uncertain id="u23" ol="in">
                            <w id="w601" n="ist" pos="VAFIN" lemma="sein" ol="in">is</w>
                            <w id="w602" n="wahr" pos="ADJD" lemma="wahr" ol="in">wahr</w>
                            <alternative id="a1" ol="in">
                                <w id="w603" n="vier" pos="CARD" lemma="vier" ol="in">vier</w>
                                <w id="w604" n="zwei" pos="CARD" lemma="zwei" ol="in">zwei</w>
                            </alternative>
                        </uncertain> -->
                        <span>
                            <xsl:attribute name="from"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor::uncertain/child::w[1]/@id"/></xsl:attribute>
                            <xsl:attribute name="to"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor::uncertain/child::w[last()]/@id"/></xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="count(w)=1"><xsl:value-of select="w[1]"/></xsl:when>
                                <xsl:otherwise>
                                    <xsl:for-each select="w">
                                        <span><xsl:value-of select="text()"/></span>
                                    </xsl:for-each>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:for-each>
                </spanGrp>                    
            </xsl:if>
            
            <!-- new 22-06-2018 - make these standoff-annotations optional -->
            <xsl:if test="$MAKE_STANDOFF_ANNOTATIONS='TRUE'">
            
                <!-- normalisations, if any -->
                <xsl:if test="descendant::w[@n]">
                    <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="norm">
                        <!-- change 01-06-2018 : discard annotations of alternatives -->
                        <xsl:for-each select="descendant::w[@n and not(ancestor::alternative)]">
                            <span>
                                <xsl:attribute name="from"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="to"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
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
                        <!-- change 01-06-2018 : discard annotations of alternatives -->
                        <xsl:for-each select="descendant::w[@lemma and not(ancestor::alternative)]">
                            <span>
                                <xsl:attribute name="from"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="to"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
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
                        <!-- change 01-06-2018 : discard annotations of alternatives -->
                        <xsl:for-each select="descendant::w[@pos and not(ancestor::alternative)]">
                            <span>
                                <xsl:attribute name="from"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
                                <xsl:attribute name="to"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@id"/></xsl:attribute>
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
            </xsl:if>
        </xsl:element> <!-- end annotationGrp -->        
    </xsl:template>

    <xsl:template match="contribution[not(@speaker-reference) 
        and (pause|non-phonological|breathe) and count(child::*[not(self::pause or self::non-phonological or self::breathe or self::time)])=0]">
        <xsl:apply-templates select="pause|non-phonological|breathe"/>
    </xsl:template>
            
    <xsl:template match="line">
        <!-- <line>
            <w>scheiß</w>
            <time timepoint-reference="TLI_13"/>
            <w>dosenöffner</w>
            <boundary type="final" movement="not-qualified" latching="no"/>
        </line> -->
        <seg xmlns="http://www.tei-c.org/ns/1.0" type="line">
            <xsl:attribute name="xml:id">
                <xsl:choose>
                    <xsl:when test="@id"><xsl:value-of select="@id"/></xsl:when>
                    <xsl:otherwise>seg_<xsl:value-of select="generate-id()"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>            
            <xsl:attribute name="subtype">
                <xsl:value-of select="child::boundary/@type"/>
                <xsl:text> </xsl:text>
                <xsl:value-of select="child::boundary/@movement"/>
                <xsl:if test="child::boundary/@latching='yes'">
                    <xsl:text> latching</xsl:text>
                </xsl:if>
            </xsl:attribute>
            <xsl:apply-templates/>            
        </seg>
            
        
    </xsl:template>
    
    <xsl:template match="time">
        <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="synch">
                <xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@timepoint-reference"/>
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
                    <xsl:otherwise>w_<xsl:value-of select="generate-id()"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="@transition or ancestor::uncertain">
                <xsl:attribute name="type">
                    <xsl:variable name="TYPE_VALUE">
                        <xsl:if test="@transition='assimilated'">assimilated </xsl:if>
                        <!-- added 05-07-2018 -->
                        <xsl:if test="ancestor::uncertain">uncertain </xsl:if>
                    </xsl:variable>
                    <xsl:value-of select="normalize-space($TYPE_VALUE)"/>
                </xsl:attribute>
            </xsl:if>
            
            <!-- new 22-06-2018: optionally include inline attributes -->
            <xsl:if test="$MAKE_INLINE_ATTRIBUTES='TRUE'">
                <xsl:if test="@n"><xsl:attribute name="norm" select="@n"></xsl:attribute></xsl:if>
                <xsl:if test="@lemma"><xsl:attribute name="lemma" select="@lemma"></xsl:attribute></xsl:if>
                <xsl:if test="@pos"><xsl:attribute name="pos" select="@pos"></xsl:attribute></xsl:if>
            </xsl:if>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="pause">
        <xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
            <!-- added 01-06-2018 -->
            <xsl:attribute name="rend">
                <xsl:choose>
                    <xsl:when test="@duration='micro'">(.)</xsl:when>
                    <xsl:when test="@duration='short'">(-)</xsl:when>
                    <xsl:when test="@duration='medium'">(--)</xsl:when>
                    <xsl:when test="@duration='long'">(---)</xsl:when>
                    <xsl:otherwise>
                        <xsl:text>(</xsl:text>
                        <xsl:value-of select="@duration"/>
                        <xsl:text>)</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
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
                <xsl:attribute name="start"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@start-reference"/></xsl:attribute>
                <xsl:attribute name="end"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@end-reference"/></xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="non-phonological">
        <xsl:element name="incident"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:if test="not(ancestor::*[@speaker-reference])">
                <xsl:attribute name="start"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@start-reference"/></xsl:attribute>
                <xsl:attribute name="end"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@end-reference"/></xsl:attribute>
            </xsl:if>
            <xsl:element name="desc">
                <xsl:value-of select="@description"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="breathe">
        <xsl:element name="vocal"  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:if test="not(ancestor::*[@speaker-reference])">
                <xsl:attribute name="start"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@start-reference"/></xsl:attribute>
                <xsl:attribute name="end"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="ancestor-or-self::*/@end-reference"/></xsl:attribute>
            </xsl:if>
            <xsl:element name="desc">
                <!-- new 01-06-2018 -->
                <xsl:attribute name="rend">
                    <xsl:if test="@type='in'">°</xsl:if>
                    <xsl:for-each select="(1 to @length)">
                        <xsl:text>h</xsl:text>
                    </xsl:for-each>                    
                    <xsl:if test="@type='out'">°</xsl:if>
                </xsl:attribute>
                <xsl:if test="@length='1'">short</xsl:if>
                <xsl:if test="@length='2'">medium</xsl:if>
                <xsl:if test="@length='3'">long</xsl:if>
                <xsl:text> breathe </xsl:text>
                <xsl:value-of select="@type"/>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <!-- 04-05-2015: TODO! -->
    <!-- changed 01-06-2018 -->
    <!-- changed again because alternatives should be mapped onto a spanGrp -->
    <!-- changed again (05-07-2018) uncertainty should be encoded in an attribute @type -->
    <xsl:template match="uncertain">
        <!-- <unclear xmlns="http://www.tei-c.org/ns/1.0"> -->
            <xsl:apply-templates select="child::*[not(self::alternative)]"/>
        <!-- </unclear> -->
        <!-- <xsl:choose>
                <xsl:when test="not(alternative)">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <choice xmlns="http://www.tei-c.org/ns/1.0">
                        <seg xmlns="http://www.tei-c.org/ns/1.0">
                            <xsl:attribute name="xml:id">
                                <xsl:choose>
                                    <xsl:when test="@id"><xsl:value-of select="@id"/></xsl:when>
                                    <xsl:otherwise>w<xsl:value-of select="generate-id()"/></xsl:otherwise>
                                </xsl:choose>
                            </xsl:attribute>                                
                            <xsl:apply-templates select="child::*[not(self::alternative)]"/>
                        </seg>
                        <xsl:apply-templates select="alternative"/>
                    </choice>
                </xsl:otherwise>
            </xsl:choose> -->
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
        <seg  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:choose>
                    <xsl:when test="@id"><xsl:value-of select="@id"/></xsl:when>
                    <xsl:otherwise>w<xsl:value-of select="generate-id()"/></xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>                                
            <xsl:apply-templates/>
        </seg>
    </xsl:template>
            
    <xsl:template match="recording">
        <media  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="mimeType"><xsl:value-of select="exmaralda:determine-recording-type(@path)"/>/wav</xsl:attribute>
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
            <!-- new 25-04-2018: this is for pointing to the corresponding AGD-ID -->
            <xsl:if test="@dgd-id and not(@dgd-id='???')">
                <idno type="AGD-ID"><xsl:value-of select="@dgd-id"/></idno>
            </xsl:if>
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
    
    <!-- <xsl:template match="timepoint[position()&gt;1]"> -->
    <xsl:template match="timepoint">
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
                        <xsl:when test="(//timepoint[1]/@absolute-time + 0.0)=0.0"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="//timepoint[1]/@timepoint-id"/></xsl:when>
                        <xsl:otherwise><xsl:value-of select="$XPOINTER_HASH"/><xsl:text>T_START</xsl:text></xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>                
    </xsl:template>
    
    <!-- *********************************************** -->
    <!-- ***** ELEMENTS OUTSIDE THE ORIGINAL SPEC ****** -->    
    <!-- *********************************************** -->
    <!-- puncutation, added 29-11-2016 -->
    <xsl:template match="p">
        <pc  xmlns="http://www.tei-c.org/ns/1.0"><xsl:apply-templates/></pc>
    </xsl:template>
    
    
    
    
</xsl:stylesheet>
