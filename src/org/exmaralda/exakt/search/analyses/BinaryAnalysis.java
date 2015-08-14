/*
 * BinaryAnalysis.java
 *
 * Created on 18. Juni 2007, 11:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.analyses;

import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class BinaryAnalysis extends AbstractAnalysis {
    
    /** Creates a new instance of BinaryAnalysis */
    public BinaryAnalysis(String n) {
        super(n);
    }

    public BinaryAnalysis(Element e){
        super(e);
    }
    
    
    public Element toXML() {
        Element retValue;
        retValue = super.toXML();
        retValue.setAttribute("type", "Binary");        
        return retValue;
    }
    
}
