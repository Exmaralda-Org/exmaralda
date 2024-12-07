<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Corpora of the Research Centre on Multilingualism (SFB 538)</title>
                <style type="text/css">
                    body {
                        font-family:Calibri,Arial,Helvetica,sans-serif;
                        padding-left:10%;
                        padding-right:10%;
                        }
                    td, th {
                        vertical-align:top;
                        font-size:9pt;
                        border: 1px solid black;
                        text-align:left;
                        border:none;
                    }
                    td.language{
                        max-width:120px;
                    }
                    th {
                        background-color:rgb(50,50,50);
                        color:white;
                    }
                    td.corpusname{
                        font-size:14pt;
                        padding-top:15px;
                    }
                    td.info{
                        background-color:rgb(230,230,230);
                        border-bottom:1px gray solid;
                    }
                    span.more, a.more {
                        text-decoration:none;
                        font-weight:bold;
                        font-size:10pt;
                        background-color:rgb(255,255,102);
                        border:1px gray solid;
                        margin-top:5px;
                        margin-left:20px;
                    }
                    table{
                        width:100%;
                    }
                </style>
            </head>
            <body>
                <p style="text-align:center; padding-top:10px;"><a href="http://www.corpora.uni-hamburg.de"><img src='images/top.png'/></a></p>
                
                 <table cellspacing="0" cellpadding="0">
                    <tr>
                    <td style="background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;vertical-align:middle;"><span style="font-size:1.5em;font-weight:bold;color:white;">Corpora of the Research Centre on Multilingualism (SFB 538)</span></td>
                    <td style="text-align:right;width:50px;background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;"><a href="de_overview.html"><img src="images/fahne-de.png" title="Diese Seite auf Deutsch"/></a>
                    </td>
                    </tr>
                </table>
                
                <p>
                    The following table gives an overview of all corpora which were constructed at the Research Centre on Multilingualism (SFB 538) between 1999 and 2011 
                    and which are now hosted by the Hamburg Centre for Language Corpora (HZSK). Click on the <span class="more" style="margin-left:0px;">More information</span> tag to obtain detailed information about the size of
                    a resource, about the procedure for getting access to it, etc. <br/>
                    Most corpora are EXMARaLDA corpora. To learn more about EXMARaLDA corpora we recommend that you
                    <ul>
                        <li>read the document <a href="http://www1.uni-hamburg.de/exmaralda/files/How_to_use_an_EXMARaLDA_corpus.pdf">How to use an EXMARaLDA corpus</a></li>
                        <li>check out the <a href="http://www.exmaralda.org/corpora/en_demokorpus.html">EXMARaLDA Demo Corpus</a> which can be used without requesting a password first</li>
                        <li>check out the <a href="en_z2_hamatac.html">Hamburg Map Task Corpus</a> as a vanilla example of a multi-level annotated EXMARaLDA corpus</li>
                        <li>visit the <a href="http://www.exmaralda.org">EXMARaLDA website</a></li>
                    </ul>
                </p>
                <table>
                    <colgroup>
                        <col/>
                        <col width="400px"/>
                        <col/>
                        <col/>
                    </colgroup>
                    <tr>
                        <th align="right">  
                                Corpus name<br/>
                                <span style="font-weight:normal;">Project / Data Owner</span><br/>
                            <span style="font-weight:normal;">Type</span>
                         </th>
                        <th><br/> <span style="font-weight:normal;">Short description</span></th>
                        <th><br/> <span style="font-weight:normal;">Keywords</span></th>
                        <th><br/> <span style="font-weight:normal;">Language(s)</span></th>
                    </tr>
                    <!-- <tr>
                        <td colspan="4" style="background-color:rgb(220,220,220);font-weight:bold;font-size:12pt;"><xsl:value-of select="count(//resource[starts-with(@type,'spoken')])"/> Spoken Resources</td>
                    </tr> -->
                    <xsl:apply-templates select="//resource[starts-with(@type,'spoken')]">
                        <xsl:sort select="@status"/>
                        <xsl:sort select="substring(project[1], 1,1)"/>
                        <xsl:sort select="tokenize(substring(project[1],2), ' ')[1]" data-type="number"/>
                    </xsl:apply-templates>                    
                    <!-- <tr>
                        <td colspan="4" style="background-color:rgb(220,220,220);font-weight:bold;font-size:12pt;"><xsl:value-of select="count(//resource[starts-with(@type,'written')])"/> Written Resources</td>
                    </tr> -->
                    <xsl:apply-templates select="//resource[starts-with(@type,'written')]">
                        <xsl:sort select="@status"/>
                        <xsl:sort select="project[1]"/>
                    </xsl:apply-templates>                    
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="resource">
        <tr>
            <td class="corpusname" colspan="4">
                <xsl:if test="website"><img src='images/check-mark.png' title='resource is available'/><xsl:text> </xsl:text></xsl:if>
                <b><xsl:value-of select="full-name"/></b>
                <a class="more">
                    <xsl:attribute name="href">en_<xsl:value-of select="filename"/>.html</xsl:attribute>
                    <xsl:text>More information</xsl:text>
                </a>
            </td>
        </tr>
        <tr>
            <td class="info" align="right"><!--<b><xsl:value-of select="full-name"/></b><br/><hr/>-->
                <b>
                    <xsl:attribute name="title"> <xsl:value-of select="project[@xml:lang='eng']"/></xsl:attribute>
                    <xsl:choose>
                        <xsl:when test="contains(filename,'_')">
                            <xsl:value-of select="upper-case(tokenize(filename,'_')[1])"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="filename"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </b> / 
                <xsl:for-each select="dataOwner">
                                <xsl:value-of select="dataOwnerName"/>                                
                            <xsl:if test="not(position()=last())">
                                <xsl:text>, </xsl:text>
                            </xsl:if>
                            </xsl:for-each>
                <br/>
                <xsl:value-of select="@type"/>
                <br/>
                <!-- <p style="text-align:right;">
                 <a class="more">
                    <xsl:attribute name="href"><xsl:value-of select="filename"/>_en.html</xsl:attribute>
                    <xsl:text>More information</xsl:text>
                </a>
                </p> -->
            </td>
            <td class="info"><xsl:value-of select="short-description[@xml:lang='eng']"/></td>
            <td class="info">
                <xsl:for-each select="keyword[@xml:lang='eng']">
                    <xsl:sort select="text()"/>
                    <xsl:value-of select="text()"/>
                    <br/>
                </xsl:for-each>
            </td>
            <td class="info language">
                <xsl:for-each select="descendant::language">
                    <xsl:sort select="@languageID"/>
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
    </xsl:template>
</xsl:stylesheet>
