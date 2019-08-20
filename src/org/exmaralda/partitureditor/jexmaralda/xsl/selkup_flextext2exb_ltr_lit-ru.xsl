<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:import href="../../../inelutilities/flextext2exb.xsl"/>
    
    <xsl:param name="PROJECTNAME" select="'Selkup'" as="xs:string"/>
    <xsl:param name="BASE-LANGUAGE" select="'sel'" as="xs:string"/><!-- get from flextext -->
    <xsl:param name="SPEAKER-CODE-POSITION" select="1" as="xs:integer"/>
    
    <!-- previously also in replaced settings_selkup.xml:    
        <timeline start="4.0" step="0.5" />
        <text-name from-item="title-abbreviation" from-lang="en" />
        <sentence-number format="both" />
    -->
        
    <xsl:param name="TIER-DEFINITIONS">
        <tiers>
            <tier name="ref" type="a" cleanup="renum" itemtype="segnum" lang="en" template="tier-sent"/>
            <tier name="st" type="a" cleanup="brackets" itemtype="lit" lang="sel-Cyrl-x-source" template="tier-sent"/>
            <tier name="stl" type="a" cleanup="brackets" itemtype="lit" lang="sel-Latn-x-source" template="tier-sent"/>
            <tier name="ts" type="a" cleanup="brackets, tx" template="tier-sent-join"/>
            <tier name="tx" type="t" cleanup="brackets, tx" itemtype="txt" template="tier-tx"/>
            <tier name="mb" type="a" cleanup="brackets, morph" sep="" template="tier-morph"/>
            <tier name="mp" type="a" cleanup="brackets, morph" itemtype="cf" sep="" template="tier-morph"/>
            <tier name="ge" type="a" cleanup="brackets, gloss, clitics" itemtype="gls" lang="en" sep="-" template="tier-morph"/>
            <!--<tier name="gg" type="a" cleanup="brackets, gloss, clitics" itemtype="gls" lang="fr" sep="-" template="tier-morph"/>-->
            <tier name="gr" type="a" cleanup="brackets, gloss, clitics" itemtype="gls" lang="ru" sep="-" template="tier-morph"/>
            <tier name="mc" type="a" cleanup="mc-tier, clitics" itemtype="msa" lang="en" sep="-" template="tier-morph"/>
            <tier name="ps" type="a" itemtype="pos" lang="en" template="tier-word"/>
            <tier name="SeR" type="a" template="tier-word-new"/>
            <tier name="SyF" type="a" template="tier-word-new"/>
            <tier name="IST" type="a" template="tier-word-new"/>
            <tier name="BOR" type="a" cleanup="brackets, gloss" itemtype="gls" lang="en-x-ref" sep="" template="tier-morph"/>
            <tier name="BOR-Phon" type="a" template="tier-word-new"/>
            <tier name="BOR-Morph" type="a" template="tier-word-new"/>
            <tier name="CS" type="a" template="tier-word-new"/>
            <tier name="fe" type="a" cleanup="brackets" itemtype="gls" lang="en" template="tier-sent"/>
            <tier name="fg" type="a" cleanup="brackets" itemtype="gls" lang="de" template="tier-sent"/>
            <tier name="fr" type="a" cleanup="brackets" itemtype="gls" lang="ru" template="tier-sent"/>
            <tier name="ltr" type="a" cleanup="brackets" itemtype="lit" lang="ru" template="tier-sent"/>            <!-- Kuzmina's literal Russian - when it's in Lit/Rus line -->            
            <!--<tier name="ltr" type="a" template="tier-sent" itemtype="gls" lang="sel-Cyrl-x-source" cleanup="brackets"/>  -->          <!-- Kuzmina's literal Russian - when it's in Free/Sel-SC line -->
            <tier name="nt" type="a" cleanup="brackets" itemtype="note" lang="en" template="tier-sent"/>
            <tier name="nto" type="a" cleanup="brackets" itemtype="note" lang="sel-Cyrl-x-source" template="tier-sent"/>
        </tiers>
    </xsl:param>
    
    
</xsl:stylesheet>