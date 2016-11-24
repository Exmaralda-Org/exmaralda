<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:template match="/">

        <html>
            <head>
                <title>EXMARaLDA changes</title>
                <script>
                   <![CDATA[ 
function toggle_visibility(className) {
    elements = document.getElementsByClassName(className);
    for (var i = 0; i < elements.length; i++) {
        elements[i].style.display = elements[i].style.display == 'none' ? 'inline' : 'none';
    }
    document.getElementById(className).style.display = 'inline';
}


]]> 
                </script>
                <style type="text/css">
                    body{
                        font-family:"Courier New", Courier, monospace;
                    }
                    h1{
                        font-size:2em;
                    }
                    h2{
                        font-size:1em;
                    }
                    tr,
                    th,
                    td{
                        font-size:0.9em;
                    }
                    
                    .bug{
                        background-color:#FFCCCC;
                    }
                    .feature{
                        background-color:#99DDFF;
                    }
                    .change{
                        background-color:#CCCCCC;
                    }
                    .old{
                        display:none;
                    }</style>

            </head>
            <body>
                <h1>EXMARaLDA changes</h1>
                <xsl:for-each-group select="//change[not(@tool='folker')]" group-by="@tool">
                    <a>
                        <xsl:attribute name="href">#<xsl:value-of select="current-grouping-key()"
                            /></xsl:attribute><xsl:value-of select="current-grouping-key()"/>
                    </a><xsl:text> | </xsl:text>
                </xsl:for-each-group><br/><br/> (<span class="bug" id="bug"><a href="#"
                        onclick="toggle_visibility('bug');">bug</a>
                </span>; <span class="feature" id="feature"><a href="#"
                        onclick="toggle_visibility('feature');">feature</a>
                </span>; <span class="change" id="change"><a href="#"
                        onclick="toggle_visibility('change');">change</a>
                </span>) <xsl:for-each-group select="//change[not(@tool='folker')]" group-by="@tool">
                    <h2><a><xsl:attribute name="name"><xsl:value-of select="current-grouping-key()"
                                /></xsl:attribute></a><xsl:value-of select="current-grouping-key()"
                        /></h2>
                    <table rules="all" frame="box" width="60%">
                        <tr>
                            <th>Date</th>
                            <th>Version</th>
                            <th>Change</th>
                        </tr>
                        <xsl:for-each select="current-group()">
                            <xsl:choose>
                                <xsl:when test="number(substring(@date,9,2))>10">
                                    <tr>
                                        <td>
                                            <xsl:attribute name="class">
                                                <xsl:value-of select="@type"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="@date"/>
                                        </td>
                                        <td>
                                            <xsl:attribute name="class">
                                                <xsl:value-of select="@type"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="@version"/>
                                        </td>
                                        <td>
                                            <xsl:attribute name="class">
                                                <xsl:value-of select="@type"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="text()"/>
                                        </td>
                                    </tr>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:comment><xsl:value-of select="@date"/>;  <xsl:value-of select="@version"/>;  <xsl:value-of select="text()"/> (<xsl:value-of select="@type"/>)</xsl:comment>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </table>
                </xsl:for-each-group>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
