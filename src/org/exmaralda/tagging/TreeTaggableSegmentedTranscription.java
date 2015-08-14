/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class TreeTaggableSegmentedTranscription implements TreeTaggableDocument {

    public String xpathToSegmentChains;
    public String xpathToTokens;
    
    Document transcriptionDocument;
    List<Element> segmentChains;
    String base;

    public TreeTaggableSegmentedTranscription(File transcription, String xpsc, String xpt) throws JDOMException, IOException {
        this(FileIO.readDocumentFromLocalFile(transcription), transcription, xpsc, xpt);
    }

    public TreeTaggableSegmentedTranscription(Document transcriptionDoc, File transcriptionFile, String xpsc, String xpt) throws JDOMException, IOException {
        transcriptionDocument = transcriptionDoc;
        base=transcriptionFile.getName();
        xpathToSegmentChains = xpsc;
        xpathToTokens = xpt;
        segmentChains = XPath.newInstance(xpathToSegmentChains).selectNodes(transcriptionDocument);
        
        System.out.println(segmentChains.size() + " segment chains for " + xpathToSegmentChains);
    }

    @Override
    public int getNumberOfTaggableSegments() {
        return segmentChains.size();
    }


    @Override
    public int getNumberOfTokens() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<String> getTokensAt(int pos) throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        Element segmentChain = segmentChains.get(pos);
        try {
            List l = XPath.newInstance(xpathToTokens).selectNodes(segmentChain);
            for (Object o : l){
                Element e = (Element)o;
                result.add(e.getText());
            }
        } catch (JDOMException ex) {
            throw new IOException(ex);
        }
        return result;
    }

    @Override
    public List<String> getIDs() throws IOException {
        ArrayList<String> result = new ArrayList<String>();
        for (Element segmentChain : segmentChains){
            try {
                List l = XPath.newInstance(xpathToTokens).selectNodes(segmentChain);
                for (Object o : l){
                    Element e = (Element)o;
                    result.add(e.getAttributeValue("id"));
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
