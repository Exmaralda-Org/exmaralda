/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.alignment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.exmaralda.orthonormal.io.XMLReaderWriter;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.convert.PraatConverter;
import org.exmaralda.partitureditor.sound.AudioProcessor;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class Aligner {

    File transcriptFile;
    File wavFile;
    AudioProcessor audioProcessor;
    
    public Aligner(File transcriptFile, File wavFile) {
        this.transcriptFile = transcriptFile;
        this.wavFile = wavFile;
        audioProcessor = new AudioProcessor();
    }

    public void setCURL_PATH(String CURL_PATH) {
        this.CURL_PATH = CURL_PATH;
    }

    public void setMAUS_WEB_SERVICE(String MAUS_WEB_SERVICE) {
        this.MAUS_WEB_SERVICE = MAUS_WEB_SERVICE;
    }

    public void setPRAATCON_PATH(String PRAATCON_PATH) {
        this.PRAATCON_PATH = PRAATCON_PATH;
    }

    public void setPRAAT_SCRIPT_PATH(String PRAAT_SCRIPT_PATH) {
        this.PRAAT_SCRIPT_PATH = PRAAT_SCRIPT_PATH;
    }
    
    // **********************************************************************

    public ArrayList<File> cut(File wavDirectory, File txtDirectory) throws JDOMException, IOException{
        System.out.println("Chopping WAV and generating TXT...");
        ArrayList<File> result = new ArrayList<File>();
        NormalizedFolkerTranscription nft = XMLReaderWriter.readFolkerTranscription(transcriptFile);
        for (int pos=0; pos<nft.getNumberOfContributions(); pos++){
            System.out.println("Processing contribution #" + Integer.toString(pos+1) + " of " + nft.getNumberOfContributions());
            Element c = nft.getContributionAt(pos);
            String filename = wavFile.getName().substring(0, wavFile.getName().indexOf(".")) + "_" + Integer.toString(pos);

            if (XPath.newInstance("descendant::w").selectNodes(c).size()<2){
                // don't treat contributions without words or with just one word
                System.out.println("No cutting performed.");
                continue;
            }
            
            String text = getContributionText(c);
            File textOutputFile = new File(txtDirectory, filename + ".TXT");            
            FileOutputStream fos = new FileOutputStream(textOutputFile);
            fos.write(text.getBytes("UTF-8"));
            fos.close();
            System.out.println("TXT file written to " + textOutputFile.getAbsolutePath());

            String startID = c.getAttributeValue("start-reference");
            String endID = c.getAttributeValue("end-reference");
            double start = nft.getTimeForId(startID).getTime() / 1000.0;
            double end = nft.getTimeForId(endID).getTime() / 1000.0;
            File outputFile = new File(wavDirectory, filename + ".WAV"); 
            System.out.println("Cutting from " + start + " to " + end);
            audioProcessor.writePart(start, end, wavFile.getAbsolutePath(), outputFile.getAbsolutePath());
            result.add(outputFile);
            System.out.println("WAV file written to " + outputFile.getAbsolutePath());
        }
        System.out.println("WAV chopped and TXT generated.");
        return result;
    }

    private String getContributionText(Element c) throws JDOMException {
        String result = "";
        List l = XPath.newInstance("descendant::w").selectNodes(c);
        for (Object o : l){
            Element w = (Element)o;
            result+=WordUtilities.getWordText(w) + " ";
        }
        
        return result;
    }
    
    // **********************************************************************

    public String PRAATCON_PATH = "C:\\Program Files\\Praat\\praatcon.exe";
    public String PRAAT_SCRIPT_PATH = "C:\\Users\\Schmidt\\Desktop\\Alignment\\script\\praatMono2Stereo.praatScript";
        
    public void convertStereoToMono(File wavDirectory) throws IOException{
        System.out.println("Converting stereo files to mono in " + wavDirectory.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder(PRAATCON_PATH, PRAAT_SCRIPT_PATH, wavDirectory.getAbsolutePath() + File.separator);
        System.out.println(pb.command());
        Process p = pb.start();
        try {
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }        
        System.out.println("All files converted to mono.");
    }
    
    // **********************************************************************
    
    /*C:\Program Files\curl\curl -v -X POST -H 'content-type: multipart/form-data'  
        -F TEXT=@C:\Users\Schmidt\Desktop\Alignment\0.txt 
        -F SIGNAL=@C:\Users\Schmidt\Desktop\Alignment\DS--_E_00001_SE_01_A_01_DF_01_1.wav 
        http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUSBasicGerman 
        > C:\Users\Schmidt\Desktop\Alignment\DS--_E_00001_SE_01_A_01_DF_01_0_Mono2.TextGrid        */

    public String CURL_PATH = "C:\\Program Files\\curl\\curl";
    public String MAUS_WEB_SERVICE = "http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUSBasicGerman";

    public void doAlignment(File wavDirectory, File txtDirectory, File textGridDirectory) throws IOException{
        File[] wavFiles = wavDirectory.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".WAV");
            }
            
        });
        int count=0;
        for (File f : wavFiles){
            File txtFile = new File(txtDirectory, f.getName().replaceAll("\\.WAV", ".TXT"));
            File praatFile = new File(textGridDirectory, f.getName().replaceAll("\\.WAV", ".textGrid"));
            System.out.println("Aligning " + txtFile.getName() + " with " + f.getName() + "...");
            ProcessBuilder pb2 = new ProcessBuilder(CURL_PATH, "-v", "-X", "POST", "-H", "'content-type: multipart/form-data'",
                    "-F", "TEXT=@" + txtFile.getAbsolutePath(),
                    "-F", "SIGNAL=@" + f.getAbsolutePath(),
                    MAUS_WEB_SERVICE);            
            // requires Java 7
            pb2.redirectOutput(praatFile);
            File errorFile = new File("C:\\Users\\Schmidt\\Desktop\\Alignment\\MAUS_ERROR.txt");
            // requires Java 7
            pb2.redirectError(errorFile);
            System.out.println(pb2.command());
            Process p2 = pb2.start();
            try {
                p2.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            } finally {                
            }
            count++;
            System.out.println("Alignment result written to " + praatFile.getName() +  " [" + Integer.toString(count) + "/" + Integer.toString(wavFiles.length)+ "]");
        }
    }

    public Document mixResult(File praatTargetDirectory) throws JDOMException, IOException, JexmaraldaException {
        NormalizedFolkerTranscription nft = XMLReaderWriter.readFolkerTranscription(transcriptFile);
        PraatConverter praatConverter = new PraatConverter();
        HashSet<Double> TimesToBeAdded = new HashSet<Double>();
        for (int pos=0; pos<nft.getNumberOfContributions(); pos++){
            System.out.println("Processing contribution #" + Integer.toString(pos+1) + " of " + nft.getNumberOfContributions());
            Element c = nft.getContributionAt(pos);
            String filename = wavFile.getName().substring(0, wavFile.getName().indexOf(".")) + "_" + Integer.toString(pos);

            List l = XPath.newInstance("descendant::w").selectNodes(c);
            if (l.size()<2){
                // don't treat contributions without words or with just one word
                continue;
            }
            
            double startTimeOfThisContribution = Double.parseDouble(c.getAttributeValue("time"));
            
            File textGridFile = new File(praatTargetDirectory, filename + ".textGrid");            
            BasicTranscription basicTranscription =
                    praatConverter.readPraatFromFile(textGridFile.getAbsolutePath());
            // the orthographic words are in the first tier
            Tier tier = basicTranscription.getBody().getTierAt(0);
            int index=0;
            for (Object o : l){
                Element word=(Element)o;
                if (index>=tier.getNumberOfEvents()) break;
                Event correspondingEvent = tier.getEventAt(index);
                String startID = correspondingEvent.getStart();
                double startTime = startTimeOfThisContribution + 
                        basicTranscription.getBody().getCommonTimeline().getTimelineItemWithID(startID).getTime();
                System.out.println(WordUtilities.getWordText(word) + " / " + correspondingEvent.getDescription());
                int wordIndex = word.getParentElement().indexOf(word);
                if (wordIndex>1){
                    // don't insert a timepoint before the first word
                    //<time timepoint-reference="TLI_100" time="305.576"/>
                    Element timepointReference = new Element("time");
                    timepointReference.setAttribute("timepoint-reference", "T_" + Double.toString(startTime).replaceAll("\\.", "_"));
                    timepointReference.setAttribute("time", Double.toString(startTime));
                    TimesToBeAdded.add(startTime);
                    word.getParentElement().addContent(wordIndex, timepointReference);
                }
                index++;
            }
        }
        ArrayList<Element> newTLIs = new ArrayList<Element>();
        for (Double time : TimesToBeAdded){
            //<timepoint timepoint-id="TLI_1" absolute-time="1.4214307805660364"/>
            Element timepoint = new Element("timepoint");
            timepoint.setAttribute("timepoint-id", "T_" + time.toString().replaceAll("\\.", "_"));
            timepoint.setAttribute("absolute-time", time.toString());                
            newTLIs.add(timepoint);
        }
        
        Element timeline = (Element) XPath.selectSingleNode(nft.getDocument(), "//timeline");
        List content = timeline.removeContent();
        for (Object o : content){
            newTLIs.add((Element)o);
        }
        
        Collections.sort(newTLIs, new Comparator<Element>(){
            @Override
            public int compare(Element o1, Element o2) {
                Double time1 = Double.parseDouble(o1.getAttributeValue("absolute-time"));
                Double time2 = Double.parseDouble(o2.getAttributeValue("absolute-time"));
                return time1.compareTo(time2);
            }            
        });
        
        timeline.addContent(newTLIs);
        
        return nft.getDocument();             
    }
    
}
