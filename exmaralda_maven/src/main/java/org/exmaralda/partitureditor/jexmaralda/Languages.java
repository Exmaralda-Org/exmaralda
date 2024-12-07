package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/*
 * Languages.java
 *
 * Created on 6. Februar 2001, 11:05
 */



/* Revision History
 *  0   06-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd'
 *  1   15-Sep-2003 Fundamental change: we now use the Ethnologue Language Codes
 *                  As these are so numerous, we only work with language codes,
 *                  don't check these anymore etc.
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class Languages extends Vector {

    /*public static final String [] LANGUAGES = 
        {"Afar","Abkhazian","Afrikaans","Amharic","Arabic","Assamese",
        "Aymara","Azerbaijani","Bashkir","Byelorussian","Bulgarian","Bihari","Bislama","Bengali",
        "Tibetan","Breton","Catalan","Corsican","Czech","Welsh","Danish","German","Bhutani","Greek",
        "English","Esperanto","Spanish","Estonian","Basque","Persian","Finnish","Fiji","Faeroese",
        "French","Frisian","Irish","Scots Gaelic","Galician","Guarani","Gujarati","Hausa","Hindi",
        "Croatian","Hungarian","Armenian","Interlingua","Interlingue","Inupiak","Indonesian","Icelandic",
        "Italian","Hebrew","Japanese","Yiddish","Javanese","Georgian","Kazakh","Greenlandic","Cambodian",
        "Kannada","Korean","Kashmiri","Kurdish","Kirghiz","Latin","Lingala","Laothian","Lithuanian",
        "Latvian","Malagasy","Maori","Macedonian","Malayalam","Mongolian","Moldavian","Marathi",
        "Malay","Maltese","Burmese","Nauru","Nepali","Dutch","Norwegian","Occitan","Oromo",
        "Oriya","Punjabi","Polish","Pashto","Portuguese","Quechua","Rhaeto-Romance","Kirundi",
        "Romanian","Russian","Kinyarwanda","Sanskrit","Sindhi","Sangro","Serbo-Croatian","Singhalese",
        "Slovak","Slovenian","Samoan","Shona","Somali","Albanian","Serbian","Siswati","Sesotho","Sudanese",
        "Swedish","Swahili","Tamil","Tegulu","Tajik","Thai","Tigrinya","Turkmen","Tagalog","Setswana",
        "Tonga","Turkish","Tsonga","Tatar","Twi","Ukrainian","Urdu","Uzbek","Vietnamese","Volapuk",
        "Wolof","Xhosa","Yoruba","Chinese","Zulu"};
    
    public static final String [] SORTED_LANGUAGES = 
        {"Abkhazian","Afar","Afrikaans","Albanian","Amharic","Arabic","Armenian",
        "Assamese","Aymara","Azerbaijani","Bashkir","Basque","Bengali","Bhutani","Bihari","Bislama","Breton","Bulgarian","Burmese",
        "Byelorussian","Cambodian","Catalan","Chinese","Corsican","Croatian","Czech","Danish","Dutch","English","Esperanto",
        "Estonian","Faeroese","Fiji","Finnish","French","Frisian","Galician","Georgian","German","Greek","Greenlandic","Guarani",
        "Gujarati","Hausa","Hebrew","Hindi","Hungarian","Icelandic","Indonesian","Interlingua","Interlingue","Inupiak","Irish",
        "Italian","Japanese","Javanese","Kannada","Kashmiri","Kazakh","Kinyarwanda","Kirghiz","Kirundi","Korean","Kurdish",
        "Laothian","Latin","Latvian","Lingala","Lithuanian","Macedonian","Malagasy","Malay","Malayalam","Maltese","Maori","Marathi",
        "Moldavian","Mongolian","Nauru","Nepali","Norwegian","Occitan","Oriya","Oromo","Pashto","Persian","Polish","Portuguese",
        "Punjabi","Quechua","Rhaeto-Romance","Romanian","Russian","Samoan","Sangro","Sanskrit","ScotsGaelic","Serbian",
        "Serbo-Croatian","Sesotho","Setswana","Shona","Sindhi","Singhalese","Siswati","Slovak","Slovenian","Somali","Spanish",
        "Sudanese","Swahili","Swedish","Tagalog","Tajik","Tamil","Tatar","Tegulu","Thai","Tibetan","Tigrinya","Tonga","Tsonga",
        "Turkish","Turkmen","Twi","Ukrainian","Urdu","Uzbek","Vietnamese","Volapuk","Welsh","Wolof","Xhosa","Yiddish","Yoruba","Zulu"};
    
    public static final String [] LANGUAGE_CODES = 
        {"aa","ab","af","am","ar","as","ay","az","ba","be","bg","bh",
        " bi","bn","bo","br","ca","co","cs","cy","da","de","dz","el","en","eo","es","et","eu","fa","fi",
        "fj","fo","fr","fy","ga","gd","gl","gn","gu","ha","hi","hr","hu","hy","ia","ie","ik","in","is",
        "it","iw","ja","ji","jw","ka","kk","kl","km","kn","ko","ks","ku","ky","la","ln","lo","lt","lv",
        "mg","mi","mk","ml","mn","mo","mr","ms","mt","my","na","ne","nl","no","oc","om","or","pa","pl",
        "ps","pt","qu","rm","rn","ro","ru","rw","sa","sd","sg","sh","si","sk","sl","sm","sn","so","sq",
        "sr","ss","st","su","sv","sw","ta","te","tg","th","ti","tk","tl","tn","to","tr","ts","tt","tw",
        "uk","ur","uz","vi","vo","wo","xh","yo","zh","zu"};*/
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new Languages */
    public Languages() {
        super();
    }
  
    /** Creates new Languages from the specified array of strings */
    public Languages(String[] l){
        super(); 
        for (int pos=0; pos<l.length; pos++){
          addLanguage(l[pos]);
        }
    }
    
    /** returns a copy of this Languages */
    public Languages makeCopy(){
        return new Languages(getAllLanguageCodes());
        
    }
    
    /** returns the code for the specified language */
    public static String getCodeForLanguage(String language){
        return language;
        /*for (int i=0; i<LANGUAGES.length; i++){
            if (LANGUAGES[i].equalsIgnoreCase(language)){
                return LANGUAGE_CODES[i];
            }
        }
        return null;*/
    }

    /** returns the code for the specified language */
    public static String getLanguageForCode(String code){
        return code;
        /*for (int i=0; i<LANGUAGE_CODES.length; i++){
            if (LANGUAGE_CODES[i].equals(code)){
                return LANGUAGES[i];
            }
        }
        return null;*/
    } 
    
    /** returns true if code is a valid language code (including '(unknown)') */
    public static boolean isLanguageCode(String code){
        return true;
        /*for (int i=0; i<LANGUAGE_CODES.length; i++){
            if (LANGUAGE_CODES[i].equals(code)){
                return true;
            }
        }
        return false;*/
    }    

    /** returns true if language is a valid language (including 'none') */
    public static boolean isLanguage(String language){
        return true;
        /*for (int i=0; i<LANGUAGES.length; i++){
            if (LANGUAGES[i].equalsIgnoreCase(language)){
                return true;
            }
        }
        return false;*/
    }    
    
    /** adds the specified language to this Languages */
    public void addLanguage(String language){
        addElement(language);
        /*if (isLanguageCode(language)){
            addElement(language);
        } else if (isLanguage(language)){
            addElement(getCodeForLanguage(language));
        }*/
    }

    /** removes the specified language to this Languages */
    public void removeLanguage(String language){        
        /*if ((isLanguageCode(language)) && (containsLanguageCode(language))){*/
            removeElement(language);
        /*} else if ((isLanguage(language)) && (containsLanguage(language))){
            removeElement(getCodeForLanguage(language));
        }*/
    }

    String getLanguageAt(int position){
        return getLanguageCodeAt(position);
        /*return getLanguageForCode((String)elementAt(position));*/
    }

    String getLanguageCodeAt(int position){
        return (String)elementAt(position);
    }
    
    public int getNumberOfLanguages(){
        return size();
    }
    
    public boolean containsLanguage(String language){
        return containsLanguageCode(language);
        /*for (int position=0; position<getNumberOfLanguages(); position++){
            if (getLanguageAt(position).equalsIgnoreCase(language)){
                return true;
            }
        }
        return false;*/
    }

    public boolean containsLanguageCode(String code){
        return this.contains(code);
        /*for (int position=0; position<getNumberOfLanguages(); position++){
            if (getLanguageCodeAt(position).equals(code)){
                return true;
            }
        }
        return false;*/
    }

    public String[] getAllLanguages(){
        return getAllLanguageCodes();
        /*String[] result = new String[getNumberOfLanguages()];
        for (int position=0; position<getNumberOfLanguages(); position++){
            result[position]=(String)(elementAt(position));
        }
        return result;*/
    }

    public String[] getAllLanguageCodes(){
        String[] result = new String[getNumberOfLanguages()];
        for (int position=0; position<getNumberOfLanguages(); position++){
            result[position]=getLanguageCodeAt(position);
        }
        return result;
    }
    
    public String getLanguagesString(){
        String result = new String();
        String[] allLang = getAllLanguages();
        for (int pos=0; pos<allLang.length; pos++){
            result+=allLang[pos];
            if (pos<allLang.length-1){result+="; ";}
        }
        return result;
    }
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    public String toXML(){
        String result=new String();
        for (int i=0; i<getNumberOfLanguages(); i++){
            result+="<language lang=\"";
            result+=getLanguageCodeAt(i);
            result+="\"/>";
        }
        return result;
    }
}