/*
 * SegmentedTranscription.java
 *
 * Created on 6. August 2002, 11:25
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.io.*;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;

/**
 *
 * @author  Thomas
 * @version 
 */
public class SegmentedTranscription extends AbstractTranscription implements XMLable {

    private SegmentedBody body;
    private ConversionInfo conversionInfo;
    
    /** Creates new SegmentedTranscription */
    public SegmentedTranscription() {
        body = new SegmentedBody();
    }
    
    public SegmentedBody getBody(){
        return body;
    }
    
    public void setBody(SegmentedBody sb){
        body = sb;
    }
    
    public ConversionInfo getConversionInfo(){
        return conversionInfo;
    }
    
    public void setConversionInfo(ConversionInfo ci){
        conversionInfo = ci;
    }
    
    public String toXML(){
        StringBuffer sb=new StringBuffer();
        sb.append(StringUtilities.makeXMLOpenElement("segmented-transcription", null) + "\n");
        sb.append(super.toXML());
        sb.append(getBody().toXML());
        sb.append(getConversionInfo().toXML());
        sb.append(StringUtilities.makeXMLCloseElement("segmented-transcription") + "\n");
        return sb.toString();
    }
    
    /** writes an XML file with the specified file name and the specified path to
     *  the dtd */
    public void writeXMLToFile(String filename, String pathToDTD) throws IOException {
        // changed 11-05-2010: new method can also produce relative paths that
        // go via parent folders, i.e. including one or more ..s
        getHead().getMetaInformation().relativizeReferencedFile(filename, MetaInformation.NEW_METHOD);
        // change rolled back, can't find a decent method to resolve such a path
        // 13-08-2010: change reintroduced
        //getHead().getMetaInformation().relativizeReferencedFile(filename);
        relativizeLinks(filename);
        System.out.println("started writing document" + filename + "...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        fos.write(StringConstants.XML_HEADER.getBytes("UTF-8"));        
        fos.write(StringUtilities.makeXMLDoctypeSegmentedTranscription(pathToDTD).getBytes("UTF-8"));
        fos.write(StringConstants.XML_COPYRIGHT_COMMENT.getBytes("UTF-8"));
        fos.write(toXML().getBytes("UTF-8"));
        fos.close();
        System.out.println("document written.");
        // ".." in relative paths allowed now
        getHead().getMetaInformation().resolveReferencedFile(filename, MetaInformation.NEW_METHOD);
        resolveLinks(filename);        
    }

    public ListTranscription toListTranscription(SegmentedToListInfo info){
        return toListTranscription(info,true);
    }
    
    public ListTranscription toListTranscription(SegmentedToListInfo info, boolean augment){
        ListTranscription result = new ListTranscription();
        result.setHead(this.getHead());
        // Added for version 1.2.5.
        // made optional for version 1.4.6.
        if (augment){
            getBody().augmentCommonTimeline();
        }
        Hashtable TLIMapping = this.getBody().makeTLIHashtable();
        ///////////////////
        result.getBody().setCommonTimeline(this.getBody().getCommonTimeline());
        String[] speakerIDs = getHead().getSpeakertable().getAllSpeakerIDs();
        for (int pos=0; pos<speakerIDs.length; pos++){
            String sp = speakerIDs[pos];
            String[] main = info.getMainForSpeaker(sp);

            if (main!=null){
                String tierID = main[0];
                String segmentationName = main[1];
                String segmentName = main[2];
                
                Vector d = info.getDependentsForSpeaker(sp);
                //System.out.println(d.size() + " dependents to check for speaker " + sp);
                Vector a = info.getAnnotationsForSpeaker(sp);
                
                try{
                    SegmentedTier st = (SegmentedTier)(getBody().getAbstractTierWithID(tierID));
                    Segmentation seg = st.getSegmentationWithName(segmentationName);
                    Vector v = seg.getAllSegmentsWithName(segmentName);

                    for (int pos2=0; pos2<v.size(); pos2++){
                        TimedSegment ts = (TimedSegment)(v.elementAt(pos2));
                        SpeakerContribution sc = new SpeakerContribution();
                        sc.setTierReference(seg.getTierReference());
                        sc.setSpeaker(sp);
                        sc.setMain(ts);
                        
                        for (int pos3=0; pos3<d.size(); pos3++){
                            String[] dep = (String[])(d.elementAt(pos3));
                            String dTierID = dep[0];
                            String dSegmentationName = dep[1];
                            SegmentedTier dSt = (SegmentedTier)(getBody().getAbstractTierWithID(dTierID));
                            Segmentation dSeg = dSt.getSegmentationWithName(dSegmentationName);
                            AbstractSegmentVector partOfdSeg = dSeg.getSegments(getBody().getCommonTimeline(),TLIMapping, ts.getStart(), ts.getEnd());
                            partOfdSeg.setTierReference(dSeg.getTierReference());
                            if (partOfdSeg.size()>0){
                                sc.addDependent(partOfdSeg);
                            }
                        }
                        
                        for (int pos4=0; pos4<a.size(); pos4++){
                            String[] ann = (String[])(a.elementAt(pos4));
                            String aTierID = ann[0];
                            String aSegmentationName = ann[1];
                            SegmentedTier aSt = (SegmentedTier)(getBody().getAbstractTierWithID(aTierID));
                            Annotation aSeg = aSt.getAnnotationWithName(aSegmentationName);
                            AbstractSegmentVector partOfaSeg = aSeg.getSegments(getBody().getCommonTimeline(), TLIMapping, ts.getStart(), ts.getEnd());
                            partOfaSeg.setTierReference(aSeg.getTierReference());
                            if (partOfaSeg.size()>0){
                                sc.addAnnotation(partOfaSeg);
                            }
                        }

                        result.getBody().addSpeakerContribution(sc);                    
                    }
                } catch (JexmaraldaException je){
                    je.printStackTrace();
                }
            }            
        }

        // added 30-08-2010: need to take care of d tiers *without* speakers
        // because the above only handles speaker assigned tiers, example:
        /*<segmented-tier id="TIE9" category="e" type="d" display-name="[e]">
            <segmentation name="Event" tierref="TIE9">
                <ats n="e" id="TIE9.e0" s="T5" e="T6">background noise</ats>
                <ats n="e" id="TIE9.e1" s="T15" e="T16"><![CDATA[<dur=1>]]></ats>
                <ats n="e" id="TIE9.e2" s="T28" e="T29"><![CDATA[<dur=3>]]></ats>
                <ats n="e" id="TIE9.e3" s="T40" e="T41"><![CDATA[<dur=2>]]></ats>
            </segmentation>
        </segmented-tier>*/

        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            SegmentedTier tier = (SegmentedTier)(getBody().elementAt(pos));
            if ((tier.getSpeaker()!=null) || (!("d".equals(tier.getType())))){
                continue;
            }
            Segmentation segmentation = tier.getSegmentationWithName("Event");
            for (Object o : segmentation){
                if (!(o instanceof AtomicTimedSegment)) continue;
                AtomicTimedSegment ats = (AtomicTimedSegment)o;
                SpeakerContribution sc = new SpeakerContribution();
                sc.setTierReference(tier.getID());
                sc.setSpeaker(null);
                /* <speaker-contribution speaker="SPK1" tierref="TIE6">
                    <main>
                        <ts n="sc" id="TIE6.sc10" s="T57" e="T58">
                            <ts n="e" id="TIE6.e14" s="T57" e="T58">you didn't talk about the book</ts>
                        </ts>
                    </main> */
                TimedSegment ts = new TimedSegment();
                ts.setStart(ats.getStart());
                ts.setEnd(ats.getEnd());
                ts.setName("sc-d");
                ts.setID("sc-d." + tier.getID() + "." + ats.getID());
                ts.add(ats);
                sc.setMain(ts);
                result.getBody().addSpeakerContribution(sc);
            }
        }


        return result;                
    }
    
    public SegmentList makeSegmentList(String segmentName){
        SegmentList result = new SegmentList();
        for (int pos=0; pos<getBody().getNumberOfTiers(); pos++){
            SegmentedTier t = (SegmentedTier)(getBody().elementAt(pos));
            for (int pos2=0; pos2<t.size(); pos2++){
                if (t.elementAt(pos2) instanceof Segmentation){
                    Segmentation seg = (Segmentation)(t.elementAt(pos2));
                    result.addAll(seg.getAllSegmentsWithName(segmentName));
                }
            }
        }
        result.setTlis(getBody().makeTLIHashtable());
        return result;
    }
    
    public BasicTranscription toBasicTranscription() throws JexmaraldaException {
        if (conversionInfo==null){
            throw new JexmaraldaException(99,"No conversion info");
        }
        BasicTranscription result = new BasicTranscription();
        result.setHead(this.getHead());
        Timeline tl = new Timeline();
        for (int pos=0; pos<conversionInfo.getTimeline().getNumberOfTimelineItems(); pos++){
            String id = conversionInfo.getTimeline().getTimelineItemAt(pos).getID();
            TimelineItem tli = this.getBody().getCommonTimeline().getTimelineItemWithID(id);
            tl.addTimelineItem(tli);
        }
        result.getBody().setCommonTimeline(tl);
        HashSet<String> processedIDs = new HashSet<String>();
        for (int pos=0; pos<conversionInfo.getTierConversionInfos().size(); pos++){
            String[] info = (String[])(conversionInfo.getTierConversionInfos().elementAt(pos));
            SegmentedTier st = getBody().getSegmentedTierWithID(info[0]);
            // changed to catch an error..., 20-03-2008
            Tier basicTier = st.toBasicTier(info, processedIDs);
            /*if (result.getBody().containsTierWithID(basicTier.getID())){
                basicTier.setID(result.getBody().getFreeID());
            }*/
            result.getBody().addTier(basicTier);
        }
        
        return result;
    }
    
    public void relativizeLinks(String relativeToWhat){
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            SegmentedTier t = (SegmentedTier)(body.elementAt(pos));
            for (int pos2=0; pos2<t.size(); pos2++){
                Object o = t.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation seg = (Segmentation)o;
                    for (int pos3=0; pos3<seg.size(); pos3++){
                        Object o2 = seg.elementAt(pos3);
                        if (o2 instanceof AbstractSegment){
                            ((AbstractSegment)o2).relativizeLink(relativeToWhat);
                        }
                    }
                }
            }
        }
    }

    public void resolveLinks(String relativeToWhat){
        for (int pos=0; pos<body.getNumberOfTiers(); pos++){
            SegmentedTier t = (SegmentedTier)(body.elementAt(pos));
            for (int pos2=0; pos2<t.size(); pos2++){
                Object o = t.elementAt(pos2);
                if (o instanceof Segmentation){
                    Segmentation seg = (Segmentation)o;
                    for (int pos3=0; pos3<seg.size(); pos3++){
                        Object o2 = seg.elementAt(pos3);
                        if (o2 instanceof AbstractSegment){
                            ((AbstractSegment)o2).resolveLink(relativeToWhat);
                        }
                    }
                }
            }
        }
    }

    public void setEXBSource(String filename) {
        getHead().getMetaInformation().getUDMetaInformation().setAttribute("# EXB-SOURCE", filename);
    }

}
