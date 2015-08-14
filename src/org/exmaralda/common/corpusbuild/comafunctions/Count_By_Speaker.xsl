<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pers="http://www.exmaralda.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" version="2.0">
	<xsl:template match="/">
		<html>
		<head>
			<xsl:call-template name="generate-styles"/>
		</head>
		<body>
		<h1>Corpus statistics grouped by speaker</h1>
		<table>
		<xsl:for-each-group select="//speaker" group-by="@abbreviation">
			<xsl:sort select="@abbreviation"/>
				<tr><td class="empty" colspan="5"> </td></tr>
				<tr>
				<td colspan="5" class="name">
					<xsl:value-of select="current-grouping-key()"/>		
					<!-- <xsl:text> (</xsl:text>
					<xsl:value-of select="//Speaker[Sigle=current-grouping-key()]/Description/Key[@Name='Status']"/>												
					<xsl:text>)</xsl:text> -->
				</td>
				</tr>
				<tr>
					<td class="header">Comm</td>
					<!-- <td class="header">Trans</td> -->
					<td class="header">Contr</td>
					<td class="header">Utterances</td>
					<td class="header">Words</td>
					<td class="header">Non-Pho</td>
				</tr>
				<xsl:for-each-group select="current-group()" group-by="../@communication-id">
					<xsl:sort select="../@communication-name"/>
					<tr>
						<td class="total commname">
							<!--<xsl:value-of select="current-grouping-key()"/>-->
							<xsl:value-of select="//Communication[@Id=current-grouping-key()]/@Name"></xsl:value-of>
						</td>
						<!-- <td></td> -->
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Speaker Contributions']/@count)"></xsl:value-of>
						</td>
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Utterances']/@count)"></xsl:value-of>
						</td>
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Words']/@count)"></xsl:value-of>
						</td>
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Non-Phonological']/@count)"></xsl:value-of>
						</td>
					</tr>
				</xsl:for-each-group>
				<tr>
					<td class="totaltotal">
						Total
					</td>
					<!-- <td></td> -->
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Speaker Contributions']/@count)"></xsl:value-of>
					</td>
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Utterances']/@count)"></xsl:value-of>
					</td>
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Words']/@count)"></xsl:value-of>
					</td>
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Non-Phonological']/@count)"></xsl:value-of>
					</td>
				</tr>
		</xsl:for-each-group>
				<tr><td class="empty" colspan="5"> </td></tr>

				<tr>
					<td colspan="5" class="name">Whole Corpus</td>
				</tr>
			
				<tr>
					<td class="header">Speaker</td>
					<!-- <td class="header">Trans</td> -->
					<td class="header">Contr</td>
					<td class="header">Utterances</td>
					<td class="header">Words</td>
					<td class="header">Non-Pho</td>
				</tr>
	
				<tr>
					<td bgcolor="#ffff00">All</td>
					<td bgcolor="#ffff00"><xsl:value-of select="sum(//count[@segment-name='Speaker Contributions']/@count)"></xsl:value-of></td>
					<td bgcolor="#ffff00"><xsl:value-of select="sum(//count[@segment-name='Utterances']/@count)"></xsl:value-of></td>
					<td bgcolor="#ffff00"><xsl:value-of select="sum(//count[@segment-name='Words']/@count)"></xsl:value-of></td>
					<td bgcolor="#ffff00"><xsl:value-of select="sum(//count[@segment-name='Non-Phonological']/@count)"></xsl:value-of></td>
				</tr>
		</table>
		</body>
		</html>
	</xsl:template>
	
	<xsl:template name="generate-styles">
		<style type="text/css">
			body {font-family: Calibri, Corbel, Arial, Helvetica, sans-serif;}
			table {border-collapse: collapse;}
			td {margin: 0px; padding: 0.3em; border: 1px solid; text-align: right; width: 6em;}
			td.header, td.comname {background-color:#CCCCCC;}
			td.totaltotal {font-weight:bold; background-color:#AAAAAA; }
			td.name {font-size:large; text-align: left; background-color:#ffff99; margin-top:1em; }
			td.empty {background-color:#FFFFFF; border:none;}
			h1 {font-size: large; font-weight: bold;}
			
		</style>
	</xsl:template>
</xsl:stylesheet>
