package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
/*
 * AbstractBody.java
 *
 * Created on 12. April 2001, 10:47
 */



/**
 * Abstract parent class of BasicBody, SegmentedBody and ListBody 
 * contains the common timeline and the hastable for keeping 
 * track of tier positions
 * @author  Thomas
 * @version 
 */
public abstract class AbstractBody extends Vector {

    Timeline commonTimeline;
    Hashtable positions;
    
    // ********************************************
    // ********** CONSTRUCTORS ********************
    // ********************************************

    /** Creates new AbstractBody */
    public AbstractBody() {
        super();
        commonTimeline = new Timeline();
        positions=new Hashtable();        
    }
    

    // ********************************************
    // ********** GET AND SET METHODS *************
    // ********************************************

    /** returns the common timeline */
    public Timeline getCommonTimeline(){
        return commonTimeline;
    }
    
    /** sets the common timeline to the specified value */
    public void setCommonTimeline(Timeline tl){
        commonTimeline = tl;
    }

}