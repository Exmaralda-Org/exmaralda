/*
 * FSMSaxHandler.java
 *
 * Created on 29. Juli 2002, 16:09
 */

package org.exmaralda.partitureditor.fsm;

import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;
//import com.sun.xml.parser.Resolver;

/**
 *
 * @author  Thomas
 * @version 
 */
public class FSMSaxHandler extends org.xml.sax.helpers.DefaultHandler{

    private Hashtable charSets;
    private FiniteStateMachine fsm;

    private String currentCharSet;
    private String currentCharSetName;
    private String startState;
    private String endState;
    private String currentPCData;
    private String currentSource;
    private String currentTarget;
    private String currentInput;
    private int currentInputType;
    private Output currentOutput;
    private boolean currentOOValue;
    private String currentPrefix;
    private String currentSuffix;
    private String currentForbidden;
    
    private final static int CHAR_TYPE = 0;
    private final static int CHAR_SET_TYPE = 1;
    private final static int OTHER_TYPE = 2;
    private final static int END_TYPE = 3;
    
    
    /** Creates new FSMSaxHandler */
    public FSMSaxHandler() {
        currentPCData = new String();                
        charSets = new Hashtable();
    }
    
    FiniteStateMachine getFSM(){
        return fsm;
    }
    
// ----------------------------------------------------------------------------------------------------------- 
    public void startDocument(){
      System.out.println("started reading fsm...");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void endDocument(){
        System.out.println("fsm read.");
    }

// ----------------------------------------------------------------------------------------------------------- 
    public void startElement(String namespaceURI, String localName, String name, Attributes atts ) {
        //System.out.println("Opening element " + name);
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.FSM           :       fsm = new FiniteStateMachine();
                                                    fsm.name = atts.getValue("name");
                                                    break;
            case SAXUtilities.CHAR_SET      :       currentCharSet = new String();
                                                    currentCharSetName = atts.getValue("id");
                                                    break;
            case SAXUtilities.CHAR          :       break;
            case SAXUtilities.START_STATE   :       startState = atts.getValue("id");
                                                    break;
            case SAXUtilities.END_STATE     :       endState = atts.getValue("id");
                                                    break;
            case SAXUtilities.TRANSITIONS   :       currentSource = atts.getValue("source");
                                                    break;
            case SAXUtilities.TRANSITION    :       currentOOValue = false;
                                                    currentPrefix = null;
                                                    currentSuffix = null;
                                                    currentOutput = new Output(null, null, false);
                                                    break;
            case SAXUtilities.INPUT_CHAR    :       currentInputType = CHAR_TYPE;
                                                    break;
            case SAXUtilities.INPUT_CHAR_SET:       currentInputType = CHAR_SET_TYPE;
                                                    currentInput = atts.getValue("id");
                                                    break;
            case SAXUtilities.INPUT_OTHER   :       currentInputType = OTHER_TYPE;
                                                    break;
            case SAXUtilities.INPUT_END     :       currentInputType = END_TYPE;
                                                    break;
            case SAXUtilities.TARGET        :       currentTarget = atts.getValue("id");
                                                    break;
            case SAXUtilities.OUTPUT        :       currentOOValue = (atts.getValue("oo").equals("yes"));
                                                    break;
            case SAXUtilities.PREFIX        :       break;
            case SAXUtilities.SUFFIX        :       break;
        }
        currentPCData = new String();
    }

    public void endElement(String namespaceURI, String localName, String name){      
        int id = SAXUtilities.getIDForElementName(name);
        switch (id) {
            case SAXUtilities.FSM           :       break;
            case SAXUtilities.CHAR_SET      :       charSets.put(currentCharSetName,currentCharSet);
                                                    break;
            case SAXUtilities.CHAR          :       currentCharSet+=currentPCData;
                                                    break;
            case SAXUtilities.START_STATE   :       fsm.setStartState(startState);
                                                    break;
            case SAXUtilities.END_STATE     :       fsm.setEndState(endState);
                                                    break;
            case SAXUtilities.TRANSITIONS   :       break;
            case SAXUtilities.TRANSITION    :       switch (currentInputType){
                                                        case CHAR_TYPE      :   fsm.putTransition(currentSource, currentTarget, currentInput, currentOutput);
                                                                                break;
                                                        case CHAR_SET_TYPE  :   String charSet = (String)charSets.get(currentInput);
                                                                                for (int pos=0; pos<charSet.length(); pos++){
                                                                                    String c = new Character(charSet.charAt(pos)).toString();
                                                                                    fsm.putTransition(currentSource, currentTarget, c, currentOutput);
                                                                                }
                                                                                break;
                                                        case OTHER_TYPE     :   fsm.putTransition(currentSource, currentTarget, "OTH", currentOutput);
                                                                                break;
                                                        case END_TYPE       :   fsm.putTransition(currentSource, currentTarget, "END", currentOutput);
                                                                                break;
                                                    }
                                                    break;
            case SAXUtilities.INPUT_CHAR    :       currentInput = currentPCData;
                                                    break;
            case SAXUtilities.INPUT_CHAR_SET:       break;
            case SAXUtilities.INPUT_OTHER   :       break;
            case SAXUtilities.INPUT_END     :       break;
            case SAXUtilities.TARGET        :       break;
            case SAXUtilities.OUTPUT        :       currentOutput = new Output(currentPrefix, currentSuffix, currentOOValue);
                                                    break;
            case SAXUtilities.PREFIX        :       currentPrefix = currentPCData;
                                                    break;
            case SAXUtilities.SUFFIX        :       currentSuffix = currentPCData;
                                                    break;
            case SAXUtilities.FORBIDDEN     :       currentForbidden = currentPCData;
                                                    fsm.putForbidden(currentSource, currentForbidden);
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
    public void error (SAXParseException e)
    throws SAXParseException
    {
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
