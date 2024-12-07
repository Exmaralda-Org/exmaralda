/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;

/**
 *
 * @author thomas
 */
public class TreeTaggableBasicTranscription implements TreeTaggableDocument {

    BasicTranscription bt;
    Map<String, List<List<Event>>> tierIDs2SegmentChains = new HashMap<>();
    int totalSegmentChains = 0;
    int totalTokens = 0;
    List<List<String>> tokens = new ArrayList<>();
    List<String[]> ids  = new ArrayList<>();

    public TreeTaggableBasicTranscription(BasicTranscription bt, String tokenTierCategory) {
        this.bt = bt;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            if ((tier.getCategory()==null) || (!(tier.getCategory().equals(tokenTierCategory)))) continue;
            List<List<Event>> segmentChains = tier.getSegmentChains(bt.getBody().getCommonTimeline());
            tierIDs2SegmentChains.put(tier.getID(), segmentChains);
            totalSegmentChains+=segmentChains.size();
            for (List<Event> sc : segmentChains){
                //System.out.println("sc");
                totalTokens+=sc.size();
                List<String> thisTokens = new ArrayList<>();
                tokens.add(thisTokens);
                for(Event e : sc){
                    //System.out.print(e.getDescription() + " ");
                    if ((e.getDescription()!=null) && e.getDescription().trim().length()>0){
                        //thisTokens.add(e.getDescription().replaceAll("\\u00A0", " ").trim());
                        String[] subTokens = e.getDescription().replaceAll("\\u00A0", " ").trim().split(" ");
                        for (String subToken : subTokens){
                            if (subToken.contains("|")){
                                subToken = subToken.split("\\|")[0].replaceAll("\\*", "");
                            }
                            thisTokens.add(subToken);
                            String[] idSeries = {
                                tier.getID(),
                                e.getStart(),
                                e.getEnd()
                            };
                            ids.add(idSeries);
                        }
                    } else {
                        System.out.println("EMPTY TOKEN!");
                    }
                } 
                //System.out.println("");
            }
        }
    }

    
    @Override
    public int getNumberOfTaggableSegments() {
        return totalSegmentChains;
    }

    @Override
    public int getNumberOfTokens() {
        return totalTokens;
    }

    @Override
    public List getTokensAt(int pos) throws IOException {
        //String[] tokenizedTokens = tokens.get(pos).split(" ");
        //return Arrays.asList(tokenizedTokens);
        return tokens.get(pos);
    }

    @Override
    public List getIDs() throws IOException {
        return ids;
    }

    @Override
    public String getBase() {
        return "";
    }

}
