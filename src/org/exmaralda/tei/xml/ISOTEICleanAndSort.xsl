<?xml version="1.0" encoding="UTF-8"?>
<!-- change 03-03-2016: additional namespaces no longer necessary 
        xmlns:standoff="http://standoff.proposal"
-->        
<xsl:stylesheet  version="2.0"
        xmlns="http://www.tei-c.org/ns/1.0" 
        xmlns:tei="http://www.tei-c.org/ns/1.0" 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:tesla="http://www.exmaralda.org"  
        xmlns:xs="http://www.w3.org/2001/XMLSchema">
	
	<!-- new 25-06-2018 -->
	<!-- if this parameter is set to TRUE, XPointers will be used instead of IDREFs -->
	<xsl:param name="USE_XPOINTER">FALSE</xsl:param>
	
	<!-- <transcriptionDesc ident="HIAT" version="2004"> -->
	<xsl:param name="TRANSCRIPTION_SYSTEM" select="//*:transcriptionDesc/@ident"/>
	
	
	
	<xsl:variable name="XPOINTER_HASH">
		<xsl:choose>
			<xsl:when test="$USE_XPOINTER='TRUE'">#</xsl:when>
			<xsl:otherwise></xsl:otherwise>
		</xsl:choose>            
	</xsl:variable>

	<!-- I've been having loads of trouble with namespaces in this stylesheet -->
	<!-- The whole thing could probably look much simpler without these problems -->
	
	<!-- ***************************** -->
	
	<!-- memorizes position of timeline items for quicker reference -->
	<xsl:variable name="timeline-positions">
		<positions>
			<xsl:for-each select="//*:when">
				<item>
					<xsl:attribute name="id"><xsl:value-of select="@xml:id"/></xsl:attribute>
					<xsl:attribute name="position"><xsl:value-of select="count(preceding-sibling::*:when)"/></xsl:attribute>
				</item>
			</xsl:for-each>
		</positions>
	</xsl:variable>
	
	<!-- returns the position number of the timeline item with the given id -->
	<xsl:function name="tesla:timeline-position" as="xs:integer">
		<xsl:param name="timeline-id"/>
		<xsl:value-of select="$timeline-positions/descendant::*:item[@id=$timeline-id]/@position"/>
	</xsl:function>
	
	<!-- ***************************** -->
	
	<!-- root template: copy everything if no other instruction is found -->
	<xsl:template match="/">
		<xsl:apply-templates mode="normal"/>		
	</xsl:template>
		
	<xsl:template match="@* | node()" mode="normal">
		<xsl:copy><xsl:apply-templates select="@* | node()" mode="normal"/></xsl:copy>
	</xsl:template>
	

	<!-- body template: sort u, event, pauses and apply templates -->
	<xsl:template match="*:body" mode="normal">
		<xsl:element name="body" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:apply-templates select="*:u | *:event | *:pause" mode="normal">
				<xsl:sort select="tesla:timeline-position(@start)" data-type="number"/>
				<xsl:sort select="tesla:timeline-position(@end)" data-type="number"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<!-- events -->
	<xsl:template match="*:event" mode="normal">
		<!-- changed 16-02-2021 -->
		<xsl:choose>
			<xsl:when test="$TRANSCRIPTION_SYSTEM='HIAT' and string-length(translate(@desc,'()0123456789,s',''))=0">
				<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:copy-of select="@start"/>
					<xsl:copy-of select="@end"/>
					<xsl:attribute name="dur">
						<xsl:variable name="DURATION" select="translate(@desc, '()', '')"/>
						<xsl:text>PT</xsl:text>
						<xsl:value-of select="translate($DURATION, ',s', '.S')"/>						
					</xsl:attribute>
					<xsl:attribute name="rend">
						<xsl:value-of select="@desc"/>
					</xsl:attribute>						
				</xsl:element>				
			</xsl:when>
			<xsl:otherwise>
				<xsl:element name="incident">
					<xsl:copy-of select="@start"/>
					<xsl:copy-of select="@end"/>
					<xsl:copy-of select="@who"/>
					<!-- issue #504 -->
                                        <xsl:attribute name="rend">                                            
                                            <xsl:value-of select="@desc"/>
					</xsl:attribute>                                                                                
					<xsl:element name="desc">
						<xsl:value-of select="@desc"/>
					</xsl:element>
				</xsl:element>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- u elements -->
	<xsl:template match="*:u" mode="normal">
		<!-- change 03-03-2016: element renamed, namespace switch no longer necessary -->        
		<xsl:element name="annotationBlock" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="who">
				<xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@who"/>
			</xsl:attribute>
			<xsl:attribute name="start">
				<xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@start"/>
			</xsl:attribute>
			<xsl:attribute name="end">
				<xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@end"/>
			</xsl:attribute>
			<xsl:attribute name="xml:id">
				<xsl:text>au</xsl:text><xsl:value-of select="count(preceding::*:u)+1"/>
			</xsl:attribute>
			<xsl:element name="u" xmlns="http://www.tei-c.org/ns/1.0">
				<xsl:attribute name="xml:id">
					<xsl:text>u</xsl:text><xsl:value-of select="count(preceding::*:u)+1"/>
				</xsl:attribute>
				<xsl:apply-templates select="child::*[not(self::*:annotations)]" mode="normal"/>
			</xsl:element>
			<xsl:for-each-group select="*:annotations/*:annotation" group-by="@level">
				<xsl:element name="spanGrp" xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:attribute name="type"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
					<xsl:for-each select="current-group()">
						<xsl:element name="span">
							<xsl:attribute name="from"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@start"/></xsl:attribute>
							<xsl:attribute name="to"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@end"/></xsl:attribute>
							<xsl:value-of select="@value"/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each-group>
		</xsl:element>
	</xsl:template>
	
	<!-- seg whose last child is an anchor -->
	<xsl:template match="//*:seg[@type='utterance' and name(child::*[last()])='anchor']" mode="normal">
		<xsl:element name="seg">
			<xsl:attribute name="xml:id"><xsl:text>seg</xsl:text><xsl:value-of select="count(preceding::*:seg[@type='utterance'])"/></xsl:attribute>
			<xsl:apply-templates select="@* | node()" mode="normal"/>
		</xsl:element>
		<xsl:element name="anchor">
			<xsl:attribute name="synch"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="child::*[last()]/@synch"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!-- seg whose last child is NOT an anchor -->
	<xsl:template match="//*:seg[@type='utterance' and not(name(child::*[last()])='anchor')]" mode="normal">
		<xsl:element name="seg">
			<xsl:attribute name="xml:id"><xsl:text>seg</xsl:text><xsl:value-of select="count(preceding::*:seg[@type='utterance'])"/></xsl:attribute>
			<xsl:apply-templates select="@* | node()" mode="normal"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:seg/@type" mode="normal">
       	<xsl:attribute name="type"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

	<xsl:template match="*:seg/@mode" mode="normal">
    	<xsl:attribute name="subtype"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <!-- ************************************************************** -->
	<!-- templates for things between uncertain-start and uncertain-end -->
	<!-- ************************************************************** -->
	
	<xsl:template match="*:uncertain-start[following-sibling::*:uncertain-end]" mode="normal">
     	<!-- removed uncertain 11-07-2018 -->
		<!-- <xsl:element name="unclear" xmlns="http://www.tei-c.org/ns/1.0"> -->
		<xsl:variable name="POSITION_NEXT_UNCERTAIN_END" select="count(current()/following-sibling::*:uncertain-end[1]/preceding-sibling::*)"/>    
		<xsl:apply-templates 
            	select="following-sibling::*[count(preceding-sibling::*) &lt; $POSITION_NEXT_UNCERTAIN_END]" mode="grab_em"/>
		<!-- </xsl:element> -->
    </xsl:template>
	
<!--    <xsl:template match="*:w[preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]" mode="grab_em"> -->
	<xsl:template match="*:w" mode="grab_em">
    		<xsl:element name="w">
            <!-- added 11-07-2018 -->
        	<xsl:attribute name="type">
        		<xsl:text>uncertain</xsl:text>
        		<!-- added 28-01-2021: if this is repaired -->
        		<xsl:if test="following-sibling::*[not(self::*:uncertain-end)][1][self::*:repair]">
        			<xsl:text> repair</xsl:text>
        		</xsl:if>        		
        	</xsl:attribute>
        	<xsl:apply-templates select="@* | node()" mode="grab_em"/>			
        </xsl:element>
    </xsl:template>
	
    <!-- added 12-03-2015 -->
	<!-- <xsl:template match="*:pc[preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]" mode="grab_em"> -->
	<xsl:template match="*:pc" mode="grab_em">
          <xsl:element name="pc">
          	<!-- added 11-07-2018 -->
          	<xsl:attribute name="type">uncertain</xsl:attribute>
          	<xsl:apply-templates select="@* | node()" mode="grab_em"/>			
          </xsl:element>
    </xsl:template>

	<!-- added 25-01-2021 -->
	<!-- <xsl:template match="*:anchor[preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]" mode="grab_em"> -->
	<xsl:template match="*:anchor" mode="grab_em">	
		<xsl:element name="anchor">
			<xsl:attribute name="synch" select="@synch"/>
		</xsl:element>
		<!-- <WATCH_OUT_1/> -->
	</xsl:template>
	
	

	<!-- <xsl:template match="*[not(self::*:pause) and not(self::*:uncertain-start) and not(self::anchor) and preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end 
		and count(preceding-sibling::*:uncertain-start)!=count(following-sibling::*:uncertain-end)]"> -->
	<xsl:template match="*[ 
		 count(preceding-sibling::*:uncertain-start)&gt;0 
		 and (count(preceding-sibling::*:uncertain-start)!=count(preceding-sibling::*:uncertain-end))]" mode="normal"> 
            <!-- do nothing if you encounter this while NOT in mode="grab_em"-->
		<!-- <HUCH><xsl:value-of select="text()"/></HUCH> -->
    </xsl:template>
	
	<!-- ****************************************************************** -->
	<!-- end templates for things between uncertain-start and uncertain-end -->
	<!-- ****************************************************************** -->
	
	<xsl:template match="//*:seg[@type='utterance']/*:anchor[not(following-sibling::*)]" mode="normal">
		<!-- do not copy the last anchor? -->
	</xsl:template>
	
	<!-- ********* -->
	<!--  PAUSES   -->
	<!-- ********* -->
	
	
	<!-- matches a short pause followed by a short pause followed by something other than a short pause -->
	<!-- changed because there was ambiguity wrt short pause preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and following-sibling::*[1][self::*:pause and @dur='short'] and  following-sibling::*[2][not(self::*:pause)] and not(preceding-sibling::*[1][self::*:pause and @dur='short'])]" mode="normal">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">medium</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!-- matches a short pause followed by a short pause followed by a short pause-->
	<xsl:template match="*:pause[@dur='short' and following-sibling::*[1][self::*:pause and @dur='short'] and following-sibling::*[2][self::*:pause and @dur='short']]" mode="normal">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">long</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!--matches a short pause not followed or preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and not(preceding-sibling::*[1][self::*:pause and @dur='short']) and not(following-sibling::*[1][self::*:pause and @dur='short'])]" mode="normal">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">short</xsl:attribute>
		</xsl:element>
	</xsl:template>
        
	<!--matches a short pause preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and preceding-sibling::*[1][self::*:pause and @dur='short']]" mode="normal">
		<!-- do nothing -->
	</xsl:template>
	
	<!-- the remaining cases -->
	<xsl:template match="*:pause[@dur='micro' or @dur='medium' or @dur='long' and not(preceding-sibling::*[1][self::*:pause] or following-sibling::*[1][self::*:pause])]" mode="normal">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type" select="@dur"/>
		</xsl:element>		
	</xsl:template>
	
	<xsl:template match="*:pause[not(@dur='micro' or @dur='short' or @dur='medium' or @dur='long')]" mode="normal">
		<xsl:element name="pause">
			<xsl:variable name="PAUSE_NOTATION">
				<xsl:choose>
					<xsl:when test="contains(@dur, ',') and ends-with(@dur, 's')"><xsl:value-of select="translate(@dur, ',s', '.S')"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="concat(@dur, 'S')"/></xsl:otherwise> 
				</xsl:choose>
			</xsl:variable>
			<xsl:attribute name="dur">
				<xsl:text>PT</xsl:text>
				<xsl:value-of select="$PAUSE_NOTATION"/>
			</xsl:attribute>
			<xsl:apply-templates select="@*[not(name()='dur')] | node()" mode="normal"/>
			<!-- <xsl:apply-templates select="@*[not(name()='dur')]"/> -->
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:pause/text()" mode="normal"/>
	
	<!-- ************* -->
	<!--  END PAUSES   -->
	<!-- ************* -->
	
	
	
	
	
	<xsl:template match="*:u/@start | *:u/@end" mode="normal"> -->
		<!-- do nothing -->		
	</xsl:template> 
		
	<xsl:template match="*:uncertain-end" mode="normal">
		<!-- do nothing -->		
	</xsl:template>
	
	<!--************************-->
	
	<xsl:template match="*:w" mode="normal">
		<xsl:element name="w">
			<xsl:if test="following-sibling::*[1][self::*:repair]">
				<xsl:attribute name="type">repair</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates mode="normal"/>
		</xsl:element>
	</xsl:template>

	<!-- changed 29-01-2021 -->
	<!-- <xsl:template match="*:anchor[count(preceding-sibling::*:uncertain-start)=0 or (count(preceding-sibling::*:uncertain-start)!=count(following-sibling::*:uncertain-end))]"> -->
	<xsl:template match="*:anchor" mode="normal">
		<xsl:element name="anchor">
			<xsl:attribute name="synch"><xsl:value-of select="$XPOINTER_HASH"/><xsl:value-of select="@synch"/></xsl:attribute>			
			<!-- <xsl:apply-templates select="@synch"/> -->
		</xsl:element>
		<!-- <WATCH_OUT_2/> -->
	</xsl:template>
	
	<!-- added 29-01-2021 -->
	<!-- <xsl:template match="*:anchor[count(preceding-sibling::*:uncertain-start)&gt;0 and (count(preceding-sibling::*:uncertain-start)=count(following-sibling::*:uncertain-end))]">
	</xsl:template> -->

	<xsl:template match="*:c" mode="normal">
		<xsl:element name="c">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:repair" mode="normal">
		<!-- <xsl:element name="incident">
			<xsl:attribute name="type">repair</xsl:attribute>
		</xsl:element> -->
		<!-- <xsl:element name="pc">/</xsl:element> -->
	</xsl:template>

	
</xsl:stylesheet>
