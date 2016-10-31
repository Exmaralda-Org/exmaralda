/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class Evaluate {

    public static String OUTPUT_PATH = "C:\\Users\\Schmidt\\Desktop\\FOLK-Normal\\Evaluation.xml";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            new Evaluate().doit();
        } catch (JDOMException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Evaluate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void doit() throws JDOMException, IOException {
       Element rootElement = new Element("normalization-evaluation");
       Document outputDocument = new Document(rootElement);
       Hashtable<String,Integer> allForms = new Hashtable<String,Integer>();
       File[] flns = new File(ResetDatabaseLexicon.PATH).listFiles(ResetDatabaseLexicon.FLN_FILTER);
       for (File f : flns){
           System.out.println("Reading " + f.getAbsolutePath());
           Document doc = FileIO.readDocumentFromLocalFile(f);
           int countWords = XPath.newInstance("//w").selectNodes(doc).size();
           List normalizedWords = XPath.newInstance("//w[@n]").selectNodes(doc);
           int countNormalizedWords = normalizedWords.size();
           int countCapitalizations = 0;
           int countOneToMany = 0;
           int countNonLexicalised = 0;
           int countCutOff = 0;
           int countIdeolect = 0;
           for (Object o : normalizedWords){
               Element w = (Element)o;
               String wordText = WordUtilities.getWordText(w);
               String normalization = w.getAttributeValue("n");
               if (normalization.contains(" ")) countOneToMany++;
               if (normalization.contains("#")) countNonLexicalised++;
               if (normalization.contains("%")) countCutOff++;
               if (normalization.contains("§")) countIdeolect++;
               if (normalization.toLowerCase().equals(wordText.toLowerCase())) countCapitalizations++;
               
               String hash = wordText + "***" + normalization;
               if (!allForms.containsKey(hash)){
                   allForms.put(hash, new Integer(0));
               }
               allForms.put(hash, allForms.get(hash).intValue()+1);
           }
           Element thisEvaluation = new Element("evaluation");
           rootElement.addContent(thisEvaluation);
           thisEvaluation.setAttribute("file", f.getName());
           thisEvaluation.setAttribute("words", Integer.toString(countWords));
           thisEvaluation.setAttribute("normalizedWords", Integer.toString(countNormalizedWords));
           thisEvaluation.setAttribute("capitalisations", Integer.toString(countCapitalizations));
           thisEvaluation.setAttribute("oneToMany", Integer.toString(countOneToMany));
           thisEvaluation.setAttribute("nonLexicalised", Integer.toString(countNonLexicalised));
           thisEvaluation.setAttribute("cutOff", Integer.toString(countCutOff));
           thisEvaluation.setAttribute("ideolect", Integer.toString(countIdeolect));
       }
       Element forms = new Element("forms");
       rootElement.addContent(forms);
       for (String hash : allForms.keySet()){
           int index = hash.indexOf("***");
           String word = hash.substring(0, index);
           String normalization = hash.substring(index+3);
           String count = Integer.toString(allForms.get(hash));
           Element thisForm = new Element("form");
           forms.addContent(thisForm);
           thisForm.setAttribute("word", word);
           thisForm.setAttribute("normalization", normalization);
           thisForm.setAttribute("count", count);
       }
       FileIO.writeDocumentToLocalFile(OUTPUT_PATH, outputDocument); 
    }
}
