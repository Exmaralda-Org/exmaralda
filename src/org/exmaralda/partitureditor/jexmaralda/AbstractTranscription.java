package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;

/*
 * AbstractTranscription.java
 *
 * Created on 6. Februar 2001, 17:04
 */


/* Revision History
 *  0   17-Apr-2001 first reference implementation
*/

/**
 * Abstract parent class of BasicTranscription, SegmentedTranscription and ListTranscription.
 * Only contains the head common to all these classes.
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */
public abstract class AbstractTranscription extends Object {

    private Head head;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new GeneralTranscription */
    public AbstractTranscription() {
        head = new Head();
    }
    
    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the head of the transcription */
    public Head getHead(){
        return head;
    }
    
    /** sets the head of the transcription to the specified value */
    public void setHead(Head h){
        head = h;
    }    
    
  

    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns the head as an XML element &lt;head&gt; as
     *  specified in the corresponding dtds */
    public String toXML(){
        return head.toXML();
    }
           
}