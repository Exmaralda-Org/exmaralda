/*
 * XMLElement.java
 *
 * Created on 26. Februar 2002, 09:57
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  thomas.schmidt@uni-hamburg.de
 * @version 1.0.2
 * interface for XML elements providing all the methods for outputting XML
 */
public interface XMLElement {
    
    /** returns a string representing this object in XML */
    public String toXML();
    
    /** writes a string representing this object in XML to the specified output stream*/
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException;

}

