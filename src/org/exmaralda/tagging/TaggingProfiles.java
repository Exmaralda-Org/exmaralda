/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.util.prefs.Preferences;

/**
 *
 * @author thomas
 */
public class TaggingProfiles {

    public static String HIAT_WORDS_PUNCTUATION_SEGMENTATION_XPATH = "//segmentation[@name='SpeakerContribution_Utterance_Word']/ts";
    public static String HIAT_WORDS_PUNCTUATION_TOKEN_XPATH = "descendant::*[not(self::ats) "
            + "and not(descendant::*) and not(text()='(') and not(text()=')') and not(text()='/') and string-length(normalize-space(text()))>0]";

    public static String GENERIC_WORDS_PUNCTUATION_SEGMENTATION_XPATH = "//segmentation[@name='SpeakerContribution_Word']/ts";
    public static String GENERIC_WORDS_PUNCTUATION_TOKEN_XPATH = "descendant::*[not(self::ats) and not(descendant::*) "
            + "and not(text()='(') and not(text()=')') and not(text()='/') and string-length(normalize-space(text()))>0]";

    public static String CGAT_MINIMAL_WORDS_SEGMENTATION_XPATH = "//segmentation[@name='SpeakerContribution_Word']/ts";
    public static String CGAT_MINIMAL_WORDS_TOKEN_XPATH = "descendant::*[not(self::ats) and not(descendant::*) "
            + "and not(text()='(') and not(text()=')') and not(text()='/') and string-length(normalize-space(text()))>0]";

    public static String PREFERENCES_NODE = "org.sfb538.exmaralda.treeTagger";

    public static String getSegmentationXPathForProfile(String profileName){
        switch (profileName) {
            case "HIAT transcription - words and punctuation":
                return HIAT_WORDS_PUNCTUATION_SEGMENTATION_XPATH;
            case "Generic - words and punctuation":
                return GENERIC_WORDS_PUNCTUATION_SEGMENTATION_XPATH;
            case "cGAT minimal transcription - words":            
                return CGAT_MINIMAL_WORDS_SEGMENTATION_XPATH;
            default:
                break;
        }
        return "";
    }

    public static String getTokenXPathForProfile(String profileName){
        switch (profileName) {
            case "HIAT transcription - words and punctuation":
                return HIAT_WORDS_PUNCTUATION_TOKEN_XPATH;
            case "Generic - words and punctuation":
                return GENERIC_WORDS_PUNCTUATION_TOKEN_XPATH;
            case "cGAT minimal transcription - words":            
                return CGAT_MINIMAL_WORDS_TOKEN_XPATH;
            default:
                break;
        }
        return "";
    }

    public static void writePreferences(String directory, String parameterFile, String parameterFileEncoding, 
            String abbreviationsFile, String abbreviationsFileEncoding, String[] options) {

        Preferences preferences = java.util.prefs.Preferences.userRoot().node(PREFERENCES_NODE);
        preferences.put("directory", directory);
        preferences.put("parameter-file", parameterFile);
        preferences.put("parameter-file-encoding", parameterFileEncoding);
        preferences.put("abbreviations-file", abbreviationsFile);
        preferences.put("abbreviations-file-encoding", abbreviationsFileEncoding);
        if (options!=null){
            preferences.putBoolean("lemma-option", false);
            preferences.putBoolean("token-option", false);
            preferences.putBoolean("nounkown-option", false);
            for (String o : options){
                if (o.equals("-lemma")){
                    preferences.putBoolean("lemma-option", true);
                } else if (o.equals("-token")){
                    preferences.putBoolean("token-option", true);
                } else if (o.equals("-no-unknown")){
                    preferences.putBoolean("nounknown-option", true);
                }
            }
        }        
        
    }

    public static void writePreferences(String directory, String parameterFile, String parameterFileEncoding, String[] options) {
        writePreferences(directory, parameterFile, parameterFileEncoding, "", "", options);
    }

    public static void writePreferences(String directory,
            String parameterFile, String parameterFileEncoding, String abbreviationsFile, String abbreviationsFileEncoding, String[] options,
            String taggingProfileName, boolean writeSextant, boolean integrateSextant, String sextantSuffix) {

        writePreferences(directory, parameterFile, parameterFileEncoding, abbreviationsFile, abbreviationsFileEncoding, options);

        Preferences preferences = java.util.prefs.Preferences.userRoot().node(PREFERENCES_NODE);
        preferences.put("tagging-profile-name", taggingProfileName);
        preferences.putBoolean("write-sextant", writeSextant);
        preferences.putBoolean("integrate-sextant", integrateSextant);
        preferences.put("sextant-suffix", sextantSuffix);
        
        
    }


    public static void writePreferences(String directory,
            String parameterFile, String parameterFileEncoding, String[] options,
            String taggingProfileName, boolean writeSextant, boolean integrateSextant, String sextantSuffix) {
        writePreferences(directory, parameterFile, parameterFileEncoding, "", "", options, taggingProfileName, writeSextant, integrateSextant, sextantSuffix);    
    }


}
