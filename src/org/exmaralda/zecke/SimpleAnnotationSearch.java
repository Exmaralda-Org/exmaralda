/*
 * SimpleTranscriptionSearch.java
 *
 * Created on 17. Juni 2004, 10:27
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
import org.exmaralda.partitureditor.jexmaralda.SegmentedBody;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.TimedAnnotation;
import org.exmaralda.partitureditor.jexmaralda.Annotation;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import java.util.regex.*;


/**
 *
 * @author  thomas
 */
public class SimpleAnnotationSearch extends AbstractSearch {
    
    AnnotationSearchResult result = new AnnotationSearchResult();

    /** Creates a new instance of SimpleTranscriptionSearch */
    public SimpleAnnotationSearch(CorpusTree ct) {
        super(ct);
    }
    
    public AbstractSearchResult search(int searchType, String searchExpression) throws Exception {
        long start = new java.util.Date().getTime();
        result.clear();
        Vector allPaths = corpusTree.getSelectedCorpusElements();
        SegmentedTranscriptionSaxReader reader = new SegmentedTranscriptionSaxReader();
        for (int pos=0; pos<allPaths.size(); pos++){
            fireSearchProgressChanged(allPaths.size(), pos);
            CorpusElement ce = (CorpusElement)(allPaths.elementAt(pos));
            System.out.println("Now searching " + ce.getTranscriptionName());
            String path = ce.getTranscriptionPath();
            SegmentedTranscription st = reader.readFromFile(path);
            SegmentedBody body = st.getBody();
            for (int pos2=0; pos2<body.getNumberOfTiers(); pos2++){
                SegmentedTier tier = (SegmentedTier)(body.elementAt(pos2));
                if (!tier.getType().equals("t")) continue;
                AnnotationSearchResult resultsForThisTier = match(path, st, tier, searchExpression);
                result.addAll(resultsForThisTier);
            }
        }
        long end = new java.util.Date().getTime();
        this.timeForLastSearch = (end-start)/1000;
        return result;
    }
    

    AnnotationSearchResult match(String path, SegmentedTranscription st, SegmentedTier tier, String expression){
        AnnotationSearchResult result = new AnnotationSearchResult();
        String transcriptionName = st.getHead().getMetaInformation().getTranscriptionName();
        String tierID = tier.getID();
        String speakerID = tier.getSpeaker();
        String speakerAbb = "";
        if (speakerID!=null){
            try{
                speakerAbb = st.getHead().getSpeakertable().getSpeakerWithID(speakerID).getAbbreviation();
            } catch (JexmaraldaException je){}
        }
        
        Pattern pattern = Pattern.compile(expression);

        // get matching annotations
        Vector matchingAnnotations = new Vector();        
        for (int pos=0; pos<tier.size(); pos++){
            Object o = tier.elementAt(pos);
            if (!(o instanceof Annotation)) continue;
            Annotation a = (Annotation)o;
            String annotationName = a.getName();
            for (int annotationCount=0;  annotationCount < a.getNumberOfSegments(); annotationCount++){
                Object o2 = a.elementAt(annotationCount);
                if (!(o2 instanceof TimedAnnotation)) continue;
                TimedAnnotation ta = (TimedAnnotation)o2;
                Matcher matcher = pattern.matcher(ta.getDescription());
                matcher.reset();
                while (matcher.find()){
                    int start = matcher.start();
                    int end = matcher.end();
                    Object[] matchThingy = {annotationName, ta, new Integer(start), new Integer(end - start)};
                    matchingAnnotations.addElement(matchThingy);
                }
            }
        }
        
        // preparation (make an index)
        Vector mappings = new Vector();
        for (int pos=0; pos<tier.size(); pos++){
            Object o = tier.elementAt(pos);
            if (!(o instanceof Segmentation)) continue;
            Segmentation s = (Segmentation)o;
            Hashtable mapping = new Hashtable();
            for (int segmentCount=0; segmentCount<s.getNumberOfSegments(); segmentCount++){
                Object o2 = s.elementAt(segmentCount);
                if (!(o2 instanceof TimedSegment)) continue;
                TimedSegment ts = (TimedSegment)o2;
                mapping.putAll(ts.indexTLIs());
            }
            mappings.addElement(mapping);                
        }
        
        // get corresponding timed segments
        for (int pos=0; pos<matchingAnnotations.size(); pos++){
            Object[] matchThingy = ((Object[])(matchingAnnotations.elementAt(pos)));
            TimedAnnotation annotation = (TimedAnnotation)(matchThingy[1]);
            String[] tlis = {annotation.getStart(), annotation.getEnd()};
            TimedSegment correspondingTimedSegment = null;
            for (int i=0; i<mappings.size();i++){
                Hashtable mapping = (Hashtable)(mappings.elementAt(i));
                if (mapping.containsKey(tlis)){
                    correspondingTimedSegment = (TimedSegment)(mapping.get(tlis));
                    break;
                } else if (mapping.containsKey(annotation.getStart()) && mapping.containsKey(annotation.getEnd())){
                    TimedSegment ts1 = (TimedSegment)(mapping.get(annotation.getStart()));
                    TimedSegment ts2 = (TimedSegment)(mapping.get(annotation.getEnd()));
                    if (ts1==ts2){
                        correspondingTimedSegment = ts1;
                        break;
                    }
                }
            }

            if (correspondingTimedSegment==null) continue;

            AnnotationSearchResultItem asri =
                     new AnnotationSearchResultItem(transcriptionName,
                                                    path, 
                                                    tierID, 
                                                    speakerID, 
                                                    speakerAbb, 
                                                    correspondingTimedSegment, 
                                                    (TimedAnnotation)(matchThingy[1]), 
                                                    ((Integer)(matchThingy[2])).intValue(), 
                                                    ((Integer)(matchThingy[3])).intValue(), 
                                                    (String)(matchThingy[0])
                                                    );
            result.addElement(asri);
        }
        
        
        
        return result;
    }
    
    

    public void fireSearchProgressChanged(int i1, int i2) {
         // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==SearchListener.class) {
                ((SearchListener)listeners[i+1]).searchProgressChanged(i1, i2, null);
            }
         }        
    }
    
}
