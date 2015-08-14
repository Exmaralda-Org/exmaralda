<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tesla="http://www.exmaralda.org"  xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    
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
    
    <!-- ****************************************************************** -->
    <!-- ********************* ROOT TEMPLATE    *********************** -->
    <!-- ****************************************************************** -->
    <xsl:template match="/">
        <TEI xmlns="http://www.tei-c.org/ns/1.0">
            <teiHeader>
                <fileDesc>
                    <titleStmt><title></title></titleStmt>
                    <publicationStmt><p></p></publicationStmt>
                    <sourceDesc>
                        <recordingStmt>
                            <xsl:apply-templates select="//referenced-file"/>
                        </recordingStmt>                                        
                    </sourceDesc>
                    <!-- ... -->
                </fileDesc>
                <profileDesc>
                    <!-- ... -->
                    <particDesc>
                        <xsl:apply-templates select="//speaker"/>
                    </particDesc>                    
                </profileDesc>
                <revisionDesc>
                    <!-- ... -->                    
                    <xsl:element name="change">
                        <xsl:attribute name="when">
                            <xsl:value-of select="current-dateTime()"/>
                        </xsl:attribute>
                        <xsl:text>Created by XSL transformation from an EXMARaLDA basic transcription</xsl:text>
                    </xsl:element>
                </revisionDesc>                
            </teiHeader>
            <text>
                <timeline unit="s">
                    <xsl:attribute name="origin">
                        <xsl:text>#</xsl:text><xsl:value-of select="//tli[1]/@id"></xsl:value-of>
                    </xsl:attribute>
                    <xsl:apply-templates select="//tli"/>
                </timeline>
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
    
    <!-- events from primary transcription tiers with speakers with no immediately preceding event -->
    <xsl:template match="event[../@speaker and ../@type='t']" mode="first-pass">
        <xsl:variable name="DIV_START"><xsl:value-of select="@start"/></xsl:variable>
        <xsl:variable name="DIV_END"><xsl:value-of select="tesla:last-endpoint-of-segment-chain(.)"/></xsl:variable>
        <xsl:element name="div" xmlns="http://www.tei-c.org/ns/1.0">            
            <!-- <xsl:attribute name="type">u_with_annotations</xsl:attribute> -->
            <xsl:element name="u">
                <xsl:attribute name="who">
                    <xsl:text>#</xsl:text><xsl:value-of select="parent::tier/@speaker"/>
                </xsl:attribute>
                <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
                    <xsl:attribute name="synch">
                        <xsl:text>#</xsl:text><xsl:value-of select="@start"/>
                    </xsl:attribute>
                </xsl:element>
                <xsl:value-of select="text()"/>
                <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
                        <xsl:attribute name="synch">
                            <xsl:text>#</xsl:text><xsl:value-of select="@end"/>
                        </xsl:attribute>
                </xsl:element>
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
            <xsl:element name="anchor" xmlns="http://www.tei-c.org/ns/1.0">
                <xsl:attribute name="synch">
                    <xsl:text>#</xsl:text><xsl:value-of select="@end"/>
                </xsl:attribute>
            </xsl:element>
            <xsl:if test="following-sibling::event and tesla:timeline-position(@end)&gt;=tesla:timeline-position(following-sibling::event[1]/@start)">
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

    <xsl:template match="referenced-file">
        <recording  xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="type"><xsl:value-of select="tesla:determine-recording-type(@url)"/></xsl:attribute>
            <xsl:attribute name="url"><xsl:value-of select="@url"/></xsl:attribute>
        </recording>        
    </xsl:template>
    
    <xsl:template match="speaker">
        <xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="sex">
                <xsl:choose>
                    <xsl:when test="sex/@value='m'">1</xsl:when>
                    <xsl:when test="sex/@value='f'">2</xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:element name="persName">
                <xsl:element name="abbr">
                    <xsl:value-of select="abbreviation"/>
                </xsl:element>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="tli">
        <xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:attribute name="xml:id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:if test="@time">
                <xsl:attribute name="absolute">
                    <xsl:value-of select="tesla:seconds-to-timestring(@time)"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>                
    </xsl:template>


</xsl:stylesheet>
