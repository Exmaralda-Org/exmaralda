/*
 * JosCorpusTest.java
 *
 * Created on 29. Juli 2003, 09:25
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;

/**
 *
 * @author  thomas
 */
public class JosCorpusTest {
    
    public static final String[] FILENAMES = {"jos20007","jos20021","jos20112","jos20126",
                                 "jos20208","jos20222","jos20328","jos20411",
                                 "jos20425","jos20511","jos20601","jos20622",
                                 "jos20706","jos20720","jos20804","jos20818",
                                 "jos20902","jos20916","jos21109","jos21123",
                                 "jos30006","jos30020","jos30110","jos30124",
                                 "jos30215","jos30229","jos30327","jos30417"};

    
    static final String STYLESHEET_NAME = "/org/exmaralda/partitureditor/jexmaralda/xsl/SimpleCHAT2BasicTranscription.xsl";
    /** Creates a new instance of JosCorpusTest */
    public JosCorpusTest() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       StylesheetFactory ssf = new StylesheetFactory();
       for (int pos=0; pos<FILENAMES.length; pos++){
           try{
                 String xchatFileName = "D:\\Jos_Corpus\\jos-xml\\" + FILENAMES[pos] + ".xml";
                 String bt2String = ssf.applyInternalStylesheetToExternalXMLFile(STYLESHEET_NAME, xchatFileName);
                 BasicTranscription bt2 = new BasicTranscription();
                 bt2.BasicTranscriptionFromString(bt2String);
                 
                 String simpleExFileName = "D:\\Jos_Corpus\\SimpleExmaralda\\" + FILENAMES[pos] + ".txt";
                 System.out.println("Processing " + simpleExFileName);
                 SimpleExmaraldaReader ser = new SimpleExmaraldaReader(simpleExFileName);
                 BasicTranscription bt = ser.parseBasicTranscription();
                 bt.setHead(bt2.getHead());
                 reorderTiers(bt);
                 String exmFileName = "D:\\Jos_Corpus\\EXMARaLDA_BasicTranscription\\" + FILENAMES[pos] + "_bas.xml";
                 bt.writeXMLToFile(exmFileName,"none");
                 makeFormatTable(bt, FILENAMES[pos]);                 
           } catch (Throwable t){
               t.printStackTrace();
           }
       }
    }
    
    public static void makeFormatTable(BasicTranscription bt, String filename)throws JexmaraldaException, SAXException, java.io.IOException{
        TierFormatTable tft = new TierFormatTable(bt);
        for (int pos=0; pos<bt.getBody().size(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            TierFormat tf = tft.getTierFormatForTier(tier.getID());
            if (tier.getType().equals("t")){
                tf.setProperty("font-name","Times New Roman");                   
                tf.setProperty("font-size", "12"); 
                if (tier.getSpeaker().equals("CHI")){
                    tf.setProperty("font-face","Bold");                  
                }                
            } else if (tier.getCategory().equals("pho")){
                tf.setProperty("font-size", "8"); 
                tf.setProperty("font-color", "blue"); 
            } else if (tier.getCategory().equals("sit")){
                tf.setProperty("font-size", "6"); 
                tf.setProperty("font-color", "darkGray"); 
            }                                            
        }
        TierFormat rf = tft.getTierFormatForTier("ROW-LABEL");
        rf.setProperty("bg-color","white");
        TierFormat cf = tft.getTierFormatForTier("COLUMN-LABEL");
        cf.setProperty("bg-color","white");        
        cf.setProperty("font-size","5");        
        String tftFileName = "D:\\Jos_Corpus\\EXMARaLDA_FormatTables\\" + filename + "_f.xml";
        tft.writeXMLToFile(tftFileName, "none");
    }    
    
    public static void reorderTiers(BasicTranscription bt) throws JexmaraldaException {
        String[] tierIDs = new String[bt.getBody().getNumberOfTiers()];
        int count = 0;
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            if (tier.getSpeaker().equals("CHI") && tier.getCategory().equals("v")){
                tierIDs[count] = tier.getID();
                count++;
                break;
            }
        }
        for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
            Tier tier = bt.getBody().getTierAt(pos);
            if (tier.getSpeaker().equals("CHI") && tier.getCategory().equals("a")){
                tierIDs[count] = tier.getID();
                count++;
                break;
            }
        }
        String[] speakerIDs = bt.getHead().getSpeakertable().getAllSpeakerIDs();
        for (int speakPos = 0; speakPos < speakerIDs.length; speakPos++){
            if (speakerIDs[speakPos].equals("CHI")) continue;
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                Tier tier = bt.getBody().getTierAt(pos);
                if (tier.getSpeaker().equals(speakerIDs[speakPos]) && tier.getCategory().equals("v")){
                    tierIDs[count] = tier.getID();
                    count++;
                    break;
                }
            }
            for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                Tier tier = bt.getBody().getTierAt(pos);
                if (tier.getSpeaker().equals(speakerIDs[speakPos]) && tier.getCategory().equals("a")){
                    tierIDs[count] = tier.getID();
                    count++;
                    break;
                }
            }
            
        }
        bt.getBody().reorderTiers(tierIDs);        
    }
    
}
