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
public class TagDirectory {

    public static String TTC = "c:\\TreeTagger";
    public static String PF = "c:\\TreeTagger\\lib\\german-utf8.par";
    public static String ENC = "UTF-8";
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ((args.length!=2) && (args.length!=6)){
            System.out.println("usage: tagDirectory inputDir ouputDir [ttDir] [ttParamFile] [applyPP] [encoding]");
            System.out.println(" inputDir  = directory with normalised FOLKER transcriptions (*.fln)");
            System.out.println(" outputDir = directory where to write the tagged FOLKER transcriptions (*.fln)");        
            System.out.println(" ttDir     = directory with TT exe (default c:\\TreeTagger)");
            System.out.println(" ttParamFile = TT parameter file (default c:\\TreeTagger\\lib\\german-utf8.par)");
            System.out.println(" applyPP = whether (TRUE/FALSE) to apply FOLK post processing (default TRUE)");
            System.out.println(" encoding = the encoding of the parameter file (default: UTF-8 / iso8859-1");
            System.exit(1);
        }
        try {
            new TagDirectory().doit(args);
        } catch (IOException ex) {
            Logger.getLogger(TagDirectory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(TagDirectory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void doit(String[] args) throws IOException, JDOMException {
        System.out.println("=================================");
        String inputDir = args[0];
        String outputDir = args[1];
        boolean applyPP = true;
        if (args.length>2){
            TTC = args[2];
            PF = args[3];
            applyPP = Boolean.parseBoolean(args[4]);
            ENC = args[5];
            System.out.println("TreeTagger directory set to " + TTC);
            System.out.println("TreeTagger parameter file set to " + PF);
            if (!applyPP) System.out.println("No post processing will be applied.");
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
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });

        PostProcessingRules ppr = new PostProcessingRules();
        ppr.read(PostProcessingRules.FOLK_RULES);
        
        
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts in " + in.getAbsolutePath() + ".");
        int count2 = 0;
        for (File transcript : transcriptFiles){
            System.out.println("=================================");
            Document trDoc = FileIO.readDocumentFromLocalFile(transcript);
            List l = XPath.selectNodes(trDoc, "//@lemma|//@pos|//@p-pos");
            for (Object o : l){
                Attribute a = (Attribute)o;
                a.detach();
            }
            File intermediate = File.createTempFile("FLN","TMP");
            intermediate.deleteOnExit();
            FileIO.writeDocumentToLocalFile(intermediate, trDoc);
            
            System.out.println("Tagging " + transcript.getName() + " (" + (count2+1) + " of " + transcriptFiles.length + ")");
            TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(intermediate, true);
            File output = File.createTempFile("FLN","TMP");
            output.deleteOnExit();
            tt.tag(ttont, output);
            SextantOrthonormalIntegrator soi = new SextantOrthonormalIntegrator(intermediate.getAbsolutePath());
            soi.integrate(output.getAbsolutePath());
            String OUTPUT = new File(out, transcript.getName()).getAbsolutePath();
            System.out.println("Writing " + OUTPUT);
            soi.writeDocument(OUTPUT);

            if (applyPP){
                Document doc = FileIO.readDocumentFromLocalFile(new File(OUTPUT));
                int count = ppr.apply(doc);
                System.out.println("Applied " + count + " post processing rules");
                FileIO.writeDocumentToLocalFile(OUTPUT, doc);
            }
            System.out.println("=================================");
            count2++;
        }
        
        
    }
}
