/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MakeTrainingLexicon {

    static String INPUT_DIRECTORY = "Z:\\TAGGING\\TRAINING\\kleiner_Goldstandard";
    static String OUTPUT_FILE = "Z:\\TAGGING\\TRAINING\\goldstandard_lexicon.txt";
    
    File inputDirectory;
    File outputFile;
    
    public MakeTrainingLexicon(String in, String out) {
        inputDirectory = new File(in);
        outputFile = new File(out);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MakeTrainingLexicon mtl = new MakeTrainingLexicon(INPUT_DIRECTORY, OUTPUT_FILE);
        if (args.length!=2){
            
        } else {
            mtl = new MakeTrainingLexicon(args[0], args[1]);
        }
        try {
            mtl.doit();
        } catch (JDOMException ex) {
            Logger.getLogger(MakeTrainingLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MakeTrainingLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        HashMap<String, ArrayList<String[]>> entries = new HashMap<String, ArrayList<String[]>>();
        File[] files = inputDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".fln");
            }            
        });
        for (File f : files){
            System.out.println("Reading " + f.getAbsolutePath());
            Document doc = FileIO.readDocumentFromLocalFile(f);
            List tokens = XPath.selectNodes(doc, "//w");
            for (Object o : tokens){
                Element token = (Element)o;
                String n = token.getAttributeValue("n");
                if (n==null){
                    n = WordUtilities.getWordText(token);
                }
                String pos = token.getAttributeValue("pos");
                String lemma = token.getAttributeValue("lemma");
                
                String[] ns = n.split(" ");
                String[] poss = pos.split(" ");
                String[] lemmas = lemma.split(" ");
                
                if (!(ns.length==poss.length && poss.length==lemmas.length)){
                    System.out.println("Mismatch: " + IOUtilities.elementToString(token));
                } else {
                    for (int i=0; i<ns.length; i++){
                        if (!entries.containsKey(ns[i])){
                            ArrayList<String[]>  newEntry = new ArrayList<String[]>();
                            entries.put(ns[i], newEntry);
                        }
                        ArrayList<String[]> entry = entries.get(ns[i]);
                        String[] thisEntry = {poss[i], lemmas[i]};
                        //abandoned	JJ abandoned	VBD abandon	VBN abandon
                        boolean contains = false;
                        for (String[] x : entry){
                            if (x[0].equals(thisEntry[0])){
                                contains=true;
                                break;
                            }
                        }
                        if (!contains){
                            entry.add(thisEntry);
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = entries.keySet();
        ArrayList<String> sortedKeySet = new ArrayList<String>();
        sortedKeySet.addAll(keySet);
        Collections.sort(sortedKeySet);
        for (String n : sortedKeySet){
            sb.append(n);
            ArrayList<String[]> entry = entries.get(n);
            for (String[] x : entry){
                sb.append("\t");
                sb.append(x[0]);
                sb.append("\t");
                sb.append(x[1]);                
            }
            sb.append("\n");
        }
        
        sb.append(".\t$.\t.\n");
        
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(sb.toString().getBytes());       
        fos.close();                

        
    }

}
