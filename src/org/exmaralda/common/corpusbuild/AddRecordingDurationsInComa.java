/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
import org.exmaralda.partitureditor.sound.JMFPlayer;


/**
 *
 * @author thomas
 */
public class AddRecordingDurationsInComa {

    public static String CORPUS_FILENAME = "S:\\TP-Z2\\DATEN\\MAPTASK\\Map_Task_Aufnahmen\\MAPTASK.coma";
    public static String CORPUS_PATH = "S:\\TP-Z2\\DATEN\\MAPTASK\\Map_Task_Aufnahmen\\";
    public static String OUT_FILENAME = "S:\\TP-Z2\\DATEN\\MAPTASK\\Map_Task_Aufnahmen\\MAPTASK.coma";

    JMFPlayer player = new JMFPlayer();

    GUID guido = new GUID();

    public AddRecordingDurationsInComa() {
    }

    void doit() throws JDOMException, IOException{
        Document corpus = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(CORPUS_FILENAME);
        XPath xp1 = XPath.newInstance("//Communication/Recording/Media/NSLink");
        List allSoundFiles = xp1.selectNodes(corpus);
        for (Object o : allSoundFiles){
            Element soundFile = (Element)o;

            String soundPath = CORPUS_PATH + soundFile.getText();
            System.out.println(soundPath);
            player.setSoundFile(soundPath);

            Element communicationElement = soundFile.getParentElement().getParentElement();
            if (communicationElement.getChild("RecordingDuration")==null){
                Element durEl = new Element("RecordingDuration");
                communicationElement.addContent(durEl);
                durEl.setText(Long.toString(Math.round(player.getTotalLength()*1000.0)));
            } else {
                communicationElement.getChild("RecordingDuration").setText(Long.toString(Math.round(player.getTotalLength()*1000.0)));
            }

           //System.out.println("***********************");
       }
       org.exmaralda.common.jdomutilities.IOUtilities.writeDocumentToLocalFile(OUT_FILENAME, corpus);
    }
    
    public static void main(String[] args){
        try {
            new AddRecordingDurationsInComa().doit();
            System.exit(0);
        } catch (JDOMException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AddRecordingDurationsInComa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
class AudioFilenameFilter implements java.io.FilenameFilter {

    public boolean accept(File dir, String name){
        return (name.endsWith("wav") || name.endsWith("mp3"));
    }
}

    

