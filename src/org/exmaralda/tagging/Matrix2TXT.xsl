<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="text" encoding="UTF-8"/>
    <xsl:template match="/">
        <xsl:text> </xsl:text>
        <xsl:for-each-group select="//@pos" group-by=".">
            <xsl:sort select="."/>
            <xsl:text>&#9;</xsl:text><xsl:value-of select="current-grouping-key()"/>                    
        </xsl:for-each-group>
        <xsl:text>       
</xsl:text>
        <xsl:for-each-group select="//@pos" group-by=".">
            <xsl:sort select="."/>
               <xsl:value-of select="current-grouping-key()"/>
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
                     <xsl:text>&#9;</xsl:text><xsl:value-of select="$COUNT"/>
               </xsl:for-each-group>
            <xsl:text>       
</xsl:text>
        </xsl:for-each-group>
    </xsl:template>
</xsl:stylesheet>