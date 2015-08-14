<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"  xmlns:exmaralda="http://www.exmaralda.org/xml">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title><xsl:value-of select="//full-name"/></title>
                <style type="text/css">
                    body {
                        font-family:Calibri,Arial,Helvetica,sans-serif;
                        padding-left:10%;
                        padding-right:10%;
                    }
                    td, th {
                        vertical-align:top;
                        font-size:11pt;
                        border:none;
                        padding:5px;
                    }
                    th {
                        text-align:right;
                        background-color:rgb(50,50,50);
                        color:white;
                        min-width:200px;
                        max-width:200px;
                    }
                    td {
                        background-color:rgb(230,230,230);
                    }
                    p.corpus-home{
                        font-size:1.3em;
                        text-align:left;
                        border:2px gray solid;
                        background-color:gray;
                        color:white;
                    }
                    table{
                        width:100%;
                    }
                    a.prevnext{
                        text-decoration:none;
                    }
                </style>
            </head>
            <body>
                <p style="text-align:center; padding-top:2px;"><a href="http://www.corpora.uni-hamburg.de"><img src='images/top.png'/></a></p>
                <table cellspacing="0" cellpadding="0">
                    <tr>
                    <td style="background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;vertical-align:middle;"><span style="font-size:1.5em;font-weight:bold;color:white;"><xsl:value-of select="//full-name"/></span></td>
                    <td style="text-align:right;width:50px;background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;"><a>
                        <xsl:attribute name="href"><xsl:text>de_</xsl:text><xsl:value-of select="//filename"/><xsl:text>.html</xsl:text></xsl:attribute>
                        <img src="images/fahne-de.png" title="Diese Seite auf Deutsch"/>
                    </a>
                    </td>
                    </tr>
                </table>
                <p>
                    <a class="prevnext">
                        <xsl:attribute name="href">en_overview.html</xsl:attribute>
                        <xsl:text>[Overview]</xsl:text>
                    </a>
                    <xsl:text>   </xsl:text>
                    <a class="prevnext">
                        <xsl:attribute name="href">en_<xsl:value-of select="//prev/@filename"/><xsl:text>.html</xsl:text></xsl:attribute>
                        <xsl:text>[Previous]</xsl:text>
                    </a>
                    <xsl:text>   </xsl:text>
                    <a class="prevnext">
                        <xsl:attribute name="href">en_<xsl:value-of select="//next/@filename"/><xsl:text>.html</xsl:text></xsl:attribute>
                        <xsl:text>[Next]</xsl:text>
                    </a>
                </p>
                
                <table>
                   <tr>
                       <th>Full name</th>
                       <td><xsl:value-of select="//full-name"/></td>
                   </tr>
                    <tr>
                        <th>Type</th>
                        <td><xsl:value-of select="//resource/@type"/></td>
                    </tr>
                    <tr>
                        <th>Project</th>
                        <td><xsl:value-of select="//project[@xml:lang='eng']"/></td>
                    </tr>
                    <tr>
                        <th>Data owner</th>
                        <td>
                            <xsl:for-each select="//dataOwner">
                                <xsl:value-of select="dataOwnerName"/>
                                <xsl:text> (</xsl:text>
                            <xsl:value-of select="dataOwnerEmail"/>
                            <xsl:text>)</xsl:text>
                            <xsl:if test="not(position()=last())">
                                <xsl:text>, </xsl:text>
                            </xsl:if>
                            </xsl:for-each> 
                        </td>
                    </tr>
                    <tr>
                        <th>Short description</th>
                        <td><xsl:value-of select="//short-description[@xml:lang='eng']"/></td>
                    </tr>
                    <tr>
                        <th>Keywords</th>
                        <td>
                            <xsl:for-each select="//keyword[@xml:lang='eng']">
                                <xsl:value-of select="text()"/>,                                 
                            </xsl:for-each>
                        </td>                        
                    </tr>
                    <tr>
                        <th>Language(s)</th>
                        <td>
                            <xsl:for-each select="descendant::language">
                                <xsl:if test="not(@languageName)">
                                    <xsl:value-of select="@languageID"/><xsl:text>, </xsl:text>
                                </xsl:if>
                                <xsl:if test="@languageName">
                                    <xsl:value-of select="@languageName"/><xsl:text> (</xsl:text>
                                    <xsl:value-of select="@languageID"/><xsl:text>), </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </td>                                                
                    </tr>
                    <tr>
                        <th>Reference<xsl:if test="count(//reference)>1">s</xsl:if></th>
                        <td>
                        	<xsl:for-each select="//reference"><xsl:apply-templates/><br/></xsl:for-each>
                        </td>
                    </tr>
                    <xsl:if test="contains(//resource/@type,'spoken')">
                    <tr>
                        <th>Transcription</th>
                        <td>
                            <xsl:for-each select="descendant::transcription-convention">
                                <xsl:value-of select="@type"/>
                                <xsl:text> transcription according to </xsl:text>
                                <xsl:value-of select="@system"/>
                                <xsl:if test="string-length(text())&gt;0">
                                    <xsl:text> (</xsl:text>
                                    <xsl:value-of select="text()"/>
                                    <xsl:text>)</xsl:text>
                                </xsl:if>
                                <br/>
                            </xsl:for-each>
                        </td>                                                
                    </tr>              
                    </xsl:if>                        
                    <tr>
                        <th>Annotations</th>
                        <td>
                            <xsl:for-each select="descendant::annotation">
                                <xsl:value-of select="@category"/>
                                <xsl:if test="description[@xml:lang='eng']">
                                    <xsl:text> [=</xsl:text>
                                    <xsl:value-of select="description[@xml:lang='eng']"/>
                                    <xsl:text>]</xsl:text>
                                </xsl:if>
                                <xsl:text>, </xsl:text>
                            </xsl:for-each>
                        </td>                                                
                    </tr>
                    <tr>
                        <th>Size</th>
                        <td>
                            <xsl:for-each select="descendant::size">
                                <xsl:apply-templates select="."/>
                            </xsl:for-each>
                        </td>                                                
                    </tr>
                    <tr>
                        <th>Access</th>
                        <td><xsl:apply-templates select="//access[@xml:lang='eng']"/></td>
                    </tr>
                    <xsl:if test="descendant::remark">
                         <tr>
                            <th>Remarks</th>
                            <td>
                                <xsl:for-each select="descendant::remark[@xml:lang='eng']">
                                    <xsl:value-of select="text()"/>
                                    <br/>
                                </xsl:for-each>
                            </td>
                         </tr>
                    </xsl:if>
                    <tr>
                        <th>Version(s)</th>
                        <td>
                            <xsl:for-each select="descendant::version">
                                <b><xsl:value-of select="@no"/></b>
                                <xsl:text> [</xsl:text>
                                <xsl:value-of select="@released"/>
                                <xsl:text>] - </xsl:text>
                                <xsl:value-of select="text()"/>
                                <br/>
                            </xsl:for-each>
                        </td>                                                
                    </tr>
                
                <xsl:if test="//website">
                	<tr>
                		<th>Corpus homepage:</th>
                		<td style="background-color:yellow;">
                		<a>
                            <xsl:attribute name="href"><xsl:value-of select="//website[@xml:lang='eng']/@url"/></xsl:attribute>
                            <xsl:value-of select="//website[1]/@url"/>
                        </a>
                		</td>
                	</tr>
                </xsl:if>

                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="size">
        <xsl:choose>
            <xsl:when test="@unit='recordings'">
                <xsl:if test="@approximate='true'">ca. </xsl:if><xsl:value-of select="text()"/><xsl:text> </xsl:text><xsl:value-of select="@unit"/>                
                <xsl:if test="//size[@unit='miliseconds of recording']">
                    <xsl:variable name="MILISECONDS" select="//size[@unit='miliseconds of recording']/text()"/>
                     <xsl:text> (total duration: </xsl:text>   
                     <xsl:value-of  select="exmaralda:FORMAT_TIME($MILISECONDS div 1000.0)"/>
                    <xsl:text> hours)</xsl:text>   
                </xsl:if>
                <br/>
            </xsl:when>
            <xsl:when test="@unit='miliseconds of recording'"></xsl:when>
             <xsl:otherwise>
                <xsl:if test="@approximate='true'">ca. </xsl:if><xsl:value-of select="text()"/><xsl:text> </xsl:text><xsl:value-of select="@unit"/>
                 <br/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="email">
        <a class="email">
            <xsl:attribute name="href">mailto:<xsl:value-of select="@to"/><xsl:if test="@cc">?cc=<xsl:value-of select="@cc"/></xsl:if></xsl:attribute>
            <xsl:value-of select="@to"/>
        </a>
    </xsl:template>
    
    <xsl:template match="form">
        <a class="form">
            <xsl:attribute name="href"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:value-of select="text()"/>
        </a>
    </xsl:template>
    <xsl:template match="link">
    	<a><xsl:attribute name="href"><xsl:value-of select="@url"/></xsl:attribute><xsl:value-of select="text()"/></a>
    	</xsl:template>
    <xsl:template match="hzsk-address">
        <pre>
            Hamburger Zentrum für Sprachkorpora
            Universität Hamburg
            Max Brauer-Allee 60
            D-22765 Hamburg
            GERMANY
        </pre>
    </xsl:template>
    
    <xsl:function name="exmaralda:FORMAT_TIME">
        <xsl:param name="TIME"></xsl:param>
        <xsl:variable name="totalseconds">
            <xsl:value-of select="0 + $TIME"/>
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
        <xsl:value-of select="$hours"/>
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
    </xsl:function>
    

</xsl:stylesheet>
