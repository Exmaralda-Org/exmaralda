/*
 * Search.java
 *
 * Created on 11. Juni 2004, 14:22
 */

package org.exmaralda.zecke;

import org.exmaralda.partitureditor.jexmaralda.Segmentation;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import java.util.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  thomas
 */
public abstract class AbstractSearch {
    
    public static final int STRING_SEARCH_TYPE = 0;
    public static final int REGULAR_EXPRESSION_SEARCH_TYPE = 1;
    public static final int XPATH_EXPRESSION_SEARCH_TYPE = 2;
    
    double timeForLastSearch;
    
    CorpusTree corpusTree;
    Vector segmentationPreferences = new Vector();
    
    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    
    
    /** Creates a new instance of Search */
    public AbstractSearch(CorpusTree ct) {
        corpusTree = ct;
    }
    
    
    public void addSearchListener(SearchListener l) {
         listenerList.add(SearchListener.class, l);
    }

    
    public double getTimeForLastSearch(){
        return timeForLastSearch;
    }
    
    public void setSegmentationPreferences(Vector sp){
        segmentationPreferences = sp;
    }
    
    public Vector getSegmentationPreferences(){
        return segmentationPreferences;
    }

    public abstract AbstractSearchResult search(int searchType, String searchExpression) throws Exception;
    
    public Segmentation determineSegmentation(SegmentedTier tier){
        for (int pos=0; pos<segmentationPreferences.size(); pos++){
            String name = (String)(segmentationPreferences.elementAt(pos));
            if (tier.hasSegmentationWithName(name)){
                return tier.getSegmentationWithName(name);
            }
        }
        for (int pos=0; pos<tier.size(); pos++){
            Object o = tier.elementAt(pos);
            if (o instanceof Segmentation){
                return (Segmentation)o;
            }
        }
        return null;
    }
    
    public abstract void fireSearchProgressChanged(int i1, int i2);
    

}
