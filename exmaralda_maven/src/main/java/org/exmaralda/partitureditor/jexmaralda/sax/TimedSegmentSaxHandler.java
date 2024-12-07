/*
 * TimedSegmentSaxHandler.java
 *
 * Created on 6. August 2002, 15:18
 */

package org.exmaralda.partitureditor.jexmaralda.sax;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;
import org.exmaralda.partitureditor.jexmaralda.sax.SAXUtilities;

/**
 *
 * @author  Thomas
 * @version 
 */
public class TimedSegmentSaxHandler extends org.xml.sax.helpers.DefaultHandler{

    /** Creates new TimedSegmentSaxHandler */
    public TimedSegmentSaxHandler() {
    }
    
    private String currentPCData;
    
    private Vector segmentStack;
    
    public TimedSegment getTimedSegment(){
        return ((TimedSegment)(segmentStack.firstElement()));
    }
    
    public void startDocument(){
        segmentStack = new Vector();
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {        
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.TS                    :   TimedSegment ts = new TimedSegment();
                                                        currentPCData = new String();
                                                        ts.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            ts.setID(atts.getValue("id"));
                                                        }
                                                        ts.setStart(atts.getValue("s"));
                                                        ts.setEnd(atts.getValue("e"));
                                                        segmentStack.addElement(ts);
                                                        break;
            case SAXUtilities.ATS                   :   AtomicTimedSegment ats = new AtomicTimedSegment();                                            
                                                        currentPCData = new String();
                                                        ats.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            ats.setID(atts.getValue("id"));
                                                        }
                                                        ats.setStart(atts.getValue("s"));
                                                        ats.setEnd(atts.getValue("e"));
                                                        segmentStack.addElement(ats);
                                                        break;                                                        
            case SAXUtilities.NTS                   :   NonTimedSegment nts = new NonTimedSegment();                                            
                                                        currentPCData = new String();
                                                        nts.setName(atts.getValue("n"));
                                                        if (atts.getIndex("id")>=0){
                                                            nts.setID(atts.getValue("id"));
                                                        }
                                                        segmentStack.addElement(nts);
                                                        break;                                                        
        }
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){               
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.TS                    :   TimedSegment ts = ((TimedSegment)(segmentStack.lastElement()));
                                                        if (currentPCData.length()>0){
                                                            ts.setDescription(currentPCData);
                                                        }
                                                        currentPCData = new String();
                                                        int stackSize = segmentStack.size();
                                                        if (stackSize>1){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize-2))).add(ts);
                                                            segmentStack.removeElementAt(stackSize-1);
                                                        }
                                                        break;
            case SAXUtilities.ATS                    :  AtomicTimedSegment ats = ((AtomicTimedSegment)(segmentStack.lastElement()));
                                                        ats.setDescription(currentPCData);
                                                        currentPCData = new String();
                                                        int stackSize2 = segmentStack.size();
                                                        if (stackSize2>1){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize2-2))).add(ats);
                                                            segmentStack.removeElementAt(stackSize2-1);
                                                        }
                                                        break;
            case SAXUtilities.NTS                    :  NonTimedSegment nts = ((NonTimedSegment)(segmentStack.lastElement()));
                                                        nts.setDescription(currentPCData);
                                                        currentPCData = new String();
                                                        int stackSize3 = segmentStack.size();
                                                        if (stackSize3>1){
                                                            ((TimedSegment)(segmentStack.elementAt(stackSize3-2))).add(nts);
                                                            segmentStack.removeElementAt(stackSize3-1);
                                                        }
                                                        break;
        }           
    }
        
    public void characters (char buff[], int offset, int length){
        String newData = new String();
        for (int pos=offset; pos<offset+length; pos++){
            if ((buff[pos]!='\n') && (buff[pos]!='\t')){
                newData+=buff[pos]; //ignore newline and tabs
            }
        }
        currentPCData+=newData;
    } // end characters
            
// ----------------------------------------------------------------------------------------------------------- 
    public void error (SAXParseException e) throws SAXParseException {
        System.out.println("Error: " + e.getMessage());
        throw e;
    }
    
    

// ----------------------------------------------------------------------------------------------------------- 
    public void setDocumentLocator (Locator l)
    {
    }


// ----------------------------------------------------------------------------------------------------------- 
    public void ignorableWhitespace (char buf [], int offset, int len)
    throws SAXException
    {
        // do nothing
    }
   
    
    

}
