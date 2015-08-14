/*
 * Internationalizer.java
 *
 * Created on 25. April 2005, 13:01
 */

package org.exmaralda.exakt.search.swing;

import java.net.*;
import java.io.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
import javax.swing.DefaultComboBoxModel;

import javax.xml.parsers.SAXParserFactory;  
import javax.xml.parsers.ParserConfigurationException;  
import javax.xml.parsers.SAXParser;  



/**
 *
 * @author  thomas
 */
public class MACOSHarmonizer {
    
    //public static final String[] LANGUAGES = {"de", "en", "fr"};
     
    
    public static void harmonize(java.awt.Component[] comps){
        String os = System.getProperty("os.name").substring(0,3);
        if (!(os.equalsIgnoreCase("mac"))) return;
        for (java.awt.Component comp : comps){
            harmonize(comp);
        }
    }
    
    public static void harmonize(java.awt.Component comp){
        if (comp instanceof javax.swing.JComboBox){
            return;
        }
        if (comp instanceof java.awt.Container){
            java.awt.Component[] comps2 = ((java.awt.Container)(comp)).getComponents();                
            harmonize(comps2);
        }
        if (comp instanceof javax.swing.JButton){
            javax.swing.JButton button = ((javax.swing.JButton)(comp));
            button.putClientProperty("JButton.buttonType", "square");
        } 
        /*if (comp instanceof javax.swing.JComboBox){
            javax.swing.JComboBox combobox = ((javax.swing.JComboBox)comp);
            combobox.putClientProperty("JComboBox.isSquare", true);
        }*/
        
    }
    
}
