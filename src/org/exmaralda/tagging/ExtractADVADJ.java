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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class ExtractADVADJ {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new ExtractADVADJ().doit();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ExtractADVADJ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ExtractADVADJ.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ExtractADVADJ.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String INPUT = "Z:\\TAGGING\\WÖRTERBÜCHER\\DeReKo-2014-II-MainArchive-STT.100000.freq";
    String OUTPUT = "Z:\\TAGGING\\WÖRTERBÜCHER\\ADVADJ.txt";

    HashSet<String> ADV = new HashSet<String>();
    HashSet<String> ADJ = new HashSet<String>();
    
    private void doit() throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileInputStream fis = new FileInputStream(new File(INPUT));
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine;
        System.out.println("Started reading document");
        int lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            String[] bits = nextLine.split("\t");
            String pos = bits[2];
            if ("ADV".equals(pos)){
                ADV.add(bits[0]);
                System.out.println("ADV: " + bits[0]);
            }
            if ("ADJD".equals(pos) || "ADJA".equals(pos)){
                ADJ.add(bits[0]);
                System.out.println("ADJ: " + bits[0]);
            }
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");
        
        ADV.retainAll(ADJ);
        write(ADV, OUTPUT);
    }   

    private void write(HashSet<String> entries, String filename) throws FileNotFoundException, IOException {
        System.out.println("started writing document... ");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        for (String thisEntry :entries){
            int count = 0;
            fos.write(thisEntry.getBytes("UTF-8"));
            fos.write(System.getProperty("line.separator").getBytes("UTF-8"));
        }
        fos.close();
        System.out.println("document written.");
    }
}
