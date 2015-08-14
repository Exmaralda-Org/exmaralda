/*
 * XCHATTest.java
 *
 * Created on 16. Juli 2003, 12:59
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class XCHATTest {
    
    /*public static String[] FILENAMES = {"christiane","andreas1","andreas2","andreas3","andreas4",
                                 "andreas5","andreas6","andreas7","andreas8",
                                 "carsten","frederik","gabi",
                                 "kai1","kai2","katrin","markus","nicole",
                                 "regina1","regina2","roman","teresa"};*/
    
   public static String[] FILENAMES = { "arali-4t","arali-5t","arali-6t","arali-7t","arfun-4t","arfun-5t","arfun-6t",
                                        "arfun-7t","argul-10t","argul-8t","argul-9t","arkad-4t","arkad-5t","arkad-6t",
                                        "arkad-7t","arsez-4t","arsez-5t","arsez-6t","arsez-7t","arsib-10t","arsib-4t",
                                        "arsib-5t","arsib-6t","arsib-7t","arsib-8t","arsib-9t","dhabb-4t","dhabb-5t",
                                        "dhabb-6t","dhabb-7t","dhcem-10t","dhcem-8t","dhcem-9t","dhcig-10t","dhcig-8t",
                                        "dhcig-9t","dhera-10t","dhera-8t","dhera-9t","dhlev-4t","dhlev-5t","dhlev-6t",
                                        "dhlev-7t","dhsal-4t","dhsal-5t","dhsal-6t","dhsal-7t","dhser-4t","dhser-5t",
                                        "dhser-6t","dhser-7t","dhsev-4t","dhsev-5t","dhsev-6t","dhsev-7t","dhsuk-4t",
                                        "dhsuk-5t","dhsuk-6t","dhsuk-7t","dhumi-10t","dhumi-4t","dhumi-5t","dhumi-6t",
                                        "dhumi-7t","dhumi-8t","dhumi-9t","itade-4t","itade-5t","itade-6t","itade-7t",
                                        "itemi-10t","itemi-8t","itemi-9t","itgul-10t","itgul-8t","itgul-9t","itram-10t",
                                        "itram-8t","itram-9t","itsel-4t","itsel-5t","itsel-6t","itsel-7t","itser-4t",
                                        "itser-5t","itser-6t","itser-7t","ityav-4t","ityav-5t","ityav-6t","ityav-7t",
                                        "jhcig-4t","jhcig-5t","jhcig-6t","jhcig-7t","jhfat-10t","jhfat-8t","jhfat-9t",
                                        "jhfig-4t","jhfig-5t","jhfig-6t","jhfig-7t","jhgok-10t","jhgok-8t","jhgok-9t",
                                        "jhozl-10t","jhozl-8t","jhozl-9t","jhsez-4t","jhsez-5t","jhsez-6t","jhsez-7t",
                                        "jhufu-10t","jhufu-8t","jhufu-9t","kaali-10t","kaali-8t","kaali-9t","kaalk-10t",
                                        "kaalk-8t","kaalk-9t","kabes-4t","kabes-5t","kabes-6t","kabes-7t","kabet-10t",
                                        "kabet-4t","kabet-5t","kabet-6t","kabet-7t","kabet-8t","kabet-9t","kafer-10t",
                                        "kafer-8t","kafer-9t","kahas-10t","kahas-8t","kahas-9t","kaism-4t","kaism-5t",
                                        "kaism-6t","kaism-7t","kamur-4t","kamur-5t","kamur-6t","kamur-7t","kayur-10t",
                                        "kayur-8t","kayur-9t","kocey-10t","kocey-8t","kocey-9t","koeyu-10t","koeyu-8t",
                                        "koeyu-9t","kohan-10t","kohan-8t","kohan-9t","koism-10t","koism-8t","koism-9t",
                                        "komah-4t","komah-5t","komah-6t","komah-7t","komuh-10t","komuh-8t","komuh-9t",
                                        "korab-4t","korab-5t","korab-6t","korab-7t","kozuh-10t","kozuh-8t","kozuh-9t"};
                                        
   /** Creates a new instance of XCHATTest */
    public XCHATTest() {
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       XCHATConverter xcc = new XCHATConverter();
       for (int pos=0; pos<FILENAMES.length; pos++){
           try{
                 String xchatFileName = "D:\\Aarsenbos_Corpus\\aarsenbos-xml\\turkbilingturk\\" + FILENAMES[pos] + ".xml";
                 System.out.println("Processing " + xchatFileName);
                 org.exmaralda.partitureditor.jexmaralda.BasicTranscription bt = xcc.readXCHATFromFile(xchatFileName);
                 reorderTiers(bt);
                 removeEmptyTiers(bt);
                 makeFormatTable(bt, FILENAMES[pos]);
                 String exmFileName = "D:\\Aarsenbos_Corpus\\EXMARaLDA_BasicTranscription\\" + FILENAMES[pos] + "_bas.xml";
                 bt.writeXMLToFile(exmFileName,"none");
           } catch (Throwable t){
               t.printStackTrace();
           }
       }
    }
    
    public static void reorderTiers(BasicTranscription bt) throws JexmaraldaException {
        int numberOfSpeakers = bt.getHead().getSpeakertable().getNumberOfSpeakers();
        String[] tierIDs = new String[numberOfSpeakers*3];
        int speakerPos=0;
        for (int pos=0; pos<numberOfSpeakers*3; pos+=3){
            String speakerID = bt.getHead().getSpeakertable().getSpeakerAt(speakerPos).getID();
            tierIDs[pos]="TIE_" + speakerID + "_V";
            tierIDs[pos+1]="TIE_" + speakerID + "_PHO";
            tierIDs[pos+2]="TIE_" + speakerID + "_SIT";            
            speakerPos++;
        }
        bt.getBody().reorderTiers(tierIDs);
    }
    
    public static void removeEmptyTiers(BasicTranscription bt)  {
        for (int pos=0; pos<bt.getBody().size(); pos++){
            if (bt.getBody().getTierAt(pos).getNumberOfEvents()==0){
                if (!bt.getBody().getTierAt(pos).getType().equals("t")){
                    System.out.println("Removing " + bt.getBody().getTierAt(pos).getDescription(bt.getHead().getSpeakertable()));
                    bt.getBody().removeTierAt(pos);                
                }
            }
        }
    }
    
    public static void makeFormatTable(BasicTranscription bt, String filename)throws JexmaraldaException, SAXException, java.io.IOException{
        TierFormatTable tft = new TierFormatTable(bt);
        for (int pos=0; pos<bt.getBody().size(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            TierFormat tf = tft.getTierFormatForTier(tier.getID());
            if (tier.getType().equals("t")){
                tf.setProperty("font:name","Times New Roman");                   
                tf.setProperty("font:size", "12"); 
                if (tier.getSpeaker().equals("CHI")){
                    tf.setProperty("font:face","Bold");                  
                }                
            } else if (tier.getCategory().equals("pho")){
                tf.setProperty("font:size", "8"); 
                tf.setProperty("font:color", "blue"); 
            } else if (tier.getCategory().equals("sit")){
                tf.setProperty("font:size", "6"); 
                tf.setProperty("font:color", "darkGray"); 
            }                                            
        }
        TierFormat rf = tft.getTierFormatForTier("ROW-LABEL");
        rf.setProperty("bg:color","white");
        TierFormat cf = tft.getTierFormatForTier("COLUMN-LABEL");
        cf.setProperty("bg:color","white");        
        cf.setProperty("font:size","5");        
        String tftFileName = "D:\\Aarsenbos_Corpus\\EXMARaLDA_FormatTables\\" + filename + "_f.xml";
        tft.writeXMLToFile(tftFileName, "none");
    }
}
