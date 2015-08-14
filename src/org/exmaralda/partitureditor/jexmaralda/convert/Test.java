/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author thomas
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            //try {
                //CHATConverter cc = new CHATConverter(new File("T:\\TP-Z2\\DATEN\\CHILDES\\MacWhinney\\boys46.cha"));
                //CHATConverter cc = new CHATConverter(new File("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\EnglishTranslator\\export\\EnglishTranslator.cha"));
                //CHATConverter cc = new CHATConverter(new File("S:\\TP-Z2\\Schulungen\\ICCA_Folkerschulung_2010\\block2.cha"));
                //BasicTranscription bt = cc.convert();
                //bt.writeXMLToFile("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\CHAT_OUT.exb", "none");
                //System.out.println(bt.getHead().toXML());
                //System.out.println(bt.getBody().toXML());
                /*System.out.println(bt.getBody().getCommonTimeline().toXML());
                for (int pos=0; pos<bt.getBody().getNumberOfTiers(); pos++){
                    System.out.println(bt.getBody().getTierAt(pos).toXML());
                }*/
                //BasicTranscription bt = new BasicTranscription("C:\\Users\\Schmidt\\Desktop\\Augsburg\\Beispiel_Augsburg.exb");
                //StylesheetFactory sf = new StylesheetFactory(true);
                //String out = sf.applyInternalStylesheetToString("/org/exmaralda/partitureditor/jexmaralda/xsl/Partitur2HTML5.xsl", bt.toXML());
                //System.out.println(out);
            //PraatConverter pc = new PraatConverter();
            //pc.readPraatFromFile("C:\\Users\\Schmidt\\Desktop\\FOLK_E_00002_SE_01_A_01_DF_01.TextGrid");
            /*} catch (JexmaraldaException ex) {
                ex.printStackTrace();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }*/
            
            
            //BasicTranscription bt = new BasicTranscription("C:\\Users\\Schmidt\\Dropbox\\IDS\\AGD\\Peters\\BEISPIEL\\ZW--_E_05206_SE_01_T_01_DF_01.exb");
            BasicTranscription bt = new BasicTranscription("C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\MoDiKo-Gesamtkorpus\\1a_07_prozesserhebung_stufenpresse\\1a_07_prozesserhebung_stufenpresse.exb");
            TEIConverter converter = new TEIConverter();
            //TCFConverter converter = new TCFConverter();
            //converter.writeNewHIATTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\HIAT_new.xml");
            //System.out.println("DONE 1!");
            //converter.writeHIATISOTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\FRENCH_MICRO.xml");
            //converter.writeHIATTCFToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\ZW_MICRO.tcf");
            converter.writeCGATMINIMALISOTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\tei_out.xml", 
                    "C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\MoDiKo-Gesamtkorpus\\cGAT_Minimal_Custom_FSM.xml");
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
