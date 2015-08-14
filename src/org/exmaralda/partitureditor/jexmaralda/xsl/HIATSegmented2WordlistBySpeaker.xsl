<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" method="html"/>
    <xsl:template match="/">
        <html>
            <head>
                <style type="text/css">
                    td {font-size:8pt;}
                    td.count{text-align:right;}
                </style>
            </head>
            <body>
                <xsl:for-each select="//segmented-tier[@speaker and @type='t']">
                    <h1>Speaker <xsl:value-of select="//speaker[@id=current()/@speaker]/abbreviation"/></h1>
                    <p>
                        <xsl:apply-templates select="segmentation[@name='SpeakerContribution_Utterance_Word']"/>
                     </p>
                </xsl:for-each>                
            </body>
        </html>
        
    </xsl:template>
    
    <xsl:template match="segmentation">
        <xsl:value-of select="count(descendant::ts[@n='HIAT:w'])"/> tokens<br/><br/>
        <table>
            <xsl:for-each-group select="descendant::ts[@n='HIAT:w']" group-by="text()">
                <xsl:sort select="text()"/>
                    <tr>
                        <td>
                            <xsl:value-of select="current-grouping-key()"/>
                        </td>
                        <td class="count">
                            <xsl:value-of select="count(current-group())"/>
                        </td>
                    </tr>
            </xsl:for-each-group>
        </table>
    </xsl:template>
    
</xsl:stylesheet>
