/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
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
    // change 06.11.2013
    //public String xpathToTokens = "descendant::*[self::w or self::p]"; 
    public String xpathToTokens = "descendant::*[(self::w and not(@n='&')) or self::p]"; 
    
    Document transcriptionDocument;
    List<Element> contributions;
    String base;
    
    boolean basedOnNormalization;
   

    public TreeTaggableOrthonormalTranscription(File transcription, boolean basedOnNormalization) throws JDOMException, IOException {
        this(FileIO.readDocumentFromLocalFile(transcription), transcription, basedOnNormalization);
    }

    public TreeTaggableOrthonormalTranscription(Document transcriptionDoc, File transcriptionFile, boolean bon) throws JDOMException, IOException {
        transcriptionDocument = transcriptionDoc;
        base=transcriptionFile.getName();
        contributions = XPath.newInstance(XPATH_TO_CONTRIBUTIONS).selectNodes(transcriptionDocument);
        basedOnNormalization = bon;
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
        Vector<String> result = new Vector<String>();
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
                       result.add(WordUtilities.getWordText(e));
                   } else {
                       //String[] tokens = normalizedForm.split(" ");
                       // change 07/11/2014: trim before splitting, causes problems otherwise
                       String[] tokens = normalizedForm.trim().split(" ");
                       for (String t : tokens){
                           result.add(t);
                       }
                   }
                } else {
                    //result.add(e.getText());
                    // changed 03-02-2015
                    result.add(WordUtilities.getWordText(e));
                }
                    
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
        return result;
    }

    @Override
    public List<String> getIDs() throws IOException {
        Vector<String> result = new Vector<String>();
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

    public String getBase() {
        return base;
    }

}
