/*
 * InternationalizationSaxHandler.java
 *
 * Created on 25. April 2005, 13:26
 */

package org.exmaralda.common.helpers;

import java.util.*;
import org.xml.sax.*;
/**
 *
 * @author  thomas
 */
public class InternationalizationSaxHandler extends org.xml.sax.helpers.DefaultHandler {
    
    String currentItemName = "";
    Hashtable languageHashtables = new Hashtable();
    Vector languages = new Vector();
    
    /** Creates a new instance of InternationalizationSaxHandler */
    public InternationalizationSaxHandler() {
        languageHashtables.put("en", new Hashtable());
    }
    
    public Hashtable getTranslationPairs(String language){
        if (languageHashtables.containsKey(language)){
            return (Hashtable)(languageHashtables.get(language));
        } 
        //return (Hashtable)(languageHashtables.get(language));
        return (Hashtable)(languageHashtables.get("en"));
    }
    
    public String[][] getLanguages(){
        String[][] result = new String[languages.size()][2];
        for (int pos=0; pos<languages.size(); pos++){
            result[pos] = (String[])(languages.elementAt(pos));
        }
        return result;
    }
// ----------------------------------------------------------------------------------------------------------- 
    public void startDocument(){
      System.out.println("started reading Internationalization file...");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("Internationalization file read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        if (name.equals("language")){
            String shortName = atts.getValue("short");
            String longName = atts.getValue("long");
            addLanguage(shortName, longName);
        }
        if (name.equals("item")){
            currentItemName = atts.getValue("id");
        } 
        if (name.equals("translation")){
            String language = atts.getValue("lang");
            String translation = atts.getValue("trans");
            addPair(language, translation);
        }
        
    }
// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){}    
// -----------------------------------------------------------------------------------------------------------    
    public void characters (char buff[], int offset, int length){} 
// ----------------------------------------------------------------------------------------------------------- 
    public void error (SAXParseException e) throws SAXParseException {
        System.out.println("Error: " + e.getMessage());
        throw e;
    }
// ----------------------------------------------------------------------------------------------------------- 
    public void setDocumentLocator (Locator l){}
// ----------------------------------------------------------------------------------------------------------- 
    public void ignorableWhitespace (char buf [], int offset, int len) throws SAXException {}
       
    void addPair(String language, String translation){
        if (!languageHashtables.containsKey(language)){
            languageHashtables.put(language, new Hashtable());
        }
        Hashtable translationPairs = (Hashtable)(languageHashtables.get(language));
        translationPairs.put(currentItemName, translation);
    }
    
    void addLanguage(String shortName, String longName){
        String[] language = {shortName, longName};
        languages.addElement(language);
        if (!languageHashtables.containsKey(shortName)){
            languageHashtables.put(shortName, new Hashtable());
        }
    }
    
}
