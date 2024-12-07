<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pers="http://www.exmaralda.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xdt="http://www.w3.org/2005/xpath-datatypes" version="2.0">
	<xsl:function name="pers:typeSafeDateToDays">
		<xsl:param name="value"/>
		<xsl:choose>
			<xsl:when test="($value castable as xs:dateTime)">
				<xsl:copy-of select="days-from-duration(xs:dateTime($value) - xs:dateTime('1900-01-01T00:00:00'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:copy-of select="0"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:function>
	<xsl:template match="/">
		<html>
		<head>
			<xsl:call-template name="generate-styles"></xsl:call-template>
		</head>
		<body>
		<table>
		<xsl:for-each-group select="//speaker" group-by="@abbreviation">
			<xsl:sort select="//Communication[Setting/Person[text()=//Speaker[Sigle=current-grouping-key()]/@Id]][1]/Description/Key[@Name='Projekt']"/>
			<xsl:sort select="//Speaker[Sigle=current-grouping-key()]/Description/Key[@Name='Familie (Pseudo)']"/>							
			<!--<xsl:sort select="substring(//Speaker[Sigle=current-grouping-key()][1]/Location[Description/Key[@Name='Typ' and text()='Geburt']][1]/Period/PeriodStart,1,4)" order="descending"/>
			<xsl:sort select="@abbreviation"/>-->
			<xsl:variable name="birthday" select="//Speaker[Sigle=current-grouping-key()][1]/Location[Description/Key[@Name='Typ' and text()='Geburt']][1]/Period/PeriodStart"/>
			<tr>
				<td>
					<h1>
						<xsl:value-of select="//Speaker[Sigle=current-grouping-key()]/Description/Key[@Name='Familie (Pseudo)']"/>
					</h1>
				</td>
				<td><h1>
				<xsl:value-of select="current-grouping-key()"/>
				<xsl:text> (</xsl:text>
				<xsl:value-of select="//Speaker[Sigle=current-grouping-key()]/Pseudo"/>
				<xsl:text>) </xsl:text>
				<span style="font-size:10pt">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="//Speaker[Sigle=current-grouping-key()]/Description/Key[@Name='Eigenschaft']"/>
					<xsl:text>, </xsl:text>
					<xsl:value-of select="//Communication[Setting/Person[text()=//Speaker[Sigle=current-grouping-key()]/@Id]][1]/Description/Key[@Name='Projekt']"/>
					<xsl:text> - </xsl:text>
					<xsl:choose>
						<xsl:when test="//Speaker[Sigle=current-grouping-key()]/Location/Description/Key[@Name='Typ' and text()='Geburt']">
							<xsl:value-of select="substring($birthday,1,4)"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>??-??-??</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</span>
				
			</h1>
			<table>
				<colgroup>
					<col width="80px"/>
					<col width="80px"/>
					<col width="80px"/>
					<col width="80px"/>
					<col width="80px"/>
					<col width="80px"/>
					<col width="80px"/>
				</colgroup>
				<tr>
					<th>Comm</th>
					<!--<th>Trans</th>-->
					<!--<th>Contr</th>-->
					<th>Utterances</th>
					<th>Words</th>
					<!--<th>MLU</th>-->
					<!--<th>Non-Pho</th>-->
					<th>Age</th>
				</tr>
				<xsl:for-each-group select="current-group()" group-by="../@communication-id">
					<xsl:sort select="pers:typeSafeDateToDays(//Communication[@Id=current-grouping-key()]/Location/Period/PeriodStart)"/>
					<xsl:variable name="communicationDate">
						<xsl:value-of select="//Communication[@Id=current-grouping-key()]/Location/Period/PeriodStart"/>
					</xsl:variable>
					<!-- <xsl:for-each select="current-group()">
						<xsl:sort select="../@transcription-name"/>
						<tr>
							<td> </td>
							<td>
								<xsl:value-of select="../@transcription-name"/>
							</td>
							<td>
								<xsl:value-of select="count[@segment-name='Speaker Contributions']/@count"/>
							</td>
							<td>
								<xsl:value-of select="count[@segment-name='Utterances']/@count"/>
							</td>
							<td>
								<xsl:value-of select="count[@segment-name='Words']/@count"/>
							</td>
							<td>
								<xsl:value-of select="round(100*(count[@segment-name='Words']/@count div count[@segment-name='Utterances']/@count)) div 100"/>
							</td>
							<td>
								<xsl:value-of
									select="count[@segment-name='Non-Phonological']/@count"/>
							</td>
						</tr>
					</xsl:for-each> -->
					<tr>
						<td class="total">
							<!--<xsl:value-of select="current-grouping-key()"/>-->
							<!--<xsl:value-of select="//Communication[@Id=current-grouping-key()]/@Name"/><br/>-->
							<xsl:value-of select="substring-before(//Communication[@Id=current-grouping-key()]/Transcription[1]/Filename,'.xml')"/>
						</td>
						<!--<td></td>
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Speaker Contributions']/@count)"></xsl:value-of>
						</td>-->
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Utterances']/@count)"></xsl:value-of>
						</td>
						<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Words']/@count)"></xsl:value-of>
						</td>
						<!--<td class="total">
							<xsl:value-of select="round(100*sum(current-group()/count[@segment-name='Words']/@count) div sum(current-group()/count[@segment-name='Utterances']/@count)) div 100"></xsl:value-of>
						</td>-->
						<!--<td class="total">
							<xsl:value-of select="sum(current-group()/count[@segment-name='Non-Phonological']/@count)"></xsl:value-of>
							</td>-->
						<td>
							<xsl:variable name="age">
								<!--<xsl:variable name="commYear" select="substring($communicationDate,1,4)"></xsl:variable>
								<xsl:variable name="birthYear" select="substring($birthday,1,4)"></xsl:variable>
								<xsl:if test="string-length($commYear)&gt;0 and string-length($birthYear)&gt;0">
									<xsl:value-of select="xs:integer($commYear) - xs:integer($birthYear)"></xsl:value-of>
									</xsl:if>-->
								<xsl:if test="($communicationDate castable as xs:dateTime) and ($birthday castable as xs:dateTime)">
									<xsl:variable name="ageInDays">
										<xsl:value-of select="days-from-duration(xs:dateTime(xs:dateTime($communicationDate)) - xs:dateTime(xs:dateTime($birthday)))"/>							
									</xsl:variable>
									<xsl:variable name="years"><xsl:value-of select="$ageInDays div 365"/></xsl:variable>
									<xsl:variable name="months"><xsl:value-of select="floor(($years - floor($years)) * 12)"/></xsl:variable>
									<xsl:value-of select="floor($years)"/> 
									<xsl:text>;</xsl:text>
									<xsl:value-of select="$months"/> 
									<!--<xsl:value-of select="year-from-dateTime($communicationDate) - year-from-dateTime($birthday)"/>
									<xsl:value-of select="mo"></xsl:value-of>-->
								</xsl:if>
							</xsl:variable>
							<xsl:value-of select="$age"/>
						</td>
					</tr>
				</xsl:for-each-group>
				<tr>
					<td class="totaltotal">
						Total
					</td>
					<!--<td></td>
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Speaker Contributions']/@count)"></xsl:value-of>
					</td>-->
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Utterances']/@count)"></xsl:value-of>
					</td>
					<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Words']/@count)"></xsl:value-of>
					</td>
					<!-- <td class="totaltotal">
						<xsl:value-of select="round(100*sum(current-group()/count[@segment-name='Words']/@count) div sum(current-group()/count[@segment-name='Utterances']/@count)) div 100"></xsl:value-of>
					</td>-->
					<!--<td class="totaltotal">
						<xsl:value-of select="sum(current-group()/count[@segment-name='Non-Phonological']/@count)"></xsl:value-of>
					</td>-->
					<td> </td>
				</tr>
			</table>
			</td>
			</tr>
		</xsl:for-each-group>
		</table>
		</body>
		</html>
	</xsl:template>
	
	<xsl:template name="generate-styles">
		<style type="text/css">
			th {background-color:rgb(100,100,100);border-width:1px; color:white; padding-right:5px}
			td {border-style:solid;border-width:1px;  text-align:right; padding-right:5px;vertical-align:top}
			td.total {font-weight:bold; background-color:rgb(200,200,200); padding-right:5px}
			td.totaltotal {font-weight:bold; background-color:rgb(200,0,0); padding-right:5px}
			h1 {font-size:12pt}
		</style>
	</xsl:template>
</xsl:stylesheet>
