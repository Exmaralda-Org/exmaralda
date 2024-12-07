/*
 * Internationalizer.java
 *
 * Created on 25. April 2005, 13:01
 */

package org.exmaralda.common.helpers;

import org.xml.sax.*;
import java.util.*;
import javax.swing.DefaultComboBoxModel;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.SAXParser;  



/**
 *
 * @author  thomas
 */
public class Internationalizer {
    
    //public static final String[] LANGUAGES = {"de", "en", "fr"};
     
    //static String INTERNATIONALIZATION_FILENAME = "/org/exmaralda/partitureditor/jexmaraldaswing/Internationalization.xml";
    static String INTERNATIONALIZATION_FILENAME = "/org/exmaralda/common/helpers/Internationalization.xml";
    
    static Hashtable translationPairs = new Hashtable();
    
    static boolean internationalizeAll = true;
    
    public static String[][] LANGUAGES;

    public String getINTERNATIONALIZATION_FILENAME() {
        return INTERNATIONALIZATION_FILENAME;
    }

    static {
        try{
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            XMLReader xmlReader = null;
            // Create a JAXP SAXParser
            SAXParser saxParser = spf.newSAXParser();
            // Get the encapsulated SAX XMLReader
            xmlReader = saxParser.getXMLReader();
            // Set the ContentHandler of the XMLReader
            InternationalizationSaxHandler handler = new InternationalizationSaxHandler();
            xmlReader.setContentHandler(handler);
            // Tell the XMLReader to parse the XML document
            java.io.InputStream is = Internationalizer.class.getResourceAsStream(INTERNATIONALIZATION_FILENAME);
            xmlReader.parse(new org.xml.sax.InputSource(is));     

            /*String SETTINGS_FILENAME = System.getProperty("user.home") + System.getProperty("file.separator") + "exmaralda.settings";
            java.io.FileInputStream fis = new java.io.FileInputStream(SETTINGS_FILENAME);
            java.util.Properties settings = new java.util.Properties();
            settings.load(fis);                       
            String language = settings.getProperty("LANGUAGE","en");*/
            
            String language = java.util.prefs.Preferences.userRoot().
                                node("org.sfb538.exmaralda.PartiturEditor").
                                get("LANGUAGE","en");
            
            translationPairs = handler.getTranslationPairs(language);
            
            LANGUAGES = handler.getLanguages();
        }
        catch(Exception se){
            System.out.println("Error reading internationalization file:");
            System.out.println(se.getLocalizedMessage());
            se.printStackTrace();
        }
    }

    public static String getString(String source){
        if (source==null) {return "";}
        if (translationPairs.containsKey(source)){
            return ((String)translationPairs.get(source));
        } else {
            String modifiedSource = source.trim();
            //System.out.println("Modified source: " + modifiedSource);
            while ((modifiedSource.length()>1) && (!Character.isLetterOrDigit(modifiedSource.charAt(modifiedSource.length()-1)))){
                modifiedSource = modifiedSource.substring(0, modifiedSource.length()-1);
                //System.out.println("Modified source: " + modifiedSource);
            }
            if (translationPairs.containsKey(modifiedSource)){
                return ((String)translationPairs.get(modifiedSource));
            }
        }
        return source;
    }
    
    public static int getIndexForLanguage(String language){
        for (int i=0; i<LANGUAGES.length; i++){
            if (LANGUAGES[i][0].equals(language)){
                return i;
            }
        }
        return -1;
    }

    public static String getLanguageForIndex(int index){
        if ((index<0) || (index>=LANGUAGES.length)){
            return "en";
        }
        return LANGUAGES[index][0];
    }
    
    public static DefaultComboBoxModel getLanguagesForComboBox(){
        DefaultComboBoxModel result = new DefaultComboBoxModel();
        for (int pos=0; pos<LANGUAGES.length; pos++){
            result.addElement(LANGUAGES[pos][1]);
        }
        return result;
    }
    
    public static void internationalizeDialogToolTips(javax.swing.JDialog dialog){
        dialog.setTitle(Internationalizer.getString(dialog.getTitle()));        
        java.awt.Component[] comps = dialog.getComponents();
        internationalizeComponentToolTips(comps);
        dialog.pack();
    }
    
    
    public static void internationalizeComponentToolTips(java.awt.Component[] comps){
        for (int pos=0; pos<comps.length; pos++){
            java.awt.Component comp = comps[pos];
            internationalizeComponentToolTips(comp);
        }
    }
    
    public static void internationalizeComponentToolTips(java.awt.Component comp){
        if (comp instanceof java.awt.Container){
            java.awt.Component[] comps2 = ((java.awt.Container)(comp)).getComponents();                
            internationalizeComponentToolTips(comps2);
        }
        if (comp instanceof javax.swing.JLabel){
            javax.swing.JLabel label = ((javax.swing.JLabel)(comp));
            String lText = label.getText();
            if ((lText!=null) && (lText.length()>0)){
                label.setToolTipText(getString(lText));
                if (internationalizeAll){
                    label.setText(getString(lText));
                }
            }
        }
        if (comp instanceof javax.swing.JCheckBox){
            javax.swing.JCheckBox checkBox = ((javax.swing.JCheckBox)(comp));
            checkBox.setToolTipText(getString(checkBox.getText()));
            if (internationalizeAll){
                checkBox.setText(getString(checkBox.getText()));
            }
        }
        if (comp instanceof javax.swing.JRadioButton){
            javax.swing.JRadioButton radioButton = ((javax.swing.JRadioButton)(comp));
            radioButton.setToolTipText(getString(radioButton.getText()));
            if (internationalizeAll){
                radioButton.setText(getString(radioButton.getText()));
            }
        }
        if (comp instanceof javax.swing.JButton){
            javax.swing.JButton button = ((javax.swing.JButton)(comp));
            String bText = button.getText();
            if ((bText!=null) && (bText.length()>0)){
                button.setToolTipText(getString(bText));
                if (internationalizeAll){
                    button.setText(getString(bText));
                }
            }
        }
        if (comp instanceof javax.swing.JPanel){
            javax.swing.JPanel panel = ((javax.swing.JPanel)(comp));
            if (panel.getBorder() instanceof javax.swing.border.TitledBorder){
                javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)(panel.getBorder());
                panel.setToolTipText(getString(border.getTitle()));
                if (internationalizeAll){
                    border.setTitle(getString(border.getTitle()));
                }                    
            }
        }
        if (comp instanceof javax.swing.JList){
            javax.swing.JList list = ((javax.swing.JList)(comp));
            if (list.getBorder() instanceof javax.swing.border.TitledBorder){
                javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)(list.getBorder());
                list.setToolTipText(getString(border.getTitle()));
                if (internationalizeAll){
                    border.setTitle(getString(border.getTitle()));
                }                    
            }
        }
        if (comp instanceof javax.swing.JScrollPane){
            javax.swing.JScrollPane scrollPane = ((javax.swing.JScrollPane)(comp));
            if (scrollPane.getBorder() instanceof javax.swing.border.TitledBorder){
                javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)(scrollPane.getBorder());
                scrollPane.setToolTipText(getString(border.getTitle()));
                if (internationalizeAll){
                    border.setTitle(getString(border.getTitle()));
                }                    
            }
        }        
        if (comp instanceof javax.swing.JTabbedPane){
            javax.swing.JTabbedPane tabbedPane = ((javax.swing.JTabbedPane)(comp));
            for (int pos=0; pos<tabbedPane.getTabCount(); pos++){
                tabbedPane.setToolTipTextAt(pos, Internationalizer.getString(tabbedPane.getTitleAt(pos)));
                if (internationalizeAll){
                    tabbedPane.setTitleAt(pos, Internationalizer.getString(tabbedPane.getTitleAt(pos)));
                }
            }
        }
        
    }
    
}
