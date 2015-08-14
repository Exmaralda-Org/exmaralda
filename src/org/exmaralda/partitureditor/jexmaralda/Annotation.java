/*
 * Annotation.java
 *
 * Created on 2. August 2002, 14:19
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Annotation extends AbstractSegmentVector implements XMLable{

    /** Creates new Annotation */
    public Annotation() {
    }


    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"name", getName()}, {"tierref", getTierReference()}};
        sb.append(StringUtilities.makeXMLOpenElement("annotation", atts));
        for (int pos=0; pos<getNumberOfSegments(); pos++){
            sb.append(((XMLable)elementAt(pos)).toXML());
        }
        sb.append(StringUtilities.makeXMLCloseElement("annotation"));
        return sb.toString();                
    }
    
}
