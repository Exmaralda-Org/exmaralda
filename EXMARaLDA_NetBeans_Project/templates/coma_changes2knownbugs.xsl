<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:template match="/">
		<html>
			<head>
				<title>Coma bugs and requirements</title>
				<style type="text/css">
						tr, th {						
						font-family: "Courier New", Courier, monospace;
						font-size: 11px;
						}
										
						.req {
						background-color: #FFCCCC;
						}
						.bug {
						background-color: #99DDFF;
						}
						.change {
						background-color: #CCCCCC;
						}
					
				</style>
			</head>
			<body>
				<h1>Coma bugs and requirements</h1>

					<table rules="all" frame="box" width="600px">

						<tr>
							<th>Added</th>
							<th>Bug</th>
						</tr>
						<xsl:for-each select="//bug[@tool='coma'] " >
							<xsl:sort select="@priority" order="descending"/>
							<tr>
								<xsl:attribute name="style">background-color: rgb(255,<xsl:value-of select="255-(@priority*25)"/>,<xsl:value-of select="255-(@priority*25)"/>);</xsl:attribute>

								<td>
									<xsl:value-of select="@added"/>
								</td>
								<td>
									<xsl:value-of select="text()"/>
								</td>
							</tr>
						</xsl:for-each>
						
						<tr>
							<th>Added</th>
							<th>Requirement</th>
						</tr>
						<xsl:for-each select="//req[@tool='coma'] " >
							<xsl:sort select="@priority" order="descending"/>
							<tr>
								<xsl:attribute name="style">background-color: rgb(<xsl:value-of select="255-(@priority*25)"/>,<xsl:value-of select="255-(@priority*25)"/>,255);</xsl:attribute>
								
								<td>
									<xsl:value-of select="@added"/>
								</td>
								<td>
									<xsl:value-of select="text()"/>
								</td>
							</tr>
						</xsl:for-each>
					</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
