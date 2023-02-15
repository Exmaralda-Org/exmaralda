<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:exmaralda="http://www.exmaralda.org"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <x>
            <xsl:apply-templates mode="TIMELINE"/>
            <xsl:apply-templates/>
            <xsl:apply-templates mode="ANNOTATION"/>
        </x>
    </xsl:template>
    
    <xsl:template match="contribution" mode="TIMELINE">
        <!-- <timeline-fork start="TLI_35" end="TLI_36">
            <tli id="TLI_35.TIE_V_HM.1"/>
        </timeline-fork> -->
        
        <xsl:for-each select="time[preceding-sibling::*]">
            <xsl:variable name="THIS_POSITION" select="count(preceding-sibling::*)"/>
            <xsl:variable name="PREVIOUS_POSITION">
                <xsl:choose>
                    <xsl:when test="preceding-sibling::time"><xsl:value-of select="count(preceding-sibling::time[1]/preceding-sibling::*)"/></xsl:when>
                    <xsl:otherwise>0</xsl:otherwise>
                </xsl:choose>
            </xsl:variable>           
            <timeline-fork type="normal">
                <xsl:attribute name="start">
                    <xsl:choose>
                        <xsl:when test="preceding-sibling::time"><xsl:value-of select="preceding-sibling::time[1]/@timepoint-reference"/></xsl:when>
                        <xsl:otherwise><xsl:value-of select="parent::contribution/@start-reference"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:attribute name="end" select="@timepoint-reference"/>
                <xsl:for-each select="parent::contribution/(w|pause|breath)[count(preceding-sibling::*)&lt;$THIS_POSITION 
                    and count(preceding-sibling::*)&gt;$PREVIOUS_POSITION
                    and not(preceding-sibling::*[1][self::time])
                    and (preceding-sibling::*)
                    ]">
                    <tli>                        
                        <xsl:attribute name="id">
                            <xsl:text>TLI_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@speaker-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@start-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@end-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="count(preceding-sibling::*)"/>
                        </xsl:attribute>
                    </tli> 
                </xsl:for-each>
            </timeline-fork>
        </xsl:for-each>
        <xsl:if test="not(*[last()][self::time])">
            <timeline-fork type="last">
                <xsl:variable name="LAST_POSITION" select="count(time[last()]/preceding-sibling::*)"/>
                <!-- <xsl:attribute name="start" select="time[last()]/@timepoint-reference"/> -->
                <xsl:attribute name="start">
                    <xsl:choose>
                        <xsl:when test="time[last()]">
                            <xsl:value-of select="time[last()]/@timepoint-reference"/>                           
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@start-reference"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:attribute name="end" select="@end-reference"/>
                <xsl:for-each select="(w|pause|breath)[
                    count(preceding-sibling::*)&gt;$LAST_POSITION
                    and not(preceding-sibling::*[1][self::time])
                    and (preceding-sibling::*)
                    ]">
                    <tli>                        
                        <xsl:attribute name="id">
                            <xsl:text>TLI_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@speaker-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@start-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="ancestor::contribution/@end-reference"/>
                            <xsl:text>_</xsl:text>
                            <xsl:value-of select="count(preceding-sibling::*)"/>
                        </xsl:attribute>
                    </tli> 
                </xsl:for-each>
            </timeline-fork>
        </xsl:if>
        
    </xsl:template>
    
    <xsl:template match="contribution">
        <!-- <contribution speaker-reference="HM" start-reference="TLI_0" end-reference="TLI_4" parse-level="2"> -->
        <!-- <ts n="sc" id="Seg_0" s="TLI_0" e="TLI_4"> -->
        <ts>
            <xsl:attribute name="s" select="@start-reference"/>
            <xsl:attribute name="e" select="@end-reference"/>
            <xsl:attribute name="n">sc</xsl:attribute>
            <xsl:attribute name="id">
                <xsl:text>Seg_</xsl:text>
                <xsl:value-of select="@speaker-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="@start-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="@end-reference"/>
            </xsl:attribute>
            <xsl:apply-templates mode="TRANSCRIPTION"/>
        </ts>
    </xsl:template>
    
    <xsl:function name="exmaralda:getStart">
        <xsl:param name="ELEMENT"/>
        <xsl:choose>
            <xsl:when test="not($ELEMENT/preceding-sibling::*)"><xsl:value-of select="$ELEMENT/parent::contribution/@start-reference"/></xsl:when>
            <xsl:when test="$ELEMENT/preceding-sibling::*[1][self::time]"><xsl:value-of select="$ELEMENT/preceding-sibling::time[1]/@timepoint-reference"/></xsl:when>
            <xsl:otherwise>
                <xsl:text>TLI_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@speaker-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@start-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@end-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="count($ELEMENT/preceding-sibling::*)"/>                    
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:function>
    
    <xsl:function name="exmaralda:getEnd">
        <xsl:param name="ELEMENT"/>
        <xsl:choose>
            <xsl:when test="not($ELEMENT/following-sibling::*)"><xsl:value-of select="$ELEMENT/parent::contribution/@end-reference"/></xsl:when>
            <xsl:when test="$ELEMENT/following-sibling::*[1][self::time]"><xsl:value-of select="$ELEMENT/following-sibling::time[1]/@timepoint-reference"/></xsl:when>
            <xsl:otherwise>
                <xsl:text>TLI_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@speaker-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@start-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="$ELEMENT/ancestor::contribution/@end-reference"/>
                <xsl:text>_</xsl:text>
                <xsl:value-of select="count($ELEMENT/preceding-sibling::*) + 1"/>                    
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    
    <xsl:template match="w" mode="TRANSCRIPTION">
        <!-- <w id="w1" i="y" pos="NGIRR" lemma="ja" p-pos="0.974849">ja</w> -->
        <!-- <ts n="cGAT:w" id="Seg_2" s="TLI_0" e="TLI_0_w2">ja</ts> -->

        <ts n="cGAT:w">
            <xsl:attribute name="id" select="@id"/>
            <xsl:attribute name="s" select="exmaralda:getStart(current())"/>
            <xsl:attribute name="e" select="exmaralda:getEnd(current())"/>
            <!-- <xsl:value-of select="text()"/> -->
            <xsl:value-of select="normalize-space(text())"/>
        </ts>        
        <xsl:if test="not(following-sibling::*[1][self::p])">
            <nts n="cGAT:p">
                <xsl:attribute name="id" select="generate-id()"/>
                <xsl:text><![CDATA[ ]]></xsl:text>
            </nts>            
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="pause" mode="TRANSCRIPTION">
        <!-- <pause duration="micro"/> -->
        <!-- <nts n="cGAT:p" id="Seg_67">(</nts>
        <ats n="cGAT:pause" id="Seg_68" s="TLI_1_pause_d1e119" e="TLI_1_w23">.</ats>
        <nts n="cGAT:p" id="Seg_69">)</nts> -->
        <nts n="cGAT:p">
            <xsl:attribute name="id" select="generate-id()"/>
            <xsl:text>(</xsl:text>
        </nts>
        <ats n="cGAT:pause">
            <xsl:attribute name="s" select="exmaralda:getStart(current())"/>
            <xsl:attribute name="e" select="exmaralda:getEnd(current())"/>
            <xsl:attribute name="id" select="generate-id()"/>
            <xsl:choose>
                <xsl:when test="@duration='micro'">.</xsl:when>
                <xsl:otherwise><xsl:value-of select="@duration"/></xsl:otherwise>
            </xsl:choose>            
        </ats>
        <nts n="cGAT:p">
            <xsl:attribute name="id" select="generate-id()"/>
            <xsl:text>)</xsl:text>
        </nts>
        <xsl:if test="not(following-sibling::*[1][self::p])">
            <nts n="cGAT:p">
                <xsl:attribute name="id" select="generate-id()"/>
                <xsl:text><![CDATA[ ]]></xsl:text>
            </nts>            
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="breathe" mode="TRANSCRIPTION">
        <!-- <breathe type="in" length="1"/> -->
        <!-- <ats n="cGAT:b" id="Seg_92" s="TLI_2" e="TLI_3">°h </ats> -->
        <ats n="cGAT:b">
            <xsl:attribute name="s" select="exmaralda:getStart(current())"/>
            <xsl:attribute name="e" select="exmaralda:getEnd(current())"/>
            <xsl:attribute name="id" select="generate-id()"/>
            <xsl:if test="@type='in'">°</xsl:if>
            <xsl:text>h</xsl:text>
            <xsl:if test="@length&gt;1">h</xsl:if>
            <xsl:if test="@length&gt;2">h</xsl:if>
            <xsl:if test="@type='out'">°</xsl:if>
            <xsl:text> </xsl:text>
        </ats>        
        <xsl:if test="not(following-sibling::*[1][self::p])">
            <nts n="cGAT:p">
                <xsl:attribute name="id" select="generate-id()"/>
                <xsl:text><![CDATA[ ]]></xsl:text>
            </nts>            
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="contribution" mode="ANNOTATION">
        <!-- <contribution speaker-reference="HM" start-reference="TLI_0" end-reference="TLI_4" parse-level="2"> -->
        <!-- <ts n="sc" id="Seg_0" s="TLI_0" e="TLI_4"> -->
        <xsl:if test="*[@n]">
            <annotation name="norm">
                <xsl:apply-templates select="descendant::*[@n]/@n" mode="ANNOTATION"></xsl:apply-templates>
            </annotation>        
        </xsl:if>
        <xsl:if test="*[@lemma]">
            <annotation name="lemma">
                <xsl:apply-templates select="descendant::*[@lemma]/@lemma" mode="ANNOTATION"></xsl:apply-templates>
            </annotation>        
        </xsl:if>
        <xsl:if test="*[@pos]">
            <annotation name="pos">
                <xsl:apply-templates select="descendant::*[@pos]/@pos" mode="ANNOTATION"></xsl:apply-templates>
            </annotation>        
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="w/@*" mode="ANNOTATION">
        <ta>
            <xsl:attribute name="s" select="exmaralda:getStart(parent::*)"/>
            <xsl:attribute name="e" select="exmaralda:getEnd(parent::*)"/>
            <xsl:attribute name="id" select="generate-id()"/>
            <xsl:value-of select="."/>
        </ta>
    </xsl:template>
    
    
</xsl:stylesheet>