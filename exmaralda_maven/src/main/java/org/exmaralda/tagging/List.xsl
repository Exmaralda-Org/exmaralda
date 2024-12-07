<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd"
    version="2.0">
    <xsl:template match="/">
        <head>
            <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
            <style type="text/css">
                        body {font-family: 'Arial', sans-serif;}
                        table.forms tr:nth-child(even) {
                            background-color:rgb(240,240,240);
                        }
                        td {vertical-align:top; font-weight:normal; font-size:10pt;}
                        td.number {text-align:right;}
                        th{text-align:left}
            </style>
        </head>
        <body>
            <table class="forms">
                <tr>
                    <th></th>
                    <th>Lemma</th>
                    <th>Normal</th>
                    <th>Transkribiert</th>
                    <th>POS (original)</th>
                    <th>POS (korrigiert)</th>
                    <th>Korrigiert?</th>                    
                </tr>
                <!--                <w id="w156" pos="KOUS" lemma="dass" p-pos="1.0" pos_c="KOUS">dass</w>                -->
                <!--                <w id="w162" n="Theorie" pos="NN" lemma="Theorie" p-pos="1.0" pos_c="NN">theorie</w>                -->
                <xsl:for-each-group select="//w[@pos_c]" group-by="@lemma">
                    <xsl:sort select="@lemma"/>
                    <xsl:for-each select="current-group()">
                        <xsl:sort select="@n"/>
                        <xsl:sort select="@pos"/>
                        <xsl:sort select="@pos_c"/>
                        <tr>
                            <td class="number">
                                <xsl:if test="position()=last()">
                                    <xsl:attribute name="style">border-bottom:2px solid black;</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="position()"/>
                            </td>
                            <td><xsl:value-of select="@lemma"/></td>
                            <td><xsl:value-of select="@n"/></td>
                            <td><xsl:apply-templates select="text()"/></td>
                            <td><xsl:value-of select="@pos"/></td>
                            <td><xsl:value-of select="@pos_c"/></td>
                            <td><xsl:if test="not(@pos=@pos_c)">!!!</xsl:if></td>
                        </tr>
                    </xsl:for-each>
                </xsl:for-each-group>
            </table>
        </body>
    </xsl:template>
    
    
</xsl:stylesheet>