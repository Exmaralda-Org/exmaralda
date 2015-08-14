/*
 * InterlinearTextSaxHandler.java
 *
 * Created on 21. Maerz 2002, 16:32
 */

package org.exmaralda.partitureditor.interlinearText.sax;

import org.exmaralda.partitureditor.interlinearText.*;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;
import org.exmaralda.partitureditor.interlinearText.sax.SAXUtilities;

/**
 *
 * @author  Thomas
 * @version 
 */
public class InterlinearTextSaxHandler extends org.xml.sax.helpers.DefaultHandler {

    private InterlinearText it;
    private String currentPropertyName;
    private Vector openElements;
    private String currentPCData;
    private UdInformation currentUdInformation;
    private String currentUdAttributeName;
    private ItBundle currentItBundle;
    private SyncPoints currentSyncPoints;
    private SyncPoint currentSyncPoint;
    private ItLine currentItLine;
    private ItChunk currentItChunk;
    private ItLabel currentItLabel;
    private Run currentRun;
    private Line currentLine;
    private Formats formats;
    private Format currentFormat;
    private Link currentLink;
    private String currentAnchor;
    private int itElementCount = -1;
    private int itLineCount = -1;
    
    /** Creates new InterlinearTextSaxHandler */
    public InterlinearTextSaxHandler() {
        openElements = new Vector();
        currentPCData = new String();        
    }
    
    public InterlinearText getInterlinearText(){
        return it;
    }

    // ----------------------------------------------------------------------------------------------------------- 
    
    public void startDocument(){
      System.out.println("started reading document...");
      it = new InterlinearText();
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("document read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) throws SAXException {
        openElements.addElement(name);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.FORMATS       :       formats = new Formats();
                                                    break;
            case SAXUtilities.FORMAT        :       currentFormat = new Format();
                                                    currentFormat.setID(atts.getValue("id"));
                                                    break;
            case SAXUtilities.LINE          :       currentLine = new Line();
                                                    findFormat(atts,currentLine);
                                                    itElementCount++;
                                                    break;
            case SAXUtilities.IT_BUNDLE     :       currentItBundle = new ItBundle();
                                                    findFormat(atts,currentItBundle);
                                                    itElementCount++;
                                                    itLineCount=-1;
                                                    break;
            case SAXUtilities.SYNC_POINTS   :       currentSyncPoints = new SyncPoints();
                                                    findFormat(atts,currentSyncPoints);
                                                    break;
            case SAXUtilities.SYNC_POINT    :       currentSyncPoint = new SyncPoint();
                                                    currentSyncPoint.setID(atts.getValue("id"));
                                                    if (atts.getIndex("offset")>=0)
                                                        {
                                                            try {
                                                                String value = atts.getValue("offset");
                                                                double doubleValue = new Double(value).doubleValue();
                                                                currentSyncPoint.setOffset(doubleValue);
                                                            } catch (NumberFormatException nfe){
                                                                throw new SAXException("Wrong number format");
                                                            }
                                                        }
                                                    findFormat(atts,currentSyncPoint);
                                                    break;
            case SAXUtilities.IT_LINE       :       currentItLine = new ItLine();
                                                    String value = atts.getValue("breaktype");
                                                    short breaktype = -1;
                                                    if (value.equals("NB_TIME")){
                                                        breaktype = ItLine.NB_TIME;
                                                    } else if (value.equals("NB_NOTIME")){
                                                        breaktype = ItLine.NB_NOTIME;
                                                    } else if (value.equals("NB_DEP")){
                                                        breaktype = ItLine.NB_DEP;
                                                    } else if (value.equals("NB_LNK")){
                                                        breaktype = ItLine.NB_LNK;
                                                    } else if (value.equals("B")){
                                                        breaktype = ItLine.B;
                                                    } else if (value.equals("IMG")){
                                                        breaktype = ItLine.IMG;
                                                    } else {
                                                        throw new SAXException("Unknown breaktype.");
                                                    }
                                                    currentItLine.setBreakType(breaktype);
                                                    findFormat(atts,currentItLine);
                                                    break;
            case SAXUtilities.IT_CHUNK      :       currentItChunk = new ItChunk();
                                                    currentItChunk.setStart(currentSyncPoints.getSyncPointWithID(atts.getValue("start-sync")));
                                                    if (atts.getIndex("end-sync")>=0){
                                                        currentItChunk.setEnd(currentSyncPoints.getSyncPointWithID(atts.getValue("end-sync")));
                                                    }
                                                    findFormat(atts,currentItChunk);
                                                    break;
            case SAXUtilities.IT_LABEL      :       currentItLabel = new ItLabel();
                                                    findFormat(atts,currentItLabel);
                                                    break;
            case SAXUtilities.RUN           :       currentRun = new Run();
                                                    findFormat(atts,currentRun);
                                                    break;
            case SAXUtilities.LINK          :       currentLink = new Link();
                                                    currentLink.setURL(atts.getValue("url"));
                                                    currentLink.setType(atts.getValue("type"));
                                                    break;
            case SAXUtilities.ANCHOR        :       currentAnchor = new String();                                                    
                                                    break;
            case SAXUtilities.PROPERTY      :       currentPropertyName = atts.getValue("name");
                                                    break;
            case SAXUtilities.UD_ATTRIBUTE  :       currentUdAttributeName = atts.getValue("name");
                                                    break;
            case SAXUtilities.UD_INFORMATION:       currentUdInformation = new UdInformation();
                                                    break;
        }
        currentPCData = new String();
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endElement(String namespaceURI, String localName, String name){               
        openElements.removeElementAt(openElements.size()-1);
        int id = SAXUtilities.getIDForElementName(name);
        if (openElements.size()<=0) return;
        int parentID = SAXUtilities.getIDForElementName((String)openElements.elementAt(openElements.size()-1));
        switch (id) {
            case SAXUtilities.FORMATS       :       it.setFormats(formats);
                                                    break;
            case SAXUtilities.FORMAT        :       formats.addFormat(currentFormat);
                                                    break;
            case SAXUtilities.LINE          :       it.addItElement(currentLine);
                                                    break;
            case SAXUtilities.IT_BUNDLE     :       it.addItElement(currentItBundle);
                                                    break;
            case SAXUtilities.SYNC_POINTS   :       currentItBundle.setSyncPoints(currentSyncPoints);
                                                    break;
            case SAXUtilities.SYNC_POINT    :       currentSyncPoint.setText(currentPCData);
                                                    currentSyncPoints.addSyncPoint(currentSyncPoint);
                                                    break;
            case SAXUtilities.IT_LINE       :       currentItBundle.addItLine(currentItLine);
                                                    break;
            case SAXUtilities.IT_CHUNK      :       currentItLine.addItChunk(currentItChunk);
                                                    break;
            case SAXUtilities.IT_LABEL      :       currentItLine.setLabel(currentItLabel);
                                                    break;
            case SAXUtilities.RUN           :       currentRun.setText(currentPCData);
                                                    if (parentID==SAXUtilities.IT_CHUNK){
                                                        currentItChunk.addRun(currentRun);
                                                    } else if (parentID==SAXUtilities.IT_LABEL){
                                                        currentItLabel.addRun(currentRun);
                                                    } else if (parentID==SAXUtilities.LINE){
                                                        currentLine.addRun(currentRun);
                                                    }
                                                    break;
            case SAXUtilities.LINK          :       if (parentID==SAXUtilities.IT_CHUNK){
                                                        currentItChunk.addLink(currentLink);
                                                    } else if (parentID==SAXUtilities.IT_LABEL){
                                                        currentItLabel.addLink(currentLink);
                                                    }
                                                    break;
            case SAXUtilities.ANCHOR        :       currentAnchor = currentPCData;   
                                                    if (parentID==SAXUtilities.IT_BUNDLE){
                                                        currentItBundle.addAnchor(currentAnchor);
                                                    } else if (parentID==SAXUtilities.LINE){
                                                        currentLine.addAnchor(currentAnchor);
                                                    }            
                                                    break;
            case SAXUtilities.PROPERTY      :       currentFormat.setProperty(currentPropertyName,currentPCData);
                                                    break;
            case SAXUtilities.UD_ATTRIBUTE  :       currentUdInformation.setProperty(currentUdAttributeName, currentPCData);
                                                    break;
            case SAXUtilities.UD_INFORMATION:       switch (parentID){
                                                        case SAXUtilities.INTERLINEAR_TEXT : it.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.LINE             : currentLine.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.IT_BUNDLE        : currentItBundle.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.SYNC_POINTS      : currentSyncPoints.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.IT_LINE          : currentItLine.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.IT_LABEL         : currentItLabel.setUdInformation(currentUdInformation); break;
                                                        case SAXUtilities.IT_CHUNK         : currentItChunk.setUdInformation(currentUdInformation); break;
                                                    }
                                                    break;
                                                    
        }
        currentPCData = new String();
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
    

    private void findFormat(Attributes atts, org.exmaralda.partitureditor.interlinearText.Formattable f){
        if (atts.getIndex("formatref")>=0)
            {
              f.setFormat(formats.getFormatWithID(atts.getValue("formatref")));
            }
    }
}
