/*
 * PraatPanelEvent.java
 *
 * Created on 17. Mai 2004, 12:02
 */

package org.exmaralda.partitureditor.praatPanel;

/**
 *
 * @author  thomas
 */
public class PraatPanelEvent extends java.awt.AWTEvent {
    
    public static final double SELECT_NEXT_TLI = -7.4637892478329467832;
    public static final double SELECT_PREVIOUS_TLI = -8.46378257483957483;
    public static final double LINK_SNAPSHOT = -9.4563254632;
    public static final double LINK_AUDIO_SNIPPET = -10.463278436;
    public static final double PLAYBACK_MODE_ON = -11.4637284673826;
    public static final double PLAYBACK_MODE_OFF = -14.4673826;
    public static final double SOUND_FILE_SELECTION_CHANGED = -13.1313131313;
    
    double time;
    int index;
    
    /** Creates a new instance of PraatPanelEvent */
    public PraatPanelEvent(double t) {
        super(new Object(), 0);
        time = t;
    }
    
    public PraatPanelEvent(double t, int i) {
        super(new Object(), 0);
        time = t;
        index = i;
    }

    public double getTime(){
        return time;
    }

    public int getIndex(){
        return index;
    }
    
}
