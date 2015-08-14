/*
 * SimpleTranscriptionSearch.java
 *
 * Created on 17. Juni 2004, 10:27
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.AtomicTimedSegment;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedBody;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.xml.sax.*;
import java.util.regex.*;


/**
 *
 * @author  thomas
 */
public class SimpleDescriptionSearch extends AbstractSearch {
    
    /** Creates a new instance of SimpleTranscriptionSearch */
    public SimpleDescriptionSearch(CorpusTree ct) {
        super(ct);
    }
    
    public AbstractSearchResult search(int searchType, String searchExpression) throws Exception {
        long start = new java.util.Date().getTime();
        DescriptionSearchResult result = new DescriptionSearchResult();
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
                String tierID = tier.getID();
                String speakerID = tier.getSpeaker();
                String category = tier.getCategory();
                String speakerAbb = "";
                if (speakerID!=null){
                    speakerAbb = st.getHead().getSpeakertable().getSpeakerWithID(speakerID).getAbbreviation();
                }
                if (!tier.getType().equals("d")) continue;
                Segmentation segmentation = determineSegmentation(tier);
                if (segmentation == null) continue;
                for (int segCount=0; segCount<segmentation.size(); segCount++){
                    Object o = segmentation.elementAt(segCount);
                    if (!(o instanceof AtomicTimedSegment)) continue;
                    AtomicTimedSegment ats = (AtomicTimedSegment)o;
                    result.addAll(
                        match(searchType, searchExpression, ats, 
                              ce.getTranscriptionName(), ce.getTranscriptionPath(), 
                              tierID, speakerID, speakerAbb, category)
                              );
                }
            }
        }
        long end = new java.util.Date().getTime();
        this.timeForLastSearch = (end-start)/1000;
        return result;
    }
    
    Vector match(int searchType, String searchExpression, AtomicTimedSegment ats, 
                 String transcriptionName, String transcriptionPath,
                 String tierID, String speakerID, String speakerAbb, String category){
        Vector result = new Vector();
        switch (searchType){
            /*case AbstractSearch.STRING_SEARCH_TYPE :
                String desc = ts.getDescription();
                int fromIndex = 0;
                while (fromIndex<desc.length()){
                    int matchPosition = desc.indexOf(searchExpression, fromIndex);
                    if (matchPosition>=0){
                        TranscriptionSearchResultItem item = 
                            new TranscriptionSearchResultItem(transcriptionName, transcriptionPath, 
                                                              tierID, speakerID, speakerAbb,   
                                                              ts, matchPosition, searchExpression.length());
                        result.addElement(item);
                        fromIndex = matchPosition + 1;
                    } else {
                        break;
                    }
                }
                break;*/
            case AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE :
                Pattern pattern = Pattern.compile(searchExpression);
                Matcher matcher = pattern.matcher(ats.getDescription());
                matcher.reset();
                while (matcher.find()){
                    int start = matcher.start();
                    int end = matcher.end();
                    DescriptionSearchResultItem item = 
                        new DescriptionSearchResultItem(transcriptionName, transcriptionPath, 
                                                          tierID, speakerID, speakerAbb,   
                                                          ats, start, end-start, category);
                    result.addElement(item);
                }
                break;
            case AbstractSearch.XPATH_EXPRESSION_SEARCH_TYPE :
                break;
            default :
                
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
