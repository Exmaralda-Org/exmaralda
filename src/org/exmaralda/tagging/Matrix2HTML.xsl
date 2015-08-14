<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xhtml" encoding="UTF-8"/>
    <xsl:template match="/">
        <head>
            <style type="text/css">
                body {
                    font-family: Calibri, Arial, sans-serif;
                    font-size: 8pt;
                }
                th {
                    font-size:10pt;
                }
                td {
                    text-align: center;
                    font-size: 10pt;
                }
            </style>
        </head>
        <body>
            <table>
                <tr>
                    <th>
                        <xsl:attribute name="colspan" select="count(distinct-values(//@pos))+1"/>
                        <xsl:value-of select="/matrix/@source"/>
                    </th>
                </tr>
                <tr>                    
                    <th> </th>
                    <xsl:for-each-group select="//@pos" group-by=".">
                        <xsl:sort select="."/>
                        <th><xsl:value-of select="current-grouping-key()"/></th>
                    </xsl:for-each-group>
                </tr>
                <xsl:for-each-group select="//@pos" group-by=".">
                    <xsl:sort select="."/>
                    <tr>
                        <th><xsl:value-of select="current-grouping-key()"/></th>
                        <xsl:variable name="SOURCE_POS" select="current-grouping-key()"/>
                        <xsl:for-each-group select="//@pos" group-by=".">
                            <xsl:sort select="."/>
                            <xsl:variable name="TARGET_POS" select="current-grouping-key()"/>
                            <xsl:variable name="COUNT">
                                <xsl:choose>
                                    <xsl:when test="//source[@pos=$SOURCE_POS]/target[@pos=$TARGET_POS]">
                                        <xsl:value-of select="//source[@pos=$SOURCE_POS]/target[@pos=$TARGET_POS]/@count"/>
                                    </xsl:when>
                                    <xsl:otherwise>0</xsl:otherwise>
                                </xsl:choose>                                
                            </xsl:variable>
                            <td>
                               <xsl:attribute name="style">
                                   <xsl:choose>
                                       <xsl:when test="$TARGET_POS=$SOURCE_POS">font-weight:bold;</xsl:when>
                                       <xsl:when test="$COUNT=0">color:gray;</xsl:when>
                                       <xsl:otherwise>font-weight:bold;color:red;</xsl:otherwise>
                                   </xsl:choose>
                               </xsl:attribute>
                               <xsl:attribute name="title"><xsl:value-of select="$SOURCE_POS"/>/<xsl:value-of select="$TARGET_POS"/></xsl:attribute>
                               <xsl:value-of select="$COUNT"/>
                            </td>
                        </xsl:for-each-group>
                    </tr>
                </xsl:for-each-group>
            </table>
        </body>
    </xsl:template>
</xsl:stylesheet>