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
public class UnverstdlCorrect extends AbstractConsistencyThing {

    String WORD_REGEX = "(\\+\\+\\+)+";
    String NORM_REGEX = "[A-Za-zäöüÄÖÜß]+";
    static int count;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new UnverstdlCorrect();
        try {
            act.doit();
            System.out.println(count + " changes");
            
        } catch (Exception ex) {
            Logger.getLogger(UnverstdlCorrect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");            
            if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX) && word.getChildren().isEmpty()){
                System.out.print(word.getText() + " --> ");
                word.setText(normText.toLowerCase());
                count++;
                System.out.println(word.getText());
            }
        }
        writeDocument();
    }

}
