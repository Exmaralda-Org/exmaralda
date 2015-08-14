/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.data;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class TranscriptionHead {

    Element headElement;
    
    public TranscriptionHead(File f) throws IOException, JDOMException {
        Document transcription = IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
        headElement = (Element) XPath.newInstance("//head").selectSingleNode(transcription);
        if (headElement.getChild("transcription-log")==null){
            generateLogElement();
        }
        if (headElement.getChild("mask")==null){
            generateMaskElement();
        }
        if (headElement.getChild("mask").getChild("key")==null){
            generateKeyElement();
        }
        headElement.detach();
    }

    public TranscriptionHead() {
        headElement = new Element("head");
        generateLogElement();
        generateMaskElement();
        generateKeyElement();
    }

    public Element getHeadElement() {
        return headElement;
    }

    public void appendLog(Element newLog) {
        getLogElement().addContent(newLog);
    }
    
    Element getLogElement(){
        return headElement.getChild("transcription-log");
    }

    Element getMaskElement(){
        return headElement.getChild("mask");
    }
    
    public Element getKeyElement(){
        return headElement.getChild("mask").getChild("key");
    }

    private void generateLogElement() {
        Element logElement = new Element("transcription-log");
        headElement.addContent(logElement);
        Element firstLogEntry = new Element("log-entry");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String formattedDate = dateFormat.format(date);        
        firstLogEntry.setAttribute("start", formattedDate);
        firstLogEntry.setAttribute("end", formattedDate);
        firstLogEntry.setAttribute("who", "system");
        firstLogEntry.setText("transcription-log created");
        logElement.addContent(firstLogEntry);
    }

    private void generateMaskElement() {
        Element maskElement = new Element("mask");
        headElement.addContent(maskElement);        
    }
    
    private void generateKeyElement(){
        getMaskElement().addContent(new Element("key"));
        
    }

    public void addMaskSegment(double selectionStart, double selectionEnd, String maskText) {
        Element maskSegment = new Element("mask-segment");
        maskSegment.setAttribute("start", Double.toString(selectionStart/1000.0));
        maskSegment.setAttribute("end", Double.toString(selectionEnd/1000.0));
        maskSegment.setText(maskText);
        getMaskElement().addContent(maskSegment);
    }
    

    // this is dramatically buggy!!!
    // not any more, I think
    public int extractMaskSegments(EventListTranscription elt) {
        Vector<Event> allEvents = elt.getEventlist().getEvents();
        int count=0;
        int moveCount = 0;
        ArrayList<Integer> toBeRemoved = new ArrayList<Integer>(); 
        for (Event e : allEvents){
            String text = e.getText();
            if (text!=null && text.startsWith("[[[") && text.endsWith("]]]")){
                String cleanText = text.substring(3, text.length()-3);
                addMaskSegment(e.getStartpoint().getTime(), e.getEndpoint().getTime(), cleanText);
                toBeRemoved.add(count);
                //System.out.println("To be removed: " + text);
                moveCount++;
            }
            count++;                
        }
        int[] tbrs = new int[toBeRemoved.size()];
        int i = 0;
        for (int tbr : toBeRemoved){
            tbrs[i] = tbr;
            i++;
        }
        elt.removeEvents(tbrs);
        return moveCount;
    }

    public void deleteMask() {
        getMaskElement().removeContent();
        generateKeyElement();
    }
    
}
