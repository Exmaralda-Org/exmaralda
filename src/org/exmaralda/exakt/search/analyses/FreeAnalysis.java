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
public class FreeAnalysis extends AbstractAnalysis {
    
    /** Creates a new instance of BinaryAnalysis */
    public FreeAnalysis(String n) {
        super(n);
    }

    public FreeAnalysis(Element e){
        super(e);
    }
    
    
    public Element toXML() {
        Element retValue;
        retValue = super.toXML();
        retValue.setAttribute("type", "Free");        
        return retValue;
    }
    
}
