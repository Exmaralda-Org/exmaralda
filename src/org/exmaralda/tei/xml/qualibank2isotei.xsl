<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:tei="http://www.tei-c.org/ns/1.0"
    exclude-result-prefixes="xs tei" version="2.0" xmlns="http://www.tei-c.org/ns/1.0">

    <!-- The four UK QualiBank TEI Profiles as described by D Bell 04-Jul-2014 all share the <teiHeader> section,
     but vary in the <text> section. Profile 1 to 4 mainly consist of a "standard interviewer/respondent(s) transcript",
     modelled as a list of <u>, and 2 to 4 also include "structural notes" of various @type:s. These can be interviewers'
     comments, themes or headings in essay type transcripts. The 5th profile is a list of structural notes only, without 
     a transcript, with contents of contextual information, thus treated as metadata and not a transcript, and not now. -->

    <!-- identity transform for TEI to TEI -->
    <xsl:template match="node() | @*">
        <xsl:copy>
            <xsl:apply-templates select="node() | @*"/>
        </xsl:copy>
    </xsl:template>

    <!-- fileDesc - we take what's there -->
    <xsl:template match="//tei:fileDesc">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- publicationStmt needs some more info for valid XML -->
    <xsl:template match="//tei:publicationStmt[not(tei:publisher)]">
        <publicationStmt>
            <publisher>QualiBank</publisher>
            <xsl:apply-templates/>
        </publicationStmt>
    </xsl:template>

    <!-- teiHeader extension: profileDesc/particDesc and revisionDesc -->
    <xsl:template match="//tei:teiHeader[../tei:text/tei:body/tei:u/@who]">
        <xsl:copy>
            <xsl:apply-templates select="tei:fileDesc"/>
            <profileDesc>
                <!-- particDesc with list of speakers -->
                <particDesc>
                    <xsl:for-each select="distinct-values(//tei:u/@who)">
                        <person>
                            <xsl:attribute name="xml:id">
                                <xsl:value-of select="."/>
                            </xsl:attribute>
                            <xsl:attribute name="n">
                                <xsl:value-of select="."/>
                            </xsl:attribute>
                        </person>
                    </xsl:for-each>
                </particDesc>
            </profileDesc>
            <!-- revisionDesc -->
            <revisionDesc>
                <change>
                    <xsl:attribute name="when">
                        <xsl:value-of select="current-dateTime()"/>
                    </xsl:attribute>
                    <xsl:text>Created by XSL transformation (qualibank2isotei.xsl) from a QualiBank TEI transcript</xsl:text>
                </change>
            </revisionDesc>
        </xsl:copy>
    </xsl:template>

    <!-- text needs xml:lang, which is hopefully English, every u gets an annotationBlock etc. -->
    <xsl:template match="//tei:text">
        <xsl:copy>
            <xsl:attribute name="xml:lang">
                <xsl:text>en</xsl:text>
            </xsl:attribute>
            <timeline>
                <xsl:attribute name="unit">
                    <xsl:text>s</xsl:text>
                </xsl:attribute>
                <xsl:for-each select="//tei:u">
                    <xsl:variable name="n" select="@n"/>
                    <when>
                        <xsl:attribute name="xml:id">
                            <xsl:value-of select="concat('TLI_', $n)"/>
                        </xsl:attribute>
                    </when>
                </xsl:for-each>
                <when>
                    <xsl:attribute name="xml:id">
                        <xsl:value-of select="concat('TLI_', count(//tei:u) + 1)"/>
                    </xsl:attribute>
                </when>
            </timeline>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="//tei:u[not(tei:note)]">
        <xsl:variable name="id" select="@xml:id"/>
        <xsl:variable name="n" select="@n"/>
        <annotationBlock>
            <xsl:attribute name="xml:id">
                <xsl:value-of select="concat('aB_', $id)"/>
            </xsl:attribute>
            <xsl:attribute name="who">
                <xsl:value-of select="@who"/>
            </xsl:attribute>
            <xsl:attribute name="start">
                <xsl:value-of select="concat('TLI_', $n)"/>
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:value-of select="concat('TLI_', number($n) +1)"/>
            </xsl:attribute>
            <u>
                <xsl:attribute name="xml:id">
                    <xsl:value-of select="$id"/>
                </xsl:attribute>
                <seg>
                    <xsl:attribute name="type">
                        <xsl:text>contribution</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="xml:id">
                        <xsl:value-of select="concat('seg', '_', $id)"/>
                    </xsl:attribute>
                    <anchor>
                        <xsl:attribute name="synch">
                            <xsl:value-of select="concat('TLI_', $n)"/>
                        </xsl:attribute>
                    </anchor>
                    <!-- remove all trailing spaces, then add one, to avoid double spaces -->
                    <xsl:value-of select="concat(normalize-space(text()), ' ')"/>
                    <anchor>
                        <xsl:attribute name="synch">
                            <xsl:value-of select="concat('TLI_', number($n) + 1)"/>
                        </xsl:attribute>
                    </anchor>
                </seg>
            </u>
        </annotationBlock>
    </xsl:template>
    
    <!-- Interviewer comment notes are treated as incidents, since they describe things, though they are in fact annotations without bases -->

    <xsl:template match="note[ancestor::tei:u]">
        <spanGrp>
            <xsl:attribute name="type">
                <xsl:value-of select="@type"/>
            </xsl:attribute>
            <span>
                <xsl:attribute name="from">
                    <xsl:value-of select="ancestor::*[@start][1]/@start"/>
                </xsl:attribute>
                <xsl:attribute name="to">
                    <xsl:value-of select="ancestor::*[@end][1]/@end"/>
                </xsl:attribute>
                <xsl:value-of select="text()"/>
            </span>
        </spanGrp>
    </xsl:template>

    <!-- independent <note>s appearing as siblings of <u>s, in some cases they are rather really long spans, 
         but this is good enough for now since the scope is not made explicit. -->

    <xsl:template match="note[not(ancestor::tei:u)]">
        <incident>
            <!-- the independent notes have their own numbering (@n) -->
            <xsl:attribute name="start">
                <xsl:value-of select="concat('TLI_', number(@n) - 1)"/>
            </xsl:attribute>
            <xsl:attribute name="end">
                <xsl:value-of select="concat('TLI_', @n)"/>
            </xsl:attribute>
            <!-- only these independent notes have an xml:id, of course -->
            <xsl:attribute name="xml:id">
                <xsl:value-of select="concat('i_', @xml:id)"/>
            </xsl:attribute>
            <desc>
                <xsl:value-of select="text()"/>
            </desc>
        </incident>
    </xsl:template>
    
</xsl:stylesheet>
