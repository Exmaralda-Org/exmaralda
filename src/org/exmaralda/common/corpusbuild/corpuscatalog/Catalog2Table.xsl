<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html" encoding="UTF-8"/>
    <xsl:template match="/">
        <html>
            <head>
                <style type="text/css">
                    body {
                        font-family:Calibri,Arial,Helvetica,sans-serif;
                    }
                    td, th {
                        vertical-align:top;
                        font-size:10pt;
                        border: 1px solid black;
                    }
                </style>
            </head>
            <body>
                <table>
                    <tr>
                        <th>
                                Corpus name<br/>
                                Project / Data Owner<br/>
                                Type
                         </th>
                        <th>Short description</th>
                        <th>Language(s)</th>
                        <th>Size</th>
                    </tr>
                    <tr>
                        <td colspan="4" style="background-color:rgb(220,220,220);font-weight:bold;">Spoken resources</td>
                    </tr>
                    <xsl:apply-templates select="//resource[starts-with(@type,'spoken')]">
                        <xsl:sort select="project[1]"/>
                    </xsl:apply-templates>                    
                    <tr>
                        <td colspan="4" style="background-color:rgb(220,220,220);font-weight:bold;">Written resources</td>
                    </tr>
                    <xsl:apply-templates select="//resource[starts-with(@type,'written')]">
                        <xsl:sort select="project[1]"/>
                    </xsl:apply-templates>                    
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="resource">
        <tr>
            <td><b><xsl:value-of select="full-name"/></b><br/>
                <xsl:value-of select="substring-before(project[@xml:lang='eng'],' ')"/> / 
                <xsl:value-of select="dataOwner"/>
                <br/>
                <xsl:value-of select="@type"/></td>
            <td><xsl:value-of select="short-description[@xml:lang='eng']"/></td>
            <td>
                <xsl:for-each select="descendant::language">
                    <xsl:sort select="@languageID"/>
                    <xsl:value-of select="@languageID"/>,
                </xsl:for-each>
            </td>
            <td>
                <xsl:for-each select="size">
                    <xsl:sort select="@unit"/>
                    <xsl:if test="@approximate='true'">ca. </xsl:if>
                    <xsl:value-of select="text()"/><xsl:text> </xsl:text><xsl:value-of select="@unit"/>
                    <br/>
                </xsl:for-each>
            </td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
