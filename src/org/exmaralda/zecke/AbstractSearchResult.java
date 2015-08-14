/*
 * AbstractSearchResult.java
 *
 * Created on 17. Juni 2004, 10:26
 */

package org.exmaralda.zecke;

import java.util.*;
/**
 *
 * @author  thomas
 */
public abstract class AbstractSearchResult extends Vector implements XMLable{
    
    /** Creates a new instance of AbstractSearchResult */
    public AbstractSearchResult() {
    }
    
    public abstract String toXML();
    
}
