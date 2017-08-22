/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Eventlist;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author Schmidt
 */
public class SubtitleConverter {

    EventListTranscription transcription;
    
    
    //SubRip (.srt) Struktur (UTF 8):
    //1
    //00:03:10,500 --> 00:03:13,000
    //Wenn ich mir was
    //
    //2
    //00:00:15,000 --> 00:00:18,000
    //wünschen dürfte!    
    
    public SubtitleConverter(EventListTranscription elt){
        transcription = elt;
    }
    
    public SubtitleConverter(BasicTranscription bt){
        transcription = org.exmaralda.folker.io.EventListTranscriptionConverter.importExmaraldaBasicTranscription(bt);
    }

    //SubRip (.srt) Struktur (UTF 8):
    //1
    //00:03:10,500 --> 00:03:13,000
    //Wenn ich mir was
    //
    //2
    //00:00:15,000 --> 00:00:18,000
    //wünschen dürfte!    
    public String getSRT(){
        return getSRT(false,false);
    }

    public String getSRT(boolean frames, boolean plainText){
        StringBuilder sb = new StringBuilder();
        Eventlist eventlist = transcription.getEventlist();
        int index=1;
        String lastStart = "";
        String lastEnd = "";
        for (Event event : eventlist.getEvents()){
            double startTime = event.getStartpoint().getTime();
            double endTime = event.getEndpoint().getTime();
            String start = "00:" + TimeStringFormatter.formatMiliseconds(startTime, 3).replace('.', ',');
            String end = "00:" + TimeStringFormatter.formatMiliseconds(endTime, 3).replace('.', ',');
            if (frames){
                String startMili = start.substring(start.indexOf(",")+1);
                String endMili = end.substring(end.indexOf(",")+1);
                int startFramesInt = (int) Math.floor(Double.parseDouble("0." + startMili) * 25.0);
                String startFrames = Integer.toString(startFramesInt);
                if (startFramesInt<10) startFrames = "0" + startFrames;
                int endFramesInt = (int) Math.floor(Double.parseDouble("0." + endMili) * 25.0);                
                String endFrames = Integer.toString(endFramesInt);
                if (endFramesInt<10) endFrames = "0" + endFrames;
                start = start.substring(0,start.indexOf(",")) + ":" + startFrames;
                end = end.substring(0,end.indexOf(",")) + ":" + endFrames;
                if (start.equals(lastStart) && end.equals(lastEnd)){
                    start = "";
                    end = "";
                } else {
                    lastStart = start;
                    lastEnd = end;
                }
            }
            if (!plainText){
                sb.append(Integer.toString(index)).append(System.getProperty("line.separator"));
                sb.append(start).append(" --> ").append(end).append(System.getProperty("line.separator"));
                sb.append(event.getText()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
            } else {
                sb.append(start).append(" ").append(end).append(" ").append(event.getText()).append(System.getProperty("line.separator"));
            }
            index++;
        }
        return sb.toString();
    }
    
    public String getVTT(){
        StringBuilder sb = new StringBuilder();
        sb.append("WEBVTT").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
        Eventlist eventlist = transcription.getEventlist();
        int index=1;
        String lastStart = "";
        String lastEnd = "";
        for (Event event : eventlist.getEvents()){
            double startTime = event.getStartpoint().getTime();
            double endTime = event.getEndpoint().getTime();
            String start = "00:" + TimeStringFormatter.formatMiliseconds(startTime, 3);
            String end = "00:" + TimeStringFormatter.formatMiliseconds(endTime, 3);
            
            
            // make sure there are three digits after the decimal point
            int indexDecimalPointStart = start.lastIndexOf(".");
            if (start.length()-indexDecimalPointStart==2) {
                start+="00";
            } else if (start.length()-indexDecimalPointStart==3) {
                start+="0";                
            }
            
            int indexDecimalPointEnd = end.lastIndexOf(".");
            if (end.length()-indexDecimalPointEnd==2) {
                end+="00";
            } else if (end.length()-indexDecimalPointEnd==3) {
                end+="0";                
            }
            
            
            sb.append(Integer.toString(index)).append(System.getProperty("line.separator"));
            sb.append(start).append(" --> ").append(end).append(System.getProperty("line.separator"));
            sb.append(event.getText()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
            index++;
        }
        return sb.toString();
    }
    
    
    static String PSEUDO_VTT_XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/VTTPseudoXML2FLK.xsl";
    // 22-08-2017: quick hack for issue #119
    // meant to work for VTT output of YouTube ASR
    public static BasicTranscription readVTT(File file) throws IOException, JDOMException, SAXException, ParserConfigurationException, TransformerException, TransformerConfigurationException, JexmaraldaException{
        // 00:00:00.000 --> 00:00:05.370 align:start position:19%
        // in<c.colorCCCCCC><00:00:00.750><c> böblingen</c></c><c.colorE5E5E5><00:00:01.050><c> treten</c><00:00:01.770><c> zwei</c><00:00:02.100><c> listen</c><00:00:02.639><c> an</c><00:00:02.820><c> und</c></c>
        String timeRegEx = "\\d{2}\\:\\d{2}\\:\\d{2}\\.\\d{3}";
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String nextLine = "";
        System.out.println("Started reading document");
        boolean cueRead = false;
        Element currentElement = null;
        ArrayList<Element> allElements = new ArrayList<Element>();
        while ((nextLine = br.readLine()) != null){
            if (cueRead){
                // second line
                String modifiedLine = nextLine;
                modifiedLine = modifiedLine.replaceAll("(" + timeRegEx + ")", "time time=\"$1\"/");
                modifiedLine = modifiedLine.replaceAll("\\<c\\.color([A-Z0-9]{6})\\>", "<c color=\"$1\">");
                modifiedLine = "<content>" + modifiedLine + "</content>";
                System.out.println(modifiedLine);
                Element contentElement = IOUtilities.readElementFromString(modifiedLine);
                currentElement.addContent(contentElement);
                allElements.add(currentElement);
                cueRead = false;
            }
            if (nextLine.matches("^" + timeRegEx + " \\-\\-\\> " + timeRegEx + " .+")){
                // first line
                String time1String = nextLine.substring(0,12);
                String time2String = nextLine.substring(17,29);
                currentElement = new Element("cue");
                currentElement.setAttribute("start", time1String);
                currentElement.setAttribute("end", time2String);
                cueRead = true;
            }
        }
        
        Document pseudoDoc = new Document(new Element("doc"));        
        pseudoDoc.getRootElement().addContent(allElements);
        StylesheetFactory ssf = new StylesheetFactory(true);
        String flkString = ssf.applyInternalStylesheetToString(PSEUDO_VTT_XSL, IOUtilities.documentToString(pseudoDoc));
        BasicTranscription result = EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(IOUtilities.readDocumentFromString(flkString));
        return result;
    }

    public void writeVTT(File file) throws IOException{
        System.out.println("started writing document " + file.getAbsolutePath() + "...");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getVTT().getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");        
    }

    public void writeSRT(File file) throws IOException{
        writeSRT(file, false, false);
    }
    public void writeSRT(File file, boolean frames, boolean plainText) throws IOException{
        System.out.println("started writing document " + file.getAbsolutePath() + "...");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getSRT(frames, plainText).getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");        
    }
    
    public static void main(String[] args){
        File f = new File("F:\\Dropbox\\IDS\\AGD\\BETV\\BETV_E_00003_SE_01_T_01.vtt");
        try {
            BasicTranscription bt = SubtitleConverter.readVTT(f);
            bt.writeXMLToFile("F:\\Dropbox\\IDS\\AGD\\BETV\\BETV_E_00003_SE_01_T_01.exb", "none");
        } catch (IOException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
