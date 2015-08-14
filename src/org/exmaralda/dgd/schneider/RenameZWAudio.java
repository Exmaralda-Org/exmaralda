/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.schneider;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class RenameZWAudio extends AbstractSchneiderProcessor {

    String META_PATH = "Y:\\thomas\\ZW_HE\\Meta\\ZW_HE_Metadata.xml";
    String DGD1_WAV_PATH = "Y:\\media\\audio\\ZW_DGD_1.0";
    String TRANSCRIPT_PATH = "Y:\\thomas\\ZW_HE";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new RenameZWAudio().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(RenameZWAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(RenameZWAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        getMappings(META_PATH);
        File[] wavFiles = new File(DGD1_WAV_PATH).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".wav");
            }       
        });
        
        StringBuffer sb = new StringBuffer();
        for (File wavFile : wavFiles){
            String code = wavFile.getName().substring(0,5);
            String target = old2new.get(code);
            File transcriptFile = new File(new File(TRANSCRIPT_PATH), target + "_SE_01_T_01_DF_01.fln");
            boolean transcriptExists = transcriptFile.exists();
            System.out.print(wavFile.getName() + " --> " + code + " --> " + target);
            if (target==null){
                System.out.println(" --> PROBLEM");                                
            } else if (transcriptExists){
                //System.out.println(" --> UMBENENNEN");
                File renamedFile = new File(new File(DGD1_WAV_PATH + "\\umbenannt"), target + "_SE_01_A_02.WAV");
                System.out.println(" ---> UMBENENNEN IN " + renamedFile.getAbsolutePath());
                sb.append("move " + wavFile.getAbsolutePath() + " " + renamedFile.getAbsolutePath() + System.getProperty("line.separator"));                
                //wavFile.renameTo(renamedFile);
            } else  {               
                File movedFile = new File(new File(DGD1_WAV_PATH + "\\aussortiert"), wavFile.getName());
                System.out.println(" ---> VERSCHIEBEN NACH " + movedFile.getAbsolutePath());
                sb.append("move " + wavFile.getAbsolutePath() + " " + movedFile.getAbsolutePath() + System.getProperty("line.separator"));                
                //wavFile.renameTo(movedFile);
            }
        }
        
        this.logFile = new File(new File(TRANSCRIPT_PATH), "rename2.bat");
        this.writeLogToTextFile(sb);
        
    }
    
}

