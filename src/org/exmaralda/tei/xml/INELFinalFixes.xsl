<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    
    <!-- These are the symbols signalling morpheme breaks in the morpheme layers -->
    <xsl:param name="MORPHEME_TOKENIZE_REGEX">[=\-]</xsl:param>

    <!-- mb, mp, ge, gr, and gg  -->
    <xsl:param name="MORPHEME_LEVEL_LAYERS">
        <layers>
            <layer>mb</layer>
            <layer>mp</layer>
            <layer>ge</layer>
            <layer>gr</layer>
            <layer>gg</layer>
        </layers>
    </xsl:param>
    
    <!-- If an explicit list is the way to go, then I would suggest treating the following as "word-level":
        tx, mc, ps, SeR, SyF, IST, BOR, BOR-Morph, BOR-Phon, BOR-Src, CS, ExLocPoss, geo 
    -->
    <xsl:param name="WORD_LEVEL_LAYERS">
        <layers>
            <layer>mc</layer>
            <layer>ps</layer>
            <layer>SeR</layer>
            <layer>SyF</layer>
            <layer>IST</layer>
            <layer>BOR</layer>
            <layer>BOR-Morph</layer>
            <layer>BOR-Phon</layer>
            <layer>BOR-Src</layer>
            <layer>CS</layer>
            <layer>ExLocPoss</layer>
            <layer>geo</layer>
        </layers>
    </xsl:param>
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- For morpheme level layers: Tokenize and assign ids or ref-ids -->
    <xsl:template match="tei:spanGrp[@type = $MORPHEME_LEVEL_LAYERS/layers/layer]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="@from"/>
            <xsl:attribute name="to" select="@from"/>
            <xsl:variable name="AB_ID" select="ancestor::tei:annotationBlock/@xml:id"/>
            <xsl:variable name="COUNT" select="count(preceding-sibling::tei:span) + 1"/>
            <xsl:variable name="TYPE" select="parent::tei:spanGrp/@type"/>
            <xsl:for-each select="tokenize(text(), $MORPHEME_TOKENIZE_REGEX)">
                <tei:span>
                    <xsl:choose>
                        <!-- mb is special: it defines the morphemes, so it gets an @xml:id -->
                        <xsl:when test="$TYPE='mb'">
                            <xsl:attribute name="xml:id" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                            
                        </xsl:when>
                        <!-- all others refer to the morpheme defined under mb via @from and @to -->
                        <xsl:otherwise>
                            <xsl:attribute name="from" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                            
                            <xsl:attribute name="to" select="concat($AB_ID, '_mb', $COUNT, '_', position())"/>                                                        
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:value-of select="."/>
                </tei:span>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    

    <!-- For word layers: make to the same value as from, i.e. skip/ignore punctuation -->
    <xsl:template match="tei:spanGrp[@type = $WORD_LEVEL_LAYERS/layers/layer]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="@from"/>
            <xsl:attribute name="to" select="@from"/>
            <xsl:value-of select="text()"/>
        </xsl:copy>        
    </xsl:template>
        

    <!-- Sentence-level annotations (tier types ref, st, stl, ts, fe, fr, ltr, nt in the example, often there are others) refer to words:
        <span from="TIE4.e0.w" to="TIE4.e1.2.1">ASS_ChND_190725_Batu_conv.ASS.001 (001)</span>
            ...when they should refer to the segment instead:
        <span from="TIE4.u1" to="TIE4.u1">ASS_ChND_190725_Batu_conv.ASS.001 (001)</span>
        Then everything that's neither "morpheme-" not "word-level" belongs to the "sentence-level" bucket.        
    -->
    <xsl:template match="tei:spanGrp[not(@type = $MORPHEME_LEVEL_LAYERS/layers/layer or @type = $WORD_LEVEL_LAYERS/layers/layer)]/tei:span">
        <xsl:copy>
            <xsl:attribute name="from" select="id(@from)/ancestor::tei:seg[1]/@xml:id"/>
            <xsl:attribute name="to" select="id(@to)/ancestor::tei:seg[1]/@xml:id"/>
            <xsl:apply-templates select="@*[not(name()='from' or name()='to')]|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- incident nodes may be lacking ids:
        <incident><desc>ChND:</desc></incident>
        The old converter created them:
        <incident xml:id="inc7"><desc>ChND:</desc></incident> 
    -->    
    <xsl:template match="tei:incident[not(@xml:id)]">
        <xsl:copy>
            <xsl:attribute name="xml:id" select="generate-id(.)"/>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>        
    </xsl:template>
        
    

    
</xsl:stylesheet>