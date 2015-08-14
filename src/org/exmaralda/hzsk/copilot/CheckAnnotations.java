/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.hzsk.copilot;

/**
 *
 * @author Schmidt
 */

import java.io.*;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class CheckAnnotations extends AbstractCorpusProcessor {
    
    StringBuffer out = new StringBuffer();
    HashSet<String> categories = new HashSet<String>();
    
    /** Creates a new instance of CheckAnnotations */
    public CheckAnnotations() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            CheckAnnotations ca = new CheckAnnotations();
            ca.doIt(false);
            ca.output();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processTranscription(BasicTranscription bt) {
        boolean changed = false;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier t = bt.getBody().getTierAt(pos);
            String cat = t.getCategory();
            if (!(categories.contains(cat))){
                out.append(cat + "\n");
            }
            categories.add(cat);
            
            if (cat.equals("en")){
               t.setCategory("eng");
               changed = true;
            } else if (cat.equals("de")){
               t.setCategory("deu");
               changed = true;                
            } else if (cat.equals("k en") || cat.equals("k-en")){
               t.setCategory("k-eng");
               changed = true;                
            } else if (cat.equals("nn en")){
               t.setCategory("nn-eng");
               changed = true;                
            }
        }
        if (changed){
            try {
                bt.writeXMLToFile(currentFilename, "none");
            } catch (IOException ex) {
                Logger.getLogger(CheckAnnotations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void output(){
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(AbstractCorpusProcessor.CORPUS_BASEDIRECTORY, "annotations.txt"));
            fos.write(out.toString().getBytes());
            fos.close();
            for (String c : categories){
                System.out.println(c);
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public void processTranscription(SegmentedTranscription st) {
    }

    
}
