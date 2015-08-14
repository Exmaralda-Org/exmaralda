/*
 * Linkable.java
 *
 * Created on 2. August 2002, 11:07
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface Linkable {
    
    /** returns the link medium of this event */
    public String getMedium();
    
    /** sets the link medium of this event to the specified value */
    public void setMedium(String m);
    
    /** returns the link URL of this event */
    public String getURL();
    
    /** sets the link URL of this event to the specified value */
    public void setURL(String u);

}

