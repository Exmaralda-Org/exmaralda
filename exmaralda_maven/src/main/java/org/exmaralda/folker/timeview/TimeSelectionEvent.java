/*
 * TimeSelectionEvent.java
 *
 * Created on 13. Maerz 2008, 15:39
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.timeview;

/**
 *
 * @author thomas
 */
public class TimeSelectionEvent {
    
    /** the whole selection has changed */
    public static final int SELECTION_CHANGED = 0;    
    public static final int SELECTION_CHANGED_WITH_RIGHT_MOUSE_BUTTON = 1;
    public static final int START_TIME_CHANGED = 2;
    public static final int END_TIME_CHANGED = 3;
    public static final int ZOOM_CHANGED = 4;
    public static final int MARK_SET = 5;
    public static final int MARK_REMOVED = 6;
    public static final int SELECTION_DETACHED = 7;
    
    private double startTime;
    private double endTime;
    private int type;
    private boolean selectionAttached;
    
    /** Creates a new instance of TimeSelectionEvent */
    public TimeSelectionEvent(double start, double end, int t, boolean sia) {
        startTime = start;
        endTime = end;
        type = t;
        selectionAttached = sia;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isSelectionAttached() {
        return selectionAttached;
    }

    public void setSelectionAttached(boolean selectionAttached) {
        this.selectionAttached = selectionAttached;
    }
    
    
}
