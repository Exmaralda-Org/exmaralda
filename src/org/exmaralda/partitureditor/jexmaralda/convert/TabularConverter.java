/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author bernd
 */
public class TabularConverter {
 
    
    // This is for issue #446
    
    public List<String[]> BasicTranscriptionToTabular(BasicTranscription bt, String masterTierID, String[] dependentTierIDs) throws JexmaraldaException{
        List<String[]> result = new ArrayList<>();
        Tier masterTier = bt.getBody().getTierWithID(masterTierID);
        //String[] annotationTierIDs = bt.getBody().getTiersOfSpeakerAndType(masterTier.getSpeaker(), "a");
        String[] annotationTierIDs = dependentTierIDs;
        for (int pos=0; pos<masterTier.getNumberOfEvents(); pos++){
            String[] thisResult = new String[annotationTierIDs.length + 3];
            Event masterEvent = masterTier.getEventAt(pos);
            thisResult[0] = masterEvent.getDescription();
            thisResult[1] = masterEvent.getStart();
            thisResult[2] = masterEvent.getEnd();
            int i = 3;
            for (String annotationTierID : annotationTierIDs){
                Tier dependentTier = bt.getBody().getTierWithID(annotationTierID);
                Vector<Event> annotationsBetween 
                        //= dependentTier.getEventsBetween(bt.getBody().getCommonTimeline(), masterEvent.getStart(), masterEvent.getEnd());
                        = dependentTier.getEventsIntersecting(bt.getBody().getCommonTimeline(), masterEvent.getStart(), masterEvent.getEnd());
                String concatenatedDescription = "";
                for (Event annotationEvent : annotationsBetween){
                    concatenatedDescription+=annotationEvent.getDescription() + " ";
                }
                thisResult[i] = concatenatedDescription;
                i++;
            }
            result.add(thisResult);
        }        
        return result;
    }
}
