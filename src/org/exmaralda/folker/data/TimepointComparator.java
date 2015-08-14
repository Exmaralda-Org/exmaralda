/*
 * TimelineSorter.java
 *
 * Created on 7. Mai 2008, 14:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

/**
 *
 * @author thomas
 */
public class TimepointComparator implements java.util.Comparator<Timepoint> {
    
    double tolerance;
    
    public TimepointComparator(){
        this(0);
    }
    
    /** Creates a new instance of TimelineSorter */
    public TimepointComparator(double t) {
        tolerance = t;
    }

    public int compare(Timepoint o1, Timepoint o2) {
        if (Math.abs(o1.getTime()-o2.getTime())<tolerance) return 0;
        return Double.compare(o1.getTime(), o2.getTime());
        
    }
    
}
