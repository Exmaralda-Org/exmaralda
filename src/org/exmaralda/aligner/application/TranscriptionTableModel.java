/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.aligner.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.folker.data.Timepoint;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class TranscriptionTableModel extends AbstractTableModel {

    Document transcriptionDoc;
    String[] texts;
    String[] speakers;
    Double[][] times;
    
    // for a AGD transcription (Korpus Dialogstrukturen)
    public TranscriptionTableModel(NormalizedFolkerTranscription nft) {
        try {
            transcriptionDoc = nft.getDocument();
            List l = XPath.newInstance("//contribution").selectNodes(transcriptionDoc);
            texts = new String[l.size()];
            speakers = new String[l.size()];
            times = new Double[l.size()][2];
            int i = 0;
            for (Object o : l){
                Element contribution = (Element)o;
                String text = "";
                List l2 = XPath.newInstance("descendant::w").selectNodes(contribution);
                for (Object o2 : l2){
                    Element word = (Element)o2;
                    text+=word.getText() +  " ";
                }
                texts[i] = text;
                speakers[i] = contribution.getAttributeValue("speaker-reference");
                String startID = contribution.getAttributeValue("start-reference");
                String endID = contribution.getAttributeValue("end-reference");                
                times[i][0] = nft.getTimeForId(startID).getTime() / 1000.0;
                times[i][1] = nft.getTimeForId(endID).getTime() / 1000.0;
                i++;
            }
        } catch (JDOMException ex) {
            Logger.getLogger(TranscriptionTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // for a BBAW transcription (Berliner Wendekorpus)
    public TranscriptionTableModel(Document doc){
        try {
            transcriptionDoc = doc;
            List l = XPath.newInstance("//text/body/div[1]/u/seg").selectNodes(transcriptionDoc);
            System.out.println(l.size() + " <u>-elements");
            texts = new String[l.size()];
            speakers = new String[l.size()];
            times = new Double[l.size()][2];
            int i = 0;
            for (Object o : l){
                /*<u who="FK" xml:id="u_trans_1"> ? <w xml:id="t1">wie</w>
                  <w xml:id="t2">lange</w>
                  <w xml:id="t3">arbeitest</w>
                  <w xml:id="t4">du</w>
                  <w xml:id="t5">hier</w>? </u>*/
                Element contribution = (Element)o;
                String text = "";
                //List l2 = XPath.newInstance("descendant::w").selectNodes(contribution);
                List l2 = XPath.newInstance("child::node()").selectNodes(contribution);
                for (Object o2 : l2){
                    if (o2 instanceof Element){
                        Element e = (Element)o2;                        
                        if ("w".equals(e.getName())){
                            text+=e.getText() +  " ";
                        } else if ("vocal".equals(e.getName())){
                            text+="[" + e.getChildText("desc")+ "] ";                            
                        } else if ("pause".equals(e.getName())){
                            text+="* ";                            
                        }
                    } else {
                        Text t = (Text)o2;
                        text+=t.getTextNormalize();
                    }
                    //Element word = (Element)o2;
                    //text+=word.getText() +  " ";
                }
                texts[i] = text;
                speakers[i] = contribution.getParentElement().getAttributeValue("who");
                String startTime = contribution.getAttributeValue("start-time");
                String endTime = contribution.getAttributeValue("end-time");                
                if (startTime==null){
                    times[i][0] = 0.0;
                } else {
                    times[i][0] = Double.parseDouble(startTime);
                }
                if (endTime==null){
                    times[i][1] = 0.0;
                } else {
                    times[i][1] = Double.parseDouble(endTime);
                }
                i++;

             }
        } catch (JDOMException ex) {
            Logger.getLogger(TranscriptionTableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public int getRowCount() {
        return texts.length;
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex<2){
            if (times[rowIndex][columnIndex]!=null){
                return new Timepoint(times[rowIndex][columnIndex]*1000.0);
            } else {
                return null;
            }            
        } else if (columnIndex==2){
            return (speakers[rowIndex]);
        }
        return texts[rowIndex];
    }

    @Override
    public String getColumnName(int column) {
        if (column==0) return "Start";
        if (column==1) return "End";
        if (column==2) return "Sprecher";
        return "Text";
    }

    void setTime(int r, int c, double d) {
        times[r][c] = new Double(d);
        fireTableCellUpdated(r, c);
    }

    Double getTime(int row, int c) {
        return times[row][c];
    }

    void removeTimes() {
        for (int i=0; i<times.length; i++){
            times[i][0]=null;
            times[i][1]=null;
            fireTableCellUpdated(i,0);
            fireTableCellUpdated(i,1);
        }
    }

    Document getNewDocument() throws IOException {
        try {
            if (transcriptionDoc.getRootElement().getName().equals("folker-transcription")){
                Element tl = transcriptionDoc.getRootElement().getChild("timeline");
                tl.removeContent();
                HashSet<Double> allTimes = new HashSet<Double>();
                for (Double[] t : times){
                    allTimes.add(t[0]);
                    allTimes.add(t[1]);
                }
                //allTimes.addAll(Arrays.asList(times[0]));
                //allTimes.addAll(Arrays.asList(times[1]));
                ArrayList<Double> allTimesList = new ArrayList<Double>();
                allTimesList.addAll(allTimes);
                Collections.sort(allTimesList);
                HashMap<Double, String> times2IDs = new HashMap<Double, String>();
                int count=0;
                for (Double time : allTimesList){
                    // <timepoint timepoint-id="TLI_0" absolute-time="0.0"/>
                    Element timepoint = new Element("timepoint");
                    String id = "TLI_" + Integer.toString(count);
                    timepoint.setAttribute("timepoint-id", id);
                    timepoint.setAttribute("absolute-time", time.toString());
                    tl.addContent(timepoint);
                    times2IDs.put(time, id);
                    count++;
                }

                //<contribution speaker-reference="S2" start-reference="TLI_0" end-reference="TLI_1"
                //parse-level="2" speaker-dgd-id="???" id="c1" time="0.0">
                List l = XPath.newInstance("//contribution").selectNodes(transcriptionDoc);
                int i = 0;
                for (Object o : l){
                    Element contribution = (Element)o;
                    Double start = times[i][0];
                    Double end = times[i][1];
                    contribution.setAttribute("start-reference", times2IDs.get(start));
                    contribution.setAttribute("end-reference", times2IDs.get(end));
                    contribution.setAttribute("time", start.toString());
                    //<time timepoint-reference="TLI_0" time="0.0"/>
                    Element firstTime = (Element)(contribution.getChildren("time").get(0));
                    Element lastTime = (Element)(contribution.getChildren("time").get(1));
                    // fixed 02-08-2013
                    firstTime.setAttribute("timepoint-reference", times2IDs.get(start));
                    firstTime.setAttribute("time", start.toString());
                    // fixed 02-08-2013
                    lastTime.setAttribute("timepoint-reference", times2IDs.get(end));
                    lastTime.setAttribute("time", end.toString());
                    i++;
                }


                return transcriptionDoc;
            } else {
                //BBAW transcription!
                List l = XPath.newInstance("//text/body/div[1]/u/seg").selectNodes(transcriptionDoc);
                int i = 0;
                for (Object o : l){
                    Element contribution = (Element)o;
                    Double start = times[i][0];
                    Double end = times[i][1];
                    if (start!=null){
                        contribution.setAttribute("start-time", Double.toString(start));
                    } 
                    if (end!=null){
                        contribution.setAttribute("end-time", Double.toString(end));
                    }
                    i++;
                }


                return transcriptionDoc;
                
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
    }
    
}
