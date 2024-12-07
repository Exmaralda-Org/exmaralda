<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" version="1.0" encoding="iso-8859-1" indent="yes"/>
	<xsl:template match="/">
		<CHAT>
			<xsl:apply-templates select="CHAT/Participants"/>
			<xsl:apply-templates select="//u"/>
		</CHAT>
	</xsl:template>
	<xsl:template match="CHAT/Participants">
		<Participants>
			<xsl:apply-templates select="participant"/>
		</Participants>
	</xsl:template>
	<xsl:template match="participant">
		<!-- Do nothing -->
	</xsl:template>
	<!-- matches utterances -->
	<xsl:template match="u">
		<xsl:value-of select="@who"/>
		<xsl:text>: </xsl:text>
		<xsl:apply-templates select="e | w | s |  pause | wn | g "/>
		<xsl:apply-templates select="a"/>
		<xsl:text>&#x0a;</xsl:text>		
	</xsl:template>

	<!-- matches words -->
	<xsl:template match="w">
		<xsl:call-template name="checkOverlapBegin"></xsl:call-template>
		<xsl:value-of select="text()"/>
		<xsl:apply-templates select="f[@type='fragment']"></xsl:apply-templates>
		<xsl:call-template name="spaceBetweenWords"></xsl:call-template>
		<xsl:call-template name="checkLastInUtterance"></xsl:call-template>
		<xsl:call-template name="checkOverlapEnd"></xsl:call-template>
	</xsl:template>

	<!-- matches word nets -->
	<xsl:template match="wn">
		<xsl:apply-templates select="w | wk"/>
		<xsl:call-template name="spaceBetweenWords"></xsl:call-template>
		<xsl:call-template name="checkLastInUtterance"></xsl:call-template>
		<xsl:call-template name="checkOverlapEnd"></xsl:call-template>
	</xsl:template>

	<!-- matches groups -->
	<xsl:template match="g">
		<xsl:call-template name="checkOverlapBegin"></xsl:call-template>
		<xsl:apply-templates select="w | k | s | t | pause"/>
		<xsl:call-template name="spaceBetweenWords"></xsl:call-template>
		<xsl:call-template name="checkLastInUtterance"></xsl:call-template>
		<xsl:call-template name="checkOverlapEnd"></xsl:call-template>
	</xsl:template>

	<!-- matches pauses -->
	<xsl:template match="pause">
		<xsl:text># </xsl:text>
	</xsl:template>
	
	<!-- matches unintelligible words and passages -->
	<xsl:template match="e">
		<xsl:call-template name="checkOverlapBegin"></xsl:call-template>
		<xsl:if test="@type='unintelligible-word'">
			<xsl:text>((unintelligible word))</xsl:text>
		</xsl:if>
		<xsl:if test="@type='unintelligible'">
			<xsl:text>((unintelligible))</xsl:text>
		</xsl:if>
		<xsl:call-template name="spaceBetweenWords"></xsl:call-template>
		<xsl:call-template name="checkLastInUtterance"></xsl:call-template>
		<xsl:call-template name="checkOverlapEnd"></xsl:call-template>
	</xsl:template>


	<!-- matches annotations on the utterance level -->
	<xsl:template match="a">
		<xsl:text>{</xsl:text>
		<xsl:value-of select="@type"/>
		<xsl:text>: </xsl:text>
		<xsl:value-of select="text()"/>
		<xsl:text>}</xsl:text>
	</xsl:template>

	<!-- matches fragments -->
	<xsl:template match="w/f[@type='fragment']">
		<xsl:text>(</xsl:text>
		<xsl:value-of select="text()"/>
		<xsl:text>)</xsl:text>
	</xsl:template>

	<!-- matches segmentors (comma) -->
	<xsl:template match="s[@type='comma']">
		<xsl:text>, </xsl:text>
	</xsl:template>

	<!-- matches terminators (period, question mark, trail off, emphasis) -->
	<xsl:template match="t[@type='p']">
		<xsl:text>. </xsl:text>
	</xsl:template>
	<xsl:template match="t[@type='trail off']">
		<xsl:text>… </xsl:text>
	</xsl:template>
	<xsl:template match="t[@type='e']">
		<xsl:text>! </xsl:text>
	</xsl:template>
	<xsl:template match="t[@type='q']">
		<xsl:text>? </xsl:text>
	</xsl:template>
	<xsl:template match="wk[@type='cmp']">
		<xsl:text>+</xsl:text>
	</xsl:template>

	<!-- matches retracing -->
	<xsl:template match="k[@type='retracing']">
		<xsl:text>/</xsl:text>
	</xsl:template>


	<!-- matches things that won't be processed for the moment because they are too complex... :-( -->
	<xsl:template match="w/f[@type='completion']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='comments']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='paralinguistics']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='actions']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='addressee']">
		<!-- do nothing -->
	</xsl:template>

	<xsl:template name="spaceBetweenWords">
		<xsl:if test="following-sibling::*[1][self::w]">
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="following-sibling::*[1][self::pause]">
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="following-sibling::*[1][self::e]">
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="following-sibling::*[1][self::g]">
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:if test="following-sibling::*[1][self::wn]">
			<xsl:text> </xsl:text>
		</xsl:if>	
	</xsl:template>

	<xsl:template name="checkOverlapBegin">
		<xsl:if test="./overlap">
			<xsl:text>&lt;</xsl:text>
		</xsl:if>	
	</xsl:template>
	
	<xsl:template name="checkOverlapEnd">
		<xsl:if test="./overlap">
			<xsl:text>&gt;</xsl:text>
			<xsl:if test="./overlap[@type='overlap follows']">
				<xsl:value-of select="count(../preceding-sibling::node()) + 1" />
			</xsl:if>
			<xsl:if test="./overlap[@type='overlap precedes'] and not(./overlap[@index])">
				<xsl:value-of select="count(../preceding-sibling::node())" />
			</xsl:if>
			<xsl:if test="./overlap[@type='overlap precedes'] and ./overlap[@index]">
				<xsl:variable name="x1" select="count(../preceding-sibling::node())"></xsl:variable>
				<xsl:variable name="x2" select="./overlap/@index"></xsl:variable>
				<xsl:value-of select="$x1 - $x2 + 1" />
			</xsl:if>
			<xsl:if test="./overlap[@index]">
				<xsl:text>.</xsl:text>
				<xsl:value-of select="./overlap/@index"></xsl:value-of>
			</xsl:if>
			<xsl:text>&gt;</xsl:text>
		</xsl:if>
	</xsl:template>
	
	
	<xsl:template name="checkLastInUtterance">
		<xsl:if test="following-sibling::*[1][self::t]">
			<xsl:apply-templates select="../t"></xsl:apply-templates>
		</xsl:if>	
		<xsl:if test="following-sibling::*[1][self::a]">
			<xsl:apply-templates select="../t"></xsl:apply-templates>
		</xsl:if>	
	</xsl:template>
	

</xsl:stylesheet>
