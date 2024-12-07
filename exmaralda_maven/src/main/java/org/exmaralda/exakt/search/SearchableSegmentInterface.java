/*
 * SearchableSegmentInterface.java
 *
 * Created on 5. Januar 2007, 17:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.*;

/**
 *
 * @author thomas
 */
public interface SearchableSegmentInterface {
    
    public SearchResultList search(SearchParametersInterface searchParameters);
    
}
