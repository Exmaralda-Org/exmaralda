/*
 * TestSearch.java
 *
 * Created on 17. Juni 2004, 09:50
 */

package org.exmaralda.zecke;

import java.io.*;
/**
 *
 * @author  thomas
 */
public class TestSearch {
    
/*        static String[] NAMES = {
            "10\\P-10",
            "11\\P-11",
            "12\\P-12",
            "14\\T-AUF-14",
            "37_1\\PSD-37-1",
            "38\\P-38",
            "39\\PSD-39",
            "43\\T-AUF-43",
            "45\\T-AUF-45",
            "5\\T-AUF-5",
            "52\\D-AUF-52",
            "56\\D-AUF-56",
            "8\\T-AUF-8"};
            
         static String PATH = "D:\\AAA_Corpus\\K2\\0.1";*/
    
    public static String[] NAMES =
        {"EFE04dt_Alt_0352a_f_ENF_010992\\EFE04dt_Alt_0352a_1_ENF",
        "EFE04dt_Bin_0722_f_SKO_100203\\EFE04dt_Bin_0722_SKO",
        "EFE04dt_Gok_0321a_f_ENF_191292\\EFE04dt_Gok_0321a_1_ENF",
        "EFE04dt_Gok_0321a_f_ENF_191292\\EFE04dt_Gok_0321a_2_ENF",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_1_SKO",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_2_SKO",
        "EFE04dt_Kub_0717_f_SKO_190501\\EFE04dt_Kub_0717_3_SKO",
        "EFE04dt_Nad_0469_f_ENF_140796\\EFE04dt_Nad_0469_1_ENF",
        "EFE04dt_Nad_0469_f_ENF_140796\\EFE04dt_Nad_0469_2_ENF",
        "EFE04dt_Per_0272a_f_ENF_160892\\EFE04dt_Per_0272a_1_ENF",
        "EFE04dt_Seh_0328a_f_ENF_131192\\EFE04dt_Sem_0328a_1_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_1_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_2_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_3_ENF",
        "EFE04dt_Sey_0184b_f_ENF_260492\\EFE04dt_Sey_0184b_4_ENF",
        "EFE04dt_Zer_0068_f_ENF_020792\\EFE04dt_Zer_0068_1_ENF",
        "EFE04dt_Zer_0068_f_ENF_020792\\EFE04dt_Zer_0068_2_ENF",
        "EFE04tk_Alt_0352a_f_ENF_010992\\EFE04tk_Alt_0352a_1_ENF",
        "EFE04tk_Alt_0352a_f_ENF_010992\\EFE04tk_Alt_0352a_2_ENF",
        "EFE04tk_Azi_0305a_f_ENF_010593\\EFE04tk_Azi_0305a_1_ENF",
        "EFE04tk_Can_0338a_f_ENF_290393\\EFE04tk_Can_0338a_1_ENF",
        "EFE04tk_Eme_0826_f_SKO_250801\\EFE04tk_Eme_0826_1_SKO",
        "EFE04tk_Eme_0826_f_SKO_250801\\EFE04tk_Eme_0826_2_SKO",
        "EFE04tk_Gok_0321_f_ENF_191292\\EFE04tk_Gok_0321_1_ENF",
        "EFE04tk_Gok_0321_f_ENF_191292\\EFE04tk_Gok_0321_2_ENF",
        "EFE04tk_Gok_0321_f_ENF_191292\\EFE04tk_Gok_0321_3_ENF",
        "EFE04tk_Kub_0736_f_SKO_190501\\EFE04tk_Kub_0736_1_SKO",
        "EFE04tk_Per_0272a_f_ENF_160892\\EFE04tk_Per_0272a_1_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_1_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_2_ENF",
        "EFE04tk_Seh_0328a_f_ENF_131192\\EFE04tk_Seh_0328a_3_ENF",
        "EFE04tk_Zer_0068a_f_ENF_020792\\EFE04tk_Zer_0068a_1_ENF",
        "EFE04tk_Zer_0068a_f_ENF_020792\\EFE04tk_Zer_0068a_2_ENF"};
        
     public static String PATH = "D:\\AAA_Corpus\\E5\\0.1";

    /** Creates a new instance of TestSearch */
    public TestSearch() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String[] corpusFiles = new String[NAMES.length];
        for (int pos=0; pos<NAMES.length; pos++){
            String corpusFile = PATH + "\\" + NAMES[pos] + "_s.xml";
            corpusFiles[pos] = corpusFile;
        }
        try {
            CorpusTree ct = new CorpusTree("Corpus", corpusFiles);
            //System.out.println(ct.toXML());
            SimpleTranscriptionSearch search = new SimpleTranscriptionSearch(ct);
            AbstractSearchResult result = search.search(AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE, "[Ii]ste");
            //TranscriptionSearchResult castResult = (TranscriptionSearchResult)result;
            
            System.out.println("====================================");
            System.out.println("It took me " + Double.toString(search.getTimeForLastSearch()) + " seconds ");
            System.out.println("to find " + result.size() + " results. ");
            System.out.println("====================================");
            
            System.out.println("started writing document...");
            String outputfilepath="d:\\searchresult.xml";                        
            FileOutputStream fos = new FileOutputStream(new File(outputfilepath));
            fos.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes("UTF-8"));
            fos.write("<?xml-stylesheet type=\"text/xsl\" href=\"D:\\SR2HTML.xsl\"?>".getBytes("UTF-8"));
            fos.write("<result>".getBytes("UTF-8"));
            for (int pos=0; pos<result.size(); pos++){
                XMLable item = (XMLable)(result.elementAt(pos));
                fos.write(item.toXML().getBytes("UTF-8"));
            }
            fos.write("</result>".getBytes("UTF-8"));
            
            // fos.write(result.toXML().getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");                        
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
}
