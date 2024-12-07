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
                        body {font-family:'Calibri', 'Arial', sans-serif;}
                        td{
                            border-top:1px solid gray;
                        }
                        td.time {vertical-align:top; font-weight:normal; color:rgb(100,100,100); font-size:8pt;}
                        td.numbering {vertical-align:top;  font-weight:bold; color: gray;}
                        td.speaker {vertical-align:top; font-weight:bold; padding-left:3px; padding-right:3px; font-size:14pt;}
                        td.contribution {vertical-align:top;}
                        div.nn_word{
                            display:inline-table;
                            vertical-align:text-top;
                        }
                        
                        div.n_word{
                            display:inline-table;
                            vertical-align:text-top;
                            margin-bottom:5px;
                            /*border-left:1px solid gray;*/
                            /*border-right:1px solid gray;*/
                            }
                        div.word{
                            font-weight:bold;
                            font-size:14pt;
                        }
                        div.lemma{
                            font-weight:normal;
                            color:gray;
                            font-size:10pt;
                            }
                        div.pos{
                            font-weight:normal;
                            color:green;
                            font-size:10pt;
                            }
                        div.pos-sup{
                            font-weight:bold;
                            color:green;
                            font-size:10pt;
                            }
                        div.c{
                            font-weight:bold;
                            color:red;
                            font-size:10pt;
                            }
                        div.no_lemma{
                            color:gray;
                        }                     
                        span.non-pho{
                            color:blue;
                            font-size:14pt;
                        }
                        div.main{
                            position:relative;
                            top:50px;
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
                            <div class="speakername">Lemma</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('lemma','true')">show</span><span class="button" onclick="toggleVisibility('lemma','false')">hide</span></div>
                        </div>
                        <div class="indcontrol">
                            <div class="speakername">POS</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('pos','true')">show</span><span class="button" onclick="toggleVisibility('pos','false')">hide</span></div>
                        </div>
                        <div class="indcontrol">
                            <div class="speakername">POS-SUP</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('pos-sup','true')">show</span><span class="button" onclick="toggleVisibility('pos-sup','false')">hide</span></div>
                        </div>
                        <div class="indcontrol">
                            <div class="speakername">C</div>
                            <div class="buttons"><span class="button" onclick="toggleVisibility('c','true')">show</span><span class="button" onclick="toggleVisibility('c','false')">hide</span></div>
                        </div>
                    </div>
                    <div class="main">
                    <table>
                        <xsl:apply-templates select="//speaker-contribution"/>                            
                        
                    </table>
                    </div>
                </body>
        </html>
    </xsl:template>
    
    <xsl:template match="speaker-contribution">
        <tr>
            <td class="numbering">
                <xsl:variable name="NUMBER">
                    <xsl:value-of select="$COUNT_START + position()"/>
                </xsl:variable>
                <xsl:if test="($NUMBER + 0) &lt; 10"><xsl:text>0</xsl:text></xsl:if>
                <xsl:if test="($NUMBER + 0) &lt; 100"><xsl:text>0</xsl:text></xsl:if>
                <xsl:value-of select="$NUMBER"/>                
            </td>
            <td class="speaker">
                <xsl:value-of select="//speaker[@id=current()/@speaker]/abbreviation"/>
            </td>
            <td class="contribution">
                <xsl:apply-templates select="descendant::ts[not(*)] | descendant::ats | descendant::nts"/>
            </td>
        </tr>
    </xsl:template>
    
    <xsl:template match="ts[not(*)]">
        <xsl:variable name="START" select="@s"/>        
        <xsl:variable name="END" select="@e"/>        
        <xsl:element name="div">
                    <xsl:attribute name="class">n_word</xsl:attribute>
                        <xsl:element name="div">
                            <xsl:attribute name="class">word</xsl:attribute>
                             <xsl:attribute name="name">word</xsl:attribute>
                            <xsl:value-of select="text()"/>
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">lemma</xsl:attribute>
                            <xsl:attribute name="name">lemma</xsl:attribute>
                        <xsl:value-of select="ancestor::speaker-contribution/annotation[@name='lemma']/ta[@s=$START and @e=$END]/text()"/>
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">pos</xsl:attribute>
                            <xsl:attribute name="name">pos</xsl:attribute>
                            <xsl:value-of select="ancestor::speaker-contribution/annotation[@name='pos']/ta[@s=$START and @e=$END]/text()"/>
                        </xsl:element>
                        <xsl:element name="div">
                            <xsl:attribute name="class">pos-sup</xsl:attribute>
                            <xsl:attribute name="name">pos-sup</xsl:attribute>
                            <xsl:value-of select="ancestor::speaker-contribution/annotation[@name='pos-sup']/ta[@s=$START and @e=$END]/text()"/>
                        </xsl:element>
            <xsl:element name="div">
                <xsl:attribute name="class">c</xsl:attribute>
                <xsl:attribute name="name">c</xsl:attribute>
                <xsl:value-of select="ancestor::speaker-contribution/annotation[@name='c']/ta[@s=$START and @e=$END]/text()"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="ats">
        <xsl:element name="span">
            <xsl:attribute name="class">non-pho</xsl:attribute>
            <xsl:attribute name="name">non-pho</xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
        
    </xsl:template>
    
    <xsl:template match="nts">
        <xsl:element name="span">
            <xsl:attribute name="class">non-pho</xsl:attribute>
            <xsl:attribute name="name">non-pho</xsl:attribute>
            <xsl:value-of select="text()"/>
        </xsl:element>
    </xsl:template>
    
    
    
</xsl:stylesheet>
