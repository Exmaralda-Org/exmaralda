/*
 * TextConverter.java
 *
 * Created on 8. Juni 2007, 09:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.GATParser;
import org.exmaralda.folker.data.Speaker;
import org.exmaralda.folker.data.Timeline;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
/**
 *
 * @author thomas
 */
public class LinguaTecCTMConverter {
    
    ArrayList<String> lines = new ArrayList<String>();
    public boolean appendSpaces = true;
    
    /** Creates a new instance of TextConverter */
    public LinguaTecCTMConverter() {
    }
    
    public void readText(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String nextLine = new String();
        System.out.println("Started reading document");
        while ((nextLine = br.readLine()) != null){
            lines.add(nextLine);
        }
        br.close();
    }

    public void readText(File file, String encoding) throws 
        FileNotFoundException, IOException, UnsupportedEncodingException{
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, encoding);
        BufferedReader br = new BufferedReader(isr);
        String nextLine = new String();
        System.out.println("Started reading document " + file.getAbsolutePath());
        int lineCount=0;
        while ((nextLine = br.readLine()) != null){
            // remove komisches Zeichen am Anfang von Unicode-kodierten Dateien
            if (lineCount==0 && encoding.startsWith("UTF") && nextLine.charAt(0)=='\uFEFF'){
                nextLine = nextLine.substring(1);
            }
            lines.add(nextLine);
            lineCount++;
        }
        br.close();
        System.out.println("Document read.");                
    }
    
    
    public EventListTranscription importText(){
        double maxTime = getMaximumTime();
        EventListTranscription transcription = new EventListTranscription(0.0, maxTime * 1000.0);
        Timeline timeline = transcription.getTimeline();
        transcription.addSpeaker("X");
        Speaker dummySpeaker = transcription.getSpeakerWithID("X");
        transcription.setMediaPath("Z:\\Spracherkennungstest\\FOLK\\FOLK_SS21_01_A07_b_120.wav");
        double currentStart = 0;
        String currentText = "";
        for (String line : lines){
            System.out.println("Processing " + line);
            String[] data = line.split(" ");
            String time = data[2];
            String duration = data[3];
            String word = data[4];
            //de_15639a18d980412d8a4b88f65fcf1bf9_1365584002_FolkSS2101A03ageschnitten 1 894.590 0.760 [SILENCE_0.760]
            if (word.startsWith("[SILENCE")){
                transcription.addEvent(currentStart * 1000.0, Double.parseDouble(time) * 1000.0, currentText, dummySpeaker);
                                
                DecimalFormat df2 = new DecimalFormat("#0.00");                
                String durationText = word.substring(word.indexOf("_") +1, word.length()-1);
                double durationDouble = Double.parseDouble(durationText);
                String pauseText = "(" + df2.format(durationDouble).replaceAll("\\,", ".") + ")";
                transcription.addEvent(Double.parseDouble(time) * 1000.0, (Double.parseDouble(time) + Double.parseDouble(duration)) * 1000.0, pauseText, null);
                
                currentText = "";
                currentStart = (Double.parseDouble(time) + Double.parseDouble(duration));
            } else {
                currentText+=word.toLowerCase() + " ";
            }
        }
        
        return transcription;
    }
    
    double getMaximumTime(){
        // de_15639a18d980412d8a4b88f65fcf1bf9_1365584002_FolkSS2101A03ageschnitten 1 899.240 0.710 Plochingen
        String lastLine = lines.get(lines.size()-1);
        String[] data = lastLine.split(" ");
        String time = data[2];
        String duration = data[3];
        return Double.parseDouble(time) + Double.parseDouble(duration);
    }
    
    public static void main(String[] args){
        try {
            File f = new File("Z:\\Spracherkennungstest\\FOLK\\FOLK_SS21_01_A07_b_120.ctm");
            LinguaTecCTMConverter tc = new LinguaTecCTMConverter();
            tc.readText(f, "UTF-8");
            EventListTranscription elt = tc.importText();
            File out = new File(f.getParentFile(), "FOLK_SS21_01_A07_b_120.flk");
            EventListTranscriptionXMLReaderWriter.writeXML(elt, out, new GATParser(), 0);
        } catch (IOException ex) {
            Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JDOMException ex) {
                Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (TransformerException ex) {
                Logger.getLogger(LinguaTecCTMConverter.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    
    
}
