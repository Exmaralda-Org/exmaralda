/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class CharacterInventory {

    File inputDirectory;
    String inputSuffix;
    
    File[] inputFiles;
    
    File outputFile;
    
    HashSet<Character> inventory = new HashSet<Character>();
    
    

    
    public CharacterInventory(String[] args){
        inputDirectory = new File(args[0]);
        inputSuffix = args[1];
        
        outputFile = new File(args[2]);
        
        inputFiles = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(inputSuffix.toUpperCase());
            }            
        });
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CharacterInventory aa = new CharacterInventory(args);
            aa.processFiles();
        } catch (SAXException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CharacterInventory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void processFiles() throws UnsupportedEncodingException, FileNotFoundException, IOException, JDOMException, SAXException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        XPath xp = XPath.newInstance("//contribution/descendant::text()");
        for (File inputFile : inputFiles){
            System.out.println("Reading " + inputFile.getName());                        
            Document xmlDocument = FileIO.readDocumentFromLocalFile(inputFile.getAbsolutePath());
            List texts = xp.selectNodes(xmlDocument);
            for (Object o : texts){
                Text t = (Text)o;
                String txt = t.getText();
                for (char c : txt.toCharArray()){
                    inventory.add(c);
                }
            }            
            
        }
        
        System.out.println("started writing output file...");
        FileOutputStream fos = new FileOutputStream(outputFile);
        for (Character c : inventory){
            fos.write((c.toString() + "\t" + String.format("%x", new Integer(c.charValue())) + System.getProperty("line.separator")).getBytes("UTF-8"));
        }
        fos.close();
        System.out.println("document written.");
    }

}
