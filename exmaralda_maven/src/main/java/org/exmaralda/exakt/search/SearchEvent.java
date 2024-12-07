/*
 * SearchEvent.java
 *
 * Created on 8. Januar 2007, 10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.search;

/**
 *
 * @author thomas
 */
public class SearchEvent {
    
    public static final short CORPUS_INIT_PROGRESS = 0; 
    public static final short SEARCH_PROGRESS_CHANGED = 1;
    public static final short SEARCH_STOPPED = 2;
    public static final short SEARCH_COMPLETED = 3;
    
    private short type;
    private double progress;
    private Object data;
    
    /** Creates a new instance of SearchEvent */
    public SearchEvent(short t, double p, Object d) {
        setType(t);
        setProgress(p);
        setData(d);
    }
    
    public short getType(){
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    

    
}
