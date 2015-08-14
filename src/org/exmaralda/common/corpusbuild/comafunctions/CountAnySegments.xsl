<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
   
   <xsl:variable name="ROOT" select="/"/>
   <xsl:variable name="SEGMENTATION" select="if(exists(//ts[@n='cGAT:w'])) then 'cGAT' else if(exists(//ts[@n='GEN:w'])) then 'GENERIC' else 'HIAT'"/>
   <xsl:variable name="SEGMENTATION_NAME" select="if($SEGMENTATION=('cGAT','GENERIC')) then 'SpeakerContribution_Word' else 'SpeakerContribution_Utterance_Word'"/>
   
   <xsl:variable name="MAP">
      <type abbr="sc" val="Speaker Contributions"/>
      <type abbr="{concat($SEGMENTATION,':w')}" val="Words"/>
      <type abbr="{concat($SEGMENTATION,':non-pho')}" val="Non-Phonological"/>
      <type abbr="HIAT:u" val="Utterances"/>
      <type abbr="HIAT:ip" val="Punctuation"/>
      <type abbr="cGAT:pause" val="Pause"/>
      <type abbr="cGAT:b" val="Breathe"/>
   </xsl:variable>
   
   <xsl:key name="get-description" match="type/@val" use="../@abbr"/>
   <xsl:key name="speaker-abbrev-by-id" match="speaker/abbreviation" use="../@id"/>
   <xsl:key name="speaker-sigle-by-id" match="speaker/ud-speaker-information/ud-information[@attribute-name='Sigle']" use="../../@id"/>
   
	<xsl:template match="/">
		<transcription>
			<xsl:for-each select="//segmented-tier[@type='t']/segmentation[@name=$SEGMENTATION_NAME]">
				<speaker abbreviation="{key('speaker-abbrev-by-id', ../@speaker, $ROOT)}" sigle="{key('speaker-sigle-by-id', ../@speaker, $ROOT)}"
				   id="{../@speaker}">
				   <xsl:variable name="THIS_SEGMENTATION" select="."/>
				   <xsl:for-each select="distinct-values(descendant::*[local-name()=('ts','ats','nts')]/@n)">
				      <count segment-name="{(key('get-description', ., $MAP), .)[1]}" count="{count($THIS_SEGMENTATION/descendant::*[local-name()=('ts','ats','nts')][@n=current()])}"/>
				   </xsl:for-each>
				</speaker>
			</xsl:for-each>
		</transcription>
	</xsl:template>
</xsl:stylesheet>