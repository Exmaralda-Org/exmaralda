/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.*;
import org.jdom.xpath.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class LevelSmallSelfoverlap extends AbstractTranscriptionProcessor {

    double THE_DIFFERENCE = 0.1;
    String INPUT_DIR = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.1\\";
    String OUTPUT_DIR = "T:\\TP-Z2\\DATEN\\SBCSAE\\0.2\\";
    
    int count = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LevelSmallSelfoverlap lstd = new LevelSmallSelfoverlap();
        lstd.TRANSCRIPTION_PATH = lstd.INPUT_DIR;
        try {
            lstd.process();
            System.out.println("Overall: " + lstd.count);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    

    @Override
    public void processTranscription(Document transcription) throws JDOMException {
        try {
            HashSet<String> speakers = new HashSet<String>();
            List l = new SelfoverlapErrors().speakerXPath.selectNodes(transcription);
            for (Object o : l) {
                Attribute a = (Attribute) o;
                speakers.add(a.getValue());
            }
            for (String speaker : speakers) {
                String xp = "//intonation-unit[@speaker='" + speaker + "']";
                List l2 = XPath.newInstance(xp).selectNodes(transcription);
                double lastEnd = -1;
                Element lastElement = null;
                for (Object o : l2) {
                    Element e = (Element) o;
                    double thisStart = Double.parseDouble(e.getAttributeValue("startTime"));
                    if ((lastEnd != -1) && (thisStart < lastEnd)) {
                        double difference = lastEnd - thisStart;
                        if (difference < 0.2) {
                            lastElement.setAttribute("endTime", Double.toString(thisStart));
                            count++;
                        }
                    }
                    lastEnd = Double.parseDouble(e.getAttributeValue("endTime"));
                    lastElement = e;
                }
            }

            String out = new org.exmaralda.sbcsae.convertStep1.TRN2XML().writeTRN(transcription);
            java.io.FileOutputStream fos = null;
            String outfilename = this.OUTPUT_DIR + this.filename + ".trn";
            System.out.println("Saving " + outfilename);
            System.out.println("started writing document...");
            fos = new java.io.FileOutputStream(new java.io.File(outfilename));
            fos.write(out.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

}
