<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:str="http://exmaralda.org/xmlns#string"
    xmlns:double="http://exmaralda.org/xmlns#double"
    exclude-result-prefixes="#all"
    version="2.0">

    <!--
        Author:      Daniel Jettka
        Created:     03.06.2017, 14:45
        Description: This stylesheet can be applied to the XML resulting from an EXAKT search.
                     It transforms the search results into a collection of new EXBs by using parameters 
                     (e.g. for the range of left and right context).
    -->
    
    <!-- **** START: Parameters **** -->

    <!-- path to coma file -->
    <xsl:param name="COMA_PATH" required="yes" as="xs:string"/>

    <!-- seconds left and right from annotation instance -->
    <xsl:param name="LEFT_CONTEXT" required="yes" as="xs:integer"/>
    <xsl:param name="RIGHT_CONTEXT" required="yes" as="xs:integer"/>

    <!-- template for result files -->
    <xsl:param name="TEMPLATE_FILE" required="no" as="xs:string"/>

    <!-- result directory - new EXBs written here -->
    <xsl:param name="OUTPUT_DIRECTORY" required="yes" as="xs:string"/>

    <!-- parameter sets start time of first event in result EXB to 0.0 -->
    <xsl:param name="RESET_TIMES" required="yes" as="xs:string"/>

    <!-- if present, then this parameter defines the value of annotations applied to matches -->
    <xsl:param name="ANNOTATION_TEXT" required="yes" as="xs:string"/>

    <!-- **** END:   Parameters **** -->


    <!-- **** START: Keys **** -->
    <xsl:key name="tli-by-time" match="tli" use="@time"/>
    <xsl:key name="tli-by-id" match="tli" use="@id"/>
    <xsl:key name="tier-by-id" match="tier" use="@id"/>
    <xsl:key name="speaker-name-by-id" match="speaker/abbreviation" use="../@id"/>
    <xsl:key name="exb-by-exs-locator" match="basic-transcription" use="../@locator"/>
    <!-- **** END:   Keys **** -->


    <!-- **** START: Global variables **** -->
    <xsl:variable name="corpus-base-directory" select="//base-directory/@url" as="xs:string"/>
    <xsl:variable name="coma-path" select="replace($COMA_PATH, '\\', '/')" as="xs:string"/>
    <xsl:variable name="coma-name" select="tokenize($coma-path, '/')[last()]" as="xs:string"/>
    <xsl:variable name="coma" select="document($coma-path)"/>
    <xsl:variable name="xsl-path" select="replace(static-base-uri(), '\\', '/')" as="xs:string"/>
    <xsl:variable name="output-path" select="replace($OUTPUT_DIRECTORY, '\\', '/')" as="xs:string"/>
    <xsl:variable name="left-seconds" select="$LEFT_CONTEXT" as="xs:integer"/>
    <xsl:variable name="right-seconds" select="$RIGHT_CONTEXT" as="xs:integer"/>
    <xsl:variable name="annotation-text" select="$ANNOTATION_TEXT[not(.='')]" as="xs:string?"/>
    <xsl:variable name="reset-times" select="$RESET_TIMES='true'" as="xs:boolean"/>
    <xsl:variable name="dateTime" select="replace(string-join(tokenize(string(current-dateTime()), ':')[position() &lt; 3], ''), 'T', '-')" as="xs:string"/>
    <xsl:variable name="tier-template" select="if(not($TEMPLATE_FILE='')) then document(replace($TEMPLATE_FILE, '\\', '/')) else ()"/>
    <xsl:variable name="keep-tier-types" select="('t', 'a', 'd')" as="xs:string+"/>
    <xsl:variable name="exbs">
        <xsl:for-each select="distinct-values(//locator/@file)">
            <exb locator="{.}">
                <xsl:document>
                    <xsl:copy-of select="document(concat($corpus-base-directory, '/', replace(replace(., '_s.exs', '.exb'), '.exs', '.exb')))"/>
                </xsl:document>
            </exb>
        </xsl:for-each>
    </xsl:variable>
    <!-- **** END:   Global variables **** -->


    <!-- **** START: Templates **** -->
    <xsl:template match="/">
        
        <!-- a search result looks like this in the processed XML -->        
        <!--
            <locator file="RudiVoellerWutausbruch/RudiVoellerWutausbruch_s.exs" xpath="(//segmentation[@name='SpeakerContribution_Event']/ts)[11]"/>
            <left-context>Jà äh ich hab doch keine Schärfe jetzt da reingebracht. (Also) ganz ehrlich.
                ((lacht in Intervallen,3s))˙ ((lacht in Intervallen,1,8s))˙ ((atmet ein)) Also in Island
                gibt es kein Weizenbier zu </left-context>
            <match original-match-start="194">mein</match>
            <right-context>em… Äh äh muss ich ganz ehrlich sagen, ((atmet ein)) äh m ich bin auch kein
                Weizenbiertrinker. Ich weiß auch nicht, ob wir jetzt • mit dem Stil weitermachen wollen.
                ((atmet ein)) Äh du hast es ja gesagt: Jetzt simmer nämlich schon da, • wo wir waren. Äh
                warum sollen mer nicht zu dem (Punkt) kommen, zu äh/ wo ma schon lange sind? ((atmet ein))
                Äh˙ • am Mittwoch müssen die da sein, • die sich • • den Arsch aufreißen. ((atmet ein)) • •
                • Sitzen die auf der Bank oder sind die heute schon auf dem Feld gewesen? Und warum haben
                sie s heute nicht gemacht? </right-context>
            <data>TIE1</data>
            <data>SPK0</data>
            <data>T117</data>
        -->

        <xsl:for-each select="//search-result">
            <xsl:variable name="search-result" select="."/>
            <xsl:variable name="result-position" select="position()" as="xs:integer"/>
            <!-- anywho... adjusting EXS filename seems to be safest method to retrieve EXB file -->
            <xsl:variable name="exb" as="document-node()">
                <xsl:document>
                   <xsl:copy-of select="key('exb-by-exs-locator', locator/@file, $exbs)"/>
                </xsl:document>
            </xsl:variable>
            <xsl:variable name="tier-id" select="data[1]" as="xs:string"/>
            <xsl:variable name="speaker-id" select="data[2]" as="xs:string"/>
            <xsl:variable name="start-event-id" select="data[3]" as="xs:string"/>

            <xsl:for-each select="$exb">
                <xsl:variable name="interpolated-tlis" as="element(tli)+">
                    <xsl:for-each select="descendant::tli">
                        <tli id="{@id}" time="{double:get-time-for-tli($exb/descendant::tli, position())}"/>
                    </xsl:for-each>
                </xsl:variable>


                <xsl:variable name="matching-events" as="element()+">
                    <xsl:choose>
                        <!-- for search in transcription or description the events that contain the match have to be calculated by the start ID of th first event in the segmentation unit and the provided character offset  -->
                        <xsl:when test="starts-with($search-result/locator/@xpath, '(//segmentation')">
                            <xsl:copy-of select="descendant::tier[@id=$tier-id]/event[concat(@start, '-', @end) = str:get-events-by-offset($exb/descendant::tier[@id=$tier-id]/event[@start=$start-event-id]/(., following-sibling::event), xs:integer($search-result//match/@original-match-start), xs:integer($search-result//match/@original-match-start + string-length($search-result/match)), ())]"/>
                        </xsl:when>
                        <!-- for search in annotation the search result provides the start ID of the matching event -->
                        <xsl:when test="starts-with($search-result/locator/@xpath, '(//annotation')">
                            <xsl:copy-of select="descendant::tier[@id=$tier-id]/event[@start = $start-event-id]"/>
                        </xsl:when>
                        <!-- for search in description the search result provides the start ID of the matching event -->
                        <xsl:otherwise>
                            <xsl:copy-of select="error((), 'Creation of collection only for search in transcription and annotation tiers.')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:variable name="tier-number" select="count(key('tier-by-id', $tier-id, $exb)/preceding-sibling::tier) + 1" as="xs:integer"/>
                <xsl:variable name="matching-start" select="$matching-events[1]/@start" as="xs:string"/>
                <xsl:variable name="matching-end" select="$matching-events[last()]/@end" as="xs:string"/>

                <!-- for DEBUGGING purposes -->
                <!--<xsl:result-document href="{concat('file:/', $output-path, '/', substring-before($coma-name, '.coma'), '-search-result-', $result-position, '.xml')}">
                    <timleine pos="{$tier-number}">
                        <xsl:copy-of select="$search-result"/>
                        <xsl:copy-of select="$matching-events"></xsl:copy-of>
                    </timleine>
                </xsl:result-document>-->

                <!-- write the new transcript -->
                <xsl:result-document href="{concat('file:/', $output-path, '/', substring-before($coma-name, '.coma'), '-collection-', $result-position, '.exb')}">
                    <xsl:comment select="concat('created automatically by ', tokenize($xsl-path, '/')[last()], ' on ', current-dateTime())"/>
                    <xsl:for-each select="$exb">
                        <xsl:copy>
                            <xsl:copy-of select="@*"/>
                            <xsl:variable name="start-time" select="xs:double((key('tli-by-id', $matching-start)/@time[.!=''], key('tli-by-id', $matching-start)/(preceding-sibling::tli/@time[.!=''])[last()])[1])" as="xs:double"/>
                            <xsl:variable name="end-time" select="xs:double((key('tli-by-id', $matching-end)/@time[.!=''], key('tli-by-id', $matching-end)/(following-sibling::tli/@time[.!=''])[1])[1])" as="xs:double"/>
                            <xsl:apply-templates mode="process-exb">
                                <xsl:with-param name="tli-IDs" select="$interpolated-tlis[((@time &gt;= ($start-time - $left-seconds)) and (@time &lt;= $end-time)) or ((@time &lt;= ($end-time + $right-seconds)) and (@time &gt;= $start-time))]/@id" as="xs:string+"/>
                                <xsl:with-param name="match-tier-number" select="$tier-number" as="xs:integer"/>
                                <xsl:with-param name="match-speaker-id" select="$speaker-id" as="xs:string"/>
                                <xsl:with-param name="new-annotation-start-end" select="concat($matching-start, '-', $matching-end)" as="xs:string"/>
                                <xsl:with-param name="anno-counter" select="$result-position" as="xs:integer"/>
                            </xsl:apply-templates>
                        </xsl:copy>
                    </xsl:for-each>
                </xsl:result-document>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:template>


    <xsl:template match="*" mode="process-exb">
        <xsl:param name="tli-IDs" as="xs:string+"/>
        <xsl:param name="match-tier-number" as="xs:integer"/>
        <xsl:param name="match-speaker-id" as="xs:string"/>
        <xsl:param name="new-annotation-start-end" as="xs:string"/>
        <xsl:param name="anno-counter" as="xs:integer"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="process-exb">
                <xsl:with-param name="tli-IDs" select="$tli-IDs"/>
                <xsl:with-param name="match-tier-number" select="$match-tier-number" as="xs:integer"/>
                <xsl:with-param name="match-speaker-id" select="$match-speaker-id" as="xs:string"/>
                <xsl:with-param name="new-annotation-start-end" select="$new-annotation-start-end" as="xs:string"/>
                <xsl:with-param name="anno-counter" select="$anno-counter" as="xs:integer"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="common-timeline" mode="process-exb">
        <xsl:param name="tli-IDs" as="xs:string+"/>
        <xsl:param name="match-tier-number" as="xs:integer"/>
        <xsl:param name="match-speaker-id" as="xs:string"/>
        <xsl:param name="new-annotation-start-end" as="xs:string"/>
        <xsl:param name="anno-counter" as="xs:integer"/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="key('tli-by-id', $tli-IDs)" mode="process-exb">
                <xsl:with-param name="minus" select="if($reset-times) then (key('tli-by-id', $tli-IDs)[exists(@time[.!=''])])[1]/@time else 0.0"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="tli" mode="process-exb">
        <xsl:param name="minus" as="xs:double"/>
        <xsl:copy>
            <xsl:copy-of select="@id"/>
            <xsl:if test="exists(@time)">
                <xsl:attribute name="time" select="@time - $minus"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="tier" mode="process-exb">
        <xsl:param name="tli-IDs" as="xs:string+"/>
        <xsl:param name="match-tier-number" as="xs:integer"/>
        <xsl:param name="match-speaker-id" as="xs:string"/>
        <xsl:param name="new-annotation-start-end" as="xs:string"/>
        <xsl:param name="anno-counter" as="xs:integer"/>
        <!--<xsl:if test="$tier-number = count(preceding-sibling::tier) + 1">-->

        <xsl:variable name="tier-speaker-name" select="(key('speaker-name-by-id', $match-speaker-id), $match-speaker-id)[1]" as="xs:string"/>
        <xsl:variable name="tier-position" select="count(preceding-sibling::tier) + 1" as="xs:integer"/>

        <!-- copy tier if it is a transcription tier -->
        <xsl:if test="@type = $keep-tier-types">
            <!-- copy tier and events that have start and end IDs of tli's that lie in range -->
            <xsl:copy>
                <xsl:copy-of select="@*"/>
                <xsl:copy-of select="event[(@start = $tli-IDs) and (@end = $tli-IDs)]"/>
            </xsl:copy>
        </xsl:if>

        <!--<!-\- for the transcription tier of the matched speaker, return annotation and template tiers -\->
        <xsl:if test="(@speaker = $tier-speaker-id) and (@type = 't')">-->

        <!-- for the tier containing the match, return annotation and template tiers -->
        <xsl:if test="$tier-position = $match-tier-number">

            <!-- create tier for annotation mark if provided in parameter $ANNOTATION_TEXT-->
            <xsl:if test="$annotation-text">
                <xsl:variable name="annotation-tier-category" select="lower-case(replace(replace($annotation-text, '#', ''), '^\s+|\s+$', ''))" as="xs:string"/>
                <tier id="{@id}00" speaker="{$match-speaker-id}" category="{$annotation-tier-category}" type="a" display-name="{concat($tier-speaker-name, ' ', '[', $annotation-tier-category, ']')}">
                    <event start="{substring-before($new-annotation-start-end, '-')}" end="{substring-after($new-annotation-start-end, '-')}">
                        <xsl:value-of select="replace($annotation-text, '#', xs:string($anno-counter))"/>
                    </event>
                </tier>
            </xsl:if>


            <!-- copy tiers from template -->
            <xsl:for-each select="$tier-template//tier">
                <tier id="{@id}0{position()}" speaker="{$match-speaker-id}" type="{@type}" display-name="{$tier-speaker-name} [{@category}]"/>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>

    <xsl:template match="text()|processing-instruction()|comment()" mode="process-exb">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <!-- **** END:   Templates **** -->



    <!-- **** START: Functions **** -->

    <xsl:function name="str:string2regex" as="xs:string?">
        <xsl:param name="in" as="xs:string?"/>
        <xsl:value-of select="replace($in, '([\(\)\[\]\{\}\.\$\?\+\*\\\-])',  '\\$1')"/>
    </xsl:function>

    <!-- get time for timeline item by id (if it does not have @time then interpolate) -->
    <xsl:function name="double:get-time-for-tli" as="xs:double">
        <xsl:param name="timeline" as="element()+"/>
        <xsl:param name="tli-position" as="xs:integer"/>
        <xsl:choose>
            <xsl:when test="exists($timeline[$tli-position]/@time[not(matches(., '^\s*$'))])">
                <xsl:value-of select="$timeline[$tli-position]/@time"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:variable name="preceding-time-position" select="index-of($timeline/@id, ($timeline[exists(@time) and (position() &lt; $tli-position)]/@id)[last()])" as="xs:integer"/>
                <xsl:variable name="following-time-position" select="index-of($timeline/@id, ($timeline[exists(@time) and (position() &gt; $tli-position)]/@id)[1])" as="xs:integer"/>
                <xsl:variable name="interpolated-time" select="$timeline[$preceding-time-position]/@time + ((($timeline[$following-time-position]/@time - $timeline[$preceding-time-position]/@time) div ($following-time-position - $preceding-time-position)) * ($tli-position - $preceding-time-position))" as="xs:double"/>
                <xsl:message select="concat('*** Warning: timeline item ', $timeline[$tli-position]/@id, ' has no time info (calculated to ', $interpolated-time, ')')" terminate="no"/>
                <xsl:value-of select="$interpolated-time"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="str:get-events-by-offset" as="xs:string*">
        <xsl:param name="events" as="element(event)*"/>
        <xsl:param name="start-offset" as="xs:integer"/>
        <xsl:param name="end-offset" as="xs:integer"/>
        <xsl:param name="result-events" as="xs:string*"/>
        <xsl:choose>
            <!-- as long as end is not reached, iterate through text content of events and save start-end to result-events when in offset range -->
            <xsl:when test="not($end-offset &lt; 1)">
                <xsl:variable name="first-event-has-content" select="string-length($events[1]) &gt; 0" as="xs:boolean"/>
                <xsl:variable name="new-first-event" as="element()?">
                    <xsl:if test="$first-event-has-content">
                        <event start="{$events[1]/@start}" end="{$events[1]/@end}"><xsl:value-of select="substring($events[1], 2)"/></event>
                    </xsl:if>
                </xsl:variable>
                <xsl:copy-of select="str:get-events-by-offset(($new-first-event, subsequence($events,2)), $start-offset - (1[$first-event-has-content], 0)[1], $end-offset - (1[$first-event-has-content], 0)[1], ($result-events, ($events[1]/concat(@start,'-',@end))[$start-offset &lt; 0]))"/>
            </xsl:when>
            <!-- end iteration when end-offset eq 0, and return distinct start-end infos of events that contained characters from the offset range -->
            <xsl:otherwise>
                <xsl:copy-of select="distinct-values($result-events)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    <!-- **** END:   Functions **** -->

</xsl:stylesheet>

