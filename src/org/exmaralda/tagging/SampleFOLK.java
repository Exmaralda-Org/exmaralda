/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
public class SampleFOLK {


    String SOURCE_DIR = "C:\\Users\\Schmidt\\Desktop\\DGD-RELEASE\\transcripts\\FOLK";
    String TARGET_DIR = "Z:\\TAGGING\\SAMPLE";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ArrayList<Element> allSamples = new ArrayList<Element>();
            SampleFOLK sampleFOLK = new SampleFOLK();
            
            String[] ALLTAG_NICHT_STANDARD = {"FOLK_E_00020", "FOLK_E_00040", "FOLK_E_00143"};
            allSamples.addAll(sampleFOLK.sample(ALLTAG_NICHT_STANDARD, 3, 1000, "ALLTAG_NICHT_STANDARD"));
            
            String[] ALLTAG_STANDARD = {"FOLK_E_00043", "FOLK_E_00046", "FOLK_E_00053"};
            allSamples.addAll(sampleFOLK.sample(ALLTAG_STANDARD, 3, 1000, "ALLTAG_STANDARD"));

            String[] STUTTGART_21 = {"FOLK_E_00064", "FOLK_E_00069"};
            allSamples.addAll(sampleFOLK.sample(STUTTGART_21, 5, 1000, "STUTTGART_21"));
            
            String[] MAPTASKS = sampleFOLK.find("Maptask");
            allSamples.addAll(sampleFOLK.sample(MAPTASKS, 1, 500, "MAPTASK"));

            String[] INTERVIEWS = sampleFOLK.find("Sprachbiografisches Interview");
            allSamples.addAll(sampleFOLK.sample(INTERVIEWS, 1, 1000, "INTERVIEW"));

            String[] PRUEFUNG = sampleFOLK.find("Prüfungsgespräch in der Hochschule");
            allSamples.addAll(sampleFOLK.sample(PRUEFUNG, 1, 500, "PRUEFUNG"));

            String[] BERUFSSCHULE = sampleFOLK.find("Unterrichtsstunde in der Berufsschule");
            allSamples.addAll(sampleFOLK.sample(BERUFSSCHULE, 1, 500, "BERUFSSCHULE"));
            
            String[] WGYM = sampleFOLK.find("Unterrichtsstunde im Wirtschaftsgymnasium");
            allSamples.addAll(sampleFOLK.sample(WGYM, 1, 500, "WIRTSCHAFTSGYMNASIUM"));

            String[] SCHICHT = sampleFOLK.find("Schichtübergabe in einem Krankenhaus");
            allSamples.addAll(sampleFOLK.sample(SCHICHT, 1, 1000, "SCHICHTUEBERGABE"));

            String[] TRAINING = sampleFOLK.find("Training in einer Hilfsorganisation");
            allSamples.addAll(sampleFOLK.sample(TRAINING, 1, 1000, "TRAINING"));

            String[] MEETING = sampleFOLK.find("Meeting in einer sozialen Einrichtung");
            allSamples.addAll(sampleFOLK.sample(MEETING, 1, 1000, "MEETING"));

            String[] LLFB = {"FOLK_E_00144"};
            allSamples.addAll(sampleFOLK.sample(LLFB, 1, 1000, "LEHRER_LEHRER-FEEDBACK"));
            
            String[] UMRA = {"FOLK_E_00133"};
            allSamples.addAll(sampleFOLK.sample(UMRA, 1, 1000, "UMRÄUMEN"));

            String[] SIN = {"FOLK_E_00161"};
            allSamples.addAll(sampleFOLK.sample(SIN, 2, 1000, "SIN"));

            String[] REST_ALLTAG = {"FOLK_E_00066"};
            allSamples.addAll(sampleFOLK.sample(REST_ALLTAG, 2, 500, "REST_ALLTAG"));

            String[] SPIEL_KINDER = {"FOLK_E_00010", "FOLK_E_00011", "FOLK_E_00012"};
            allSamples.addAll(sampleFOLK.sample(SPIEL_KINDER, 1, 500, "SPIEL_KINDER"));

            String[] VORLESEN_KINDER = sampleFOLK.find("Vorlesen für Kinder");
            allSamples.addAll(sampleFOLK.sample(VORLESEN_KINDER, 1, 500, "VORLESEN_KINDER"));

            String[] POLIZEI = sampleFOLK.find("Gespräch im Polizeirevier");
            allSamples.addAll(sampleFOLK.sample(POLIZEI, 1, 200, "POLIZEI"));
            
            Document resultDocument = new Document(new Element("samples"));
            resultDocument.getRootElement().addContent(allSamples);
            FileIO.writeDocumentToLocalFile(new File("Z:\\TAGGING\\Sample.xml"), resultDocument);
        } catch (JDOMException ex) {
            Logger.getLogger(SampleFOLK.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SampleFOLK.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<Element> sample(String[] ids, int samplesPerEvent, int wordsPerSample, String name) throws JDOMException, IOException {
        ArrayList<Element> result = new ArrayList<Element>();
        for (final String id : ids){
            File[] transcriptFiles = new File(SOURCE_DIR).listFiles(new FilenameFilter(){
                @Override
                public boolean accept(File dir, String name) {
                    return name.toLowerCase().endsWith(".fln") && name.startsWith(id);
                }            
            });
            int sampleNo = 1;
            // pick a start transcript randomly
            int transcriptNo = (int) (Math.random()*transcriptFiles.length);
            while (sampleNo<=samplesPerEvent){
                File transcriptFile = transcriptFiles[transcriptNo];
                System.out.println("Processing " + transcriptFile.getName());
                Document transcriptDoc = FileIO.readDocumentFromLocalFile(transcriptFile);
                List contributions = XPath.selectNodes(transcriptDoc, "//contribution");
                List allWords = XPath.selectNodes(transcriptDoc, "//w");
                // transcript is smaller than sample size?
                boolean takeAnyway = allWords.size()<wordsPerSample;
                
                System.out.println("Total of " + contributions.size() + " contributions.");
                // pick a start contribution randomly
                int contributionNo = (int) (Math.random()*contributions.size());
                if (takeAnyway){
                    contributionNo=0;
                }
                System.out.println("Starting with contribution #" + contributionNo);
                int rememberStart = contributionNo;
                int wordCount=0;
                while (contributionNo<contributions.size() && wordCount<wordsPerSample){
                    Element contribution = (Element)(contributions.get(contributionNo));
                    List words = XPath.selectNodes(contribution, "descendant::w");
                    wordCount+=words.size();
                    contributionNo++;
                }
                if (wordCount>=wordsPerSample || takeAnyway){
                    for (int i=0; i<rememberStart; i++){
                        Element contribution = (Element)(contributions.get(i));
                        contribution.detach();
                    }
                    for (int i=contributionNo; i<contributions.size(); i++){
                        Element contribution = (Element)(contributions.get(i));
                        contribution.detach();                        
                    }
                    List contributions2 = XPath.selectNodes(transcriptDoc, "//contribution");
                    System.out.println("Remaining " + contributions2.size() + " contributions.");
                    String sampleName = transcriptFile.getName().substring(0, transcriptFile.getName().indexOf(".")) +
                                        "_S_" + Integer.toString(sampleNo) + ".fln";
                    File sampleFile = new File(new File(TARGET_DIR), sampleName);
                    System.out.println("Writing sample to " + sampleFile.getAbsolutePath());
                    FileIO.writeDocumentToLocalFile(sampleFile, transcriptDoc);
                    List words = XPath.selectNodes(transcriptDoc, "//w");
                    Element sampleElement = new Element("sample");
                    sampleElement.setAttribute("file", sampleFile.getName());
                    sampleElement.setAttribute("tokens", Integer.toString(words.size()));
                    sampleElement.setAttribute("name", name);
                    result.add(sampleElement);
                    sampleNo++;
                } else {
                    System.out.println("SAMPLE failed!!!");
                }
                transcriptNo++;
                transcriptNo = transcriptNo % transcriptFiles.length;
            }                
        }
        return result;
    }

    private String[] find(String beschreibung) throws JDOMException, IOException {
        Document metaDoc = FileIO.readDocumentFromLocalFile(new File("Z:\\TAGGING\\FOLK_META.xml"));
        String xp = "//event[Beschreibung[1]='" + beschreibung + "']";
        List l =  XPath.selectNodes(metaDoc, xp);
        String[] result = new String[l.size()];
        for (int i=0; i<l.size(); i++){
            Element e = (Element) l.get(i);
            result[i] = e.getAttributeValue("event-id");
        }
        return result;
    }
}
