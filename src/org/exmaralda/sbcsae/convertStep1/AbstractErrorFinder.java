/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

import org.jdom.*;
/**
 *
 * @author thomas
 */
public abstract class AbstractErrorFinder extends AbstractTranscriptionProcessor {

    Document errors;

    public AbstractErrorFinder() {
        errors = new Document(new Element("errors"));
    }
    
    public void writeErrorDocument(String path) throws java.io.IOException {
        org.exmaralda.exakt.utilities.FileIO.writeDocumentToLocalFile(path, errors);
    }
    
    public void addError(String transcription, String iu_no, String errorMessage) {
        Element error = new Element("error");
        error.setAttribute("transcription", transcription);
        error.setAttribute("iu_no", iu_no);
        error.setText(errorMessage);
        errors.getRootElement().addContent(error);

    }
    
    


}
