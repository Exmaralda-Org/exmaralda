/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class AssignFOLKAudio {

    String[][] t2r = {
        {"FOLK_E_00011_SE_01_T_01","FOLK_E_00011_SE_01_A_01"},
        {"FOLK_E_00011_SE_01_T_02","FOLK_E_00011_SE_01_A_01"},
        {"FOLK_E_00011_SE_01_T_03","FOLK_E_00011_SE_01_A_01"},
        {"FOLK_E_00011_SE_01_T_04","FOLK_E_00011_SE_01_A_01"},
        {"FOLK_E_00011_SE_01_T_05","FOLK_E_00011_SE_01_A_02"},
        {"FOLK_E_00011_SE_01_T_06","FOLK_E_00011_SE_01_A_02"},
        {"FOLK_E_00011_SE_01_T_07","FOLK_E_00011_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_01","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_02","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_03","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_04","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_05","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_06","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_07","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_08","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_09","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_10","FOLK_E_00021_SE_01_A_01"},
        {"FOLK_E_00021_SE_01_T_11","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_12","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_13","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_14","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_15","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_16","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00021_SE_01_T_17","FOLK_E_00021_SE_01_A_02"},
        {"FOLK_E_00030_SE_01_T_01","FOLK_E_00030_SE_01_A_01"},
        {"FOLK_E_00030_SE_01_T_02","FOLK_E_00030_SE_01_A_01"},
        {"FOLK_E_00030_SE_01_T_03","FOLK_E_00030_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_01","FOLK_E_00055_SE_01_A_01"},
        {"FOLK_E_00055_SE_01_T_02","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_03","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_04","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_05","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_06","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_07","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_08","FOLK_E_00055_SE_01_A_02"},
        {"FOLK_E_00055_SE_01_T_09","FOLK_E_00055_SE_01_A_02"}            
    };
    
    public AssignFOLKAudio(String in, String out, String meta) throws JDOMException, IOException {
        //TranscriptRecordingMapFOLK trans2rec = new TranscriptRecordingMapFOLK(meta);
        //trans2rec.doit();
        Hashtable<String,String> trans2rec = new Hashtable<String,String>();
        for (String[] x : t2r){
            trans2rec.put(x[0], x[1]);
        }
        File[] flns = new File(in).listFiles();
        for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document doc = FileIO.readDocumentFromLocalFile(f);
           
           Element recording = (Element)(XPath.selectSingleNode(doc, "//recording"));
    
           
           //FOLK_E_00007_SE_01_T_01_DF_01.fln
           String transcriptID = f.getName().substring(0,23);
           //String recordingID = trans2rec.transcript2recording.get(transcriptID);
           
           //FOLK_E_00003_SE_01_A_01_DF_01
           String recordingID = f.getName().substring(0,18) + "_A_01";
           if (trans2rec.containsKey(transcriptID)){
               recordingID = trans2rec.get(transcriptID);
           }

           System.out.println("Recording: " + recordingID);
           
           String path = "../../media/audio/FOLK/" + recordingID + "_DF_01.WAV";
           
           recording.setAttribute("path" , path);

           File outFile = new File(new File(out), f.getName());
           FileIO.writeDocumentToLocalFile(outFile, doc);
        }
    }


    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AssignFOLKAudio assignFOLKAudio = new AssignFOLKAudio(
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\3", 
                                                      "C:\\Users\\Schmidt\\Desktop\\FOLK_Werkstatt\\4", 
                                                      "O:\\xml\\events\\Werkstatt\\folk"
                                                      );
        } catch (JDOMException ex) {
            Logger.getLogger(AssignFOLKAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AssignFOLKAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
