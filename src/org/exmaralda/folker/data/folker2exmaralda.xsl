<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!-- changes on 04-02-2009: -->
    <!-- symbol for clitics is now _ instead of + -->
    <!-- symbol for alternative is now / instaed of | -->
    <!-- symbol for breathe is now ° instead of _ -->
    <!-- symbol for unintelligible is now + instead of * -->
    <!-- changes on 31-05-2011 -->
    <!-- adaptations for basic transcript -->

    <xsl:output encoding="UTF-8" method="xml"/>
    <xsl:template match="/">
        <basic-transcription>
            <head>
                <meta-information>
                    <project-name/>
                    <transcription-name/>
                    <xsl:element name="referenced-file">
                        <xsl:attribute name="url">
                            <xsl:value-of select="//recording/@path"/>
                        </xsl:attribute>
                    </xsl:element>
                    <ud-meta-information/>
                    <comment/>
                    <transcription-convention/>
                </meta-information>
                <speakertable>
                      <xsl:apply-templates select="//speaker"/>
                </speakertable>
            </head>
            <basic-body>
                <common-timeline>
                    <xsl:apply-templates select="//timepoint"/>
                </common-timeline>
                <xsl:call-template name="MAKE_TIERS"/>
            </basic-body>
        </basic-transcription>
    </xsl:template>
    
    <xsl:template match="speaker">
        <xsl:element name="speaker">
            <xsl:attribute name="id">
                <xsl:value-of select="@speaker-id"/>
            </xsl:attribute>
            <abbreviation>
                <xsl:value-of select="@speaker-id"/>
            </abbreviation>
            <sex value="u"/>
            <languages-used/>
            <l1/>
            <l2/>
            <ud-speaker-information>
                <ud-information attribute-name="Name">
                    <xsl:value-of select="name"/>
                </ud-information>
            </ud-speaker-information>
            <comment/>            
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="timepoint">
        <xsl:element name="tli">
            <xsl:attribute name="id">
                <xsl:value-of select="@timepoint-id"/>
            </xsl:attribute>
            <xsl:attribute name="time">
                <xsl:value-of select="@absolute-time"/>
            </xsl:attribute>
        </xsl:element>
    </xsl:template>
    
    <xsl:template name="MAKE_TIERS">
        <xsl:for-each-group select="//contribution[@speaker-reference]" group-by="@speaker-reference">
            <xsl:element name="tier">
                <xsl:attribute name="type">t</xsl:attribute>
                <xsl:attribute name="category">v</xsl:attribute>
                <xsl:attribute name="id">TIE<xsl:value-of select="position()"/></xsl:attribute>
                <xsl:attribute name="display-name"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>                
                <xsl:attribute name="speaker"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
            <xsl:for-each select="current-group()">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
            </xsl:element>
        </xsl:for-each-group>
        <xsl:element name="tier">
            <xsl:attribute name="type">t</xsl:attribute>
            <xsl:attribute name="category">v</xsl:attribute>
            <xsl:attribute name="id">TIE_NOSP</xsl:attribute>
            <xsl:attribute name="display-name"></xsl:attribute>                
            <xsl:apply-templates select="//contribution[not(@speaker-reference)]"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="contribution[unparsed or segment]">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="contribution[not(unparsed) and not(segment)]">
        <xsl:variable name="transformed">
            <contribution>
                <xsl:copy-of select="@speaker-reference"/>
                <xsl:copy-of select="@start-reference"/>
                <xsl:copy-of select="@end-reference"/>
                <unparsed>
                    <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
                </unparsed>
            </contribution>
        </xsl:variable>
        <xsl:call-template name="TRANSFORM_TRANSFORMED">
            <xsl:with-param name="contribution" select="$transformed"/>
        </xsl:call-template>
        <xsl:apply-templates select="$transformed/unparsed/text()"/>
    </xsl:template>
    
    <xsl:template name="TRANSFORM_TRANSFORMED">
        <xsl:param name="contribution"/>
        <xsl:apply-templates select="$contribution/*"/>
    </xsl:template>
    
        
    <!-- ****************** START OF VERY IMPORTANT WORD TEMPLATE ************************** -->    
    <xsl:template match="w" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <!-- <xsl:when test="not(preceding-sibling::*)">
            </xsl:when> -->
            <xsl:when test="@transition='assimilated'">
                <!-- change 04-02-2009 -->
                <xsl:text>_</xsl:text>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time) and not(self::latching) and not(self::boundary) and not(self::comment)]">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <!-- <xsl:if test="not(@transition='assimilated') and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))"> -->
        <!-- This is saying: if the following sibling is not an assimilated word, -->
        <!-- AND if (a timepoint immediately follows OR this is the end of the contribution): put a space -->
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    <!-- ****************** END OF VERY IMPORTANT WORD TEMPLATE ************************** -->    

    
    
    <xsl:template match="w/text()" mode="transform-parsed-to-unparsed">
        <xsl:if test="string-length(normalize-space())&gt;0">
            <xsl:value-of select="."/>
        </xsl:if>
    </xsl:template>
        
    <xsl:template match="time" mode="transform-parsed-to-unparsed">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template match="pause" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:choose>
            <xsl:when test="@duration='unspecified'"><xsl:text>(*)</xsl:text></xsl:when>
            <xsl:when test="@duration='micro'"><xsl:text>(.)</xsl:text></xsl:when>
            <xsl:when test="@duration='short'"><xsl:text>(-)</xsl:text></xsl:when>
            <xsl:when test="@duration='medium'"><xsl:text>(--)</xsl:text></xsl:when>
            <xsl:when test="@duration='long'"><xsl:text>(---)</xsl:text></xsl:when>
            <xsl:otherwise><xsl:text>(</xsl:text><xsl:value-of select="@duration"></xsl:value-of><xsl:text>)</xsl:text></xsl:otherwise>
        </xsl:choose>        
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <!-- 06-03-2009: this is retained for backwards compatibility -->
    <!-- the element 'unintelligible' will not be written any more -->
    <!-- instead the sequence of plus signs is encoded as a word -->
    <xsl:template match="unintelligible" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:for-each select="(1 to @length)">
            <!-- change 04-02-2009 -->
            <xsl:text>+++</xsl:text>
        </xsl:for-each>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>

        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="non-phonological" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:text>((</xsl:text>
        <xsl:value-of select="@description"/>
        <xsl:text>))</xsl:text>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>

        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="uncertain" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time or self::boundary or self::latching])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        <xsl:for-each select="alternative">
            <!-- change 04-02-2009 -->
            <xsl:text>/</xsl:text>
            <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        </xsl:for-each>
        <xsl:text>)</xsl:text>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>

        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
       
    <xsl:template match="breathe" mode="transform-parsed-to-unparsed">
        <!-- change 07-05-2012 -->
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time or self::boundary or self::latching])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:if test="@type='in'">
            <!-- change 04-02-2009 -->
            <xsl:text>°</xsl:text>
        </xsl:if>
        <xsl:for-each select="(1 to @length)">
            <xsl:text>h</xsl:text>
        </xsl:for-each>
        <xsl:if test="@type='out'">
            <!-- change 04-02-2009 -->
            <xsl:text>°</xsl:text>
        </xsl:if>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>

        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <!-- *************** START TEMPLATES FOR BASIC TRANSCRIPT ************** -->
    
    <xsl:template match="lengthening" mode="transform-parsed-to-unparsed">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="line" mode="transform-parsed-to-unparsed">
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <!-- changed 28-03-2012 -->
        <xsl:if test="parent::contribution[string-length(@speaker-reference)&gt;0] and not(child::*[last()][self::boundary or self::breathe or self::non-phonological])"><xsl:text>&#x0020;</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="stress" mode="transform-parsed-to-unparsed">
        <xsl:if test="@type='strong'"><xsl:text>!</xsl:text></xsl:if>
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <xsl:if test="@type='strong'"><xsl:text>!</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="stress/text()" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="translate(., 'abcdefghijklmnopqrstuvwxyzäöü', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ')"/>
    </xsl:template>
    
    <xsl:template match="latching" mode="transform-parsed-to-unparsed">
        <xsl:text>=</xsl:text>
    </xsl:template>
    
    <xsl:template match="boundary" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@movement='low-fall'">.</xsl:when>
            <xsl:when test="@movement='fall'">;</xsl:when>
            <xsl:when test="@movement='steady'">–</xsl:when>
            <xsl:when test="@movement='rise'">,</xsl:when>
            <xsl:when test="@movement='high-rise'">?</xsl:when>
            <xsl:otherwise>|</xsl:otherwise>
        </xsl:choose>
        <xsl:if test="@latching='yes'">=</xsl:if>
        <xsl:if test="@type='final' and @latching='no'"><xsl:text>&#x0020;</xsl:text></xsl:if>
    </xsl:template>
    
    <xsl:template match="comment" mode="transform-parsed-to-unparsed">        
        <xsl:choose>
            <xsl:when test="@position='start'">
                <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time or self::latching])"><xsl:text>&#x0020;</xsl:text></xsl:if>
                <xsl:text>&lt;&lt;</xsl:text>
                <xsl:value-of select="@description"/>
                <xsl:text>&gt;</xsl:text>
                <xsl:if test="not(following-sibling::*[1][self::uncertain])"><xsl:text>&#x0020;</xsl:text></xsl:if>
            </xsl:when>
            <xsl:otherwise>&#x0020;&gt;<xsl:if test="not(following-sibling::*[1][self::boundary or self::pause])"><xsl:text>&#x0020;</xsl:text></xsl:if></xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
        
    <!-- *************** END TEMPLATES FOR BASIC TRANSCRIPT ************** -->
    
    <xsl:template match="text()" mode="transform-parsed-to-unparsed">
        <!-- do nothing -->
    </xsl:template>
    
    <xsl:template match="contribution/segment">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="@start-reference"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="@end-reference"/></xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
        
    
    <xsl:template match="contribution/unparsed/text()">
        <xsl:element name="event">
            <xsl:attribute name="start">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time">
                        <xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::contribution/@start-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:choose>
                    <xsl:when test="following-sibling::time">
                        <xsl:value-of select="following-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::contribution/@end-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                                
            </xsl:attribute>
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>

    <!--  added 18-05-2012: make an event for empty contributions -->
    <xsl:template match="contribution/unparsed[not(*) and not(text())]">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="ancestor::contribution/@start-reference"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="ancestor::contribution/@end-reference"/></xsl:attribute>
          </xsl:element>  
    </xsl:template>
    
    <!-- *************** RULES BEYOND FOLKER/GAT ************** -->
    
    <!-- added 02-08-2012: some first extra rules for non FOLKER DGD files -->
    <xsl:template match="p" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="text()"/><xsl:text>&#x0020;</xsl:text>
    </xsl:template>
    
    <xsl:template match="hesitation" mode="transform-parsed-to-unparsed">
        <xsl:text>&#x0020;..&#x0020;</xsl:text>
    </xsl:template>

    <xsl:template match="false-start" mode="transform-parsed-to-unparsed">
        <xsl:text>--&#x0020;</xsl:text>
    </xsl:template>

    <xsl:template match="overlap[@type='inline']" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@position='start'">(</xsl:when>
            <xsl:otherwise>) </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
