/*
 * SegmentedTier.java
 *
 * Created on 2. August 2002, 13:59
 */

package org.exmaralda.partitureditor.jexmaralda;

import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class SegmentedTier extends AbstractTier implements XMLable {

    private Vector timelineForks;

    /** Creates new SegmentedTier */
    public SegmentedTier() {
        timelineForks = new Vector();
    }
    
    public Vector getTimelineForks(){
        return timelineForks;
    }
    
    public void addTimelineFork(TimelineFork tlf){
        timelineForks.addElement(tlf);
    }
        
    public boolean hasTimelineFork(String start, String end){
        for (int pos=0; pos<timelineForks.size(); pos++){
            TimelineFork tlf = (TimelineFork)(timelineForks.elementAt(pos));
            if (tlf.getStart().equals(start) && tlf.getEnd().equals(end)) {return true;}
        }
        return false;
    }
    
    public TimelineFork getTimelineFork(String start, String end){
        for (int pos=0; pos<timelineForks.size(); pos++){
            TimelineFork tlf = (TimelineFork)(timelineForks.elementAt(pos));
            if (tlf.getStart().equals(start) && tlf.getEnd().equals(end)) {return tlf;}
        }
        return null;
    }

    public void removeTimelineFork(String start, String end){
        for (int pos=0; pos<timelineForks.size(); pos++){
            TimelineFork tlf = (TimelineFork)(timelineForks.elementAt(pos));
            if (tlf.getStart().equals(start) && tlf.getEnd().equals(end)) {
                timelineForks.removeElementAt(pos);
                return;
            }
        }
    }

    public boolean hasSegmentationWithName(String name){
        for (int pos=0; pos<size(); pos++){
            if (elementAt(pos) instanceof Segmentation){
                if (((Segmentation)(elementAt(pos))).getName().equals(name)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public Segmentation getSegmentationWithName(String name){
        return getSegmentationWithName(name, new HashSet<String>());
    }
    
    public Segmentation getSegmentationWithName(String name, HashSet<String> idsToIgnore){
        for (int pos=0; pos<size(); pos++){
            if (elementAt(pos) instanceof Segmentation){
                Segmentation s = (Segmentation)(elementAt(pos));
                if (idsToIgnore.contains(s.getTierReference())) continue;
                if (s.getName().equals(name)){
                    return s;
                }
            }
        }
        return null;        
    }

    public Annotation getAnnotationWithName(String name){
        return getAnnotationWithName(name, new HashSet<String>());
    }

    public Annotation getAnnotationWithName(String name, HashSet<String> idsToIgnore){
        for (int pos=0; pos<size(); pos++){
            if (elementAt(pos) instanceof Annotation){
                Annotation a = ((Annotation)(elementAt(pos)));
                if (idsToIgnore.contains(a.getTierReference())) continue;
                if (a.getName().equals(name)){
                    return a;
                }
            }
        }
        return null;
    }

    public Vector<Annotation> getAnnotationsWithName(String name){
        Vector<Annotation> r = new Vector<Annotation>();
        for (int pos=0; pos<size(); pos++){
            if (elementAt(pos) instanceof Annotation){
                if (((Annotation)(elementAt(pos))).getName().equals(name)){
                    r.addElement((Annotation)elementAt(pos));
                }
            }
        }
        return r;
    }

    public Tier toBasicTier(String[] conversionInfo){
        return toBasicTier(conversionInfo, new HashSet<String>());
    }
    
    public Tier toBasicTier(String[] conversionInfo, HashSet<String> processedIDs){
        Tier result = new Tier();
        result.setSpeaker(this.getSpeaker());
        result.setCategory(conversionInfo[2]);
        result.setDisplayName(conversionInfo[3]);
        result.setType(conversionInfo[4]);
        if (conversionInfo[4].equals("t")){
            Segmentation s = getSegmentationWithName(conversionInfo[1], processedIDs);
            result.setID(s.getTierReference());
            for (int pos=0; pos<s.size(); pos++){
                TimedSegment ts = (TimedSegment)s.elementAt(pos);
                Vector leaves = ts.getLeaves();
                for (int i=0; i<leaves.size(); i++){
                    // Here: another rabbit in the pepper
                    // If the Segmented Transcriptions cometh not out 
                    // of the Paryture-Editore, it doeth notte be
                    // guaranteed that we encounter only timed segments here
                    // Atomic Timed-Segments should not be a probleme, butte:
                    // Non-Timed-Segments do notte haf a start and an end poynte
                    // they must need be appended to ye olde eventte that cometh
                    // before them
                    TimedSegment ts2=(TimedSegment)(leaves.elementAt(i));
                    Event newEvent = new Event();
                    newEvent.setStart(ts2.getStart());
                    newEvent.setEnd(ts2.getEnd());
                    newEvent.setDescription(ts2.getDescription());
                    if (ts2.getMedium()!=null) newEvent.setMedium(ts2.getMedium());
                    if (ts2.getURL()!=null) newEvent.setURL(ts2.getURL());
                    result.addEvent(newEvent);
                }
            }
            processedIDs.add(s.getTierReference());
        } else if (conversionInfo[4].equals("d")){
            Segmentation s = getSegmentationWithName(conversionInfo[1], processedIDs);
            result.setID(s.getTierReference());
            for (int pos=0; pos<s.size(); pos++){
                AtomicTimedSegment ts = (AtomicTimedSegment)s.elementAt(pos);
                Event newEvent = new Event();
                newEvent.setStart(ts.getStart());
                newEvent.setEnd(ts.getEnd());
                newEvent.setDescription(ts.getDescription());
                if (ts.getMedium()!=null) newEvent.setMedium(ts.getMedium());
                if (ts.getURL()!=null) newEvent.setURL(ts.getURL());
                result.addEvent(newEvent);
            }
            processedIDs.add(s.getTierReference());
        } else if (conversionInfo[4].equals("a")){
            Annotation a = getAnnotationWithName(conversionInfo[1], processedIDs);
            result.setID(a.getTierReference());
            for (int pos=0; pos<a.size(); pos++){
                TimedAnnotation ts = (TimedAnnotation)a.elementAt(pos);
                Event newEvent = new Event();
                newEvent.setStart(ts.getStart());
                newEvent.setEnd(ts.getEnd());
                newEvent.setDescription(ts.getDescription());
                // ADDED 10-12-2010: convert links
                newEvent.setMedium(ts.getMedium());
                newEvent.setURL(ts.getURL());
                result.addEvent(newEvent);
            }
            processedIDs.add(a.getTierReference());            
        }
        return result;
    }
    
  
    
    public void flatMerge   (String[] namesOfSegmentations,
                             String[] namesOfSegments,
                             String   nameOfResultSegmentation,
                             String   nameOfResultSegments) throws JexmaraldaException {
        if (namesOfSegmentations.length == 0) return;
        if (hasSegmentationWithName(nameOfResultSegmentation)){
            throw new JexmaraldaException(111, "Segmentation " + nameOfResultSegmentation + " already exists.");
        }
        for (int pos=0; pos<namesOfSegmentations.length; pos++){
            if (!hasSegmentationWithName(namesOfSegmentations[pos])){
                throw new JexmaraldaException(111, "Segmentation " + namesOfSegmentations[pos] + " does not exist.");
            }
        }                   
        Segmentation firstSegmentation = getSegmentationWithName(namesOfSegmentations[0]);
        Segmentation newSegmentation = new Segmentation();
        newSegmentation.setName(nameOfResultSegmentation);
        
        for (int pos=0; pos<firstSegmentation.size(); pos++){
            Object o = firstSegmentation.elementAt(pos);
            if (!(o instanceof TimedSegment)){
                throw new JexmaraldaException(111, "Segmentation " + namesOfSegmentations[0] + " contains <ats> or <nts>  on highest level");
            } 
            TimedSegment referenceSegment = (TimedSegment)o;
            String[] startPointMappings = new String[referenceSegment.getDescription().length()+1];
            startPointMappings[0] = referenceSegment.getStart();
            startPointMappings[referenceSegment.getDescription().length()] = referenceSegment.getEnd();
            referenceSegment.mapStartPoints(startPointMappings, namesOfSegments, 0);
            for (int i=1; i<namesOfSegmentations.length; i++){
                Segmentation seg = getSegmentationWithName(namesOfSegmentations[i]);
                // search the corresponding TimedSegment on the other segmentation
                Timeable t = seg.getSegmentAtStartPoint(referenceSegment.getStart());
                if (!(t instanceof TimedSegment)){
                    throw new JexmaraldaException(111, "Segmentation " + namesOfSegmentations[i] + " contains <ats> or <nts> or <ta> on highest level");
                }
                TimedSegment segment = (TimedSegment)t;

                // check if end times also match
                if (!segment.getEnd().equals(referenceSegment.getEnd())){
                    throw new JexmaraldaException(  111, "Start/End Mismatch: " + segment.toXML() + " " + referenceSegment.toXML());
                }            
                // check if text matches
                if (!segment.getDescription().equals(referenceSegment.getDescription())){
                    throw new JexmaraldaException(111, "Text Mismatch: " + segment.toXML() + " " + referenceSegment.toXML());
                }            
                segment.mapStartPoints(startPointMappings, namesOfSegments, 0);                                    
            }
            
            TimedSegment newSegment = new TimedSegment();
            referenceSegment.copyAttributes(newSegment);
            
            Vector spmVector = new Vector();
            for (int j=0; j<startPointMappings.length; j++){
                if (startPointMappings[j]!=null){
                    TLICharPos tlicp = new TLICharPos(startPointMappings[j], j);
                    spmVector.add(tlicp);
                }
            }
            
            String desc = referenceSegment.getDescription();
            for (int j=0; j<spmVector.size()-1; j++){
                TLICharPos start = (TLICharPos)(spmVector.elementAt(j));
                TLICharPos end = (TLICharPos)(spmVector.elementAt(j+1));
                int startIndex = start.charpos;
                int endIndex = end.charpos;
                String startTLI = start.tli;
                String endTLI = end.tli;
                TimedSegment daughterSegment = new TimedSegment();
                daughterSegment.setName(nameOfResultSegments);
                daughterSegment.setStart(startTLI);
                daughterSegment.setEnd(endTLI);
                daughterSegment.setDescription(desc.substring(startIndex, endIndex));
                newSegment.add(daughterSegment);
            }
            newSegmentation.addSegment(newSegment);
        }
        this.addSegmentation(newSegmentation);
    }
    
    public void hierarchicalMerge  (String nameOfUpper, 
                                    String nameOfLower, 
                                    String nameOfResult,
                                    String nameOfMergeMother,
                                    String nameOfMergeChild) throws JexmaraldaException{
       
        if (!hasSegmentationWithName(nameOfUpper)){throw new JexmaraldaException(111, "Segmentation " + nameOfUpper + " does not exist.");}
        if (!hasSegmentationWithName(nameOfLower)){throw new JexmaraldaException(111, "Segmentation " + nameOfLower + " does not exist.");}
        if (hasSegmentationWithName(nameOfResult)){throw new JexmaraldaException(111, "Segmentation " + nameOfResult + " already exists.");}
        Segmentation upper = getSegmentationWithName(nameOfUpper);
        Segmentation lower = getSegmentationWithName(nameOfLower);
        Segmentation result = new Segmentation();
        result.setName(nameOfResult);
        for (int pos=0; pos<upper.size(); pos++){
            // get the TimedSegment on the upper segmentation
            Object o = upper.elementAt(pos);
            if (!(o instanceof TimedSegment)){
                throw new JexmaraldaException(111, "Segmentation " + nameOfUpper + " contains <ats> or <nts> or <ta> on highest level");
            } 
            TimedSegment upperSegment = (TimedSegment)o;
            
            // search the corresponding TimedSegment on the lower segmentation
            Timeable t = lower.getSegmentAtStartPoint(upperSegment.getStart());
            if (!(t instanceof TimedSegment)){
                throw new JexmaraldaException(111, "Segmentation " + nameOfLower + " contains <ats> or <nts> or <ta> on highest level");
            }
            TimedSegment lowerSegment = (TimedSegment)t;
            
            // check if end times also match
            if (!lowerSegment.getEnd().equals(upperSegment.getEnd())){
                throw new JexmaraldaException(  111, "Start/End Mismatch: " + upperSegment.toXML() + " " + lowerSegment.toXML());
            }            
            // check if text matches
            if (!lowerSegment.getDescription().equals(upperSegment.getDescription())){
                throw new JexmaraldaException(111, "Text Mismatch: " + upperSegment.toXML() + " " + lowerSegment.toXML());
            }            
            
            TimedSegment resultSegment = upperSegment.hierarchicalMerge(lowerSegment, nameOfMergeMother, nameOfMergeChild);
            result.addSegment(resultSegment);
        }
        this.addSegmentation(result);
    }
    
    /** returns the tier as an XML element &lt;segmented-tier&gt; as
     *  specified in the corresponding dtd */
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        String [][] atts = {{"id", getID()},
                                {"speaker", getSpeaker()},
                                {"category", getCategory()},
                                {"type", getType()},
                                {"display-name", this.getDisplayName()}
        };
        sb.append(StringUtilities.makeXMLOpenElement("segmented-tier", atts));
        for (int pos=0; pos<timelineForks.size(); pos++){
            sb.append(((TimelineFork)timelineForks.elementAt(pos)).toXML());
        }
        for (int pos=0; pos<size(); pos++){
            sb.append(((XMLable)elementAt(pos)).toXML());
        }
        sb.append(StringUtilities.makeXMLCloseElement("segmented-tier"));
        return sb.toString();               
    }
    
    public void addSegmentation(Segmentation s){
        addElement(s);
    }
    
    public void addAnnotation(Annotation a){
        addElement(a);
    }
    
}

/*class TLICharPos {

    public String tli;
    public int charpos;
    
    public TLICharPos(String t, int c){
        tli = t;
        charpos = c;
    }
    
} */   
