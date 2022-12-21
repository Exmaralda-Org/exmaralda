/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;

/**
 *
 * @author thomas
 */
public class TreeTaggerTest {

    // 04-11-2021, issue #286 : this one works
    public static String TTC = "C:\\Users\\bernd\\Dropbox\\TreeTagger";
    //public static String TTC = "C:\\TreeTagger";
    

    
    
    // 04-11-2021, issue #286 : and this one doesn't
    //public static String TTC = "c:\\TreeTagger"; 
    
    public static String PF = "C:\\Users\\bernd\\Dropbox\\TreeTagger\\lib\\ParameterFile_ORIGINAL_ALL.par";
    //public static String PF = "C:\\Users\\bernd\\Dropbox\\TreeTagger\\lib\\english-bnc.par";
    //public static String PF = "/Volumes/DATA/Dropbox/TreeTagger/english.par";
    
    //public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown"};
    //public static String INPUT="Y:\\thomas\\DR2FLK\\TagVerschiebung_shortest.xml";
    //public static String INPUT="C:\\Users\\Schmidt\\Desktop\\HAMATAC\\David_Rufus\\MT_091209_David_s.exs";
    //public static String INPUT="N:\\Workspace\\DH\\Lesetext1\\04-LT1_Fln\\DH--_E_00001_SE_01_T_01_DF_01.fln";
    //public static String INPUT = "/Volumes/DATA/FOLK/00194_Jour_Fixe/FOLK_E_00194_SE_01_T_01.fln";
    public static String INPUT = "C:\\Users\\bernd\\Dropbox\\work\\2021_MARGO_TEXAS_GERMAN\\AUTO_NORMALIZE_RESULTS\\DEFAULT\\1-35-1-20-a.exb"; 
    //public static String INPUT="S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\MAPTASK.coma";
    public static String OUTPUT="C:\\Users\\bernd\\Dropbox\\work\\2021_MARGO_TEXAS_GERMAN\\AUTO_NORMALIZE_RESULTS\\TAGGED\\1-35-1-20-a.exb"; 
    //public static String OUTPUT="C:\\Users\\Schmidt\\Desktop\\HAMATAC\\David_Rufus\\MT_091209_David_s_out.exs";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            TreeTagger tt = new TreeTagger(TTC, PF, "", OPT);
            //TreeTaggableSegmentedTranscription ttont = new TreeTaggableSegmentedTranscription(new File(INPUT), 
            //        TaggingProfiles.HIAT_WORDS_PUNCTUATION_SEGMENTATION_XPATH,
            //        TaggingProfiles.HIAT_WORDS_PUNCTUATION_TOKEN_XPATH);
            TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(new File(INPUT), true);
            
            BasicTranscription bt = new BasicTranscription(INPUT);
            TreeTaggableBasicTranscription ttbt = new TreeTaggableBasicTranscription(bt, "norm");
            BasicTranscriptionTokenHandler btth = new BasicTranscriptionTokenHandler(bt, ttbt.ids, "lemma", "pos", "p");
            tt.tag(ttbt, null, btth);
            bt.writeXMLToFile(OUTPUT, "none");
            //ttont.setXPathToTokens("//w|//p[not(text()=',')]");
            //ttont.clearTagging();
            ttont.setVerbose(true);
            //File output = File.createTempFile("EXB",".xml");
            //output.deleteOnExit();
            //System.out.println("***" + output.getAbsolutePath());
            //tt.tag(ttont, output);
            //SextantOrthonormalIntegrator soi = new SextantOrthonormalIntegrator(INPUT);
            //SextantIntegrator soi = new SextantIntegrator(INPUT);
            //soi.integrate(output.getAbsolutePath());
           //soi.writeDocument(OUTPUT);
            
            
            
            /*CorpusTreeTagger ctt = new CorpusTreeTagger(new File(INPUT), tt,
                    TaggingProfiles.HIAT_WORDS_PUNCTUATION_SEGMENTATION_XPATH,
                    TaggingProfiles.HIAT_WORDS_PUNCTUATION_TOKEN_XPATH,
                    true,
                    true,
                    "_POS");
            ctt.tagCorpus();
            System.out.println("Done");*/
            System.exit(0);
            /*String segmentationXPath = "//segmentation[@name='SpeakerContribution_Utterance_Word']/ts";
            String tokenXPath = "descendant::*[not(self::ats) and not(descendant::*) and not(text()='(') and not(text()=')') and not(text()='/') and string-length(normalize-space(text()))>0]";
            TreeTagger tt = new TreeTagger(TTC, PF, OPT);
            TreeTaggableSegmentedTranscription ttst = new TreeTaggableSegmentedTranscription(new File("S:\\TP-Z2\\TAGGING\\SegSexTest.exs"), segmentationXPath, tokenXPath);
            tt.tag(ttst, new File("S:\\TP-Z2\\TAGGING\\SegSexTest.esa"));*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
