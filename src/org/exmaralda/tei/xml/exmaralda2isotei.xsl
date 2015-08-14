<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:tesla="http://www.exmaralda.org" 
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:isoSpoken="http://iso-tei-spoken.org/ns/1.0"
        xmlns:standoff="http://standoff.proposal">
    
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
    
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
                            <recording type="video">
                                <!-- element from TEI P5, but not allowed there as a child of recording -->
                                <xsl:apply-templates select="//referenced-file"/>
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
                        <isoSpoken:transcriptionDesc ident="HIAT" version="2004">
                                <desc><xsl:comment>Fill me in</xsl:comment></desc>
                                <label><xsl:comment>Fill me in</xsl:comment></label>
                        </isoSpoken:transcriptionDesc>
                </encodingDesc>
                <revisionDesc>
                    <!-- ... -->                    
                    <xsl:element name="change">
                        <xsl:attribute name="when"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                        <xsl:text>Created by XSL transformation from an EXMARaLDA basic transcription</xsl:text>
                    </xsl:element>
                </revisionDesc>                
            </teiHeader>
            <text>
                <xsl:call-template name="MAKE_TIMELINE"/>
                <body>
                    <xsl:for-each select="//tli">
                        <xsl:for-each select="//tier[@type='t' and @speaker]/event[@start=current()/@id]">
                            <xsl:if test="not(preceding-sibling::event) or tesla:timeline-position(@start)&gt;tesla:timeline-position(preceding-sibling::event[1]/@end)">
                                <xsl:apply-templates select="." mode="first-pass"/>
                            </xsl:if>                            
                        </xsl:for-each>
                        <xsl:apply-templates select="//tier[@type='d']/event[@start=current()/@id]"></xsl:apply-templates>
                    </xsl:for-each>                                    
                </body>
            </text>
        </TEI>            
    </xsl:template>
    
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <!-- **********           MEDIA          ************* -->
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <xsl:template match="referenced-file">
        <media  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="mimeType"><xsl:value-of select="tesla:determine-recording-type(@url)"/>/xxx</xsl:attribute>
            <xsl:attribute name="url"><xsl:value-of select="@url"/></xsl:attribute>
        </media>        
    </xsl:template>
        
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <!-- **********         TIMELINE         ************* -->
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <xsl:template name="MAKE_TIMELINE">
        <timeline unit="s" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="origin">
                <xsl:choose>
                    <xsl:when test="//tli[1]/@time=0.0">#<xsl:value-of select="//tli[1]/@id"/></xsl:when>
                    <xsl:otherwise>#T_START</xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:if test="//tli[1]/@time&gt;0.0">
                <when xml:id="T_START" absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0"/>                        
            </xsl:if>
            <xsl:apply-templates select="//tli"/>
        </timeline>        
    </xsl:template>

    <!-- CHANGE FOR ISO: use decimal seconds notation, use intervals and since instead of absoulute -->
    <xsl:template match="tli[position()&gt;1]">
        <xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:if test="@time">
                <xsl:attribute name="interval">
                    <xsl:value-of select="@time"/>
                </xsl:attribute>
                <xsl:attribute name="since">
                    <xsl:choose>
                        <xsl:when test="//tli[1]/@time=0.0">#<xsl:value-of select="//tli[1]/@id"/></xsl:when>
                        <xsl:otherwise>#T_START</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>                
    </xsl:template>
    
    <!-- CHANGE FOR ISO: special treatment for first -->
    <xsl:template match="tli[position()=1]">
        <xsl:choose>
            <xsl:when test="@time=0" >
                <when absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:attribute name="xml:id" select="@id"/>
                </when>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:attribute name="xml:id">
                        <xsl:value-of select="@id"/>
                    </xsl:attribute>
                    <xsl:if test="@time">
                        <xsl:attribute name="interval">
                            <xsl:value-of select="@time"/>
                        </xsl:attribute>
                        <xsl:attribute name="since">#T_START</xsl:attribute>
                    </xsl:if>
                </xsl:element>                                
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <!-- **********           SPEAKERS       ************* -->
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    
    <xsl:template match="speaker">
        <xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="n"><xsl:value-of select="abbreviation"/></xsl:attribute>
            <xsl:attribute name="sex">
                <xsl:choose>
                    <xsl:when test="sex/@value='m'">1</xsl:when>
                    <xsl:when test="sex/@value='f'">2</xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:element name="persName"></xsl:element>
        </xsl:element>        
    </xsl:template>
    
    
    
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <!-- *******         TRANSCRIPTION       ************* -->
    <!-- ************************************************* -->
    <!-- ************************************************* -->

    <!-- events from primary transcription tiers with speakers with no immediately preceding event -->
    <!-- CHANGE FOR ISO: use annotatedU instead of div -->
    <xsl:template match="event[../@speaker and ../@type='t']" mode="first-pass">
        <xsl:variable name="DIV_START"><xsl:value-of select="@start"/></xsl:variable>
        <xsl:variable name="DIV_END"><xsl:value-of select="tesla:last-endpoint-of-segment-chain(.)"/></xsl:variable>
        <!-- CHANGE FOR ISO: who, start and end attributes on annotationGrp instead of on u and as anchors -->
        <xsl:element name="annotationGrp" xmlns="http://standoff.proposal">            
            <xsl:attribute name="who">
                <xsl:text>#</xsl:text><xsl:value-of select="parent::tier/@speaker"/>
            </xsl:attribute>
            <xsl:attribute name="start">
                <xsl:text>#</xsl:text><xsl:value-of select="@start"/>                
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:text>#</xsl:text><xsl:value-of select="$DIV_END"/>                
            </xsl:attribute>
            <xsl:element name="u" xmlns="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="xml:id"><xsl:text>u_</xsl:text><xsl:value-of select="generate-id()"/></xsl:attribute>
                <xsl:value-of select="text()"/>
                <xsl:if test="following-sibling::event and tesla:timeline-position(@end)&gt;=tesla:timeline-position(following-sibling::event[1]/@start)">
                    <xsl:apply-templates select="following-sibling::event[1]" mode="second-pass"/>
                </xsl:if>
            </xsl:element> <!-- end u -->
            
            <!-- take care of annotations belonging to this u -->            
            <xsl:for-each select="//tier[@speaker=current()/../@speaker and @type='a']">
                <xsl:call-template name="find-annotations">
                    <xsl:with-param name="START"><xsl:value-of select="$DIV_START"/></xsl:with-param>
                    <xsl:with-param name="END"><xsl:value-of select="$DIV_END"/></xsl:with-param>
                    <xsl:with-param name="TIER_ID"><xsl:value-of select="@id"/></xsl:with-param>
                </xsl:call-template>
            </xsl:for-each>
        </xsl:element> <!-- end div -->
    </xsl:template>
    
    <!-- events from primary transcription tiers with speakers with an immediately preceding event -->
    <xsl:template match="event" mode="second-pass">
            <xsl:value-of select="text()"/>
            <xsl:if test="following-sibling::event and tesla:timeline-position(@end)&gt;=tesla:timeline-position(following-sibling::event[1]/@start)">
                <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:attribute name="synch">
                        <xsl:text>#</xsl:text><xsl:value-of select="@end"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:apply-templates select="following-sibling::event[1]" mode="second-pass"/>
            </xsl:if>        
    </xsl:template>
    
    <!-- finds all annotations in the tier with the given TIER_ID which start at or after the given START point and which end at or before the given END --> 
    <xsl:template name="find-annotations">
        <xsl:param name="START"/>
        <xsl:param name="END"/>
        <xsl:param name="TIER_ID"/>
        
        <xsl:variable name="SPAN_GRP" >
            <xsl:element name="spanGrp">
                <xsl:for-each select="//tier[@id=$TIER_ID]/event">
                    <xsl:if test="tesla:timeline-position(@start)&gt;=tesla:timeline-position($START) and tesla:timeline-position(@end)&lt;=tesla:timeline-position($END)">
                        <xsl:element name="span">
                            <xsl:attribute name="from"><xsl:text>#</xsl:text><xsl:value-of select="@start"/></xsl:attribute>
                            <xsl:attribute name="to"><xsl:text>#</xsl:text><xsl:value-of select="@end"/></xsl:attribute>
                            <xsl:value-of select="text()"/>
                        </xsl:element>
                    </xsl:if>
                </xsl:for-each>
            </xsl:element>
        </xsl:variable>
        
        <xsl:if test="count($SPAN_GRP/descendant::span)&gt;0">
            <xsl:element name="spanGrp" xmlns="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="type"><xsl:value-of select="//tier[@id=$TIER_ID]/@category"/></xsl:attribute>
                <xsl:for-each select="$SPAN_GRP/descendant::span">
                    <xsl:element name="span" xmlns="http://www.tei-c.org/ns/1.0">
                        <xsl:attribute name="from"><xsl:value-of select="@from"/></xsl:attribute>
                        <xsl:attribute name="to"><xsl:value-of select="@to"/></xsl:attribute>
                        <xsl:value-of select="text()"></xsl:value-of>
                    </xsl:element>
                </xsl:for-each>
                <!-- <xsl:copy-of select="$SPAN_GRP/descendant::span"  xmlns="http://www.tei-c.org/ns/1.0"/> -->
            </xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="event[../@type='d']">
        <xsl:element name="incident" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:if test="../@speaker">
                <xsl:attribute name="who">
                    <xsl:text>#</xsl:text><xsl:value-of select="../@speaker"/>
                </xsl:attribute>                
            </xsl:if>
            <xsl:attribute name="type">
                <xsl:value-of select="../@category"/>
            </xsl:attribute>
            <xsl:attribute name="start">
                <xsl:text>#</xsl:text><xsl:value-of select="@start"/>
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:text>#</xsl:text><xsl:value-of select="@end"/>
            </xsl:attribute>
            <xsl:element name="desc">
                <xsl:value-of select="text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    
    <!-- ************************************************* -->
    <!-- ************************************************* -->
    <!-- *******  VARIABLES AND FUNCTIONS    ************* -->
    <!-- ************************************************* -->
    <!-- ************************************************* -->

    <!-- memorizes position of timeline items for quicker reference -->
    <xsl:variable name="timeline-positions">
        <positions>
            <xsl:for-each select="//tli">
                <item>
                    <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
                    <xsl:attribute name="position"><xsl:value-of select="count(preceding-sibling::tli)"/></xsl:attribute>
                </item>
            </xsl:for-each>
        </positions>
    </xsl:variable>
    
    <!-- returns the position number of the timeline item with the given id -->
    <xsl:function name="tesla:timeline-position" as="xs:integer">
        <xsl:param name="timeline-id"/>
        <xsl:value-of select="$timeline-positions/descendant::item[@id=$timeline-id]/@position"/>
    </xsl:function>
    
    <!-- returns latest event that is connected to the given event through an uninterrupted chain of other events -->
    <xsl:function name="tesla:last-endpoint-of-segment-chain">
        <xsl:param name="event"/>
        <xsl:choose>
            <xsl:when test="not($event/following-sibling::event) or tesla:timeline-position($event/following-sibling::event[1]/@start)&gt;tesla:timeline-position($event/@end)">
                <xsl:value-of select="$event/@end"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="tesla:last-endpoint-of-segment-chain($event/following-sibling::event[1])"/>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:function>
    
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
    
    
</xsl:stylesheet>
