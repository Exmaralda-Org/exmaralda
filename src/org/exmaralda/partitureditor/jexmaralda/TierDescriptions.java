/*
 * TierDescriptions.java
 *
 * Created on 10. August 2001, 10:59
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class TierDescriptions extends Hashtable {

    /** Creates new TierDescriptions */
    public TierDescriptions() {
        super();
    }
    
    /** Creates new TierDescriptions */
    public TierDescriptions(BasicTranscription t) {
        super();
        for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
            Tier tier = t.getBody().getTierAt(pos);
            addTierDescription(tier.getID(), tier.getDescription(t.getHead().getSpeakertable()));
        }
    }

    public void addTierDescription(String tierID, String description){
        put(tierID, description);
    }
    
    public String getTierDescriptionForTierID(String tierID){
        if (containsKey(tierID)){return (String)get(tierID);}
        return new String();
    }
    

}