/*
 * Segmentor.java
 *
 * Created on 6. August 2002, 15:51
 */

package org.exmaralda.partitureditor.jexmaralda;

import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.fsm.FiniteStateMachine;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.fsm.*;
import org.xml.sax.*;
import java.util.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class FSMSegmentor {

    /** Creates new Segmentor */
    public FSMSegmentor() {
    }
    
    public static Vector segment(Segmentation sourceSegmentation, 
                                 Segmentation targetSegmentation, 
                                 FiniteStateMachine fsm, 
                                 String idPrefix) throws FSMException {
        TimedSegmentSaxReader tsr = new TimedSegmentSaxReader();
        Vector timelineForks = new Vector();
        for (int pos=0; pos<sourceSegmentation.size(); pos++){
            Object s = sourceSegmentation.elementAt(pos);
            if (s instanceof TimedSegment){
                TimedSegment seg = (TimedSegment)s;
                try {
                    String parseOutput = fsm.process(seg.getDescription());
                    try {                    
                        TimedSegment ts = tsr.readFromString(parseOutput);
                        ts.setStart(seg.getStart());
                        ts.setEnd(seg.getEnd());
                        ts.timeDown();
                        Vector v = time(seg,ts,idPrefix);
                        targetSegmentation.addSegment(ts);
                        timelineForks.addAll(v);
                    } catch (SAXException se){
                        FSMException fsme = new FSMException("FSM output could not be parsed as timed segment.\n" + se.getMessage(), parseOutput);
                        fsme.setTierID(sourceSegmentation.getTierReference());
                        fsme.setTLI(seg.getStart());
                        throw fsme;
                    }            
                } catch (FSMException fsme){
                    fsme.setTierID(sourceSegmentation.getTierReference());
                    /* changed on 10-01-2005 */
                    //fsme.setTLI(seg.getStart());
                    fsme.setTLI(seg.getTLIByCharacterOffset(StringUtilities.stripXMLElements(fsme.getProcessedOutput()).length()));
                    throw fsme;
                }
            } else if (s instanceof AtomicTimedSegment){
                targetSegmentation.addSegment((AtomicTimedSegment)s);
            }
        }
        return timelineForks;
    }
    
    public static Vector getSegmentationErrors(Segmentation sourceSegmentation, FiniteStateMachine fsm){
        Vector result = new Vector();
        TimedSegmentSaxReader tsr = new TimedSegmentSaxReader();
        for (int pos=0; pos<sourceSegmentation.size(); pos++){
            Object s = sourceSegmentation.elementAt(pos);
            if (s instanceof TimedSegment){
                TimedSegment seg = (TimedSegment)s;
                try {
                    String parseOutput = fsm.process(seg.getDescription());
                    try {                    
                        TimedSegment ts = tsr.readFromString(parseOutput);
                    } catch (SAXException se){
                        FSMException fsme = new FSMException("FSM output could not be parsed as timed segment.\n" + se.getMessage(), parseOutput);
                        System.out.println(parseOutput);
                        fsme.setTierID(sourceSegmentation.getTierReference());
                        fsme.setTLI(seg.getStart());
                        result.addElement(fsme);
                    }            
                } catch (FSMException fsme){
                    fsme.setTierID(sourceSegmentation.getTierReference());
                    fsme.setTLI(seg.getTLIByCharacterOffset(StringUtilities.stripXMLElements(fsme.getProcessedOutput()).length()));
                    result.addElement(fsme);
                }
            }
        }
        return result;                                     
    }
    
    static Vector time (TimedSegment timed, TimedSegment notTimed, String idPrefix){

        Vector timelineForks = new Vector();
        
        // get a mapping from charPos to timeline from the timed segment
        Vector tLeaves = timed.getLeaves();
        Describable d = null;

        // make a vector in which the tlis that can be derived from the timed segment are stored
        Vector tlis = new Vector();
        int cp = 0;
        for (int pos=0; pos<tLeaves.size(); pos++){
            d = (Describable)(tLeaves.elementAt(pos));
            tlis.addElement(new TLICharPos(((Timeable)d).getStart(),cp));
            cp+=d.getDescription().length();
        }
        tlis.addElement(new TLICharPos(((Timeable)d).getEnd(), timed.getDescription().length()));

        // then go through the not-timed segment and assign start points
        Vector ntLeaves = notTimed.getLeaves();
        cp = 0;
        int time = 0;
        int nextCp = 1;
        TimelineFork tlf = new TimelineFork();
        for (int pos=0; pos<ntLeaves.size(); pos++){
            d = (Describable)(ntLeaves.elementAt(pos));
            if (d instanceof Timeable){
                String tli = ((TLICharPos)(tlis.elementAt(nextCp-1))).tli;
                if (time==0){
                    ((Timeable)d).setStart(tli);
                } else {
                    TimelineItem newTLI = new TimelineItem();
                    String id = tli + "." + idPrefix + "." + Integer.toString(time);
                    newTLI.setID(id);
                    try {tlf.addTimelineItem(newTLI);} catch (JexmaraldaException je) {}
                    ((Timeable)d).setStart(id);
                }
                time++;
            }
            cp+=d.getDescription().length();
            if (cp >= ((TLICharPos)(tlis.elementAt(nextCp))).charpos){
                tlf.setStart(((TLICharPos)(tlis.elementAt(nextCp-1))).tli);
                tlf.setEnd(((TLICharPos)(tlis.elementAt(nextCp))).tli);
                if (tlf.getNumberOfTimelineItems()>0){timelineForks.addElement(tlf);}
                tlf = new TimelineFork();
                while ((nextCp < tlis.size()-1) && (cp > ((TLICharPos)(tlis.elementAt(nextCp))).charpos)){
                    nextCp++;
                }
                if (cp == ((TLICharPos)(tlis.elementAt(nextCp))).charpos){time=0;}
                else {time=1;}
                if ((nextCp < tlis.size()-1)
                    && (cp == ((TLICharPos)(tlis.elementAt(nextCp))).charpos)) {
                    nextCp++;
                }
            }
        }
        

        // assign other start points and make timeline forks
        for (int pos=0; pos<ntLeaves.size(); pos++){
            d = (Describable)(ntLeaves.elementAt(pos));
            if (d instanceof Timeable){
                if (((Timeable)d).getStart().length()<=0){
                    ((Timeable)d).setStart("***TLI***");
                }
            }
            cp+=d.getDescription().length();
        }
        
        
        // go through the non-timed segment again and assign end points (derived from the start points)
        Timeable t = null;
        for (int pos=0; pos<ntLeaves.size(); pos++){
            Object s = ntLeaves.elementAt(pos);
            if (s instanceof Timeable){
                if (t!=null){
                    t.setEnd(((Timeable)s).getStart());
                    //t.timeUp();
                }
                t = (Timeable)s; 
                //t.timeUp();
            }
        }
        
        // propagate the times up in the segment tree (changed version 1.2.7.)
        for (int pos=0; pos<ntLeaves.size(); pos++){
            Object s = ntLeaves.elementAt(pos);
            if (s instanceof Timeable){
                ((Timeable)(s)).timeUp();
            }
        }

        return timelineForks;
        
    }
    

}

class TLICharPos {

    public String tli;
    public int charpos;
    
    public TLICharPos(String t, int c){
        tli = t;
        charpos = c;
    }
    
}
