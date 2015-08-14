/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.coma.helpers.GUID;
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.AbstractFilter;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class Folker2DGD {

    
    Document metaDataDocument;
    File inDirectory;
    File outDirectory;
    Hashtable<String, Double> ids2Times;
    
    
    AbstractFilter textFilter = new AbstractFilter(){
        @Override
        public boolean matches(Object o) {
            return ((o instanceof org.jdom.Text) && ((org.jdom.Text)o).getTextTrim().length()>0);
        }
    };
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Folker2DGD f2d = new Folker2DGD(args);
        } catch (JDOMException ex) {
            Logger.getLogger(Folker2DGD.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Folker2DGD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Folker2DGD(String[] args) throws JDOMException, IOException {
        inDirectory = new File(args[0]);
        outDirectory = new File(args[1]);
        outDirectory.mkdir();
        for (File f : outDirectory.listFiles()){
            f.delete();
        }        
        
        metaDataDocument = FileIO.readDocumentFromLocalFile(args[2]);
        File[] files = inDirectory.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return name.toUpperCase().endsWith(".FLN");
            }            
        });
        for (File f : files){
            System.out.println("==========================");
            System.out.println(f.getName());
            System.out.println("==========================");
            Document transcriptionDocument = FileIO.readDocumentFromLocalFile(f.getAbsolutePath());
            removeTagProbabilities(transcriptionDocument);
            addDGDIDs(transcriptionDocument, f.getName());
            addElementIDs(transcriptionDocument);
            supplementNormalizedForms(transcriptionDocument);
            roundTimes(transcriptionDocument);
            insertStartEnd(transcriptionDocument);
            insertTimes(transcriptionDocument);
            if (f.getName().startsWith("FOLK")){
                markOverlaps(transcriptionDocument);
            }
            File outFile = new File(outDirectory, f.getName());
            FileIO.writeDocumentToLocalFile(outFile.getAbsolutePath(), transcriptionDocument);
        }
    }
    
    public void markOverlaps(Document transcriptionDocument) throws JDOMException{        
        Hashtable<String,Integer> overlapIntervals = new Hashtable<String,Integer>();
        List l = XPath.newInstance("//contribution").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element c = (Element)o;
            Iterator i = c.getDescendants(new ElementFilter("time"));
            Vector<Element> v = new Vector<Element>();
            while (i.hasNext()){
                v.add((Element)(i.next()));
            }
            for (int j=0; j<v.size()-1; j++){
                Element thisT = v.get(j);
                Element nextT = v.get(j+1);
                String key = thisT.getAttributeValue("timepoint-reference") + "***" + nextT.getAttributeValue("timepoint-reference");
                if (!(overlapIntervals.containsKey(key))){
                    overlapIntervals.put(key, new Integer(0));
                }
                overlapIntervals.put(key, new Integer(overlapIntervals.get(key).intValue()+1));
            }
        }
        for (Object o : l){
            Element c = (Element)o;
            Iterator i = c.getDescendants(new ElementFilter("time"));
            Vector<Element> v = new Vector<Element>();
            while (i.hasNext()){
                v.add((Element)(i.next()));
            }
            for (int j=0; j<v.size()-1; j++){
                Element thisT = v.get(j);
                Element nextT = v.get(j+1);
                String key = thisT.getAttributeValue("timepoint-reference") + "***" + nextT.getAttributeValue("timepoint-reference");
                int intervalCount = overlapIntervals.get(key).intValue();
                if (intervalCount>1){
                    if (thisT.getAttribute("ol")==null){
                        thisT.setAttribute("ol", "start");
                    } else {
                        thisT.setAttribute("ol", thisT.getAttributeValue("ol") + " start");
                    }
                    if (nextT.getAttribute("ol")==null){
                        nextT.setAttribute("ol", "end");
                    } else {
                        nextT.setAttribute("ol", nextT.getAttributeValue("ol") + " end");
                    }
                }
            }
        }
        
        
        for (Object o : l){
            Element c = (Element)o;
            Iterator i = c.getDescendants();
            boolean insideOverlap = false;
            while (i.hasNext()){
                Object o2 = i.next();
                if (!(o2 instanceof Element)) continue;
                Element e = (Element)(o2);
                if (e.getName().equals("time") && "end".equals(e.getAttributeValue("ol"))){
                    insideOverlap = false;
                }
                if (insideOverlap){
                    e.setAttribute("ol", "in");
                }
                if (e.getName().equals("w") && e.getChild("time")!=null){
                    boolean overlapStartsInside = false;
                    List l2 = e.getChildren("time");
                    for (Object o3 : l2){
                        Element e3 = (Element)o3;
                        overlapStartsInside = overlapStartsInside || "start".equals(e3.getAttributeValue("ol"));
                    }
                    if (overlapStartsInside){
                        e.setAttribute("ol", "in");
                    }                    
                }
                if (e.getName().equals("time") && "start".equals(e.getAttributeValue("ol"))){
                    insideOverlap = true;
                }
            }            
        }
        
        
    }

    public void insertStartEnd(Document transcriptionDocument) throws JDOMException{        
        List l = XPath.newInstance("//timepoint").selectNodes(transcriptionDocument);
        ids2Times = new Hashtable<String,Double>();
        for (Object o : l){
            Element e = (Element)o;
            double time = Double.parseDouble(e.getAttributeValue("absolute-time"));
            String id = e.getAttributeValue("timepoint-id");
            ids2Times.put(id, time);
        }        
        
        l = XPath.newInstance("//contribution").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element c = (Element)o;
            if (c.getContentSize()==0){
                System.out.println("Empty content: " + IOUtilities.elementToString(c));
                c.detach();
                continue;
            }
            String startID = c.getAttributeValue("start-reference");
            String endID = c.getAttributeValue("end-reference");
            if (!(c.getContent(0) instanceof Element && ((Element)(c.getContent(0))).getName().equals("time"))){
                Element t1 = new Element("time");
                t1.setAttribute("timepoint-reference", startID);
                c.addContent(0, t1);
            }
            if (!(c.getContent(c.getContentSize()-1) instanceof Element && ((Element)(c.getContent(c.getContentSize()-1))).getName().equals("time"))){
                Element t2 = new Element("time");
                t2.setAttribute("timepoint-reference", endID);
                c.addContent(t2);
            }
        }        
    }

    public void removeTagProbabilities(Document transcriptionDocument) throws JDOMException{        
        List l = XPath.newInstance("//*[@p-pos]").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            e.removeAttribute("p-pos");
        }
    }

    public void insertTimes(Document transcriptionDocument) throws JDOMException{        
        List l = XPath.newInstance("//*[@start-reference]").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            e.setAttribute("time", Double.toString(ids2Times.get(e.getAttributeValue("start-reference"))));            
        }
        l = XPath.newInstance("//*[@timepoint-reference]").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            e.setAttribute("time", Double.toString(ids2Times.get(e.getAttributeValue("timepoint-reference"))));            
        }

    }

    public void roundTimes(Document transcriptionDocument) throws JDOMException{        
        List l = XPath.newInstance("//timepoint").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            double time = Double.parseDouble(e.getAttributeValue("absolute-time"));
            double roundedTime = Math.round(time * 1000.0) / 1000.0;
            e.setAttribute("absolute-time", Double.toString(roundedTime));
        }        
    }

    public void supplementNormalizedForms(Document transcriptionDocument) throws JDOMException{
        List l = XPath.newInstance("//w[not(@n)]").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            String wordText = this.getWordText(e);
            e.setAttribute("n", wordText);
        }        
    }

    public void addElementIDs(Document transcriptionDocument) throws JDOMException{
        Hashtable<Character,Integer> counts = new Hashtable<Character,Integer>();
        List l = XPath.newInstance("//*[not(@id) and (self::contribution or ancestor::contribution) and not(self::time)]").selectNodes(transcriptionDocument);
        for (Object o : l){
            Element e = (Element)o;
            Character first = new Character(e.getName().charAt(0));
            if (!(counts.containsKey(first))){
                counts.put(first, new Integer(0));
            }
            int count = counts.get(first).intValue() + 1;
            counts.put(first, new Integer(count));
            String idString = first.toString() + Integer.toString(count);
            e.setAttribute("id", idString);
        }
    }
    
    public void addDGDIDs(Document transcriptionDocument, String transcriptFileName) throws JDOMException{
        if (transcriptionDocument.getRootElement().getAttribute("id")==null){
            transcriptionDocument.getRootElement().setAttribute("id", new GUID().makeID());
        }
        //FOLK_E_00016_SE_01_T_01_DF_01.fln
        String transcriptKennung = transcriptFileName.substring(0, transcriptFileName.indexOf("."));
        transcriptionDocument.getRootElement().setAttribute("dgd-id", transcriptKennung);
        transcriptionDocument.getRootElement().setAttribute("corpus-dgd-id", transcriptKennung.substring(0,4));
        
        String speechEventKennung = transcriptKennung.substring(0,18);
        
        // <recording path="../../media/audio/FOLK/FOLK_E_00001_SE_01_A_01_DF_01.WAV"/>
        Element recordingElement = ((Element)XPath.newInstance("//recording").selectSingleNode(transcriptionDocument));
        String recordingPath = recordingElement.getAttributeValue("path");
        String recording = recordingPath.substring(recordingPath.lastIndexOf("/")+1);
        String recordingKennung = recording.substring(0, recording.lastIndexOf(".WAV"));
        recordingElement.setAttribute("dgd-id", recordingKennung);
        
        //<speaker speaker-id="PL">
        Hashtable<String, String> ids2Kennung = new Hashtable<String,String>();
        List speakers = XPath.newInstance("//speaker").selectNodes(transcriptionDocument);
        for (Object o : speakers){
            Element speaker = (Element)o;
            String speakerID = speaker.getAttributeValue("speaker-id").trim();
            //<Sprecher Kennung="FOLK_S_00001" SE-Kennung="FOLK_E_00001_SE_01" sigle="ML"/>
            //System.out.println("Looking up " + speechEventKennung + " " + speakerID);
            String xp = "//Sprecher[@SE-Kennung='" + speechEventKennung + "' and @sigle='" + speakerID + "']";
            if (transcriptKennung.startsWith("HL")){
                xp = "//Sprecher[@SE-Kennung='" + speechEventKennung + "' and contains(@sigle,'" + speakerID + "')]";                
            }
            Element metaSprecher = (Element) XPath.newInstance(xp).selectSingleNode(metaDataDocument);
            if (metaSprecher!=null && metaSprecher.getAttributeValue("Kennung")!=null){
                speaker.setAttribute("dgd-id", metaSprecher.getAttributeValue("Kennung"));
            } else {
                speaker.setAttribute("dgd-id", "???");
            }
            ids2Kennung.put(speakerID, speaker.getAttributeValue("dgd-id"));
        }
        List contributions = XPath.newInstance("//contribution[@speaker-reference]").selectNodes(transcriptionDocument);
        for (Object o : contributions){
            Element contribution = (Element)o;
            String speakerID = contribution.getAttributeValue("speaker-reference").trim();
            //System.out.println("Looking up " + speakerID);
            String speakerKennung = ids2Kennung.get(speakerID);
            contribution.setAttribute("speaker-dgd-id", speakerKennung);
        }
        
        
    }
    
    String getWordText(Element wordElement){        
        Iterator i = wordElement.getDescendants(textFilter);
        StringBuffer result = new StringBuffer();
        while (i.hasNext()){
            result.append(((org.jdom.Text)(i.next())).getText());
        }
        return result.toString();
    }


}
