/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class ReplaceTypesInCategory extends AbstractCorpusChecker {

    
    String category;
    String type;
    Map<String, String> replacementMap;
    public int allReplacementCounts = 0;
    
    public ReplaceTypesInCategory(String c, String t, Map<String, String> rm) {
        super();
        category = c;
        type = t;
        replacementMap = rm;
    }


    @Override
    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, SAXException {
        for (int i=0; i<bt.getBody().getNumberOfTiers(); i++){
            Tier tier = bt.getBody().getTierAt(i);
            boolean isRelevantTier = (type==null || type.equals(tier.getType())) && category.equals(tier.getCategory());
            if ((!isRelevantTier)) continue;
            
            int c = 0;
            for (String sourceType : replacementMap.keySet()){
                String targetType = replacementMap.get(sourceType);
                if (!(sourceType.equals(targetType))){
                    for (int pos=0 ; pos<tier.getNumberOfEvents(); pos++){
                        Event event = tier.getEventAt(pos);
                        if (sourceType.equals(event.getDescription())){
                            event.setDescription(targetType);
                            c++;
                        }
                    }
                }
            }
            
            if (c>0){
                try {
                    bt.writeXMLToFile(currentFilename, "none");
                } catch (IOException ex) {
                    Logger.getLogger(ReplaceTypesInCategory.class.getName()).log(Level.SEVERE, null, ex);
                    throw new SAXException(ex);
                }
            }
            allReplacementCounts+=c;
            
            
            
        }
    }
    
    
}
