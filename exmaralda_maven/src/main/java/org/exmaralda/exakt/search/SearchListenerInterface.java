/*
 * SearchListenerInterface.java
 *
 * Created on 5. Januar 2007, 17:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public interface SearchListenerInterface {
    
    public void processSearchEvent(SearchEvent se);

}
