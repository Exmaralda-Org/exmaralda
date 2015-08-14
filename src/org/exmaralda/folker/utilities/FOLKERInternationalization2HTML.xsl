<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <html>
            <head>
            </head>
            <body>
                <table>
                    <xsl:apply-templates/>
                </table>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="subdomain">
        <tr>
            <td colspan="4" style="background-color:rgb(200,200,200);">
                <xsl:value-of select="@name"/>                
            </td>
        </tr>
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="item">
        <tr>
            <td><xsl:value-of select="@id"/></td>
            <xsl:apply-templates/>
        </tr>
    </xsl:template>
    
    <xsl:template match="translation">
        <td><xsl:value-of select="@trans"/></td>
    </xsl:template>
    
</xsl:stylesheet>
