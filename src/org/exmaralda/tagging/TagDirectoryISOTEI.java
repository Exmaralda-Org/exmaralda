/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class TagDirectoryISOTEI {

    public static String TTC = "C:\\Users\\bernd\\Dropbox\\TreeTagger";
    public static String PF = "C:\\Users\\bernd\\Dropbox\\TreeTagger\\lib\\ParameterFile_ORIGINAL_ALL.par";
    public static String ENC = "UTF-8";
    //public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown"};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args.length==0){
            String[] customArgs = {"C:\\Users\\bernd\\switchdrive\\Datenbeispiel Thomas Schmidt\\TRANSFORMATION\\5-FINALIZED", 
                "C:\\Users\\bernd\\switchdrive\\Datenbeispiel Thomas Schmidt\\TRANSFORMATION\\6-TAGGED"};
            args = customArgs;
        }
        if ((args.length!=2) && (args.length!=7)){
            System.out.println("usage: tagDirectory inputDir ouputDir [ttDir] [ttParamFile] [applyPP] [encoding] [xpathToTokens]");
            System.out.println(" inputDir  = directory with normalised FOLKER transcriptions (*.fln)");
            System.out.println(" outputDir = directory where to write the tagged FOLKER transcriptions (*.fln)");        
            System.out.println(" ttDir     = directory with TT exe (default c:\\TreeTagger)");
            System.out.println(" ttParamFile = TT parameter file (default c:\\TreeTagger\\lib\\german-utf8.par)");
            System.out.println(" applyPP = whether (TRUE/FALSE) to apply FOLK post processing (default TRUE)");
            System.out.println(" encoding = the encoding of the parameter file (UTF-8 (default) | iso8859-1");
            System.out.println(" xpathToTokens = the xpath selecting tokens for a contribution (ALL | NO_XY (default) | NO_DUMMIES)");
            System.exit(1);
        }
        try {
            new TagDirectoryISOTEI().doit(args);
        } catch (IOException ex) {
            Logger.getLogger(TagDirectoryISOTEI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TagDirectoryISOTEI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void doit(String[] args) throws IOException, JDOMException {
        System.out.println("=================================");
        String inputDir = args[0];
        String outputDir = args[1];
        String xpathToTokens = TreeTaggableISOTEITranscription.XPATH_NO_XY;
        boolean applyPP = true;
        if (args.length>2){
            TTC = args[2];
            PF = args[3];
            applyPP = Boolean.parseBoolean(args[4]);
            ENC = args[5];
            System.out.println("TreeTagger directory set to " + TTC);
            System.out.println("TreeTagger parameter file set to " + PF);
            if (!applyPP) System.out.println("No post processing will be applied.");
            if ("ALL".equals(args[6])){
                xpathToTokens = TreeTaggableISOTEITranscription.XPATH_ALL_WORDS_AND_PUNCTUATION;
            }
            if ("NO_DUMMIES".equals(args[6])){
                xpathToTokens = TreeTaggableISOTEITranscription.XPATH_NO_DUMMIES;
            }
        }
        File in = new File(inputDir);
        File out = new File(outputDir); 
        boolean created = out.mkdir();
        if (created){
            System.out.println("Created directory " + out.getAbsolutePath() + ".");
        } else {
            File[] allFiles = out.listFiles();
            System.out.println("Deleting " + allFiles.length + " files from " + out.getAbsolutePath() + ".");
            for (File f : allFiles){
                f.delete();
            }        
        }
        TreeTagger tt = new TreeTagger(TTC, PF, ENC, OPT);        
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".xml"));
            }               
        });

        PostProcessingRules ppr = new PostProcessingRules();
        ppr.read(PostProcessingRules.FOLK_RULES);
        
        CombinedPostProcessingRules pprCombined = new CombinedPostProcessingRules();
        pprCombined.read(CombinedPostProcessingRules.FOLK_RULES);
        
        
        System.out.println("Found " + transcriptFiles.length + " ISO/TEI transcripts in " + in.getAbsolutePath() + ".");
        int count2 = 0;
        for (File transcript : transcriptFiles){
            System.out.println("=================================");
            Document trDoc = FileIO.readDocumentFromLocalFile(transcript);
            
            // get rid of all existing attributes for pos and lemma
            List l = XPath.selectNodes(trDoc, "//@lemma|//@pos|//@p-pos");
            for (Object o : l){
                Attribute a = (Attribute)o;
                a.detach();
            }
            File intermediate = File.createTempFile("ISO_TEI","TMP");
            intermediate.deleteOnExit();
            FileIO.writeDocumentToLocalFile(intermediate, trDoc);
            
            System.out.println("Tagging " + transcript.getName() + " (" + (count2+1) + " of " + transcriptFiles.length + ")");
            TreeTaggableISOTEITranscription ttont = new TreeTaggableISOTEITranscription(intermediate, true);
            ttont.setXPathToTokens(xpathToTokens);
                        
            File output = File.createTempFile("ISO_TEI","TMP");
            System.out.println(output.getAbsolutePath() + " created.");
            output.deleteOnExit();
            tt.tag(ttont, output);
            System.out.println("Tagging done");
            SextantISOTEIIntegrator soi = new SextantISOTEIIntegrator(intermediate.getAbsolutePath());
            soi.integrate(output.getAbsolutePath());
            String OUTPUT = new File(out, transcript.getName()).getAbsolutePath();
            System.out.println("Writing " + OUTPUT);
            soi.writeDocument(OUTPUT);

            
            if (applyPP){
                Document doc = FileIO.readDocumentFromLocalFile(new File(OUTPUT));
                
                // "Ordinary" Post Processing Rules : 1:1
                int count = ppr.applyISOTEI(doc);
                System.out.println("Applied 1:1 post processing rules on " + count + " elements. " );
                
                // "Combined" Post Processing Rules : 2:2
                //int countCombi = pprCombined.apply(doc);
                //System.out.println("Applied 2:2 post processing rules on " + countCombi + " elements. " );
                
                
                
                FileIO.writeDocumentToLocalFile(OUTPUT, doc);
            }
            System.out.println("=================================");
            count2++;
        }
        
        
    }
}
