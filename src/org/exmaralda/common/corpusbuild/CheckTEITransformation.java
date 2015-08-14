/*
 * WordCount.java
 *
 * Created on 12. Oktober 2006, 12:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.transform.XSLTransformException;
import org.xml.sax.*;
import java.io.*;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public class CheckTEITransformation {
    
   Document errorsDocument;
   Element errors;
   TEIMerger teiMerger;
   
   /** Creates a new instance of WordCount */
    public CheckTEITransformation() {
        errorsDocument = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeEmptyDocument();
        errors = errorsDocument.getRootElement().getChild("errors");
        try {
            teiMerger = new TEIMerger();
        } catch (XSLTransformException ex) {
            Logger.getLogger(CheckTEITransformation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public void processTranscription(Document st, String currentFilename) throws SAXException, URISyntaxException  {
        try {
            teiMerger.SegmentedTranscriptionToTEITranscription(st, "SpeakerContribution_Utterance_Word", "SpeakerContribution_Event");
        } catch (XSLTransformException ex) {
            Logger.getLogger(CheckTEITransformation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JDOMException ex) {
            Logger.getLogger(CheckTEITransformation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CheckTEITransformation.class.getName()).log(Level.SEVERE, null, ex);
            String message = ex.getMessage();
            String[] m = message.split(" ");
            Element error;
            String text = "TEI transformation error";
            error = org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.makeError(currentFilename, m[0], m[1], text);
            errors.addContent(error);
            
            
        }
    }
    
    public void output(String errorDocumentName) throws IOException, JDOMException, URISyntaxException{
        org.exmaralda.partitureditor.jexmaralda.errorChecker.Utilities.writeErrorList(errorsDocument, errorDocumentName);
    }
    
    
}
