<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet  version="2.0"
        xmlns="http://www.tei-c.org/ns/1.0" 
        xmlns:tei="http://www.tei-c.org/ns/1.0" 
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
        xmlns:tesla="http://www.exmaralda.org"  
        xmlns:xs="http://www.w3.org/2001/XMLSchema" 
        xmlns:standoff="http://standoff.proposal">
	
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
	<xsl:template match="/ | @* | node()">
		<xsl:copy><xsl:apply-templates select="@* | node()"/></xsl:copy>
	</xsl:template>

	<!-- body template: sort u, event, pauses and apply templates -->
	<xsl:template match="*:body">
		<xsl:element name="body" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:apply-templates select="*:u | *:event | *:pause">
				<xsl:sort select="tesla:timeline-position(@start)" data-type="number"/>
				<xsl:sort select="tesla:timeline-position(@end)" data-type="number"/>
			</xsl:apply-templates>
		</xsl:element>
	</xsl:template>
	
	<!-- events -->
	<xsl:template match="*:event">
		<xsl:element name="incident">
			<xsl:copy-of select="@start"/>
			<xsl:copy-of select="@end"/>
			<xsl:copy-of select="@who"/>
			<xsl:element name="desc">
				<xsl:value-of select="@desc"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>
	
	<!-- u elements -->
	<xsl:template match="*:u">
		<xsl:element name="annotationGrp"  xmlns="http://standoff.proposal">
			<xsl:attribute name="who">
				<xsl:text>#</xsl:text><xsl:value-of select="@who"/>
			</xsl:attribute>
			<xsl:attribute name="start">
				<xsl:text>#</xsl:text><xsl:value-of select="@start"/>
			</xsl:attribute>
			<xsl:attribute name="end">
				<xsl:text>#</xsl:text><xsl:value-of select="@end"/>
			</xsl:attribute>
			<xsl:attribute name="xml:id">
				<xsl:text>au</xsl:text><xsl:value-of select="count(preceding::*:u)+1"/>
			</xsl:attribute>
			<xsl:element name="u" xmlns="http://www.tei-c.org/ns/1.0">
				<xsl:attribute name="xml:id">
					<xsl:text>u</xsl:text><xsl:value-of select="count(preceding::*:u)+1"/>
				</xsl:attribute>
				<xsl:apply-templates select="child::*[not(self::*:annotations)]"/>
			</xsl:element>
			<xsl:for-each-group select="*:annotations/*:annotation" group-by="@level">
				<xsl:element name="spanGrp" xmlns="http://www.tei-c.org/ns/1.0">
					<xsl:attribute name="type"><xsl:value-of select="current-grouping-key()"/></xsl:attribute>
					<xsl:for-each select="current-group()">
						<xsl:element name="span">
							<xsl:attribute name="from"><xsl:text>#</xsl:text><xsl:value-of select="@start"/></xsl:attribute>
							<xsl:attribute name="to"><xsl:text>#</xsl:text><xsl:value-of select="@end"/></xsl:attribute>
							<xsl:value-of select="@value"/>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>
			</xsl:for-each-group>
		</xsl:element>
	</xsl:template>
	
	<!-- seg whose last child is an anchor -->
	<xsl:template match="//*:seg[@type='utterance' and name(child::*[last()])='anchor']">
		<xsl:element name="seg">
			<xsl:attribute name="xml:id"><xsl:text>seg</xsl:text><xsl:value-of select="count(preceding::*:seg[@type='utterance'])"/></xsl:attribute>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:element>
		<xsl:element name="anchor">
			<xsl:attribute name="synch">#<xsl:value-of select="child::*[last()]/@synch"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!-- seg whose last child is NOT an anchor -->
	<xsl:template match="//*:seg[@type='utterance' and not(name(child::*[last()])='anchor')]">
		<xsl:element name="seg">
			<xsl:attribute name="xml:id"><xsl:text>seg</xsl:text><xsl:value-of select="count(preceding::*:seg[@type='utterance'])"/></xsl:attribute>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:seg/@type">
               	<xsl:attribute name="type"><xsl:value-of select="."/></xsl:attribute>
        	</xsl:template>

	<xsl:template match="*:seg/@mode">
            		<xsl:attribute name="subtype"><xsl:value-of select="."/></xsl:attribute>
    </xsl:template>

    <xsl:template match="*:uncertain-start[following-sibling::*:uncertain-end]">
     	<xsl:element name="unclear" xmlns="http://www.tei-c.org/ns/1.0">
            <xsl:apply-templates select="following-sibling::*[count(preceding-sibling::*)&lt;count(current()/following-sibling::*:uncertain-end/preceding-sibling::*)]" mode="grab_em"/>
	</xsl:element>
    </xsl:template>
	
    <xsl:template match="*:w[not(self::*:uncertain-start) and preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]" mode="grab_em">
            <xsl:element name="w">
                    <xsl:apply-templates select="@* | node()"/>			
            </xsl:element>
    </xsl:template>
	
    <!-- added 12-03-2015 -->
    <xsl:template match="*:pc[not(self::*:uncertain-start) and preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]" mode="grab_em">
            <xsl:element name="pc">
                    <xsl:apply-templates select="@* | node()"/>			
            </xsl:element>
    </xsl:template>

    <xsl:template match="*[not(self::*:pause) and not(self::*:uncertain-start) and preceding-sibling::*:uncertain-start and following-sibling::*:uncertain-end]">
            <!-- do nothing -->
    </xsl:template>
	
	<xsl:template match="//*:seg[@type='utterance']/*:anchor[not(following-sibling::*)]">
		<!-- do not copy the last anchor? -->
	</xsl:template>
	
	<!-- matches a short pause followed by a short pause followed by something other than a short pause -->
	<!-- changed because there was ambiguity wrt short pause preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and following-sibling::*[1][self::*:pause and @dur='short'] and  following-sibling::*[2][not(self::*:pause)] and not(preceding-sibling::*[1][self::*:pause and @dur='short'])]">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">medium</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!-- matches a short pause followed by a short pause followed by a short pause-->
	<xsl:template match="*:pause[@dur='short' and following-sibling::*[1][self::*:pause and @dur='short'] and following-sibling::*[2][self::*:pause and @dur='short']]">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">long</xsl:attribute>
		</xsl:element>
	</xsl:template>
	
	<!--matches a short pause not followed or preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and not(preceding-sibling::*[1][self::*:pause and @dur='short']) and not(following-sibling::*[1][self::*:pause and @dur='short'])]">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type">short</xsl:attribute>
		</xsl:element>
	</xsl:template>
        
	<!--matches a short pause preceded by a short pause -->
	<xsl:template match="*:pause[@dur='short' and preceding-sibling::*[1][self::*:pause and @dur='short']]">
		<!-- do nothing -->
	</xsl:template>
	
	<!-- the remaining cases -->
	<xsl:template match="*:pause[@dur='micro' or @dur='medium' or @dur='long' and not(preceding-sibling::*[1][self::*:pause] or following-sibling::*[1][self::*:pause])]">
		<xsl:element name="pause" xmlns="http://www.tei-c.org/ns/1.0">
			<xsl:attribute name="type" select="@dur"/>
		</xsl:element>		
	</xsl:template>
	
	
	
	<xsl:template match="*:u/@*">
		<!-- do nothing -->		
	</xsl:template>
		
	<xsl:template match="*:uncertain-end">
		<!-- do nothing -->		
	</xsl:template>
	
	<!--************************-->
	
	<xsl:template match="*:w">
		<xsl:element name="w">
			<xsl:if test="following-sibling::*[1][self::*:repair]">
				<xsl:attribute name="type">repair</xsl:attribute>
			</xsl:if>
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="*:anchor">
		<xsl:element name="anchor">
			<xsl:attribute name="synch">#<xsl:value-of select="@synch"/></xsl:attribute>
			<!-- <xsl:apply-templates select="@synch"/> -->
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:c">
		<xsl:element name="c">
			<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>
	
	<xsl:template match="*:repair">
		<!-- <xsl:element name="incident">
			<xsl:attribute name="type">repair</xsl:attribute>
		</xsl:element> -->
	</xsl:template>

	<xsl:template match="*:pause[not(@dur='micro' or @dur='short' or @dur='medium' or @dur='long')]">
		<xsl:element name="pause">
			<xsl:attribute name="dur">
				<xsl:text>PT</xsl:text>
				<xsl:value-of select="translate(@dur, ',s', '.S')"/>
			</xsl:attribute>
			<xsl:apply-templates select="@*[not(name()='dur')] | node()"/>
		</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>
