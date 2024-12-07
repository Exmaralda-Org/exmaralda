/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class CollectTypesInCategory extends AbstractCorpusChecker {

    
    String category;
    String type;
    Map<String, Integer> allTypesTable = new HashMap<>();
    
    public CollectTypesInCategory(String c, String t) {
        super();
        category = c;
        type = t;
    }


    @Override
    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, SAXException {
        for (int i=0; i<bt.getBody().getNumberOfTiers(); i++){
            Tier tier = bt.getBody().getTierAt(i);
            boolean isRelevantTier = (type==null || type.equals(tier.getType())) && category.equals(tier.getCategory());
            if ((!isRelevantTier)) continue;
            Map<String, Integer> thisTypesTable = tier.getTypesTable();
            for (String key : thisTypesTable.keySet()){
                allTypesTable.put(key, allTypesTable.getOrDefault(key, 0) + thisTypesTable.get(key));
            }
        }
    }
    
    public Map<String, Integer> getTypesTable(){
        return allTypesTable;
    }
    
}
