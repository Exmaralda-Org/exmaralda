/*
 * ListBody.java
 *
 * Created on 27. August 2002, 14:37
 */

package org.exmaralda.partitureditor.jexmaralda;

import java.util.*;

/**
 *
 * @author  Thomas
 */
public class ListBody extends AbstractBody implements XMLable {
    
    private Vector timelineForks;
    private Hashtable positions;
    
    /** Creates a new instance of ListBody */
    public ListBody() {
        timelineForks = new Vector();
        positions = new Hashtable();
    }

    public int find(String tierID, String timeID) {
        for (int pos=0; pos<getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution sc = getSpeakerContributionAt(pos);
            String start = sc.getMain().getStart();
            String tier = sc.getTierReference();
            if (tier.equals(tierID) && start.equals(timeID)){
                return pos;
            }
        }
        return 0;
    }
    
    public Vector getTimelineForks(){
        return timelineForks;
    }
    
    public void addTimelineFork(TimelineFork tlf){
        timelineForks.addElement(tlf);
    }
    
    public int getNumberOfSpeakerContributions(){
        return size();
    }
    
    public void addSpeakerContribution(SpeakerContribution sc){
        addElement(sc);
        if (!positions.containsKey(sc.getMain().getStart())){
            positions.put(sc.getMain().getStart(),new Vector());
        }
        Vector v = (Vector)(positions.get(sc.getMain().getStart()));
        v.add(new Integer(size()-1));
    }
    
    public SpeakerContribution getSpeakerContributionAt(int pos){
        return (SpeakerContribution)elementAt(pos);
    }
    
    public void sort(){
        sort(false);
    }
    
    public void sort(boolean shortBeforeLong){
        Collections.sort(this, new SpeakerContributionComparator(getCommonTimeline(), shortBeforeLong));
    }

    public boolean areAllSpeakerContributionsInTheCommonTimeline(Vector scsThatAreNot){
        boolean result= true;
        for (int pos=0; pos<getNumberOfSpeakerContributions(); pos++){
            SpeakerContribution sc = getSpeakerContributionAt(pos);
            int start = getCommonTimeline().lookupID(sc.getMain().getStart());
            int end = getCommonTimeline().lookupID(sc.getMain().getEnd());
            if ((start<0) || (end<0)){
                result = false;
                scsThatAreNot.addElement(sc);
            }
        }
        return result;
    }
    
    public String toXML() {
        //sort();
        StringBuffer sb = new StringBuffer();
        sb.append(StringUtilities.makeXMLOpenElement("list-body", null));
        sb.append(StringUtilities.makeXMLOpenElement("common-timeline", null));
        sb.append(getCommonTimeline().toXML());
        sb.append(StringUtilities.makeXMLCloseElement("common-timeline"));
        for (int pos=0; pos<timelineForks.size(); pos++){
            sb.append(((TimelineFork)timelineForks.elementAt(pos)).toXML());
        }
        for (int pos=0; pos<getNumberOfSpeakerContributions(); pos++){
            sb.append(getSpeakerContributionAt(pos).toXML());
        }
        sb.append(StringUtilities.makeXMLCloseElement("list-body"));
        return sb.toString();
    }
    
    public String toHTML(Speakertable st){
        StringBuffer sb = new StringBuffer();
        sb.append("<table>");
        for (int pos=0; pos<getNumberOfSpeakerContributions(); pos++){
            sb.append(getSpeakerContributionAt(pos).toHTML(st));
        }        
        sb.append("</table>");
        return sb.toString();
    }
    
    private Vector sortLength(Vector v){
        // calculate maxLength
        System.out.print("Input length " + v.size() + "  ");
        int maxLength=0;       
        for (int pos=0; pos<v.size(); pos++){
            int index = ((Integer)(v.elementAt(pos))).intValue();
            SpeakerContribution sc = (SpeakerContribution)(elementAt(index));
            maxLength = Math.max(maxLength, getCommonTimeline().calculateSpan(sc.getMain().getStart(),sc.getMain().getEnd()));
        }
        
        Vector result = new Vector();
        int length = maxLength;
        while ((v.size()>0) && (length>=0)){
            for (int pos=0; pos<v.size(); pos++){
                int index = ((Integer)(v.elementAt(pos))).intValue();
                SpeakerContribution sc = (SpeakerContribution)(elementAt(index));
                if (getCommonTimeline().calculateSpan(sc.getMain().getStart(),sc.getMain().getEnd()) == length){
                    result.add(new Integer(index));
                    v.removeElementAt(pos);
                    // added version 1.2.7.
                    pos--;
                }
             }
             length--;
        }
        return result;
    }
    
}
