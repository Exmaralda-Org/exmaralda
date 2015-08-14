/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;

/**
 *
 * @author Schmidt
 */
public class DerewoWordlist {

    HashSet<String> allWords = new HashSet<String>();
    String DEREWO_PATH = "/org/exmaralda/orthonormal/lexicon/derewo_wordlist.txt";
    
    public DerewoWordlist() throws IOException {
        // read the Wordlist from the internal file
        java.io.InputStream is = getClass().getResourceAsStream(DEREWO_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String nextLine = new String();
        while ((nextLine = br.readLine()) != null){
            allWords.add(nextLine);
        }
        br.close();                
    }
    
    public int checkNormalizedFolkerTranscription(Document doc){
        Iterator i = doc.getRootElement().getDescendants(new ElementFilter("w"));
        int count=0;
        while (i.hasNext()){
            Element word = (Element)(i.next());
            String wordText = WordUtilities.getWordText(word);
            if (allWords.contains(wordText)){
                word.setAttribute("i", "y");
            } else {
                word.setAttribute("i", "n");
                count++;
            }
        }
        return count;
    }
    
    public static void main(String[] args){
        try {
            DerewoWordlist derewoWordlist = new DerewoWordlist();
            Document doc = FileIO.readDocumentFromLocalFile("C:\\Users\\Schmidt\\Desktop\\FOLK\\FOLK-Normal\\transcripts\\1-Tagged-Normal\\FOLK_E_00047_SE_01_T_02_DF_01.fln");
            derewoWordlist.checkNormalizedFolkerTranscription(doc);
            FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Desktop\\derewo_test.fln"), doc);
        } catch (JDOMException ex) {
            Logger.getLogger(DerewoWordlist.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DerewoWordlist.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
}
