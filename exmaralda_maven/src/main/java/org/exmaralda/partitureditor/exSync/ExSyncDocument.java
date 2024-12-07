/*
 * ExSyncDocument.java
 *
 * Created on 11. April 2002, 11:48
 */

package org.exmaralda.partitureditor.exSync;

import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.Speaker;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import java.util.*;
import org.xml.sax.*;
import org.exmaralda.partitureditor.jexmaralda.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExSyncDocument extends Vector {

    public StringBuffer messages = null;
    
    public ExSyncDocument() {
        super();
    }

    /** Creates new ExSyncDocument from the file with the specified filename*/
    public ExSyncDocument(String filename) throws SAXException {
        this();
        writeMessage("Trying to read exSync document " + filename);
        try {
            org.exmaralda.partitureditor.exSync.sax.ExSyncDocumentSaxReader reader = new org.exmaralda.partitureditor.exSync.sax.ExSyncDocumentSaxReader();
            ExSyncDocument d = reader.readFromFile(filename);
            writeMessage("Document read.");
            for (int pos=0; pos<d.size(); pos++){
                this.addTrack(d.getTrackAt(pos));
                writeMessage("Track " + d.getTrackAt(pos).name + " added.");
            }        
        } catch (SAXException se){
            writeMessage("Document could not be read. ");
            writeMessage("Error message : " + se.getMessage());
            throw se;
        }
    }
    
    public void addTrack(Track t){
        addElement(t);
    }
    
    public Track getTrackAt(int position){
        return (Track)elementAt(position);
    }   
    
    public BasicTranscription toBasicTranscription() throws JexmaraldaException {
        BasicTranscription result = new BasicTranscription();
        writeMessage("Generating timeline...");
        result.getBody().setCommonTimeline(makeTimeline());
        writeMessage("Timeline generated.");
        writeMessage("Getting entries...");
        for (int pos=0; pos<size(); pos++){
            Track t = getTrackAt(pos);
            t.messages = this.messages;
            Speaker newSpeaker = new Speaker();
            newSpeaker.setID("SPK" + Integer.toString(pos));
            newSpeaker.setAbbreviation(t.name);
            result.getHead().getSpeakertable().addSpeaker(newSpeaker);
            writeMessage("Getting entries of track " + t.name);
            writeMessage("===============================");
            writeMessage("Entries not integrated:");
            Tier newTier = t.toTier(result.getBody().getCommonTimeline());
            newTier.setSpeaker("SPK" + Integer.toString(pos));
            newTier.setID("TIE" + Integer.toString(pos));
            result.getBody().addTier(newTier);
            writeMessage("===============================");
        }
        result.makeDisplayNames();
        writeMessage("Done.");
        return result;
    }
    
    public Timeline makeTimeline(){
        Timeline result = new Timeline();

        TimelineItem start = new TimelineItem();
        start.setID("T_START");
        try{ result.addTimelineItem(start);}
        catch (JexmaraldaException je) {}
        
        int maxNumberOfSyncTabs = 0;
        int trackNoWithMax = 0;
        for (int pos=0; pos<size(); pos++){
            Track t = getTrackAt(pos);
            if (t.syncTabs.size()>maxNumberOfSyncTabs){
                maxNumberOfSyncTabs = t.syncTabs.size();
                trackNoWithMax = pos;
            }
        }

        Track trackWithMax = getTrackAt(trackNoWithMax);
        writeMessage("Reference track is track " + trackWithMax.name);
        for (int pos2=0; pos2<trackWithMax.syncTabs.size(); pos2++){
            TimelineItem tli = new TimelineItem();
            SyncTab st = trackWithMax.syncTabs.getSyncTabAt(pos2);
            tli.setID("T" + st.id);
            try{ result.addTimelineItem(tli);}
            catch (JexmaraldaException je) {
                writeMessage("Jexmaralda Exception : " + je.getMessage());
            }
        }
        TimelineItem end = new TimelineItem();
        end.setID("T_END");
        try{ result.addTimelineItem(end);}
        catch (JexmaraldaException je) {}
        
        boolean goon=true;
        int cycles=0;
        while(goon){
            cycles++;
            goon = false;
            writeMessage("Cycle " + Integer.toString(cycles));
            Vector stretches = new Vector();
            for (int pos=0; pos<size(); pos++){
                if (pos==trackNoWithMax) continue;
                Track track = getTrackAt(pos);
                for (int pos2=0; pos2<track.syncTabs.size(); pos2++){
                    SyncTab st = track.syncTabs.getSyncTabAt(pos2);
                    String id = "T" + st.id;
                    if (result.containsTimelineItemWithID(id)) continue;
                    else if (pos2==0) continue;
                    else {
                        SyncTabStretch sts = new SyncTabStretch();
                        String startID = track.syncTabs.getSyncTabAt(pos2-1).id;
                        sts.addElement(startID);
                        while ((pos2<track.syncTabs.size()) && (!result.containsTimelineItemWithID("T"+ track.syncTabs.getSyncTabAt(pos2).id))){
                            sts.addElement(track.syncTabs.getSyncTabAt(pos2).id);
                            pos2++;
                        }
                        if (pos2<track.syncTabs.size()){
                            String endID = track.syncTabs.getSyncTabAt(pos2).id;
                            sts.addElement(endID);
                            stretches.addElement(sts);
                        }
                    }
                }
            }

            java.util.Collections.sort(stretches);
            for (int pos=0; pos<stretches.size(); pos++){
                SyncTabStretch sts = (SyncTabStretch)(stretches.elementAt(pos));
                //System.out.println("Trying to integrate" + sts.toString() + " into " + result.toXML());
                String startID = "T" + (String)(sts.firstElement());
                String endID = "T" + (String)(sts.lastElement());
                int first = result.lookupID(startID);
                int second = result.lookupID(endID);               
                //System.out.println(startID + "(" + first + ") / " + endID + "(" + second + ")");
                if (second - first  == 1){
                    writeMessage("Integrating " + sts.toString());
                    goon = true;
                    for (int pos2=1; pos2<sts.size()-1; pos2++){
                        String id = "T" + (String)(sts.elementAt(pos2));
                        TimelineItem newTLI = new TimelineItem();
                        newTLI.setID(id);
                        try{ result.insertTimelineItemBefore(endID, newTLI);}
                        catch (JexmaraldaException je) {
                            writeMessage("Jexmaralda Exception : " + je.getMessage());
                        }                            
                    }
                }
            }
        }
        
        return result;
    }
    
    private void writeMessage(String m){
        if (messages!=null){
            messages.append(m);
            messages.append(System.getProperty("line.separator"));
        }
    }

}

