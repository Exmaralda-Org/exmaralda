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
public class FindWordInternalPauses extends AbstractConsistencyThing {

    static String RESULT_FILE = "C:\\Users\\Schmidt\\Desktop\\FOLK_RELEASE\\Consistency\\WordInternPause.xml";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AbstractConsistencyThing act = new FindWordInternalPauses();
        try {
            act.doit();
            act.writeResultDocument(new File(RESULT_FILE));
            int matches = FileIO.readDocumentFromLocalFile(RESULT_FILE).getRootElement().getContentSize();
            System.out.println(matches + " matches.");
        } catch (Exception ex) {
            Logger.getLogger(FindWordInternalPauses.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void processDocument(Document doc) throws Exception {
        List words = XPath.selectNodes(doc, "//w[@n and following-sibling::*[1][self::pause and @duration='micro'] and following-sibling::*[2][self::w]]");
        for (Object o : words){
            Element word1 = (Element)o;
            String wordText1 = WordUtilities.getWordText(word1);
            String normText1 = word1.getAttributeValue("n");
            
            Element word2 = (Element) XPath.selectSingleNode(word1, "following-sibling::w[1]");
            String wordText2 = WordUtilities.getWordText(word2);
            String normText2 = word2.getAttributeValue("n");

            //if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX)){
            //if (normText!=null && normText.replaceAll(" ", "").equals(wordText) && normText.indexOf(" ")==normText.lastIndexOf(" ")){
            //if (wordText.matches(WORD_REGEX) && normText!=null && normText.matches(NORM_REGEX)){
                //word.detach();
                //word.setAttribute("transcript", currentFile.getName());
                //resultRoot.addContent(word);
            if (normText1.length()>wordText1.length()+2){    
                Element contribution = (Element) XPath.selectSingleNode(word1, "ancestor::contribution");
                contribution.detach();
                contribution.setAttribute("transcript", currentFile.getName());
                contribution.setAttribute("match", word1.getAttributeValue("id"));
                resultRoot.addContent(contribution);
            }
            //}
        }
    }
}
