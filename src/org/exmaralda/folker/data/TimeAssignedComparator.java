/*
 * EventComparator.java
 *
 * Created on 7. Mai 2008, 16:31
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

/**
 *
 * @author thomas
 */
public class TimeAssignedComparator  implements java.util.Comparator<TimeAssigned> {
    
    TimepointComparator timepointComparator = new TimepointComparator(0);
    
    /** Creates a new instance of EventComparator */
    public TimeAssignedComparator() {
    }

    public int compare(TimeAssigned o1, TimeAssigned o2) {
        
        int startComparison = timepointComparator.compare(o1.getStartpoint(), o2.getStartpoint());
        if (startComparison!=0) return startComparison;
        
        int endComparison = timepointComparator.compare(o1.getEndpoint(), o2.getEndpoint());
        if (endComparison!=0) return -endComparison;
        
        return 0;
    }
    
}
