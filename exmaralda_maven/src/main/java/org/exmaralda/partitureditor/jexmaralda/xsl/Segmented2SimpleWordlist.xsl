<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output encoding="UTF-8" method="html"/>
    <xsl:template match="/">
        <html>
            <head>
            </head>
            <body>
                <p>
                 <xsl:for-each select="//ts[substring-after(@n, ':')='w']">
                     <xsl:sort select="text()"/>
                     <xsl:value-of select="text()"/><br/>
                </xsl:for-each>
                </p>
            </body>
        </html>
        
    </xsl:template>
    
    
</xsl:stylesheet>
