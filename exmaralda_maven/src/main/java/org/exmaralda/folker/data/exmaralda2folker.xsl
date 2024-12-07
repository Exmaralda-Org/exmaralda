<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:folker="http://www.exmaralda.org"  xmlns:xs="http://www.w3.org/2001/XMLSchema" version="2.0">
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
    
    <xsl:function name="folker:timeline-position" as="xs:integer">
        <xsl:param name="timeline-id"/>
        <xsl:value-of select="$timeline-positions/descendant::item[@id=$timeline-id]/@position"/>
    </xsl:function>
    
    <xsl:function name="folker:last-endpoint-of-segment-chain">
        <xsl:param name="event"/>
        <xsl:choose>
            <xsl:when test="not($event/following-sibling::event) or folker:timeline-position($event/following-sibling::event[1]/@start)&gt;folker:timeline-position($event/@end)">
                <xsl:value-of select="$event/@end"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="folker:last-endpoint-of-segment-chain($event/following-sibling::event[1])"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <xsl:template match="/">
            <folker-transcription>
                <head>        
                </head>
                <speakers>
                    <xsl:apply-templates select="//speaker"/>
                </speakers>
                <xsl:element name="recording">
                    <xsl:attribute name="path">
                        <xsl:variable name="WAV_URL">
                            <xsl:choose>
                                <xsl:when test="//referenced-file[ends-with(lower-case(@url),'wav')]">
                                    <xsl:value-of select="//referenced-file[ends-with(lower-case(@url),'wav')][1]/@url"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="//referenced-file[1]/@url"/>                                    
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:value-of select="$WAV_URL"/>
                    </xsl:attribute>
                </xsl:element>
                <timeline>
                    <xsl:apply-templates select="//tli"/>
                </timeline>
                
                <xsl:for-each select="//tli">
                    <xsl:for-each select="//tier[@type='t' and not(@speaker)]/event[@start=current()/@id]">
                       <xsl:apply-templates select="." mode="first-pass"/>
                    </xsl:for-each>
                    <xsl:for-each select="//tier[@type='t' and @speaker]/event[@start=current()/@id]">
                        <xsl:if test="not(preceding-sibling::event) or folker:timeline-position(@start)&gt;folker:timeline-position(preceding-sibling::event[1]/@end)">
                            <xsl:apply-templates select="." mode="first-pass"/>
                        </xsl:if>                            
                    </xsl:for-each>
                </xsl:for-each>                
          </folker-transcription>
    </xsl:template>
    
    <xsl:template match="event[../@speaker and string-length(../@speaker)&gt;0]" mode="first-pass">
        <xsl:element name="contribution">
            <xsl:attribute name="speaker-reference">
                <xsl:value-of select="parent::tier/@speaker"/>
            </xsl:attribute>
            <xsl:attribute name="start-reference">
                <xsl:value-of select="@start"/>
            </xsl:attribute>
            <xsl:attribute name="end-reference">
                <xsl:value-of select="folker:last-endpoint-of-segment-chain(.)"/>
            </xsl:attribute>
            <xsl:element name="segment">
                <xsl:attribute name="start-reference">
                    <xsl:value-of select="@start"/>
                </xsl:attribute>
                <xsl:attribute name="end-reference">
                    <xsl:value-of select="@end"/>
                </xsl:attribute>
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:if test="following-sibling::event and folker:timeline-position(@end)&gt;=folker:timeline-position(following-sibling::event[1]/@start)">
                <xsl:apply-templates select="following-sibling::event[1]" mode="second-pass"/>
            </xsl:if>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="event[not(../@speaker) or string-length(../@speaker)=0]" mode="first-pass">
        <xsl:element name="contribution">
            <xsl:attribute name="start-reference">
                <xsl:value-of select="@start"/>
            </xsl:attribute>
            <xsl:attribute name="end-reference">
                <xsl:value-of select="@end"/>
            </xsl:attribute>
            <xsl:element name="segment">
                <xsl:attribute name="start-reference">
                    <xsl:value-of select="@start"/>
                </xsl:attribute>
                <xsl:attribute name="end-reference">
                    <xsl:value-of select="@end"/>
                </xsl:attribute>
                <xsl:value-of select="text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

    <xsl:template match="event" mode="second-pass">
        <xsl:element name="segment">
            <xsl:attribute name="start-reference">
                <xsl:value-of select="@start"/>
            </xsl:attribute>
            <xsl:attribute name="end-reference">
                <xsl:value-of select="@end"/>
            </xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
        <xsl:if test="following-sibling::event and folker:timeline-position(@end)&gt;=folker:timeline-position(following-sibling::event[1]/@start)">
            <xsl:apply-templates select="following-sibling::event[1]" mode="second-pass"/>
        </xsl:if>        
    </xsl:template>
    
    
    <xsl:template match="speaker">
        <xsl:element name="speaker">
            <xsl:attribute name="speaker-id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:element name="name">
                <xsl:value-of select="ud-speaker-information/ud-information[@attribute-name='Name']"/>
            </xsl:element>
        </xsl:element>        
    </xsl:template>
    
    <xsl:template match="tli">
        <xsl:element name="timepoint">
            <xsl:attribute name="timepoint-id">
                <xsl:value-of select="@id"/>
            </xsl:attribute>
            <xsl:attribute name="absolute-time">
                <xsl:value-of select="@time"/>
            </xsl:attribute>                
        </xsl:element>        
    </xsl:template>
</xsl:stylesheet>
