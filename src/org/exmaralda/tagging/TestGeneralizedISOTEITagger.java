/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.JDOMException;

/**
 *
 * @author bernd
 */
public class TestGeneralizedISOTEITagger {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new TestGeneralizedISOTEITagger().doit();
        } catch (IOException | JDOMException ex) {
            Logger.getLogger(TestGeneralizedISOTEITagger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    String treeTaggerDirectory = GeneralizedISOTEITagger.TTC;
    String xpathToTokens = TreeTaggableISOTEITranscription.XPATH_ALL_WORDS_AND_PUNCTUATION;
    String[] treeTaggerOptions = GeneralizedISOTEITagger.OPT;
    String[][] lang2ParamFile = {
        {"de", 
            "C:\\UDE\\zumult-pipeline\\src\\main\\java\\de\\linguisticbits\\zumult\\pipeline\\treetagger\\2021-04-16_ParameterFile_ORIGINAL_ALL_FINAL.par", 
            "UTF-8"},
        {"en", 
            "C:\\UDE\\zumult-pipeline\\src\\main\\java\\de\\linguisticbits\\zumult\\pipeline\\treetagger\\english-penn.par", 
            "UTF-8"},
    };
    String[][] lang2PostProcessFile = {
        {"de", PostProcessingRules.FOLK_RULES}
    };
    
    String IN = "C:\\Users\\bernd\\OneDrive\\Desktop\\POS\\TEST_MULTILING\\in.xml";
    String OUT = "C:\\Users\\bernd\\OneDrive\\Desktop\\POS\\TEST_MULTILING\\out.xml";

    private void doit() throws IOException, JDOMException {
        /*
            public GeneralizedISOTEITagger(String treeTaggerDirectory, String xpathToTokens, boolean applyPostProcessing,
                    String[] treeTaggerOptions,
                    String[][] lang2ParamFile,
                    String[][] lang2PostProcessFile
        
        */
        GeneralizedISOTEITagger tagger = new GeneralizedISOTEITagger(treeTaggerDirectory, xpathToTokens, true,
            treeTaggerOptions, lang2ParamFile, lang2PostProcessFile);
        tagger.tagFile(new File(IN), new File(OUT));
    }
    
}
