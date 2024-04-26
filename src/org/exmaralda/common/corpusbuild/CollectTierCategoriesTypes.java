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
public class CollectTierCategoriesTypes extends AbstractCorpusChecker {

    // 26-04-2024
    Map<CategoryPlusType, Integer> categoryPlusTypeMap = new HashMap<>();
    
    public CollectTierCategoriesTypes() {
        super();
    }


    @Override
    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, SAXException {
        for (int i=0; i<bt.getBody().getNumberOfTiers(); i++){
            Tier tier = bt.getBody().getTierAt(i);
            CategoryPlusType cpt = new CategoryPlusType(tier.getCategory(), tier.getType());
            categoryPlusTypeMap.put(cpt, categoryPlusTypeMap.getOrDefault(cpt, 0) + 1);
        }
    }
    
    public Map<CategoryPlusType, Integer> getCategoryPlusTypeMap(){
        return categoryPlusTypeMap;
    }
    
}
