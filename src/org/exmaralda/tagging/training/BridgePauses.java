/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging.training;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class BridgePauses {

    String inputDir = "F:\\AGD-DATA\\dgd2_data\\transcripts\\FOLK-GOLD";
    String outputDir = "Z:\\TAGGING\\GOLD-STANDARD\\BRIDGE_3_0";   
    double MIN_LENGTH = 3.0;    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new BridgePauses().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(BridgePauses.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BridgePauses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
        File in = new File(inputDir);
        File out = new File(outputDir); 
        boolean created = out.mkdir();
        if (created){
            System.out.println("Created directory " + out.getAbsolutePath() + ".");
        } else {
            File[] allFiles = out.listFiles();
            /*System.out.println("Deleting " + allFiles.length + " files from " + out.getAbsolutePath() + ".");
            for (File f : allFiles){
                f.delete();
            } */       
        }
        
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });

        
        
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts in " + in.getAbsolutePath() + ".");
        for (File transcript : transcriptFiles){
            System.out.println("=================================");
            Document trDoc = FileIO.readDocumentFromLocalFile(transcript);
            
            // 1. Get all speakers
            HashSet<String> allSpeakerIDs = new HashSet<String>();
            List sl = XPath.selectNodes(trDoc, "//speaker");
            for (Object o : sl){
                Element e = (Element)o;
                String id = e.getAttributeValue("speaker-id");
                allSpeakerIDs.add(id);
            }
            
            // 2. bridge pauses for each speaker
            for (String speakerID : allSpeakerIDs){
                List cl = XPath.selectNodes(trDoc, "//contribution[@speaker-reference='" + speakerID + "']");
                double lastEnd = -100.0;
                Element currentContribution = null;
                for (Object o : cl){
                    Element thisContribution = (Element)(o);
                    System.out.println(IOUtilities.elementToString(thisContribution));
                    double thisStart = Double.parseDouble(thisContribution.getAttributeValue("time"));
                    double delta = thisStart - lastEnd;
                    Element endTimeElement = (Element) XPath.selectSingleNode(thisContribution, "descendant::time[last()]");
                    lastEnd = Double.parseDouble(endTimeElement.getAttributeValue("time"));
                    if (delta<MIN_LENGTH){
                        Element pauseElement = new Element("pause");
                        pauseElement.setAttribute("duration", Double.toString(Math.round(delta*100.0)/100.0));                        
                        currentContribution.addContent(pauseElement);
                        List content = thisContribution.removeContent();
                        currentContribution.addContent(content);
                        currentContribution.setAttribute("end-reference", thisContribution.getAttributeValue("end-reference"));
                    } else {
                        currentContribution = thisContribution;
                    }
                }
            }
            
            // 3. Get rid of empty contributions
            List empties = XPath.selectNodes(trDoc, "//contribution[not(*)]");
            for (Object o : empties){
                Element e = (Element)o;
                e.detach();
            }
            
            // 4. Get rid of superfluous pauses
            List pauseConts = XPath.selectNodes(trDoc, "//contribution[not(@speaker-reference)]");
            for (Object o : pauseConts){
                Element pauseC = (Element)o;
                Element precedingC = (Element) XPath.selectSingleNode(pauseC, "preceding-sibling::contribution[@speaker-reference][1]");
                if (precedingC==null) continue;
                
                double pauseStart = Double.parseDouble(pauseC.getAttributeValue("time"));
                Element pauseEndTimeElement = (Element) XPath.selectSingleNode(pauseC, "descendant::time[last()]");
                double pauseEnd = Double.parseDouble(pauseEndTimeElement.getAttributeValue("time"));
                
                double precedingStart = Double.parseDouble(precedingC.getAttributeValue("time"));
                Element precedingEndTimeElement = (Element) XPath.selectSingleNode(precedingC, "descendant::time[last()]");
                double precedingEnd = Double.parseDouble(precedingEndTimeElement.getAttributeValue("time"));
                
                if (pauseStart>=precedingStart && pauseEnd<=precedingEnd){
                    pauseC.detach();
                }
            }

            // write back
            File outFile = new File(new File(outputDir), transcript.getName());
            FileIO.writeDocumentToLocalFile(outFile, trDoc);
            
            
            
        }
        

    }
    
}
