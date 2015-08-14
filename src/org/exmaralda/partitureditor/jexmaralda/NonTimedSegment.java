/*
 * NonTimedSegment.java
 *
 * Created on 1. August 2002, 14:28
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
public class NonTimedSegment extends AbstractSegment implements XMLable {

    /** Creates new NonTimedSegment */
    public NonTimedSegment() {
    }

    public AbstractSegment makeCopy() {
        NonTimedSegment result = new NonTimedSegment();
        result.setDescription(this.getDescription());
        return result;                
    }

    public String toXML() {
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"n", getName()},
                                {"id", getID()}};
        sb.append(StringUtilities.makeXMLOpenElement("nts", atts));
        sb.append(StringUtilities.checkCDATA(getDescription()));
        sb.append(StringUtilities.makeXMLCloseElement("nts"));
        return sb.toString();        
    }
    
    
}
