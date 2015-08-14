/*
 * COMAXSLCorpusWalker.java
 *
 * Created on 26. Januar 2007, 15:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import org.jdom.*;
import org.jdom.transform.*;
import java.io.*;
/**
 *
 * @author thomas
 */
public class COMAXSLCorpusWalker extends COMACorpusWalker {
    
    XSLTransformer transformer; 
    Document transformResult;
    
    /** Creates a new instance of COMAXSLCorpusWalker */
    public COMAXSLCorpusWalker(String rootElementName) {
        transformResult = new Document();
        transformResult.setRootElement(new Element(rootElementName));
    }

    public void readStylesheet(File stylesheet) throws JDOMException{
        transformer = new XSLTransformer(stylesheet);
    }
    
    public void processTranscription(Document document) throws JDOMException {
        Document thisResult = transformer.transform(document);
        Element communication = currentNode.getParentElement().getParentElement();
        Element transcription = currentNode.getParentElement();
        thisResult.getRootElement().setAttribute("communication-id", communication.getAttributeValue("Id"));
        thisResult.getRootElement().setAttribute("communication-name", communication.getAttributeValue("Name"));
        thisResult.getRootElement().setAttribute("transcription-name", transcription.getChild("Name").getText());
        thisResult.getRootElement().setAttribute("transcription-file", this.currentFilename.substring(currentFilename.lastIndexOf("/")));
        transformResult.getRootElement().addContent(thisResult.getRootElement().detach());
    }
    
    public Document getResultDocument(){
        return transformResult;
    }
    
    
    
    
}
