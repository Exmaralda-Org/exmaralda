/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.hzsk.copilot;

/**
 *
 * @author Schmidt
 */

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class HarmonizeAnnotations extends AbstractCorpusProcessor {
    
    StringBuffer out = new StringBuffer();
    HashMap<String,String> mappings = new HashMap<String,String>();
    
    /*String[][] MAPPINGS = { {" Non-source", "Non-source"},
                            {"Non-sorce", "Non-source"},
                            {"Non-souce", "Non-source"},
                            {"Non-source", "Non-source"},
                            {"non-source", "Non-source"},
                            {"Non-sourece", "Non-source"},
                            {"Non--rendition", "Non-rendition"},
                            {"Non-redition", "Non-rendition"},
                            {"Non-rendition", "Non-rendition"},
                            {"non-rendition", "Non-rendition"},
                            {"Non-renditon", "Non-rendition"},
                            {"Rendition", "Rendition"},
                            {"Souce", "Source"},
                            {"Source", "Source"},
                            {"Sources", "Source"},
                            {"Sourhce", "Source"},
                            {"Sournce", "Source"},
                            {"Unidentifiable", "Unidentifiable"}};*/
    
    /*String[][] MAPPINGS = {{" Portuguese", "Portuguese"},
                            {"English", "English"},
                            {"Germa ", "German"},
                            {"German", "German"},
                            {"German ", "German"},
                            {"German.", "German"},
                            {"Haitian Creole", "Haitian Creole"},
                            {"mixed", "Mixed"},
                            {"Mixed", "Mixed"},
                            {"N/A", "N/A"},
                            {"Polish", "Polish"},
                            {"Portuguese", "Portuguese"},
                            {"Romanian", "Romanian"},
                            {"Russian", "Russian"},
                            {"Spanish", "Spanish"},
                            {"Spanish ", "Spanish"},
                            {"Turkish", "Turkish"},
                            {"Um", "Unidentifiable"},
                            {"Unidentifiable", "Unidentifiable"},
                            {"unidentifiable", "Unidentifiable"},
                            {"unidentifiable/non-linguistic", "unidentifiable/non-linguistic"}};*/
    
    String[][] MAPPINGS = {{" lengthened ", "lengthened"},
                            {"affirmative ", "affirmative"},
                            {"ausatmend ", "breathing out"},
                            {"chuckling", "chuckling"},
                            {"extended", "extended"},
                            {"fallende Intonation ", "falling"},
                            {"falling", "falling"},
                            {"falling ", "falling"},
                            {"falling intonation ", "falling"},
                            {"falling tone", "falling"},
                            {"falling tone ", "falling"},
                            {"faster ", "faster"},
                            {"floating ", "floating"},
                            {"gedehnt", "lengthened"},
                            {"indignant", "indignant"},
                            {"leise ", "quietly"},
                            {"lengthened", "lengthened"},
                            {"lengthened ", "lengthened"},
                            {"lengthened  ", "lentghened"},
                            {"lengthened coda", "lengthened coda"},
                            {"lengthened onset", "lengthened onset"},
                            {"lengthened, level tone", "lengthened, level tone"},
                            {"level tone", "level tone"},
                            {"level tone ", "level tone"},
                            {"more quickly", "faster"},
                            {"more quickly ", "faster"},
                            {"more quickly and loudly ", "faster and louder"},
                            {"more quietly", "more quietly"},
                            {"more quietly ", "more quietly"},
                            {"more quietly, falling tone ", "more quietly, falling tone"},
                            {"quickly", "fast"},
                            {"quickly ", "fast"},
                            {"quickly, quietly", "fast, quietly"},
                            {"quiet", "quietly"},
                            {"quiet ", "quietly"},
                            {"quietly", "quietly"},
                            {"quietly ", "quietly"},
                            {"raised tone", "rising"},
                            {"raised volume", "louder"},
                            {"raised volume, rising tone ", "louder, rising"},
                            {"raising ", "rising"},
                            {"raising tone", "rising"},
                            {"rendition of Spanish, not German", "rendition of Spanish, not German"},
                            {"rising", "rising"},
                            {"rising ", "rising"},
                            {"rising intonation ", "rising"},
                            {"rising tone", "rising"},
                            {"rising tone ", "rising"},
                            {"rising tone  ", "rising"},
                            {"schmunzelnd ", "smiling"},
                            {"schnell", "fast"},
                            {"schwebend ", "floating"},
                            {"short laughter", "short laughter"},
                            {"silently ", "quietly"},
                            {"slower ", "slower"},
                            {"slower and more clearly ", "slower and more clearly"},
                            {"slowly ", "slowly"},
                            {"smiling", "smiling"},
                            {"smiling ", "smiling"},
                            {"unidentifiable", "unidentifiable"},
                            {"very quietly", "very quietly"},
                            {"very quietly ", "very quietly"}};
    
    String TIER_CATEGORY = "sup";
    
    /** Creates a new instance of CheckAnnotations */
    public HarmonizeAnnotations() {
        for (String[] m : MAPPINGS){
            mappings.put(m[0], m[1]);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            HarmonizeAnnotations ca = new HarmonizeAnnotations();
            ca.doIt(false);
            ca.output();
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void processTranscription(BasicTranscription bt) {
        boolean changed = false;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier t = bt.getBody().getTierAt(pos);
            String cat = t.getCategory();
            if (!(this.TIER_CATEGORY.equals(cat))){
                continue;
            }
            changed = true;
            for (int i=0; i<t.getNumberOfEvents(); i++){
                Event e = t.getEventAt(i);
                String d = e.getDescription();
                if (mappings.containsKey(d)){
                    String newD = mappings.get(d);
                    e.setDescription(newD);
                    System.out.println(d + " --> " + newD);
                }
            }
        }
        if (changed){
            try {
                bt.writeXMLToFile(currentFilename, "none");
            } catch (IOException ex) {
                Logger.getLogger(HarmonizeAnnotations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    void output(){
    }
    
    public void processTranscription(SegmentedTranscription st) {
    }

    
}
