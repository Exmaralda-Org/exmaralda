/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.tokenlist.HashtableTokenList;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class GenerateFOLKWordlists {

    File transcriptDirectory;
    HashtableTokenList tokenlist = new HashtableTokenList();
            
    public GenerateFOLKWordlists(String transcriptPath) {
        transcriptDirectory = new File(transcriptPath);
    }
    
    void makeWordlist() throws JDOMException, IOException{
        File[] transcriptFiles = transcriptDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".flk")||name.toLowerCase().endsWith(".fln"));
            }               
        });
        tokenlist.readWordsFromFolkerFiles(transcriptFiles);        
    }
    
    void writeWordList(String xmlPath) throws IOException{
        tokenlist.write(new File(xmlPath));
    }

    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            if (args.length!=2){
                System.out.println("Usage: GenerateFOLKWordlists transcriptDirectory output.xml");
                System.exit(0);
            }
            GenerateFOLKWordlists gfw = new GenerateFOLKWordlists(args[0]);
            gfw.makeWordlist();
            gfw.writeWordList(args[1]);
        } catch (JDOMException ex) {
            Logger.getLogger(GenerateFOLKWordlists.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GenerateFOLKWordlists.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }
}
