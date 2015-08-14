/*
 * ConversionInfo.java
 *
 * Created on 2. November 2004, 15:26
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;
/**
 *
 * @author  thomas
 */
public class ConversionInfo {
    
    Timeline timeline;
    Vector tiers;
    
    /** Creates a new instance of ConversionInfo */
    public ConversionInfo() {
        timeline = new Timeline();
        tiers = new Vector();
    }
    
    public void setTimeline(Timeline tl){
        timeline = tl;
    }
    
    public Timeline getTimeline(){
        return timeline;
    }
    
    public void addTierConversionInfo(String[] info){
        tiers.addElement(info);
    }
    
    public Vector getTierConversionInfos(){
        return tiers;
    }
    
    public String toXML(){
        StringBuffer sb = new StringBuffer();
        sb.append("<conversion-info>");
        sb.append("<basic-transcription-conversion-info>");
        sb.append("<conversion-timeline>");
        for (int pos=0; pos<timeline.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = timeline.getTimelineItemAt(pos);
            sb.append("<conversion-tli id=\"" + tli.getID() + "\"/>");
        }
        sb.append("</conversion-timeline>");
        for (int pos=0; pos<tiers.size(); pos++){
            String[] info = (String[])(tiers.elementAt(pos));
            sb.append("<conversion-tier ");
            sb.append("segmented-tier-id=\"" + info[0] + "\" ");
            // bug fix 12-03-2015
            sb.append("name=\"" + StringUtilities.toXMLString(info[1]) + "\" ");
            sb.append("category=\"" + StringUtilities.toXMLString(info[2]) + "\" ");
            sb.append("display-name=\"" + StringUtilities.toXMLString(info[3]) + "\" ");
            sb.append("type=\"" + info[4] + "\"");
            sb.append("/>");
        }
        sb.append("</basic-transcription-conversion-info>");
        sb.append("</conversion-info>");        
        return sb.toString();
    }
    
}
