<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:template match="/">
        <html>
            <body style="font-family:sans-serif;">
                <xsl:apply-templates select="//resource"/>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="resource">
        <h2><xsl:value-of select="full-name"/></h2>
        <p>
            <xsl:apply-templates select="organization[@xml:lang='eng']"/>
            <xsl:apply-templates select="project[@xml:lang='eng']"/>
        </p>
        <hr/>
        <p>
            <xsl:value-of select="short-description[@xml:lang='eng']"/>
        </p>
        <hr/>
        <p>
            <b>More information: </b>
            <xsl:element name="a">
                <xsl:attribute name="href">
                    <xsl:text>http://www.corpora.uni-hamburg.de/sfb538/</xsl:text>
                    <xsl:text>en_</xsl:text>
                    <xsl:value-of select="filename"/>
                    <xsl:text>.html</xsl:text>
                </xsl:attribute>
                <xsl:text>http://www.corpora.uni-hamburg.de/sfb538/</xsl:text>
                <xsl:text>en_</xsl:text>
                <xsl:value-of select="filename"/>
                <xsl:text>.html</xsl:text>
            </xsl:element>
        </p>
        <hr/>
        <p>
            <xsl:apply-templates select="exmaralda-coma[@type='remote']"/>
            <xsl:apply-templates select="exmaralda-rdb"/>            
        </p>
    </xsl:template>
    
    <xsl:template match="organization">
        <b>Organization: </b><xsl:value-of select="text()"/>
        <br/>                
    </xsl:template>
    
    <xsl:template match="project">
        <b>Project: </b><xsl:value-of select="text()"/>
        <br/>                
    </xsl:template>
    
    <xsl:template match="exmaralda-coma">
        <b>COMA URL: </b><xsl:value-of select="@url"/>
        <xsl:if test="@login='anonymous'"> (anonymous login)</xsl:if>
        <xsl:if test="@login='http'"> (username and password required)</xsl:if>
        <br/>        
    </xsl:template>

    <xsl:template match="exmaralda-rdb">
        <b>Database URL: </b><xsl:value-of select="@url"/> (<xsl:value-of select="@corpus-name"/>)
        <br/>        
    </xsl:template>
    


</xsl:stylesheet>
