/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusWalker;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class CorpusTreeTagger extends COMACorpusWalker {

    TreeTagger treeTagger;
    String segmentationXPath;
    String tokenXPath;
    boolean writeSextantFiles;
    boolean integrate;
    String suffix;

    public CorpusTreeTagger(File corpusFile, TreeTagger tt, String sxp, String txp, boolean wsf, boolean i, String s) throws IOException, JDOMException {
        super();
        readCorpus(corpusFile);
        treeTagger =  tt;
        segmentationXPath = sxp;
        tokenXPath = txp;
        writeSextantFiles = wsf;
        integrate = i;
        suffix = s;
    }

    File getOutputFile(File inputFile) throws IOException{
        String name = inputFile.getName();
        if (name.contains(".")){
            name = inputFile.getName().substring(0, inputFile.getName().lastIndexOf("."));
        }
        if (writeSextantFiles){
            File output = new File(inputFile.getParentFile(), name + suffix + ".esa");
            return output;
        } else {
            File output = File.createTempFile(name + suffix, ".esa");
            output.deleteOnExit();
            return output;
        }
    }

    @Override
    public void processTranscription(Document document) throws JDOMException {
        try {
            File currentFile = new File(currentPath);
            TreeTaggableSegmentedTranscription ttst = new TreeTaggableSegmentedTranscription(document, currentFile, segmentationXPath, tokenXPath);
            File outputFile = getOutputFile(currentFile);
            treeTagger.tag(ttst, outputFile);
            if (integrate){
                SextantIntegrator sextantIntegrator = new SextantIntegrator(currentPath);
                sextantIntegrator.integrate(outputFile.getAbsolutePath());
                sextantIntegrator.writeDocument(currentPath);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new JDOMException(ex.getLocalizedMessage());
        }

    }

    public void tagCorpus() throws IOException, JDOMException{
        fireCorpusInit(0.1, "Initialising tagger...");
        walk(SEGMENTED_TRANSCRIPTIONS);
    }
    
    public static String TTC = "c:\\TreeTagger";
    public static String PF = "c:\\TreeTagger\\lib\\german-utf8.par";
    public static String ENC = "UTF-8";
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};

    public static void main(String[] args){
        try {
            File corpusFile = new File("C:\\Users\\Schmidt\\ownCloud\\Shared\\ModiKo\\Datengrundlage\\MoDiKo-Gesamtkorpus-1\\modiko-gesamtkorpus-1.coma");
            TreeTagger tt = new TreeTagger(TTC,PF,ENC,OPT);
            String segmentationXPath = TaggingProfiles.GENERIC_WORDS_PUNCTUATION_SEGMENTATION_XPATH;
            String tokenXPath = TaggingProfiles.GENERIC_WORDS_PUNCTUATION_TOKEN_XPATH;
            CorpusTreeTagger ctt = new CorpusTreeTagger(corpusFile,tt,segmentationXPath,tokenXPath,true, true, "_POS");
            ctt.tagCorpus();
        } catch (IOException ex) {
            Logger.getLogger(CorpusTreeTagger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(CorpusTreeTagger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }



}
