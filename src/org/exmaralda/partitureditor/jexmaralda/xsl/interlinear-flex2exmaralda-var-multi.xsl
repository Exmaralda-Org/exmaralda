<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema" 
    xmlns:my="http://www.philol.msu.ru/~languedoc/xml" 
    xmlns:inel="https://inel.corpora.uni-hamburg.de/xmlns" 
    exclude-result-prefixes="#all"
    version="2.0">

    <xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
        
    <xsl:namespace-alias stylesheet-prefix="#default" result-prefix=""/>


    <!--
            This is the transform from SIL FLEx flextext format into EXMARaLDA exb, with configurable settings.
            See interlinear-eaf2exmaralda.xsl for conversion from ELAN.
            (c) Alexandre Arkhipov, 2015-2019.
            This transform is for a single file containing multiple texts. 
            Based on transform for Nganasan.
            Based on the batch version based on interlinear-eaf2exmaralda.xsl v1.11.
            
            v3.1: Changes by Daniel Jettka, 22.01.2019
                   - added preprocessing step (inel:append-punct) to merge punctuation into words already in flextext input
                   - added cleanup functionality for clitics
                   - consistency checks
                   - more tier formats
            v3.0: Changes by Daniel Jettka, 30.10.2018
                  cf. https://lab.multilingua.uni-hamburg.de/redmine/versions/75
                   - restructured XSLT
                   - settings now in global variables
                   - stylesheet modulation (this one is imported into more specific ones that can override global variables and templates)
                     this stylesheet can be used in two ways:
                     (a) it can be applied directly by using a separate settings file (path in param SETTINGS-FILE), whereas the settings file itself is an XSLT stylesheet that is the same like the one used in (b)
                     (b) a corpus-specific XSLT can imports this general stylesheet and override parameters, variables and templates with corpus-specific information
            v2.03: Time attributes (per phrase) are imported if there are any
            v2.02: Added sentence-numbering options (flat, para, both); moved all parameters to settings file; added couple of tier styles
            v2.01: Added lang attribute to tier-word template (23.11.2016)
            v2.00: Many parameters related to specific tiers etc. are now read from a settings file.
                   NB: the cleanup functions are NOT yet called according to the settings file, but controlled internally
            v1.21: ... is replaced with … and both are counted as a (punctuation) word. (27.02.2016)
            v1.20: Transform for single files.
    -->
    

    <!-- *************************** -->
    <!-- ***** START: KEYS ********* -->
    <xsl:key name="tli-id-by-time" match="tli/@id" use="../@time"/>
    <xsl:key name="tier-format-by-tierref" match="tier-format" use="@tierref"/>
    <!-- ***** END: KEYS ********** -->
    <!-- ************************** -->


    <!-- ******************************** -->
    <!-- *** START: GLOBAL PARAMETERS *** -->
    
    <xsl:param name="SETTINGS-FILE" as="xs:string" required="no"/>
    
    <!-- project-name: goes into meta-information -->
    <xsl:param name="PROJECTNAME" select="(replace($settings//xsl:param[@name='PROJECTNAME']/@select, '^''|''$', ''), '')[1]" as="xs:string"/>
    
    <!-- speaker-code position: speaker code is extracted from the text name; normally it begins the file name (set position to "1"); if there is a _ (underscore) before speaker code, set position to "2" -->
    <xsl:param name="SPEAKER-CODE-POSITION" select="(replace($settings//xsl:param[@name='SPEAKER-CODE-POSITION']/@select, '^''|''$', ''), 1)[1]" as="xs:integer"/>
    
    <!-- base language of text: infer the base language by most used txt-items' @lang -->
    <xsl:variable name="txt-langs" select="$preprocessed-root//item[@type='txt']/@lang" as="xs:string+"/>
    <xsl:param name="BASE-LANGUAGE" select="(replace($settings//xsl:param[@name='BASE-LANGUAGE']/@select, '^''|''$', ''), $txt-langs[index-of($txt-langs,.)[max(for $item in $txt-langs return count(index-of($txt-langs, $item)))]])[1]" as="xs:string"/>
    
    <!-- tier definition: XML representation of tiers -->
    <xsl:param name="TIER-DEFINITIONS" select="$settings//xsl:param[@name='TIER-DEFINITIONS']"/>
    
    <!-- timeline: 
         start:  offset before the first word (in seconds) 
         step:   duration for one word (in seconds)
    -->
    <!--<xsl:param name="TIMELINE-START" select="0.0" as="xs:decimal"/>
    <xsl:param name="TIMELINE-STEP" select="0.5" as="xs:decimal"/>-->
    <xsl:param name="TIMELINE-STEP" select="0.5" as="xs:decimal"/><!-- only used internally for now to identify distinct timeline items -->
    
    <!-- text-name: extracted from one of items under interlinear-text
         from-item: specify "title" or "abbreviation" 
         from-lang: specify writing system
    -->
    <xsl:param name="TEXT-NAME-FROM-ITEM" select="'title'" as="xs:string"/>
    <xsl:param name="TEXT-NAME-FROM-LANG" select="'en'" as="xs:string"/>
    
    <!-- sentence-number: set @format to "flat", "para", or "both"
         flat: number sentences consecutively throughout the text
         para: get sentence numbers from segnum items, expecting para.sent if dot is present
         both: flat numbering followed by para.sent numbering in brackets 
    -->
    <xsl:param name="SENTENCE-NUMBER-FORMAT" select="'both'" as="xs:string"/>
    
    <!-- if no reference to a TIERFORMAT-TABLE-FILE is given, then the definitions from $DEFAULT-TIERFORMAT-TABLE are taken -->
    <xsl:param name="TIERFORMAT-TABLE-FILE" as="xs:string?" required="no"/>
    
    <!-- **** END: GLOBAL PARAMETERS **** -->
    <!-- ******************************** -->
    
    
    <!-- ********************************* -->
    <!-- **** START: GLOBAL VARIABLES **** -->
    
    <xsl:variable name="settings" select="if(doc-available($SETTINGS-FILE)) then document($SETTINGS-FILE) else ()"/>

    <!-- constants -->
    <xsl:variable name="tiers-morph" select="'mb', 'mp'" as="xs:string+"/>
    <xsl:variable name="tiers-gloss" select="'ge', 'gr', 'mc', 'hn'" as="xs:string+"/>
    <xsl:variable name="clitic-separator" select="'='" as="xs:string+"/>
    <xsl:variable name="zero-morph-marker" select="'-^0'" as="xs:string+"/>
    <xsl:variable name="regex.ellipsis" select="'\(\((\.+|…)\)\)'" as="xs:string"/>
    <xsl:variable name="regex.quote" select="'[&quot;“”]'" as="xs:string"/>
    <xsl:variable name="regex.sentpunct" select="'[,!\.\?:;…]'" as="xs:string"/>
    <xsl:variable name="regex.dash" select="'[–\-—]'" as="xs:string"/>
    <xsl:variable name="speaker-from-text-name"
        select="(
        (:1:) 'SPK'[substring-before($filename, '_') = ''], 
        (:2:) substring-before(substring-after($filename, '_'), '_'),
        (:3:) substring-before($filename, '_')
        )[1]"/>
    
    <xsl:variable name="speakers" select="
        (: test if all phrases have speaker info :)
        if(empty(//phrase[empty(@speaker)])) 
        then distinct-values(//phrase/@speaker) 
        else 
            (: test if no phrase has speaker info :)
            if(empty(//phrase[exists(@speaker)])) 
            then distinct-values(//interlinear-text/@guid)
            else error((), concat('***ERROR: speaker info inconsistent for phrases (no speaker in: ', string-join(//phrase[empty(@speaker)]/inel:cleanup( string-join(./item[ @type = 'segnum'], ' || '), 'renum'), ' '), ').'))" as="xs:string+"/>
    
    <xsl:variable name="output-directory" select="replace(base-uri(), tokenize(substring(base-uri(), 6), '/')[last()], '')"/>
    <xsl:variable name="filename" select="replace(replace(base-uri(), '^.*/', ''), '.flextext', '')"/>
    <xsl:variable name="preprocessed-root">
        <!--<xsl:copy-of select="/"/>-->
        <xsl:for-each select="/">
            <xsl:apply-templates mode="preprocess-punctuation"/>
        </xsl:for-each>
    </xsl:variable>
    
    <!-- temporary building of tiers (events include explicit time in @start and @end) -->
    <xsl:variable name="flextext2exb.tiers">
        <!-- Here go the tiers. The output order is fixed. -->
        <xsl:for-each select="$speakers">
            <xsl:variable name="current-speaker" select="." as="xs:string"/>
            <xsl:for-each select="$TIER-DEFINITIONS//tier">
                <xsl:call-template name="tier">
                    <xsl:with-param name="speaker" select="$current-speaker" as="xs:string"/>
                </xsl:call-template>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:variable>
    
    <!-- build the timeline on the basis of actually existing times in events -->
    <xsl:variable name="timeline">
        <xsl:for-each select="distinct-values(($flextext2exb.tiers//event/@start, $flextext2exb.tiers//event/@end))">
            <xsl:sort select="." data-type="number"/>
            <tli id="T{position()}" type="appl">
                <!--<xsl:if test="$use-flex-times">
                    <xsl:attribute name="time" select="."/>
                </xsl:if>-->
                <xsl:attribute name="time" select="."/>
            </tli>                            
        </xsl:for-each>
    </xsl:variable>
            
    <!-- v2.03: checks whether time attributes are present -->
    <xsl:variable name="use-flex-times" as="xs:boolean" select="        
        if(
            (: test if for all phrases time info can be retrieved :)
            every $p-times 
            in //phrase/string-join((((@begin-time-offset, preceding-sibling::phrase[1]/@end-time-offset)[not(matches(., '^\s*$'))])[1], ((@end-time-offset, following-sibling::phrase[1]/@begin-time-offset)[not(matches(., '^\s*$'))])[1]), '#') 
            satisfies (matches($p-times, '^[0-9]+#[0-9]+$') and not(xs:integer(tokenize($p-times, '#')[1]) &gt;= xs:integer(tokenize($p-times, '#')[2])))
        )
        then true()
        else 
            (: test if no time info exists :)
            if(
                (: test if for no phrase time info can be retrieved :)
                every $p-times
                in //phrase/string-join((((@begin-time-offset, preceding-sibling::phrase[1]/@end-time-offset)[not(matches(., '^\s*$'))])[1], ((@end-time-offset, following-sibling::phrase[1]/@begin-time-offset)[not(matches(., '^\s*$'))])[1]), '#') 
                satisfies not(matches($p-times, '^[0-9]+#[0-9]+$'))
            )
            then false()
            else error((), concat('***ERROR: time info inconsistent for phrases (no time in: ', string-join(//phrase[empty((@begin-time-offset, @end-time-offset)[not(matches(., '^\s*$'))])]/inel:cleanup( string-join(./item[ @type = 'segnum'], ' || '), 'renum'), ' '), ').'))"/>


    <!-- find a format table supplied in (a) path to document, (b) settings file, (c) take default from here -->
    <xsl:variable name="TIERFORMAT-TABLE">
        <xsl:choose>
            <xsl:when test="doc-available($TIERFORMAT-TABLE-FILE)">
                <xsl:copy-of select="document($TIERFORMAT-TABLE-FILE)//*:tierformat-table"/>
            </xsl:when>
            <xsl:when test="exists($settings//xsl:param[@name='TIER-FORMAT-TABLE']//*:tierformat-table)">
                <xsl:copy-of select="$settings//xsl:param[@name='TIER-FORMAT-TABLE']//*:tierformat-table"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$DEFAULT-TIERFORMAT-TABLE//*:tierformat-table"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="DEFAULT-TIERFORMAT-TABLE">
        <tierformat-table>
            <timeline-item-format show-every-nth-numbering="1" show-every-nth-absolute="1"
                absolute-time-format="time" miliseconds-digits="1"/>
            <tier-format tierref="ref">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="st">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="stl">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ts">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#R00G99B33</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="tx">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#R00G00B99</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mb">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mp">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="gr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="gg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ge">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="go">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="mc">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ps">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="hn">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="SeR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="SyF">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="IST">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="Top">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="Foc">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Typ">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Phon">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="BOR-Morph">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="CS">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            
            <tier-format tierref="#">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fe">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#RccG00B00</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fr_ed">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">#RccG00B00</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="fo">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ltr">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="lto">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="ltg">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="src">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="srl">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">red</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="nt">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="nto">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">brown</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="EMPTY">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">white</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">2</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="ROW-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Bold</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Times New Roman</property>
            </tier-format>
            <tier-format tierref="SUB-ROW-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Right</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">8</property>
                <property name="font-name">Times New Roman</property>
            </tier-format>
            <tier-format tierref="EMPTY-EDITOR">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">white</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">lightGray</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">2</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="COLUMN-LABEL">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">blue</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis</property>
            </tier-format>
            <tier-format tierref="TIE0">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE4">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE3">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE2">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
            <tier-format tierref="TIE1">
                <property name="row-height-calculation">Generous</property>
                <property name="fixed-row-height">10</property>
                <property name="font-face">Plain</property>
                <property name="font-color">black</property>
                <property name="chunk-border-style">solid</property>
                <property name="bg-color">white</property>
                <property name="text-alignment">Left</property>
                <property name="chunk-border-color">#R00G00B00</property>
                <property name="chunk-border"/>
                <property name="font-size">12</property>
                <property name="font-name">Charis SIL</property>
            </tier-format>
        </tierformat-table>
    </xsl:variable>
    
    
    <!-- **** END: GLOBAL VARIABLES ****** -->
    <!-- ********************************* -->



    <!-- **************************** -->
    <!-- ***** START: TEMPLATES ***** -->

    <xsl:template match="/">
        
        <!-- some consistency checks -->
        <xsl:for-each select="//phrase[not(concat(@begin-time-offset, '#', @end-time-offset) = '#') and concat(@begin-time-offset, '#', @end-time-offset, '#', @speaker) = following-sibling::phrase/concat(@begin-time-offset, '#', @end-time-offset, '#', @speaker)]">
            <xsl:copy-of select="error((), concat('***ERROR: time info inconsistent; more than one phrase with time ', @begin-time-offset, '-', @end-time-offset))"/>
        </xsl:for-each>
        <!-- multiple interlinear text in one file not supported in this version -->
        <xsl:if test="//interlinear-text[2]">    
            <xsl:copy-of select="error((), '***ERROR: multiple interlinear-text elements in one file not supported - please split into multiple files.')"/>
        </xsl:if>
        
        <xsl:message select="('Time information retrieved from flextext.'[$use-flex-times], 'Time information generated by interpolation.'[not($use-flex-times)])[1]"/>
        <xsl:message select="$preprocessed-root"></xsl:message>
        <conversion-report filename="{$filename}" output-location="{concat($output-directory, $filename, '.exb')}" settings-file="{($SETTINGS-FILE[doc-available($SETTINGS-FILE)], error((),concat('***ERROR: settings file not found: ', $SETTINGS-FILE)))[1]}">
            <!--<conversion-settings filename="{$settings-file}">
                <xsl:copy-of select="$settings/*/*"/>
            </conversion-settings>-->
            <interlinear-text name="{$filename}"/>
            <xsl:result-document method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no" href="{$output-directory}{$filename}.exb">
                <basic-transcription>
                    <head>
                        <meta-information>
                            <project-name>
                                <xsl:value-of select="$PROJECTNAME"/>
                            </project-name>
                            <transcription-name>
                                <xsl:value-of select="$filename"/>
                            </transcription-name>
                            <referenced-file url="{$filename}.wav"/>
                            <ud-meta-information/>
                            <comment/>
                            <transcription-convention/>
                        </meta-information>
                        <speakertable>
                            <xsl:for-each select="$speakers">
                                <speaker id="{.}">
                                    <abbreviation>
                                        <xsl:value-of select="."/>
                                    </abbreviation>
                                    <sex value="u"/>
                                    <languages-used/>
                                    <l1/>
                                    <l2/>
                                    <ud-speaker-information/>
                                    <comment/>
                                </speaker>
                            </xsl:for-each>
                        </speakertable>
                    </head>
                    <basic-body>
                        
                        <!-- TIMELINE -->
                        <common-timeline>
                            <xsl:for-each select="$timeline//tli">
                                <xsl:copy>
                                    <xsl:copy-of select="@id, @type, @time[$use-flex-times]"/>
                                </xsl:copy>
                            </xsl:for-each>                     
                        </common-timeline>
                        
                        <!-- TIERS -->
                        <!-- replace time info in event/@start and event/@end with tli reference -->
                        <xsl:for-each select="$flextext2exb.tiers//tier">
                            <tier>
                                <xsl:copy-of select="@*"/>
                                <xsl:for-each select="event">
                                    <event start="{key('tli-id-by-time', @start, $timeline)}" end="{key('tli-id-by-time', @end, $timeline)}">
                                    <xsl:value-of select="."/>
                                </event>
                                </xsl:for-each>
                            </tier>
                        </xsl:for-each>
                        <!--<xsl:copy-of select="$flextext2exb.tiers"/> -->                   
                    </basic-body>
                    
                    <!-- insert the tierformat-table (dynamically created formatting template) -->
                    <xsl:call-template name="set-format-table">
                        <xsl:with-param name="tiers-to-format" select="$flextext2exb.tiers//tier"/>
                    </xsl:call-template>
                    
                    
                    
                </basic-transcription>
            </xsl:result-document>
            
        </conversion-report>
    </xsl:template>
    
    
    <xsl:template name="tier">
        <xsl:param name="speaker" select="'SPK'"/>
        
        <!-- info from tier definitions -->        
        <xsl:variable name="template" select="@template" as="xs:string"/>
        <xsl:variable name="cat" select="@name" as="xs:string"/>
        <xsl:variable name="itemtype" select="(@itemtype, 'txt')[1]" as="xs:string"/>
        <xsl:variable name="lang" select="(@lang, $BASE-LANGUAGE)[1]" as="xs:string"/>
        <xsl:variable name="prefix" select="concat($filename, '.', concat($speaker, '.')[exists($speakers[2])])" as="xs:string"/>
        <xsl:variable name="sep" select="(@sep, '')[1]" as="xs:string"/>
        <xsl:variable name="cleanup-routines" select="tokenize(@cleanup, '\s*,\s*')" as="xs:string*"/>
        <xsl:variable name="type" select="@type" as="xs:string"/>
        
        <xsl:variable name="id" select="concat($cat, concat('-', $speaker)[exists($speakers[2])])" as="xs:string"/>
        <tier id="{$id}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$id}">
            
            <xsl:variable name="phrases" select="$preprocessed-root//phrase[ if(exists(@speaker)) then (@speaker = $speaker) else true() ]" as="element()*"/>
            <xsl:for-each select="1 to count($phrases)">                
                <xsl:variable name="pos" select="." as="xs:integer"/>
                
                <xsl:for-each select="$phrases[$pos]">
                    
                    <!-- get words (disregard §) -->
                    <xsl:variable name="descendant-words" select="$phrases[$pos]/descendant::word[not(matches(item[1], '^§$'))]" as="element()*"/>
                    <xsl:variable name="preceding-words" select="$phrases[position() &lt; $pos]/descendant::word[not(matches(item[1], '^§$'))]" as="element()*"/>
                    
                    <!-- get time span for phrase -->
                    <!--<xsl:variable name="phrase-start-time" select="if($use-flex-times) then ((@begin-time-offset, preceding-sibling::phrase[1]/@end-time-offset)[1] div 1000) else ($TIMELINE-START + (count($preceding-words) * $TIMELINE-STEP))"/>
                    <xsl:variable name="phrase-end-time" select="if($use-flex-times) then ((@end-time-offset, following-sibling::phrase[1]/@begin-time-offset)[1] div 1000) else ($TIMELINE-START + ((count($preceding-words) + count($descendant-words)) * $TIMELINE-STEP))"/>-->
                    
                    <xsl:variable name="phrase-start-time" select="if($use-flex-times) then ((@begin-time-offset, $phrases[$pos - 1]/@end-time-offset)[1] div 1000) else (count($preceding-words) * $TIMELINE-STEP)"/>
                    <xsl:variable name="phrase-end-time" select="if($use-flex-times) then ((@end-time-offset, $phrases[$pos + 1]/@begin-time-offset)[1] div 1000) else ((count($preceding-words) + count($descendant-words)) * $TIMELINE-STEP)"/>
                    
                    
                    <xsl:choose>
                        
                        <!-- tier-sent -->
                        <xsl:when test="$template = 'tier-sent'">
                            <!-- for ref tier, output the filename dot (paragraph number dot) sentence number -->
                            <!-- v2.02: if format is 'flat' then just count the sentences through the whole text -->
                            <xsl:variable name="text" as="xs:string?" select="concat(
                                $prefix                                                                                                     [$cat = 'ref'], 
                                format-number($pos, '#000')                                                                           [$cat = 'ref' and $SENTENCE-NUMBER-FORMAT = ('both', 'flat')], 
                                ' ('                                                                                                        [$cat = 'ref' and $SENTENCE-NUMBER-FORMAT = 'both'], 
                                inel:cleanup( string-join(./item[ @type = $itemtype and ($lang = ('', @lang))], ' || '), $cleanup-routines) [(not($cat='ref') or $SENTENCE-NUMBER-FORMAT = ('para', 'both')) and not(.='')],
                                ')'                                                                                                         [$cat = 'ref' and $SENTENCE-NUMBER-FORMAT = 'both']
                             )"/>
                            <xsl:if test="not($text = '')">                            
                                <!-- version that writes times in @start and @end -->
                                <event start="{$phrase-start-time}" end="{$phrase-end-time}"><xsl:value-of select="$text"/></event>
                            </xsl:if>
                        </xsl:when>
                        
                        <!-- tier-sent-join- -->
                        <xsl:when test="$template = 'tier-sent-join'">                        
                            <!-- version that writes times in @start and @end -->
                            <!--<event start="{$phrase-start-time}" end="{$phrase-end-time}"><xsl:value-of select="inel:words-with-whitespaces( $descendant-words, $cleanup-routines, true())"/></event>-->
                            
                            <event start="{$phrase-start-time}" end="{$phrase-end-time}"><xsl:value-of select="string-join($descendant-words/item[1], '')"/></event>
                        </xsl:when>
                        
                        <!-- 'tier-tx' -->
                        <xsl:when test="$template = ('tier-tx', 'tier-word', 'tier-morph')">
                                                    
                            <!-- reference sequence for creating events -->
                            <!--<xsl:variable name="word-sequence" select="inel:words-with-whitespaces($descendant-words, $cleanup-routines, false())" as="xs:string*"/>-->
                            <xsl:variable name="word-sequence" select="inel:words-with-whitespaces($descendant-words, $cleanup-routines, false())" as="xs:string*"/>
                            
                            <!-- sequence of values that finally will go into events' text values (e.g. words themselves for tier-tx or morphemes for other tiers) -->
                            <xsl:variable name="result-value-sequence" as="xs:string*">
                                <xsl:choose>
                                    <xsl:when test="$template='tier-tx'">
                                        <xsl:copy-of select="$word-sequence"/>
                                    </xsl:when>
                                    <!-- test if zero-morph should be encoded -->
                                    <xsl:when test="$cat='mc' and descendant::morph[last()]/item[@type='txt'] = $zero-morph-marker">
                                        <xsl:copy-of select="$descendant-words/inel:cleanup(
                                            inel:multi-replace(
                                                string-join(descendant::morph/item[@type = $itemtype and @lang = $lang], $sep), 
                                                (for $x in $zero-morph-marker return concat(inel:string-to-regex-pattern($x), '$')),
                                                (for $x in $zero-morph-marker return '.[$1]')
                                            ),
                                            $cleanup-routines)"/>
                                    </xsl:when>
                                    <xsl:when test="$cat = 'ps'">
                                        <xsl:copy-of select="$descendant-words/inel:cleanup(
                                            string-join(item[@type = $itemtype and @lang = $lang], $sep),
                                            $cleanup-routines)"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:copy-of select="$descendant-words/inel:cleanup(
                                            string-join(descendant::morph/item[@type = $itemtype and @lang = $lang], $sep),
                                            $cleanup-routines)"/>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:variable>
                            
                            <!-- iterate and return events -->
                            <xsl:copy-of select="inel:words2events($word-sequence, $result-value-sequence, 1, $phrase-start-time, $phrase-end-time, ())"/>
                            
                        </xsl:when>
                        
                        <!-- 'tier-word-new' -->
                        <xsl:when test="$template = 'tier-word-new'"/><!-- no content for new tiers-->
                        
                    </xsl:choose>                
                </xsl:for-each>
                
            </xsl:for-each>
        </tier>
    </xsl:template>
    
    
    
    
    
    <!-- *** preprocessing flextext for a clean base (above all: merge punct into words) *** -->
    <!-- copy default -->
    <xsl:template match="*" mode="preprocess-punctuation">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="preprocess-punctuation"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- ignore punctuation, except ((...)) -->
    <xsl:template match="word[inel:treat-as-puntuation(item[1])]" mode="preprocess-punctuation"/>
    
    <!-- merge punctuation into "real" words -->
    <xsl:template match="word" mode="preprocess-punctuation">
        <!-- do not consider leading dash (see XPath predicate) 
        <xsl:variable name="preceding-texts" select="preceding-sibling::word[not(position() = 1 and matches(item[1], '^[\-–]$'))]/item[1]" as="xs:string*"/>-->
        <!-- also consider leading dash because undecidable if it marks speaker change or direct speech -->
        <xsl:variable name="preceding-texts-in-phrase" select="preceding-sibling::word/item[1]" as="xs:string*"/>
        <xsl:variable name="following-texts-in-phrase" select="following-sibling::word/item[1]" as="xs:string*"/>
        <xsl:variable name="preceding-texts-of-speaker" select="preceding::phrase[@speaker=current()/ancestor::phrase/@speaker]/descendant::word/item[1]" as="xs:string*"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:for-each select="*[position() = 1]">
                <!-- this is always item -->
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <!--<xsl:message select="."></xsl:message>
                    <xsl:message select="$preceding-texts-in-phrase"></xsl:message>
                    <xsl:message select="$following-texts-in-phrase"></xsl:message>
                    <xsl:message select="$preceding-texts-of-speaker"></xsl:message>-->
                    <xsl:value-of select="inel:append-punct($preceding-texts-in-phrase, $following-texts-in-phrase, ., $preceding-texts-of-speaker)"/>
                </xsl:copy>
            </xsl:for-each>
            <xsl:for-each select="*[position() &gt; 1]">
                <!-- this is the rest of the word -->
                <xsl:apply-templates mode="preprocess-punctuation"/>
                <!--<xsl:copy-of select="."/>-->
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>  
            
    <!-- prepare items for stems which are not first stem of a word -->
    <xsl:template match="morph[@type='stem' and exists(preceding-sibling::morph[@type='stem'])]/item[@type=('txt', 'cf')]" mode="preprocess-punctuation">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="concat('-', text())"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- prepare items in morphs with clitics -->
    <xsl:template match="morph[exists(item[matches(., concat('^[', string-join($clitic-separator, ''), ']'))])]" mode="preprocess-punctuation">
        <xsl:variable name="current-separator" select="substring(item[1], 1, 1)" as="xs:string"/>
        <!-- test if use of separator is not consistent -->
        <!--<xsl:if test="some $item in item[position()!=last()] satisfies not(matches($item, concat('^[', $current-separator, ']')))">
            <xsl:copy-of select="error((), concat('***ERROR: inconsistent use of clitics separator: ', .))"/>
        </xsl:if>-->
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:for-each select="item">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:value-of select="concat(
                        (: insert the separator if not already present:) $current-separator[not(substring(current(), 1, 1) = $current-separator)], 
                        (: return item value if unequals separator only :) text()[not(matches(., concat('^', $current-separator, '$')))] 
                        )"/>
                </xsl:copy>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    
    <!-- prepare item from mc tier with brackets if they are present in gloss tiers -->
    <xsl:template match="item[@type='msa' and matches(string-join(preceding-sibling::item[@type=('gls')], ''), '^(\[[^\]]*\])+$') and not(matches(text(), '^\[[^\]]*\]$'))]" mode="preprocess-punctuation">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:value-of select="concat('[', text(), ']')"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- copy pos item -->
    <xsl:template match="text()[parent::item[@type='pos']]" mode="preprocess-punctuation">
        <xsl:copy-of select="parent::item"/>
    </xsl:template>
    
    <xsl:template match="text()" mode="preprocess-punctuation">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    
    
    <!-- insert the tierformat-table (copied a formatting template) -->
    <xsl:template name="set-format-table">
        <xsl:param name="tiers-to-format" as="element()*"/>
        <xsl:choose>
            <xsl:when test="exists($TIERFORMAT-TABLE//*:tier-format)">
                <tierformat-table>
                    <timeline-item-format><xsl:copy-of select="$TIERFORMAT-TABLE//*:timeline-item-format/@*"/></timeline-item-format>
                    <xsl:for-each select="$tiers-to-format">
                        <xsl:variable name="tier-id" select="@id" as="xs:string"/>
                        <xsl:variable name="TIERFORMAT" select="key('tier-format-by-tierref', @category, $TIERFORMAT-TABLE)" as="element()*"/>
                        <xsl:choose>
                            <xsl:when test="$TIERFORMAT[2]">
                                <xsl:message select="concat('***WARNING: two tierformats found for tier with id ''', $tier-id, ''' - chose first one.')" terminate="no"/>
                            </xsl:when>
                            <xsl:when test="empty($TIERFORMAT)">
                                <xsl:message select="concat('***WARNING: no tierformat found for tier with id ''', $tier-id, ''' - ignored.')" terminate="no"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <tier-format tierref="{$tier-id}">
                                    <xsl:for-each select="$TIERFORMAT//*:property">
                                        <property><xsl:copy-of select="@*,text()"/></property>
                                    </xsl:for-each>
                                </tier-format>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
                </tierformat-table>       
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="concat('***ERROR: no tierformat-table found (supplied file: ''', $TIERFORMAT-TABLE-FILE, ''').')"/>
            </xsl:otherwise>
        </xsl:choose>        
    </xsl:template>     
    

    <!-- ***** END: TEMPLATES ******* -->
    <!-- **************************** -->


    
    <!-- **************************** -->
    <!-- ***** START: FUNCTIONS ***** -->
    
    <!-- append preceding and following punctuation to word -->
    <xsl:function name="inel:append-punct">
        <xsl:param name="preceding-texts" as="xs:string*"/>
        <xsl:param name="following-texts" as="xs:string*"/>
        <xsl:param name="result" as="xs:string?"/>
        <xsl:param name="all-preceding-texts" as="xs:string*"/>
        <xsl:choose>
            <!-- most of the rules need positive and negative conditions to allow or suppress appending -->
            
            <!-- ignore § -->
            <xsl:when test="matches($following-texts[1], '§')">                
                <xsl:value-of select="inel:append-punct($preceding-texts, (replace($following-texts[1], '§', '')[not(matches(., '^$'))], subsequence($following-texts, 2)), $result, $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], '§')">                
                <xsl:value-of select="inel:append-punct(($preceding-texts[position() != last()], replace($preceding-texts[last()], '§', '')[not(matches(., '^$'))]), $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- clause punctuation (with optional dash before the punctuation) -->
            <xsl:when test="matches($following-texts[1], concat('^', $regex.dash, '?', $regex.sentpunct, '+$'))">                
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], concat('^', $regex.dash, '?', $regex.sentpunct, '+$'))">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- keep leading dash (because undecidable if it marks speaker change or direct speech) -->
            <xsl:when test="matches($preceding-texts[last()], concat('^', $regex.sentpunct, '?', $regex.dash, '$')) and (count($preceding-texts) = 1)">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, concat($preceding-texts[last()], ' ', $result), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], concat('^', $regex.sentpunct, '?', $regex.dash, '$')) and not(count($preceding-texts) = 1)"><!-- ignore dash in preceding word if in middle of sentence -->
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($following-texts[1], concat('^', $regex.sentpunct, '?', $regex.dash, '$'))">
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, ' ', $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- append slash to previous word -->            
            <xsl:when test="matches($following-texts[1], '^/')">                
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], '^/')">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- ** quotation marks ** -->
            <!-- quotation mark as only one in this phrase -->
            <xsl:when test="matches($following-texts[1], concat('^', $regex.quote, '$')) 
                and count(tokenize(string-join(subsequence($following-texts, 2), ''), $regex.quote)) = 1
                and count(tokenize(string-join($preceding-texts, ''), $regex.quote)) = 1">
                <xsl:choose>
                    <!-- if in previous text there is an even number of quotes, then this must be an opening one which is not appended -->
                    <xsl:when test="inel:pattern-occurs-pairwise(string-join(($all-preceding-texts, $preceding-texts), ''), $regex.quote)">
                        <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), $result, $all-preceding-texts)"/>
                    </xsl:when>
                    <xsl:otherwise>                        
                        <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, $following-texts[1]), $all-preceding-texts)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[1], concat('^', $regex.quote, '$')) 
                and count(tokenize(string-join($following-texts, ''), $regex.quote)) = 1
                and count(tokenize(string-join($preceding-texts[position()!=last()], ''), $regex.quote)) = 1">
                <xsl:choose>
                    <!-- if in previous text there is an even number of quotes, then this must be an opening one prepended -->
                    <xsl:when test="inel:pattern-occurs-pairwise(string-join(($all-preceding-texts, $preceding-texts[position()!=last()]), ''), $regex.quote)">
                        <xsl:value-of select="inel:append-punct($preceding-texts[position()!=last()], $following-texts, concat($preceding-texts[last()],$result), $all-preceding-texts)"/>
                    </xsl:when>
                    <xsl:otherwise>                        
                        <xsl:value-of select="inel:append-punct($preceding-texts[position()!=last()], $following-texts, $result, $all-preceding-texts)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            
            <!-- quotes: special case 1 -->
            <xsl:when test="matches($following-texts[1], concat($regex.quote, $regex.sentpunct, '+'))">
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], concat($regex.quote, $regex.sentpunct, '+'))">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            <!-- quotes: special case 2 -->
            <xsl:when test="matches($following-texts[1], concat($regex.sentpunct, '+', $regex.quote))">
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result,$following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="matches($preceding-texts[last()], concat($regex.sentpunct, '+', $regex.quote))">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- quotation mark in preceding: closing -> disregard -->
            <xsl:when test="matches($preceding-texts[last()], '[^&quot;“”]*[&quot;“”][^&quot;“”]*') and 
                (:if number of &quot; is even in preceding text incl. this one, this must be an closing quote :)
                inel:pattern-occurs-pairwise(string-join($preceding-texts, ''), $regex.quote)">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            <!-- quotation mark in preceding: opening -> prepend to word -->
            <xsl:when test="matches($preceding-texts[last()], '[&quot;“”]*[&quot;“”][^&quot;“”]*') and 
                (:if number of &quot; is odd in preceding text incl. this one, then this must be an opening quote :)
                not( inel:pattern-occurs-pairwise(string-join($preceding-texts, ''), $regex.quote) )">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, concat($preceding-texts[last()], $result), $all-preceding-texts)"/>
            </xsl:when>
            <!-- quotation mark in following: closing -> append to word -->
            <xsl:when test="matches($following-texts[1], '[^&quot;“”]*[&quot;“”][^&quot;“”]*') and 
                (:if number of &quot; is even in following text excl. this one, then this must be a closing one:)
                inel:pattern-occurs-pairwise(string-join(subsequence($following-texts, 2), ''), $regex.quote)">
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), concat($result, $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <!-- quotation mark in following: opening -> disregard -->
            <xsl:when test="matches($following-texts[1], '[^&quot;“”]*[&quot;“”][^&quot;“”]*') and 
                (:if number of &quot; is odd in following text excl. this one, then this must be an opening one :)
                not( inel:pattern-occurs-pairwise(string-join(subsequence($following-texts, 2), ''), $regex.quote) )">
                <xsl:value-of select="inel:append-punct($preceding-texts, subsequence($following-texts, 2), $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- ** brackets ** -->
            <!-- opening brackets -->
            <xsl:when test="(matches($preceding-texts[last()], '[^\(]*\([^\(]*$') or matches($preceding-texts[last()], '[^\[]*\[[^\[]*$')) and not(matches($preceding-texts[last()], concat('^', $regex.ellipsis, '$')))">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, concat($preceding-texts[last()], $result), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="(matches($following-texts[1], '[^\(]*\([^\(]*$') or matches($following-texts[1], '[^\[]*\[[^\[]*$')) and not(matches($preceding-texts[last()], concat('^', $regex.ellipsis, '$')))">
                <xsl:value-of select="inel:append-punct($preceding-texts, $following-texts[position() != 1], $result, $all-preceding-texts)"/>
            </xsl:when>
            <!-- closing brackets -->
            <xsl:when test="(matches($following-texts[1], '[^\)]*\)[^\)]*$') or matches($following-texts[1], '[^\]]*\][^\]]*$')) and not(matches($following-texts[1], concat('^', $regex.ellipsis, '$')))">
                <xsl:value-of select="inel:append-punct($preceding-texts, $following-texts[position() != 1], concat($result, $following-texts[1]), $all-preceding-texts)"/>
            </xsl:when>
            <xsl:when test="(matches($preceding-texts[last()], '[^\)]*\)[^\)]*$') or matches($preceding-texts[last()], '[^\]]*\][^\]]*$')) and not(matches($following-texts[1], concat('^', $regex.ellipsis, '$')))">
                <xsl:value-of select="inel:append-punct($preceding-texts[position() != last()], $following-texts, $result, $all-preceding-texts)"/>
            </xsl:when>
            
            <!-- no punctuation left in preceding or following sequence of strings -->
            <xsl:when test="not(inel:treat-as-puntuation($preceding-texts[last()]) or inel:treat-as-puntuation($following-texts[1]))">
                <xsl:value-of select="concat($result, ' ')"/>
            </xsl:when>
            <!-- if there is more puntuation that could not be appended -->
            <xsl:otherwise>
                <!--<xsl:message select="'inel:treat-as-puntuation(', $preceding-texts[last()], ') = ', inel:treat-as-puntuation($preceding-texts[last()])"/>
                <xsl:message select="'inel:treat-as-puntuation(', $following-texts[1], ') = ', inel:treat-as-puntuation($following-texts[1])"></xsl:message>-->
                <xsl:copy-of select="error((), concat('***Error: Could not append to ''', $result, ''': preceding ''', $preceding-texts[last()], ''' or following ''', $following-texts[1], '''.'))"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="inel:isPunctuation" as="xs:boolean">
        <xsl:param name="item" as="element()"/>
        <xsl:value-of select="($item/@type='punct') and ($item/@lang=$BASE-LANGUAGE)"/>
    </xsl:function>
    
    <xsl:function name="inel:treat-as-puntuation" as="xs:boolean">
        <xsl:param name="str" as="xs:string?"/>
        <xsl:copy-of select="matches($str, '^[^\w]|[^\w\-]$') and 
            not(matches($str, concat('^', $regex.ellipsis, '(', $regex.sentpunct, '+)*', '$'))) and 
            not(matches($str, concat('^', $regex.dash, '\w+$')))"/>
    </xsl:function>
    
    <xsl:function name="inel:pattern-occurs-pairwise" as="xs:boolean">
        <xsl:param name="text" as="xs:string?"/>
        <xsl:param name="pattern" as="xs:string"/>
        <xsl:variable name="occurences" select="count(tokenize($text, $pattern)) - 1" as="xs:integer"/>
        <xsl:copy-of select="($occurences div 2) = round-half-to-even(($occurences div 2), 0)"/>
    </xsl:function>
    
    
    <xsl:function name="inel:multi-replace" as="xs:string?">
        <xsl:param name="text" as="xs:string?"/>
        <xsl:param name="replacee" as="xs:string*"/>
        <xsl:param name="replacer" as="xs:string*"/>
        <xsl:value-of select="if(exists($replacee[1])) then inel:multi-replace(replace($text, $replacee[1], $replacer[1]), subsequence($replacee, 2), subsequence($replacer, 2)) else $text"/>
    </xsl:function>
    
    
    <xsl:function name="inel:cleanup" as="xs:string?">
        <xsl:param name="text" as="xs:string?"/>
        <xsl:param name="routines" as="xs:string*"/>
        <xsl:choose>
            <xsl:when test="$routines[1]">
                <xsl:variable name="new-text">
                    <xsl:choose>
                        
                        <!-- cleanup brackets -->
                        <xsl:when test="$routines[1] = 'brackets'">
                            <xsl:value-of select="inel:multi-replace( 
                                $text, 
                                ('\[\[', '\]\]', '\.\.\.', '///',     '\?\?\?'), 
                                ('((',   '))',   '…',     '((XXX))', '((unknown))')
                                )"/>                            
                        </xsl:when>
                        
                        <!-- cleanup tx -->
                        <xsl:when test="$routines[1] = 'tx'">                            
                            <xsl:value-of select="inel:multi-replace( 
                                $text, 
                                ('[ˀ]', '[03]([\.,!\?;:]*)$'), 
                                ('',           '$1') 
                                )"/>
                            <!-- formerly stripped all trailing spaces ( +$), but not anymore because of preprocessing -->
                            <!-- strip final 0 and 3 -->
                            <!-- strip "deep consonant" and "deep glottal stop" -->
                        </xsl:when>
                        
                        <!-- cleanup morph -->
                        <xsl:when test="$routines[1] = 'morph'">
                            <xsl:value-of select="inel:multi-replace(
                                    $text, 
                                    ( for $x in $zero-morph-marker return inel:string-to-regex-pattern($x) ), 
                                    ( for $x in $zero-morph-marker return '' )
                                )"/>
                            <!-- strip zero morph markers, e.g. -^0 -->
                        </xsl:when>
                        
                        <!-- cleanup mc-tier -->
                        <xsl:when test="$routines[1] = 'mc-tier'">
                            <xsl:value-of select="inel:multi-replace($text, ('&lt;\s*Not\s*Sure\s*&gt;', 'Attaches\s*to\s*any\s*category', '-(\[.+?\])'), ('%%', 'any', '.$1'))"/>
                            <!-- replace some codes -->
                            <!-- replace -[] with .[] -->
                        </xsl:when>
                        
                        <!-- cleanup gloss -->
                        <xsl:when test="$routines[1] = 'gloss'">
                            <xsl:value-of select="inel:multi-replace($text, ('-(\[.+?\])'), ('.$1'))"/>
                            <!-- replace -[] with .[] -->
                            <!-- removed this step because of pre-processing: "erase spaces" -->
                        </xsl:when>
                        
                        <!-- cleanup clitics -->
                        <xsl:when test="$routines[1] = 'clitics'">
                            <xsl:value-of select="inel:multi-replace(
                                $text, 
                                (for $x in $clitic-separator return concat('\-(', $x, ')')), 
                                (for $x in $clitic-separator return '$1')
                                )"/>
                            <!-- remove - before any clitics separator -->
                        </xsl:when>
                        
                        <!-- renumbering -->
                        <xsl:when test="$routines[1] = 'renum'">
                            <xsl:choose>
                                <xsl:when test="not(substring-after($text, '.') = '')">
                                    <xsl:value-of select="concat(format-number(number(substring-before($text, '.')), '#000'), '.', format-number(number(substring-after($text, '.')), '#000'))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="format-number(number($text), '#000')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                            <!-- take "para.sent" number from segnum, extract second part, -->
                            <!-- convert to a number and pad with zeroes to 3 digits -->
                        </xsl:when>
                                                         
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:value-of select="inel:cleanup( $new-text, subsequence($routines, 2) )"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    
    <xsl:function name="inel:words-with-whitespaces" as="xs:string*">
        <xsl:param name="word-elems" as="element()*"/>
        <xsl:param name="cleanup-routines" as="xs:string*"/>
        <xsl:param name="join" as="xs:boolean"/>
        <xsl:variable name="sentence-sequence" as="xs:string*">
           <xsl:for-each select="1 to count($word-elems)">
               <xsl:variable name="i" select="." as="xs:integer"/>
               <!-- return a trailing space, if
                    (a) this is the last item of the sequence
                    (b) this is not a punct and the following is also not a punct
                    (c) this is a dash in first position of the sequence
                    (d) this is a punct and the following one is not a punct, except this is the first item of the sequence
                    (e) the following item starts with a bracket
                    AND
                    (f) this is not the start of a bracketed sequence
                    (g) this is not the item just before ending a bracketed sequence
               -->
               <xsl:variable name="trailing-space" select="' '[(
                   (:a:) ($i = count($word-elems)) or 
                   (:b:) (not(inel:isPunctuation($word-elems[$i]/item[1])) and not(inel:isPunctuation($word-elems[$i+1]/item[1]))) or
                   (:c:) ($i=1 and matches($word-elems[$i]/item[1], '[–-—]')) or
                   (:d:) (inel:isPunctuation($word-elems[$i]/item[1]) and not(inel:isPunctuation($word-elems[$i+1]/item[1])) and not($i=1)) or
                   (:e:) matches($word-elems[$i+1]/item[1], '^(\(|\[)'))
                   and
                   (:f:) not(matches($word-elems[$i]/item[1], '((\([^\)]*)|(\[[^\]]*))$')) and 
                   (:g:) not(matches($word-elems[$i+1]/item[1], '^\-*(\)|\])'))
                   ]" as="xs:string?"/>
               <xsl:sequence select="concat( inel:cleanup($word-elems[$i]/item[1], $cleanup-routines), $trailing-space )"/>
           </xsl:for-each>
        </xsl:variable>
        <xsl:copy-of select="if($join) then string-join($sentence-sequence, '') else $sentence-sequence"/>
    </xsl:function>
    

    <xsl:function name="inel:words2events" as="element()*">
        <xsl:param name="whitespaced-words" as="xs:string*"/>
        <xsl:param name="result-values" as="xs:string*"/>
        <xsl:param name="index" as="xs:integer"/>
        <xsl:param name="start-time" as="xs:double"/>
        <xsl:param name="end-time" as="xs:double"/>
        <xsl:param name="result-events" as="element()*"/>
        <xsl:variable name="time-interval" select="($end-time - $start-time) div count($whitespaced-words)" as="xs:double"/>
        <xsl:choose>
            <xsl:when test="$index &lt;= count($whitespaced-words)">
                <!-- 
                    (1) if the text of this word contains an opening parantheses, but not a closing one, and does not end with a whitespace (necessary for multi-worded paranthesed sequences)
                    (2) if the text of this word does not match the above (it's a simple word)
                -->
                <xsl:variable name="position-of-closing" select="if(matches($whitespaced-words[$index], '^[^\(]*\([^\)]*$') and not(matches($whitespaced-words[$index], '\s$'))) 
                    then (:1:) min(index-of($whitespaced-words[position() &gt; $index], ($whitespaced-words[position() &gt; $index and matches(., '^[^\(]*\)[^\)]*$')])[1]))
                    else (:2:) 0" as="xs:integer"/>
                
                <!-- create the new event -->
                <xsl:variable name="new-event" as="element()?">
                    <xsl:for-each select="string-join($result-values[position() &gt;= $index and position() &lt;= ($index + $position-of-closing)], '')[not(.='')]">
                        <event start="{$start-time + (($index - 1) * $time-interval)}" end="{$start-time + (($index + $position-of-closing) * $time-interval)}"><xsl:value-of select="replace(., '\s+$', ' ')"/></event>
                    </xsl:for-each>                             
                </xsl:variable>
                
                <!-- continue recursion with new event added to $result-events -->
                <xsl:copy-of select="inel:words2events($whitespaced-words, $result-values, ($index + $position-of-closing + 1), $start-time, $end-time, ($result-events, $new-event))"/>
                
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="$result-events"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    
    <xsl:function name="inel:string-to-regex-pattern" as="xs:string?">
        <xsl:param name="string" as="xs:string?"/>
        <xsl:value-of select="replace($string, '([\.\?\*\+\\\^\-\|\(\)\[\]\{\}])', '\\$1')"/>
    </xsl:function>
    
    <!-- ***** END: FUNCTIONS ******* -->
    <!-- **************************** -->

    
</xsl:stylesheet>
