<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exmaralda="http://www.exmaralda.org" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes" />
    <xsl:variable name="COUNT_START">
        <xsl:choose>
            <xsl:when test="//@count-start"><xsl:value-of select="number(//@count-start)"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="number('0')"/></xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:variable name="INCLUDE_HOURS">
        <xsl:choose>
            <xsl:when test="//timepoint[@absolute-time+0.0&gt;3599.99]">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    <xsl:template match="/">    
        <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                    <style type="text/css">
                        body {font-family:'Courier New', 'Courier', monospace;}
                        td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100); font-size:8pt;}
                        td.numbering {vertical-align:top;  font-weight:bold; color: gray;}
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px;}
                        td.contribution {vertical-align:top;}
                        div.nn_word{
                            display:inline-table;
                            vertical-align:text-top;
                        }
                        
                        div.n_word{
                            display:inline-table;
                            vertical-align:text-top;
                            border-left:1px solid gray;
                            border-right:1px solid gray;
                            }
                        div.normalized_word{
                            font-weight:bold;
                        }
                        div.lemma{
                            font-weight:bold;
                            color:red;
                        }
                        div.word{
                        }
                        div.no_lemma{
                            color:gray;
                        }                     
                        span.non-pho{
                            color:blue;
                        }
                        div.control{
                            text-align:center;
                            background-color:rgb(220,220,220);
                            position:fixed;
                            right:10px;
                            top:10px;
                            z-index:100;
                        }
                        
                        
                        div.indcontrol{
                            display:inline-table;
                            border-top-style:solid;
                            border-top-color:gray;
                            border-top-width:1px;
                            border-bottom-style:solid;
                            border-bottom-color:gray;
                            border-bottom-width:1px;
                            margin-left:5px;
                            margin-right:5px;
                        }                   
                        div.buttons{
                            font-variant:small-caps;
                            font-size:8pt;
                        }
                        
                        div.speakername{
                            font-family:sans-serif;
                            font-variant:small-caps;
                            font-size:10pt;
                        }
                        
                        span.button{
                            font-family:sans-serif;
                            padding-left:3px;
                            padding-right:3px;
                            cursor:pointer;
                        }
                        
                    </style>
                    <xsl:element name="script">
                        <xsl:attribute name="type">text/javascript</xsl:attribute>
                        <xsl:text disable-output-escaping="yes">
                            function toggleVisibility(name, show) {
                            <![CDATA[var els = document.getElementsByName(name);
                                for (var i=0; i < els.length; ++i){
                                    el = els[i];
                                    if (show=='false') {						
                                        el.style.display = 'none';
                                    } else {
                                        el.style.display = '';
                                    }
                                }]]>
                            }
                            </xsl:text>
                    </xsl:element>
                </head>
                <body>
                    <div class="control">
                        <div class="indcontrol">
                            <div class="speakername">Originalformen</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('word','true')">show</span><span class="button" onclick="toggleVisibility('word','false')">hide</span></div>
                        </div>
                        <div class="indcontrol">
                            <div class="speakername">Nicht normalisierte formen</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('no_lemma','true')">show</span><span class="button" onclick="toggleVisibility('no_lemma','false')">hide</span></div>
                        </div>
                        <div class="indcontrol">
                            <div class="speakername">Nicht-Phonologisches</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('non-pho','true')">show</span><span class="button" onclick="toggleVisibility('non-pho','false')">hide</span></div>
                        </div>
                    </div>
                    <table>
                        <xsl:apply-templates select="//contribution">
                            
                        </xsl:apply-templates>
                    </table>
                </body>
        </html>
    </xsl:template>
    
    <xsl:template match="contribution">
        <tr>
            <td class="time">
                <xsl:text>{</xsl:text>
                <xsl:value-of select="exmaralda:format_time(//timepoint[@timepoint-id=current()/@start-reference]/@absolute-time, $INCLUDE_HOURS)"/>
                <xsl:text>}</xsl:text>
            </td>
            <td class="numbering">
                <xsl:variable name="NUMBER">
                    <xsl:value-of select="$COUNT_START + position()"/>
                </xsl:variable>
                <xsl:if test="($NUMBER + 0) &lt; 10"><xsl:text>0</xsl:text></xsl:if>
                <xsl:if test="($NUMBER + 0) &lt; 100"><xsl:text>0</xsl:text></xsl:if>
                <xsl:value-of select="$NUMBER"/>                
            </td>
            <td class="speaker">
                <xsl:value-of select="@speaker-reference"/>
            </td>
            <td class="contribution">
                <xsl:apply-templates/>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template match="w">
            <xsl:if test="not(@n)">
                <xsl:element name="div">
                    <xsl:if test="not(@pos=@pos_c)">
                        <xsl:choose>
                            <xsl:when test="@pos_c">
                                <xsl:attribute name="style">background:yellow;</xsl:attribute>                               
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="style">background:rgb(160,160,160);</xsl:attribute>                                                               
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                    <xsl:attribute name="class">nn_word</xsl:attribute>
                    <xsl:element name="div">
                        <xsl:attribute name="class">word</xsl:attribute>
                        <xsl:attribute name="name">word</xsl:attribute>
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                    <xsl:element name="div">
                        <xsl:attribute name="class">no_lemma</xsl:attribute>
                        <xsl:attribute name="name">no_lemma</xsl:attribute>
                        <xsl:value-of select="text()"/>
                    </xsl:element>
                    <xsl:element name="div">
                        <xsl:attribute name="class">pos</xsl:attribute>
                        <xsl:attribute name="name">pos</xsl:attribute>
                        <xsl:value-of select="@pos"/>                            
                    </xsl:element>
                    <xsl:element name="div">
                        <xsl:attribute name="class">pos_c</xsl:attribute>
                        <xsl:attribute name="name">pos_c</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@pos_c">
                                <xsl:value-of select="@pos_c"/>                                                            
                            </xsl:when>
                            <xsl:otherwise>---</xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                    <xsl:element name="div">
                        <xsl:attribute name="class">p-pos</xsl:attribute>
                        <xsl:attribute name="name">p-pos</xsl:attribute>
                        <xsl:value-of select="@p-pos"/>                            
                    </xsl:element>                    
                </xsl:element>
            </xsl:if>
            <xsl:if test="@n">
                <xsl:element name="div">
                    <xsl:if test="not(@pos=@pos_c)">
                        <xsl:choose>
                            <xsl:when test="@pos_c">
                                <xsl:attribute name="style">background:yellow;</xsl:attribute>                               
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="style">background:rgb(160,160,160);</xsl:attribute>                                                               
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:if>
                    <xsl:attribute name="class">n_word</xsl:attribute>
                        <xsl:element name="div">
                            <xsl:attribute name="class">normalized_word</xsl:attribute>
                            <xsl:attribute name="name">normalized_word</xsl:attribute>
                            <xsl:value-of select="text()"/>
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">lemma</xsl:attribute>
                            <xsl:attribute name="name">lemma</xsl:attribute>
                            <xsl:value-of select="@n"/>
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">pos</xsl:attribute>
                            <xsl:attribute name="name">pos</xsl:attribute>
                            <xsl:value-of select="@pos"/>                            
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">pos_c</xsl:attribute>
                            <xsl:attribute name="name">pos_c</xsl:attribute>
                        <xsl:choose>
                            <xsl:when test="@pos_c">
                                <xsl:value-of select="@pos_c"/>                                                            
                            </xsl:when>
                            <xsl:otherwise>---</xsl:otherwise>
                        </xsl:choose>
                   
                        </xsl:element>
                    <xsl:element name="div">
                        <xsl:attribute name="class">p-pos</xsl:attribute>
                        <xsl:attribute name="name">p-pos</xsl:attribute>
                        <xsl:value-of select="@p-pos"/>                            
                    </xsl:element>
                </xsl:element>
            </xsl:if>

    </xsl:template>
    
    <xsl:template match="pause">
        <xsl:element name="span">
            <xsl:attribute name="class">non-pho</xsl:attribute>
            <xsl:attribute name="name">non-pho</xsl:attribute>
            <xsl:choose>
                <xsl:when test="@duration='micro'">(.)</xsl:when>
                <xsl:when test="@duration='short'">(-)</xsl:when>
                <xsl:when test="@duration='medium'">(--)</xsl:when>
                <xsl:when test="@duration='long'">(---)</xsl:when>
                <xsl:otherwise>(<xsl:value-of select="@duration"/>)</xsl:otherwise>
            </xsl:choose>
        </xsl:element>
        
    </xsl:template>
    
    <xsl:template match="non-phonological">
        <xsl:element name="span">
            <xsl:attribute name="class">non-pho</xsl:attribute>
            <xsl:attribute name="name">non-pho</xsl:attribute>
            <xsl:text>((</xsl:text>
                <xsl:value-of select="@description"/>
                <xsl:text>))</xsl:text>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="uncertain">
        <span>(</span>
        <xsl:apply-templates select="w"/>
        <xsl:for-each select="alternative">
            <!-- change 04-02-2009 -->
            <xsl:text>/</xsl:text>
            <xsl:apply-templates select="w"/>
        </xsl:for-each>
        <span>)</span>
    </xsl:template>
    
    <xsl:template match="breathe">
        <xsl:element name="span">
            <xsl:attribute name="class">non-pho</xsl:attribute>
            <xsl:attribute name="name">non-pho</xsl:attribute>
            <xsl:if test="@type='in'">
                    <xsl:text>°</xsl:text>
                </xsl:if>
                <xsl:for-each select="(1 to @length)">
                    <xsl:text>h</xsl:text>
                </xsl:for-each>
                <xsl:if test="@type='out'">
                    <xsl:text>°</xsl:text>
                </xsl:if>
        </xsl:element>
    </xsl:template>
    
    
    
    

    <xsl:function name="exmaralda:format_time">
        <xsl:param name="time_sec"/>
        <xsl:param name="include_hours"/>
		<xsl:if test="string-length($time_sec)=0">
			<xsl:text></xsl:text>
		</xsl:if>
		<xsl:if test="string-length($time_sec)&gt;0">
			<xsl:variable name="totalseconds">
				<xsl:value-of select="0 + $time_sec"/>
			</xsl:variable>
			<xsl:variable name="hours">
				<xsl:value-of select="0 + floor($totalseconds div 3600)"/>
			</xsl:variable>
			<xsl:variable name="minutes">
				<xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
			</xsl:variable>
			<xsl:variable name="seconds">
				<xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
			</xsl:variable>
			<xsl:if test="$include_hours='true'">
                <xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
                    <xsl:text>0</xsl:text>
                    <xsl:value-of select="$hours"/>
                </xsl:if>
                <xsl:if test="$hours + 0 = 0">
                    <xsl:text>00</xsl:text>
                </xsl:if>
    			<xsl:text>:</xsl:text>
            </xsl:if>
			<xsl:if test="$minutes+0 &lt; 10">
				<xsl:text>0</xsl:text>
			</xsl:if>
			<xsl:value-of select="$minutes"/>
			<xsl:text>:</xsl:text>
			<xsl:variable name="roundsec">
				<!-- <xsl:value-of select="round($seconds*100) div 100"/> -->
                <xsl:value-of select="round($seconds)"/>
			</xsl:variable>
			<!-- changed 04-03-2010 -->
            <!-- <xsl:if test="$seconds+0 &lt; 10"> -->
            <xsl:if test="$roundsec+0 &lt; 10">
				<xsl:text>0</xsl:text>
			</xsl:if>
			<xsl:value-of select="$roundsec"/>
			<!-- <xsl:if test="string-length(substring-after($roundsec,'.'))=1">0</xsl:if>
			<xsl:if test="not(contains($roundsec,'.'))">.00</xsl:if> -->
		</xsl:if>
	</xsl:function>

    
    
</xsl:stylesheet>
