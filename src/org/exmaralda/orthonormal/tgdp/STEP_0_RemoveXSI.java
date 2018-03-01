/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.tgdp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.JDOMException;

/**
 *
 * @author Thomas_Schmidt
 */
public class STEP_0_RemoveXSI extends AbstractEAFProcessor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new STEP_0_RemoveXSI().doit();
        } catch (IOException ex) {
            Logger.getLogger(STEP_0_RemoveXSI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processFile(File eafFile) throws IOException {
        StringBuilder result = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(eafFile);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String nextLine="";
            while ((nextLine = br.readLine()) != null){
                if ((nextLine.trim().length()>0) && (!(nextLine.contains("xsi:noNamespaceSchemaLocation=\"http://www.mpi.nl/tools/elan/EAFv2.0.xsd\"")))){
                    result.append(nextLine);
                } else if (nextLine.contains("xsi:noNamespaceSchemaLocation=\"http://www.mpi.nl/tools/elan/EAFv2.0.xsd\"")){
                    //<!DOCTYPE logfile SYSTEM "../../../DTD/chatkorpus.dtd">
                    result.append(nextLine.replace("xsi:noNamespaceSchemaLocation=\"http://www.mpi.nl/tools/elan/EAFv2.0.xsd\"", ""));
                    System.out.println("GOT ONE!");
                }
            }
            br.close();
            
            
            org.jdom.Document doc = FileIO.readDocumentFromString(result.toString());
            
            FileIO.writeDocumentToLocalFile(eafFile, doc);
            
        } catch (JDOMException ex) {
            System.out.println(result.toString());
            throw new IOException(ex);
        }
        
    }
    
}
