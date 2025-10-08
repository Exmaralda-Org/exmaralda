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
    
    <xsl:template match="tei:annotationBlock">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
            
            <xsl:if test="descendant::tei:w/@norm">
                <tei:spanGrp type="norm">                
                    <xsl:apply-templates select="descendant::tei:w" mode="PROCESS_ATTRIBUTES">
                        <xsl:with-param name="ATTRIBUTE_NAME">norm</xsl:with-param>
                    </xsl:apply-templates>
                </tei:spanGrp>
            </xsl:if>
            
            <xsl:if test="descendant::tei:w/@lemma">
                <tei:spanGrp type="lemma">                
                    <xsl:apply-templates select="descendant::tei:w" mode="PROCESS_ATTRIBUTES">
                        <xsl:with-param name="ATTRIBUTE_NAME">lemma</xsl:with-param>
                    </xsl:apply-templates>
                </tei:spanGrp>
            </xsl:if>

            <xsl:if test="descendant::tei:w/@pos">
                <tei:spanGrp type="pos">                
                    <xsl:apply-templates select="descendant::tei:w" mode="PROCESS_ATTRIBUTES">
                        <xsl:with-param name="ATTRIBUTE_NAME">pos</xsl:with-param>
                    </xsl:apply-templates>
                </tei:spanGrp>
            </xsl:if>
            
            <xsl:if test="descendant::tei:w/@phon">
                <tei:spanGrp type="phon">                
                    <xsl:apply-templates select="descendant::tei:w" mode="PROCESS_ATTRIBUTES">
                        <xsl:with-param name="ATTRIBUTE_NAME">phon</xsl:with-param>
                    </xsl:apply-templates>
                </tei:spanGrp>
            </xsl:if>
            
            <!-- 08-10-2025: new for issue #538 -->
            <xsl:if test="descendant::tei:w/@xml:lang">
                <tei:spanGrp type="lang">                
                    <xsl:apply-templates select="descendant::tei:w" mode="PROCESS_ATTRIBUTES">
                        <xsl:with-param name="ATTRIBUTE_NAME">xml:lang</xsl:with-param>
                    </xsl:apply-templates>
                </tei:spanGrp>
            </xsl:if>

        </xsl:copy>
    </xsl:template>
        
    <xsl:template match="tei:w" mode="PROCESS_ATTRIBUTES">
        <xsl:param name="ATTRIBUTE_NAME"/>
        <tei:span>
            <xsl:attribute name="from" select="@xml:id"/>
            <xsl:attribute name="to" select="@xml:id"/>
            <xsl:variable name="CONTENT" select="@*[name()=$ATTRIBUTE_NAME]"/>
            <xsl:choose>
                <xsl:when test="not(contains($CONTENT, ' '))"><xsl:value-of select="$CONTENT"/></xsl:when>
                <xsl:otherwise>
                    <xsl:for-each select="tokenize($CONTENT, ' ')">
                        <tei:span><xsl:value-of select="current()"/></tei:span>
                    </xsl:for-each>
                </xsl:otherwise>
            </xsl:choose>
        </tei:span>
    </xsl:template>
    
    <xsl:template match="tei:w/@norm"/>
    <xsl:template match="tei:w/@lemma"/>
    <xsl:template match="tei:w/@pos"/>
    <xsl:template match="tei:w/@phon"/>
    <xsl:template match="tei:w/@xml:lang"/>
    
</xsl:stylesheet>