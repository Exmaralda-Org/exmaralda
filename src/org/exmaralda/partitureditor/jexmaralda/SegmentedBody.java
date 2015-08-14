/*
 * SegmentedBody.java
 *
 * Created on 2. August 2002, 13:58
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
import org.jdom.Element;
/**
 *
 * @author  Thomas
 * @version 
 */
public class SegmentedBody extends AbstractTierBody implements XMLable {

    /** Creates new SegmentedBody */
    public SegmentedBody() {
    }

    public Object getSegmentChainWithAnnotations(String speakerID, String tliID, String segmentationName){
        // 1. find the right segmentation
        SegmentedTier st = null;
        Segmentation s = null;
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            st = (SegmentedTier)elementAt(pos);
            if (!(st.getSpeaker().equals(speakerID))) continue;
            if (!st.hasSegmentationWithName(segmentationName)) continue;
            s = st.getSegmentationWithName(segmentationName);
        }
        // no segmentation found
        if (s==null) return null;

        // 2. find the right segment chain
        TimedSegment segmentChain = null;
        SegmentList l = s.getAllSegmentsWithName("sc");
        for (int pos=0; pos<l.size(); pos++){
            TimedSegment ts = (TimedSegment)(l.elementAt(pos));
            String matchedID = getCommonTimelineMatch(tliID);
            String startID = ts.getStart();
            String endID = ts.getEnd();
            Timeline tl = getCommonTimeline();
            if ((matchedID.equals(startID)) || (tl.before(startID, matchedID) && (tl.before(matchedID, endID)))){
                segmentChain = ts;
                break;
            }
        }

        // 3. find the corresponding annotations
        // 4. pack the whole thing into something suitable


        return segmentChain;
    }

    public void augmentCommonTimeline(){
        System.out.println("%%%%%%%%%%%%%%%%%% AM AUGMENTING");
        Timeline tl = this.getCommonTimeline();
        for (int pos=0; pos<tl.getNumberOfTimelineItems()-1; pos++){
            String start = tl.getTimelineItemAt(pos).getID();
            String end = tl.getTimelineItemAt(pos+1).getID();
            int tiercount = 0;
            TimelineFork tlf = new TimelineFork();
            int tierposition = -1;
            for (int tierno=0; tierno<getNumberOfTiers(); tierno++){
                SegmentedTier t = (SegmentedTier)(elementAt(tierno));
                if (t.hasTimelineFork(start, end)) {
                    tiercount++;
                    tlf = t.getTimelineFork(start, end);
                    tierposition = tierno;
                }
                if (tiercount>1) {break;}
            }
            if (tiercount==1){
                tl.addAll(pos+1, tlf);
                tl.updatePositions();
                pos+=tlf.getNumberOfTimelineItems();
                ((SegmentedTier)(elementAt(tierposition))).removeTimelineFork(start,end);;
            }
        }
    }
    
    /** returns a hashtable where each tli is assigned the nearest tli on the common timeline */
    public Hashtable makeTLIHashtable(){
        Hashtable result = new Hashtable();
        for (int pos=0; pos<getCommonTimeline().getNumberOfTimelineItems(); pos++){
            TimelineItem tli = getCommonTimeline().getTimelineItemAt(pos);
            result.put(tli.getID(),tli.getID());
        }
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            SegmentedTier st = (SegmentedTier)elementAt(pos);
            Vector tlfs = st.getTimelineForks();
            for (int pos2=0; pos2<tlfs.size(); pos2++){
                TimelineFork tlf = (TimelineFork)(tlfs.elementAt(pos2));
                for (int pos3=0; pos3<tlf.getNumberOfTimelineItems(); pos3++){
                    TimelineItem tli = tlf.getTimelineItemAt(pos3);
                    result.put(tli.getID(), tlf.getStart());
                }
            }
        }
        return result;
    }
    
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtilities.makeXMLOpenElement("segmented-body", null));
        sb.append(StringUtilities.makeXMLOpenElement("common-timeline", null));
        sb.append(getCommonTimeline().toXML());
        sb.append(StringUtilities.makeXMLCloseElement("common-timeline"));
        for (int pos=0; pos<getNumberOfTiers(); pos++){
            sb.append(((XMLable)elementAt(pos)).toXML());
        }
        sb.append(StringUtilities.makeXMLCloseElement("segmented-body"));
        return sb.toString();
    }
    
    public void makeIDs(){
        int idno=0;
        for (int pos=0; pos<size(); pos++){
            SegmentedTier st = (SegmentedTier)(elementAt(pos));
            for (int pos2=0; pos2<st.size(); pos2++){
                Object o = st.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    for (int pos3=0; pos3<s.getNumberOfSegments(); pos3++){
                        Object o2 = s.elementAt(pos3);
                        if (o2 instanceof TimedSegment){
                            TimedSegment ts = (TimedSegment)o2;
                            idno = ts.makeIDs(idno);
                        }
                        else if (o2 instanceof AtomicTimedSegment){
                            AtomicTimedSegment ats = (AtomicTimedSegment)o2;
                            ats.setID("Seg_" + Integer.toString(idno));
                            idno++;
                        }
                    }
                }
                else if (o instanceof Annotation){
                    Annotation a = (Annotation)o;
                    for (int pos3=0; pos3<a.getNumberOfSegments(); pos3++){
                        Object o2 = a.elementAt(pos3);
                        if (o2 instanceof TimedAnnotation){
                            TimedAnnotation ta = (TimedAnnotation)o2;
                            ta.setID("Seg_" + Integer.toString(idno));
                            idno++;
                        }
                    }
                }
            }
        }
    }
    
    /*public String countSegments(Speakertable spkt){
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("-------------------------------" + nl);
        for (int pos=0; pos<size(); pos++){
            SegmentedTier st = (SegmentedTier)(elementAt(pos));
            sb.append("Tier " + st.getDescription(spkt) + nl);
            for (int pos2=0; pos2<st.size(); pos2++){
                Object o = st.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    sb.append(nl + "Segmentation " + s.getName()+ nl);
                    HashSet segmentHashSet = s.getAllSegmentNames();                    
                    for (Iterator i = segmentHashSet.iterator(); i.hasNext(); ){
                       String segmentName = new String((String)i.next());
                       int count = (s.getAllSegmentsWithName(segmentName)).size();
                       sb.append(Integer.toString(count) + " segments of name " + segmentName + nl);
                    }                    
                }
                else if (o instanceof Annotation){
                    Annotation a = (Annotation)o;
                    int count = a.size();
                    sb.append(nl + Integer.toString(count) + " annotations of name " + a.name);
                }
            }
            sb.append(nl + "-------------------------------" + nl);
        }
        return sb.toString();
    }*/
    
    //TODO: Das sieht scheisse aus.
    public String countSegments(Speakertable spkt){
        String nl = System.getProperty("line.separator");
        StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>Count segments</title><style type=\"text/css\">td{vertical-align:top;border:1px solid black;}</style></head><body><table rules=\"all\">" + nl);
        for (int pos=0; pos<size(); pos++){
            SegmentedTier st = (SegmentedTier)(elementAt(pos));
            sb.append("<tr><td valign=\"top\"><b>Tier " + st.getDescription(spkt) + "</b></td>" + nl);
            for (int pos2=0; pos2<st.size(); pos2++){
                Object o = st.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    //sb.append(nl + "Segmentation " + s.getName()+ nl);
                   sb.append("<td valign=\"top\">");
                    HashSet segmentHashSet = s.getAllSegmentNames();
                    for (Iterator i = segmentHashSet.iterator(); i.hasNext(); ){
                       String segmentName = new String((String)i.next());
                       int count = (s.getAllSegmentsWithName(segmentName)).size();
                       sb.append(Integer.toString(count) + "  <i>" + segmentName + "</i><br/>");
                    }
                    sb.append("<b>[" + s.getName() + "]</b><br/>");
                    sb.append("</td>");
                }
                else if (o instanceof Annotation){
                    Annotation a = (Annotation)o;
                    int count = a.size();
                   sb.append("<td valign=\"top\">");
                    sb.append(Integer.toString(count) + " " + a.name);
                   sb.append("<b>[Annotation]</b><br/>");
                    sb.append("</td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append(nl + "</table></body></html>" + nl);
        return sb.toString();
    }
    
    
    public SegmentedTier getSegmentedTierWithID(String id){
        return (SegmentedTier)(elementAt(lookupID(id)));
    }
    
    public String getCommonTimelineMatch(String timeID){
        if (getCommonTimeline().containsTimelineItemWithID(timeID)){
            return timeID;
        }
        return (String)(makeTLIHashtable().get(timeID));
    }

    public Vector<Element> getWordList(){
        Vector<Element> result = new Vector<Element>();
        for (int pos=0; pos<size(); pos++){
            SegmentedTier st = (SegmentedTier)(elementAt(pos));
            for (int pos2=0; pos2<st.size(); pos2++){
                Object o = st.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation s = (Segmentation)o;
                    HashSet segmentHashSet = s.getAllSegmentNames();
                    for (Iterator i = segmentHashSet.iterator(); i.hasNext(); ){
                       String segmentName = new String((String)i.next());
                       if (!(segmentName.endsWith(":w"))) continue;
                       SegmentList thisWordList  = (s.getAllSegmentsWithName(segmentName));
                       for (Object w : thisWordList){
                           TimedSegment word = (TimedSegment)w;
                           Element thisWord = new Element("word");
                           thisWord.setText(word.getDescription());
                           thisWord.setAttribute("start", getCommonTimelineMatch(word.getStart()));
                           // changed 30-03-2010: take care of cases where speaker is null
                           if (st.getSpeaker()!=null){
                            thisWord.setAttribute("speaker", st.getSpeaker());
                           } else {
                            thisWord.setAttribute("speaker", "");
                           }
                           thisWord.setAttribute("tier", s.getTierReference());
                           result.add(thisWord);

                       }
                    }
                }
            }
        }

        return result;
    }
    
   
}
