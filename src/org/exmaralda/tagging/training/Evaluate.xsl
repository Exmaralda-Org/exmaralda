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
            <strike style="color:gray;">Wörter insgesamt: <xsl:value-of select="count(//w)"/></strike><br/>
            Wörter insgesamt: <xsl:value-of select="exmaralda:count-tokens(/*)"/><br/>
            <strike style="color:gray;">Nicht vergleichbar: <xsl:value-of select="count(//w[not(@pos_c)])"/> (= <xsl:value-of select="count(//w[not(@pos_c)]) div count(//w) * 100"/>%)</strike><br/>
            <strike style="color:gray;">Übereinstimmung: <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c])"/> (= <xsl:value-of select="count(//w[@pos_c and @pos=@pos_c]) div count(//w[@pos_c]) * 100"/>%)</strike><br/>
            Übereinstimmung: <xsl:value-of select="exmaralda:count-agreement(/*)"/> (= <xsl:value-of select="exmaralda:count-agreement(/*) div exmaralda:count-tokens(/*) * 100"/>%)<br/>
            <strike style="color:gray;">Abweichung <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)])"/> (= <xsl:value-of select="count(//w[@pos_c and not(@pos=@pos_c)]) div count(//w[@pos_c]) * 100"/>%)</strike><br/> 
            <strike style="color:gray;">Übereinstimmung (Superkategorie): <xsl:value-of select="count(//w[@pos_c and @super=@super_c])"/> (= <xsl:value-of select="count(//w[@pos_c and @super=@super_c]) div count(//w[@pos_c]) * 100"/>%)</strike><br/>
            Übereinstimmung (Superkategorie): <xsl:value-of select="exmaralda:count-super-agreement(/*)"/> (= <xsl:value-of select="exmaralda:count-super-agreement(/*) div exmaralda:count-tokens(/*) * 100"/>%)<br/>
            <strike style="color:gray;">Abweichung (Superkategorie): <xsl:value-of select="count(//w[@pos_c and not(@super=@super_c)])"/> (= <xsl:value-of select="count(//w[@pos_c and not(@super=@super_c)]) div count(//w[@pos_c]) * 100"/>%)</strike> 
        </div>
    </xsl:template>
    <xsl:template name="ANALYSE">
        <div>
            <table class="forms">
                <th>Form</th>
                <th># Korrekturen</th>
                <th>Korrigiert in...</th>
                <xsl:for-each-group select="//w[@pos_c and not(@pos=@pos_c)]" group-by="exmaralda:normalized-form(.)">
                    <xsl:sort select="count(current-group())" order="descending"/>
                    <tr>
                        <td><xsl:value-of select="current-grouping-key()"/></td>
                        <td class="number"><xsl:value-of select="count(current-group())"/></td>
                        <td>
                            <table>
                                <xsl:for-each-group select="current-group()" group-by="@pos_c">
                                    <xsl:sort select="count(current-group())" order="descending"/>
                                    <tr>
                                        <td><xsl:value-of select="current-grouping-key()"/></td>
                                        <td class="number"><xsl:value-of select="count(current-group())"/></td>
                                    </tr>
                                </xsl:for-each-group>
                            </table>
                        </td>
                    </tr>
                </xsl:for-each-group>
            </table>
        </div>
    </xsl:template>
    
    <xsl:function name="exmaralda:count-tokens">
        <xsl:param name="ROOT"/>
        <xsl:variable name="COUNTER">
            <xsl:for-each select="$ROOT/descendant::w[not(contains(@pos,' '))]">*</xsl:for-each>
            <xsl:for-each select="$ROOT/descendant::w[contains(@pos,' ')]">
                <xsl:for-each select="tokenize(@pos,' ')">*</xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="string-length($COUNTER)"/>
    </xsl:function>
    
    <xsl:function name="exmaralda:count-agreement">
        <xsl:param name="ROOT"/>
        <xsl:variable name="COUNTER">
            <xsl:for-each select="$ROOT/descendant::w[not(contains(@pos,' '))]">
                <xsl:if test="@pos=@pos_c">*</xsl:if>
            </xsl:for-each>
            <xsl:for-each select="$ROOT/descendant::w[contains(@pos,' ')]">
                <xsl:variable name="TOKENS1">
                    <tokens>
                        <xsl:for-each select="tokenize(@pos,' ')">
                            <token><xsl:value-of select="."/></token>
                        </xsl:for-each>                        
                    </tokens>                    
                </xsl:variable>
                <xsl:variable name="TOKENS2">
                        <tokens>
                            <xsl:for-each select="tokenize(@pos_c,' ')">
                                <token><xsl:value-of select="."/></token>
                            </xsl:for-each>                        
                        </tokens>                    
                </xsl:variable>
                <xsl:for-each select="$TOKENS1/descendant::token">
                    <xsl:if test="text()=$TOKENS1/descendant::token[position()]">*</xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="string-length($COUNTER)"/>
    </xsl:function>

    <xsl:function name="exmaralda:count-super-agreement">
        <xsl:param name="ROOT"/>
        <xsl:variable name="COUNTER">
            <xsl:for-each select="$ROOT/descendant::w[not(contains(@super,' '))]">
                <xsl:if test="@super=@super_c">*</xsl:if>
            </xsl:for-each>
            <xsl:for-each select="$ROOT/descendant::w[contains(@super,' ')]">
                <xsl:variable name="TOKENS1">
                    <tokens>
                        <xsl:for-each select="tokenize(@super,' ')">
                            <token><xsl:value-of select="."/></token>
                        </xsl:for-each>                        
                    </tokens>                    
                </xsl:variable>
                <xsl:variable name="TOKENS2">
                    <tokens>
                        <xsl:for-each select="tokenize(@super_c,' ')">
                            <token><xsl:value-of select="."/></token>
                        </xsl:for-each>                        
                    </tokens>                    
                </xsl:variable>
                <xsl:for-each select="$TOKENS1/descendant::token">
                    <xsl:if test="text()=$TOKENS1/descendant::token[position()]">*</xsl:if>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:variable>
        <xsl:value-of select="string-length($COUNTER)"/>
    </xsl:function>

    <xsl:function name="exmaralda:normalized-form">
        <xsl:param name="w"/>
        <xsl:choose>
            <xsl:when test="$w/@n"><xsl:value-of select="$w/@n"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$w/text()"/></xsl:otherwise>
        </xsl:choose>
   </xsl:function>
</xsl:stylesheet>