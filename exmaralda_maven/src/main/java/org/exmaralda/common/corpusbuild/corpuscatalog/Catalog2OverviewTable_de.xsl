<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <title>Korpora des Sonderforschungsbereichs 538 Mehrsprachigkeit</title>
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
                    th {
                        background-color:rgb(50,50,50);
                        color:white;
                    }
                    td.corpusname{
                        font-size:14pt;
                        padding-top:15px;
                    }
                    td.language{
                        max-width:120px;
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
                    <td style="background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;vertical-align:middle;"><span style="font-size:1.5em;font-weight:bold;color:white;">Korpora des Sonderforschungsbereichs 538 Mehrsprachigkeit</span></td>
                    <td style="text-align:right;width:50px;background-color:grey;padding:4px;spacing:0px;border:0px;margin:0px;"><a href="en_overview.html"><img src="images/fahne-en.png" title="this page in english"/></a>
                    </td>
                    </tr>
                </table>
                
                <p>
                    Die folgende Tabelle gibt einen Überblick über alle Korpora, die zwischen 1999 und 2011 am Sonderforschungsbereich 538 Mehrsprachigkeit erstellt wurden
                    und nun vom Hamburger Zentrum für Sprachkorpora (HZSK) gepflegt werden. Klicken Sie auf <span class="more" style="margin-left:0px;">Mehr Information</span>, um detaillierte Angaben
                    zur Größe eines Korpus, zu den Zugangbedingungen etc. zu erhalten. <br/>
                    Der Großteil der Korpora sind EXMARaLDA-Korpora. Um mehr über EXMARaLDA-Korpora zu erfahren, empfehlen wir einen der folgenden Schritte:
                    <ul>
                        <li>lesen Sie das Dokument <a href="http://www1.uni-hamburg.de/exmaralda/files/How_to_use_an_EXMARaLDA_corpus.pdf">How to use an EXMARaLDA corpus</a></li>
                        <li>sehen Sie sich das <a href="http://www.exmaralda.org/corpora/en_demokorpus.html">EXMARaLDA Demokorpus</a> an, das ohne Passwortschutz zugänglich ist</li>
                        <li>sehen Sie sich das <a href="en_z2_hamatac.html">Hamburg Map Task Corpus</a> an, das exemplarisch die Möglichkeiten eines mehrebenenannotierten EXMARaLDA-Korpus veranschaulicht</li>
                        <li>besuchen Sie die <a href="http://www.exmaralda.org">EXMARaLDA-Website</a></li>
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
                                Korpusname<br/>
                                <span style="font-weight:normal;">Projekt / Dateninhaber</span><br/>
                            <span style="font-weight:normal;">Typ</span>
                         </th>
                        <th><br/> <span style="font-weight:normal;">Kurzbeschreibung</span></th>
                        <th><br/> <span style="font-weight:normal;">Schlüsselwörter</span></th>
                        <th><br/> <span style="font-weight:normal;">Sprache(n)</span></th>
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
                    <xsl:attribute name="href">de_<xsl:value-of select="filename"/>.html</xsl:attribute>
                    <xsl:text>Mehr Information</xsl:text>
                </a>
            </td>
        </tr>
        <tr>
            <td class="info" align="right"><!--<b><xsl:value-of select="full-name"/></b><br/><hr/>-->
                <b>
                    <xsl:attribute name="title"> <xsl:value-of select="project[@xml:lang='deu']"/></xsl:attribute>
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
            <td class="info"><xsl:value-of select="short-description[@xml:lang='deu']"/></td>
            <td class="info">
                <xsl:for-each select="keyword[@xml:lang='deu']">
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
