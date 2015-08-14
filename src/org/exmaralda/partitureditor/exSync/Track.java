/*
 * Track.java
 *
 * Created on 11. April 2002, 11:59
 */

package org.exmaralda.partitureditor.exSync;

/**
 *
 * @author  Thomas
 * @version 
 */
public class Track {

    public String name;
    public String font;
    public int noEntries;
    public SyncTabs syncTabs;
    public String text;
    public StringBuffer messages = null;
    
    /**creates new Track */
    public Track(){
        syncTabs = new SyncTabs();
    }
    
    public org.exmaralda.partitureditor.jexmaralda.Tier toTier(org.exmaralda.partitureditor.jexmaralda.Timeline timeline){
        SyncTab endST = new SyncTab();
        endST.id = "_END";
        endST.offset = text.length();
        syncTabs.addSyncTab(endST);
        org.exmaralda.partitureditor.jexmaralda.Tier result = new org.exmaralda.partitureditor.jexmaralda.Tier();
        result.setCategory("?");
        result.setType("T");        
        int notIntegrated = 0;
        for (int pos=0; pos<syncTabs.size()-1; pos++){
            org.exmaralda.partitureditor.jexmaralda.Event newEvent = new org.exmaralda.partitureditor.jexmaralda.Event();
            SyncTab st = syncTabs.getSyncTabAt(pos);
            SyncTab nextSt = syncTabs.getSyncTabAt(pos+1);                    
            if (timeline.containsTimelineItemWithID("T" + st.id) &&
                timeline.containsTimelineItemWithID("T" + nextSt.id)){
                String textOfEvent = text.substring(st.offset+1, nextSt.offset);            
                newEvent.setDescription(CharsetConverter.convert(textOfEvent, font));
                newEvent.setStart("T" + st.id);
                newEvent.setEnd("T" + nextSt.id);
                result.addEvent(newEvent);
            } else if (timeline.containsTimelineItemWithID("T" + st.id)){
                int pos2=pos+1;
                while (pos2<syncTabs.size() && !timeline.containsTimelineItemWithID("T" + syncTabs.getSyncTabAt(pos2).id)){
                    pos2++;
                }
                if (pos2<syncTabs.size()){
                    SyncTab nextAvailableSyncTab = syncTabs.getSyncTabAt(pos2);
                    String textOfEvent = text.substring(st.offset+1, nextSt.offset);            
                    newEvent.setDescription(CharsetConverter.convert(textOfEvent, font));
                    newEvent.setStart("T" + st.id);
                    newEvent.setEnd("T" + nextAvailableSyncTab.id);
                    result.addEvent(newEvent);
                }
            } else {
                String textOfEvent = text.substring(st.offset+1, nextSt.offset);            
                writeMessage(CharsetConverter.convert(textOfEvent, font));
                //System.out.println("Could not integrate \"" + CharsetConverter.convert(textOfEvent, font) + "\"");
                notIntegrated++;
            }
        }
        result.removeEmptyEvents();
        writeMessage(notIntegrated + " entries of " + syncTabs.size() + " not integrated in track " + name);
        return result;
    }        
    
    private void writeMessage(String m){
        if (messages!=null){
            messages.append(m);
            messages.append(System.getProperty("line.separator"));
        }
    }
    

}


