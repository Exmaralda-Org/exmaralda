/*
 * SyncTabs.java
 *
 * Created on 11. April 2002, 11:59
 */

package org.exmaralda.partitureditor.exSync;

import java.util.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public class SyncTabs extends Vector {

    /** creates new SyncTabs */
    public SyncTabs(){
        super();
    }
    
    public void addSyncTab(SyncTab st){
        addElement(st);
    }
    
    public SyncTab getSyncTabAt(int position){
        return (SyncTab)elementAt(position);
    }

}

