<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"        
    exclude-result-prefixes="xs"
    version="2.0">
    
    
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:w">
        <xsl:copy>
            <xsl:if test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='norm']">
                <xsl:attribute name="norm">
                    <xsl:variable name="id" select="@xml:id"/>
                    <xsl:choose>
                        <xsl:when test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='norm']/descendant::tei:span[@from=$id]">
                            <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='norm']/descendant::tei:span[@from=$id]"/>                            
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="text()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='lemma']">
                <xsl:attribute name="lemma">
                    <xsl:variable name="id" select="@xml:id"/>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='lemma']/descendant::tei:span[@from=$id]"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:if test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='pos']">
                <xsl:attribute name="pos">
                    <xsl:variable name="id" select="@xml:id"/>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='pos']/descendant::tei:span[@from=$id]"/>
                </xsl:attribute>
            </xsl:if>
            
            <!-- 08-10-2025: new for issue #538 -->
            <xsl:if test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='lang']">
                <xsl:attribute name="xml:lang">
                    <xsl:variable name="id" select="@xml:id"/>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='lang']/descendant::tei:span[@from=$id]"/>
                </xsl:attribute>
            </xsl:if>
            
            <xsl:if test="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='phon']">
                <xsl:attribute name="phon">
                    <xsl:variable name="id" select="@xml:id"/>
                    <xsl:value-of select="ancestor::tei:annotationBlock/descendant::tei:spanGrp[@type='phon']/descendant::tei:span[@from=$id]"/>
                </xsl:attribute>
            </xsl:if>

            <xsl:apply-templates select="@*|node()"/>            
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="tei:spanGrp[@type='lemma']"/>
    <xsl:template match="tei:spanGrp[@type='pos']"/>
    <xsl:template match="tei:spanGrp[@type='norm']"/>
    <xsl:template match="tei:spanGrp[@type='lang']"/>
    <xsl:template match="tei:spanGrp[@type='phon']"/>
    
</xsl:stylesheet>