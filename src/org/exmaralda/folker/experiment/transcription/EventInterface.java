/*
 * EventInterface.java
 *
 * Created on 20. Maerz 2008, 11:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.experiment.transcription;

import org.jdom.*;
/**
 *
 * @author thomas
 */
public interface EventInterface {
    
    public double getStartTime();
    
    public double getEndTime();
    
    public String getDescription();
    
    public void setDescription(String d);
    
    public String getTierID();
    
    public String getSpeakerID();
    
    public void setSpeakerID(String s);
    
    public String getStartID();
    
    public String getEndID();
    
    public boolean contains(double seconds);
    
    public Element toXMLElement();

}
