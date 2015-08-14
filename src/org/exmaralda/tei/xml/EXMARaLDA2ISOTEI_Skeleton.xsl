<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"  
        xmlns:tesla="http://www.exmaralda.org" 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:tei="http://www.tei-c.org/ns/1.0" 
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:isoSpoken="http://iso-tei-spoken.org/ns/1.0"
        xmlns:standoff="http://standoff.proposal">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

	<xsl:function name="tesla:determine-recording-type">
		<xsl:param name="path"/>
		<xsl:choose>
			<xsl:when test="ends-with(lower-case($path), '.wav')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.mp3')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.aif')">audio</xsl:when>
			<xsl:when test="ends-with(lower-case($path), '.snd')">audio</xsl:when>
			<xsl:otherwise>video</xsl:otherwise>
		</xsl:choose>		
	</xsl:function>
	
	<!-- memorizes position of timeline items for quicker reference -->
	<xsl:variable name="timeline-positions">
		<positions>
			<xsl:for-each select="//tli">
				<item>
					<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
					<xsl:attribute name="position"><xsl:value-of select="count(preceding-sibling::tli)"/></xsl:attribute>
				</item>
			</xsl:for-each>
		</positions>
	</xsl:variable>
	
	<!-- returns the position number of the timeline item with the given id -->
	<xsl:function name="tesla:timeline-position" as="xs:integer">
		<xsl:param name="timeline-id"/>
		<xsl:value-of select="$timeline-positions/descendant::item[@id=$timeline-id]/@position"/>
	</xsl:function>
	
	
	<xsl:function name="tesla:seconds-to-timestring">
		<xsl:param name="seconds"/>
		<xsl:variable name="totalseconds">
			<xsl:value-of select="0 + $seconds"/>
		</xsl:variable>
		<xsl:variable name="hours">
			<xsl:value-of select="0 + floor($totalseconds div 3600)"/>
		</xsl:variable>
		<xsl:variable name="minutes">
			<xsl:value-of select="0 + floor(($totalseconds - 3600*$hours) div 60)"/>
		</xsl:variable>
		<xsl:variable name="seconds">
			<xsl:value-of select="0 + ($totalseconds - 3600*$hours - 60*$minutes)"/>
		</xsl:variable>
		<xsl:if test="$hours+0 &lt; 10 and $hours &gt;0">
			<xsl:text>0</xsl:text>
			<xsl:value-of select="$hours"/>
		</xsl:if>
		<xsl:if test="$hours + 0 = 0">
			<xsl:text>00</xsl:text>                            
		</xsl:if>
		<xsl:text>:</xsl:text>
		<xsl:if test="$minutes+0 &lt; 10">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="$minutes"/>
		<xsl:text>:</xsl:text>
		<xsl:if test="$seconds+0 &lt; 10">
			<xsl:text>0</xsl:text>
		</xsl:if>
		<xsl:value-of select="$seconds"/>            
	</xsl:function>
	
	
	<!-- top level element / main structure -->
	<!-- ****************************************************************** -->
	<!-- ************************ ROOT TEMPLATE    ************************ -->
	<!-- ****************************************************************** -->
	<xsl:template match="/">
		<TEI xmlns="http://www.tei-c.org/ns/1.0">
			<teiHeader>
				<!-- ***************************************************** -->
				<fileDesc>
					<titleStmt><title></title></titleStmt>
					<publicationStmt>
						<authority><xsl:comment>Fill me in</xsl:comment></authority>
						<availability>
							<licence target="someurl"/>
							<p><xsl:comment>Fill me in</xsl:comment></p>
						</availability>
						<distributor><xsl:comment>Fill me in</xsl:comment></distributor>
						<address>
                                                    <addrLine><xsl:comment>Fill me in</xsl:comment></addrLine>  
                                                </address>
					</publicationStmt>
					<!-- ***************************************************** -->
					<sourceDesc>
						<recordingStmt>
							<recording type="video">
								<!-- element from TEI P5, but not allowed there as a child of recording -->
								<xsl:apply-templates select="//referenced-file"/>
								<broadcast>
									<ab><xsl:comment>Fill me in</xsl:comment></ab>
								</broadcast>
								<!-- information about the equipment used for creating the recording -->
								<!-- where recordings are made by the researcher, this would be -->
								<!-- place to specify the recording equipment (e.g. Camcorder) -->
								<equipment>
									<ab><xsl:comment>Fill me in</xsl:comment></ab>
									<ab><xsl:comment>Fill me in</xsl:comment></ab>
								</equipment>                  
							</recording>
						</recordingStmt>                                        
					</sourceDesc>
					<!-- ... -->
				</fileDesc>
				<!-- ***************************************************** -->                
				<profileDesc>
					<!-- ... -->
					<particDesc>
						<xsl:apply-templates select="//speaker"/>
					</particDesc>        
					<!-- ***************************************************** -->                
					<settingDesc>
						<place><xsl:comment>Fill me in</xsl:comment></place>
						<setting>
							<activity><xsl:comment>Fill me in</xsl:comment></activity>
						</setting>
					</settingDesc>                    
				</profileDesc>
				<encodingDesc>
					<appInfo>
						<!-- information about the application with which -->
						<!-- the transcription was created -->
						<application ident="EXMARaLDA" version="1.5.3">
							<label>EXMARaLDA Partitur-Editor</label>
							<desc>Transcription Tool providing a TEI Export</desc>
						</application>
					</appInfo>       
					<!-- information about the transcription convention used -->
					<isoSpoken:transcriptionDesc ident="HIAT" version="2004">
						<desc><xsl:comment>Fill me in</xsl:comment></desc>
                                                <label><xsl:comment>Fill me in</xsl:comment></label>
					</isoSpoken:transcriptionDesc>
				</encodingDesc>
				<revisionDesc>
					<!-- ... -->                    
					<xsl:element name="change">
						<xsl:attribute name="when"><xsl:value-of select="current-dateTime()"/></xsl:attribute>
						<xsl:text>Created by XSL transformation from an EXMARaLDA basic transcription</xsl:text>
					</xsl:element>
				</revisionDesc>                
			</teiHeader>
			<text>
				<xsl:call-template name="MAKE_TIMELINE"/>
				<body>
				</body>
			</text>
		</TEI>            
	</xsl:template>
	
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	<!-- **********           MEDIA          ************* -->
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	<xsl:template match="referenced-file">
		<media  xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="mimeType"><xsl:value-of select="tesla:determine-recording-type(@url)"/>/xxx</xsl:attribute>
			<xsl:attribute name="url"><xsl:value-of select="@url"/></xsl:attribute>
		</media>        
	</xsl:template>
	
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	<!-- **********         TIMELINE         ************* -->
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	<xsl:template name="MAKE_TIMELINE">
		<timeline unit="s" xmlns="http://www.tei-c.org/ns/1.0">
			<!-- <xsl:attribute name="origin">
				<xsl:choose>
					<xsl:when test="//common-timeline/tli[1]/@time=0.0">#<xsl:value-of select="//common-timeline/tli[1]/@id"/></xsl:when>
					<xsl:otherwise>#T_START</xsl:otherwise>
				</xsl:choose>                
			</xsl:attribute> -->
			<xsl:if test="//common-timeline/tli[1]/@time&gt;0.0">
				<!-- <when xml:id="T_START" absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0"/> -->
				<when xml:id="T_START" xmlns="http://www.tei-c.org/ns/1.0"/>				
			</xsl:if>
			<xsl:apply-templates select="//common-timeline/tli"/>
		</timeline>        
	</xsl:template>
	
	<!-- CHANGE FOR ISO: use decimal seconds notation, use intervals and since instead of absoulute -->
	<xsl:template match="common-timeline/tli[position()&gt;1]">
		<xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="xml:id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:if test="@time">
				<xsl:attribute name="interval">
					<xsl:value-of select="@time"/>
				</xsl:attribute>
				<xsl:attribute name="since">
					<xsl:choose>
						<xsl:when test="//tli[1]/@time=0.0">#<xsl:value-of select="//common-timeline/tli[1]/@id"/></xsl:when>
						<xsl:otherwise>#T_START</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:if>
		</xsl:element>                
	</xsl:template>
	
	<!-- CHANGE FOR ISO: special treatment for first -->
	<xsl:template match="//common-timeline/tli[position()=1]">
		<xsl:choose>
			<xsl:when test="@time=0" >
				<when absolute="00:00:00.0" xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:attribute name="xml:id" select="@id"/>
				</when>
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="when" xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:attribute name="xml:id">
						<xsl:value-of select="@id"/>
					</xsl:attribute>
					<xsl:if test="@time">
						<xsl:attribute name="interval">
							<xsl:value-of select="@time"/>
						</xsl:attribute>
						<xsl:attribute name="since">#T_START</xsl:attribute>
					</xsl:if>
				</xsl:element>                                
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	<!-- **********           SPEAKERS       ************* -->
	<!-- ************************************************* -->
	<!-- ************************************************* -->
	
	<xsl:template match="speaker">
		<xsl:element name="person" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="xml:id">
				<xsl:value-of select="@id"/>
			</xsl:attribute>
			<xsl:attribute name="n"><xsl:value-of select="abbreviation"/></xsl:attribute>
			<xsl:attribute name="sex">
				<xsl:choose>
					<xsl:when test="sex/@value='m'">1</xsl:when>
					<xsl:when test="sex/@value='f'">2</xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose>                
			</xsl:attribute>
			<xsl:element name="persName"></xsl:element>
		</xsl:element>        
	</xsl:template>
	
	
	
</xsl:stylesheet>
