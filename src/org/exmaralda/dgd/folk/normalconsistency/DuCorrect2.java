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
public class DuCorrect2 extends AbstractConsistencyThing {

    String NORM_REGEX = "du .+";
    static int count = 0;
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new DuCorrect2();
        try {
            act.doit();
            System.out.println(count + " changes");
        } catch (Exception ex) {
            Logger.getLogger(DuCorrect2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");            
            if (normText!=null && normText.matches(NORM_REGEX)){
                String newNorm = word.getAttributeValue("n").replaceAll("^du ", "Du ");
                word.setAttribute("n", newNorm);
                System.out.println(newNorm);
                count++;
            }
        }
        writeDocument();
    }

}
