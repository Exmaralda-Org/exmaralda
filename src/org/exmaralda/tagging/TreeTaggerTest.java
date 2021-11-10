/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;

/**
 *
 * @author thomas
 */
public class TreeTaggerTest {

    // 04-11-2021, issue #286 : this one works
    public static String TTC = "D:\\Dropbox\\TreeTagger";

    // 04-11-2021, issue #286 : and this one doesn't
    //public static String TTC = "c:\\TreeTagger";
    
    //public static String PF = "c:\\TreeTagger\\lib\\german-utf8.par";
    public static String PF = "C:\\TreeTagger\\lib\\italian.par";
    
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
    //public static String INPUT="Y:\\thomas\\DR2FLK\\TagVerschiebung_shortest.xml";
    //public static String INPUT="C:\\Users\\Schmidt\\Desktop\\HAMATAC\\David_Rufus\\MT_091209_David_s.exs";
    //public static String INPUT="N:\\Workspace\\DH\\Lesetext1\\04-LT1_Fln\\DH--_E_00001_SE_01_T_01_DF_01.fln";
    public static String INPUT = "M:\\GeWiss\\1-NON-DE\\FLN\\ita\\GWSS_E_03017_SE_01_T_01_DF_01.fln";
    //public static String INPUT="S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\MAPTASK.coma";
    public static String OUTPUT="M:\\GeWiss\\1-NON-DE\\Debug_ITA_POS.xml";
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
            //ttont.setXPathToTokens("//w|//p[not(text()=',')]");
            //ttont.clearTagging();
            ttont.setVerbose(true);
            File output = File.createTempFile("FLN",".xml");
            //output.deleteOnExit();
            System.out.println("***" + output.getAbsolutePath());
            tt.tag(ttont, output);
            SextantOrthonormalIntegrator soi = new SextantOrthonormalIntegrator(INPUT);
            //SextantIntegrator soi = new SextantIntegrator(INPUT);
            soi.integrate(output.getAbsolutePath());
            soi.writeDocument(OUTPUT);
            
            
            
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
