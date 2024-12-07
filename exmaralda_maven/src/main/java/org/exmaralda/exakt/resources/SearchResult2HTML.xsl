<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
                                <xsl:call-template name="MAKE_CSS"/>
			</head>
			<body>
				<table>
					<colgroup>
						<col width="30px"/>
						<col/>
						<col/>
						<col width="30%"/>
						<col/>
						<col width="30%"/>
						<xsl:for-each select="search-result-list/search-result[1]/meta">
							<col/>
						</xsl:for-each>
					</colgroup>
					<tr>
						<th></th>
						<th>Comm</th>
						<th>Spk</th>
						<th></th>
						<th></th>
						<th></th>
						<xsl:for-each select="search-result-list/search-result[1]/meta">
							<th>
								<xsl:value-of select="@name"/>
							</th>
						</xsl:for-each>
						<xsl:for-each select="search-result-list/analyses/analysis">
							<th>
								<xsl:value-of select="@name"/>
							</th>							
						</xsl:for-each>
							
					</tr>
					<xsl:apply-templates select="//search-result"/>
				</table>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="search-result">
		<xsl:element name="tr">
			<xsl:attribute name="class">
				<xsl:if test="position() mod 2 = 0">EVEN_ROW</xsl:if>
				<xsl:if test="position() mod 2 = 1">ODD_ROW</xsl:if>
			</xsl:attribute>
			<xsl:element name="td">
				<xsl:attribute name="class">info</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="locator/@file"/>
					<xsl:text> : </xsl:text>
					<xsl:value-of select="locator/@xpath"/>
				</xsl:attribute>
				<xsl:value-of select="position()"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">communication</xsl:attribute>
				<xsl:value-of select="@communication"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">speaker</xsl:attribute>
				<xsl:value-of select="@speaker"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">
					<xsl:text>left_context</xsl:text>
					<xsl:if test="@selected='false'">
						<xsl:text>&#x020;unselected</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="left-context"/>
				</xsl:attribute>
				<xsl:value-of select="substring(left-context,string-length(left-context)-40)"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">
					<xsl:text>match_text</xsl:text>
					<xsl:if test="@selected='false'">
						<xsl:text>&#x020;unselected</xsl:text>
					</xsl:if>					
				</xsl:attribute>
				<xsl:value-of select="match"/>
			</xsl:element>
			<xsl:element name="td">
				<xsl:attribute name="class">
					<xsl:text>right_context</xsl:text>
					<xsl:if test="@selected='false'">
						<xsl:text>&#x020;unselected</xsl:text>
					</xsl:if>					
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="right-context"/>
				</xsl:attribute>
				<xsl:value-of select="substring(right-context,1,40)"/>
			</xsl:element>
			<xsl:apply-templates select="meta"/>
			<xsl:apply-templates select="data"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="meta">
		<xsl:element name="td">
			<xsl:attribute name="class">meta</xsl:attribute>
			<xsl:value-of select="text()"/>
			<!--<xsl:value-of select="translate(text(),'/', ' ')"/>-->
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="data[position()&lt;=3]">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="data[position()&gt;3]">
		<xsl:element name="td">
			<xsl:attribute name="class">data</xsl:attribute>
			<xsl:value-of select="text()"/>
			<!--<xsl:value-of select="translate(text(),'/', ' ')"/>-->
		</xsl:element>
	</xsl:template>
	
	<xsl:template name="MAKE_CSS">
		<style type="text/css">
			table {table-layout:fixed;border-collapse:collapse;border-spacing:0px}
			tr.ODD_ROW  td {background-color:rgb(240,240,240)}
			tr.EVEN_ROW  td {background-color:rgb(255,255,255)}
			th {text-align:left; background-color:rgb(100,100,100); color:rgb(255,255,255); font-weight:bold; font-size:10pt}
			td.info{font-weight:bold;  font-size:9pt; text-align:right; padding-right:10px}
			td.communication{font-size:8pt;padding-right:10px}
			td.speaker{font-weight:bold; font-size:8pt;padding-right:10px}
			td.left_context{text-align:right; font-size:10pt}
			td.right_context{text-align:left; font-size:10pt}
			td.match_text{text-align:center;font-weight:bold; font-size:10pt; color:red}
			td.meta{text-align:left; font-size:8pt; font-style:italic;padding-right:10px}
			td.data{text-align:left; font-size:8pt; font-style:italic;padding-right:10px; color:rgb(0,230,0)}
			td.unselected{text-decoration:line-through}
		</style>		
	</xsl:template>
</xsl:stylesheet>
