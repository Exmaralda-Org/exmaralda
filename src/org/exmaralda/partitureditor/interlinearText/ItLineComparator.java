/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.interlinearText;

/**
 *
 * @author thomas
 */
public class ItLineComparator implements java.util.Comparator<ItLine> {

    SyncPoints syncPoints;
    
    public ItLineComparator(SyncPoints sp) {
        syncPoints = sp;
    }
    
    
    public int compare(ItLine o1, ItLine o2) {
        int start1 = syncPoints.indexOf(o1.getItChunkAt(0).getStart());
        int start2 = syncPoints.indexOf(o2.getItChunkAt(0).getStart());
        return new Integer(start1).compareTo(new Integer(start2));
    }
    
}
