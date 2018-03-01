<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:exmaralda="http://www.exmaralda.org"
    exclude-result-prefixes="xs" version="2.0">
    
    <xsl:param name="PRIMARY_TIER_TYPE" select="'parent'"/>
    <xsl:param name="ORTH_TIER_TYPE" select="'orthT'"/>
    <xsl:param name="WORD_TIER_TYPE" select="'wordT'"/>
    
    <xsl:variable name="COPY_TIME">
        <xsl:copy-of select="//TIME_ORDER"/>
    </xsl:variable>
    <xsl:function name="exmaralda:TIME_SLOT_POSITION">
        <xsl:param name="LOOKUP_ID"/>
        <xsl:value-of select="count($COPY_TIME/descendant::TIME_SLOT[@TIME_SLOT_ID=$LOOKUP_ID]/preceding-sibling::*)"/>
    </xsl:function>
    
    <xsl:template match="/">
        <TEI>

            <teiHeader>
                <fileDesc>
                    <titleStmt>
                        <title><xsl:value-of select="base-uri()"/></title>
                    </titleStmt>

                    <!-- *********************************** -->
                    <!-- Distribution information, see 4.1.1 -->
                    <!-- *********************************** -->
                    <publicationStmt>
                        <!-- FILL ME IN -->
                    </publicationStmt>

                    <!-- ******************************** -->
                    <!-- Recording information, see 4.1.2 -->
                    <!-- ******************************** -->
                    <sourceDesc>
                        <recordingStmt>
                            <recording type="audio">
                                <media mimeType="audio/wav">
                                    <xsl:attribute name="url">
                                        <xsl:value-of select="tokenize(/ANNOTATION_DOCUMENT/HEADER[1]/MEDIA_DESCRIPTOR[1]/@MEDIA_URL, '/')[last()]"/>
                                    </xsl:attribute>
                                </media>
                                
                            </recording>
                        </recordingStmt>
                    </sourceDesc>
                </fileDesc>

                <profileDesc>

                    <!-- ********************************** -->
                    <!-- Participant information, see 4.2.1 -->
                    <!-- ********************************** -->
                    <particDesc>
                        <!-- <xsl:for-each-group select="//TIER" group-by="@PARTICIPANT"> -->
                        <xsl:for-each-group select="//TIER[not(@PARENT_REF)]" group-by="@TIER_ID">
                            <person role="speaker">
                                <xsl:attribute name="xml:id" select="translate(current-grouping-key(), ' ', '_')"/>
                                <xsl:attribute name="n" select="current-grouping-key()"/>
                            </person>
                        </xsl:for-each-group>
                    </particDesc>
                    

                    <!-- ****************************** -->
                    <!-- Setting information, see 4.2.2 -->
                    <!-- ****************************** -->
                    <settingDesc>
                        <!-- FILL ME IN -->
                    </settingDesc>

                </profileDesc>

                <!-- ****************************** -->
                <!-- Description of source, see 4.3 -->
                <!-- ****************************** -->
                <encodingDesc>
                    <!-- FILL ME IN -->
                </encodingDesc>

                <revisionDesc>
                    <!-- FILL ME IN -->
                </revisionDesc>
            </teiHeader>

            <!-- END TEI HEADER -->

            <text>
                <xsl:apply-templates select="//TIME_ORDER"/>
                <body>
                    <!-- <xsl:apply-templates select="//TIER[@LINGUISTIC_TYPE_REF=$PRIMARY_TIER_TYPE]/ANNOTATION" mode="primary"> -->
                    <xsl:apply-templates select="//TIER[not(@PARENT_REF)]/ANNOTATION" mode="primary">
                        <xsl:sort select="exmaralda:TIME_SLOT_POSITION(ALIGNABLE_ANNOTATION/@TIME_SLOT_REF1)" data-type="number"/>
                    </xsl:apply-templates>
                </body>
            </text>
        </TEI>

    </xsl:template>

    <xsl:template match="TIME_ORDER">
        <!-- ***************** -->
        <!-- Timeline, see 5.1 -->
        <!-- ***************** -->
        <timeline unit="s">
            <when xml:id="ts_origin"/>
            <xsl:apply-templates select="TIME_SLOT"/>
        </timeline>
    </xsl:template>

    <!--  <TIME_SLOT TIME_SLOT_ID="ts1" TIME_VALUE="970"/> -->
    <xsl:template match="TIME_SLOT">
        <when since="#ts_origin">
            <xsl:attribute name="interval" select="@TIME_VALUE div 1000.0"/>
            <xsl:attribute name="xml:id" select="@TIME_SLOT_ID"/>
        </when>
    </xsl:template>
    
    <xsl:template match="ANNOTATION" mode="primary">
        <annotationBlock>
            <xsl:attribute name="start" select="ALIGNABLE_ANNOTATION/@TIME_SLOT_REF1"/>
            <xsl:attribute name="end" select="ALIGNABLE_ANNOTATION/@TIME_SLOT_REF2"/>
            <!-- <xsl:attribute name="who" select="ancestor::TIER/@PARTICIPANT"/> -->
            <xsl:attribute name="who" select="translate(ancestor::TIER/@TIER_ID, ' ', '_')"/>
            <xsl:attribute name="xml:id">
                <xsl:text>ab_</xsl:text><xsl:value-of select="ALIGNABLE_ANNOTATION/@ANNOTATION_ID"/>
            </xsl:attribute> 
            <xsl:variable name="THIS_ID" select="ALIGNABLE_ANNOTATION/@ANNOTATION_ID"/>
            <xsl:variable name="THIS_TIER_ID" select="ancestor::TIER/@TIER_ID"/>
            <xsl:variable name="THIS_START" select="ALIGNABLE_ANNOTATION/@TIME_SLOT_REF1"/>
            <xsl:variable name="THIS_POSITION" select="count(preceding-sibling::ANNOTATION) + 1"/>
            <u>
                <xsl:attribute name="xml:id" select="$THIS_ID"/>
                <!-- <xsl:value-of select="ALIGNABLE_ANNOTATION/ANNOTATION_VALUE"/> -->
                <!-- words -->
                <xsl:analyze-string select="replace(ALIGNABLE_ANNOTATION/ANNOTATION_VALUE, '\.\.\.', '&#x2026;')" regex="([A-ZÄÖÜa-zäöüß]+(-)?|(\((\?)+)\))">
                    <xsl:matching-substring>
                        <w>
                            <xsl:attribute name="xml:id"><xsl:value-of select="$THIS_ID"/><xsl:text>_w</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
                            <xsl:value-of select="."/>
                        </w>
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <xsl:variable name="POSITION1" select="position()"/>
                        <xsl:analyze-string select="." regex=" +">
                            <xsl:matching-substring><!-- do nothing, i.e. get rid of whitespace --></xsl:matching-substring>
                            <!-- puncutation -->
                            <xsl:non-matching-substring>
                                <xsl:analyze-string select="." regex=".">
                                   <xsl:matching-substring>
                                       <p>
                                           <xsl:attribute name="xml:id"><xsl:value-of select="$THIS_ID"/><xsl:text>_p</xsl:text><xsl:value-of select="$POSITION1"/><xsl:text>_</xsl:text><xsl:value-of select="position()"/></xsl:attribute>
                                           <xsl:value-of select="."/>
                                       </p>                                         
                                   </xsl:matching-substring>                                   
                                </xsl:analyze-string>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </u>
            
            <xsl:choose>
                <xsl:when test="//REF_ANNOTATION[@ANNOTATION_REF=$THIS_ID and contains(ancestor::TIER/@TIER_ID, 'Translation')]">
                    <spanGrp type="translation" xml:lang="en">
                        <span>
                            <xsl:value-of select="//REF_ANNOTATION[@ANNOTATION_REF=$THIS_ID  and contains(ancestor::TIER/@TIER_ID, 'Translation')]/ANNOTATION_VALUE"/>
                        </span>
                    </spanGrp>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:if test="//TIER[@PARENT_REF=$THIS_TIER_ID and starts-with(@TIER_ID, 'Translation')]">
                        <spanGrp type="translation" xml:lang="en">
                            <span>
                                <xsl:value-of select="//TIER[@PARENT_REF=$THIS_TIER_ID and contains(@TIER_ID, 'Translation')]/descendant::ANNOTATION[$THIS_POSITION]/descendant::ALIGNABLE_ANNOTATION"/>
                            </span>
                        </spanGrp>                        
                    </xsl:if>
                </xsl:otherwise>
            </xsl:choose>
        </annotationBlock>
    </xsl:template>

</xsl:stylesheet>
