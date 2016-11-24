<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:exb="https://corpora.uni-hamburg.de/exmaralda"
    xmlns:svg="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:string="http://modiko.net/xpath-ns/string"
    exclude-result-prefixes="xsl xs string"
    version="2.0">
    
    <xsl:output method="xhtml" encoding="UTF-8" doctype-public="html" omit-xml-declaration="no"/>
    
    <xsl:param name="VIDEO_URL" select="concat(substring-before(//referenced-file[ends-with(@url, '.mpg')]/@url, '.mpg'), '.ogg')" as="xs:string"/>
    <xsl:param name="DIAGRAM_URL" select="concat(string-join(tokenize(base-uri(/), '/')[position()!=last()], '/'), '/', distinct-values(//tier[@category='ref']/event/substring-before(text(), '#')))" as="xs:string"/>
    
    <xsl:key name="time-by-id" match="tli/@time" use="../@id"/>
    <xsl:key name="highlight-times-by-event-id" match="elem/@time" use="../@id"/>
    <xsl:key name="elem-id-by-highlight-times" match="elem/@id" use="../@time"/>
    
    <xsl:variable name="TLI_IDs" select="//common-timeline/tli/@id" as="xs:string+"/>
    <xsl:variable name="EVENT_HIGHLIGHT_TIMES">
        <xsl:for-each select="//event">
            <xsl:variable name="id" select="concat(parent::tier/@id, 'EV', count(preceding-sibling::event) + 1)" as="xs:string"/>
            <xsl:variable name="start" select="(key('time-by-id', @start)[.!=''], preceding-sibling::event/(key('time-by-id', @end), key('time-by-id', @start)))[1]" as="xs:double"/>
            <xsl:variable name="end" select="(key('time-by-id', @end)[.!=''], following-sibling::event/(key('time-by-id', @start), key('time-by-id', @end)))[1]" as="xs:double"/>
            <xsl:for-each select="xs:integer(round-half-to-even($start, 2) * 100) to xs:integer(round-half-to-even($end, 2) * 100)">
                <elem id="{$id}" time="{.}"/>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:variable>
    <xsl:variable name="SVG_ELEM_HIGHLIGHT_TIMES">
        <xsl:for-each select="//tier[@category='ref']/event">
            <xsl:variable name="start" select="(key('time-by-id', @start)[.!=''], preceding-sibling::event/(key('time-by-id', @end), key('time-by-id', @start)))[1]" as="xs:double"/>
            <xsl:variable name="end" select="(key('time-by-id', @end)[.!=''], following-sibling::event/(key('time-by-id', @start), key('time-by-id', @end)))[1]" as="xs:double"/>
            <xsl:for-each select="for $xpPart in tokenize(substring-after(text(), '#'), 'xpointer\(id\(''')[.!=''] return substring-before($xpPart, '''))')">
                <xsl:variable name="id" select="." as="xs:string"/>
                <xsl:for-each select="xs:integer(round-half-to-even($start, 2) * 100) to xs:integer(round-half-to-even($end, 2) * 100)">
                    <elem id="{$id}" time="{.}"/>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:for-each>
    </xsl:variable>
    
    <xsl:variable name="SVGzoom" select="0.22" as="xs:double"/>
    
    <xsl:template match="/">
         <html xmlns="http://www.w3.org/1999/xhtml">
             <head>
                 <title></title>
                 <style type="text/css">
                     html, body{ margin:0;padding:0;font-family:"Open Sans",Helvetica,Verdana,Arial,sans-serif; }
                     
                     header, section#overview{ border-top:1px solid #aaa; border-bottom:1px solid #aaa; color:#888; background-color:#f0f0f0; }
                     
                     header{ clear:both; background-color:#f0f0f0; color:#333; padding:5px; overflow:auto; } 
                     header h1{ font-size:1em; float:left; margin:0; }
                     
                     section#controls{ float:right; padding:0; font-size:0.8em; }
                     section#sec1{ display:table; table-layout:fixed; width:100%; clear:both; }
                     section#transcript-sec{ overflow:scroll; margin-left:5em; }
                     div#video, div#diagram { display:table-cell; height:300px; overflow-x:scroll; min-width:300px; text-align:center; vertical-align:top; background-color:#fafafa; }
                     section#overview{ width:100%; min-height: 10px; padding:0; font-size:0.8em; }
                     div#video video{ width:100%; min-width:250px; height: 100%; min-height:200px; }
                     footer{ width:100%; color:#aaa; font-size:0.8em; padding:10px; text-align:center; clear:both; border-top:1px solid #aaa; }                     
                     svg { background-color:#fafafa; min-width:100%; }
                     table#transcript{ font-size:0.8em; color: #666; border-collapse:separate; border-spacing:0 1px; background-color:#e0e0e0; }
                     table#transcript tr{ background-color:#e0e0e0; }
                     table#transcript tr:first-child td{ padding-top: 6px; }
                     table#transcript tr:last-child td{ padding-bottom: 6px; }
                     table#transcript td{ white-space:nowrap; padding: 3px 6px 3px 2px; }
                     table#transcript td.empty{ color:transparent; }
                     table#transcript td.event{ background-color:#fff; border:1px solid #aaa;}
                     table#transcript tr.timeline{ background-color:#aaa; color:#fff; font-size:0.7em; font-weight:bold; }
                     table#transcript tr.timeline td.tli{ cursor:pointer; }
                     table#transcript tr.tier{ margin-bottom:3px; }
                     table#transcript tr.tier td.tier-name{ position:absolute; width:5em; left:0; top:auto; margin-top:0; }
                     table#transcript tr.nv td.event, table#transcript tr.ref td.event{ font-style:italic; font-size:0.9em; }
                     form#autoActions{ margin:0; }
                     form#autoActions label{ margin-right: 10px; }
                 </style>
                 <script type="text/javascript">
                     <xsl:comment>
                     <![CDATA[
                     var activeEvents = new Array();
                     var activeSvgElems = new Array();
                     var highlightedBackground = "rgba(255,30,30,0.1)";                    
                     var highlightedStroke = "#ff3030";
                     var unhighlightedBackground = "#ffffff";
                     var unhighlightedFill = "transparent";
                     var currentlyScrolledToID = '';
                     
                     function bodyloaded(){
                        // fill some variables
                        mediaElem = document.getElementById('videoElem');
                        transcriptSection = document.getElementById('transcript-sec');
                        diagramSection = document.getElementById('diagram').getElementsByTagName('div')[0];
                        highlightEvents = document.getElementById('highlightEventsInput').checked;
                        highlightSvg = document.getElementById('highlightSvgInput').checked;
                        scrollEvents = document.getElementById('scrollEventsInput').checked;
                        scrollSvg = document.getElementById('scrollSvgInput').checked;
                     }
                     
                     function videoTimeUpdate(){
                        transcriptEventProcessor();
                        svgElementProcessor();
                     }
                     
                     function transcriptEventProcessor(){
                        idArray = eventTimes[Math.floor(mediaElem.currentTime * 100)]
                     
                        // first remove and dehighlight old IDs from activeEvents
                        countActiveEvents = activeEvents.length; //has to remain constant value
                        for (var i=0; i<countActiveEvents; i++) {
                            if(idArray.indexOf(activeEvents[countActiveEvents - i - 1]) < 0){
                                document.getElementById(activeEvents[countActiveEvents - i - 1]).style.backgroundColor = unhighlightedBackground;
                                activeEvents.splice(countActiveEvents - i - 1, 1);
                            }
                        }
                     
                        // then push and highlight new IDs
                        for (var i = 0; i < idArray.length; i++) {
                            if(!activeEvents.indexOf(idArray[i]) >= 0){
                                if(highlightEvents){ document.getElementById(idArray[i]).style.backgroundColor = highlightedBackground };
                                activeEvents.push(idArray[i]);
                            }
                        }
                        
                        // scroll to first event in activeEvents
                        if((idArray.length > 0) && scrollEvents){
                            transcriptSection.scrollLeft = document.getElementById(idArray[0]).offsetLeft;
                        }
                     }
                     
                     function svgElementProcessor(){
                        idArray = svgTimes[Math.floor(mediaElem.currentTime * 100)];
                     
                        // first remove and dehighlight old IDs from activeSvgElems
                        countActiveElems = activeSvgElems.length; //has to remain constant value
                        for (var i=0; i<countActiveElems; i++) {
                            if(idArray.indexOf(activeSvgElems[countActiveElems - i - 1]) < 0){
                                currentSvgElem = svgDoc.getElementById(activeSvgElems[countActiveElems - i - 1]);
                                currentSvgElem.style.fill = unhighlightedFill;
                                currentSvgElem.style.stroke = unhighlightedFill;
                                activeSvgElems.splice(countActiveElems - i - 1, 1);
                            }
                        }
                     
                        // then push and highlight new IDs
                        for (var i = 0; i < idArray.length; i++) {
                            if(!activeSvgElems.indexOf(idArray[i]) >= 0){
                                if(highlightSvg){                                    
                                    currentSvgElem = svgDoc.getElementById(idArray[i]);
                                    currentSvgElem.style.fill = highlightedBackground;
                                    currentSvgElem.style.stroke = highlightedStroke;
                                }
                                activeSvgElems.push(idArray[i]);
                            }
                        }   
                        
                        //scroll to first svg element in activeSvgElems
                        if((idArray.length > 0) && scrollSvg){
                            if(idArray[0] != currentlyScrolledToID){
                                currentlyScrolledToID = idArray[0];
                                svgElemInfo = svgDoc.getElementById(currentlyScrolledToID).getBoundingClientRect();
                                divInfo = diagramSection.getBoundingClientRect();
                                diagramSection.scrollLeft = (svgElemInfo.left - divInfo.left) - 50 + diagramSection.scrollLeft;
                                diagramSection.scrollTop = (svgElemInfo.top - divInfo.top) - 50 + diagramSection.scrollTop;
                            }
                            
                        }
                     }
                     
                     ]]>
                     </xsl:comment>
                 </script>
             </head>
             <body onload="bodyloaded();">
                 <header>
                     <h1><xsl:value-of select="//transcription-name"/></h1>
                     <section id="controls">
                         <!--<span id="mediaTime"></span>-->
                         <form action="#" id="autoActions">
                             Automatic actions: 
                             <input id="highlightEventsInput" name="highlightEventsInput" checked="checked" type="checkbox" onchange="highlightEvents = this.checked;"/>
                             <label for="highlightEventsInput">highlight transcription</label>
                             <input id="highlightSvgInput" name="highlightSvgInput" checked="checked" type="checkbox" onchange="highlightSvg = this.checked;"/>
                             <label for="highlightSvgInput">highlight diagram</label>
                             <input id="scrollEventsInput" name="scrollEventsInput" checked="checked" type="checkbox" onchange="scrollEvents = this.checked;"/>
                             <label for="scrollEventsInput">scroll transcription</label>
                             <input id="scrollSvgInput" name="scrollSvgInput" checked="checked" type="checkbox" onchange="scrollSvg = this.checked;"/>
                             <label for="scrollSvgInput">scroll diagram</label>
                         </form>
                     </section>
                 </header>
                 <section id="sec1">
                     <div id="video">
                         <video id="videoElem" src="{$VIDEO_URL}" controls="controls" ontimeupdate="videoTimeUpdate();"/>
                     </div>
                     <div id="diagram">
                         <div style="height:100%;overflow-y:scroll">
                             <xsl:for-each select="document($DIAGRAM_URL)">
                                 <xsl:call-template name="process-svg"/>
                             </xsl:for-each>
                         </div>
                     </div>
                 </section>
                 <section id="overview">
                     <!--<xsl:call-template name="create-overview"/>-->
                 </section>
                 <section id="transcript-sec">
                     <xsl:call-template name="process-exb"/>
                 </section>
                 <footer>
                     Copyright: MoDiKo 2015
                 </footer>
                 <script type="text/javascript">
                     <xsl:copy-of select="$SVGPanCODE/comment()"/>
                 </script>
             </body>
         </html>
    </xsl:template>
        
    <xsl:template name="process-svg">
        <xsl:apply-templates mode="SVG"/>
    </xsl:template>
    
    <xsl:template match="*:svg" mode="SVG">
        <xsl:copy xmlns="http://www.w3.org/2000/svg">
            <xsl:copy-of select="@*[not(local-name()=('style','width','height'))]"/>
            <xsl:attribute name="width" select="@width * $SVGzoom"/>
            <xsl:attribute name="height" select="@height * $SVGzoom"/>
            <xsl:attribute name="id" select="'svgDoc'"/>
            <xsl:copy-of select="namespace::*"/>
            <!--<xsl:attribute name="viewBox" select="concat('0 0 ', @width, ' ',@height)"/>-->
            <!--<script xlink:href="SVGPan.js"></script>-->
            <!--<script type="text/javascript">
                        <xsl:copy-of select="$SVGPanCODE/comment()"/>
                    </script>-->
            <g transform="matrix({$SVGzoom},0,0,{$SVGzoom},60,0)">
                <xsl:apply-templates mode="SVG"/>
            </g>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="*[exists(svg:desc|svg:title)]" mode="SVG">
        <a xlink:href="#{distinct-values(key('elem-id-by-highlight-times', key('highlight-times-by-event-id', @id, $SVG_ELEM_HIGHLIGHT_TIMES), $EVENT_HIGHLIGHT_TIMES))[1]}" xmlns="http://www.w3.org/2000/svg">
            <xsl:copy xmlns="http://www.w3.org/2000/svg">
                <xsl:copy-of select="@*[not(local-name()='style')]"/>
                <xsl:if test="exists(@style)">
                    <xsl:attribute name="style" select="string:multi-replace(@style, ('fill:none;','stroke:#ff0000;'), ('fill:transparent;','stroke:transparent;'))"/>
                </xsl:if>
                <xsl:attribute name="title" select="concat(svg:title, ': ', svg:desc)"/>
                <xsl:attribute name="onmouseover" select="'this.style.stroke = highlightedStroke; this.style.fill = highlightedBackground;'"/>
                <xsl:attribute name="onmouseout" select="'this.style.stroke = unhighlightedFill; this.style.fill = unhighlightedFill;'"/>
                <xsl:copy-of select="namespace::*"/>
                <xsl:apply-templates mode="SVG"/>
            </xsl:copy>
        </a>
    </xsl:template>
        
    <xsl:template match="*" mode="SVG">
        <xsl:copy xmlns="http://www.w3.org/2000/svg">
            <xsl:copy-of select="@*[not(local-name()='style')]"/>
            <xsl:if test="exists(@style)">
                <xsl:attribute name="style" select="string:multi-replace(@style, ('fill:none;','stroke:#ff0000;'), ('fill:transparent;','stroke:transparent;'))"/>
            </xsl:if>
            <xsl:copy-of select="namespace::*"/>
            <xsl:apply-templates mode="SVG"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="svg:image" mode="SVG">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="xlink:href" select="concat('diagramme/', @xlink:href)"/>
            <xsl:apply-templates mode="SVG"/>
        </xsl:copy>
    </xsl:template>
        
    <xsl:template match="text()" mode="SVG">
        <xsl:copy-of select="."/>
    </xsl:template>
    
    <xsl:template name="process-exb">
        <table id="transcript">
            <tr class="timeline">
                <td></td>
                <xsl:for-each select="//common-timeline/tli">
                    <xsl:variable name="mins" select="xs:integer(floor(round-half-to-even(@time, 2) div 60))" as="xs:integer"/>
                    <xsl:variable name="secs" select="xs:integer(floor(round-half-to-even(@time, 2)) -  ($mins * 60))" as="xs:integer"/>
                    <xsl:variable name="hsecs" select="xs:integer(100 * (round-half-to-even(@time, 2) - floor(round-half-to-even(@time, 2))))" as="xs:integer"/>
                    <td id="{@id}" class="tli" onclick="mediaElem.currentTime = this.getElementsByTagName('span')[0].id;mediaElem.play();" title="play from here">
                        <xsl:value-of select="position()"/> | <span id="{round-half-to-even(@time, 2)}"><xsl:value-of select="concat('0'[$mins &lt; 10], $mins, ':', '0'[$secs &lt; 10], $secs, '.', '0'[$hsecs &lt; 10], $hsecs)"/></span>
                    </td>
                </xsl:for-each>
            </tr>
            <xsl:for-each select="//tier">
                <tr id="{@id}" class="tier {@category}">
                    <td class="tier-name"><xsl:value-of select="@display-name"/></td>
                    <xsl:apply-templates/>
                    <xsl:if test="empty(event)"><td colspan="{count($TLI_IDs)}" class="empty">.</td></xsl:if>
                </tr>
            </xsl:for-each>
        </table>
        <script type="text/javascript">
            /* event IDs in array - position = hundred's second (1=0.01) */
            eventTimes = [<xsl:for-each select="0 to xs:integer(ceiling((round-half-to-even(max(//common-timeline/tli/@time), 2) * 100))) ">[<xsl:for-each select="key('elem-id-by-highlight-times', xs:string(.), $EVENT_HIGHLIGHT_TIMES)"><xsl:value-of select="concat('''', ., '''')"/><xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>]<xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>]
            svgTimes = [<xsl:for-each select="0 to xs:integer(ceiling((round-half-to-even(max(//common-timeline/tli/@time), 2) * 100))) ">[<xsl:for-each select="key('elem-id-by-highlight-times', xs:string(.), $SVG_ELEM_HIGHLIGHT_TIMES)"><xsl:value-of select="concat('''', ., '''')"/><xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>]<xsl:if test="position()!=last()">,</xsl:if></xsl:for-each>]
        </script>
    </xsl:template>
    
    <xsl:template match="event">
        <xsl:for-each select="if(exists(preceding-sibling::event[1])) 
            then (index-of($TLI_IDs, @start) - index-of($TLI_IDs, preceding-sibling::event[1]/@end))[.&gt;0]
                else (index-of($TLI_IDs, @start) - 1)[.&gt;0]">
            <td class="empty">
                <xsl:if test=".&gt;1"><xsl:attribute name="colspan" select="."/></xsl:if>
                <xsl:value-of select="'.'"/>
            </td>
        </xsl:for-each>
        <td class="event" id="{parent::tier/@id}EV{count(preceding-sibling::event) + 1}" exb:start="{key('time-by-id', @start)}" exb:end="{key('time-by-id', @end)}">
            <xsl:if test="parent::tier/@category='ref'">
                <xsl:variable name="IDs" select="for $xpPart in tokenize(substring-after(., '#'),'xpointer\(id\(''')[position()!=1] return substring-before($xpPart, '''))')" as="xs:string+"/>
                <xsl:attribute name="onmouseover" select="for $id in $IDs return concat('svgDoc.getElementById(''', $id, ''').style.stroke = highlightedStroke; svgDoc.getElementById(''', $id, ''').style.fill = highlightedBackground; ')"/>
                <xsl:attribute name="onmouseout" select="for $id in $IDs return concat('svgDoc.getElementById(''', $id, ''').style.stroke = unhighlightedFill; svgDoc.getElementById(''', $id, ''').style.fill = unhighlightedFill; ')"/>
            </xsl:if>
            <xsl:for-each select="(index-of($TLI_IDs, @end) - index-of($TLI_IDs, @start))[.&gt;1]">
                <xsl:attribute name="colspan" select="."/>
            </xsl:for-each>
            <xsl:value-of select="."/>
        </td>
        <xsl:if test="empty(following-sibling::event[1])">
            <td class="empty">
                <xsl:for-each select="(count($TLI_IDs) - index-of($TLI_IDs, @end) + 1)">
                    <xsl:if test=". &gt; 1"><xsl:attribute name="colspan" select="."/></xsl:if>
                    <xsl:value-of select="'.'"/>
                </xsl:for-each>
            </td>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="create-overview">
        <xsl:variable name="LINE_WIDTH" select="2" as="xs:integer"/>
        <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="100%" height="{(count(//tier[exists(event)]) * 5) + 5}" viewBox="0 0 {max(for $time in key('time-by-id', //event/@start) return ($time * 2))} {(count(//tier[exists(event)]) * 5) + 5}">
            <xsl:for-each select="//tier[exists(event)]">
                <xsl:variable name="pos" select="position()" as="xs:integer"/>
                <xsl:for-each select="event[exists(@start) and exists(@end)]">
                    <line x1="{key('time-by-id', @start) * 2}" y1="{$pos * 5}" x2="{key('time-by-id', @end) * 2}" y2="{$pos * 5}" style="stroke:rgb(255,0,0);stroke-width:2" />
                </xsl:for-each>
            </xsl:for-each>            
        </svg>
    </xsl:template>
    
    <xsl:function name="string:multi-replace">
        <xsl:param name="string" as="xs:string*"/>
        <xsl:param name="replacee" as="xs:string*"/>
        <xsl:param name="replacer" as="xs:string*"/>
        <xsl:if test="$string">
            <xsl:choose>
                <xsl:when test="exists($replacee[1]) and exists($replacer[1])">
                    <xsl:copy-of select="string:multi-replace(replace($string, $replacee[1], $replacer[1]), subsequence($replacee, 2), subsequence($replacer, 2))"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$string"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:function>
    
    <xsl:variable name="SVGPanCODE">
        <xsl:comment><![CDATA[
/** 
 *  SVGPan library 1.2.1
 * ======================
 *
 * This code is licensed under the following BSD license:
 * Copyright 2009-2010 Andrea Leofreddi <a.leofreddi@itcharm.com>. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:  
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution. 
 * THIS SOFTWARE IS PROVIDED BY Andrea Leofreddi ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Andrea Leofreddi OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Andrea Leofreddi.
 */

"use strict";

var enablePan = 1; // 1 or 0: enable or disable panning (default enabled)
var enableZoom = 1; // 1 or 0: enable or disable zooming (default enabled)
var enableDrag = 0; // 1 or 0: enable or disable dragging (default disabled)

var svgDocument = document.getElementById('svgDoc');
var state = 'none', svgRoot, stateTarget, stateOrigin, stateTf;

setupHandlers(svgDocument);

/**
 * Register handlers
 */
function setupHandlers(svgDocument){
	setAttributes(svgDocument, {
		"onmouseup" : "handleMouseUp(evt)", "onmousedown" : "handleMouseDown(evt)", "onmousemove" : "handleMouseMove(evt)", "onmouseout" : "handleMouseUp(evt)", // Decomment this to stop the pan functionality when dragging out of the SVG element
	});

	if(navigator.userAgent.toLowerCase().indexOf('webkit') >= 0)
		svgDocument.addEventListener('mousewheel', handleMouseWheel, false); // Chrome/Safari
	else
		svgDocument.addEventListener('DOMMouseScroll', handleMouseWheel, false); // Others
}

/**
 * Retrieves the root element for SVG manipulation. The element is then cached into the svgRoot global variable.
 */
function getRoot(svgDocument) {
	if(typeof(svgRoot) == "undefined") {
		var g = null;
		g = svgDocument.getElementById("viewport");

		if(g == null)
			g = svgDocument.getElementsByTagName('g')[0];
    if(g == null)
			alert('Unable to obtain SVG root element');
		setCTM(g, g.getCTM());

		g.removeAttribute("viewBox");
		svgRoot = g;
	}
	return svgRoot;
}

/**
 * Instance an SVGPoint object with given event coordinates.
 */
function getEventPoint(evt) {
	var p = svgDocument.createSVGPoint(); 
	p.x = evt.clientX;
	p.y = evt.clientY;      
	return p;
}

/**
 * Sets the current transform matrix of an element.
 */
function setCTM(element, matrix) {
	var s = "matrix(" + matrix.a + "," + matrix.b + "," + matrix.c + "," + matrix.d + "," + matrix.e + "," + matrix.f + ")";
  element.setAttribute("transform", s);
}

/**
 * Dumps a matrix to a string (useful for debug).
 */
function dumpMatrix(matrix) {
	var s = "[ " + matrix.a + ", " + matrix.c + ", " + matrix.e + "\n  " + matrix.b + ", " + matrix.d + ", " + matrix.f + "\n  0, 0, 1 ]";
  return s;
}

/**
 * Sets attributes of an element.
 */
function setAttributes(element, attributes){
	for (var i in attributes)
		element.setAttributeNS(null, i, attributes[i]);
}

/**
 * Handle mouse wheel event.
 */
function handleMouseWheel(evt) {
	if(!enableZoom)
		return;

	if(evt.preventDefault)
		evt.preventDefault();

	evt.returnValue = false;

	var svgDoc = evt.target.ownerDocument;
	var delta;

	if(evt.wheelDelta)
		delta = evt.wheelDelta / 3600; // Chrome/Safari
	else
		delta = evt.detail / -90; // Mozilla

	var z = 1 + delta; // Zoom factor: 0.9/1.1  
	var g = getRoot(svgDoc);	
	var p = getEventPoint(evt);  
	p = p.matrixTransform(g.getCTM().inverse());

	// Compute new scale matrix in current mouse position
	var k = svgDocument.createSVGMatrix().translate(p.x, p.y).scale(z).translate(-p.x, -p.y);

        setCTM(g, g.getCTM().multiply(k));

	if(typeof(stateTf) == "undefined")
		stateTf = g.getCTM().inverse();

	stateTf = stateTf.multiply(k.inverse());
}

/**
 * Handle mouse move event.
 */
function handleMouseMove(evt) {
	if(evt.preventDefault)
		evt.preventDefault();  
	evt.returnValue = false;  
	var svgDoc = evt.target.ownerDocument;  
	var g = getRoot(svgDoc);    
	if(state == 'pan' && enablePan) {
		// Pan mode
		var p = getEventPoint(evt).matrixTransform(stateTf);

		setCTM(g, stateTf.inverse().translate(p.x - stateOrigin.x, p.y - stateOrigin.y));
	} else if(state == 'drag' && enableDrag) {
		// Drag mode
		var p = getEventPoint(evt).matrixTransform(g.getCTM().inverse());
    setCTM(stateTarget, svgDocument.createSVGMatrix().translate(p.x - stateOrigin.x, p.y - stateOrigin.y).multiply(g.getCTM().inverse()).multiply(stateTarget.getCTM()));
    stateOrigin = p;
	}
}

/**
 * Handle click event.
 */
function handleMouseDown(evt) {
	if(evt.preventDefault)
		evt.preventDefault();
	evt.returnValue = false;  
	var svgDoc = evt.target.ownerDocument;  
	var g = getRoot(svgDoc);  
	if(
		evt.target.tagName == "svg" 
		|| !enableDrag // Pan anyway when drag is disabled and the user clicked on an element 
	) {
		// Pan mode
		state = 'pan'; 
		stateTf = g.getCTM().inverse(); 
		stateOrigin = getEventPoint(evt).matrixTransform(stateTf);
	} else {
		// Drag mode
		state = 'drag'; 
		stateTarget = evt.target; 
		stateTf = g.getCTM().inverse(); 
		stateOrigin = getEventPoint(evt).matrixTransform(stateTf);
	}
}

/**
 * Handle mouse button release event.
 */
function handleMouseUp(evt) {
	if(evt.preventDefault)
		evt.preventDefault();  
	evt.returnValue = false;  
	var svgDoc = evt.target.ownerDocument; 
	if(state == 'pan' || state == 'drag') {
		// Quit pan mode
		state = '';
	}
}
]]>
        </xsl:comment>
    </xsl:variable>
    
</xsl:stylesheet>