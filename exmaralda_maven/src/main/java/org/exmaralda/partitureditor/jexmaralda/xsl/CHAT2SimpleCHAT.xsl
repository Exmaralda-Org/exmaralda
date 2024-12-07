<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:strip-space elements="u"/>
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
		<xsl:element name="participant">
			<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
			<xsl:attribute name="name"><xsl:value-of select="@name"/></xsl:attribute>
			<xsl:attribute name="role"><xsl:value-of select="@role"/></xsl:attribute>
			<xsl:attribute name="age"><xsl:value-of select="@age"/></xsl:attribute>
			<xsl:attribute name="sex"><xsl:value-of select="@sex"/></xsl:attribute>
		</xsl:element>
	</xsl:template>
	<xsl:template match="u">
		<xsl:element name="u">
			<xsl:attribute name="who"><xsl:value-of select="@who"/></xsl:attribute>
			<text>
				<xsl:apply-templates select="e | w | s |  t | pause | wn | g "/>
			</text>
			<xsl:apply-templates select="a"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="w">
		<xsl:value-of select="text()"/>
		<xsl:apply-templates select="f[@type='fragment']"></xsl:apply-templates>
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
	<xsl:template match="w/f[@type='fragment']">
		<xsl:text>(</xsl:text>
		<xsl:value-of select="text()"></xsl:value-of>
		<xsl:text>)</xsl:text>
	</xsl:template>
	<xsl:template match="w/f[@type='completion']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="e">
		<xsl:text>[</xsl:text>
			<xsl:value-of select="@type"></xsl:value-of>
			<xsl:apply-templates select="a"></xsl:apply-templates>
		<xsl:text>] </xsl:text>
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='comments']">
		<xsl:text>: </xsl:text>
		<xsl:value-of select="text()"></xsl:value-of>
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='paralinguistics']">
		<xsl:text>: </xsl:text>
		<xsl:value-of select="text()"></xsl:value-of>
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='actions']">
		<xsl:text>: </xsl:text>
		<xsl:value-of select="text()"></xsl:value-of>
	</xsl:template>
	<xsl:template match="e[@type='action']/a[@type='addressee']">
		<!-- do nothing -->
	</xsl:template>
	<xsl:template match="wn">
		<xsl:apply-templates select="w | wk"></xsl:apply-templates>
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
	<xsl:template match="g">
		<xsl:apply-templates select="w | k | s | t | pause"></xsl:apply-templates>
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
	<xsl:template match="s[@type='comma']">
		<xsl:text>, </xsl:text>
	</xsl:template>
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
	<xsl:template match="k[@type='retracing']">
		<xsl:text>/</xsl:text>	
	</xsl:template>
	<xsl:template match="a">
		<xsl:element name="annotation">
			<xsl:attribute name="type"><xsl:value-of select="@type"/></xsl:attribute>
			<xsl:value-of select="text()"/>
		</xsl:element>
	</xsl:template>
	<xsl:template match="pause">
		<xsl:text># </xsl:text>
	</xsl:template>
</xsl:stylesheet>
