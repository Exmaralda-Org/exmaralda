/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author bernd
 */
public class GeneralizedISOTEITagger {
    
    public static String TTC = "C:\\Users\\bernd\\Dropbox\\TreeTagger";
    public static String[] OPT = {"-token","-lemma","-sgml","-no-unknown"};

    public String xpathToTokens;
    public boolean applyPP;
    
    
    // language specific
    Map<String, TreeTagger> language2TreeTagger = new HashMap<>();
    Map<String, PostProcessingRules> language2PostProcessing = new HashMap<>();
    
    
    
    
    public static String PF = "C:\\linguisticbits_nb\\src\\de\\linguisticbits\\austin\\tgdp\\march_2023\\tagger\\2021-04-16_ParameterFile_ORIGINAL_ALL_FINAL.par";
    public static String ENC = "UTF-8";
    
    
    public GeneralizedISOTEITagger(String treeTaggerDirectory, String xpathToTokens, boolean applyPostProcessing,
            String[] treeTaggerOptions,
            String[][] lang2ParamFile,
            String[][] lang2PostProcessFile
            ) throws IOException, JDOMException{
        
        this.xpathToTokens = xpathToTokens;
        this.applyPP = applyPostProcessing;
        
        for (String[] paramFileSpec : lang2ParamFile){
            String language = paramFileSpec[0];
            String paramFilePath = paramFileSpec[1];
            String paramFileEncoding = paramFileSpec[2];
            TreeTagger tt = new TreeTagger(treeTaggerDirectory, paramFilePath, paramFileEncoding, treeTaggerOptions);   
            tt.verbose = false;
            language2TreeTagger.put(language, tt);
            System.out.println("[GeneralizedISOTEITagger] TreeTagger for language " + language + " initialised with " + new File(paramFilePath).getName());
        }
        
        for (String[] ppRuleSpec : lang2PostProcessFile){
            String language = ppRuleSpec[0];
            String path = ppRuleSpec[1];
            PostProcessingRules ppr = new PostProcessingRules();
            ppr.read(path);
            language2PostProcessing.put(language, ppr);
            System.out.println("[GeneralizedISOTEITagger] PostProcessing rules for language " + language + " initialised with " + path);
        }
    }
    
    public void tagFile(File input, File output) throws JDOMException, IOException{
            Document trDoc = FileIO.readDocumentFromLocalFile(input);
            
            // get rid of all existing attributes for pos and lemma
            List l = XPath.selectNodes(trDoc, "//@lemma|//@pos|//@p-pos");
            for (Object o : l){
                Attribute a = (Attribute)o;
                a.detach();
            }
            
            Set<String> languages = language2TreeTagger.keySet();
            
            List<File> tempFiles = new ArrayList<>();
            
            File intermediate = File.createTempFile("ISO_TEI","TMP");
            intermediate.deleteOnExit();
            FileIO.writeDocumentToLocalFile(intermediate, trDoc);

            for (String language : languages){

                TreeTagger treeTagger = language2TreeTagger.get(language);
                String xpathToSegs = 
                        "//tei:annotationBlock/descendant::tei:seg[not(tei:seg) and ancestor-or-self::*[@xml:lang][1]/@xml:lang='" + language + "']";
                
                System.out.println("[GeneralizedISOTEITagger] Tagging " + input.getName() + " for language " + language);
                TreeTaggableISOTEITranscription ttont = new TreeTaggableISOTEITranscription(intermediate, true);
                ttont.setXPathToSegs(xpathToSegs);
                ttont.setXPathToTokens(xpathToTokens);
                
                File tempOutput = File.createTempFile("ISO_TEI","TMP");
                System.out.println("[GeneralizedISOTEITagger] " + tempOutput.getAbsolutePath() + " created.");
                tempOutput.deleteOnExit();
                treeTagger.tag(ttont, tempOutput);
                System.out.println("[GeneralizedISOTEITagger] Tagging for language " + language + " done");
                tempFiles.add(tempOutput);
                
            }
            
            SextantISOTEIIntegrator soi = new SextantISOTEIIntegrator(intermediate.getAbsolutePath());
            for (File tempOutput : tempFiles){
                System.out.println("[GeneralizedISOTEITagger] Writing " + tempOutput.getAbsolutePath());
                soi.integrate(tempOutput.getAbsolutePath());
                soi.writeDocument(intermediate.getAbsolutePath());
                soi = new SextantISOTEIIntegrator(intermediate.getAbsolutePath());                
            }
            soi.writeDocument(output.getAbsolutePath());

            
            if (applyPP){
                Document doc = FileIO.readDocumentFromLocalFile(output);                
                Set<String> ppLanguages = language2PostProcessing.keySet();
                for (String language : ppLanguages){
                    PostProcessingRules ppr = this.language2PostProcessing.get(language);
                    if (ppr!=null){
                        // "Ordinary" Post Processing Rules : 1:1
                        int count = ppr.applyISOTEI(doc, language);
                        System.out.println("[GeneralizedISOTEITagger] Applied 1:1 post processing rules for language " + language + " on " + count + " elements. " );                
                    }
                }
                FileIO.writeDocumentToLocalFile(output, doc);
            }
            
            intermediate.delete();
            for (File tempOutput : tempFiles){
                tempOutput.delete();
            }
            
        
    }
    
    
}
