/*
 * CorpusTree.java
 *
 * Created on 11. Juni 2004, 14:23
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import javax.swing.tree.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import java.io.*;


/**
 *
 * @author  thomas
 */
public class CorpusTree extends DefaultMutableTreeNode implements XMLable {
    
    private HashSet transcriptionNames = new HashSet();
    
    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    
    /** Creates a new instance of CorpusTree */
    public CorpusTree(String corpusName) {
        super();
        this.setUserObject(new String(corpusName));
    }
    
    public CorpusTree(String corpusName, String[] filenameList) throws JexmaraldaException, SAXException{
        super();
        this.setUserObject(new String(corpusName));
        SegmentedTranscriptionSaxReader stsr = new SegmentedTranscriptionSaxReader();
        for (int pos=0; pos<filenameList.length; pos++){
            SegmentedTranscription st = stsr.readFromFile(filenameList[pos]);
            String transcriptionName = st.getHead().getMetaInformation().getTranscriptionName();
            if (transcriptionName.length()<=0){
                transcriptionName = getFreeTranscriptionName();
            }
            CorpusElement ce = new CorpusElement(true, transcriptionName, filenameList[pos]);
            this.addCorpusElement(ce);
        }
    }

    public void readFiles(String[] filenameList) throws JexmaraldaException, SAXException {
        SegmentedTranscriptionSaxReader stsr = new SegmentedTranscriptionSaxReader();
        for (int pos=0; pos<filenameList.length; pos++){
            this.fireSearchProgressChanged(filenameList.length, pos, null);
            SegmentedTranscription st = stsr.readFromFile(filenameList[pos]);
            String transcriptionName = st.getHead().getMetaInformation().getTranscriptionName();
            if (transcriptionName.length()<=0){
                transcriptionName = getFreeTranscriptionName();
            }
            CorpusElement ce = new CorpusElement(true, transcriptionName, filenameList[pos]);
            this.addCorpusElement(ce);
        }        
    }
    
    public HashSet getMetaDataKeys() throws SAXException, JexmaraldaException{
        SegmentedTranscriptionSaxReader stsr = new SegmentedTranscriptionSaxReader();
        Vector corpusElements = getSelectedCorpusElements();
        HashSet returnSet = new HashSet();
        System.out.println("ewuriouerwopi");
        for (int pos=0; pos<corpusElements.size(); pos++){
            Object o = corpusElements.elementAt(pos);
            CorpusElement ce = (CorpusElement)o;
            String path = ce.getTranscriptionPath();
            System.out.println(path);
            SegmentedTranscription st = stsr.readFromFile(path);
            String[] keysForThisFile = st.getHead().getSpeakertable().getAllUDAttributes();
            for (int i=0; i<keysForThisFile.length; i++){
                returnSet.add(keysForThisFile[i]);
            }
        }
        return returnSet;
    }
    
    public void addSearchListener(SearchListener l) {
         listenerList.add(SearchListener.class, l);
    }
    
    
    public Vector getSelectedCorpusElements(){        
        Vector result = new Vector();
        for (Enumeration e=depthFirstEnumeration(); e.hasMoreElements(); ){
            Object o = e.nextElement();
            if (o instanceof CorpusElement){
                if (((CorpusElement)o).isSelected()){
                    result.add(o);
                }
            }
        }
        return result;
    }
    
    public void addCorpusElement(CorpusElement ce){
        if (ce.transcriptionName.length()<=0){
            ce.transcriptionName = getFreeTranscriptionName();
        }
        this.add(ce);
        transcriptionNames.add(ce.getTranscriptionName());
    }
    
    private String getFreeTranscriptionName(){
        int count = 1;
        while (transcriptionNames.contains("Transcription" + Integer.toString(count))) {count++;}
        return "Transcription" + Integer.toString(count);
    }
    
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<corpus name=\"" + toString() + "\">");
        for (int pos=0; pos<getChildCount(); pos++){
            sb.append(((XMLable)(getChildAt(pos))).toXML());
        }
        sb.append("</corpus>");
        return sb.toString();
    }
    
    public void writeToPlainTextFile(String path) throws IOException {
        FileOutputStream fos = new FileOutputStream(path);
        for (int pos=0; pos<getChildCount(); pos++){
            CorpusElement ce = (CorpusElement)(getChildAt(pos));
            String file = new java.io.File(ce.transcriptionPath).toURI().toString() + System.getProperty("line.separator");
            fos.write(file.getBytes());
        }
        fos.close();        
    }
    
    public void fireSearchMessageChanged(String message) {
         // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==SearchListener.class) {
                ((SearchListener)listeners[i+1]).searchMessageChanged(message);
            }
         }        
    }
    
    public void fireSearchProgressChanged(int i1, int i2, String message) {
         // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==SearchListener.class) {
                ((SearchListener)listeners[i+1]).searchProgressChanged(i1, i2, message);
            }
         }        
    }
    
    

}
