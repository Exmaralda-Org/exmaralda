/*
 * TimeAssigned.java
 *
 * Created on 23. Juni 2008, 13:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

/**
 *
 * @author thomas
 */
public interface TimeAssigned {
    
    public Timepoint getStartpoint();

    public void setStartpoint(Timepoint startpoint);

    public Timepoint getEndpoint();

    public void setEndpoint(Timepoint endpoint);



}
