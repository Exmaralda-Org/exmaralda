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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Schmidt
 */
public class CleanDerekoLexicon {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new CleanDerekoLexicon().doit();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CleanDerekoLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CleanDerekoLexicon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CleanDerekoLexicon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String INPUT = "Z:\\TAGGING\\W÷RTERB‹CHER\\DeReKo-2014-II-MainArchive-STT.100000.freq";
    String OUTPUT_GOOD = "Z:\\TAGGING\\W÷RTERB‹CHER\\DeReKo_GOOD.freq";
    String OUTPUT_BAD = "Z:\\TAGGING\\W÷RTERB‹CHER\\DeReKo_BAD.freq";

    ArrayList<String[]> allEntries = new ArrayList<String[]>();
    ArrayList<String[]> badEntries = new ArrayList<String[]>();
    ArrayList<String[]> additionalEntries = new ArrayList<String[]>();
    
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
            allEntries.add(bits);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");
        
        String[] closedListCategories = {"ADV", "KOKOM", "KON", "KOUI", "KOUS", "PWAV", "SEQ", "SPELL", 
                                        "NGHES", "NGONO", "NGIRR", "NGONO", "PTKVZ", "PWAV", 
                                        "APPR", "SEQU", "PTKMA", "NGAKW", "ART", "FM", "XY"};
        HashSet<String> clc = new HashSet<String>();
        for (String c : closedListCategories){
            clc.add(c);
        }

        
        for (String[] thisEntry : allEntries){
            //der	die	ART	241408360.16429
            boolean throwMeOut =
                    (thisEntry.length!=4) // Eintr‰ge mit falscher Zahl an Records
                    || (thisEntry[1].equalsIgnoreCase("UNKNOWN"))   // unknown lemmas
                    || (thisEntry[2].equals("XY"))  // XY tags
                    || (!(thisEntry[2].equals("NE") || thisEntry[2].equals("NN")) && (thisEntry[0].matches("^[A-Zƒ÷‹].*"))) // groﬂgeschrieben aber nicht NE oder NN
                    || ((thisEntry[2].equals("NE") || thisEntry[2].equals("NN")) && (thisEntry[0].matches("^[a-z‰ˆ¸].*"))) // NE oder NN aber kleingeschrieben
                    || ((thisEntry[0].matches("^[A-Zƒ÷‹].*")) && (thisEntry[1].matches("^[a-z‰ˆ¸].*"))) // Form groﬂ-, aber Lemma kleingeschrieben
                    || (thisEntry[2].equals("VVIMP")) // VIMP tags
                    || (thisEntry[0].matches(".*[0-9].*")) // alles mit Ziffern
                    || (thisEntry[0].matches(".*[\\%\\$].*")) // alles mit diesen Interpunktionszeichen : %$
                    || (thisEntry[0].matches("^[A-Zƒ÷‹]$")) // einzelne Groﬂbuchstaben
                    || (thisEntry[2].equals("ADV")) // ADV tags
                    || (thisEntry[2].equals("FM")) // FM tags
                    || (thisEntry[2].equals("PAV")) // PAV tags
                    || (thisEntry[2].equals("PROAV")) // PROAV tags
                    || (!(thisEntry[2].equals("NE")) && (thisEntry[0].matches("[A-Zƒ÷‹]+"))) // ganz groﬂgeschriebene Formen, sofern nicht NE
                    || (thisEntry[0].contains(".")) // Formen mit Punkten
                    || (clc.contains(thisEntry[2])) // closed list categories
                    || (thisEntry[2].startsWith("$")); // Interpunktion
            
            if (throwMeOut){
                badEntries.add(thisEntry);
            }
        }
        for (String[] thisEntry : badEntries){
            allEntries.remove(thisEntry);
        }
        
        
        for (String[] thisEntry : allEntries){
            if (thisEntry[2].equals("PTKANT") || thisEntry[2].equals("ITJ")){
                thisEntry[2] = "NGIRR";
            }
            if (thisEntry[2].equals("PIS")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PIAT", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("PIAT")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PIS", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("PDS")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PDAT", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("PDAT")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PDS", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("PPOSAT")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PPOSS", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("PPOSS")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "PPOSAT", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
            if (thisEntry[2].equals("VVINF")){
                String[] additionalEntry = {thisEntry[0], thisEntry[1], "VVFIN", thisEntry[3]};
                additionalEntries.add(additionalEntry);
            }
        }
        
        allEntries.addAll(additionalEntries);
        write(allEntries, OUTPUT_GOOD);
        write(badEntries, OUTPUT_BAD);
    }

    private void write(ArrayList<String[]> entries, String filename) throws FileNotFoundException, IOException {
        System.out.println("started writing document... ");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        for (String[] thisEntry :entries){
            int count = 0;
            for (String bit : thisEntry){
                fos.write(bit.getBytes("UTF-8"));
                count++;
                if (count<thisEntry.length){
                    fos.write("\t".getBytes("UTF-8"));
                } else {
                    fos.write(System.getProperty("line.separator").getBytes("UTF-8"));
                }
            }                
        }
        fos.close();
        System.out.println("document written.");
    }
}
