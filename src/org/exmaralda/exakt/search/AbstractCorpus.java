/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

import java.util.Vector;

/**
 *
 * @author thomas
 */
public abstract class AbstractCorpus {

    private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();
    private int numberOfSegments = 0;
    public int counter = 0;

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.addElement(sli);
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listenerList.elementAt(i).processSearchEvent(se);
        }
    }

    public int getNumberOfSearchableSegments() {
        return numberOfSegments;
    }

    public void setNumberOfSearchableSegments(int numberOfSegments) {
        this.numberOfSegments = numberOfSegments;
    }

    public boolean hasNext() {
        return counter < getNumberOfCorpusComponents();
    }

    public void reset() {
        counter = 0;
    }

    public abstract int getNumberOfCorpusComponents();



}
