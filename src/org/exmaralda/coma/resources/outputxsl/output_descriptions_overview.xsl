<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template match="/">
        <html>
            <head>
                <xsl:call-template name="GENERATE_STYLES"/>
            </head>
            <body>
                
            </body>
        </html>
        <xsl:call-template name="GET_KEYS"><xsl:with-param name="PARENT">Communication</xsl:with-param></xsl:call-template>
        <xsl:call-template name="GET_KEYS"><xsl:with-param name="PARENT">Speaker</xsl:with-param></xsl:call-template>
        <xsl:call-template name="GET_KEYS"><xsl:with-param name="PARENT">Recording</xsl:with-param></xsl:call-template>
        <xsl:call-template name="GET_KEYS"><xsl:with-param name="PARENT">Transcription</xsl:with-param></xsl:call-template>
    </xsl:template>

    <xsl:template name="GET_KEYS">
        <xsl:param name="PARENT"/>
        <xsl:if test="//*[name()=$PARENT]/Description/Key[not(starts-with(@Name,'#'))]">
        <h1><xsl:value-of select="$PARENT"/> Descriptions</h1>
        <table>
            <tr>
                <th>Key name</th>
                <th>Key coverage</th>
                <th>Key values</th>
            </tr>
            <xsl:for-each-group select="//*[name()=$PARENT]/Description/Key[not(starts-with(@Name,'#'))]" group-by="@Name">
                <xsl:sort select="current-grouping-key()"/>                        
                <tr>
                    <td>
                        <xsl:value-of select="current-grouping-key()"/>
                    </td>
                    <td>
                        <xsl:value-of select="count(current-group())"/> / <xsl:value-of select="count(//*[name()=$PARENT])"></xsl:value-of>
                    </td>
                    <td>
                        <xsl:variable name="DISTINCT_VALUES" select="count(distinct-values(current-group()/text()))"/>
                        <i><xsl:value-of select="$DISTINCT_VALUES"/> distinct values</i><br/>
                        <xsl:for-each-group select="current-group()" group-by="text()">
                            <xsl:sort select="count(current-group())" order="descending"/>
                            <xsl:if test="$DISTINCT_VALUES&lt;8 or count(current-group())>3">                               
                                <xsl:value-of select="substring(current-grouping-key(), 0, 50)"/>
                                <xsl:if test="string-length(current-grouping-key())>50"> <i> [...]</i></xsl:if>
                                <xsl:text> (</xsl:text>
                                <xsl:value-of select="count(current-group())"/>
                                <xsl:text>)</xsl:text>
                                <br/>
                            </xsl:if>
                        </xsl:for-each-group>
                    </td>
                </tr>
            </xsl:for-each-group>
        </table>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="GENERATE_STYLES">
        <style type="text/css">
            body {
                font-family:calibri,helvetica,sans-serif;
                font-size:10pt;
            }
            th {
                border:1px solid silver;
                font-size:9pt;
                background-color:silver;
            }
            td {
                border:1px solid silver;
                font-size:9pt;
                vertical-align:top;
            }
            h1 {
                font-size:11pt;
                border:1px solid black;
                padding:3px;
                max-width:500px;
            }
        </style>
    </xsl:template>
    
    
</xsl:stylesheet>
