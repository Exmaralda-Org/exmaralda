/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.convert.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterEvent;
import org.exmaralda.partitureditor.jexmaralda.convert.ConverterListener;
import org.exmaralda.partitureditor.jexmaralda.convert.F4Converter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        F4Converter f4Converter = new F4Converter();
        f4Converter.readText(new File("N:\\Workspace\\IS\\ISZ_Ergaenzung_Luppi_2019\\Transkripte\\ISZ-Luppi-AGD\\f4-Transkripte\\Biran_1.txt"));
        BasicTranscription importF4 = f4Converter.importF4(true);
        importF4.getBody().getCommonTimeline().completeTimes();
        importF4.getHead().getMetaInformation().setReferencedFile("N:\\Workspace\\IS\\ISZ_Ergaenzung_Luppi_2019\\Audio\\s_2_Archiv_Format\\Biran_2019_05_22.wav");
        importF4.writeXMLToFile("N:\\Workspace\\IS\\ISZ_Ergaenzung_Luppi_2019\\Transkripte\\ISZ-Luppi-AGD\\Test.exb", "none");

        System.exit(0);
        //new Test().doit();
        StylesheetFactory sf = new StylesheetFactory(true);
        try {
            String s = sf.applyExternalStylesheetToExternalXMLFile("C:\\Users\\thomas.schmidt\\Desktop\\Terminate.xsl", "C:\\Users\\thomas.schmidt\\Desktop\\out3.xml");
        } catch (SAXException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SAXException");
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("TransformerException");
            List<String> warnings = sf.getWarnings();
            for (String w : warnings){
                System.out.println(w);
            }
        }
        try {
            
            
            /*TranscriberConverter tc = new TranscriberConverter();
            BasicTranscription bt = tc.readTranscriberFromFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\Slovene\\Gordan_NZosnmdopr-jg0909141900_s3 (2).trs");
            bt.writeXMLToFile("C:\\Users\\thomas.schmidt\\Desktop\\DEBUG\\Slovene\\Gordan_NZosnmdopr-jg0909141900_s3.exb", "none");*/
            //BasicTranscription bt = EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(new File("D:\\AGD-DATA\\dgd2_data\\transcripts\\FOLK\\FOLK_E_00069_SE_01_T_01_DF_01.fln"));
            //SubtitleConverter sc = new SubtitleConverter(bt);
            //sc.writeVTT(new File("D:\\WebApplication3\\web\\data\\FOLK_E_00069_SE_01_T_01.vtt"));
            //try {
                //CHATConverter cc = new CHATConverter(new File("F:\\Dropbox\\DEBUG\\issue99\\liean11a.1.cha"));
                //CHATConverter cc = new CHATConverter(new File("T:\\TP-Z2\\DATEN\\EXMARaLDA_DemoKorpus\\EnglishTranslator\\export\\EnglishTranslator.cha"));
                //CHATConverter cc = new CHATConverter(new File("S:\\TP-Z2\\Schulungen\\ICCA_Folkerschulung_2010\\block2.cha"));
                /*AudacityConverter cc = new AudacityConverter();
                BasicTranscription bt = cc.readAudacityFromFile(new File("C:\\Users\\Thomas_Schmidt\\Desktop\\DEBUG\\AudacityImport\\T1220ASPEAK_dellines_hs02.txt"));
                bt.writeXMLToFile("C:\\Users\\Thomas_Schmidt\\Desktop\\DEBUG\\AudacityImport\\out.exb", "none");*/
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
            //BasicTranscription bt = new BasicTranscription("C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\MoDiKo-Gesamtkorpus\\1a_07_prozesserhebung_stufenpresse\\1a_07_prozesserhebung_stufenpresse.exb");
            //TEIConverter converter = new TEIConverter();
            //TCFConverter converter = new TCFConverter();
            //converter.writeNewHIATTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\HIAT_new.xml");
            //System.out.println("DONE 1!");
            //converter.writeHIATISOTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\FRENCH_MICRO.xml");
            //converter.writeHIATTCFToFile(bt, "C:\\Users\\Schmidt\\Desktop\\TEI\\ZW_MICRO.tcf");
            //converter.writeCGATMINIMALISOTEIToFile(bt, "C:\\Users\\Schmidt\\Desktop\\tei_out.xml", 
            //        "C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\MoDiKo-Gesamtkorpus\\cGAT_Minimal_Custom_FSM.xml");
            
            /*String IN = "C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\Transana Daten für Konvertierung\\xml Dateien";
            String OUT = "C:\\Users\\Schmidt\\Dropbox\\IDS\\HZSK\\WV_MuM-Multi\\EXB";
            
            File[] xmlFiles = new File(IN).listFiles(new FilenameFilter(){

                @Override
                public boolean accept(File dir, String name) {
                    return name.toUpperCase().endsWith(".XML");
                }
                
            });
            
            TransanaConverter tc = new TransanaConverter();
            for (File f : xmlFiles){
                BasicTranscription bt = tc.readTransanaFromXMLFile(f);
                File out = new File(OUT, f.getName().replaceAll("\\.xml", ".exb"));
                bt.writeXMLToFile(out.getAbsolutePath(), "none");
                System.out.println(out.getAbsolutePath() + " written.");
            }*/
                        
            
            
            
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws IOException {
        long start = System.currentTimeMillis();
        TEIConverter converter = new TEIConverter();
        converter.addConverterListener(new ConverterListener(){
            @Override
            public void processConverterEvent(ConverterEvent converterEvent) {
                System.out.println(converterEvent.getMessage() + " / " + converterEvent.getProgress());
            }
            
        });
        int count = 0;
        File[] files = new File("D:\\AGD-DATA\\dgd2_data\\iso-transcripts\\FOLK").listFiles();
        for (File f : files){
            System.out.println("[" + count + "/" + files.length + "]");
            converter.readISOTEIFromFile(f.getAbsolutePath());
            count++;
        }
        long end = System.currentTimeMillis();
        
        System.out.println(((end - start) / 1000) + " seconds for " + count + " files.");
    }

}
