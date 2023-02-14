/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author thomas
 */
//public class SextantTokenHandler implements org.annolab.tt4j.TokenHandler<String> {
public class BasicTranscriptionTokenHandler implements org.annolab.tt4j.ProbabilityHandler<String> {


    boolean beenToProbabilityFlag;
    
    BasicTranscription bt;
    List<String[]> idList = new ArrayList<>();
    int count = 0;
    
    String lemmaCategory;
    String posCategory;
    String probCategory;
    
    Map<String, Tier> speaker2LemmaTiers = new HashMap<>();
    Map<String, Tier> speaker2PosTiers = new HashMap<>();
    Map<String, Tier> speaker2ProbTiers = new HashMap<>();

    public BasicTranscriptionTokenHandler(BasicTranscription bt, List<String[]> idList, String lemmaCategory, String posCategory, String probCategory) {
        this.bt = bt;
        this.idList = idList;
        this.lemmaCategory = lemmaCategory;
        this.posCategory = posCategory;
        this.probCategory = probCategory;
               
        System.out.println("BasicTransriptionTokenHandler initialised.");
    }
    
    void setIDList(List<String[]> ids) {
        idList = ids;
    }
    


    @Override
    public void probability(String pos, String lemma, double probability) {
        try {
            System.out.println(lemma + " " + pos);
            
            beenToProbabilityFlag = true;
            if (count>=idList.size()){
                System.out.println("Probability: " + pos + " " + lemma + " " + probability);
                System.out.println("Irregularity in POS tagging");
                System.out.println("---------------------------");
                
                //return;
            }
            
            String[] ids = idList.get(count);

            Tier sourceTier = bt.getBody().getTierWithID(ids[0]);
            int sourceTierIndex = bt.getBody().lookupID(sourceTier.getID());
            String speaker = sourceTier.getSpeaker();
            
            if (!(speaker2ProbTiers.containsKey(speaker))){
                Tier probTier = new Tier(bt.getBody().getFreeID(),speaker ,probCategory , "a");
                bt.getBody().insertTierAt(probTier, sourceTierIndex+1);
                speaker2ProbTiers.put(speaker, probTier);
            }

            if (!(speaker2PosTiers.containsKey(speaker))){
                Tier posTier = new Tier(bt.getBody().getFreeID(),speaker ,posCategory , "a");
                bt.getBody().insertTierAt(posTier, sourceTierIndex+1);
                speaker2PosTiers.put(speaker, posTier);
            }

            if (!(speaker2LemmaTiers.containsKey(speaker))){
                Tier lemmaTier = new Tier(bt.getBody().getFreeID(),speaker ,lemmaCategory , "a");
                bt.getBody().insertTierAt(lemmaTier, sourceTierIndex+1);
                speaker2LemmaTiers.put(speaker, lemmaTier);
            }



            Event posEvent = new Event(ids[1], ids[2], pos);
            Event lemmaEvent = new Event(ids[1], ids[2], lemma);
            Event probabilityEvent = new Event(ids[1], ids[2], Double.toString(probability));
            
            speaker2LemmaTiers.get(speaker).addEvent(lemmaEvent);
            speaker2PosTiers.get(speaker).addEvent(posEvent);
            speaker2ProbTiers.get(speaker).addEvent(probabilityEvent);
            
            
            count++;
        } catch (JexmaraldaException ex) {
            Logger.getLogger(BasicTranscriptionTokenHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void token(String token, String pos, String lemma) {
        //System.out.println(token + " / " + pos + " / " + lemma);
        beenToProbabilityFlag = false;
        
        probability(pos, lemma, 0.99);        
        
    }



}
