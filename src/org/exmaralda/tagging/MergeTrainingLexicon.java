/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class MergeTrainingLexicon {

    
    HashMap<String, ArrayList<String[]>> entries = new HashMap<String, ArrayList<String[]>>();

    String DEREKO_LEXICON = "Z:\\TAGGING\\WÖRTERBÜCHER\\DeReKo_GOOD.freq";
    String CLOSED_LISTS_DIRECTORY = "Z:\\TAGGING\\closedlists";
    String TRAINING_LEXICON = "Z:\\TAGGING\\TRAINING\\goldstandard_lexicon.txt";
    String OUTPUT_FILE = "Z:\\TAGGING\\TRAINING\\lexicon.txt";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            MergeTrainingLexicon mtl = new MergeTrainingLexicon();
            if (args.length==4){
                mtl.DEREKO_LEXICON = args[0];
                mtl.CLOSED_LISTS_DIRECTORY = args[1];
                mtl.TRAINING_LEXICON = args[2];
                mtl.OUTPUT_FILE = args[3];
            }
            mtl.doit();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MergeTrainingLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MergeTrainingLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MergeTrainingLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        
        //********************************************
        FileInputStream fis = new FileInputStream(new File(DEREKO_LEXICON));
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine;
        System.out.println("Started reading DeReKo document");
        int lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            String[] bits = nextLine.split("\t");
            add(bits[0], bits[1], bits[2]);
            lineCount++;
        }
        br.close();
        System.out.println("DeReKo Document read.");
        
        //********************************************
        FileInputStream fis3 = new FileInputStream(new File(TRAINING_LEXICON));
        InputStreamReader isr3 = new InputStreamReader(fis3);
        BufferedReader br3 = new BufferedReader(isr3);
        String nextLine3;
        System.out.println("Started reading training document");
        int lineCount3=0;
        while ((nextLine3 = br3.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount3==0 && nextLine3.charAt(0)=='\uFEFF'){
                nextLine3 = nextLine3.substring(1);
            }
            String[] bits = nextLine3.split("\t");
            for (int pos=1; pos<bits.length-1; pos++){
                add(bits[0], bits[pos+1], bits[pos]);
            }
            lineCount3++;
        }
        br.close();
        System.out.println("training document read.");
        
        //********************************************
        File[] txt_files = new File(CLOSED_LISTS_DIRECTORY).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".TXT");
            }            
        });
        for (File f : txt_files){
            System.out.println("Processing " + f.getName());
            String pos = f.getName().substring(0,f.getName().indexOf("."));

            FileReader fr = new FileReader(f);
            BufferedReader br2 = new BufferedReader(fr);
            String nextLine2 = new String();
            while ((nextLine2 = br2.readLine()) != null) {
                // ignore multiwords and starred entries
                if ((nextLine2.trim().length()==0) || (nextLine2.trim().contains(" ")) || (nextLine2.contains("*"))) continue;
                add(nextLine2.trim(), nextLine2.trim(), pos);
                
            }
            br2.close();
        }
        //********************************************

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
                sb.append(" ");
                sb.append(x[1]);                
            }
            sb.append("\n");
        }
        
        //sb.append(".\t$.\t.\n");
        
        FileOutputStream fos = new FileOutputStream(OUTPUT_FILE);
        fos.write(sb.toString().getBytes());       
        fos.close();                
        
        System.out.println("Output written to " + OUTPUT_FILE);

        
        
    }

    private void add(String form, String lemma, String pos) {
            if (!entries.containsKey(form)){
                ArrayList<String[]>  newEntry = new ArrayList<String[]>();
                entries.put(form, newEntry);
            }
            ArrayList<String[]> entry = entries.get(form);
            String[] thisEntry = {pos, lemma};
            //abandoned	JJ abandoned	VBD abandon	VBN abandon
            boolean contains = false;
            for (String[] x : entry){
                //System.out.println(x[0] + " = " + thisEntry[0]);
                if (x[0].equals(thisEntry[0])){
                    //System.out.println("!!!CONTAINS!!!");
                    contains=true;
                    break;
                }
            }
            if (!contains){
                entry.add(thisEntry);
            }
    }
}
