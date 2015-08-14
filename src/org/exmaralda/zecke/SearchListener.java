/*
 * SearchListener.java
 *
 * Created on 3. November 2004, 17:11
 */

package org.exmaralda.zecke;

/**
 *
 * @author  thomas
 */
public interface SearchListener extends java.util.EventListener {
    
    public void searchProgressChanged(int total, int done, String message);
    
    public void searchMessageChanged(String message);
    
}
