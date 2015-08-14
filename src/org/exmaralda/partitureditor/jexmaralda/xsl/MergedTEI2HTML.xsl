<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:tei="http://www.tei-c.org/ns/1.0"    
    xmlns:isoSpoken="http://iso-tei-spoken.org/ns/1.0"
    xmlns:standoff="http://standoff.proposal" 
    exclude-result-prefixes="xs"
    version="2.0">
    <xsl:output method="xhtml"/>
    <xsl:template match="/">
        <html>
            <head>
                <meta http-equiv="content-type" content="text/html; charset=utf-8"/>                
                <style type="text/css">
                    body{
                        font-size:12pt; 
                        font-family:Calibri, Arial, sans-serif;
                        background-color:#006600;
                    }
                    div.all{
                        border: 2px solid;
                        border-radius: 25px;     
                        margin:10px;
                        height:95%;
                        overflow:auto;
                        padding:10px;
                        background-color:white;
                        min-width:800px;
                    }
                    div.seg {
                        vertical-align: text-top;
                        margin-bottom: 5px;
                    }
                    div.speaker {
                        font-weight:bold;
                        float:left;
                        vertical-align: text-top;
                        margin-right: 10px;
                        min-width:60px;
                    }
                    div.anno-label{
                        font-size:9pt;
                        color:blue;
                    }
                    div.token {
                        display: inline-table;
                        vertical-align: text-top;
                        margin-bottom: 5px;
                    }                
                    div.annotation{
                        font-size: 9pt;
                    }
                    div.lemma {
                        font-size:9pt;
                    }
                    div.pos {
                        font-size:9pt;
                        font-weight:bold;
                    }
                    div.named-entities{
                        font-size:9pt;
                        font-weight:bold;
                        background-color:yellow;
                    }
                    div.non-pho {
                        color:gray;
                    }
                    div.utterance-end{
                        color:blue;
                        font-weight:bold;
                        padding-right:10px;                        
                    }
                </style>
            </head>
            <body>
                <div class="all">
                    <xsl:apply-templates select="//standoff:annotationGrp"/>
                </div>
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="standoff:annotationGrp">
        <xsl:apply-templates select="tei:u"/>
    </xsl:template>
    
    <xsl:template match="tei:u">
        <xsl:choose>
            <xsl:when test="tei:seg">
                <xsl:apply-templates select="tei:seg"/>                
            </xsl:when>
            <xsl:otherwise>
                <div class="seg">
                    <div class="speaker">
                        <xsl:value-of select="id(substring-after(ancestor::standoff:annotationGrp/@who,'#'))/@n"/>
                    </div>                
                    <xsl:apply-templates select="descendant::tei:w | descendant::tei:pc | descendant::tei:incident | descendant::tei:pause"/>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="tei:seg">
        <div class="seg">
            <div class="speaker">
                <xsl:if test="preceding-sibling::tei:seg"><xsl:attribute name="style">color:gray;</xsl:attribute></xsl:if>
                <xsl:value-of select="id(substring-after(ancestor::standoff:annotationGrp/@who,'#'))/@n"/>
            </div>
                
            <xsl:apply-templates select="descendant::tei:w | descendant::tei:pc | descendant::tei:incident | descendant::tei:pause"/>
            <div class="token">
                <div class="utterance-end">
                    <xsl:choose>
                        <xsl:when test="@subtype='declarative'">.</xsl:when>
                        <xsl:when test="@subtype='exclamative'">!</xsl:when>
                        <xsl:when test="@subtype='interrogative'">?</xsl:when>
                        <xsl:when test="@subtype='interrupted'">...</xsl:when>
                        <xsl:when test="@subtype='modeless'"></xsl:when>
                        <xsl:otherwise></xsl:otherwise>
                    </xsl:choose>
                </div>
            </div>
            <div class="token">
                <div class="word">
                    <xsl:text>&#160;</xsl:text>
                </div>
                <xsl:for-each-group select="ancestor::standoff:annotationGrp[1]/descendant::tei:spanGrp" group-by="@type">
                    <xsl:sort select="current-grouping-key()"/>
                    <div class="anno-label">
                        <xsl:value-of select="current-grouping-key()"/>
                    </div>
                </xsl:for-each-group>
            </div>
            
        </div>
    </xsl:template>
    
    <xsl:template match="tei:w | tei:pc">
        <xsl:variable name="THIS_ID">#<xsl:value-of select="@xml:id"/></xsl:variable>
        <div class="token">
            <div class="word"><xsl:apply-templates/><xsl:text> </xsl:text></div>
            <!-- <div class="lemma"><xsl:value-of select="ancestor::tei:annotatedU[1]/descendant::tei:spanGrp[@type='lemma']/descendant::tei:span[@from=$THIS_ID]/text()"/></div>
            <div class="pos"><xsl:value-of select="ancestor::tei:annotatedU[1]/descendant::tei:spanGrp[@type='pos']/descendant::tei:span[@from=$THIS_ID]/text()"/></div>
            <div class="named-entities"><xsl:value-of select="ancestor::tei:annotatedU[1]/descendant::tei:spanGrp[@type='named-entities']/descendant::tei:span[@from=$THIS_ID]/text()"/></div> -->
            <xsl:for-each-group select="ancestor::standoff:annotationGrp[1]/descendant::tei:spanGrp" group-by="@type">
                <xsl:sort select="current-grouping-key()"/><div class="annotation">
                    <xsl:choose>
                        <xsl:when test="ancestor::standoff:annotationGrp[1]/descendant::tei:spanGrp[@type=current-grouping-key()]/descendant::tei:span[@from=$THIS_ID]">
                            <xsl:value-of select="ancestor::standoff:annotationGrp[1]/descendant::tei:spanGrp[@type=current-grouping-key()]/descendant::tei:span[@from=$THIS_ID]/text()"/>
                        </xsl:when>
                        <xsl:otherwise>-</xsl:otherwise>
                    </xsl:choose>
                    
                </div>
            </xsl:for-each-group>
        </div>
    </xsl:template>
    
    <xsl:template match="tei:incident">
        <div class="token">
            <div class="non-pho"><xsl:text>((</xsl:text><xsl:value-of select="tei:desc"/><xsl:text>))</xsl:text></div>
        </div>
    </xsl:template>
    
    <xsl:template match="tei:pause">
        <div class="token">
            <div class="non-pho">
                <xsl:text>((</xsl:text>
                <xsl:choose>
                    <xsl:when test="ends-with(@dur,'S')"><xsl:value-of select="substring-after(substring-before(@dur, 'S'), 'PT')"/></xsl:when>
                    <xsl:when test="@type"><xsl:value-of select="@type"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="@dur"/></xsl:otherwise>
                </xsl:choose>
                
                <xsl:text>))</xsl:text>
            </div>
        </div>
    </xsl:template>
    
</xsl:stylesheet>