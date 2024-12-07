/*
 * udInformation.java
 *
 * Created on 26. Februar 2002, 11:36
 */

package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author  Thomas
 * @version 
 */
public class UdInformation extends java.util.Properties implements XMLElement {

    /** Creates new udInformation */
    public UdInformation() {
    }

    /** writes a string representing this object in XML to the specified output stream */
    public void writeXML(java.io.FileOutputStream fo) throws java.io.IOException {
        fo.write(toXML().getBytes("UTF-8"));
    }
    
    /** returns a string representing this object in XML  */
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<ud-information>");
        for (java.util.Enumeration e = propertyNames() ; e.hasMoreElements() ;) {
             String propertyName = (String)e.nextElement();
             String propertyValue = getProperty(propertyName);
             buffer.append("<ud-attribute name=\"");
             buffer.append(propertyName);
             buffer.append("\">");
             buffer.append(propertyValue);
             buffer.append("</ud-attribute>");             
        }        
        buffer.append("</ud-information>");
        return buffer.toString();        
    }
    
}
