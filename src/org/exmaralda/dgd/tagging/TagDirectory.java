/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.tagging;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.tagging.PostProcessingRules;
import org.exmaralda.tagging.SextantOrthonormalIntegrator;
import org.exmaralda.tagging.TreeTaggableOrthonormalTranscription;
import org.exmaralda.tagging.TreeTagger;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class TagDirectory {

    public static String TTC = "c:\\TreeTagger";
    public static String PF = "c:\\TreeTagger\\lib\\german-utf8.par";
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ((args.length!=2) && (args.length!=3)){
            System.out.println("usage: tagDirectory inputDirectory ouputDirectory [auxiliaryLexicon]");
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
        String inputDir = args[0];
        String outputDir = args[1];
        if (args.length>2){
            OPT[OPT.length-2] = "-lex";
            OPT[OPT.length-1] = args[2];
        }
        File in = new File(inputDir);
        File out = new File(outputDir); 
        out.mkdir();
        for (File f : out.listFiles()){
            f.delete();
        }        
        TreeTagger tt = new TreeTagger(TTC, PF, "UTF-8", OPT);        
        File[] transcriptFiles = in.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(".fln"));
            }               
        });

        PostProcessingRules ppr = new PostProcessingRules();
        ppr.read(PostProcessingRules.FOLK_RULES);
        
        
        System.out.println("Found " + transcriptFiles.length + " OrthoNormal transcripts. ");
        for (File transcript : transcriptFiles){
            System.out.println("Tagging " + transcript.getName());
            TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(transcript, true);
            File output = File.createTempFile("FLN","TMP");
            output.deleteOnExit();
            tt.tag(ttont, output);
            SextantOrthonormalIntegrator soi = new SextantOrthonormalIntegrator(transcript.getAbsolutePath());
            soi.integrate(output.getAbsolutePath());
            String OUTPUT = new File(out, transcript.getName()).getAbsolutePath();
            System.out.println("Writing " + OUTPUT);
            soi.writeDocument(OUTPUT);

            Document doc = FileIO.readDocumentFromLocalFile(new File(OUTPUT));
            int count = ppr.apply(doc);
            System.out.println("Applied " + count + " post processing rules");
            FileIO.writeDocumentToLocalFile(OUTPUT, doc);
        }
        
        
    }
}
