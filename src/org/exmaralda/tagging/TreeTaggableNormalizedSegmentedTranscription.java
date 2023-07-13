/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class TreeTaggableNormalizedSegmentedTranscription implements TreeTaggableDocument {

    public String xpathToSegmentChains;
    public String xpathToTokens;
    public String xpathToNormalization;
    
    Document transcriptionDocument;
    List<Element> segmentChains;
    String base;

    public TreeTaggableNormalizedSegmentedTranscription(File transcription, String xpsc, String xpt, String xpn) throws JDOMException, IOException {
        this(FileIO.readDocumentFromLocalFile(transcription), transcription, xpsc, xpt, xpn);
    }

    public TreeTaggableNormalizedSegmentedTranscription(Document transcriptionDoc, File transcriptionFile, String xpsc, String xpt, String xpn) throws JDOMException, IOException {
        transcriptionDocument = transcriptionDoc;
        base=transcriptionFile.getName();
        xpathToSegmentChains = xpsc;
        xpathToTokens = xpt;
        xpathToNormalization = xpn;
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
        ArrayList<String> result = new ArrayList<>();
        try {
            Element segmentChain = segmentChains.get(pos);
            Element annotation = (Element) XPath.selectSingleNode(segmentChain, xpathToNormalization);
            List l = XPath.newInstance(xpathToTokens).selectNodes(segmentChain);
            for (Object o : l){
                Element e = (Element)o;
                String start = e.getAttributeValue("s");
                String end = e.getAttributeValue("e");
                Element ta = (Element) XPath.selectSingleNode(annotation, "ta[@s='" + start + "' and @e='" + end + "']");
                String normalizedToken = ta.getText().trim();
                result.add(normalizedToken);
            }
        } catch (JDOMException ex) {
            Logger.getLogger(TreeTaggableNormalizedSegmentedTranscription.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public List<String> getIDs() throws IOException {
        ArrayList<String> result = new ArrayList<>();
        try {
            for (Element segmentChain : segmentChains){
                Element annotation = (Element) XPath.selectSingleNode(segmentChain, xpathToNormalization);
                List l = XPath.newInstance(xpathToTokens).selectNodes(segmentChain);
                for (Object o : l){
                    Element e = (Element)o;
                    String start = e.getAttributeValue("s");
                    String end = e.getAttributeValue("e");
                    //System.out.println("s=" + start + " / e=" + end);
                    Element ta = (Element) XPath.selectSingleNode(annotation, "ta[@s='" + start + "' and @e='" + end + "']");
                    result.add(ta.getAttributeValue("id"));
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(TreeTaggableNormalizedSegmentedTranscription.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public String getBase() {
        return base;
    }

    public String normalizeToken(String text) {
        return text.trim();
    }

}
