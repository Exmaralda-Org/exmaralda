package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import java.io.*;
/*
 * Speakertable.java
 *
 * Created on 6. Februar 2001, 12:00
 */




/* Revision History
 *  0   06-Feb-2001 Creation according to revision 0 of 'exmaralda-time-transcription.dtd'
 *                  and 'exmaralda-segment-transcription.dtd', most methods taken from
 *                  version 0
 */

/**
 *
 * @author  Thomas Schmidt (thomas.schmidt@uni-hamburg.de) 
 * @version 1
 */

public class Speakertable extends Vector {

    private Hashtable positions;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new Speakertable */
    public Speakertable() {
        super();
        positions = new Hashtable();
    }
    
   /** returns a copy of this speakertable */
    public Speakertable makeCopy(){
        Speakertable result = new Speakertable();
        String ids[] = getAllSpeakerIDs();
        for (int pos=0; pos<ids.length; pos++){
            try {result.addSpeaker(getSpeakerWithID(ids[pos]).makeCopy());}
            catch (JexmaraldaException je) {}
        }
        return result;
    }

    // ********************************************
    // ********** BASIC MANIPULATION **************
    // ********************************************

    /** returns true if a speaker with the specified id exists in this speaker table, false otherwise */
    public boolean containsSpeakerWithID(String id){
        if (positions.containsKey(id)){return true;}
        return false;
    }
    
    /** returns the speaker with the specified id, throws a JexmaraldaException if no such speaker exists */
    public Speaker getSpeakerWithID(String id) throws JexmaraldaException{
        if (!positions.containsKey(id)){throw new JexmaraldaException(1, new String("No such speaker: " + id));}
        return (Speaker)getSpeakerAt(lookupID(id));
    }
    
    /** returns the speaker with the specified id, throws a JexmaraldaException if no such speaker exists */
    public void removeSpeakerWithID(String id) throws JexmaraldaException{
        if (!positions.containsKey(id)){throw new JexmaraldaException(1, new String("No such speaker: " + id));}
        removeElementAt(lookupID(id));
        positions.remove(id);
        updatePositions();
    }

    public Speaker getSpeakerAt(int position){
        return (Speaker)elementAt(position);
    }
    
    private int lookupID(String id){
        if (positions.containsKey(id)) { return ((Integer)positions.get(id)).intValue(); }
        return -1;
    }
    
    public void updatePositions(){
        positions.clear();
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
            positions.put(getSpeakerAt(pos).getID(), new Integer(pos));
        }       
    }

    /** returns the number of speakers in the table */
    public int getNumberOfSpeakers(){
        return size();
    }
            
    /** adds the specified speaker to the speakertable,
     *  throws a JexmaraldaException if a speaker with his ID already exists*/
    public void addSpeaker(Speaker s) throws JexmaraldaException {
      if (positions.containsKey(s.getID())){throw new JexmaraldaException(11, "Speaker " + s.getID() + " already in speakertable");}
      addElement(s);
      positions.put(s.getID(),new Integer(getNumberOfSpeakers()-1));       
    }        
    
    /** returns a free id */
    public String getFreeID(){
        int i=0;
        while (positions.containsKey((String)("SPK" + new Integer(i).toString()))){
            i++;
        }
        return (String)("SPK" + new Integer(i));
    }
    
    /** returns a string array with the speaker IDs of the speakertable */
    public String[] getAllSpeakerIDs(){
        Vector resultVector = new Vector();
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
            resultVector.addElement(getSpeakerAt(pos).getID());
        }
        return StringUtilities.stringVectorToArray(resultVector);
    }
    

    // ********************************************
    // ******* MANIPULATION OF UD-ATTRIBUTES ******
    // ********************************************

    // changed in Version 1.2.5. to get attributes in order
    public String[] getAllUDAttributes(){
        Vector allAttributes = new Vector();
        HashSet controlSet = new HashSet();
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
            String[] atts=getSpeakerAt(pos).getUDSpeakerInformation().getAllAttributes();            
            if (atts!=null){
                for (int i=0; i<atts.length; i++){
                    allAttributes.addElement(atts[i]);
                    controlSet.add(atts[i]);
                }
            }
        }
        String[] result = new String[controlSet.size()];
        HashSet alreadyAdded = new HashSet();
        int addPos=0;
        for (int pos=0; pos<allAttributes.size(); pos++){
           String attributeName = new String((String)allAttributes.elementAt(pos));
           if (!alreadyAdded.contains(attributeName)){
            result[addPos]=attributeName;
            addPos++;
            alreadyAdded.add(attributeName);
           }
        }
        return result;
    }
    
    public void collectAttributes(String speakerID) throws JexmaraldaException{
        if (!positions.containsKey(speakerID)){throw new JexmaraldaException(1, new String("No such speaker: " + speakerID));}
        String[] allAttributes = getAllUDAttributes();
        for (int pos=0; pos<allAttributes.length; pos++){
            if (!getSpeakerWithID(speakerID).getUDSpeakerInformation().containsAttribute(allAttributes[pos])){
                getSpeakerWithID(speakerID).getUDSpeakerInformation().setAttribute(allAttributes[pos], "---unknown---");
            }
        }            
    }
    
    public void makeUDAttributesFromTemplate(Speakertable template){
        String[] allAttributes = template.getAllUDAttributes();
        for (int i=0; i<getNumberOfSpeakers(); i++){
            Speaker sp = getSpeakerAt(i);
            for (int pos=0; pos<allAttributes.length; pos++){
                if (!sp.getUDSpeakerInformation().containsAttribute(allAttributes[pos])){
                    sp.getUDSpeakerInformation().setAttribute(allAttributes[pos], "---unknown---");
                }
            }            
        }        
    }
    
    
    // ********************************************
    // ********** XML OUTPUT **********************
    // ********************************************

    /** returns speakertable as XML-element &lt;speakertable&gt; as specified in the corresponding dtd*/
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<speakertable>");
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
           sb.append(getSpeakerAt(pos).toXML());
        }
        sb.append("</speakertable>");
        return sb.toString();
    }

    // ********************************************
    // ********** RTF OUTPUT **********************
    // ********************************************
    public String toRTF(){
        StringBuffer sb = new StringBuffer();
        sb.append("{\\par\\fs28\\b\\ul Speakertable}\\par");
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
           sb.append(getSpeakerAt(pos).toRTF());
        }
        sb.append("\\par");
        return sb.toString();
    }
    
    /** makes uniform speaker IDs */
    Hashtable normalize(){
        Hashtable mappings = new Hashtable();
        for (int pos=0; pos<getNumberOfSpeakers(); pos++){
            Speaker speaker = getSpeakerAt(pos);
            String oldID = speaker.getID();
            String newID = "SPK" + Integer.toString(pos);
            speaker.setID(newID);
            mappings.put(oldID, newID);
        }        
        this.updatePositions();
        return mappings;
    }
    

}