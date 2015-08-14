/*
 * AnalysisInterface.java
 *
 * Created on 15. Juni 2007, 14:59
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
public interface AnalysisInterface {    
    
    public String getName();
    
    public Element toXML();

}
