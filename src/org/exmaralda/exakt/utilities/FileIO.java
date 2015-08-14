/*
 * FileReader.java
 *
 * Created on 27. Mai 2005, 11:23
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.exakt.utilities;

import java.net.MalformedURLException;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpus;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Content;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.*;
import org.exmaralda.exakt.exmaraldaSearch.COMACorpusInterface;
import org.exmaralda.exakt.exmaraldaSearch.COMADBCorpus;
import org.exmaralda.exakt.exmaraldaSearch.COMARemoteCorpus;
import org.exmaralda.exakt.search.AnnotationSearchResult;
import org.exmaralda.exakt.search.SearchResultInterface;
import org.exmaralda.exakt.search.SearchResultList;
import org.exmaralda.exakt.search.SimpleSearchResult;
import org.exmaralda.exakt.search.analyses.AnalysisInterface;
/**
 *
 * @author thomas
 */
public class FileIO {
    
    public static Document readDocumentFromLocalFile(String pathToDocument) throws JDOMException, IOException {
        if (pathToDocument.startsWith("http")){
            return readDocumentFromURL(pathToDocument);
        }
        SAXBuilder builder = new SAXBuilder();
        File file = new File(pathToDocument);
        Document document =  builder.build(file);
        return document;
    }
    
    public static Document readDocumentFromLocalFile(File file) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document =  builder.build(file);
        return document;
    }
    


    public static Document readDocumentFromURL(String url) throws JDOMException, IOException {
        SAXBuilder builder = new SAXBuilder();
        Document document =  builder.build(new java.net.URL(url));   
        return document;
    }
    
    public static Document readDocumentFromString(String docString) throws JDOMException, IOException{
        SAXBuilder saxBuilder = new SAXBuilder();
        java.io.StringReader sr = new java.io.StringReader(docString);
        Document doc = saxBuilder.build(sr);        
        return doc;        
    }
    
    
    public static String getDocumentAsString(Document document) throws IOException {
        return getDocumentAsString(document,false);
    }
    
    public static String getDocumentAsString(Document document, boolean omitDeclaration) throws IOException {
        XMLOutputter xmlOutputter = new XMLOutputter();
        xmlOutputter.setFormat(xmlOutputter.getFormat().setOmitDeclaration(omitDeclaration));
        java.io.StringWriter writer = new java.io.StringWriter();
        xmlOutputter.output(document,writer);
        return writer.toString();        
    }
    
    public static void writeDocumentToLocalFile(String pathToDocument, Document document) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        //String docString = xmlOutputter.outputString(document);        
        FileOutputStream fos = new FileOutputStream(new File(pathToDocument));        
        xmlOutputter.output(document,fos);
        //fos.write(docString.getBytes("UTF-8"));
        fos.close();    
    }
    
    public static Document COMASearchResultListToXML(SearchResultList list, 
                                                 COMACorpusInterface corpus,
                                                 Vector<String[]> metaIdentifier,
                                                 String pathToCorpus
            ){
            Document result = new Document();
            Element rootElement = new Element("search-result-list");
            // new 21-11-2011: need to distinguish Annotation Search Results            
            if ((list.size()>0) && (list.elementAt(0) instanceof AnnotationSearchResult)){
                rootElement.setAttribute("type", "annotation-search-result");
            }
            result.setRootElement(rootElement);
            String uriString = pathToCorpus;

            //System.out.println("URI-String : " + uriString);

            if (corpus instanceof COMACorpus){
                File f = new File(pathToCorpus).getParentFile();
                java.net.URI uri = f.toURI();
                uriString = uri.toString();
            }  else if (corpus instanceof COMARemoteCorpus){
                try {
                    // NEED TO GET THE PARENT!
                    // HERE OR SOMEWHERE ELSE?
                    uriString = new URL(new URL(corpus.getCorpusPath()), ".").toString();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
            } else if (corpus instanceof COMADBCorpus){
                //TODO:
            }
            
            Element bd = new Element("base-directory");
            bd.setAttribute("url", uriString);
            rootElement.addContent(bd);       
            
            Element a = new Element("analyses");
            for (AnalysisInterface ai : list.getAnalyses()){
                a.addContent(ai.toXML());
            }
            rootElement.addContent(a);
            
            
            for (SearchResultInterface r : list){
                SimpleSearchResult ssr = (SimpleSearchResult)r;
                Element thisSearchResult = ssr.toXML(uriString);
                String transcriptionLocator = (String)(ssr.getSearchableSegmentLocator().getCorpusComponentLocator());
                String speakerID = ssr.getAdditionalData()[1];
                
                String communicationName = corpus.getCommunicationData(transcriptionLocator,"Name*");
                thisSearchResult.setAttribute("communication", communicationName);
                String speakerSigle = corpus.getSpeakerData(transcriptionLocator,speakerID,"Sigle*");
                thisSearchResult.setAttribute("speaker", speakerSigle);
                
                for (String[] s : metaIdentifier){
                    String metaType = s[0];
                    String metaName = s[1];
                    String metaValue = "";
                    if (metaType.equals("C")) metaValue=corpus.getCommunicationData(transcriptionLocator,metaName);
                    else if (metaType.equals("T")) metaValue=corpus.getTranscriptionData(transcriptionLocator,metaName);
                    else if (metaType.equals("S")) metaValue=corpus.getSpeakerData(transcriptionLocator,speakerID,metaName);;                                
                    Element meta = new Element("meta");
                    meta.setAttribute("type", metaType);
                    meta.setAttribute("name", metaName);
                    meta.setText(metaValue);
                    thisSearchResult.addContent(meta);
                }
                rootElement.addContent(thisSearchResult);
            }
            return result;
    }
    
    public static Vector<String[]> getMetaFromSearchResult(File file) throws JDOMException, IOException{
        Vector<String[]> result = new Vector<String[]>();
        Document d = FileIO.readDocumentFromLocalFile(file);
        Element firstSearchResult = d.getRootElement().getChild("search-result");
        if (firstSearchResult==null){
            return result;
        }
        List metaElements = firstSearchResult.getChildren("meta");
        for (Object o : metaElements){
            Element metaElement = (Element)o;
            String type = metaElement.getAttributeValue("type");
            String name = metaElement.getAttributeValue("name");
            //System.out.println("Meta: " + type + "/" + name);
            String[] both = {type,name};
            result.addElement(both);
        }
        return result;
    }
    
    public static void reduceSegmentedTranscription(Document st, String tli) throws JDOMException{
        int dl = java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.EXAKT").getInt("full-display-limit", 50);
        if (dl<0) return;
        String xp = "//tli[@id=\"" + tli + "\"]";
        XPath xpath = XPath.newInstance(xp);
        Element tliElement = (Element)(xpath.selectSingleNode(st));
        Element tliParent = tliElement.getParentElement();
        if (tliParent.getName().equals("timeline-fork")){
            String tliID = tliParent.getAttributeValue("start");
            xp = "//tli[@id=\"" + tliID + "]";
            xpath = XPath.newInstance(xp);
            tliElement = (Element)(xpath.selectSingleNode(st));
        }        
        String xp2 = "//common-timeline/tli";
        XPath xpath2 = XPath.newInstance(xp2);
        List tlis = xpath2.selectNodes(st);
        int index = tlis.indexOf(tliElement);
        int startIndex = Math.max(0, index-dl);
        int endIndex = Math.min(index+dl,tlis.size()-1);
        
        String xp3 = "//segmentation/ts | //ta";
        XPath xpath3 = XPath.newInstance(xp3);
        List segments = xpath3.selectNodes(st);
        for(Object s : segments){
            Element seg = (Element)s;
            
            String start = seg.getAttributeValue("s");
            xp = "//tli[@id=\"" + start + "\"]";
            xpath = XPath.newInstance(xp);
            tliElement = (Element)(xpath.selectSingleNode(st));
            int sIndex = tlis.indexOf(tliElement);
            
            String end = seg.getAttributeValue("e");
            xp = "//tli[@id=\"" + end + "\"]";
            xpath = XPath.newInstance(xp);
            tliElement = (Element)(xpath.selectSingleNode(st));
            int eIndex = tlis.indexOf(tliElement);
            
            if ((eIndex<startIndex) || (sIndex>endIndex)){
                seg.detach();
            }
        }
        String xp4 = "//timeline-fork";
        XPath xpath4 = XPath.newInstance(xp4);
        List forks = xpath4.selectNodes(st);
        for(Object f : forks){
            Element fork = (Element)f;            
            String start = fork.getAttributeValue("start");
            xp = "//tli[@id=\"" + start + "\"]";
            xpath = XPath.newInstance(xp);
            tliElement = (Element)(xpath.selectSingleNode(st));
            int sIndex = tlis.indexOf(tliElement);
            
            String end = fork.getAttributeValue("end");
            xp = "//tli[@id=\"" + end + "\"]";
            xpath = XPath.newInstance(xp);
            tliElement = (Element)(xpath.selectSingleNode(st));
            int eIndex = tlis.indexOf(tliElement);
            
            if ((eIndex<startIndex) || (sIndex>endIndex)){
                fork.detach();
            }
        }
        for (int i=0; i<startIndex; i++){
            ((Element)(tlis.get(i))).detach();
        }
        for (int i=endIndex; i<tlis.size()-1; i++){
            ((Element)(tlis.get(i))).detach();
        }
    }
    
    public static void writeCOMASearchResultListToLocalFile(File file, 
                                                            SearchResultList list, 
                                                            COMACorpus corpus, 
                                                            Vector<String[]> metaIdentifier,
                                                            String pathToCorpus
            ) throws IOException {
            FileIO.writeDocumentToLocalFile(file, COMASearchResultListToXML(list,corpus,metaIdentifier, pathToCorpus));                
    }
    
    
    
    public static void writeDocumentToLocalFile(File file, Document document) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        FileOutputStream fos = new FileOutputStream(file);        
        xmlOutputter.output(document,fos);
        fos.close();    
    }

    public static void writeDocumentToLocalFile(File file, Document document, String encoding) throws IOException{
        XMLOutputter xmlOutputter = new XMLOutputter();
        FileOutputStream fos = new FileOutputStream(file);        
        xmlOutputter.setFormat(xmlOutputter.getFormat().setEncoding(encoding));
        xmlOutputter.output(document,fos);
        fos.close();    
    }

    public static String getPlainTextFromLocalFile(String pathToDocument) throws JDOMException, IOException {
        Document doc = readDocumentFromLocalFile(pathToDocument);
        Element body = doc.getRootElement().getChild("text");
        return getPlainText(body);
    }
    
    public static String getPlainText(Content c){
        if (c instanceof org.jdom.Text){
            return ((org.jdom.Text)c).getText();
        } else if (c instanceof org.jdom.CDATA){
            return (((org.jdom.CDATA)c).getText());                
        } else {
            StringBuffer sb = new StringBuffer();
            Element e = (Element)c;            
            List children = e.getChildren();
            //List children = e.getContent();
            // if this element has element children
            // process just those, else get all the content 
            // - it should be text
            if (children.size()==0){
                children = e.getContent();
            }
            for (int pos=0; pos<children.size(); pos++){
                Object o = children.get(pos);
                Content content = (Content)o;
                sb.append(getPlainText(content));
            }
            return sb.toString();
        }
        
    }
    
    public static void assignStylesheet(String pathToStylesheet, Document doc){
          HashMap piMap = new HashMap( 2 );
          piMap.put( "type", "text/xsl" );
          piMap.put( "href", pathToStylesheet );
          org.jdom.ProcessingInstruction pi = new org.jdom.ProcessingInstruction( "xml-stylesheet", piMap );
          doc.getContent().add( 0, pi );                
    }
}
