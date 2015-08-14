/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.dgd.folk.normalconsistency;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class SplitCorrect extends AbstractConsistencyThing {

    String WORD_REGEX = "wieviel";
    String NORM_REGEX = "ja ja";
    static int changeCount = 0;
    static int otherCount = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new SplitCorrect();
        try {
            act.doit();
            System.out.println(changeCount + " / " + otherCount + " changes");
        } catch (Exception ex) {
            Logger.getLogger(SplitCorrect.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");            
            if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX)){
                System.out.println(IOUtilities.elementToString(word));
                if (word.getChildren().isEmpty()){
                    word.removeAttribute("n");
                    word.setText("wie");
                    Element newWord = new Element("w");
                    newWord.setAttribute("id", word.getAttributeValue("id") + "a");
                    newWord.setText("viel");
                    //newWord.setAttribute("n", "etwas");
                    Element parentElement = word.getParentElement();
                    int index = parentElement.indexOf(word);
                    parentElement.addContent(index+1, newWord);
                    changeCount++;
                } else {
                    // do nothing - too complicated
                    otherCount++;
                }
            }
        }
        writeDocument();
    }

}
