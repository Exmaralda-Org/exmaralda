<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:exmaralda="http://www.exmaralda.org"
    exclude-result-prefixes="xs exmaralda"
    version="2.0">
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="ALIGNABLE_ANNOTATION[ancestor::TIER[@LINGUISTIC_TYPE_REF='TRANSLATION']]">
        <!-- <ANNOTATION>
            <ALIGNABLE_ANNOTATION ANNOTATION_ID="a47" TIME_SLOT_REF1="ts10" TIME_SLOT_REF2="ts12">
                <ANNOTATION_VALUE>Yes. Eight miles south.</ANNOTATION_VALUE>
            </ALIGNABLE_ANNOTATION>
        </ANNOTATION>
        <ANNOTATION>
            <REF_ANNOTATION ANNOTATION_ID="a43" ANNOTATION_REF="a17">
                <ANNOTATION_VALUE>Yes. How slaughters one it? With a hammer or how?</ANNOTATION_VALUE>
            </REF_ANNOTATION>
        </ANNOTATION> -->
        
        <xsl:variable name="START_TIME_ID" select="@TIME_SLOT_REF1"/>
        <xsl:variable name="END_TIME_ID" select="@TIME_SLOT_REF2"/>
        <xsl:variable name="PARENT_ID" select="ancestor::TIER/@PARENT_REF"/>
        <xsl:variable name="THIS_POSITION" select="count(../preceding-sibling::ANNOTATION) + 1"/>
        
        <xsl:variable name="REF_ANNO_ID">
            <xsl:choose>
                <xsl:when test="//TIER[@TIER_ID=$PARENT_ID]/descendant::ALIGNABLE_ANNOTATION[abs(exmaralda:time(@TIME_SLOT_REF1)-exmaralda:time($START_TIME_ID))&lt;20 and abs(exmaralda:time(@TIME_SLOT_REF2)-exmaralda:time($END_TIME_ID))&lt;20]">
                    <xsl:value-of select="//TIER[@TIER_ID=$PARENT_ID]/descendant::ALIGNABLE_ANNOTATION[abs(exmaralda:time(@TIME_SLOT_REF1)-exmaralda:time($START_TIME_ID))&lt;20 and abs(exmaralda:time(@TIME_SLOT_REF2)-exmaralda:time($END_TIME_ID))&lt;20]/@ANNOTATION_ID"/>
                </xsl:when>                
                <xsl:when test="string-length(//TIER[@TIER_ID=$PARENT_ID]/descendant::ANNOTATION[$THIS_POSITION]/ALIGNABLE_ANNOTATION/@ANNOTATION_ID)&gt;0">
                    <xsl:value-of select="//TIER[@TIER_ID=$PARENT_ID]/descendant::ANNOTATION[$THIS_POSITION]/ALIGNABLE_ANNOTATION/@ANNOTATION_ID"/>
                </xsl:when>
                <xsl:otherwise><!-- Throw it away !? --></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <REF_ANNOTATION>
            <xsl:attribute name="ANNOTATION_ID" select="@ANNOTATION_ID"/>
            <xsl:attribute name="ANNOTATION_REF" select="$REF_ANNO_ID"/>
            <xsl:apply-templates/>
        </REF_ANNOTATION>
        
        
    </xsl:template>
    
    <xsl:variable name="COPY_TIMESLOTS">
        <xsl:copy-of select="//TIME_ORDER"/>
    </xsl:variable>
    
    <xsl:function name="exmaralda:time">
        <xsl:param name="TIMEPOINT_ID"/>
        <xsl:value-of select="$COPY_TIMESLOTS/descendant::TIME_SLOT[@TIME_SLOT_ID=$TIMEPOINT_ID]/@TIME_VALUE"/>
    </xsl:function>


</xsl:stylesheet>