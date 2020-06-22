/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.annolab.tt4j.TokenHandler;
import org.annolab.tt4j.TreeTaggerException;
import org.annolab.tt4j.TreeTaggerWrapper;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Thomas_Schmidt
 */
public class UNSD_DebugTagging {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws TreeTaggerException {
        try {        
            new UNSD_DebugTagging().doit();
        } catch (IOException ex) {
            Logger.getLogger(UNSD_DebugTagging.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(UNSD_DebugTagging.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void doit() throws IOException, JDOMException, TreeTaggerException {
        String xpathToTokens = TreeTaggableOrthonormalTranscription.XPATH_NO_XY;
        boolean applyPP = true;    
        String TTC = "c:\\TreeTagger";
        String PF = "C:\\TreeTagger\\lib\\italian.par";
        String ENC = "UTF-8";
        String[] OPT = {"-token","-lemma","-sgml","-no-unknown", "", ""};
        String transcript = "M:\\GeWiss\\1-NON-DE\\FLN\\ita\\GWSS_E_03017_SE_01_T_01_DF_01.fln";
        
         System.setProperty("treetagger.home", TTC);
         TreeTaggerWrapper ttw = new TreeTaggerWrapper<String>();
         //uncomment next line to make TreeTaggerWrapper verbose
         //tt.TRACE = true;
         ttw.setProbabilityThreshold(0.999999);
         ttw.setModel(PF + ":" + ENC);
         ttw.setArguments(OPT);
         ttw.setHandler(new TokenHandler(){
            @Override
            public void token(Object token, String pos, String lemma) {
                System.out.println(token.toString() + " / " + pos + " / " + lemma);
            }
             
         });
         String[] tokens = {"ihr", "seid"};
         ttw.process(tokens);
        
        //System.exit(0);
        
        TreeTagger tt = new TreeTagger(TTC, PF, ENC, OPT);        
        Document trDoc = FileIO.readDocumentFromLocalFile(transcript);

        // get rid of all existing attributes for pos and lemma
        List l = XPath.selectNodes(trDoc, "//@lemma|//@pos|//@p-pos");
        for (Object o : l){
            Attribute a = (Attribute)o;
            a.detach();
        }
        
        /*Element contr278 = (Element) XPath.selectSingleNode(trDoc, "//contribution[278]");
        contr278.detach();
        Document doc278 = new Document(contr278);
        File intermediate = File.createTempFile("FLN","TMP");
        intermediate.deleteOnExit();
        FileIO.writeDocumentToLocalFile(intermediate, doc278);*/

        //System.out.println("Tagging " + transcript.getName() + " (" + (count2+1) + " of " + transcriptFiles.length + ")");
        TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(new File(transcript), true);
        ttont.setXPathToTokens(xpathToTokens);

        File output = File.createTempFile("FLN","TMP");
        output.deleteOnExit();
        boolean works = false;
        while (!works){
            try {
                tt.tag(ttont, output);
                works=true;
            } catch (IOException ex){
                System.out.println("Error");
                /*Element lastWord = (Element) XPath.selectSingleNode(contr278, "//descendant::w[last()]");
                lastWord.detach();
                //lastWord.setAttribute("n", "XXX");
                System.out.println("Removed word " + lastWord.getText());
                FileIO.writeDocumentToLocalFile(intermediate, doc278);
                ttont = new TreeTaggableOrthonormalTranscription(intermediate, true);
                ttont.setXPathToTokens(xpathToTokens);                                */
            }
        }
        
        /*List l2 = XPath.selectNodes(trDoc, "//contribution");
        int count=1;
        for (Object o : l2){
            Element c = (Element)o;
            c.detach();
            System.out.println("Tagging contribution #" + count);
            Document trDocPart = new Document(c);
            File intermediate = File.createTempFile("FLN","TMP");
            intermediate.deleteOnExit();
            FileIO.writeDocumentToLocalFile(intermediate, trDocPart);

            //System.out.println("Tagging " + transcript.getName() + " (" + (count2+1) + " of " + transcriptFiles.length + ")");
            TreeTaggableOrthonormalTranscription ttont = new TreeTaggableOrthonormalTranscription(intermediate, true);
            ttont.setXPathToTokens(xpathToTokens);

            File output = File.createTempFile("FLN","TMP");
            output.deleteOnExit();
            ttw.tag(ttont, output);
            count++;
        }*/
        
        
    }
    
}
