/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class TreeTaggableOrthonormalTranscription implements TreeTaggableDocument {

    public static String XPATH_TO_CONTRIBUTIONS = "//contribution";
    public static String XPATH_ALL_WORDS_AND_PUNCTUATION = "descendant::*[self::w or self::p]";
    public static String XPATH_NO_XY = "descendant::*[(self::w and not(@n='&')) or self::p]"; 
    public static String XPATH_NO_DUMMIES = "descendant::*[(self::w and not(@n='&' or @n='%' or @n='§' or @n='äh')) or self::p]"; 
    //change 06.11.2013
    //public String xpathToTokens = "descendant::*[self::w or self::p]"; 
    public String xpathToTokens = XPATH_NO_XY;
    
    Document transcriptionDocument;
    List<Element> contributions;
    String base;
    
    boolean basedOnNormalization;
    
    boolean verbose = false;
   

    public TreeTaggableOrthonormalTranscription(File transcription, boolean basedOnNormalization) throws JDOMException, IOException {
        this(FileIO.readDocumentFromLocalFile(transcription), transcription, basedOnNormalization);
    }
    
    public void setVerbose(boolean v){
        verbose = v;
    }

    public TreeTaggableOrthonormalTranscription(Document transcriptionDoc, File transcriptionFile, boolean bon) throws JDOMException, IOException {
        transcriptionDocument = transcriptionDoc;
        base=transcriptionFile.getName();
        contributions = XPath.newInstance(XPATH_TO_CONTRIBUTIONS).selectNodes(transcriptionDocument);
        basedOnNormalization = bon;
    }
    
    public void clearTagging() throws JDOMException{
        List l = XPath.selectNodes(transcriptionDocument, "//w");
        for (Object o : l){
            Element e = (Element)o;
            e.removeAttribute("lemma");
            e.removeAttribute("pos");
            e.removeAttribute("p-pos");
        }
    }
    
    public void setXPathToTokens(String xp){
        xpathToTokens = xp;
    }

    @Override
    public int getNumberOfTaggableSegments() {
        return contributions.size();
    }


    @Override
    public int getNumberOfTokens() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getTokensAt(int pos) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        Element contribution = contributions.get(pos);
        try {
            List l = XPath.newInstance(xpathToTokens).selectNodes(contribution);
            for (Object o : l){
                Element e = (Element)o;
                if (basedOnNormalization){
                   String normalizedForm = e.getAttributeValue("n");
                   if (normalizedForm==null){
                       //result.add(e.getText());                       
                       // changed 03-02-2015
                       // 01-03-2023, issue #340
                       result.add(WordUtilities.getWordText(e, true));
                   } else {
                       //String[] tokens = normalizedForm.split(" ");
                       // change 07/11/2014: trim before splitting, causes problems otherwise
                       String[] tokens = normalizedForm.trim().split(" ");
                       result.addAll(Arrays.asList(tokens));
                   }
                } else {
                    //result.add(e.getText());
                    // changed 03-02-2015
                    // 01-03-2023, issue #340
                    result.add(WordUtilities.getWordText(e, true));
                }
                    
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
        if (verbose){
            System.out.println(result.size() + " tokens at " + pos + ": ");
            for (String t : result){
                System.out.println(t);
            }
        }
        return result;
    }

    @Override
    public List<String> getIDs() throws IOException {
        ArrayList<String> result = new ArrayList<>();
        for (Element contribution : contributions){
            try {
                List l = XPath.newInstance(xpathToTokens).selectNodes(contribution);
                for (Object o : l){
                    Element e = (Element)o;
                    if (basedOnNormalization){
                        String normalizedForm = e.getAttributeValue("n");
                       if (normalizedForm==null){
                            result.add(e.getAttributeValue("id"));
                       } else {
                           String[] tokens = normalizedForm.split(" ");
                           for (String t : tokens){
                                result.add(e.getAttributeValue("id"));
                           }
                       }
                    } else {
                        result.add(e.getAttributeValue("id"));
                    }
                }
            } catch (JDOMException ex) {
                throw new IOException(ex);
            }
        }
        return result;
    }

    @Override
    public String getBase() {
        return base;
    }

}
