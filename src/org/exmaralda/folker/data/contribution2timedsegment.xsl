<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    
    <xsl:template match="/">
        <xsl:element name="ts">
            <xsl:attribute name="n">sc</xsl:attribute>
            <xsl:attribute name="s"><xsl:value-of select="//contribution/@start-reference"/></xsl:attribute>
            <xsl:attribute name="e"><xsl:value-of select="//contribution/@end-reference"/></xsl:attribute>            
            <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="w" mode="inside-alt">
        <xsl:choose>
            <xsl:when test="@transition='assimilated'">
                    <xsl:text>_</xsl:text>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time)]">
                <xsl:text>&#x0020;</xsl:text>
            </xsl:when>
        </xsl:choose>        
       <xsl:apply-templates mode="transform-parsed-to-unparsed"/>        
        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
                <xsl:text>&#x0020;</xsl:text>
        </xsl:if>        
    </xsl:template>
        
    <xsl:template match="w" mode="transform-parsed-to-unparsed">
        <xsl:choose>
            <xsl:when test="@transition='assimilated'">
                <xsl:element name="nts">
                    <xsl:attribute name="n">GAT2:assim</xsl:attribute>
                    <xsl:text>_</xsl:text>
                </xsl:element>
            </xsl:when>
            <xsl:when test="preceding-sibling::*[1][not(self::time)]">
                <xsl:element name="nts">
                    <xsl:attribute name="n">GAT2:space</xsl:attribute>
                    <xsl:text>&#x0020;</xsl:text>
                </xsl:element>
            </xsl:when>
        </xsl:choose>
        
        <xsl:element name="ts">
            <xsl:attribute name="n">GAT2:w</xsl:attribute>
            <xsl:attribute name="s">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time">
                        <xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@start-reference]/@start-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:attribute name="e">
                <xsl:choose>
                    <xsl:when test="following-sibling::time">
                        <xsl:value-of select="following-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@end-reference]/@end-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                                
            </xsl:attribute>
            <xsl:apply-templates mode="transform-parsed-to-unparsed"/>
        </xsl:element>

        <xsl:if test="not(following-sibling::*[not(self::time)][1][self::w and @transition='assimilated']) and (following-sibling::*[1][self::time] or (parent::contribution and not(following-sibling::*)))">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>                
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>                
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/text()" mode="transform-parsed-to-unparsed">
        <xsl:value-of select="."/>
    </xsl:template>
    
    <xsl:template match="time" mode="transform-parsed-to-unparsed">
        <!-- <xsl:copy-of select="."/> -->
    </xsl:template>
    
    <xsl:template match="pause" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
        </xsl:if>
        <xsl:element name="ats">                
              <xsl:attribute name="n">GAT2:pause</xsl:attribute>
            <xsl:attribute name="s">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time">
                        <xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@start-reference]/@start-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:attribute name="e">
                <xsl:choose>
                    <xsl:when test="following-sibling::time">
                        <xsl:value-of select="following-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@end-reference]/@end-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                                
            </xsl:attribute>
            <xsl:choose>
                    <xsl:when test="@duration='micro'"><xsl:text>(.)</xsl:text></xsl:when>
                    <xsl:when test="@duration='short'"><xsl:text>(-)</xsl:text></xsl:when>
                    <xsl:when test="@duration='medium'"><xsl:text>(--)</xsl:text></xsl:when>
                    <xsl:when test="@duration='long'"><xsl:text>(---)</xsl:text></xsl:when>
                    <xsl:otherwise><xsl:text>(</xsl:text><xsl:value-of select="@duration"></xsl:value-of><xsl:text>)</xsl:text></xsl:otherwise>
                </xsl:choose>
            </xsl:element>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
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
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>
                <xsl:text>&#x0020;</xsl:text>
             </xsl:element>
        </xsl:if>
        <xsl:element name="ats">
            <xsl:attribute name="n">GAT2:non-pho</xsl:attribute> 
            <xsl:attribute name="s">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time">
                        <xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@start-reference]/@start-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:attribute name="e">
                <xsl:choose>
                    <xsl:when test="following-sibling::time">
                        <xsl:value-of select="following-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@end-reference]/@end-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                                
            </xsl:attribute>
            <xsl:text>((</xsl:text>
            <xsl:value-of select="@description"/>
            <xsl:text>))</xsl:text>
         </xsl:element>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text> -->
    </xsl:template>
    
    <xsl:template match="uncertain" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
        </xsl:if>
        <xsl:element name="nts">
            <xsl:attribute name="n">GAT2:ip</xsl:attribute>            
            <xsl:text>(</xsl:text>
        </xsl:element>
        <xsl:apply-templates select="w" mode="transform-parsed-to-unparsed"/>
        <xsl:for-each select="alternative">
            <!-- change 04-02-2009 -->
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:alt</xsl:attribute>            
                <xsl:text>/</xsl:text>
            <xsl:apply-templates select="w" mode="inside-alt"/>
          </xsl:element>
        </xsl:for-each>
        <xsl:element name="nts">
            <xsl:attribute name="n">GAT2:ip</xsl:attribute>            
            <xsl:text>)</xsl:text>
        </xsl:element>
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>            
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="lengthening" mode="transform-parsed-to-unparsed">
        <xsl:for-each select="(1 to @degree)">
            <xsl:text>:</xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    
    <xsl:template match="breathe" mode="transform-parsed-to-unparsed">
        <xsl:if test="preceding-sibling::* and not(preceding-sibling::*[1][self::time])">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>            
                <xsl:text>&#x0020;</xsl:text>
             </xsl:element>
        </xsl:if>
        <xsl:element name="ats">
            <xsl:attribute name="n">GAT2:breathe</xsl:attribute>
            <xsl:attribute name="s">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time">
                        <xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@start-reference]/@start-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                
            </xsl:attribute>
            <xsl:attribute name="e">
                <xsl:choose>
                    <xsl:when test="following-sibling::time">
                        <xsl:value-of select="following-sibling::time[1]/@timepoint-reference"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="ancestor::*[@end-reference]/@end-reference"/>
                    </xsl:otherwise>
                </xsl:choose>                                
            </xsl:attribute>
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
        </xsl:element>            
        <xsl:if test="following-sibling::*[1][self::time] or not(following-sibling::*)">
            <xsl:element name="nts">
                <xsl:attribute name="n">GAT2:space</xsl:attribute>            
                <xsl:text>&#x0020;</xsl:text>
            </xsl:element>
        </xsl:if>
        
        <!-- <xsl:text>&#x0020;</xsl:text>         -->
    </xsl:template>
    
    <xsl:template match="text()" mode="transform-parsed-to-unparsed">
        <!-- do nothing -->
    </xsl:template>
    
    
</xsl:stylesheet>
