<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" method="xml" omit-xml-declaration="yes"/>
    
    <!-- ************************ -->
    <!-- Variables Declaration -->
    <!-- ************************ -->
    
    <xsl:variable name="BASE_FILENAME"><xsl:value-of select="//unique-id/@id"/></xsl:variable>
    
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <xsl:call-template name="MAKE_CSS_STYLES"></xsl:call-template>
                <script type="text/javascript">
                    function jump(time){
                        //document.getElementsByTagName('audio')[0].currentTime=time;
                        //document.getElementsByTagName('audio')[0].play();
                        document.getElementById('player').currentTime=time;
                        document.getElementById('player').play();
                    }					
                </script>                
           </head>
            <body>
                <xsl:call-template name="MAKE_TITLE"/>
                <div id="main">
                    <xsl:apply-templates select="//it-bundle"/>
                    <p> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/></p>
                </div>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="it-bundle">
            <span class="partiturnumber">
            <xsl:for-each select="anchor">
                <xsl:element name="a">
                    <xsl:attribute name="name">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:for-each>
            <xsl:text>[</xsl:text>
            <xsl:value-of select="position()"/>
            <xsl:text>]</xsl:text>
        </span>
        <xsl:element name="table">
            <xsl:attribute name="class">partitur</xsl:attribute>
            <xsl:attribute name="width"><xsl:value-of select="round(1.4*//table-width/@table-width)"/></xsl:attribute>
            <xsl:apply-templates select="sync-points"/>
            <xsl:apply-templates select="it-line"/>
        </xsl:element>
    </xsl:template>
    

    <xsl:template match="sync-points">
        <tr>
            <!-- one empty cell above the tier labels -->
            <td class="empty-sync"/>
            <xsl:apply-templates select="sync-point"/>
        </tr>
    </xsl:template>

    <xsl:template match="sync-point">
        <td class="syncpoint">
            <xsl:element name="span">
                <xsl:attribute name="class">synchpoint-anchor</xsl:attribute>
                <xsl:value-of select="substring-before(concat(text(), ' '), ' ')"/>
            </xsl:element>
            <xsl:if test="//tli[@id=current()/@id]/@time">
                <xsl:variable name="totalseconds">
                    <xsl:value-of select="0 + //tli[@id=current()/@id]/@time"/>
                </xsl:variable>
                <xsl:element name="a">
                    <xsl:attribute name="target">seeker</xsl:attribute>
                    <xsl:attribute name="class">audioLink</xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:variable name="hours">
                                <xsl:value-of select="0 + floor($totalseconds div 3600)"/>
                            </xsl:variable>
                            <xsl:variable name="minutes">
                                <xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
                            </xsl:variable>
                            <xsl:variable name="seconds">
                                <xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
                            </xsl:variable>
                            <xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
                                <xsl:text>0</xsl:text>
                                <xsl:value-of select="$hours"/>
                            </xsl:if>
                            <xsl:if test="$hours + 0 = 0">
                                <xsl:text>00</xsl:text>                            
                            </xsl:if>
                            <xsl:text>:</xsl:text>
                            <xsl:if test="$minutes+0 &lt; 10">
                                <xsl:text>0</xsl:text>
                            </xsl:if>
                            <xsl:value-of select="$minutes"/>
                            <xsl:text>:</xsl:text>
                            <xsl:if test="$seconds+0 &lt; 10">
                                <xsl:text>0</xsl:text>
                            </xsl:if>
                            <xsl:value-of select="round($seconds*100) div 100"/>
                            <xsl:text>&#x0020;-&#x0020;Click to start player</xsl:text>
                        </xsl:attribute>
                    <xsl:attribute name="onClick">
                        <xsl:text>jump('</xsl:text>
                        <xsl:value-of select="format-number(($totalseconds - 0.1), '#.##')"/><xsl:text>');</xsl:text>
                    </xsl:attribute>
                    
                         <xsl:text>*</xsl:text>
                    </xsl:element>
            </xsl:if>
                
        </td>
    </xsl:template>

    <xsl:template match="it-line">
        <xsl:element name="tr">
            <xsl:attribute name="class">
                <xsl:value-of select="@formatref"/>
            </xsl:attribute>
            <xsl:variable name="itLinePosition">
                <xsl:value-of select="position()"/>
            </xsl:variable>
            <xsl:apply-templates select="it-label"/>
            <xsl:for-each select="../sync-points/sync-point">
                <xsl:variable name="Pos">
                    <xsl:value-of select="1+count(preceding-sibling::*)"/>
                </xsl:variable>
                <xsl:variable name="interval_is_covered">
                    <xsl:for-each select="../../it-line[$itLinePosition+0]/it-chunk">
                        <xsl:variable name="startPos">
                            <xsl:value-of
                                select="1+count(../../sync-points/sync-point[@id=current()/@start-sync]/preceding-sibling::*)"
                            />
                        </xsl:variable>
                        <xsl:variable name="endPos">
                            <xsl:value-of
                                select="1+count(../../sync-points/sync-point[@id=current()/@end-sync]/preceding-sibling::*)"
                            />
                        </xsl:variable>
                        <xsl:if test="$startPos+0&lt;=$Pos+0 and $endPos+0&gt;$Pos+0"
                        >X</xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="not(contains($interval_is_covered,'X'))">
                        <xsl:element name="td">
                            <xsl:attribute name="class">
                                <xsl:if test="count(current()/../../it-line[$itLinePosition+0]/following-sibling::*)=0">                                           
                                        <xsl:text>bottom </xsl:text>
                                </xsl:if>
                                <xsl:if test="count(current()/following-sibling::*)=0">
                                    <xsl:text>right </xsl:text>
                                </xsl:if>
                                <xsl:text>empty</xsl:text>
                            </xsl:attribute>
                            <xsl:if test="count(current()/following-sibling::*)=0">
                                <xsl:attribute name="width">100%</xsl:attribute>
                            </xsl:if>
                        </xsl:element>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates
                            select="../../it-line[$itLinePosition+0]/it-chunk[@start-sync=current()/@id]"
                        />
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template match="it-label">
        <xsl:element name="td">
            <xsl:attribute name="class">
                <xsl:if test="(count(../following-sibling::*)=0)">   
                    <xsl:text>bottom </xsl:text>
                </xsl:if>
                <xsl:text>tierlabel</xsl:text>
                <xsl:choose>
                    <xsl:when test="//tier[@id=current()/../@formatref]/@type='t'">-main</xsl:when>
                    <xsl:otherwise>-other</xsl:otherwise>
                </xsl:choose>

            </xsl:attribute>
            <xsl:value-of select="run/text()"/>
            <xsl:if test="not(string-length(run/text())=0)">
                <xsl:text>&#x00A0;&#x00A0;</xsl:text>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template match="it-chunk">
        <xsl:variable name="cellspan">
            <xsl:value-of
                select="count(../../sync-points/sync-point[@id=current()/@end-sync]/preceding-sibling::*)-count(../../sync-points/sync-point[@id=current()/@start-sync]/preceding-sibling::*)"
            />
        </xsl:variable>
        <xsl:element name="td">
            <xsl:attribute name="colspan">
                <xsl:value-of select="$cellspan"/>
            </xsl:attribute>
            <xsl:attribute name="class">
                <xsl:if test="count(current()/../following-sibling::*)=0">                                           
                    <xsl:text>bottom </xsl:text>
                </xsl:if>
                <xsl:value-of select="@formatref"/>
            </xsl:attribute>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <!-- makes the navigation bar displayed at the top of diverse documents -->
    <xsl:template name="MAKE_TITLE">
        <div id="head">
             <xsl:call-template name="EMBED_PLAYER"/>            
        </div>		
    </xsl:template>
    
    
    <xsl:template name="EMBED_PLAYER">
        <xsl:choose>
            <xsl:when test="//referenced-file[ends-with(@url,'.webm') or ends-with(@url,'.WEBM')]">
                <video controls="controls" id="player">
                    <xsl:variable name="VIDEO_FILE">
                        <xsl:value-of select="//referenced-file[ends-with(@url,'.webm') or ends-with(@url,'.WEBM')]/@url"/>
                    </xsl:variable>
                    <xsl:element name="source">
                        <xsl:attribute name="src"><xsl:value-of select="$VIDEO_FILE"/></xsl:attribute>
                        <xsl:attribute name="type">video/webm</xsl:attribute>
                    </xsl:element>
                </video>                
            </xsl:when>
            <xsl:otherwise>
                <audio controls="controls" id="player">
                    <xsl:variable name="AUDIO_FILE">
                        <xsl:value-of select="//referenced-file[ends-with(@url,'.wav') or ends-with(@url,'ogg')][1]/@url"/>
                    </xsl:variable>
                    <xsl:element name="source">
                        <xsl:attribute name="src"><xsl:value-of select="$AUDIO_FILE"/></xsl:attribute>
                        <xsl:attribute name="type">
                            <xsl:choose>
                                <xsl:when test="ends-with($AUDIO_FILE,'wav')">audio/wav</xsl:when>
                                <xsl:otherwise>audio/ogg</xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                    </xsl:element>
                </audio>				                
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    

    <xsl:template name="MAKE_CSS_STYLES">
        <style type="text/css">
            body {
            }
        
        
        div#head {
            background-color: #40627C;
            color:white;
            font-size:12pt;
            font-weight:bold;
            padding:7px;
            //position:absolute;
            //left:3%;
            //right:3%;
            //width:94%;
            z-index:100;
        }
        
        div#main {
            //position:absolute;
            //top:70px;
            height:90%;
            overflow:auto;
            width:100%;
            padding-right:10px;
            padding-left:10px;
        }
        
        
        td {
            white-space:nowrap
        }
        table.partitur {
            border-collapse:collapse;
            border-spacing:0px;
            empty-cells:show;
            margin-left:2em;	
        }
        p.partiturnumber {
            padding-top:4px;
            padding-bottom:4px;
            font-weight:bold;
            margin-left:50px
        }
        
        span.partiturnumber {
            font-weight:bold;
        }
        
        p.spacer {
            margin-top:50px
        }
        
        td.empty {
            font-size:6pt
        }
        td.empty-sync {
            border-bottom-color:rgb(153,153,153);
            border-bottom-style:Solid;
            border-bottom-width:1px;        
        }
        
        td.syncpoint {
            border-bottom-color:rgb(100,100,100);
            border-bottom-style:Solid;
            background-color:silver;
            color:white;
            border-bottom-width:1px;
        }
        
        span.synchpoint-anchor {
            font-family: Sans-Serif;
            font-size: 8pt;
            font-style: normal;
            font-weight: bold;
            color: white;
            text-decoration:none
        }
        
        
        img {
            border-width:0px;
        }
        
        
        td.tierlabel-main {
            font-family: "Times New Roman";
            font-size: 11pt;
            font-style: normal;
            font-weight: bold;
            color: rgb(0,0,0);
            border-left-color:rgb(153,153,153);
            border-left-style:Solid;
            border-left-width:1px;	 
            border-right:1px solid rgb(153,153,153);
        }

    td.tierlabel-other {
            font-family: "Times New Roman";
            font-size: 10pt;
            font-style: normal;
            font-weight: bold;
            color: rgb(200,200,200);
            /*color:white;*/
            border-left-color:rgb(153,153,153);
            border-left-style:Solid;
            border-left-width:1px;	 
            border-right:1px solid rgb(153,153,153);
        }

    td.bottom {
            border-bottom-color:rgb(153,153,153);
            border-bottom-style:Solid;
            border-bottom-width:1px;
        
        }
        
        td.right {
            border-right-color:rgb(153,153,153);
            border-right-style:Solid;
            border-right-width:1px;
        
        }
        
        td.noleftborder {
            border-left-style:None;
            border-left-width:0px;
            border-right-style:None;
            border-right-width:0px;        
        }
        
        span.audiolink {
            text-decoration:none;
        }
        
        a.audiolink {
            text-decoration:none;
        }
        </style>        
    </xsl:template>

</xsl:stylesheet>
