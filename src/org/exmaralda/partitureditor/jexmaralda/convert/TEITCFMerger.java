/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class TEITCFMerger {
    
    Document teiDocument;
    Document tcfDocument;
    
    public TEITCFMerger(File teiFile, File tcfFile) throws JDOMException, IOException{
        teiDocument = FileIO.readDocumentFromLocalFile(teiFile);
        tcfDocument = FileIO.readDocumentFromLocalFile(tcfFile);
    }
    
    // new 20-04-2015
    // this is im Sinne des Erfinders: source TEI document is schlepped through in a <sourceText> element
    // and can be reread from there
    // this results in a beautifully stateless process!
    public TEITCFMerger(File tcfFile) throws JDOMException, IOException {
        tcfDocument = FileIO.readDocumentFromLocalFile(tcfFile);
        //XPath xp = XPath.newInstance("//metadata:textSource"); 
        //xp.addNamespace("metadata", "http://www.dspin.de/data/metadata");
        //Element textSourceElement = (Element) xp.selectSingleNode(tcfDocument);
        Element textSourceElement = (Element) XPath.selectSingleNode(tcfDocument, "tc:textSource");
        String textSource = textSourceElement.getText();
        teiDocument = FileIO.readDocumentFromString(textSource);        
    }
    
    public void merge() throws JDOMException{
        //XPath uXPath = XPath.newInstance("//tei:annotatedU"); 
        //uXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
        //tei:TEI/tei:text[1]/tei:body[1]/standoff:annotationGrp[1]        
        XPath uXPath = XPath.newInstance("//standoff:annotationGrp"); 
        uXPath.addNamespace("standoff", "http://standoff.proposal");
        List annotatedUs = uXPath.selectNodes(teiDocument);
        System.out.println(annotatedUs.size() + " items");
        
        addTokenAnnotation("//tc:lemma","lemma", annotatedUs, "text()"); // lemmas
        addTokenAnnotation("//tc:POStags/descendant::tc:tag","pos", annotatedUs, "text()"); // POS tags
        addTokenAnnotation("//tc:entity","named-entities", annotatedUs, "@class"); // named entities
        
        addFeatureStructureAnnotation("//tc:morphology", "morph", annotatedUs);
        
    }
    
    public Document getMergedDocument(){
        return teiDocument;
    }
    
    static String tei = "C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\HelgeSchneider_ISO_TEI_EXPORT.xml";
    //static String tcf = "C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\HelgeSchneider_TCF_WebLichtResult_2.xml";
    static String tcf = "C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\NEW_IN.tcf";
    
    public static void main(String[] args){
        try {
            //TEITCFMerger merger = new TEITCFMerger(new File(tei), new File(tcf));
            TEITCFMerger merger = new TEITCFMerger(new File(tcf));
            merger.merge();
            Document result = merger.getMergedDocument();
            FileIO.writeDocumentToLocalFile(new File("C:\\Users\\Schmidt\\Dropbox\\IDS\\TEI_ISO\\TCF\\HelgeSchneider_ISO_TEI_TCF_WebLichtResult_MERGED_2.xml"), result);
        } catch (JDOMException ex) {
            Logger.getLogger(TEITCFMerger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TEITCFMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void addTokenAnnotation(String annotationXPath, String annotationName, List annotatedUs, String valuePath) throws JDOMException {
        XPath lXPath = XPath.newInstance(annotationXPath); 
        lXPath.addNamespace("tc", "http://www.dspin.de/data/textcorpus");
        List annotations = lXPath.selectNodes(tcfDocument);
        if (annotations.isEmpty()) return;
        HashMap<String, Element> elements = new HashMap<String, Element>();
        for (Object o2 : annotations){
            Element la = (Element)o2;
            String[] tokenIDs = la.getAttributeValue("tokenIDs").split(" ");
            elements.put(tokenIDs[0], la);
        }
        
        //*******************************************************
        
        for (Object o : annotatedUs){
            Element annotatedU = (Element)o;
            XPath tXPath = XPath.newInstance("descendant::*[self::tei:w  or self::tei:pc]"); 
            tXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
            List tokens = tXPath.selectNodes(annotatedU);
            
            // 1. LEMMAS
            /* <w xml:id="w36">Karin</w>
             * <lemmas>
                  <lemma ID="le_0" tokenIDs="w36">Karin</lemma>
            </lemmas>            
            <spanGrp type="lemma">
                <span from="#w36" to="#w36">Karin</span>
            </spanGrp>*/
            if (tokens.size()>0){
                //Element spanGrp = new Element("spanGrp", annotatedU.getNamespace());
                Element spanGrp = new Element("spanGrp", Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0"));
                spanGrp.setAttribute("type", annotationName);
                for (Object o2 : tokens){
                    Element token = (Element)o2;
                    String teiTokenID = token.getAttributeValue("id", Namespace.XML_NAMESPACE); 
                    //System.out.println("Trying " + teiTokenID);
                    if (elements.containsKey(teiTokenID)){
                        Element tcfElement = elements.get(teiTokenID);
                        String[] tcfTokenIDs = tcfElement.getAttributeValue("tokenIDs").split(" ");
                        Element spanElement = new Element("span", spanGrp.getNamespace());
                        spanElement.setAttribute("from", "#" + tcfTokenIDs[0]);
                        spanElement.setAttribute("to", "#" + tcfTokenIDs[tcfTokenIDs.length-1]);
                        if ("text()".equals(valuePath)){
                            spanElement.setText(tcfElement.getText());
                        } else if (valuePath.startsWith("@")){
                            spanElement.setText(tcfElement.getAttributeValue(valuePath.substring(1)));
                        }
                        spanGrp.addContent(spanElement);
                    }
                }
                if (!(spanGrp.getChildren().isEmpty())){
                    annotatedU.addContent(spanGrp);
                }

            }            
        }
    }

    private void addFeatureStructureAnnotation(String parentXpath, String namePrefix, List annotatedUs) throws JDOMException {
        XPath lXPath = XPath.newInstance(parentXpath + "/descendant::tc:analysis"); 
        lXPath.addNamespace("tc", "http://www.dspin.de/data/textcorpus");
        List annotations = lXPath.selectNodes(tcfDocument);
        if (annotations.isEmpty()) return;
        HashMap<String, Element> elements = new HashMap<String, Element>();
        for (Object o2 : annotations){
            Element la = (Element)o2;
            String[] tokenIDs = la.getAttributeValue("tokenIDs").split(" ");
            elements.put(tokenIDs[0], la);
        }

        for (Object o : annotatedUs){
            Element annotatedU = (Element)o;
            XPath tXPath = XPath.newInstance("descendant::*[self::tei:w  or self::tei:pc]"); 
            tXPath.addNamespace("tei", "http://www.tei-c.org/ns/1.0");
            List tokens = tXPath.selectNodes(annotatedU);
            
            HashMap<String, Element> spanGrpsPerFeature = new HashMap<String, Element>();
            
            if (tokens.size()>0){
                for (Object o2 : tokens){
                    Element token = (Element)o2;
                    String teiTokenID = token.getAttributeValue("id", Namespace.XML_NAMESPACE); 
                    //System.out.println("Trying " + teiTokenID);
                    if (elements.containsKey(teiTokenID)){
                        Element tcfElement = elements.get(teiTokenID);
                        /*<tc:analysis tokenIDs="w1">
                            <tc:tag>
                                <tc:fs>
                                    <tc:f name="cat">regular noun</tc:f>
                                    <tc:f name="case">nominative</tc:f>
                                    <tc:f name="number">plural</tc:f>
                                </tc:fs>
                            </tc:tag>
                        </tc:analysis>*/
                        String[] tcfTokenIDs = tcfElement.getAttributeValue("tokenIDs").split(" ");
                        XPath fXPath = XPath.newInstance("descendant::tc:f");
                        fXPath.addNamespace("tc", "http://www.dspin.de/data/textcorpus");
                        List features = fXPath.selectNodes(tcfElement);
                        for (Object o3 : features){
                            Element feature = (Element)o3;
                            String featureName = feature.getAttributeValue("name");
                            if (!(spanGrpsPerFeature.containsKey(featureName))){
                                //Element spanGrp = new Element("spanGrp", annotatedU.getNamespace());
                                Element spanGrp = new Element("spanGrp", Namespace.getNamespace("tei", "http://www.tei-c.org/ns/1.0"));                                
                                spanGrp.setAttribute("type", namePrefix + "-" + featureName);
                                spanGrpsPerFeature.put(featureName, spanGrp);
                            }
                            Element spanGrp = spanGrpsPerFeature.get(featureName);
                            Element spanElement = new Element("span", spanGrp.getNamespace());
                            spanElement.setAttribute("from", "#" + tcfTokenIDs[0]);
                            spanElement.setAttribute("to", "#" + tcfTokenIDs[tcfTokenIDs.length-1]);
                            spanElement.setText(feature.getText());
                            spanGrp.addContent(spanElement);
                        }
                    }
                }
                for (Element spanGrp : spanGrpsPerFeature.values()){
                    if (!(spanGrp.getChildren().isEmpty())){
                        annotatedU.addContent(spanGrp);
                    }
                }

            }            
            
        }
        
        
        
        
    }
    
}
