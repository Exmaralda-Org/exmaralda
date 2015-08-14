/*
 * AbstractAnalysis.java
 *
 * Created on 15. Juni 2007, 15:01
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search.analyses;

import org.jdom.*;

/**
 *
 * @author thomas
 */
public abstract class AbstractAnalysis implements AnalysisInterface {
    
    String name;
    
    /** Creates a new instance of AbstractAnalysis */
    public AbstractAnalysis(String n) {
        name = n;
    }
    
    public AbstractAnalysis(Element e){
        name = e.getAttributeValue("name");
    }
    
    public String getName(){
        return name;
    }
    
    public Element toXML(){
        Element e = new Element("analysis");
        e.setAttribute("name", getName());
        return e;
    }
    
}
