<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <xsl:call-template name="MAKE_CSS_STYLES"></xsl:call-template>
                <xsl:call-template name="INSERT_JAVA_SCRIPT"/>                
            </head>
            <body>
                <xsl:call-template name="MAKE_TITLE"/>
                <!-- ... with one table... -->
                <div id="main">
                    <table>
                        <!-- ... and process the speaker contributions -->
                        <xsl:apply-templates select="//annotationBlock"/>
                    </table>
                    <p> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/></p>					
                </div>			
            </body>
        </html>
    </xsl:template>
    
    <!-- <gat-line start="3137.6343039999997" end="3501.824" number="007">
        <speaker><![CDATA[A:]]></speaker>
        <text><![CDATA[sicher tobi! ]]></text>
    </gat-line> -->    
    <xsl:template match="annotationBlock">
        <tr>
            <xsl:variable name="START-ID" select="@start"/>
            <xsl:variable name="END-ID" select="@end"/>
            <xsl:variable name="START-TIME" select="//when[@xml:id=$START-ID]/@interval"/>
            <xsl:variable name="END-TIME" select="//when[@xml:id=$END-ID]/@interval"/>
            <xsl:attribute name="data-start" select="$START-TIME"/>
            <xsl:attribute name="data-end" select="$END-TIME"/>
            <!-- ... with one cell for audio link ... -->
            <td class="audioLink">
                <xsl:element name="a">
                    <xsl:attribute name="onClick">
                        <xsl:text>jump('</xsl:text>
                        <xsl:value-of select="format-number(($START-TIME - 0.1), '#.##')"/><xsl:text>');</xsl:text>
                    </xsl:attribute>
                    <xsl:attribute name="title">Click to start player at <xsl:value-of select="$START-TIME"/></xsl:attribute>
                    <xsl:text>&#x00A0;</xsl:text>
                </xsl:element>
            </td>
            
            
            <!-- ... with one cell for numbering ... -->
            <td class="numbering">
                <xsl:value-of select="position()"/>
            </td>
            
            
            <!-- ... one cell for the speaker abbreviation ... -->
            <td class="abbreviation">
                <xsl:value-of select="@who"/>                
            </td>
        
            <!-- ... one cell for the text ... -->
            <td class="text">
                <span>
                    <xsl:if test="@who='Speaker_001'">
                        <xsl:attribute name="style">color:blue;</xsl:attribute>
                    </xsl:if>
                    <xsl:apply-templates select="u"/><br/>
                </span>
                <span class="translation"><xsl:value-of select="spanGrp[@type='translation']"/></span>                
            </td>
        </tr>
            
    </xsl:template>
    
    <xsl:template match="w|p">
        <xsl:apply-templates/>
        <xsl:text>&#x00A0;</xsl:text>
    </xsl:template>
    
    <!-- makes the navigation bar displayed at the top of diverse documents -->
    <xsl:template name="MAKE_TITLE">
        <div id="head">
            <xsl:call-template name="EMBED_AUDIO_PLAYER"/>            
        </div>		
    </xsl:template>
    
    
    <xsl:template name="EMBED_AUDIO_PLAYER">
        <audio controls="controls">
            <xsl:element name="source">
                <xsl:attribute name="src"><xsl:value-of select="//media/@url"/></xsl:attribute>
                <xsl:attribute name="type">audio/wav</xsl:attribute>
            </xsl:element>
        </audio>				
    </xsl:template>

    <xsl:template name="MAKE_CSS_STYLES">
        <style type="text/css">
            body {
                font-family: "Calibri", Arial, sans-serif;
                white-space:nowrap;
            }
            
            span.translation{
                font-size:smaller;
                font-style:italic;
            }
            
            div#head {
            background-color: #40627C;
            color:white;
            font-size:12pt;
            font-weight:bold;
            padding:7px;
            position:fixed;
            left:3%;
            right:3%;
            width:94%;
            z-index:100;
            }
            
            div#main {
            position:absolute;
            top:80px;
            right:0px;
            bottom:0px;
            width:98%;	
            /*height:95%;*/
            overflow:auto;	
            /*margin-bottom : 1000px*/
            }
            
            tr:nth-child(even) {
                background-color: #FFF8DC;
            }
            
            
            tr:nth-child(odd) {
                background-color: white;
            }

            
            div#footer{
            color:gray;
            border:1px solid gray;
            text-align:right;
            font-size:10pt;
            margin-top:10px;
            margin-bottom: 10px;
            position:absolute;
            right:3%;
            left:3%;
            }
            
            span#corpus-title{
            color: blue;
            }
            #head a {
            text-decoration:none;
            color:white;
            }
            #previous-doc {
            font-size: 10pt;
            }
            #next-doc {
            font-size: 10pt;
            }
            
            
            
            span.subscript {
            font-size:8pt;
            font-weight:bold;
            vertical-align:sub;
            padding-left:2px;
            padding-right:2px
            }
            
            td.audioLink {
                background-image:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQEAYAAABPYyMiAAAABGdBTUEAALGPC/xhBQAAAAFzUkdCAK7OHOkAAAAgY0hSTQAAeiYAAICEAAD6AAAAgOgAAHUwAADqYAAAOpgAABdwnLpRPAAAAAZiS0dE////////CVj33AAAAAlwSFlzAAAASAAAAEgARslrPgAAAfpJREFUSMe1VUFO20AUnRkSgVSo4wQhEErXbUyABWntLJ2eAxJvyIJcgWyBy9g3ICtChLBzBfsMtiVv/Fi9ukpkOTSqN98z89/7b+bP/yMAABCizPr++7vvA5PJ3d1kApwZnc6ZAXw92P+iaYXlPP2Iq+Jfm0iSJEkSYDQaDh0HqNWUqteBnR0pa7XNLXHkIW+pADpYlvmr3y+IWk1dP2wB0+n9/XQKLJdBsFwCcRzHcVxYztOPOPKQd1XIHwFUSsDFxfn55SUQRVEURdVHuWqJIw95HWc0cpy/BDBXPDIqLwt8c3N9PRwCaZqmaVotjDzNZqPRahVxGFfw0lAhj7CMkH693tXVzx4QhmEYhtVCyEs84wrD+PHdMIoF5rJKAO3x8dHRyQkwmz0/z2bluCDw/SAocKwawTLiAi/VpgJod3fr9b094PHx4eHpaR1PXvpr2sF+QwOUVFJKsf2npFJKCqHruq7r6+t5nud5XoyllFIqIbZOQbt9evqtDcznLy/z+edToGx78Nu2C2Wu67quW71jy+pblinEYvH29roQwjQtyzTLD9PzPM/zirE9GAzswT+U4Xh8ezseA1mWZVlWfftZJaVl+L8aEQNXNiL+lLViKmcdM5errZg7oh9xG7fiVSFUuu1jRJ7Kx6jMbvocd7tGp9v9/HP8AWIhUD3iQ2hbAAAAJXRFWHRkYXRlOmNyZWF0ZQAyMDE2LTAzLTAxVDE3OjM3OjEzKzA4OjAwpgK6zQAAACV0RVh0ZGF0ZTptb2RpZnkAMjAxNi0wMi0yMVQwMDowNTo0NyswODowMBVFaYsAAABUdEVYdHN2ZzpiYXNlLXVyaQBmaWxlOi8vL2hvbWUvZGIvc3ZnX2luZm8vc3ZnLzY3LzJiLzY3MmI2ZmM0OTY0YmUzNGI4NGFkYjhlZDcyOGU0ZDFlLnN2Z7DScMIAAAAASUVORK5CYII%3D);
                background-repeat:no-repeat;
                min-width:16px;
                min-height:16px;
                
            }
            
            td.numbering {
                color:rgb(150,150,150);
                padding-right: 5px;
            }
            
            
            table {
            margin-left:50px;
            margin-top:0px;
            margin-right:100px;
            }
            
            td {
            vertical-align:top
            }
            
            td.speaker {
                font-weight:bold;
                padding-right:8px;
                padding-left:4px
            }
            td.text {
            font-size:11pt;
            font-weight:normal;
            padding-left:8px
            }
            
            td.translation {
            font-size:9pt;
            font-weight:normal;
            color:rgb(0,0,240);
            padding-left:8px
            }
            
            
            a {
            text-decoration:none;
            }
        </style>        
    </xsl:template>
    
    <xsl:template name="INSERT_JAVA_SCRIPT">
        <script type="text/javascript">                    
            <xsl:text disable-output-escaping="yes" >
            <![CDATA[
            function jump(time){
                document.getElementsByTagName('audio')[0].currentTime=time;
                document.getElementsByTagName('audio')[0].play();
            }			
            
            function stop(){
                document.getElementsByTagName('audio')[0].pause();
            }
                        
            function registerAudioListener(){
                document.getElementsByTagName('audio')[0].addEventListener("timeupdate", updateTime, true);                     
                document.getElementsByTagName('audio')[0].addEventListener("onpause", updateTime, true);                     
            }
            
            function updateTime(){
                var player = document.getElementsByTagName('audio')[0]; 
                var elapsedTime = player.currentTime;        
                var trs = document.getElementsByTagName('tr');
                for (var i = 0; i < trs.length; i++) {
                    tr = trs[i];
                    start = tr.getAttribute('data-start');
                    end = tr.getAttribute('data-end');
                    if ((!player.paused) && (start < elapsedTime) && (end > elapsedTime)){
                        var children = tr.children;
                        for (var j = 0; j < children.length; j++) {
                          var td = children[j];
                          td.style.backgroundColor = '#E0FFFF';
                        }                        
                    } else {
                        var children = tr.children;
                        for (var j = 0; j < children.length; j++) {
                          var td = children[j];
                          td.style.backgroundColor='';
                        }                        
                    }
                }                        
            }
            window.addEventListener("DOMContentLoaded", registerAudioListener, false);
            ]]>
            </xsl:text>
        </script>                
        
    </xsl:template>
    
    
</xsl:stylesheet>