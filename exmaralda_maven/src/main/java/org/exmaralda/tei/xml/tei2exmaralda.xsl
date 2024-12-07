<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xpath-default-namespace="http://www.tei-c.org/ns/1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tesla="http://www.exmaralda.org" version="2.0">
        
    <xsl:function name="tesla:timestring-to-seconds">
        <xsl:param name="timestring"/>
        <!-- 00:00:19.67 -->
         <xsl:variable name="HOURS">
             <xsl:value-of select="substring($timestring, 1,2)"/>
         </xsl:variable>   
        <xsl:variable name="MINUTES">
            <xsl:value-of select="substring($timestring, 4,2)"/>
        </xsl:variable>   
        <xsl:variable name="SECONDS">
            <xsl:value-of select="substring($timestring, 7)"/>            
        </xsl:variable>        
        <xsl:value-of select="0 + $HOURS*3600 + $MINUTES*60 + $SECONDS"/>
    </xsl:function>
    
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name/>
                    <transcription-name/>
                    <xsl:apply-templates select="//recording"/>
                    <ud-meta-information/>
                    <comment/>
                    <transcription-convention/>
                </meta-information>
                <speakertable>
                    <xsl:apply-templates select="//person"/>
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <xsl:apply-templates select="//when"/>
                </common-timeline>
                <xsl:apply-templates select="//body"/>
            </basic-body>       
        </basic-transcription>
    </xsl:template>
    
    <xsl:template match="body">
        <xsl:for-each select="//person/@xml:id">
            <xsl:variable name="SPEAKER_ID" select="."/>
            <xsl:variable name="SPEAKER_ID_REF" select="concat( '#', $SPEAKER_ID)"/>
            <!-- primary tiers (derived from PCDATA inside u elements -->
            <xsl:element name="tier">
                <xsl:attribute name="id">TIE_<xsl:value-of select="$SPEAKER_ID"/></xsl:attribute>
                <xsl:attribute name="category">v</xsl:attribute>
                <xsl:attribute name="type">t</xsl:attribute>
                <xsl:attribute name="speaker"><xsl:value-of select="$SPEAKER_ID"/></xsl:attribute>
                <xsl:apply-templates select="//div[u[1]/@who=$SPEAKER_ID_REF]/u/text()[preceding-sibling::*[1][self::anchor] and following-sibling::*[1][self::anchor]]"/>
            </xsl:element>
            
            <!-- annotation tiers (derived from data inside span elements)-->
            <xsl:for-each-group select="//spanGrp/@type" group-by=".">
                <xsl:if test="//div[u[1]/@who=$SPEAKER_ID_REF and spanGrp[@type=current-grouping-key()]]">
                    <!-- annotation tiers -->
                    <xsl:element name="tier">
                        <xsl:attribute name="id">TIE_<xsl:value-of select="$SPEAKER_ID"/>_<xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        <xsl:attribute name="category"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        <xsl:attribute name="type">a</xsl:attribute>
                        <xsl:attribute name="speaker"><xsl:value-of select="$SPEAKER_ID"/></xsl:attribute>
                        <xsl:apply-templates select="//div[u[1]/@who=$SPEAKER_ID_REF]/spanGrp[@type=current-grouping-key()]/span"/>
                    </xsl:element>                    
                </xsl:if>
            </xsl:for-each-group>
            
            <!-- description tiers (derived from data inside incident elements) -->
            <xsl:for-each-group select="//incident/@type" group-by=".">
                <xsl:if test="//incident[@who=$SPEAKER_ID_REF and @type=current-grouping-key()]">
                    <xsl:element name="tier">
                        <xsl:attribute name="id">TIE_<xsl:value-of select="$SPEAKER_ID"/>_<xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        <xsl:attribute name="category"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
                        <xsl:attribute name="type">d</xsl:attribute>
                        <xsl:attribute name="speaker"><xsl:value-of select="$SPEAKER_ID"/></xsl:attribute>
                        <xsl:apply-templates select="//body/incident[@who=$SPEAKER_ID_REF and @type=current-grouping-key()]"/>
                    </xsl:element>                    
                </xsl:if>
            </xsl:for-each-group>            
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="u/text()">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="substring(preceding-sibling::anchor[1]/@synch,2)"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="substring(following-sibling::anchor[1]/@synch,2)"/></xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="span">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="substring(@from,2)"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="substring(@to,2)"/></xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="body/incident">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="substring(@start,2)"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="substring(@end,2)"/></xsl:attribute>
            <xsl:value-of select="desc/text()"/>
        </xsl:element>                
    </xsl:template>
    
    <xsl:template match="recording">
        <xsl:element name="referenced-file">
            <xsl:attribute name="url"><xsl:value-of select="@url"/></xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="person">
        <xsl:element name="speaker">
            <xsl:attribute name="id"><xsl:value-of select="@xml:id"></xsl:value-of></xsl:attribute>
            <xsl:element name="abbreviation">
                <xsl:value-of select="persName/abbr"/>
            </xsl:element>
            <xsl:element name="sex">
                <xsl:attribute name="value">
                    <xsl:choose>
                        <xsl:when test="@sex='1'">m</xsl:when>
                        <xsl:when test="@sex='2'">f</xsl:when>
                        <xsl:otherwise>u</xsl:otherwise>
                    </xsl:choose>                    
                </xsl:attribute>
            </xsl:element>
            <languages-used/>
             <l1/>
             <l2/>
             <ud-speaker-information/>                    
       </xsl:element>
    </xsl:template>
    
    <xsl:template match="when">
        <xsl:element name="tli">
            <xsl:attribute name="id"><xsl:value-of select="@xml:id"/></xsl:attribute>
            <xsl:if test="@absolute and string-length(@absolute)&gt;0">
                <xsl:attribute name="time">
                    <xsl:value-of select="tesla:timestring-to-seconds(@absolute)"/>
                </xsl:attribute>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
</xsl:stylesheet>
