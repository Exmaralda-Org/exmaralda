/*
 * SegmentCountForMetaInformation.java
 *
 * Created on 10. Oktober 2006, 15:57
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.jexmaralda.segment;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
/**
 *
 * @author thomas
 */
public class SegmentCountForMetaInformation {
    
    /** Creates a new instance of SegmentCountForMetaInformation */
    public SegmentCountForMetaInformation() {
    }
    
    public static void count(SegmentedTranscription st){
        SegmentedBody sb = st.getBody();
        Speakertable spkt = st.getHead().getSpeakertable();
        MetaInformation mi = st.getHead().getMetaInformation();
        Hashtable allSegmentCounts = new Hashtable();
        for (int pos=0; pos<sb.size(); pos++){
            SegmentedTier tier = (SegmentedTier)(sb.elementAt(pos));
            System.out.println("Tier " + tier.getDescription(spkt));
            for (int pos2=0; pos2<tier.size(); pos2++){
                Object o = tier.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    System.out.println("Segmentation " + s.getName());
                    HashSet segmentHashSet = s.getAllSegmentNames();                    
                    for (Iterator i = segmentHashSet.iterator(); i.hasNext(); ){
                       String segmentName = new String((String)i.next());
                       int count = (s.getAllSegmentsWithName(segmentName)).size();
                       if (!allSegmentCounts.containsKey(segmentName)){
                           allSegmentCounts.put(segmentName, new Integer(0));
                       }
                       Integer c = (Integer)(allSegmentCounts.get(segmentName));
                       allSegmentCounts.put (segmentName,new Integer(c.intValue()+count));
                    }                    
                }
            }
        }
        
        Enumeration e = allSegmentCounts.keys();
        while (e.hasMoreElements()){
            String segmentName = (String)(e.nextElement());
            Integer count = (Integer)(allSegmentCounts.get(segmentName));
            mi.getUDMetaInformation().setAttribute("# " + segmentName, count.toString());
        }        
    }
    
}
