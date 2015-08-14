<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <style type="text/css">
                    body {font-size:10pt; font-family:sans-serif;margin:20px;}
                    span.category {font-weight:bold; padiing-right:10px;}
                    td{text-align:right; padding-left:5px; width:50px; font-family:monospace;}
                    th{text-align:right; font-size:10pt;}
                    tr.odd{background-color:rgb(220,220,220);}
                    td{border-right:1px solid rgb(220,220,220);border-left:1px solid rgb(220,220,220);}
                    td.total{font-weight:bold;}
                    table{border-collapse:collapse; }
                    span.count {font-weight:bold;}
                </style>
            </head>
            <body>
                <p>
                <table>
                    <tr>
                        <th></th>
                        <xsl:for-each select="//speaker">
                            <th>
                                <xsl:attribute name="class">
                                    <xsl:if test="position() mod 2 =0">even</xsl:if>
                                    <xsl:if test="position() mod 2 =1">odd</xsl:if>
                                </xsl:attribute>
                                <xsl:value-of select="@speaker-id"></xsl:value-of>
                            </th>
                        </xsl:for-each>
                        <th>Ohne</th>
                        <th>Gesamt</th>
                    </tr>
                    <tr class="odd">
                        <th class="category">Beiträge (Anzahl)</th>
                        <xsl:for-each select="//speaker">                            
                            <td>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id])"/>
                            </td>
                        </xsl:for-each>
                        <td>
                            <xsl:value-of select="count(//contribution[not(@speaker-reference)])"/>
                        </td>
                        <td class="total">
                            <xsl:value-of select="count(//contribution)"/>
                        </td>
                        
                    </tr>
                    <tr class="even">
                        <th class="category">Beiträge (Länge)</th>
                        <xsl:for-each select="//speaker">                            
                            <td>
                                <xsl:variable name="CONTRIBUTION-LENGTH">
                                    <xsl:element name="durs">
                                    <xsl:for-each select="//contribution[@speaker-reference=current()/@speaker-id]">
                                        <xsl:element name="dur">
                                            <xsl:value-of select="//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time - //timepoint[@timepoint-id=current()/@start-reference]/@absolute-time"/><xsl:text>  </xsl:text>
                                        </xsl:element>
                                    </xsl:for-each>
                                    </xsl:element>
                                </xsl:variable>
                                <xsl:value-of select="format-number(sum($CONTRIBUTION-LENGTH/descendant-or-self::dur/text()), '.##')"/>
                            </td>
                        </xsl:for-each>
                        <td>
                            <xsl:variable name="CONTRIBUTION-LENGTH2">
                                <xsl:element name="durs">
                                    <xsl:for-each select="//contribution[not(@speaker-reference)]">
                                        <xsl:element name="dur">
                                            <xsl:value-of select="//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time - //timepoint[@timepoint-id=current()/@start-reference]/@absolute-time"/><xsl:text>  </xsl:text>
                                        </xsl:element>
                                    </xsl:for-each>
                                </xsl:element>
                            </xsl:variable>
                            <xsl:value-of select="format-number(sum($CONTRIBUTION-LENGTH2/descendant-or-self::dur/text()), '.##')"/>
                        </td>
                        <td class="total">
                            <xsl:variable name="CONTRIBUTION-LENGTH3">
                                <xsl:element name="durs">
                                    <xsl:for-each select="//contribution">
                                        <xsl:element name="dur">
                                            <xsl:value-of select="//timepoint[@timepoint-id=current()/@end-reference]/@absolute-time - //timepoint[@timepoint-id=current()/@start-reference]/@absolute-time"/><xsl:text>  </xsl:text>
                                        </xsl:element>
                                    </xsl:for-each>
                                </xsl:element>
                            </xsl:variable>
                            <xsl:value-of select="format-number(sum($CONTRIBUTION-LENGTH3/descendant-or-self::dur/text()), '.##')"/>
                        </td>
                        
                    </tr>
                    
                    <tr class="odd">
                        <th class="category">Wörter (Token)</th>
                        <xsl:for-each select="//speaker">
                            <td>
                                <xsl:attribute name="class">
                                    <xsl:if test="position() mod 2 =0">even</xsl:if>
                                    <xsl:if test="position() mod 2 =1">odd</xsl:if>
                                </xsl:attribute>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id]/descendant::w)"/>
                            </td>                            
                        </xsl:for-each>     
                        <td><xsl:value-of select="count(//contribution[not(@speaker-reference)]/descendant::w)"/></td>                            
                        <td class="total"><xsl:value-of select="count(//contribution/descendant::w)"/></td>                                                    
                    </tr>
                    <tr class="even">
                        <th class="category">Wörter (Types)</th>
                        <xsl:for-each select="//speaker">
                                <xsl:variable name="TOKEN_COUNT">
                                    <xsl:for-each-group select="//contribution[@speaker-reference=current()/@speaker-id]/descendant::w" group-by="text()">
                                        <xsl:text>x</xsl:text>
                                    </xsl:for-each-group>
                                </xsl:variable>
                                <td>
                                    <xsl:value-of select="string-length($TOKEN_COUNT)"/>
                                </td>
                        </xsl:for-each>
                        <td></td>
                        <xsl:variable name="TOTAL_TOKEN_COUNT">
                            <xsl:for-each-group select="//contribution/descendant::w" group-by="text()">
                                <xsl:text>x</xsl:text>
                            </xsl:for-each-group>
                        </xsl:variable>
                        <td class="total">
                            <xsl:value-of select="string-length($TOTAL_TOKEN_COUNT)"></xsl:value-of>
                        </td>
                    </tr>
                    <tr class="odd">
                        <th class="category">Mikropausen</th>
                        <xsl:for-each select="//speaker">
                            <td>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id]/descendant::pause[@duration='micro'])"/>
                            </td>
                        </xsl:for-each>       
                        <td>
                            <xsl:value-of select="count(//contribution[not(@speaker-reference)]/descendant::pause[@duration='micro'])"/>                            
                        </td>
                        <td class="total">
                            <xsl:value-of select="count(//contribution/descendant::pause[@duration='micro'])"/>
                        </td>
                    </tr>
                    <tr class="even">
                        <th class="category">Nichtphonologisches</th>
                        <xsl:for-each select="//speaker">
                            <td>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id]/descendant::non-phonological)"/>
                            </td>
                        </xsl:for-each>       
                        <td>
                            <xsl:value-of select="count(//contribution[not(@speaker-reference)]/descendant::non-phonological)"/>                            
                        </td>
                        <td class="total">
                            <xsl:value-of select="count(//contribution/descendant::non-phonological)"/>
                        </td>
                    </tr>
                    <tr class="odd">
                        <th class="category">Ein- / Ausatmen</th>
                        <xsl:for-each select="//speaker">
                            <td>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id]/descendant::breathe)"/>
                            </td>
                        </xsl:for-each>       
                        <td>
                            <xsl:value-of select="count(//contribution[not(@speaker-reference)]/descendant::breathe)"/>                            
                        </td>
                        <td class="total">
                            <xsl:value-of select="count(//contribution/descendant::breathe)"/>
                        </td>
                    </tr>
                    <tr class="even">
                        <th class="category">Gemessene Pausen (Anzahl)</th>
                        <xsl:for-each select="//speaker">                            
                            <td>
                                <xsl:value-of select="count(//contribution[@speaker-reference=current()/@speaker-id]/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')])"/>
                            </td>
                        </xsl:for-each>
                        <td>
                            <xsl:value-of select="count(//contribution[not(@speaker-reference)]/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')])"/>
                        </td>
                        <td class="total">
                            <xsl:value-of select="count(//contribution/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')])"/>
                        </td>
                        
                    </tr>
                    <tr class="odd">
                        <th class="category">Gemessene Pausen (Länge)</th>
                        <xsl:for-each select="//speaker">
                            <td>
                                <xsl:value-of select="round(100*sum(//contribution[@speaker-reference=current()/@speaker-id]/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')]/@duration)) div 100.0"/>
                            </td>
                        </xsl:for-each>                            
                        <td>
                            <xsl:value-of select="round(100*sum(//contribution[not(@speaker-reference)]/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')]/@duration)) div 100.0"/>
                        </td>
                        <td class="total">
                            <xsl:value-of select="round(sum(//contribution/descendant::pause[not(@duration='micro' or @duration='short' or @duration='medium' or @duration='long')]/@duration)) div 100.0"/>
                        </td>
                    </tr>
                </table>
                </p>
                <p style="border:1px gray solid; margin-top:10px;">                        
                    <xsl:variable name="totalseconds">
                        <xsl:value-of select="//timepoint[last()]/@absolute-time - //timepoint[1]/@absolute-time"/>
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
                    <span class="count">
                        <xsl:value-of select="$hours"/>
                        <xsl:text> Stunden, </xsl:text>
                        <xsl:value-of select="$minutes"/>
                        <xsl:text> Minuten, </xsl:text>
                        <xsl:value-of select="round($seconds*100) div 100.0"/>
                        <xsl:text> Sekunden </xsl:text>
                    </span> transkribierte Gesamtlänge. 
                    <span class="count">
                        <xsl:value-of select="count(//contribution)"/>
                    </span> Beiträge insgesamt, davon 
                    <span class="count"><xsl:value-of select="count(//contribution[@parse-level='1'])"/></span> mit Syntaxfehlern und 
                    <span class="count"><xsl:value-of select="count(//contribution[@parse-level='0'])"/></span> mit Zeitfehlern.                     
                </p>                
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
