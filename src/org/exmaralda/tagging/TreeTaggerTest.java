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

    public static String TTC = "c:\\TreeTagger";
    public static String PF = "c:\\TreeTagger\\lib\\german.par";
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown"};
    public static String INPUT="C:\\Users\\Schmidt\\Desktop\\FOLK_2_4\\BUG\\FOLK_E_00070_SE_01_T_02_DF_01.fln";
    //public static String INPUT="C:\\Users\\Schmidt\\Desktop\\HAMATAC\\David_Rufus\\MT_091209_David_s.exs";
    //public static String INPUT="S:\\TP-Z2\\IDS\\GAT\\orthonormal\\ek.fln";
    //public static String INPUT="S:\\TP-Z2\\DATEN\\MAPTASK\\0.2\\MAPTASK.coma";
    public static String OUTPUT="C:\\Users\\Schmidt\\Desktop\\FOLK_2_4\\BUG\\FOLK_E_00070_SE_01_T_02_DF_01_OUT.fln";
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
            File output = File.createTempFile("FLN","TMP");
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
