/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;


import org.jdom.xpath.*;
import java.util.*;
import org.jdom.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractXPathErrorFinder extends AbstractErrorFinder {
    
    
    public abstract XPath getXPath();
    
    public abstract String getErrorMessage();

    @Override
    public void processTranscription(Document transcription) throws JDOMException {
        List errorIUs = getXPath().selectNodes(transcription);
        for (Object o : errorIUs){
            Element e = (Element)o;
            addError(Integer.toString(currentTranscriptionNumber),e.getAttributeValue("line"), getErrorMessage());
        }
    }

    
    
                 

}
