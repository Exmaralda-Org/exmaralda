/*
 * AbstractTierBody.java
 *
 * Created on 27. August 2002, 14:40
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 * Abstract parent class of BasicBody 
 * @author  Thomas
 */
public class AbstractTierBody extends AbstractBody {
    
    /** Creates a new instance of AbstractTierBody */
    public AbstractTierBody() {
    }
    
    // **************** METHODS FOR MANIPULATING TIERS IN GENERAL *************************
    
    /** returns position of tier with specified id, -1 if there is 
    no such tier*/
    public int lookupID(String id){
        if (positions.containsKey(id)) { return ((Integer)positions.get(id)).intValue(); }
        return -1;
    }

    /** returns true if the body contains a tier with the specified id, false otherwise */
    public boolean containsTierWithID(String id){
        if (positions.containsKey(id)) { return true; }
        return false;
    }

    /** adds tier t under its ID if this ID is free, otherwise throws a JexmaraldaException (code 6) */
    public void addTier (AbstractTier t) throws JexmaraldaException {
        if (lookupID(t.getID())!=-1){throw new JexmaraldaException(6, new String("ID " + t.getID() + " already exists in this transcription."));}
        addElement(t);
        positions.put(t.getID(),new Integer(getNumberOfTiers()-1));       
    }            

    /** removes the tier at the specified position */
    public void removeTierAt(int position) {        
        positions.remove(((AbstractTier)elementAt(position)).getID());
        remove(position);
        updatePositions();
    }
    
    /** updates the position hashtable */
    public void updatePositions(){
        positions.clear();
        for (int i=0; i<getNumberOfTiers(); i++){
            positions.put(((AbstractTier)elementAt(i)).getID(), new Integer(i));
        }       
    }
    
    /** returns the tier with the given ID. If no such tier exists, throws
     * a corresponding Jexmaralda Exception */
    AbstractTier getAbstractTierWithID(String id) throws JexmaraldaException{
        if (lookupID(id)==-1) { throw new JexmaraldaException(5, new String("No such tier: " + id));}
        return (AbstractTier)elementAt(lookupID(id));
    }

    /** returns the tier at the given position */
    AbstractTier getAbstractTierAt(int pos) {
        return (AbstractTier)elementAt(pos);
    }

    /** returns number of tiers in the transcription */
    public int getNumberOfTiers(){
        return size();
    }
    
    /** returns an array with all tier IDs in this body */
    public String[] getAllTierIDs(){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            result.addElement(((AbstractTier)elementAt(pos)).getID());
        }
        return StringUtilities.stringVectorToArray(result);
    }    
    
    /** returns the IDs of all tiers that match the specified properties,
     * i.e. that have the specified speaker and are of the specified type */
    public String[] getTiersWithProperties(String speakerID, String t){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            AbstractTier tier = (AbstractTier)elementAt(pos);
            if ((tier.getSpeaker().equals(speakerID)) && (tier.getType().equals(t))){
                result.addElement(tier.getID());
            }
        }
        return StringUtilities.stringVectorToArray(result);
    }    
    
    /** returns the IDs of all tiers that match the specified properties,
     * i.e. that have the specified speaker and are of the specified type */
    public String[] getTiersOfType(String t){
        Vector result = new Vector();
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            AbstractTier tier = (AbstractTier)elementAt(pos);
            if (tier.getType().equals(t)){
                result.addElement(tier.getID());
            }
        }
        return StringUtilities.stringVectorToArray(result);
    }    

    /** inserts the specified tier before the one with the specified ID
     *  throws an exception if the latter does not exist */
    public void insertTierBeforeTierWithID(AbstractTier tier, String id) throws JexmaraldaException {
        if (lookupID(id)==-1) { throw new JexmaraldaException(5, new String("No such tier: " + id));}
        if (lookupID(tier.getID())!=-1){throw new JexmaraldaException(6, new String("ID " + tier.getID() + " already exists in this transcription."));}
        int position = lookupID(id);
        this.insertElementAt(tier, position);
        updatePositions();
    }
    
    /** inserts the given tier at the given position */
    public void insertTierAt(AbstractTier tier, int pos) throws JexmaraldaException {
        if (lookupID(tier.getID())!=-1){throw new JexmaraldaException(6, new String("ID " + tier.getID() + " already exists in this transcription."));}
        this.insertElementAt(tier, pos);
        updatePositions();
    }
    
    
    /** removes the tier with the specified id */
    public void removeTierWithID (String id) throws JexmaraldaException {
        if (lookupID(id)==-1) { throw new JexmaraldaException(5, new String("No such tier: " + id));}
        removeTierAt(lookupID(id));
    }

    
    /** returns a free id */
    public String getFreeID(){
        if (getNumberOfTiers()>0) {
            int i=0;
            while (positions.containsKey((String)("TIE" + new Integer(i).toString()))){i++;}
            return (String)("TIE" + new Integer(i));
        }
        return "TIE0";
    }
    
    /** removes all empty tiers, i.e. all tiers that do not contain any events */
    public void removeEmptyTiers(){
        for (int pos=0; pos<size(); pos++){
            if (getAbstractTierAt(pos).size()<=0){
                removeTierAt(pos);
                pos--;
            }
        }
    }
    
    
}
