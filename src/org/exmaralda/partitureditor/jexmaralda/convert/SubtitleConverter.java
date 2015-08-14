/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.data.Event;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Eventlist;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
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
        try {
            EventListTranscription t = EventListTranscriptionXMLReaderWriter.readXML(
                    new File("C:\\Users\\Schmidt\\Desktop\\DGD-RELEASE\\transcripts\\FOLK\\FOLK_E_00070_SE_01_T_07_DF_01.fln"));
            SubtitleConverter c = new SubtitleConverter(t);
            //c.writeSRT(new File("C:\\Users\\Schmidt\\Desktop\\out.srt"));
            System.out.println(c.getVTT());
        } catch (JDOMException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JexmaraldaException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SubtitleConverter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
