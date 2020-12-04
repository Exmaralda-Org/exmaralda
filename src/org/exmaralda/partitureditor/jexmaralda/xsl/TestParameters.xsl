<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : TestParameters.xsl
    Created on : 2. Dezember 2020, 13:39
    Author     : thomas.schmidt
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html"/>
    <xsl:param name="PARAMETER_1">Joe</xsl:param>
    <xsl:param name="PARAMETER_2">Jack</xsl:param>
    <xsl:param name="PARAMETER_3">Averell</xsl:param>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/">
        <html>
            <head>
                <title>TestParameters.xsl</title>
            </head>
            <body>
                <ul>
                    <li><xsl:value-of select="$PARAMETER_1"/></li>
                    <li><xsl:value-of select="$PARAMETER_2"/></li>
                    <li><xsl:value-of select="$PARAMETER_3"/></li>
                </ul>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
