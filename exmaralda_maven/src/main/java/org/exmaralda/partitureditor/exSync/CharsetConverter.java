/*
 * CharsetConverter.java
 *
 * Created on 31. Januar 2003, 15:01
 */

package org.exmaralda.partitureditor.exSync;

/**
 *
 * @author  Thomas
 */
public class CharsetConverter {
    
    static String[][] HIAT_TIMES = {  {"\u003c","_"},               // langsamer werdend
                                      {"\u003e","_"},               // schneller werdend
                                      {"\u00a9","\u011f"},          // kleines g mit Häkchen
                                      {"\u2122","\u0117"},          // kleines e mit Punkt
                                      {"\u221e","\u0302"},          // Diakritikon steigend-fallend
                                      // ADDED 03/09/03 Version 1.2.5.
                                      {"\u0023","\u030C"},          // Diakritikon fallend-steigend
                                      {"\u2264","_"},               // leiser werdend
                                      {"\u2265","_"},               // lauter werdend
                                      {"\u00a5","\u0161"},          // kleines s mit Häkchen
                                      {"\u00b5","\u006d\u030c"},    // kleines m mit Diakritikon fallend-steigend
                                      {"\u2202","\u0111"},          // kleines d mit Strich
                                      {"\u03c0","\u203f"},          // Ligatur-Bogen
                                      {"\u03a9","\u017e"},          // kleines z mit Häkchen
                                      {"\u00ac","\u0133"},          // kleine lj-Ligatur
                                      {"\u0192","\u0107"},          // kleines c mit Akzent akut
                                      {"\u2248","\u010d"},          // kleines c mit Häkchen
                                      {"\u2206","\u01cc"},          // kleine nj-Ligatur
                                      {"\u0152","\u0141"},          // großes L mit Strich
                                      {"\u0153","\u0142"},          // kleines l mit Strich
                                      {"\u2013","\u0304"},          // Diakritikon gleichbleibend
                                      {"\u2018","\u0301"},          // Diakritikon steigend
                                      {"\u2019","\u0300"},          // Diakritikon fallend
                                      {"\u00f7","\u0307"},          // Diakritikon Punkt
                                      {"\u0178","\u015f"},          // kleines s mit Cedille
                                      {"\u2044","\u0131"},          // kleines i ohne Punkt
                                      {"\u20ac","\u0110"},          // großes D mit Strich
                                      {"\ufb02","\u01c8"},          // große Lj-Ligatur
                                      {"\u2021","\u01c8"},          // großes S mit Häkchen
                                      {"\u00b7","\u00ce"},          // großes I mit Akzent Circonflex
                                      {"\u201a","\u015f"},          // kleines s mit Cedille
                                      {"\u2030","\u0025"},          // Prozentzeichen
                                      {"\u00c8","\u0302"},          // Diakritikon steigend-fallend (???)
                                      {"\u00cd","\u015e"},          // großes S mit Cedille
                                      {"\u00cf","\u0106"},          // großes C mit Akzent akut
                                      {"\uf88f","_"},               // Euro-Zeichen
                                      {"\u00db","\u0130"},          // großes I mit Punkt
                                      {"\u00d9","\u010c"},          // großes C mit Häkchen
                                      {"\u02d8","\u006d\u0302"},    // kleines m mit Diakritikon steigend-fallend
                                      {"\u02db","\u00cf"},          // großes I mit Trema
                                      {"\u02c7","\u017d"}           // großes Z mit Häkchen
                                     };
                                    
    static String[][] LOUIS_WILL = {  // ADDED 03/09/03 Version 1.2.5.
                                      {"\u2044","\u0131"},          // kleines i ohne Punkt
                                      // ADDED 03/09/03 Version 1.2.5.
                                      {"\u00A9","\u011F"},          // kleines g mit Häkchen
                                      // ADDED 03/09/03 Version 1.2.5.
                                      {"\u201A","\u015F"}            // kleines s mit cedille
                                    };

                                      
    static String[][] SIL_IPA = {
                                    {"\\u0022", "\u2019"},	//Ejective
                                    {"\u0024", "\u0303"},	//Nasalized
                                    {"\u002E", "\u002E"},	//Syllabic break
                                    {"\u0036", "\u031E"},	//lowered
                                    {"\u0038", "\u032F"},	//Non-syllabic
                                    {"\u003A", "\u02D0"},	//Long
                                    {"\u003D", "\u0273"},	//voiced retroflex nasal
                                    {"\u003F", "\u0294"},	//glottal stop
                                    {"\u0041", "\u0251"},	//low back unrounded vowel
                                    {"\u0042", "\u03B2"},	//greek small letter beta
                                    {"\u0043", "\u0255"},	//voiceless alveolo-palatal laminal fricative
                                    {"\u0044", "\u00F0"},	//latin small letter eth
                                    {"\u0045", "\u025B"},	//lower-mid front unrounded vowel
                                    {"\u0046", "\u0278"},	//voiceless bilabial fricative
                                    {"\u0047", "\u0262"},	//voiced uvular stop
                                    {"\u0048", "\u0266"},	//breathy-voiced glottal fricative
                                    {"\u0049", "\u0268"},	//high central unrounded vowel
                                    {"\u004A", "\u0292"},	//voiced postalveolar fricative
                                    {"\u004B", "\u0296"},	//lateral click
                                    {"\u004D", "\u0271"},	//voiced labiodental nasal
                                    {"\u004E", "\u014B"},       // latin small letter eng
                                    {"\u004F", "\u0254"},	//lower-mid back rounded vowel
                                    {"\u0050", "\u0264"},	//upper-mid back unrounded vowel
                                    {"\u0051", "\u03B8"},	//greek small letter theta
                                    {"\u0052", "\u0280"},	//voiced uvular trill
                                    {"\u0053", "\u0283"},	//voiceless postalveolar fricative
                                    {"\u0054", "\u0288"},	//voiceless retroflex stop
                                    {"\u0055", "\u028C"},	//lower-mid back unrounded vowel
                                    {"\u0056", "\u0263"},	//voiced velar fricative
                                    {"\u0057", "\u02E1"},	//lateral release
                                    {"\u0058", "\u03C7"},	//greek small letter chi
                                    {"\u0059", "\u028F"},	//semi-high front rounded vowel
                                    {"\u005A", "\u0291"},	//voiced alveolo-palatal laminal fricative
                                    {"\u005B", "\u0028"},	//left parenthesis
                                    {"\\u005C", "\u027E"},	//voiced alveolar flap or tap
                                    {"\u005D", "\u0029"},	//right parenthesis
                                    {"\u007B", "\u005B"},	//left square bracket
                                    {"\u007C", "\u027C"},	//voiced strident apico-alveolar trill
                                    {"\u007D", "\u005D"},	//right square bracket
                                    {"\u00C5", "\u0252"},	//low back rounded vowel
                                    {"\u00C7", "\u02A8"},	//voiceless alveolo-palatal affricate
                                    {"\u00E0", "\u0282"},	//voiceless retroflex fricative
                                    {"\u00E5", "\u0250"},	//low central unrounded vowel
                                    {"\u00E8", "\u02A6"},	//voiceless dental affricate
                                    {"\u00EC", "\u02A7"},	//voiceless postalveolar affricate
                                    {"\u00F1", "\u031A"},	//No audible release
                                    {"\u00F2", "\u02A4"},	//voiced postalveolar affricate
                                    {"\u00F9", "\u02A3"},	//voiced dental affricate
                                    {"\u2020", "\u0287"},	//dental click
                                    {"\u00A7", "\u031D"},	//Raised
                                    // CHANGED 04/09/03
                                    {"\u00B7", "\u02C8"},	//Primary Stress
                                    {"\u00AE", "\u0279"},	//voiced alveolar approximant
                                    {"\u00B4", "\u0259"},	//schwa (mid-central unrounded vowel)
                                    {"\u00A8", "\u026F"},	//high back unrounded vowel
                                    {"\u2260", "\u207F"},	//Nasal release
                                    {"\u00D8", "\u00F8"},	//latin small letter o with stroke
                                    {"\u00B1", "\u0274"},	//voiced uvular nasal
                                    {"\u00A5", "\u028E"},	//voiced lateral approximant
                                    {"\u2202", "\u0256"},	//voiced retroflex stop
                                    {"\u2211", "\u028D"},	//voiceless rounded labiovelar approximant
                                    {"\u220F", "\u0275"},	//rounded mid-central vowel, i.e. rounded schwa
                                    {"\u00E6", "\u026D"},	//voiced retroflex lateral
                                    {"\u0153", "\u00E6"},	//latin small letter ae
                                    {"\u03C0", "\u0153"},	//latin small ligature oe
                                    {"\u00AA", "\u02CC"},	//Secondary stress
                                    {"\u00F8", "\u0277"},	//semi-high back rounded vowel
                                    {"\u00BF", "\u0295"},	//voiced pharyngeal fricative
                                    {"\u00A1", "\u0329"},	//Syllabic
                                    {"\u00AC", "\u026C"},	//voiceless alveolar fricative
                                    {"\u0192", "\u025F"},	//voiced palatal stop
                                    {"\u2248", "\u002A"},	//Asterisk
                                    {"\u2206", "\u0293"},	//palatalized voiced postalveolar fricative
                                    {"\u00AB", "\u027D"},	//voiced retroflex flap
                                    {"\u00BB", "\u027A"},	//voiced lateral flap
                                    {"\u00D5", "\u0261"},	//voiced velar stop
                                    {"\u201D", "\u007B"},	//left curly bracket
                                    {"\u2019", "\u007D"},	//right curly bracket
                                    {"\u203A", "\u0334"},	//Velarized or pharyngealized
                                    {"\u2030", "\u025C"},	//lower-mid central unrounded vowel
                                    {"\u00C2", "\u0281"},	//voiced uvular fricative or approximant
                                    {"\u00C1", "\u0289"},	//high central unrounded vowel
                                    {"\u00CB", "\u028B"},	//voiced labiodental approximant
                                    {"\u00C8", "\u0269"},	//semi-high front unrounded vowel
                                    {"\u00CD", "\u0267"},	//voiceless coarticulated velar and palato-alveolar fricative
                                    {"\u00CC", "\u0172"},	//latin small letter h with stroke
                                    {"\u00D3", "\u026E"},	//voiced lateral fricative
                                    {"\uF8FF", "\u0297"},	//palatal click
                                    {"\u02C6", "\u0272"},	//voiced palatal nasal
                                    {"\u02D9", "\u0265"},	//voiced rounded palatal approximant
                                    // CHANGED 04/09/03
                                    {"\u02DB", "\u026A"},	//semi-high front unrounded vowel
                                    {"\u02C7", "\u028A"}, 	//semi-high back rounded vowel
                                    // ADDED 04/09/03
                                    {"\u00B0", "\u0361"},       // Ligature tie
                                    {"\u2022", "\u0361"},       // Ligature tie
                                    {"\u221E", "\u0325"}	// voiceless
                                    
                                };
                                
    static String[][] SIL_IPA_AFFRICATES = {
                                            {"dz\u0361", "\u02A3"},         // dz: voiced dental affricate
                                            {"d\u0292\u0361", "\u02A4"},    // voiced postalveolar affrictate
                                            {"d\u0291\u0361", "\u02A5"},    // voiced alveolo-palatal affricate
                                            {"ts\u0361", "\u02A6"},         // ts: voiceless dental affricate
                                            {"t\u0283\u0361", "\u02A7"},    // voiceless postalveolar affrictate 
                                            {"t\u0255\u0361", "\u02A8"}     // voiceless alveolo-palatal affricate
                                           };
        
    static char KILLED_CHAR = '_';
                                    
    /** Creates a new instance of CharsetConverter */
    public CharsetConverter() {
    }
    
    public static String convert(String sourceString, String fontname){
        return convert(sourceString, fontname, true);
    }
    
    public static String convert(String sourceString, String fontname, boolean removeKilledChars){
        String[][] fontMap = null;
        String targetString = sourceString;
        if (fontname.equals("HiatTimes")){
            fontMap = HIAT_TIMES;
        } else if (fontname.equals("LouisWill")){
            fontMap = LOUIS_WILL;
        } else if (fontname.equals("SIL_IPA")){
            fontMap = SIL_IPA;
        } else if (fontname.equals("SIL_IPA_AFFRICATES")){
            fontMap = SIL_IPA_AFFRICATES;
        }
        
        if (fontMap!=null){
            for (int pos=0; pos<fontMap.length; pos++){
                String replace = fontMap[pos][0];                
                String replacement = fontMap[pos][1];
                targetString = replace(targetString, replace, replacement);
            }            
        }
       
        if (removeKilledChars){
            targetString = removeKilledCharacters(targetString);
        }
        return targetString;
    }
    
    public static String removeKilledCharacters(String sourceString){
        StringBuffer sb = new StringBuffer();
        for (int pos=0; pos<sourceString.length(); pos++){
            char c = sourceString.charAt(pos);
            if (c!=KILLED_CHAR){
                sb.append(c);
            }
        }
        return sb.toString();           
    }
    
    public static String replace (String str, String pattern, String replace){
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
    
        if (pattern.charAt(0)=='\u2022'){
            //System.out.println("Replacing " + Integer.toString(replace.charAt(0)));
        }
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }
}
