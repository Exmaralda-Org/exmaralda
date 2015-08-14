<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
	<xsl:output encoding="UTF-8" method="xhtml" omit-xml-declaration="yes"/>	

	<xsl:variable name="BASE_FILENAME"><xsl:value-of select="//unique-id/@id"/></xsl:variable>
	
	
	<xsl:template match="/">
		<html>
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<xsl:call-template name="MAKE_CSS_STYLES"></xsl:call-template>
			</head>
			<body>
				<iframe src="" width="0" height="0" name="seeker" scrolling="no" marginheight="0" marginwidth="0" frameborder="0">
					<p>Ihr Browser kann leider keine eingebetteten Frames anzeigen</p>
				</iframe>		
				<xsl:call-template name="MAKE_TITLE"/>
				<!-- ... with one table... -->
				<div id="main">
					<table>
						<!-- ... and process the speaker contributions -->
						<xsl:apply-templates select="list-transcription/list-body/speaker-contribution"/>
					</table>
					<p> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/> <br/></p>					
				</div>			
			</body>
		</html>
	</xsl:template>
	
	<!-- for the speaker contributions ... -->
	<xsl:template match="speaker-contribution">
		<!-- ... make a table row ... -->
		<xsl:variable name="EVEN_ODD">
			<xsl:if test="position() mod 2=0">even</xsl:if>
			<xsl:if test="position() mod 2&gt;0">odd</xsl:if>
		</xsl:variable>
		<tr>
			<td>
				<xsl:attribute name="class">
					<xsl:text>audiolink </xsl:text>
				</xsl:attribute>
				<!-- if this entitiy has a start point with an absolute time value... -->
				<xsl:if test="//tli[@id=current()/descendant::ts[1]/@s]/@time">
					<xsl:element name="a">
						<xsl:attribute name="href">
							<xsl:text>seeker.html?id=</xsl:text>
							<xsl:text>thisPlayer</xsl:text>
							<xsl:text>&#x0026;pos=</xsl:text>
							<xsl:value-of select="//tli[@id=current()/descendant::ts[1]/@s]/@time"/>							
						</xsl:attribute>
						<xsl:attribute name="target">seeker</xsl:attribute>
						<xsl:attribute name="title">Click to start player at <xsl:value-of select="//tli[@id=current()/descendant::ts[1]/@s]/@time"/></xsl:attribute>
						<xsl:text>*</xsl:text>
					</xsl:element>
				</xsl:if>					
			</td>
			<!-- ... with one cell for numbering ... -->
			<td>
				<xsl:attribute name="class">
					<xsl:text>numbering </xsl:text>
				</xsl:attribute>
				<!-- ... and anchors for all start timeline references included in this sc -->
				<xsl:for-each select="*//@s">
					<xsl:element name="a">
						<xsl:attribute name="name">
							<xsl:value-of select="."/>
						</xsl:attribute>
					</xsl:element>
				</xsl:for-each>
				<xsl:value-of select="position()"/>
			</td>
			<!-- ... one cell for the speaker abbreviation ... -->
			<xsl:element name="td">
				<xsl:attribute name="class">
					<xsl:text>abbreviation </xsl:text>
				</xsl:attribute>				
				<xsl:variable name="id">
					<xsl:value-of select="@speaker"/>
				</xsl:variable>
				<xsl:attribute name="class">speaker</xsl:attribute>
				<xsl:value-of select="translate(//speaker[@id = $id]/abbreviation,' ' , '&#x00A0;')"/>
			</xsl:element>
			<td>
				<xsl:attribute name="class">
					<xsl:text>text </xsl:text>
					<xsl:value-of select="$EVEN_ODD"/>
				</xsl:attribute>								
				<xsl:apply-templates select="main"/>
			</td>
			<xsl:if test="//annotation[@name='de']">
				<td>
					<xsl:attribute name="class">
						<xsl:text>translation </xsl:text>
						<xsl:value-of select="$EVEN_ODD"/>
					</xsl:attribute>								
					<xsl:apply-templates select="annotation[@name='de']"/>
				</td>
			</xsl:if>
			<xsl:if test="//annotation[@name='en']">
				<td>
					<xsl:attribute name="class">
						<xsl:text>translation </xsl:text>
						<xsl:value-of select="$EVEN_ODD"/>
					</xsl:attribute>								
					<xsl:apply-templates select="annotation[@name='en']"/>
				</td>
			</xsl:if>
		</tr>
	</xsl:template>
	
	<!-- for the mains... -->
	<xsl:template match="main">
		<xsl:apply-templates/>
	</xsl:template>
	
	
	<xsl:template match="nts | ats">
		<xsl:choose>
			<xsl:when test="text()=' '">
				<xsl:apply-templates/>				
			</xsl:when>
			<xsl:otherwise>
				<span class="non-verbal">
					<xsl:apply-templates/>
				</span>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template match="text()">
		<xsl:value-of select="."/>
		<!-- <xsl:choose>
			<xsl:when test="name(..)='ta' or name(..)='ats' or name(..)='nts' or (name(..)='ts' and ../@n='HIAT:w')">
				<xsl:value-of select="."/>
			</xsl:when>
			<xsl:otherwise>
			</xsl:otherwise>
		</xsl:choose> -->
		
		
	</xsl:template>	
	
	<xsl:template match="ts">
		<xsl:apply-templates/>
	</xsl:template>

	
	<!-- for the dependents and annotations... -->
	<xsl:template match="dependent">
		<!-- do nothing -->
	</xsl:template>
	
	<xsl:template match="annotation">
		<xsl:apply-templates/>
	</xsl:template>

	

	<!-- makes the navigation bar displayed at the top of diverse documents -->
	<xsl:template name="MAKE_TITLE">
		<div id="head">
			<xsl:call-template name="EMBED_FLASH_PLAYER"/>            
		</div>		
	</xsl:template>
	
	
	<xsl:template name="EMBED_FLASH_PLAYER">
		<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
			codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0"
			width="400" height="30" id="player" align="middle">
			<param name="allowScriptAccess" value="sameDomain" />
			<xsl:element name="param">
				<xsl:attribute name="name">movie</xsl:attribute>
				<xsl:attribute name="value">
					<xsl:text>player.swf?id=</xsl:text>
					<xsl:text>thisPlayer</xsl:text>
					<xsl:text>&#x0026;src=</xsl:text>
					<!-- <xsl:value-of select="//referenced-file/@url"/> -->
					<xsl:value-of select="//referenced-file[ends-with(@url, 'mp3') or ends-with(@url, 'MP3')]/@url"/>
					<xsl:text>&#x0026;showmins=yes&#x0026;tcol=0xFFFFFF</xsl:text>
				</xsl:attribute>
			</xsl:element>
			<param name="quality" value="high" />
			<param name="bgcolor" value="#ffffff" />
			<param name="swliveconnect" value="true"/>
			<param name="wmode" value="transparent"/>				
			<EMBED quality="high"
				swLiveConnect="true"
				bgcolor="#ffffff" width="400" height="30"
				name="player" align="middle"
				allowScriptAccess="sameDomain"
				type="application/x-shockwave-flash"
				wmode="transparent"					
				pluginspage="http://www.macromedia.com/go/getflashplayer">
				<xsl:attribute name="src">
					<xsl:text>player.swf?id=</xsl:text>
					<xsl:text>thisPlayer</xsl:text>
					<xsl:text>&#x0026;src=</xsl:text>
					<!-- <xsl:value-of select="//referenced-file/@url"/> -->
					<xsl:value-of select="//referenced-file[ends-with(@url, 'mp3') or ends-with(@url, 'MP3')]/@url"/>
					<xsl:text>&#x0026;showmins=yes&#x0026;tcol=0xFFFFFF</xsl:text>						
				</xsl:attribute>
			</EMBED>
		</object>		
	</xsl:template>
	
	
	<xsl:template name="MAKE_CSS_STYLES">
		<style type="text/css">
			body {
			}
			
			div#head {
				background-color: #40627C;
				color:white;
				font-size:12pt;
				font-weight:bold;
				padding:7px;
				position:fixed;
				left:3%;
				right:3%;
				width:94%;
				z-index:100;
			}
			
			div#main {
				position:absolute;
				top:80px;
				right:0px;
				bottom:0px;
				width:98%;	
				/*height:95%;*/
				overflow:auto;	
				/*margin-bottom : 1000px*/
			}
			
			div#footer{
				color:gray;
				border:1px solid gray;
				text-align:right;
				font-size:10pt;
				margin-top:10px;
				margin-bottom: 10px;
				position:absolute;
				right:3%;
				left:3%;
			}
			
			span#corpus-title{
				color: blue;
			}
			#head a {
				text-decoration:none;
				color:white;
			}
			#previous-doc {
				font-size: 10pt;
			}
			#next-doc {
				font-size: 10pt;
			}
			
			
			
			span.subscript {
				font-size:8pt;
				font-weight:bold;
				vertical-align:sub;
				padding-left:2px;
				padding-right:2px
			}
			span.numbering {
				color:rgb(150,150,150)
			}
			span.non-verbal {
				color:rgb(100,100,100)
			}
			a.numbering {
				font-size:11pt;
				color:rgb(50,50,50);
				text-decoration:none;
			}
			
			a.HeadLink {
				font-family: Sans-Serif;
				font-size: 16pt;
				font-style: normal;
				font-weight: bold;
				text-decoration:none
			}
			
			table {
				margin-left:50px;
				margin-top:0px;
				margin-right:100px;
			}
			
			td {
				vertical-align:top
			}
			
			td.numbering {
			}
			td.speaker {
				font-size:11pt;
				font-weight:bold;
				padding-right:8px;
				padding-left:4px
			}
			td.text {
				font-size:11pt;
				font-weight:normal;
				padding-left:8px
			}
			
			td.translation {
				font-size:9pt;
				font-weight:normal;
				color:rgb(0,0,240);
				padding-left:8px
			}
			
			*.even {
				background-color:rgb(245,245,245);
			}
			
			*.odd {
				background-color:white;
			}
			
			a {
				text-decoration:none;
			}
		</style>        
	</xsl:template>
	


</xsl:stylesheet>
