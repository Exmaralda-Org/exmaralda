/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk.normalconsistency;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class WordSwitchCorrect extends AbstractConsistencyThing {

    String NORM_REGEX = "dein[a-z]*";
    static int count = 0;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new WordSwitchCorrect();
        try {
            act.doit();
            System.out.println(count + " changes");
        } catch (Exception ex) {
            Logger.getLogger(WordSwitchCorrect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w[following-sibling::w]");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");      
            //System.out.println("******* " + wordText);
            
            Element word2 = (Element) XPath.selectSingleNode(word, "following-sibling::w[1]");
            String wordText2 = WordUtilities.getWordText(word2);
            String normText2 = word2.getAttributeValue("n");   

            //System.out.println("******* " + wordText + " " + wordText2);
            
            if ((normText!=null && normText2!=null && !(wordText.equalsIgnoreCase(wordText2)))){
                if (((wordText2.equalsIgnoreCase(normText) && wordText.equalsIgnoreCase(normText2)))
                    || (normText.equalsIgnoreCase(wordText2 + " " + wordText))){
                    System.out.println("[" + wordText + "/" + normText + "][" + wordText2 + "/" + normText2 + "]");
                    count++;
                }
            }
        }
        //writeDocument();
    }

}
