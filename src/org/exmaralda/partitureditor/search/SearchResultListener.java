/*
 * SearchResultListener.java
 *
 * Created on 13. Juni 2003, 15:38
 */

package org.exmaralda.partitureditor.search;

import java.util.*;

/**
 *
 * @author  thomas
 */
public interface SearchResultListener extends java.util.EventListener {
    
    public void processSearchResult(EventSearchResult esr);
    
    public void processReplaceResult(EventSearchResult esr, String replaceString);
    
    public void processReplaceAll(Vector resultVector, String searchString, String replaceString);
    
}
