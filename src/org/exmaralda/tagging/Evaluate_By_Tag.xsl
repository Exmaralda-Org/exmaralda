<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:exmaralda="http://www.exmaralda.org"     
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd"
    version="2.0">
    <xsl:template match="/">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <style type="text/css">
                        body {font-family: 'Arial', sans-serif;}
                        table.forms tr:nth-child(even) {
                            //background-color:rgb(210,210,210);
                        }
                        td {vertical-align:top; font-weight:normal; font-size:10pt;}
                        td.number {text-align:right;}
                        table.forms td{
                            border-top:1px solid gray;
                        }
            </style>
        </head>
        <body>
            <xsl:call-template name="COUNT_ALL"/>
            <hr/>
            <xsl:call-template name="ANALYSE"/>
        </body>
    </xsl:template>
    <xsl:template name="COUNT_ALL">
        <div>
            Wörter insgesamt: <xsl:value-of select="count(//w)"/><br/>
            Nicht vergleichbar: <xsl:value-of select="count(//w[not(@pos_c)])"/> (= <xsl:value-of select="count(//w[not(@pos_c)]) div count(//w) * 100"/>%)<br/>            
            Übereinstimmung: <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c])"/> (= <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c]) div count(//w[@pos_c]) * 100"/>%)<br/>
            Abweichung: <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)])"/> (= <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)]) div count(//w[@pos_c]) * 100"/>%)            
        </div>
    </xsl:template>
    <xsl:template name="ANALYSE">
        <div>
            <table class="forms">
                <th>Tag</th>
                <th># Korrekturen</th>
                <th>Korrigiert in...</th>
                <xsl:for-each-group select="//w[@pos_c and not(@pos=@pos_c)]" group-by="@pos">
                    <xsl:sort select="count(current-group())" order="descending"/>
                    <tr>
                        <td><xsl:value-of select="current-grouping-key()"/></td>
                        <td class="number">
                            <xsl:value-of select="count(current-group())"/>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="format-number(count(current-group()) div count(//w[@pos_c and not(@pos=@pos_c)]) * 100, '##.##')"/>                                            
                            <xsl:text>%)</xsl:text>                                                                        
                        </td>
                        <td>
                            <table>
                                <xsl:for-each-group select="current-group()" group-by="@pos_c">
                                    <xsl:sort select="count(current-group())" order="descending"/>
                                    <tr>
                                        <td><xsl:value-of select="current-grouping-key()"/></td>
                                        <td class="number">
                                            <xsl:value-of select="count(current-group())"/>
                                        </td>
                                        <td class="number">
                                            <xsl:text> (</xsl:text>
                                            <xsl:value-of select="format-number(count(current-group()) div count(//w[@pos_c and not(@pos=@pos_c)]) * 100, '##.##')"/>                                            
                                            <xsl:text>%)</xsl:text>                                            
                                        </td>
                                    </tr>
                                </xsl:for-each-group>
                            </table>
                        </td>
                    </tr>
                </xsl:for-each-group>
            </table>
        </div>
    </xsl:template>
    
    <xsl:function name="exmaralda:normalized-form">
        <xsl:param name="w"/>
        <xsl:choose>
            <xsl:when test="$w/@n"><xsl:value-of select="$w/@n"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$w/text()"/></xsl:otherwise>
        </xsl:choose>
   </xsl:function>
</xsl:stylesheet>