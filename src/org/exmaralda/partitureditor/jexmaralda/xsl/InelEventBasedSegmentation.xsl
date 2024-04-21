<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:math="http://www.w3.org/2005/xpath-functions/math"
    xmlns:exmaralda="http://www.exmaralda.org"
    exclude-result-prefixes="xs math"
    version="2.0">
    
    <xsl:variable name="TIMELINE_COPY" as="node()">
        <timeline>
            <xsl:for-each select="//tli">
                <tli>
                    <xsl:attribute name="id" select="@id"/>
                    <xsl:attribute name="position" select="position()"/>
                </tli>
            </xsl:for-each>
        </timeline>
    </xsl:variable>
    
    <xsl:function name="exmaralda:timelinePosition" as="xs:integer">
        <xsl:param name="TLI_ID"/>
        <xsl:sequence select="$TIMELINE_COPY/descendant::tli[@id=$TLI_ID]/@position"/>
    </xsl:function>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- 
        <segmentation name="SpeakerContribution_Event" tierref="tx-KuNS">
            <ts n="sc" id="tx-KuNS.sc0" s="T1" e="T35">
                <ts n="e" id="tx-KuNS.e0" s="T1" e="T2">– Jekatʼerʼina </ts>
                <ts n="e" id="tx-KuNS.e1" s="T2" e="T3">Izmajlavna, </ts>    
    -->
    
    <xsl:template match="segmentation[@name='SpeakerContribution_Event']" mode="#default">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>            
        </xsl:copy>
        <xsl:apply-templates mode="NEW_SEGMENTATION" select="."/>
    </xsl:template>
    
    <xsl:template match="segmentation[@name='SpeakerContribution_Event']" mode="NEW_SEGMENTATION">
        <xsl:variable name="SPEAKER" select="../@speaker"/>
        <segmentation name="SpeakerContribution_Utterance_Word">
            <xsl:attribute name="tierref" select="@tierref"/>
            <xsl:for-each select="ts">
                <ts n="sc">
                    <xsl:attribute name="id" select="concat(@id, '_INELSEG')"/>
                    <xsl:attribute name="s" select="@s"/>
                    <xsl:attribute name="e" select="@e"/>
                    <xsl:variable name="SC_START" select="exmaralda:timelinePosition(@s)"/>
                    <xsl:variable name="SC_END" select="exmaralda:timelinePosition(@e)"/>
                    <xsl:for-each select="../../annotation[@name='ref']/ta[exmaralda:timelinePosition(@s) &gt;= $SC_START and exmaralda:timelinePosition(@e) &lt;= $SC_END]">
                        <ts n="INEL:u">
                            <xsl:attribute name="id" select="concat($SPEAKER, '_', @s, '_', @e, '_u_', position())"/>
                            <xsl:attribute name="s" select="@s"/>
                            <xsl:attribute name="e" select="@e"/>
                            <xsl:variable name="U_START" select="exmaralda:timelinePosition(@s)"/>
                            <xsl:variable name="U_END" select="exmaralda:timelinePosition(@e)"/>
                            <xsl:for-each select="ancestor::segmented-tier/descendant::ts[@n='e' and exmaralda:timelinePosition(@s) &gt;= $U_START and exmaralda:timelinePosition(@e) &lt;= $U_END]">
                                <xsl:variable name="W_START" select="@s"/>
                                <xsl:variable name="W_END" select="@e"/>
                                <xsl:variable name="ID" select="@id"/>
                                <xsl:choose>
                                    <xsl:when test="starts-with(text(), '((')">
                                        <!-- PAUSE or UNITELLIGIBLE OR NON-SPEECH-->
                                        <!-- <ats e="T315" id="Seg_3592" n="HIAT:non-pho" s="T314">PAUSE</ats> -->
                                        <nts n="INEL:ip" id="{concat($ID, '_nts1')}">(</nts>
                                        <nts n="INEL:ip" id="{concat($ID, '_nts2')}">(</nts>
                                        <ats n="INEL:non-pho" s="{$W_START}" e="{$W_END}">
                                            <xsl:attribute name="id" select="replace($ID, '\.e', '.np')"/>
                                            <xsl:value-of select="substring-before(substring-after(text(), '(('), '))')"/>
                                        </ats>
                                        <nts n="INEL:ip" id="{concat($ID, '_nts3')}">)</nts>
                                        <nts n="INEL:ip" id="{concat($ID, '_nts4')}">)</nts>
                                        <nts n="INEL:ip" id="{concat($ID, '_nts5')}"> </nts>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <!-- WORD -->
                                        <xsl:analyze-string select="text()" regex="[\(\) …\.\?!,–—\-=]">
                                            <xsl:matching-substring>
                                                <nts n="INEL:ip">
                                                    <xsl:attribute name="id" select="concat('nts_', $ID, '_', position())"></xsl:attribute>
                                                    <xsl:value-of select="."/>
                                                </nts>
                                            </xsl:matching-substring>
                                            <xsl:non-matching-substring>
                                                <ts n="INEL:w" s="{$W_START}" e="{$W_END}">
                                                    <xsl:attribute name="id" select="replace($ID, '\.e', '.w')"/>
                                                    <xsl:value-of select="."/>
                                                </ts>
                                            </xsl:non-matching-substring>
                                        </xsl:analyze-string>
                                    </xsl:otherwise>
                                </xsl:choose>                                
                            </xsl:for-each>
                        </ts>                    
                    </xsl:for-each>
                </ts>                    
            </xsl:for-each>
        </segmentation>
            
    </xsl:template>
        
    
    

</xsl:stylesheet>