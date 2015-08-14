/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.jdom.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.net.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractBasicTranscriptionChecker {
    
   Document errorsDocument;
   Element errors;

    public AbstractBasicTranscriptionChecker() {
        errorsDocument = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeEmptyDocument();
        errors = errorsDocument.getRootElement().getChild("errors");
    }
    
    public void output(String errorDocumentName) throws IOException, JDOMException, URISyntaxException{
        org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.writeErrorList(errorsDocument, errorDocumentName);
    }
    
    public void addError(String filename, String tierID, String tli, String text) throws URISyntaxException {
        Element error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(filename, tierID,tli , text);
        errors.addContent(error);                        
    }
    
    public abstract void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, org.xml.sax.SAXException;    
    
    public Element getErrors(){
        return errors;
    }

    public Document getErrorsDocoument() {
        return errorsDocument;
    }


   

}
