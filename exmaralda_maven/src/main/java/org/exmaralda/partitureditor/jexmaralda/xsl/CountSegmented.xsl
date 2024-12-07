<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8" omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <html>
            <head>
                <xsl:call-template name="CSS"/>
            </head>
            <body>
                <h1><xsl:value-of select="//transcription-name"/></h1>
                <table>                    
                    <tr>
                        <th class="empty"></th>
                        <xsl:for-each select="//segmented-tier">
                            <th class="tier"><xsl:value-of select="@display-name"/></th>
                        </xsl:for-each>
                        <th>Total</th>
                    </tr>
                    <tr>
                        <th>Segment chains</th>
                        <xsl:for-each select="//segmented-tier">
                            <td class="count"><xsl:value-of select="count(segmentation[1]/ts)"/></td>
                        </xsl:for-each>
                        <td class="count"><xsl:value-of select="count(//segmentation[1]/ts)"/></td>
                    </tr>
                    <xsl:for-each-group select="//segmentation/ts/descendant::*" group-by="@n">
                        <tr>
                             <th class="segment"><xsl:value-of select="current-grouping-key()"/></th>
                            <xsl:for-each select="//segmented-tier">
                                        <td class="count">
                                            <xsl:value-of select="count(descendant::*[@n=current-grouping-key()])"/>
                                        </td>
                            </xsl:for-each>
                            <td class="count">
                                <xsl:value-of select="count(current-group())"/>                                
                            </td>
                        </tr>                        
                    </xsl:for-each-group>
                    <xsl:for-each-group select="//annotation" group-by="@name">
                        <tr>
                            <th class="segment"><xsl:value-of select="current-grouping-key()"/></th>
                            <xsl:for-each select="//segmented-tier">
                                <td class="count">
                                    <xsl:value-of select="count(descendant::*[@name=current-grouping-key()]/child::*)"/>
                                </td>
                            </xsl:for-each>
                            <td class="count">
                                <xsl:value-of select="count(current-group()/child::*)"/>                                
                            </td>
                        </tr>                                                
                    </xsl:for-each-group>
                </table>
            </body>
        </html>                    
    </xsl:template>
    <xsl:template name="CSS">
        <style type="text/css">
            td{border:1px solid gray;}
            th{font-size:11pt; background-color: silver; padding-right:3px;}
            th.empty{background-color: white;}
            th.tier {text-align:left;}
            th.segment {text-align:left;}
            td.count {text-align:right; font-size:10pt;}
            body{font-family:SansSerif;}
        </style>
    </xsl:template>
</xsl:stylesheet>
