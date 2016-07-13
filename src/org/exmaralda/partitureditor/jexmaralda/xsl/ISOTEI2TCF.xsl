<?xml version="1.0" encoding="UTF-8"?>
<!-- 
    change 03-03-2016: additional namespaces no longer necessary  xmlns:standoff="http://standoff.proposal"
    change 08-07-2016: new and hopefully definite mime type for textSource/@type attribute
    change 08-07-2016: first attempt at setting the language correctly
-->        

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0" 
    xmlns:tei="http://www.tei-c.org/ns/1.0"
    xmlns:dspin="http://www.dspin.de/data"  
    xmlns:metadata="http://www.dspin.de/data/metadata"
    xpath-default-namespace="xmlns=http://www.dspin.de/data">
    <xsl:template match="/">
        <D-Spin xmlns="http://www.dspin.de/data" version="0.4">
            
            <!-- *********************************************************** -->
            <!-- ***********          Metadata Header          ************* -->
            <!-- *********************************************************** -->
            
            <!-- The MetaData section is a placeholder for document related 
                metadata, which is used by WebLicht web application to store 
                metadata about resources and tools that were used to generate 
                and process the document, ensuring the conformity with the 
                CLARIN Metadata Schema (CMDI).  
                [http://weblicht.sfs.uni-tuebingen.de/weblichtwiki/index.php/The_TCF_Format] -->
            <MetaData xmlns="http://www.dspin.de/data/metadata">
                <Services>
                    <cmd:CMD xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xmlns:cmd="http://www.clarin.eu/cmd/" CMDVersion="1.1"
                        xsi:schemaLocation="http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1320657629623/xsd">
                        <cmd:Resources>
                            <cmd:ResourceProxyList/>
                            <cmd:JournalFileProxyList/>
                            <cmd:ResourceRelationList/>
                        </cmd:Resources>
                        <cmd:Components>
                            <cmd:WebServiceToolChain>
                                <cmd:GeneralInfo>
                                    <cmd:Descriptions>
                                        <cmd:Description/>
                                    </cmd:Descriptions>
                                    <cmd:ResourceName>Custom chain</cmd:ResourceName>
                                    <cmd:ResourceClass>Toolchain</cmd:ResourceClass>
                                </cmd:GeneralInfo>
                                <cmd:Toolchain>
                                    <cmd:ToolInChain>
                                        <!-- no idea what the meaning of this is -->
                                        <cmd:PID>11858/00-1778-0000-0004-BABF-C</cmd:PID>
                                    </cmd:ToolInChain>
                                </cmd:Toolchain>
                            </cmd:WebServiceToolChain>
                        </cmd:Components>
                    </cmd:CMD>
                </Services>
            </MetaData>


            <!-- *********************************************************** -->
            <!-- ***********       Actual annotation data      ************* -->
            <!-- *********************************************************** -->
            
            <!-- The TextCorpus section contains the linguistic data itself, 
                as well as specification of the language of the data as the 
                two-letter ISO 639-1 code. The linguistic annotations are divided 
                into separate sub-sections for each annotation layer, 
                according to the stand-off based architecture. -->
            <TextCorpus xmlns="http://www.dspin.de/data/textcorpus">
                <!-- changed 08-07-2016 -->
                <!-- need to set the language correctly -->
                <!-- not quite sure where this information should come from -->
                <!-- ideally, it is encoded inside the TEI document as an @xml:lang -->
                <!-- if so, apparently the language code variant is correct, since it has two letters -->
                <xsl:attribute name="lang">
                    <xsl:choose>
                        <!-- Look for the first element of the TEI document that has an @xml:lang attribute. If there is one, use it.  -->
                        <xsl:when test="//*[@xml:lang]"><xsl:value-of select="//*[@xml:lang][1]/@xml:lang"/></xsl:when>
                        <!-- use English as the default language -->
                        <xsl:otherwise>en</xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                
                <!-- the "plain text" of the transcription -->
                <text>
                    <xsl:call-template name="GET_TEXT"/>                    
                </text>
                
                <!-- the templates in this part only make sense if the input contains a segmentation, i.e. <w> and/or <pc> elements -->
                <xsl:if test="//tei:w | //tei:pc">
                    <!-- the individual <w> tokens of the transcription (excluding intervening non-word tokens like pauses etc.) -->
                    <xsl:element name="tokens">
                        <xsl:call-template name="GET_TOKENS"/>
                    </xsl:element>
                    
                    <!-- the sentence equivalents of the transcription, either <u> elements or, if present, u/seg elements -->
                    <sentences>
                        <xsl:call-template name="GET_SENTENCES"/>
                    </sentences>
                    
                    <!-- existing lemmatization of the transcription, if present -->
                    <xsl:if test="//tei:spanGrp[@type='lemma']">
                        <lemmas>
                            <xsl:call-template name="GET_LEMMAS"/>                    
                        </lemmas>
                    </xsl:if>
                    
                    
                    <!-- existing pos tagging of the transcription, if present -->
                    <!-- it is assumed that this will be STTS -->
                    <xsl:if test="//tei:spanGrp[@type='pos']">
                        <POStags tagset="stts">
                            <xsl:call-template name="GET_POS"/>                                        
                        </POStags>
                    </xsl:if>
                    
                    
                    <!-- existing (orthographic) normalitation of the transcription, if present -->
                    <xsl:if test="//tei:spanGrp[@type='n']">
                        <orthography>
                            <xsl:call-template name="GET_NORMALISATION"/>                                        
                        </orthography>
                    </xsl:if>
                    
                </xsl:if>
                
                <!-- the text source, i.e. a full identical copy of the original TEI file -->
                <textSource>
                    <!-- changed 08-07-2016 -->
                    <xsl:attribute name="type">
                        <xsl:text>application/tei+xml;format-variant=tei-iso-spoken</xsl:text>
                        <xsl:choose>
                            <xsl:when test="//tei:w | //tei:pc"><xsl:text>;tokenized=1</xsl:text></xsl:when>
                            <xsl:otherwise><xsl:text>;tokenized=0</xsl:text></xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                    <!-- <xsl:copy><xsl:apply-templates mode="COPY_ALL"/></xsl:copy> does not work, wanted to copy the original document in here, but this results in non-valid XML -->
                    <xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
                    <xsl:copy><xsl:apply-templates mode="COPY_ALL"/></xsl:copy>
                    <xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
                </textSource>

            </TextCorpus>
        </D-Spin>        
    </xsl:template>
    
    <!-- get the text of all <w> and <pc> elements separated by a single space -->
    <xsl:template name="GET_TEXT">
        <xsl:for-each select="//tei:w | //tei:pc">
            <xsl:apply-templates/><xsl:text> </xsl:text>
        </xsl:for-each>
    </xsl:template>
    
    <!-- get all <w> and <pc> elements separated by a single space -->
    <xsl:template name="GET_TOKENS">
        <xsl:for-each select="//tei:w | //tei:pc">
            <xsl:element name="token" namespace="http://www.dspin.de/data/textcorpus">
                <xsl:attribute name="ID" select="@xml:id"/>
                <xsl:apply-templates/>
            </xsl:element>
        </xsl:for-each>        
    </xsl:template>
    
    <xsl:template name="GET_SENTENCES">
        <!-- changed 13-03-2015 to cater for FOLKER ISO TEI which does not have seg underneath u --> 
        <xsl:for-each select="//tei:u/tei:seg[descendant::tei:w] | //tei:u[not(descendant::tei:seg) and descendant::tei:w]">
            <xsl:variable name="ID_SEQUENCE">
                <xsl:for-each select="descendant::tei:w">
                    <xsl:value-of select="@xml:id"/>
                    <xsl:text> </xsl:text>
                </xsl:for-each>
            </xsl:variable>
            <xsl:element name="sentence" namespace="http://www.dspin.de/data/textcorpus">
                <xsl:attribute name="ID" select="@xml:id"/>
                <xsl:attribute name="tokenIDs" select="normalize-space($ID_SEQUENCE)"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="GET_LEMMAS">
        <!-- 
        SOURCE:    
        <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="lemma">
            <span from="#w15" to="#w15">und</span>
        (/TEI/text[1]/body[1]/standoff:annotationGrp[5]/spanGrp[2]/span[1])    
        TARGET:
        <lemma ID="le_0" tokenIDs="t_0">Karin</lemma>
        -->
        <xsl:for-each select="//tei:spanGrp[@type='lemma']/descendant::tei:span[@from]">
            <xsl:choose>
                <xsl:when test="child::tei:span">
                    <xsl:for-each select="child::tei:span">
                        <xsl:element name="lemma" namespace="http://www.dspin.de/data/textcorpus">
                            <xsl:attribute name="ID"><xsl:text>le_</xsl:text><xsl:value-of select="generate-id()"/></xsl:attribute>
                            <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(../@from,'#')"/></xsl:attribute>                 
                            <xsl:value-of select="text()"/>
                        </xsl:element>                                                            
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="lemma" namespace="http://www.dspin.de/data/textcorpus">
                        <xsl:attribute name="ID"><xsl:text>le_</xsl:text><xsl:value-of select="generate-id()"/></xsl:attribute>
                        <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(@from,'#')"/></xsl:attribute>                 
                        <xsl:value-of select="text()"/>
                    </xsl:element>                                    
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template name="GET_POS">
        <!-- 
            SOURCE:    
            <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="pos">
            <span from="#w3601" to="#w3601">ADV</span>
            (/TEI/text[1]/body[1]/standoff:annotationGrp[5]/spanGrp[2]/span[1])    
            TARGET:
            <tag ID="pt_0" tokenIDs="t_0">NE</tag>
        -->
        <xsl:for-each select="//tei:spanGrp[@type='pos']/descendant::tei:span[@from]">
            <xsl:choose>
                <xsl:when test="child::tei:span">
                    <xsl:for-each select="child::tei:span">
                        <xsl:element name="tag" namespace="http://www.dspin.de/data/textcorpus">
                            <xsl:attribute name="ID"><xsl:text>pt_</xsl:text><xsl:value-of select="generate-id()"/></xsl:attribute>
                            <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(../@from,'#')"/></xsl:attribute>                 
                            <xsl:value-of select="text()"/>
                        </xsl:element>            
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="tag" namespace="http://www.dspin.de/data/textcorpus">
                        <xsl:attribute name="ID"><xsl:text>pt_</xsl:text><xsl:value-of select="generate-id()"/></xsl:attribute>
                        <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(@from,'#')"/></xsl:attribute>                 
                        <xsl:value-of select="text()"/>
                    </xsl:element>            
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <!-- normalisations are mapped onto the <correction> element in TCF -->
    <!-- not sure whether and how this information is used by what services in WebLicht -->
    <!-- it would be good, for instance, if taggers could take this level into account -->
    <xsl:template name="GET_NORMALISATION">
        <!-- 
            SOURCE:    
            <spanGrp xmlns="http://www.tei-c.org/ns/1.0" type="n">
            <span from="#w3606" to="#w3606">Fuß</span>
            (/TEI/text[1]/body[1]/standoff:annotationGrp[5]/spanGrp[2]/span[1])    
            TARGET:
            <correction operation="replace" tokenIDs="t_0">Karina</correction>
        -->
        <xsl:for-each select="//tei:spanGrp[@type='n']/descendant::tei:span[@from]">
            <xsl:choose>
                <xsl:when test="child::tei:span">
                    <xsl:for-each select="child::tei:span">
                        <xsl:element name="correction" namespace="http://www.dspin.de/data/textcorpus">
                            <xsl:attribute name="operation">replace</xsl:attribute>
                            <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(../@from,'#')"/></xsl:attribute>                 
                            <xsl:value-of select="text()"/>
                        </xsl:element>            
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:element name="correction" namespace="http://www.dspin.de/data/textcorpus">
                        <xsl:attribute name="operation">replace</xsl:attribute>
                        <xsl:attribute name="tokenIDs"><xsl:value-of select="substring-after(@from,'#')"/></xsl:attribute>                 
                        <xsl:value-of select="text()"/>
                    </xsl:element>            
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    

    <!-- this is used to copy the original document -->
    <xsl:template match="@*|node()" mode="COPY_ALL">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" mode="COPY_ALL"/>
        </xsl:copy>
    </xsl:template>    
    
</xsl:stylesheet>