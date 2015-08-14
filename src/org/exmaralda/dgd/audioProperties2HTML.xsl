<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xd="http://www.oxygenxml.com/ns/doc/xsl"
    exclude-result-prefixes="xs xd"
    version="2.0">
    <xsl:output encoding="UTF-8" method="xhtml"/>
    <xd:doc scope="stylesheet">
        <xd:desc>
            <xd:p><xd:b>Created on:</xd:b> Feb 2, 2012</xd:p>
            <xd:p><xd:b>Author:</xd:b> Schmidt</xd:p>
            <xd:p></xd:p>
        </xd:desc>
    </xd:doc>
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Audiodaten-Übersicht</title>
                <style type="text/css">
                    body {
                        font-family: Calibri,Arial,sans-serif;
                    }
                    td {
                        border: 1px gray solid;
                        font-size:10pt;
                        padding:2px;
                    }
                    td.number{
                        text-align:right;
                    }
                    
                    th {
                        border: 1px black solid;
                        font-size:11pt;
                        font-weight:bold;
                        background-color:light-gray;
                        padding:2px;
                        
                    }
                </style>
            </head>
            <body>
                <h1>Übersicht Audiodaten</h1>
                <p>Zuletzt aktualisiert: <xsl:value-of select="current-dateTime()"/></p>
                <table>
                    <tr>
                        <th>Datei</th>
                        <th>Pfad</th>
                        <th>Länge (s)</th>
                        <th>Länge (mm:ss.xx)</th>
                        <th>Dateigröße (Bytes)</th>
                    </tr>                    
                    <xsl:apply-templates select="//audio-file">
                        <xsl:sort select="@name"/>
                    </xsl:apply-templates>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="audio-file">
        <tr>
            <td><xsl:value-of select="@name"/></td>
            <td><xsl:value-of select="@path"/></td>
            <td class="number"><xsl:value-of select="@seconds"/></td>
            <td class="number"><xsl:value-of select="@length"/></td>
            <td class="number"><xsl:value-of select="@bytes"/></td>            
        </tr>
    </xsl:template>
</xsl:stylesheet>