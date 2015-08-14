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
                <title>Metadaten-Übersicht</title>
                <style type="text/css">
                    body {
                        font-family: Calibri,Arial,sans-serif;
                    }
                    td {
                        border: 1px gray solid;
                        font-size:9pt;
                    }
                    th {
                        border: 1px black solid;
                        font-size:11pt;
                        font-weight:bold;
                        background-color:light-gray;
                    }
                </style>
            </head>
            <body>
                <h1>Übersicht FOLK-Metadaten</h1>
                <p>Zuletzt aktualisiert: <xsl:value-of select="current-dateTime()"/></p>
                <table>
                    <tr>
                        <th>Datei</th>
                        <th>Kennung</th>
                        <xsl:for-each select="/*[1]/*[1]/*[not(self::Sprecher)]">
                            <th><xsl:value-of select="name()"/></th>
                        </xsl:for-each>
                        
                        <!-- <th>Sonstige Bezeichnungen</th>
                        <th>Beschreibung</th>
                        <th>Region</th>
                        <th>Institution</th>
                        <th>Räumlichkeiten</th>
                        <th>Datum</th>
                        <th>Aufnahmebedingungen</th> -->
                    </tr>                    
                    <xsl:apply-templates/>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="event | speaker">
        <tr>
            <td><xsl:value-of select="@file"/></td>
            <td>
                <xsl:value-of select="@event-id"/>
                <xsl:value-of select="@speaker-id"/>
            </td>
            <xsl:for-each select="*[not(self::Sprecher)]">
                <td><xsl:value-of select="text()"/></td>
            </xsl:for-each>
            
        </tr>
    </xsl:template>
</xsl:stylesheet>