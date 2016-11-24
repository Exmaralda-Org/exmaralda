<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- Copied and slightly modified from the version used for Modiko (which was specific in some places to cGAT) -->
    <xsl:variable name="TOKENIZED_SEGMENTATION_NAME">
        <xsl:value-of select="(//segmentation[not(@name='SpeakerContribution_Event')])[1]/@name"/>
    </xsl:variable>
        
    
    <xsl:template match="/">
        <xsl:message><xsl:value-of select="$TOKENIZED_SEGMENTATION_NAME"/></xsl:message>
        <basic-transcription> 
            <xsl:copy-of select="//head"/>
            <basic-body>
                <xsl:copy-of select="//common-timeline"/>
                <xsl:apply-templates select="//segmented-tier[@type='t']"/>
            </basic-body>
        </basic-transcription>
    </xsl:template>
      
    <xsl:template match="segmented-tier[@type='t']">
        <xsl:apply-templates select="segmentation[@name='SpeakerContribution_Event']"/>
        <xsl:apply-templates select="segmentation[@name=$TOKENIZED_SEGMENTATION_NAME]"/>
        <xsl:apply-templates select="annotation"/>
        
        <xsl:variable name="THIS_SPEAKER" select="@speaker"/>
        <xsl:apply-templates select="//segmented-tier[@type='d' and @speaker=$THIS_SPEAKER]"></xsl:apply-templates>
    </xsl:template>
    
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->
    
    <xsl:template match="segmentation[@name='SpeakerContribution_Event']">
        <!-- <tier id="TIE1" speaker="SPK1" category="v" type="t" display-name="PM1 [v]"> -->
        <tier>
            <xsl:attribute name="id" select="../@id"/>
            <xsl:attribute name="speaker" select="../@speaker"/>
            <xsl:attribute name="category" select="../@category"/>
            <xsl:attribute name="type" select="../@type"/>
            <xsl:apply-templates/>
        </tier>
    </xsl:template>
    
    <!-- don't do anything for <ts> which are NOT leaves -->
    <xsl:template match="ts[*]">
        <xsl:apply-templates/>
    </xsl:template>
    
    <!-- simply copy the default segmentation -->
    <xsl:template match="segmentation[@name='SpeakerContribution_Event']/ts/ts[@n='e']">
        <!-- <ts n="e" id="Seg_115" s="T1" e="T16">(222) </ts>-->
        <!-- <event start="T293" end="T1">nicht geklappt also worum es geht </event> -->            
        <event>
            <xsl:attribute name="start" select="@s"/>
            <xsl:attribute name="end" select="@e"/>
            <xsl:value-of select="text()"/>
        </event>
    </xsl:template>
    
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->
        
    <xsl:template match="segmentation[@name=$TOKENIZED_SEGMENTATION_NAME]">
        <!-- <tier id="TIE1" speaker="SPK1" category="v" type="t" display-name="PM1 [v]"> -->
        <tier>
            <xsl:attribute name="id" select="concat(../@id, '_TOK')"/>
            <xsl:attribute name="speaker" select="../@speaker"/>
            <xsl:attribute name="category">tok</xsl:attribute>
            <xsl:attribute name="type" select="../@type"/>
            <xsl:apply-templates/> 
        </tier>
    </xsl:template>
    
    <xsl:template match="ats[ancestor::segmentation[@name=$TOKENIZED_SEGMENTATION_NAME]]|ts[ancestor::segmentation[@name=$TOKENIZED_SEGMENTATION_NAME] and not(*)]">
        <xsl:variable name="THIS_START" select="@s"/>
        <xsl:if test="//common-timeline/tli[@id=$THIS_START]">
            <xsl:variable name="THIS_END" select="@e"/>
            <xsl:variable name="NEXT_END">
                <xsl:choose>
                    <xsl:when test="//common-timeline/tli[@id=$THIS_END]"><xsl:value-of select="$THIS_END"/></xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="//ancestor::segmented-tier/descendant::timeline-fork[tli[@id=$THIS_END]]/@end"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <event>
                <xsl:attribute name="start" select="@s"/>
                <xsl:attribute name="end" select="$NEXT_END"/>
                <xsl:attribute name="type">
                    <xsl:choose>
                        <xsl:when test="not($THIS_END=$NEXT_END)">mixed</xsl:when>
                        <xsl:otherwise><xsl:value-of select="name()"/></xsl:otherwise>
                    </xsl:choose>
                        
                </xsl:attribute>

                <xsl:if test="name()='ats'">
                    <!-- <xsl:choose>
                        <xsl:when test="@n='cGAT:pause'"><xsl:text>(</xsl:text></xsl:when>
                        <xsl:when test="@n='cGAT:non-pho'"><xsl:text>((</xsl:text></xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose> -->
                    <xsl:text>[</xsl:text>
                </xsl:if>                
                <!-- <xsl:if test="name()='ts' and preceding-sibling::*[1][self::nts and text()='(']">
                    <xsl:text>(</xsl:text>
                </xsl:if> -->
                    
                <xsl:value-of select="text()"/>                
                
                <xsl:choose>
                    <xsl:when test="not($THIS_END=$NEXT_END)">
                        <!-- <xsl:text> </xsl:text> -->
                        <xsl:apply-templates select="following-sibling::*[1]" mode="GRAB">
                            <xsl:with-param name="END_POINT" select="$NEXT_END"/>
                        </xsl:apply-templates>
                        <!-- <xsl:text> ALERT!!!!</xsl:text> -->
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:if test="name()='ats'">
                            <!-- <xsl:choose>
                                <xsl:when test="@n='cGAT:pause'"><xsl:text>)</xsl:text></xsl:when>
                                <xsl:when test="@n='cGAT:non-pho'"><xsl:text>))</xsl:text></xsl:when>
                                <xsl:otherwise/>
                            </xsl:choose> -->
                            <xsl:text>]</xsl:text>                        
                        </xsl:if>
                        
                        <!-- <xsl:if test="name()='ts' and following-sibling::*[1][self::nts and text()=')']">
                            <xsl:text>)</xsl:text>
                            <xsl:if test="name()='ts' and following-sibling::*[2][self::nts and text()=')']">
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                        </xsl:if>
                        <xsl:if test="name()='ts' and following-sibling::*[1][self::nts and text()='/']">
                            <xsl:text>/</xsl:text>
                        </xsl:if>        -->
                    </xsl:otherwise>
                </xsl:choose>
            </event>
        </xsl:if>
    </xsl:template>

    <xsl:template match="ats[ancestor::segmentation[@name=$TOKENIZED_SEGMENTATION_NAME]]|ts[ancestor::segmentation[@name=$TOKENIZED_SEGMENTATION_NAME] and not(*)]" mode="GRAB">
        <xsl:param name="END_POINT"/>
        <xsl:value-of select="text()"/>
        <xsl:choose>
            <xsl:when test="not(@e=$END_POINT)">
                <!-- <xsl:text> </xsl:text> -->
                <xsl:apply-templates select="following-sibling::*[1]" mode="GRAB">
                    <xsl:with-param name="END_POINT" select="$END_POINT"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="following-sibling::*[1][self::nts]">
                    <xsl:value-of select="following-sibling::*[1]/text()"/>
                    <xsl:if test="following-sibling::*[2][self::nts]">
                        <xsl:value-of select="following-sibling::*[2]/text()"/>
                    </xsl:if>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
        
    <xsl:template match="nts"/>
    
    <xsl:template match="nts" mode="GRAB">
        <xsl:param name="END_POINT"/>
        <xsl:value-of select="text()"/>
        <xsl:apply-templates select="following-sibling::*[1]" mode="GRAB">
            <xsl:with-param name="END_POINT" select="$END_POINT"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <!-- <xsl:template match="ats"/> -->
    
    <xsl:template match="ats" mode="GRAB">
        <xsl:param name="END_POINT"/>
        <xsl:value-of select="text()"/>
        <xsl:if test="not(@e=$END_POINT)">
            <xsl:apply-templates select="following-sibling::*[1]" mode="GRAB">
                <xsl:with-param name="END_POINT" select="$END_POINT"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->
    <!-- ****************************************************************************** -->

    <xsl:template match="annotation">
        <tier>  
            <!-- <annotation name="pos"> -->
            <xsl:variable name="ANNOTATION_NAME" select="@name"/>
            <xsl:attribute name="id" select="concat(../@id, '_', @name)"/>
            <xsl:attribute name="speaker" select="../@speaker"/>
            <xsl:attribute name="category" select="@name"/>
            <xsl:attribute name="type">a</xsl:attribute>
            <xsl:apply-templates select="ta"/>        
        </tier>            
    </xsl:template>
    
    <xsl:template match="ta">
        <!-- <ta id="SA_1" ref-id="Seg_6" s="T16" e="T16.TIE1.1">PPER</ta> -->
        <xsl:variable name="THIS_START" select="@s"/>
        <xsl:if test="//common-timeline/tli[@id=$THIS_START]">
            <xsl:variable name="THIS_END" select="@e"/>
            <xsl:variable name="NEXT_END">
                <xsl:choose>
                    <xsl:when test="//common-timeline/tli[@id=$THIS_END]"><xsl:value-of select="$THIS_END"/></xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="//ancestor::segmented-tier/descendant::timeline-fork[tli[@id=$THIS_END]]/@end"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <event>
                <xsl:attribute name="start" select="@s"/>
                <xsl:attribute name="end" select="$NEXT_END"/>
                <xsl:value-of select="text()"/>
                <xsl:if test="not($THIS_END=$NEXT_END)">
                    <xsl:variable name="NEXT_TA_START" select="following-sibling::ta[1]/@s"/>
                    <xsl:if test="not(//common-timeline/tli[@id=$NEXT_TA_START])">
                        <xsl:text> </xsl:text>
                        <xsl:apply-templates select="following-sibling::ta[1]" mode="GRAB">
                            <xsl:with-param name="END_POINT" select="$NEXT_END"/>
                        </xsl:apply-templates>
                    </xsl:if>
                    <!-- <xsl:text> ALERT!!!!</xsl:text> -->
                </xsl:if>
            </event>
        </xsl:if>        
    </xsl:template>
    
    <xsl:template match="ta" mode="GRAB">
        <xsl:param name="END_POINT"/>
        <xsl:value-of select="text()"/>
        <xsl:if test="not(@e=$END_POINT)">
            <xsl:variable name="NEXT_TA_START" select="following-sibling::ta[1]/@s"/>
            <xsl:if test="not(//common-timeline/tli[@id=$NEXT_TA_START])">                
                <xsl:text> </xsl:text>
                <xsl:apply-templates select="following-sibling::ta[1]" mode="GRAB">
                    <xsl:with-param name="END_POINT" select="$END_POINT"/>
                </xsl:apply-templates>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="segmented-tier[@type='d']">
        <tier>
            <xsl:attribute name="id" select="@id"/>
            <xsl:attribute name="speaker" select="@speaker"/>
            <xsl:attribute name="category" select="@category"/>
            <xsl:attribute name="type" select="@type"/>
            <xsl:apply-templates/>
            <xsl:for-each select="descendant::ats">
                <!-- <ats n="e" id="Seg_136" s="T1" e="T16">PM3 modelliert den Prozess am Laptop. Rekonstruktionsquelle: Video. </ats> -->
                <event>
                    <xsl:attribute name="start" select="@s"/>
                    <xsl:attribute name="end" select="@e"/>
                    <xsl:value-of select="text()"/>
                </event>                
            </xsl:for-each>
        </tier>
    </xsl:template>
    
        
</xsl:stylesheet>