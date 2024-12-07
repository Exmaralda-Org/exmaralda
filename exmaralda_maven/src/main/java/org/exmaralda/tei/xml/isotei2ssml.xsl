<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"            
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:template match="/">
        <speak version="1.1" xmlns="http://www.w3.org/2001/10/synthesis"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://www.w3.org/2001/10/synthesis
            http://www.w3.org/TR/speech-synthesis11/synthesis.xsd"
            xml:lang="en-US">                    
            <xsl:apply-templates/>            
        </speak>
    </xsl:template>
    
    <xsl:template match="tei:annotationBlock">
        
    </xsl:template>
    
    
</xsl:stylesheet>