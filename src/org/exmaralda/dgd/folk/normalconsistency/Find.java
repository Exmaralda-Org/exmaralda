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
import org.exmaralda.common.corpusbuild.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class Find extends AbstractConsistencyThing {

    static String RESULT_FILE = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\Consistency\\Aberrant.xml";
    String WORD_REGEX = ".+";
    String NORM_REGEX = ".*§.*";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new Find();
        try {
            act.doit();
            act.writeResultDocument(new File(RESULT_FILE));
            int matches = FileIO.readDocumentFromLocalFile(RESULT_FILE).getRootElement().getContentSize();
            System.out.println(matches + " matches.");
        } catch (Exception ex) {
            Logger.getLogger(Find.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w");
        for (Object o : words){
            Element word = (Element)o;
            String wordText = WordUtilities.getWordText(word);
            String normText = word.getAttributeValue("n");
            //if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX)){
            //if (normText!=null && normText.replaceAll(" ", "").equals(wordText) && normText.indexOf(" ")==normText.lastIndexOf(" ")){
            //if (normText!=null && normText.equals(wordText+"e")){
            if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX)){
            //if (wordText.matches(WORD_REGEX)){
                //word.detach();
                //word.setAttribute("transcript", currentFile.getName());
                //resultRoot.addContent(word);
                
                Element contribution = (Element) XPath.selectSingleNode(word, "ancestor::contribution");
                contribution.detach();
                contribution.setAttribute("transcript", currentFile.getName());
                contribution.setAttribute("match", word.getAttributeValue("id"));
                resultRoot.addContent(contribution);
                
            }
        }
    }
}
