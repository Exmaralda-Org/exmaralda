/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture.undo;

/**
 *
 * @author thomas
 */
public class RestoreTimeInfo {

    public String timelineID;
    public double time;

    public RestoreTimeInfo(String timelineID, double time) {
        this.timelineID = timelineID;
        this.time = time;
    }


}
