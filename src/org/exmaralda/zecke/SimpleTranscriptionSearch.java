/*
 * SimpleTranscriptionSearch.java
 *
 * Created on 17. Juni 2004, 10:27
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.TimedSegment;
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
public class SimpleTranscriptionSearch extends AbstractSearch {
    

    TranscriptionSearchResult result = new TranscriptionSearchResult();
    
    /** Creates a new instance of SimpleTranscriptionSearch */
    public SimpleTranscriptionSearch(CorpusTree ct) {
        super(ct);
    }
    
    public AbstractSearchResult search(int searchType, String searchExpression) throws Exception {
        return search(searchType, searchExpression, null);
    }

    public AbstractSearchResult search( int searchType, 
                                        String searchExpression,
                                        String[] metaDataKeys) throws Exception {
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
                String tierID = tier.getID();
                String speakerID = tier.getSpeaker();                
                String speakerAbb = "";
                String[][] metaData = null;
                if (speakerID!=null){
                    speakerAbb = st.getHead().getSpeakertable().getSpeakerWithID(speakerID).getAbbreviation();
                    // ******************
                    if (metaDataKeys!=null){
                        System.out.println("***** HERE I GO AGAIN");
                        Speaker speaker = st.getHead().getSpeakertable().getSpeakerWithID(speakerID);
                        metaData = new String[metaDataKeys.length][2];
                        for (int i=0; i<metaDataKeys.length; i++){
                            String key = metaDataKeys[i];
                            System.out.println("*****" + key);
                            String value = speaker.getUDSpeakerInformation().getValueOfAttribute(key);
                            if (value==null) {value = "#undefined";}
                            metaData[i][0] = key;
                            metaData[i][1] = value;
                        }
                    }
                }
                if (!tier.getType().equals("t")) continue;
                Segmentation segmentation = determineSegmentation(tier);
                if (segmentation == null) continue;
                for (int segCount=0; segCount<segmentation.size(); segCount++){
                    Object o = segmentation.elementAt(segCount);
                    if (!(o instanceof TimedSegment)) continue;
                    TimedSegment ts = (TimedSegment)o;
                    result.addAll(
                        match(searchType, searchExpression, ts, 
                              ce.getTranscriptionName(), ce.getTranscriptionPath(), 
                              tierID, speakerID, speakerAbb, metaData)
                              );
                }
            }
        }
        long end = new java.util.Date().getTime();
        this.timeForLastSearch = (end-start)/1000;
        return result;
    }
    
    Vector match(int searchType, String searchExpression, TimedSegment ts, 
                 String transcriptionName, String transcriptionPath,
                 String tierID, String speakerID, String speakerAbb){
        return match(searchType, searchExpression, ts, transcriptionName, transcriptionPath, tierID, speakerID, speakerAbb, null);
    }

    Vector match(int searchType, String searchExpression, TimedSegment ts, 
                 String transcriptionName, String transcriptionPath,
                 String tierID, String speakerID, String speakerAbb, String[][] metaData){
        Vector result = new Vector();
        switch (searchType){
            case AbstractSearch.STRING_SEARCH_TYPE :
                String desc = ts.getDescription();
                int fromIndex = 0;
                while (fromIndex<desc.length()){
                    int matchPosition = desc.indexOf(searchExpression, fromIndex);
                    if (matchPosition>=0){
                        TranscriptionSearchResultItem item = 
                            new TranscriptionSearchResultItem(transcriptionName, transcriptionPath, 
                                                              tierID, speakerID, speakerAbb,   
                                                              ts, matchPosition, searchExpression.length(), metaData);
                        result.addElement(item);
                        fromIndex = matchPosition + 1;
                    } else {
                        break;
                    }
                }
                break;
            case AbstractSearch.REGULAR_EXPRESSION_SEARCH_TYPE :
                Pattern pattern = Pattern.compile(searchExpression);
                Matcher matcher = pattern.matcher(ts.getDescription());
                matcher.reset();
                while (matcher.find()){
                    int start = matcher.start();
                    int end = matcher.end();
                    TranscriptionSearchResultItem item = 
                        new TranscriptionSearchResultItem(transcriptionName, transcriptionPath, 
                                                          tierID, speakerID, speakerAbb,   
                                                          ts, start, end-start, metaData);
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
