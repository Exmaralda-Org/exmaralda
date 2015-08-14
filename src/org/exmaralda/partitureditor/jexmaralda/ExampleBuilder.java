/*
 * ExampleBuilder.java
 *
 * Created on 20. Februar 2004, 10:23
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  thomas
 */
public class ExampleBuilder {
    
    /** Creates a new instance of ExampleBuilder */
    public ExampleBuilder() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        org.exmaralda.partitureditor.interlinearText.HTMLParameters HTMLparams = new org.exmaralda.partitureditor.interlinearText.HTMLParameters();
        HTMLparams.includeSyncPoints = false;
        int excep = 0;
        int ok = 0;
        for (int pos=0; pos<ALL_FILES.length; pos++){
            try{
                String completePath = "i:\\exmaralda\\HIAT_Exmaralda\\Beispiele_Backup161203\\Beispiele\\" + ALL_FILES[pos];
                System.out.println("Reading " + completePath);
                BasicTranscription bt = new BasicTranscription(completePath);
                org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();
                String formatTableString = sf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/FormatTable4BasicTranscription.xsl", 
                                                                              bt.toXML());
                TierFormatTable tft = new TierFormatTable();
                tft.TierFormatTableFromString(formatTableString);  
                org.exmaralda.partitureditor.interlinearText.InterlinearText it =
                    org.exmaralda.partitureditor.jexmaralda.convert.ItConverter.BasicTranscriptionToInterlinearText(bt, tft);
                System.out.println("Transcript converted to interlinear text.");
                String output = completePath.substring(0, completePath.length()-4) + ".html";
                it.writeHTMLToFile(output, HTMLparams);                
                ok++;
            } catch (Exception e){
                e.printStackTrace();
                excep++;
            }            
        }
        //System.out.println(ok + " OK.");
      
        //System.out.println(excep + " Scheisse.");
    }
    
    static String[] ALL_FILES =     {"Kapitel2\\2_1_Beispiel1.xml","Kapitel2\\2_1_Beispiel2.xml","Kapitel2\\2_1_Beispiel3.xml",
                                    "Kapitel2\\2_1_Beispiel4.xml","Kapitel2\\2_1_Beispiel5.xml","Kapitel2\\2_1_Beispiel6.xml",
                                    "Kapitel2\\2_1_Beispiel7_HFU.xml","Kapitel2\\2_1_Beispiel8.xml","Kapitel3\\3_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_1\\4_1_Beispiel1.xml","Kapitel4\\Kapitel4_2\\4_2_1_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_1_Beispiel2.xml","Kapitel4\\Kapitel4_2\\4_2_1_Beispiel3.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_2_Beispiel1.xml","Kapitel4\\Kapitel4_2\\4_2_2_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_3_Beispiel1.xml","Kapitel4\\Kapitel4_2\\4_2_3_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_4_Beispiel1.xml","Kapitel4\\Kapitel4_2\\4_2_4_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_4_Beispiel3.xml","Kapitel4\\Kapitel4_2\\4_2_4_Beispiel4.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_4_Beispiel5.xml","Kapitel4\\Kapitel4_2\\4_2_4_Beispiel6.xml",
                                    "Kapitel4\\Kapitel4_2\\4_2_5_Beispiel1.xml","Kapitel4\\Kapitel4_2\\4_2_6_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_3\\4_3_Beispiel0.xml","Kapitel4\\Kapitel4_3\\4_3_Beispiel0_HFU.xml",
                                    "Kapitel4\\Kapitel4_3\\4_3_Beispiel1.xml","Kapitel4\\Kapitel4_3\\4_3_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_3\\4_3_Beispiel3.xml","Kapitel4\\Kapitel4_4\\4_4_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_4\\4_4_Beispiel1_HFU.xml","Kapitel4\\Kapitel4_4\\4_4_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_4\\4_4_Beispiel2_HFU.xml","Kapitel4\\Kapitel4_4\\4_4_Beispiel3.xml",
                                    "Kapitel4\\Kapitel4_4\\4_4_Beispiel3a_HFU.xml","Kapitel4\\Kapitel4_4\\4_4_Beispiel3b_HFU.xml",
                                    "Kapitel4\\Kapitel4_4\\4_4_Beispiel4.xml","Kapitel4\\Kapitel4_4\\4_4_Beispiel4_HFU.xml",
                                    "Kapitel4\\Kapitel4_4\\4_4_Beispiel5_HFU.xml","Kapitel4\\Kapitel4_5\\4_5_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_5\\4_5_Beispiel2.xml","Kapitel4\\Kapitel4_5\\4_5_Beispiel3.xml",
                                    "Kapitel4\\Kapitel4_5\\4_5_Beispiel36_neu.xml","Kapitel4\\Kapitel4_5\\4_5_Beispiel39_neu.xml",
                                    "Kapitel4\\Kapitel4_5\\4_5_Beispiel3a.xml","Kapitel4\\Kapitel4_5\\4_5_Beispiel4.xml",
                                    "Kapitel4\\Kapitel4_5\\4_5_Beispiel5.xml","Kapitel4\\Kapitel4_6\\4_6_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_6\\4_6_Beispiel1_HFU.xml","Kapitel4\\Kapitel4_6\\4_6_Beispiel2.xml",
                                    "Kapitel4\\Kapitel4_6\\4_6_Beispiel3.xml","Kapitel4\\Kapitel4_6\\4_6_Beispiel3_HFU.xml",
                                    "Kapitel4\\Kapitel4_6\\4_6_Beispiel4.xml","Kapitel4\\Kapitel4_6\\4_6_Beispiel4_HFU.xml",
                                    "Kapitel4\\Kapitel4_6\\4_6_Beispiel5.xml","Kapitel4\\Kapitel4_6\\4_6_Beispiel6.xml",
                                    "Kapitel4\\Kapitel4_6\\4_6_Beispiel6_RV.xml","Kapitel4\\Kapitel4_7\\4_7_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_7\\4_7_Beispiel2.xml","Kapitel4\\Kapitel4_8\\4_8_Beispiel1.xml",
                                    "Kapitel4\\Kapitel4_8\\4_8_Beispiel2.xml","Kapitel4\\Kapitel4_8\\4_8_Beispiel3.xml",
                                    "Kapitel5\\Kapitel5_1\\5_1_Beispiel1.xml","Kapitel5\\Kapitel5_1\\5_1_Beispiel2.xml",
                                    "Kapitel5\\Kapitel5_1\\5_1_Beispiel3.xml","Kapitel5\\Kapitel5_1\\5_1_Beispiel4.xml",
                                    "Kapitel5\\Kapitel5_1\\5_1_Beispiel4_neu.xml","Kapitel5\\Kapitel5_10\\5_10_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_10\\5_10_Beispiel2.xml","Kapitel5\\Kapitel5_11\\5_11_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_11\\5_11_Beispiel2.xml","Kapitel5\\Kapitel5_2\\5_2_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_2\\5_2_Beispiel2.xml","Kapitel5\\Kapitel5_3\\5_3_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_3\\5_3_Beispiel2.xml","Kapitel5\\Kapitel5_4\\5_4_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_4\\5_4_Beispiel1_RV.xml","Kapitel5\\Kapitel5_4\\5_4_Beispiel1a_HF.xml",
                                    "Kapitel5\\Kapitel5_4\\5_4_Beispiel2.xml","Kapitel5\\Kapitel5_4\\5_4_Beispiel2_neu.xml",
                                    "Kapitel5\\Kapitel5_5\\5_5_Beispiel1.xml","Kapitel5\\Kapitel5_5\\5_5_Beispiel2.xml",
                                    "Kapitel5\\Kapitel5_6\\5_6_Beispiel1.xml","Kapitel5\\Kapitel5_7\\5_7_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_8\\5_8_Beispiel1.xml","Kapitel5\\Kapitel5_9\\5_9_Beispiel1.xml",
                                    "Kapitel5\\Kapitel5_9\\5_9_Beispiel2.xml","Kapitel5\\Kapitel5_9\\5_9_Beispiel3.xml",
                                    "Kapitel7\\7_Beispiel1.xml","Kapitel7\\7_Beispiel2.xml","Kapitel7\\7_Beispiel3.xml",
                                    "Kapitel7\\7_Beispiel4.xml"};
    
}
