/*
 * CheckTierProperties.java
 *
 * Created on 16. Oktober 2006, 14:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.HashSet;


/**
 *
 * @author thomas
 */
public class CalculateTranscribedTime extends AbstractBasicTranscriptionProcessor {
    
    String HEADER = "<tr><th>COMMUNICATION</th><th>TRANSCRIPTION" +
            "</th><th>RECORDING</th>" +
            "<th>minTime</th><th>maxTime</th><</tr>";

    static String CORPUS_PATH = "T:\\TP-Z2\\DATEN\\K2\\0.8\\K2_Corpus.xml";
    static String OUT_FILE = "T:\\TP-Z2\\DATEN\\K2\\0.8\\TranscribedTime.html";
    /** Creates a new instance of CheckTierProperties */
    public CalculateTranscribedTime(String cp) {
        super(cp);
    }
    
    StringBuffer out = new StringBuffer();
    StringBuffer out2 = new StringBuffer();
    HashSet combi = new HashSet();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            CalculateTranscribedTime t = new CalculateTranscribedTime(CORPUS_PATH);
            t.doIt();
            t.output();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void processTranscription(BasicTranscription bt) {
        String communicationName = currentElement.getParentElement().getParentElement().getAttributeValue("Name");
        String transcriptionName = currentElement.getParentElement().getChild("Filename").getText();
        String referencedFile = bt.getHead().getMetaInformation().getReferencedFile();       
        
        Timeline tl = bt.getBody().getCommonTimeline();
        String minTimeID = tl.getMinTimeID();
        String maxTimeID = tl.getMaxTimeID();
        
        String minTime = "-";
        String maxTime = "-";
        String countTLI = Integer.toString(tl.getNumberOfTimelineItems());
 
        if (minTimeID!=null){
            try {            
                minTime = Long.toString(Math.round(tl.getTimelineItemWithID(minTimeID).getTime()));
                maxTime = Long.toString(Math.round(tl.getTimelineItemWithID(maxTimeID).getTime()));
            } catch (JexmaraldaException ex) {
                ex.printStackTrace();
            }
        }
        
        
        out.append("<tr><td>" + communicationName + "</td><td>" + transcriptionName + "</td><td>" + referencedFile + "</td><td>");
        out.append(minTime + "</td><td>" + maxTime + "</td>");
        out.append("</tr>");
    }
    
    void output(){
        try {
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(new File(OUT_FILE));
            fos.write("<html><body><table>".getBytes());
            fos.write(HEADER.getBytes());
            fos.write(out.toString().getBytes());
            fos.write("</table></body></html>".getBytes());            
            fos.close();            
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void processTranscription(SegmentedTranscription st) {
    }        
    
    
}

