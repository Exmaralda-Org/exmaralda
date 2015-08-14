<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xml" encoding="UTF-8"/>
    <!-- changes on 04-02-2009: -->
    <!-- symbol for clitics is now _ instead of + -->
    <!-- symbol for alternative is now / instaed of | -->
    <!-- symbol for breathe is now ° instead of _ -->
    <!-- symbol for unintelligible is now + instead of * -->
    

    <xsl:template match="/">        
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="@* | node()">
        <xsl:copy>
            <xsl:apply-templates select="@* | node()"/>
        </xsl:copy>
    </xsl:template>
    
    
    <xsl:template match="contribution[not(unparsed) and not(segment)]">
            <contribution>
                <xsl:copy-of select="@speaker-reference"/>
                <time><xsl:attribute name="absolute" select="round-half-to-even(//timepoint[@timepoint-id=current()/@start-reference]/@absolute-time,2)"/></time>
                <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
                <time><xsl:attribute name="absolute" select="round-half-to-even(//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time,2)"/></time>
            </contribution>
    </xsl:template>
    
    
   
    <xsl:template match="speakers | timeline">
        <!-- do nothing -->
    </xsl:template>
    
    <xsl:template match="time" mode="transform-parsed-to-unparsed">
        <!-- changed 14-02-2012: do not cut words by timepoints or they will be treated as two word in the full text index -->
        <xsl:if test="not(parent::w)">
            <time><xsl:attribute name="absolute" select="round-half-to-even(//timepoint[@timepoint-id=current()/@timepoint-reference]/@absolute-time,2)"/></time>
        </xsl:if>
    </xsl:template>

    <xsl:template match="w" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@transition='assimilated'">
                <!-- change 04-02-2009 -->
                <!-- changed 14-02-2012: do not use underscore or assimilated words will be treated as one in the full text index -->
                <!-- <xsl:text>_</xsl:text> -->
                <xsl:text> </xsl:text>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time) and not(self::latching) and not(self::boundary) and not(self::comment)]">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>

        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        <xsl:if test="@n">
            <b> [<xsl:value-of select="@n"/>]</b>
        </xsl:if>
        <!-- <xsl:if test="not(@transition='assimilated') and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))"> -->
        <!-- This is saying: if the following sibling is not assimilated word, -->
        <!-- AND if (a timepoint immediately follows OR this is the end of the contribution): put a space -->
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/text()" mode="transform-parsed-to-unparsed">
        <xsl:if test="string-length(normalize-space())&gt;0">
            <xsl:value-of select="."/>
        </xsl:if>
    </xsl:template>
    
    <!-- <xsl:template match="time" mode="transform-parsed-to-unparsed">
    </xsl:template> -->
    
    <xsl:template match="pause" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:choose>
                <xsl:when test="@duration='micro'"><xsl:text>(.)</xsl:text></xsl:when>
                <xsl:when test="@duration='short'"><xsl:text>(-)</xsl:text></xsl:when>
                <xsl:when test="@duration='medium'"><xsl:text>(--)</xsl:text></xsl:when>
                <xsl:when test="@duration='long'"><xsl:text>(---)</xsl:text></xsl:when>
                <xsl:otherwise><xsl:text>(</xsl:text><xsl:value-of select="@duration"></xsl:value-of><xsl:text>)</xsl:text></xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
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
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:for-each select="(1 to @length)">
                <!-- change 04-02-2009 -->
                <xsl:text>+++</xsl:text>
            </xsl:for-each>
            <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:if>
        </xsl:copy>
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="non-phonological" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:text>&#x0020;</xsl:text>
        </xsl:if>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:text>((</xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text>))</xsl:text>
            <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:if>
        </xsl:copy>
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="uncertain" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
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
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
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
    
    <xsl:template match="text()" mode="transform-parsed-to-unparsed">
        <!-- do nothing -->
    </xsl:template>
    
    <!-- *************** START TEMPLATES FOR BASIC TRANSCRIPT ************** -->
    
    <xsl:template match="lengthening" mode="transform-parsed-to-unparsed">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="line" mode="transform-parsed-to-unparsed">
        <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
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
            <xsl:when test="@position='start'">&#x0020;&lt;&lt;<xsl:value-of select="@description"/>&gt;&#x0020;</xsl:when>
            <xsl:otherwise>&#x0020;&gt;&#x0020;</xsl:otherwise>
        </xsl:choose>        
    </xsl:template>
    
    <!-- *************** END TEMPLATES FOR BASIC TRANSCRIPT ************** -->
    <xsl:template match="contribution/segment">
        <xsl:element name="event">
            <xsl:attribute name="start"><xsl:value-of select="@start-reference"/></xsl:attribute>
            <xsl:attribute name="end"><xsl:value-of select="@end-reference"/></xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
        
    
    <xsl:template match="contribution/unparsed/*">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <!-- <xsl:template match="contribution/unparsed/text()">
            <xsl:value-of select="."/>
    </xsl:template> -->
    
</xsl:stylesheet>
