<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:my="http://www.philol.msu.ru/~languedoc/xml" exclude-result-prefixes="#all"
    version="2.0">

    <xsl:output method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no"/>
    <xsl:namespace-alias stylesheet-prefix="#default" result-prefix=""/>

    <!--
            This is the transform from SIL FLEx flextext format into EXMARaLDA exb, with configurable settings.
            See interlinear-eaf2exmaralda.xsl for conversion from ELAN.
            (c) Alexandre Arkhipov, 2015-2016.

            This transform is for a single file containing multiple texts. 
            Based on transform for Nganasan.
            Based on the batch version based on interlinear-eaf2exmaralda.xsl v1.11.
            v2.02: Added sentence-numbering options (flat, para, both); moved all parameters to settings file; added couple of tier styles
            v2.01: Added lang attribute to tier-word template (23.11.2016)
            v2.00: Many parameters related to specific tiers etc. are now read from a settings file.
                   NB: the cleanup functions are NOT yet called according to the settings file, but controlled internally
            v1.21: ... is replaced with … and both are counted as a (punctuation) word. (27.02.2016)
            v1.20: Transform for single files.
    -->

    <xsl:variable name="filename" select="replace(replace(base-uri(), '^.*/', ''), '.flextext', '')"/>

    <xsl:variable name="tiers-morph">mb mp</xsl:variable>
    <xsl:variable name="tiers-gloss">ge gr mc hn</xsl:variable>


    <!--    <xsl:param name="timestart" as="xs:decimal" select="4.0"/>
    <!-\- Time offset for first word -\->
    <xsl:param name="timestep" as="xs:decimal" select="0.5"/>
    <!-\- Mean word length in sec -\->
    <xsl:param name="segnum-format" as="xs:string" select="'flat'"/>

-->
    <!-- flat numbering or para.sent numbering for sentences; set parameter to 'flat' or 'para', respectively, or 'both' -->

    <xsl:param name="settings-file" select="'settings.xml'"/>
    <xsl:variable name="output-location" select="replace(base-uri(), tokenize(substring(base-uri(), 6), '/')[last()], '')"/>
   <!-- <xsl:variable name="output-location" select="replace(base-uri(), $filename, '')"/>-->
    <xsl:variable name="settings" select="doc($settings-file)"/>
    <xsl:variable name="baseline" select="$settings//tiers/baseline/@lang"/>
    <xsl:variable name="timestart" as="xs:decimal" select="$settings//timeline/@start"/>
    <xsl:variable name="timestep" as="xs:decimal" select="$settings//timeline/@step"/>
    <xsl:variable name="segnum-format" select="$settings//sentence-number/@format"/>
    <xsl:variable name="textname" select="/document/interlinear-text[1]/item[@type = $settings//text-name/@from-item and @lang = $settings//text-name/@from-lang]/text()"/>

    <xsl:template match="/">
        <conversion-report filename="{$filename}" output-location="{concat($output-location, $textname,'.exb')}">
            <conversion-settings filename="{$settings-file}">
                <xsl:copy-of select="$settings/*/*"/>
            </conversion-settings>
            <xsl:apply-templates select="*"/>
        </conversion-report>
    </xsl:template>

    <xsl:template match="//interlinear-text">

        <xsl:variable name="current-text" select="."/>
        <xsl:variable name="textname" select="current()/item[@type = $settings//text-name/@from-item and @lang = $settings//text-name/@from-lang]/text()"/>
        <xsl:variable name="speaker"
            select="
                if (substring-before($textname, '_') = '') then
                    'SPK'
                else
                    if ($settings//speaker-code/@position = '2') then
                        substring-before(substring-after($textname, '_'), '_')
                    else
                        substring-before($textname, '_')"/>

        <interlinear-text name="{$textname}"/>
        <xsl:result-document method="xml" indent="yes" encoding="utf-8" omit-xml-declaration="no" href="{concat($output-location, $textname,'.exb')}">
            <basic-transcription>
                <head>
                    <meta-information>
                        <project-name>
                            <xsl:value-of select="$settings//project-name/@value"/>
                        </project-name>
                        <transcription-name>
                            <xsl:value-of select="$textname"/>
                        </transcription-name>
                        <referenced-file url="{concat($textname,'.wav')}"/>
                        <ud-meta-information/>
                        <comment/>
                        <transcription-convention/>
                    </meta-information>
                    <speakertable>
                        <speaker id="{$speaker}">
                            <abbreviation>
                                <xsl:value-of select="$speaker"/>
                            </abbreviation>
                            <sex value="m"/>
                            <languages-used/>
                            <l1/>
                            <l2/>
                            <ud-speaker-information> </ud-speaker-information>
                            <comment/>
                        </speaker>
                    </speakertable>
                </head>
                <basic-body>
                    <common-timeline>
                        <!-- here go the time slots -->
                        <xsl:for-each select=".//phrase">
                            <xsl:variable name="tsnumber" select="count(./preceding::word[my:word(.)])"/>
                            <!-- +position()-1 -->
                            <tli id="{concat('T',$tsnumber)}" time="{format-number($timestart + $timestep*$tsnumber, '#0.0##')}" type="appl"/>
                            <xsl:for-each select="current()//word[my:word(.)]">
                                <tli id="{concat('T',$tsnumber+position())}" time="{format-number($timestart + $timestep*($tsnumber+position()),'#0.0##')}" type="appl"/>
                            </xsl:for-each>
                        </xsl:for-each>
                    </common-timeline>

                    <!-- Here go the tiers. The output order is fixed. -->
                    <xsl:for-each select="$settings//tiers/tier">
                        <xsl:variable name="t" select="current()/@template"/>
                        <xsl:variable name="n" select="current()/@name"/>
                        <xsl:choose>
                            <xsl:when test="$t eq 'tier-sent' and $n eq 'ref'">
                                <xsl:apply-templates select="$current-text" mode="tier-sent">
                                    <xsl:with-param name="cat" select="'ref'"/>
                                    <xsl:with-param name="display" select="'ref'"/>
                                    <xsl:with-param name="itemtype"
                                        select="
                                            if (current()/@itemtype) then
                                                current()/@itemtype
                                            else
                                                'txt'"/>
                                    <xsl:with-param name="lang"
                                        select="
                                            if (current()/@lang) then
                                                current()/@lang
                                            else
                                                $baseline"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                    <xsl:with-param name="prefix" select="concat($textname, '.')"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-sent' and $n ne 'ref'">
                                <xsl:apply-templates select="$current-text" mode="tier-sent">
                                    <xsl:with-param name="cat" select="current()/@name"/>
                                    <xsl:with-param name="display" select="current()/@name"/>
                                    <xsl:with-param name="itemtype"
                                        select="
                                            if (current()/@itemtype) then
                                                current()/@itemtype
                                            else
                                                'txt'"/>
                                    <xsl:with-param name="lang"
                                        select="
                                            if (current()/@lang) then
                                                current()/@lang
                                            else
                                                $baseline"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-sent-join'">
                                <xsl:apply-templates select="$current-text" mode="tier-sent-join">
                                    <xsl:with-param name="cat" select="current()/@name"/>
                                    <xsl:with-param name="display" select="current()/@name"/>
                                    <xsl:with-param name="itemtype"
                                        select="
                                            if (current()/@itemtype) then
                                                current()/@itemtype
                                            else
                                                'txt'"/>
                                    <xsl:with-param name="lang"
                                        select="
                                            if (current()/@lang) then
                                                current()/@lang
                                            else
                                                $baseline"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-tx'">
                                <xsl:apply-templates select="$current-text" mode="tier-tx">
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-word'">
                                <xsl:apply-templates select="$current-text" mode="tier-word">
                                    <xsl:with-param name="cat" select="current()/@name"/>
                                    <xsl:with-param name="display" select="current()/@name"/>
                                    <xsl:with-param name="itemtype"
                                        select="
                                            if (current()/@itemtype) then
                                                current()/@itemtype
                                            else
                                                'txt'"/>
                                    <xsl:with-param name="lang"
                                        select="
                                            if (current()/@lang) then
                                                current()/@lang
                                            else
                                                $baseline"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-word-new'">
                                <xsl:apply-templates select="$current-text" mode="tier-word-new">
                                    <xsl:with-param name="cat" select="current()/@name"/>
                                    <xsl:with-param name="display" select="current()/@name"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:when test="$t eq 'tier-morph'">
                                <xsl:apply-templates select="$current-text" mode="tier-morph">
                                    <xsl:with-param name="cat" select="current()/@name"/>
                                    <xsl:with-param name="display" select="current()/@name"/>
                                    <xsl:with-param name="itemtype"
                                        select="
                                            if (current()/@itemtype) then
                                                current()/@itemtype
                                            else
                                                'txt'"/>
                                    <xsl:with-param name="lang"
                                        select="
                                            if (current()/@lang) then
                                                current()/@lang
                                            else
                                                $baseline"/>
                                    <xsl:with-param name="speaker" select="$speaker"/>
                                    <xsl:with-param name="sep" select="current()/@sep"/>
                                    <xsl:with-param name="cleanup" select="current()/@cleanup"/>
                                </xsl:apply-templates>
                            </xsl:when>

                        </xsl:choose>
                    </xsl:for-each>

                    <!--                    <xsl:call-template name="tier-sent">
                        <!-\- nt: Comments (notes) -\->
                        <xsl:with-param name="itemtype" select="'note'"/>
                        <xsl:with-param name="cat" select="'nt'"/>
                        <xsl:with-param name="display" select="'nt'"/>
                        <xsl:with-param name="speaker" select="$speaker"/>
                    </xsl:call-template>
-->
                </basic-body>
                <!-- insert the tierformat-table (copied a formatting template) -->
                <xsl:copy-of select="$format-table"/>
            </basic-transcription>
        </xsl:result-document>

    </xsl:template>


    <xsl:template name="tier-sent" match="interlinear-text" mode="tier-sent">
        <!-- from flextext -->
        <xsl:param name="itemtype"/>
        <xsl:param name="lang" select="''"/>
        <!-- for exmaralda -->
        <xsl:param name="cat" select="'v'"/>
        <xsl:param name="type" select="'d'"/>
        <xsl:param name="display"/>
        <!-- to append filename in ref tier -->
        <xsl:param name="prefix" select="''"/>
        <xsl:param name="speaker" select="'SPK'"/>
        <tier id="{$display}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$display}">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <!-- ./preceding::word[item/@type!='punct'] -->
                <!-- +position()-1 -->
                <xsl:variable name="ts-end" select="$ts-start + count(.//word[my:word(.)])"/>
                <xsl:variable name="value">
                    <xsl:value-of
                        select="
                            ./item[@type = $itemtype and (if ($lang eq '') then
                                true()
                            else
                                @lang = $lang)]"
                        separator=" || "/>
                </xsl:variable>
                <event start="{concat('T',$ts-start)}" end="{concat('T',$ts-end)}">
                    <!-- for ref tier, output the filename dot (paragraph number dot) sentence number -->
                    <!-- v2.02: if format is 'flat' then just count the sentences through the whole text -->
                    <xsl:choose>
                        <xsl:when test="$display = 'ref'">
                            <xsl:choose>
                                <xsl:when test="$segnum-format = 'para'">
                                    <xsl:value-of select="concat($prefix, my:cleanup-brackets(my:sent-renum($value)))"/>
                                </xsl:when>
                                <xsl:when test="$segnum-format = 'both'">
                                    <xsl:value-of select="concat($prefix, format-number(position(), '#000'), ' (', my:cleanup-brackets(my:sent-renum($value)), ')')"/>
                                </xsl:when>
                                <!-- 'flat' is applied otherwise -->
                                <xsl:otherwise>
                                    <xsl:value-of select="concat($prefix, format-number(position(), '#000'))"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="concat($prefix, my:cleanup-brackets($value))"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </event>
            </xsl:for-each>
        </tier>
    </xsl:template>


    <!-- SENTENCE-LEVEL TIERS JOINED FROM WORDS -->
    <!-- TRANSCRIPTION FOR WHOLE SENTENCE (TS < joined TX) -->
    <xsl:template name="tier-sent-join" match="interlinear-text" mode="tier-sent-join">
        <!-- from flextext -->
        <xsl:param name="itemtype"/>
        <xsl:param name="lang" select="''"/>
        <!-- for exmaralda -->
        <xsl:param name="cat" select="'v'"/>
        <xsl:param name="type" select="'d'"/>
        <xsl:param name="display"/>
        <xsl:param name="speaker" select="'SPK'"/>
        <tier id="{$display}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$display}">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <xsl:variable name="ts-end" select="$ts-start + count(.//word[my:word(.)])"/>
                <xsl:variable name="value">
                    <xsl:for-each-group select=".//word" group-starting-with="word[my:startwordgroup(.)]">
                        <!-- WHEN SENTENCE STARTS WITH PUNCTUATION, IT IS STICKED TO THE FIRST WORD -->
                        <xsl:variable name="value">
                            <xsl:value-of select="current-group()/item[@type = $itemtype or @type = 'punct']" separator=""/>
                        </xsl:variable>
                        <xsl:value-of select="my:cleanup-tx($value)"/>
                    </xsl:for-each-group>
                </xsl:variable>
                <event start="{concat('T',$ts-start)}" end="{concat('T',$ts-end)}">
                    <xsl:value-of select="my:cleanup-tx($value)"/>
                </event>
            </xsl:for-each>
        </tier>
    </xsl:template>


    <!-- WORD-LEVEL TIERS -->
    <!-- WORD TRANSCRIPTION (~TX) -->
    <xsl:template name="tier-tx" match="interlinear-text" mode="tier-tx">
        <xsl:param name="speaker" select="'SPK'"/>
        <tier id="tx" speaker="{$speaker}" category="tx" type="t" display-name="tx">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <xsl:for-each-group select=".//word" group-starting-with="word[my:startwordgroup(.)]">
                    <!-- WHEN SENTENCE STARTS WITH PUNCTUATION, IT IS STICKED TO THE FIRST WORD -->
                    <event start="{concat('T',$ts-start+position()-1)}" end="{concat('T',$ts-start+position())}">
                        <xsl:variable name="value">
                            <xsl:value-of select="current-group()/item[@type = 'txt' or @type = 'punct']" separator=""/>
                        </xsl:variable>
                        <xsl:value-of select="my:cleanup-tx($value)"/>
                    </event>
                </xsl:for-each-group>
            </xsl:for-each>
        </tier>
    </xsl:template>

    <!-- NEW EMPTY WORD-LEVEL TIERS (PS, SeR, SyF, IST) -->
    <xsl:template name="tier-word-new" match="interlinear-text" mode="tier-word-new">
        <!-- for exmaralda -->
        <xsl:param name="cat" select="'v'"/>
        <xsl:param name="type" select="'a'"/>
        <xsl:param name="display"/>
        <xsl:param name="speaker" select="'SPK'"/>
        <tier id="{$display}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$display}">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <!-- +position()-1 -->
                <xsl:for-each-group select=".//word" group-starting-with="word[my:startwordgroup(.)]">
                    <!-- WHEN SENTENCE STARTS WITH PUNCTUATION, IT IS STICKED TO THE FIRST WORD -->
                    <event start="{concat('T',$ts-start+position()-1)}" end="{concat('T',$ts-start+position())}"/>
                </xsl:for-each-group>
            </xsl:for-each>
        </tier>
    </xsl:template>


    <!-- WORD-LEVEL TIERS (PS) -->
    <xsl:template name="tier-word" match="interlinear-text" mode="tier-word">
        <!-- for exmaralda -->
        <xsl:param name="cat" select="'v'"/>
        <xsl:param name="type" select="'a'"/>
        <xsl:param name="display"/>
        <xsl:param name="speaker" select="'SPK'"/>
        <xsl:param name="lang" select="''"/>
        <xsl:param name="itemtype"/>
        <tier id="{$display}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$display}">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <!-- +position()-1 -->
                <xsl:for-each-group select=".//word" group-starting-with="word[my:startwordgroup(.)]">
                    <!-- WHEN SENTENCE STARTS WITH PUNCTUATION, IT IS STICKED TO THE FIRST WORD -->
                    <event start="{concat('T',$ts-start+position()-1)}" end="{concat('T',$ts-start+position())}">
                        <xsl:value-of select="current-group()/item[@type = $itemtype and @lang = $lang]" separator=""/>
                    </event>
                </xsl:for-each-group>
            </xsl:for-each>
        </tier>
    </xsl:template>


    <!-- MORPH/GLOSS LEVELS -->
    <xsl:template name="tier-morph" match="interlinear-text" mode="tier-morph">
        <!-- from flextext -->
        <xsl:param name="itemtype"/>
        <xsl:param name="lang" select="''"/>
        <!-- for exmaralda -->
        <xsl:param name="cat" select="'v'"/>
        <xsl:param name="type" select="'a'"/>
        <xsl:param name="display"/>
        <!-- separator -->
        <xsl:param name="sep" select="'-'"/>
        <xsl:param name="speaker" select="'SPK'"/>
        <tier id="{$display}" speaker="{$speaker}" category="{$cat}" type="{$type}" display-name="{$display}">
            <xsl:for-each select=".//phrase">
                <xsl:variable name="ts-start" select="count(./preceding::word[my:word(.)])"/>
                <!-- +position()-1 -->
                <xsl:for-each-group select=".//word" group-starting-with="word[my:startwordgroup(.)]">
                    <!-- WHEN SENTENCE STARTS WITH PUNCTUATION, IT IS STICKED TO THE FIRST WORD -->
                    <event start="{concat('T',$ts-start+position()-1)}" end="{concat('T',$ts-start+position())}">
                        <xsl:variable name="value">
                            <xsl:value-of select="current-group()//morph/item[@type = $itemtype and @lang = $lang]" separator="{$sep}"/>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="contains($tiers-morph, $display)">
                                <xsl:value-of select="my:cleanup-morph($value)"/>
                            </xsl:when>
                            <xsl:when test="contains($tiers-gloss, $display)">
                                <xsl:value-of select="my:cleanup-gloss($value)"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="my:cleanup-brackets($value)"/>
                            </xsl:otherwise>
                        </xsl:choose>

                    </event>
                </xsl:for-each-group>
            </xsl:for-each>
        </tier>
    </xsl:template>


    <xsl:function name="my:cleanup-tx" as="xs:string">
        <xsl:param name="in" as="xs:string"/>
        <xsl:value-of select="concat(replace(replace(my:cleanup-brackets($in), '( +$)|[ˀ]', ''), '[03]([\.,!\?;:]*)$', '$1'), ' ')"/>
        <!-- strip trailing space, strip final 0 and 3, attach one space -->
        <!-- strip "deep consonant" and "deep glottal stop" -->
    </xsl:function>

    <xsl:function name="my:cleanup-morph" as="xs:string">
        <xsl:param name="in" as="xs:string"/>
        <xsl:value-of select="replace(my:cleanup-brackets($in), '-\^0', '')"/>
        <!-- strip -^0 -->
    </xsl:function>

    <xsl:function name="my:cleanup-gloss" as="xs:string">
        <xsl:param name="in" as="xs:string"/>
        <xsl:value-of select="replace(my:cleanup-brackets(replace($in, ' ', '')), '-(\[.+?\])', '.$1')"/>
        <!-- replace - with . before [] -->
        <!-- fixed to lazy quant. to handle multiple replaces in one string -->
        <!-- erase spaces -->
    </xsl:function>

    <!-- replacing brackets [[ ]] with (( )) for all tiers -->
    <xsl:function name="my:cleanup-brackets" as="xs:string">
        <xsl:param name="in" as="xs:string"/>
        <!-- replace [[, ]] with ((, )) -->
        <xsl:variable name="temp" select="replace(replace($in, '\[\[', '(('), '\]\]', '))')"/>
        <!-- replace ... with … -->
        <xsl:variable name="temp2" select="replace($temp, '(\.\.\.)', '…')"/>
        <!-- replace /// with ((XXX)); ??? with ((unknown)) -->
        <xsl:value-of select="replace(replace($temp2, '(///)', '((XXX))'), '\?\?\?', '((unknown))')"/>
    </xsl:function>

    <!-- take "para.sent" number from segnum, extract second part, -->
    <!-- convert to a number and pad with zeroes to 3 digits -->
    <xsl:function name="my:sent-renum" as="xs:string">
        <xsl:param name="in" as="xs:string"/>
        <xsl:choose>
            <xsl:when test="substring-after($in, '.') != ''">
                <xsl:value-of select="concat(format-number(number(substring-before($in, '.')), '#000'), '.', format-number(number(substring-after($in, '.')), '#000'))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="format-number(number($in), '#000')"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="my:startwordgroup" as="xs:boolean">
        <xsl:param name="word" as="element(word)"/>
        <xsl:choose>
            <xsl:when test="my:lpunct($word/preceding-sibling::word[1])">
                <xsl:value-of select="false()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of
                    select="
                        if (my:lpunct($word) or my:word($word)) then
                            true()
                        else
                            false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="my:lpunct" as="xs:boolean">
        <xsl:param name="word"/>
        <!-- as="element(word)" -->
        <xsl:choose>
            <!-- had to change “ to „ for SelkupSLC since Selkup line has German-style quotation marks -->
            <xsl:when test="$word/item/@type = 'punct' and matches($word/item/text()[1], '(\(+)|(\[+)|[«„‘]')">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:when test="$word/item/@type = 'punct' and empty($word/preceding-sibling::word)">
                <xsl:value-of select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <!-- Tell apart real punctuation from non-baseline lang which also has @type="punct" -->
    <!-- Also treat "///" and "???" and "..." and "…" as a word -->
    <xsl:function name="my:word" as="xs:boolean">
        <xsl:param name="word"/>
        <!-- as="element(word)" -->
        <xsl:value-of
            select="
                if ($word/item/@type = 'punct' and $word/item/@lang = $baseline and not(matches($word/item[@type = 'punct'], '(///)|(\?\?\?)|(…)|(\.\.\.)'))) then
                    false()
                else
                    true()"
        />
    </xsl:function>

    <xsl:function name="my:sec2msec">
        <xsl:param name="time-sec"/>
        <xsl:value-of select="replace($time-sec, '([0-9]{3})$', '.$1')"/>
    </xsl:function>

    <!-- insert the tierformat-table (copied a formatting template) -->
    <xsl:variable name="format-table">
        <tierformat-table>
            <timeline-item-format show-every-nth-numbering="1" show-every-nth-absolute="1" absolute-time-format="time" miliseconds-digits="1"/>
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
</xsl:stylesheet>
