/*
 * SpeakerContributionComparator.java
 *
 * Created on 1. April 2004, 11:32
 */

package org.exmaralda.partitureditor.jexmaralda;

/**
 *
 * @author  thomas
 */
public class SpeakerContributionComparator implements java.util.Comparator {
    
    Timeline timeline = new Timeline();
    boolean shortBeforeLong = false;
    
    /** Creates a new instance of SpeakerContributionComparator */
    public SpeakerContributionComparator(Timeline tl) {
        this(tl,false);
    }
    
    public SpeakerContributionComparator(Timeline tl, boolean sbl) {
        timeline = tl;
        shortBeforeLong = true;
    }

    
    public int compare(Object o1, Object o2) {
        if ((!(o1 instanceof SpeakerContribution)) 
            || (!(o2 instanceof SpeakerContribution))) {return 0;}
        SpeakerContribution s1 = (SpeakerContribution)o1;
        SpeakerContribution s2 = (SpeakerContribution)o2;
        TimedSegment main1 = s1.getMain();
        TimedSegment main2 = s2.getMain();
        int start1 = timeline.lookupID(main1.getStart());
        int start2 = timeline.lookupID(main2.getStart());        
//        System.out.println("Start 1: " + start1 + " Start 2: " + start2);

        if ((start1>=0) && (start2>=0)){    // start points of both scs are in the common timeline
            if (start1>start2) return +1;   // first sc starts after second sc
            if (start1<start2) return -1;   // first sc starts before second sc
            // start1 and start2 are equal
            int end1 = timeline.lookupID(main1.getEnd());
            int end2 = timeline.lookupID(main2.getEnd());
//            System.out.println("End 1: " + end1 + " End 2: " + end2);
            if ((end1>=0) && (end2>=0)) {   // end points of both scs are in the common timeline
                if (end1>end2){
                    if (!shortBeforeLong) return -1;   // first sc is longer than second sc
                    else return 1;
                }
                if (end1<end2){
                    if (!shortBeforeLong) return +1;   // first sc is shorter than second sc
                    else return -1;
                }
                if (end1==end2) return 0;   // start and end of both scs are equal
            }
//            System.out.println("Abnormal endpoints!!!!!");
            if (end1>=0) return -1; // end of 1st sc is in the common timeline, end of 2nd not
            if (end2>=0) return +1; // end of 2nd sc is in the common timeline, end of 1st not
            return 0;   // none of the end points is in the common timeline
        }
//        System.out.println("Abnormal startpoints!!!!!");
        if (start1>=0) return -1; // start of 1st sc is in the common timeline, start of 2nd not
        if (start2>=0) return +1; // start of 2nd sc is in the common timeline, start of 1st not
        return 0;   // none of the start points is in the common timeline
    }
    
}
