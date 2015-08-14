/*
 * Timeable.java
 *
 * Created on 1. August 2002, 14:15
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public interface Timeable {
    
    public String getStart();
    public void setStart(String s);
    public String getEnd();
    public void setEnd(String e);
    public Hashtable indexTLIs();
    public boolean isTimed();
    void timeUp();

}

