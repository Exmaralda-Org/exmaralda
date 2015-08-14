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
import org.jdom.Text;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class SplitCorrectGeneric extends AbstractConsistencyThing {

    static int changeCount = 0;
    static int otherCount = 0;
    static int thirdCount = 0;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new SplitCorrectGeneric();
        try {
            act.doit();
            System.out.println(changeCount + " / " + otherCount + " / " + thirdCount + " changes");
        } catch (Exception ex) {
            Logger.getLogger(SplitCorrectGeneric.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");            
            if (normText!=null && normText.replaceAll(" ", "").equals(wordText) && normText.indexOf(" ")==normText.lastIndexOf(" ")){
                int index = normText.indexOf(" ");
                String word1 = normText.substring(0, index);
                String word2 = normText.substring(index+1);
                System.out.println(wordText + " --> " + normText + " --> [" + word1 + "/" + word2 + "]");
                if (word.getChildren().isEmpty()){
                    word.removeAttribute("n");
                    word.setText(word1);
                    Element newWord = new Element("w");
                    newWord.setAttribute("id", word.getAttributeValue("id") + "a");
                    newWord.setText(word2);
                    //newWord.setAttribute("n", "etwas");
                    Element parentElement = word.getParentElement();
                    int index2 = parentElement.indexOf(word);
                    parentElement.addContent(index2+1, newWord);
                    changeCount++;
                } else {
                    //<w id="w2956" i="n" n="kann ich" transcript="FOLK_E_00022_SE_01_T_02_DF_01.fln">kann<time
                    //timepoint-reference="TLI_1050"/>ich</w>
                    List l = word.getContent();
                    if (    l.size()==3
                            && l.get(0) instanceof Text
                            && l.get(1) instanceof Element
                            && ((Element)(l.get(1))).getName().equals("time")
                            && l.get(2) instanceof Text
                            && ((Text)(l.get(0))).getText().equals(word1)
                            && ((Text)(l.get(2))).getText().equals(word2)
                       ){
                            otherCount++;
                            word.removeAttribute("n");
                            List l2 = word.removeContent();
                            word.setText(word1);
                            
                            Element newWord = new Element("w");
                            newWord.setAttribute("id", word.getAttributeValue("id") + "a");
                            newWord.setText(word2);
                            //newWord.setAttribute("n", "etwas");
                            
                            Element parentElement = word.getParentElement();                            
                            int index2 = parentElement.indexOf(word);       
                            
                            parentElement.addContent(index2+1, (Element)l2.get(1));
                            parentElement.addContent(index2+2, newWord);
                            changeCount++;
                    } else {
                        thirdCount++;
                    }
                }
            }
        }
        writeDocument();
    }

}
