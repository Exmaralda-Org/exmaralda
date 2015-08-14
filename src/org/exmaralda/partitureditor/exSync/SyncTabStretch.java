/*
 * SyncTabStretch.java
 *
 * Created on 18. Maerz 2003, 15:19
 */

package org.exmaralda.partitureditor.exSync;

import java.util.*;
/**
 *
 * @author  thomas
 */
public class SyncTabStretch extends Vector implements Comparable {
    
    /** Creates a new instance of SyncTabStretch */
    public SyncTabStretch()  {
    }
    
    public int compareTo(Object obj) {
        Vector v = (Vector)obj;
        if (this.size()<v.size()) return +1;
        else if (this.size()>v.size()) return -1;
        else return 0;
    }
    
}
