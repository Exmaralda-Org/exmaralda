/*
 * PartitureTableEvent.java
 *
 * Created on 4. Dezember 2001, 09:15
 */

package org.exmaralda.partitureditor.partiture;

/**
 * custom extension of AWTEvent
 * solely needed to avert the main frame that the filename has changed
 * @author  Thomas
 * @version 
 */
public class PartitureTableEvent extends java.awt.AWTEvent {

    private Object info;
    
    public static int FILENAME_CHANGED = 1;
    public static int MEDIA_TIME_CHANGED = 2;
    public static int UNDO_CHANGED = 3;
    
    /** Creates new PartitureTableEvent */
    public PartitureTableEvent(Object source, int index, Object o) {
        super(source, index);
        info = o;
    }

    public Object getInfo(){
        return info;
    }

}
