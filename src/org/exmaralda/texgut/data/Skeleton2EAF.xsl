<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:param name="SPEAKER_NUMBERS">1,2</xsl:param>
    <xsl:param name="INTERVIEWER_NUMBERS">3,4</xsl:param>
    <xsl:param name="AUDIO_PATH">file:///D:/tgdp_20230210/prod/sound_files/1-1-1/1-1-1-3-a.wav</xsl:param>
    <xsl:param name="RELATIVE_AUDIO_PATH">../AUDIO/1-1-1-3-a.wav</xsl:param>
    
    
    <!-- copy everything unless a more specific template matches -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="MEDIA_DESCRIPTOR">
        <!-- <MEDIA_DESCRIPTOR MEDIA_URL="file:///D:/tgdp_20230210/prod/sound_files/1-1-1/1-1-1-3-a.wav"
            RELATIVE_MEDIA_URL="../AUDIO/1-1-1-3-a.wav" MIME_TYPE="audio/x-wav"/> -->
        <MEDIA_DESCRIPTOR MIME_TYPE="audio/x-wav">
            <xsl:attribute name="MEDIA_URL" select="$AUDIO_PATH"/>
            <xsl:attribute name="RELATIVE_MEDIA_URL" select="$RELATIVE_AUDIO_PATH"/>
        </MEDIA_DESCRIPTOR>        
    </xsl:template>
    
    <xsl:template match="TIME_ORDER">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
        
        <xsl:for-each select="tokenize($SPEAKER_NUMBERS, ',')">
            <xsl:variable name="CLEAN_SPEAKER_NUMBER" select="xs:integer(normalize-space(.))" as="xs:integer"/>
            <!-- <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSCRIPTION" PARTICIPANT="Speaker_0001"
                    TIER_ID="TRANSCRIPTION_Speaker_0001"> -->
            <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSCRIPTION">
                <xsl:attribute name="PARTICIPANT" select="concat('Speaker_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="TIER_ID" select="concat('TRANSCRIPTION_Speaker_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
            </TIER>
            
            <!-- 
             <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSLATION"
                PARENT_REF="TRANSCRIPTION_Interviewer_0001" PARTICIPANT="Interviewer_0001"
                TIER_ID="TRANSLATION_Interviewer_0001">            
            -->
            <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSLATION">
                <xsl:attribute name="PARENT_REF" select="concat('TRANSCRIPTION_Speaker_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="PARTICIPANT" select="concat('Speaker_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="TIER_ID" select="concat('TRANSLATION_Speaker_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
            </TIER>
        </xsl:for-each>
        
        <xsl:for-each select="tokenize($INTERVIEWER_NUMBERS, ',')">
            <xsl:variable name="CLEAN_SPEAKER_NUMBER" select="xs:integer(normalize-space(.))" as="xs:integer"/>
            <!-- <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSCRIPTION" PARTICIPANT="Speaker_0001"
                    TIER_ID="TRANSCRIPTION_Speaker_0001"> -->
            <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSCRIPTION">
                <xsl:attribute name="PARTICIPANT" select="concat('Interviewer_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="TIER_ID" select="concat('TRANSCRIPTION_Interviewer_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
            </TIER>
            
            <!-- 
             <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSLATION"
                PARENT_REF="TRANSCRIPTION_Interviewer_0001" PARTICIPANT="Interviewer_0001"
                TIER_ID="TRANSLATION_Interviewer_0001">            
            -->
            <TIER DEFAULT_LOCALE="en" LINGUISTIC_TYPE_REF="TRANSLATION">
                <xsl:attribute name="PARENT_REF" select="concat('TRANSCRIPTION_Interviewer_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="PARTICIPANT" select="concat('Interviewer_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
                <xsl:attribute name="TIER_ID" select="concat('TRANSLATION_Interviewer_',format-number($CLEAN_SPEAKER_NUMBER, '0000'))"></xsl:attribute>
            </TIER>
        </xsl:for-each>
        

    </xsl:template>
</xsl:stylesheet>