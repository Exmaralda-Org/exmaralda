<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" xmlns:foxml="info:fedora/fedora-system:def/foxml#">
    <!-- transforms a <transcription> element from a COMA file to a FOXML document which can be used to ingest a transcription data object into Fedora -->
    <!-- This is the first attempt, 10-10-2011 -->
    <xsl:param name="CORPUS_NAMESPACE"/>
    <xsl:output method="xml" encoding="UTF-8"/>
    <!-- <xsl:variable name="CORPUS_NAMESPACE">hamatac</xsl:variable> -->
    <!-- top level template -->
    <xsl:template match="/">
        <foxml:digitalObject VERSION="1.1" xmlns:foxml="info:fedora/fedora-system:def/foxml#"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"  
            xsi:schemaLocation="info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd">
            <!-- PID="rec:maptask_hitomi-elisa" -->
            <xsl:attribute name="PID">rec:<xsl:value-of select="$CORPUS_NAMESPACE"/>_<xsl:value-of select="//Communication/@Name"/></xsl:attribute>
            
            <!-- <Recording Id="RIDA65F0FB5-F41D-714E-F923-48675F74C8B4">
                <Name>MT_091209_Dimitri</Name>
                <Media Id="MIDBA3B9496-B2E7-10CC-7C71-EF41A26E8AB0">
                    <Description>
                        <Key Name="Type">digital</Key>
                    </Description>
                    <NSLink>Ali_Dimitri/MT_091209_Dimitri.wav</NSLink>
                </Media>
                <Media Id="MID4063F6FE-1B6E-3787-C0F9-063088BC352B">
                    <Description>
                        <Key Name="Type">digital</Key>
                    </Description>
                    <NSLink>Ali_Dimitri/MT_091209_Dimitri.mp3</NSLink>
                </Media>
                <Description />
                <RecordingDuration>211330</RecordingDuration>
            </Recording> -->
            <!-- *****************************************************  -->
            <foxml:objectProperties>
                <foxml:property NAME="info:fedora/fedora-system:def/model#state" VALUE="Active"/>
                <foxml:property NAME="info:fedora/fedora-system:def/model#label">
                    <xsl:attribute name="VALUE">Recording <xsl:value-of select="//Recording[1]/Name"/></xsl:attribute>
                </foxml:property>
                <foxml:property NAME="info:fedora/fedora-system:def/model#ownerId" VALUE="fedoraAdmin"/>
            </foxml:objectProperties>
            <!-- *****************************************************  -->
            
            <xsl:call-template name="FEDORA_DC_STREAM"/>
            <!-- <xsl:call-template name="HZSK_DC_STREAM"/> -->
            <xsl:call-template name="CMDI_STREAM"/>
            <xsl:call-template name="RECORDING_FILE_STREAM"/>
            <xsl:call-template name="EXTERNAL_RELATIONS_STREAM"/>
            <xsl:call-template name="TN_STREAM"/>
            <xsl:call-template name="XACML_STREAM"/>
            <xsl:call-template name="COLLECTION_VIEW_STREAM"/>
            
            
            
        </foxml:digitalObject>    
    </xsl:template>
    
    <!-- *****************************************************  -->
    
    <xsl:template name="COLLECTION_VIEW_STREAM">
        <foxml:datastream CONTROL_GROUP="M" ID="COLLECTION_VIEW" STATE="A" VERSIONABLE="true">
            <foxml:datastreamVersion>
                <xsl:attribute name="CREATED"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                <xsl:attribute name="ID">coll_xsl.0</xsl:attribute>
                <xsl:attribute name="LABEL">collection-view-xsl</xsl:attribute>
                <xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
                <foxml:contentLocation>
                    <xsl:attribute name="REF">http://hrsk.multilingua.uni-hamburg.de/xsl/collection_view_rec.xsl</xsl:attribute>
                    <xsl:attribute name="TYPE">URL</xsl:attribute>
                </foxml:contentLocation>
            </foxml:datastreamVersion>           
        </foxml:datastream>
    </xsl:template>
    
    <!-- *****************************************************  -->
    
    <xsl:template name="RECORDING_FILE_STREAM">
    
        <!-- the wav recording -->
        <foxml:datastream CONTROL_GROUP="M" ID="WAV" STATE="A" VERSIONABLE="true">
            <foxml:datastreamVersion>
                    <!-- <foxml:datastreamVersion CREATED="2011-10-07T14:13:47.638Z" ID="exb.0" LABEL="Shirin.exb" MIMETYPE="text/xml" SIZE="49341"> -->
                    <!--  CREATED="2011-10-07T14:21:19.976Z" ID="wav.0" LABEL="MT_270110_Shirin.wav" MIMETYPE="audio/x-wav" SIZE="49250460" -->
                <xsl:attribute name="CREATED"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                <xsl:attribute name="ID">wav.0</xsl:attribute>
                <xsl:attribute name="LABEL"><xsl:value-of select="//Recording[1]/Name"/></xsl:attribute>
                <xsl:attribute name="MIMETYPE">audio/x-wav</xsl:attribute>
                <!-- <xsl:attribute name="SIZE"></xsl:attribute> -->
                <foxml:contentLocation>
                    <!-- REF="http://localhost:8080/fedora/get/rec:hamatac_MT_270110_Shirin/wav/2011-10-07T14:21:19.976Z" TYPE="INTERNAL_ID" -->
                    <xsl:attribute name="REF"><xsl:value-of select="//Media[ends-with(NSLink, '.wav')]/NSLink"/></xsl:attribute>
                    <xsl:attribute name="TYPE">INTERNAL_ID</xsl:attribute>
                    <xsl:attribute name="resolve">@REF</xsl:attribute>
                </foxml:contentLocation>
            </foxml:datastreamVersion>
        </foxml:datastream>        
        
    </xsl:template>
    
    <!-- *****************************************************  -->
    <xsl:template name="TN_STREAM">    
        <!-- the icon for this type of resource -->
        <foxml:datastream CONTROL_GROUP="M" ID="TN" STATE="A" VERSIONABLE="true">
            <foxml:datastreamVersion>
                <xsl:attribute name="CREATED"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                <xsl:attribute name="ID">tn.0</xsl:attribute>
                <xsl:attribute name="LABEL">recording-icon</xsl:attribute>
                <xsl:attribute name="MIMETYPE">image/png</xsl:attribute>
                <foxml:contentLocation>
                    <xsl:attribute name="REF">http://localhost:8080/fedora/get/hzsk:recording-icon/tn</xsl:attribute>
                    <xsl:attribute name="TYPE">URL</xsl:attribute>
                </foxml:contentLocation>
            </foxml:datastreamVersion>
        </foxml:datastream>        
        
    </xsl:template>
    <!-- *****************************************************  -->
    
    <!-- the Dublin Core metadata stream for this object -->
    <xsl:template name="FEDORA_DC_STREAM">
        <foxml:datastream CONTROL_GROUP="X" ID="DC" STATE="A" VERSIONABLE="true">
            <foxml:datastreamVersion CREATED="2011-10-07T13:54:12.093Z" FORMAT_URI="http://www.openarchives.org/OAI/2.0/oai_dc/" ID="DC1.0" LABEL="Dublin Core Record for this object" MIMETYPE="text/xml" SIZE="450">
                <foxml:xmlContent>
                    <oai_dc:dc 
                        xmlns:dc="http://purl.org/dc/elements/1.1/"  
                        xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"> 
                        <!-- took this out because it causes problems - see https://jira.duraspace.org/browse/FCREPO-60 -->
                        <!-- xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                        xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd"> -->
                        <dc:title>Recording <xsl:value-of select="//Recording[1]/Name"/></dc:title>
                        <dc:identifier>
                            <xsl:text>rec:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/>_<xsl:value-of select="//Communication/@Name"/>                            
                        </dc:identifier>
                    </oai_dc:dc>
                </foxml:xmlContent>
            </foxml:datastreamVersion>
        </foxml:datastream>        
    </xsl:template>
    
    <!-- *****************************************************  -->
    
    <!-- the CMDI metadata stream for this object -->
    <xsl:template name="CMDI_STREAM">
        <foxml:datastream CONTROL_GROUP="X" ID="CMDI" STATE="A" VERSIONABLE="true">
            <foxml:datastreamVersion CREATED="2011-10-07T13:54:12.093Z" FORMAT_URI="http://www.clarin.eu/cmd" ID="CMDI1.1" LABEL="CMDI metadata for this object" MIMETYPE="text/xml">
                <foxml:xmlContent>
                    <CMD CMDVersion="1.1" 
                        xmlns="http://www.clarin.eu/cmd/">
                        <!-- xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
                            xsi:schemaLocation="http://www.clarin.eu/cmd/ http://catalog.clarin.eu/ds/ComponentRegistry/rest/registry/profiles/clarin.eu:cr1:p_1271859438204/xsd"> -->
                        <Header>
                            <MdCreator>*** TODO ***</MdCreator>
                            <MdCreationDate>*** TODO ***</MdCreationDate>
                            <MdSelfLink>*** TODO ***</MdSelfLink>
                            <MdProfile>*** TODO ***</MdProfile>
                            <MdCollectionDisplayName>*** TODO ***</MdCollectionDisplayName>
                        </Header>
                        <!-- Vorbild: NALIDA DiK-Reräsentation -->
                        <Resources>
                            <MediaFiles>
                                <MediaFile>
                                    <CatalogueLink><xsl:value-of select="//NSLink"/></CatalogueLink>
                                    <Type>audio</Type>
                                    <Format>Audio/WAV</Format>
                                </MediaFile>
                            </MediaFiles>
                      </Resources>      
                    </CMD>
                </foxml:xmlContent>
            </foxml:datastreamVersion>
        </foxml:datastream>        
    </xsl:template>
    
    <!-- *****************************************************  -->
    
    <xsl:template name="EXTERNAL_RELATIONS_STREAM">
        <foxml:datastream CONTROL_GROUP="X" ID="RELS-EXT" STATE="A" VERSIONABLE="true">                      
            <foxml:datastreamVersion CREATED="2011-10-10T07:36:03.569Z" FORMAT_URI="info:fedora/fedora-system:FedoraRELSExt-1.0" ID="RELS-EXT.1" LABEL="RDF Statements about this object" MIMETYPE="application/rdf+xml" SIZE="425">
                <foxml:xmlContent>
                    <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
                        <rdf:Description>
                            <!-- rdf:about="info:fedora/tra:hamatac_MT_270110_Shirin" -->
                            <xsl:attribute name="rdf:about">                                
                                <xsl:text>info:fedora/rec:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/>_<xsl:value-of select="//Communication/@Name"/>
                            </xsl:attribute>                            
                            <!-- information about the external relation 'hasModel' (= ContentModel for the transcription objects) -->
                            <hasModel rdf:resource="info:fedora/rec:cmodel" xmlns="info:fedora/fedora-system:def/model#"/>
                            <!-- information about the external relation 'isMemberOf' (=hierarchical subordination to a communication object -->
                            <isMemberOf xmlns="info:fedora/fedora-system:def/relations-external#">
                                <xsl:attribute name="rdf:resource"><xsl:text>info:fedora/col:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/>_rec</xsl:attribute>                                                            
                            </isMemberOf>
                            <isMemberOf xmlns="info:fedora/fedora-system:def/relations-external#">
                                <!-- rdf:resource="info:fedora/com:hamatac_MT_270110_Shirin" --> 
                                <xsl:attribute name="rdf:resource">                                
                                    <xsl:text>info:fedora/com:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/>_<xsl:value-of select="//Communication/@Name"/>
                                </xsl:attribute>                                                            
                            </isMemberOf>
                            
                            <!-- Auf  Geheiß von Daniel (Mail vom 16. Dezember 2011) hinzugefügt-->
                            <!-- Auf  Geheiß von Daniel (4. April 2012) entfernt-->
                            <!-- <isMemberOfCollection xmlns="info:fedora/fedora-system:def/relations-external#">
                                <xsl:attribute name="rdf:resource">
                                    <xsl:text>info:fedora/com:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/>_<xsl:value-of select="//Communication/@Name"/>
                                </xsl:attribute>                                                            
                            </isMemberOfCollection> -->                            
                            
                            
                        </rdf:Description>
                    </rdf:RDF>
                </foxml:xmlContent>
            </foxml:datastreamVersion>
        </foxml:datastream>       
    </xsl:template>
    
    <!-- *****************************************************  -->
    
    <xsl:template name="XACML_STREAM">
        <foxml:datastream ID="POLICY" STATE="A" CONTROL_GROUP="X" VERSIONABLE="true">
            <foxml:datastreamVersion>
                <xsl:attribute name="CREATED"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
                <xsl:attribute name="ID">POLICY.0</xsl:attribute>
                <xsl:attribute name="LABEL">xacml policy</xsl:attribute>
                <xsl:attribute name="MIMETYPE">text/xml</xsl:attribute>
                <foxml:xmlContent>
                    <Policy xmlns="urn:oasis:names:tc:xacml:1.0:policy" 
                        PolicyId="deny-apia-if-not-tomcat-role" RuleCombiningAlgId="urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable"> 
                        <!--  xsi:schemaLocation="urn:oasis:names:tc:xacml:1.0:policy http://www.fedora.info/definitions/1/0/api/cs-xacml-schema-policy-01.xsd"
                            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" >-->                        
                        <Description>
                            This policy will DENY access to ALL API-A methods to users who are NOT in the administrator or <xsl:value-of select="$CORPUS_NAMESPACE"/>_user ROLES.
                        </Description>
                        <Target>
                            <Subjects>
                                <AnySubject/>
                            </Subjects>
                            <Resources>
                                <AnyResource/>
                            </Resources>
                            <Actions>
                                <Action>
                                    <ActionMatch MatchId="urn:oasis:names:tc:xacml:1.0:function:string-equal">
                                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">urn:fedora:names:fedora:2.1:action:api-a</AttributeValue>
                                        <ActionAttributeDesignator AttributeId="urn:fedora:names:fedora:2.1:action:api" DataType="http://www.w3.org/2001/XMLSchema#string"/>
                                    </ActionMatch>
                                </Action>
                            </Actions>
                        </Target>
                        <Rule Effect="Deny" RuleId="1">
                            <Condition FunctionId="urn:oasis:names:tc:xacml:1.0:function:not">
                                <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-at-least-one-member-of">
                                    <SubjectAttributeDesignator AttributeId="fedoraRole" DataType="http://www.w3.org/2001/XMLSchema#string" MustBePresent="false"/>
                                    <Apply FunctionId="urn:oasis:names:tc:xacml:1.0:function:string-bag">
                                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string">administrator</AttributeValue>
                                        <AttributeValue DataType="http://www.w3.org/2001/XMLSchema#string"><xsl:value-of select="$CORPUS_NAMESPACE"/>_user</AttributeValue>
                                    </Apply>
                                </Apply>
                            </Condition>
                        </Rule>
                    </Policy>                    
                </foxml:xmlContent>
                <!--                <foxml:contentLocation TYPE="URL">
                    <xsl:attribute name="REF"><xsl:text>http://localhost:8080/fedora/get/col:</xsl:text><xsl:value-of select="$CORPUS_NAMESPACE"/><xsl:text>/POLICY</xsl:text></xsl:attribute>
                </foxml:contentLocation>                
-->            </foxml:datastreamVersion>
        </foxml:datastream>                
    </xsl:template>
    
    <!-- "Audit trail is a sequence of steps supported by proof documenting the real processing of a transaction flow through an organization, a process or a system. (Definition for an audit and/or IT environment).        
    An Audit log is a chronological sequence of audit records, each of which contains evidence directly pertaining to and resulting from the execution of a business process or system function." [Wikipedia] 
    We don't need this. It is automatically generated as a aprt of the ingest process -->    
   
    <!-- <xsl:template name="AUDIT_STREAM">
        <foxml:datastream CONTROL_GROUP="X" ID="AUDIT" STATE="A" VERSIONABLE="false">
            <foxml:datastreamVersion CREATED="2011-10-07T13:54:12.093Z" FORMAT_URI="info:fedora/fedora-system:format/xml.fedora.audit" ID="AUDIT.0" LABEL="Audit Trail for this object" MIMETYPE="text/xml">
                <foxml:xmlContent>
                    <audit:auditTrail xmlns:audit="info:fedora/fedora-system:def/audit#">
                        <audit:record ID="AUDREC1">
                            <audit:process type="Fedora API-M"/>
                            <audit:action>ingest</audit:action>
                            <audit:componentID/>
                            <audit:responsibility>fedoraAdmin</audit:responsibility>
                            <audit:date><xsl:value-of select="current-dateTime()"/></audit:date>
                            <audit:justification>Ingest via foxml automatically generated from COMA file</audit:justification>
                        </audit:record>
                    </audit:auditTrail>
                </foxml:xmlContent>
            </foxml:datastreamVersion>
        </foxml:datastream>        
    </xsl:template> -->
    
</xsl:stylesheet>
