/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.orthonormal.lexicon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

/**
 *
 * @author bernd
 */
public class AbstractSimpleWordlist {

    HashSet<String> allWords = new HashSet<>();

    public int checkNormalizedFolkerTranscription(Document doc) {
        Iterator i = doc.getRootElement().getDescendants(new ElementFilter("w"));
        int count = 0;
        while (i.hasNext()) {
            Element word = (Element) (i.next());
            // 01-03-2023, issue #340
            String wordText = WordUtilities.getWordText(word, true);
            if (allWords.contains(wordText)) {
                word.setAttribute("i", "y");
            } else {
                word.setAttribute("i", "n");
                count++;
            }
        }
        return count;
    }

    public boolean wordExists(String word) {
        return allWords.contains(word);
    }

    public void init(String path) throws IOException {
        // read the Wordlist from the internal file
        InputStream is = getClass().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String nextLine;
        while ((nextLine = br.readLine()) != null) {
            allWords.add(nextLine);
        }
        br.close();
    }
    
}
