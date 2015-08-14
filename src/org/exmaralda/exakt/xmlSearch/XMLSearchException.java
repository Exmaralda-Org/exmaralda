/*
 * ExmaraldaSearchException.java
 *
 * Created on 13. November 2006, 16:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.xmlSearch;

/**
 *
 * @author thomas
 */
public class XMLSearchException extends java.lang.Exception {
    
    /** Creates a new instance of ExmaraldaSearchException */
    public XMLSearchException(Exception e) {
        super(e.getMessage());
    }
    
}
