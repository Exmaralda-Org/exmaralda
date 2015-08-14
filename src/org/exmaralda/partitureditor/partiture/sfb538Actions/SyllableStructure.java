/*
 * SyllableStructure.java
 *
 * Created on 22. April 2004, 11:26
 */

package org.exmaralda.partitureditor.partiture.sfb538Actions;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.*;
import org.xml.sax.*;


/**
 *
 * @author  thomas
 */
public class SyllableStructure {
    
    static String pathToFSM = "/org/exmaralda/partitureditor/fsm/xml/SyllableStructure.xml";
    FiniteStateMachine fsm;
    
    /** Creates a new instance of SyllableStructure */
    public SyllableStructure() throws SAXException, FSMException {
         FSMSaxReader sr = new FSMSaxReader();
         java.io.InputStream is2 = getClass().getResourceAsStream(pathToFSM);
         fsm = sr.readFromStream(is2);
    }
    
    public Tier makeSyllableStructureTier(Tier phoneticTier) {
        Tier result = new Tier();
        result.setType("a");
        result.setSpeaker(phoneticTier.getSpeaker());
        result.setCategory("syll");
        result.setID(phoneticTier.getID() + "_syll");  
        result.setDisplayName("");
        for (int pos=0; pos<phoneticTier.getNumberOfEvents(); pos++){
            Event event = phoneticTier.getEventAt(pos);
            String annotation = "";
            try{
                annotation = fsm.process(event.getDescription(), false);
            } catch (FSMException e){
                annotation = "#ERROR: " + e.getProcessedOutput();
            }
            Event newEvent = new Event();
            newEvent.setStart(event.getStart());
            newEvent.setEnd(event.getEnd());
            newEvent.setDescription(annotation);
            result.addEvent(newEvent);            
        }
        return result;        
    }
    
}
