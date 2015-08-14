/*
 * ItConverter.java
 *
 * Created on 26. Februar 2002, 16:44
 */

package org.exmaralda.partitureditor.jexmaralda.convert;

import org.exmaralda.partitureditor.interlinearText.ItLine;
import org.exmaralda.partitureditor.interlinearText.ItChunk;
import org.exmaralda.partitureditor.interlinearText.Formats;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.interlinearText.ItBundle;
import org.exmaralda.partitureditor.interlinearText.SyncPoints;
import org.exmaralda.partitureditor.interlinearText.SyncPoint;
import org.exmaralda.partitureditor.interlinearText.ItLabel;
import org.exmaralda.partitureditor.interlinearText.Format;
import org.exmaralda.partitureditor.interlinearText.Run;
import org.exmaralda.partitureditor.interlinearText.*;
import org.exmaralda.partitureditor.jexmaralda.*;
/**
 *
 * @author  Thomas
 * @version 
 */
public final class ItConverter {

    static Formats formats;
    static SyncPoints syncPoints;
    
    /** Creates new ItConverter */
    public ItConverter() {
    }
    
    /** converts the given basic transcription with the given tier format table to an interlinear text object */
    public static InterlinearText BasicTranscriptionToInterlinearText(BasicTranscription t, TierFormatTable tft){
        return BasicTranscriptionToInterlinearText(t,tft,0);
    }
    
    /** converts the given basic transcription with the given tier format table to an interlinear text object, 
     *  starts the count of timeline items at the specified value */
    public static InterlinearText BasicTranscriptionToInterlinearText(BasicTranscription t, TierFormatTable tft, int timelineStart){
        InterlinearText result = new InterlinearText(); 
        formats = TierFormatTableToFormats(tft);
        result.setFormats(formats);
        ItBundle itb = new ItBundle();
        syncPoints = TimelineToSyncPoints(t.getBody().getCommonTimeline(), tft.getTimelineItemFormat(), timelineStart);
        itb.setSyncPoints(syncPoints);
        for (int pos=0; pos<t.getBody().getNumberOfTiers(); pos++){
            Tier tier = t.getBody().getTierAt(pos);
            tier.sort(t.getBody().getCommonTimeline());
            ItLine itl = TierToItLine(tier, t.getHead().getSpeakertable());
            itb.addItLine(itl);
        }
        result.addItElement(itb);
        return result;
    }

    /** converts the given tier format table to a Formats object for interlinear text 
     * (not much to be done here, actually, but I preferred to keep the TierFormatTable object
     * for historical reasons) */
    static Formats TierFormatTableToFormats(TierFormatTable tft){
        Formats result = new Formats();
        String[] ids = tft.getAllTierIDs();
        for (int pos=0; pos<ids.length; pos++){
            try {
                TierFormat tf = tft.getTierFormatForTier(ids[pos]).makeCopy();
                tf.convertColors();
                Format newFormat = new Format();
                newFormat.setID(tf.getTierref());
                for (java.util.Enumeration e = tf.propertyNames() ; e.hasMoreElements() ;) {
                     String propertyName = (String)e.nextElement();
                     String propertyValue = tf.getProperty(propertyName);
                     newFormat.setProperty(propertyName, propertyValue);
                }
                result.addFormat(newFormat);
            }
            catch (JexmaraldaException je){
                /* should never get here*/
                System.out.println("Error " + je.getMessage());
            }
        }
        return result;
    }
    
    /** converts the given Timeline object to a SyncPoints object for interlinear text */
    static SyncPoints TimelineToSyncPoints(Timeline timeline, TimelineItemFormat tlif, int timelineStart){
        SyncPoints result = new SyncPoints();
        result.setFormat(formats.getFormatWithID("COLUMN-LABEL"));
        for (int pos=0; pos<timeline.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = timeline.getTimelineItemAt(pos);
            SyncPoint newSyncPoint = new SyncPoint();
            newSyncPoint.setID(tli.getID());
            newSyncPoint.setText(tli.getDescription(pos + timelineStart, tlif));
            newSyncPoint.setFormat(formats.getFormatWithID("COLUMN-LABEL"));
            result.addSyncPoint(newSyncPoint);
        }
        return result;
    }
    
    /** converts the given tier to an ItLine object using the given speakertable (for line labelling) */
    static ItLine TierToItLine(Tier tier, Speakertable speakertable){
        ItLine result = new ItLine();
        if      (tier.getType().equals("t"))    {result.setBreakType(ItLine.NB_TIME);}
        else if (tier.getType().equals("d"))    {result.setBreakType(ItLine.NB_NOTIME);}
        else if (tier.getType().equals("a"))    {result.setBreakType(ItLine.NB_DEP);}
        else if (tier.getType().equals("l"))    {result.setBreakType(ItLine.NB_LNK);}
        result.setFormat(formats.getFormatWithID(tier.getID()));
        ItLabel label = new ItLabel();
        label.setFormat(formats.getFormatWithID("ROW-LABEL"));
        if (tier.getSpeaker()!=null){
            label.addUdInformation("javascript:onClickAnchor",tier.getSpeaker());
        }
        Run newRun = new Run();
        // changed in Version 1.2.5.
        //newRun.setText(tier.getDescription(speakertable));
        newRun.setText(tier.getDisplayName());
        label.addRun(newRun);
        result.setLabel(label);
        for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
            Event event = tier.getEventAt(pos);
            ItChunk newItChunk = new ItChunk();
            newItChunk.setStart(syncPoints.getSyncPointWithID(event.getStart()));
            newItChunk.setEnd(syncPoints.getSyncPointWithID(event.getEnd()));
            if (!event.getMedium().equals("none")){
                newItChunk.addLink(new org.exmaralda.partitureditor.interlinearText.Link(event.getURL(), event.getMedium()));
            }
            newRun = new Run();
            newRun.setText(event.getDescription());
            newItChunk.addRun(newRun);
            result.addItChunk(newItChunk);
        }
        return result;        
    }
    
    /** converts the given color to a string representation as demanded in the
     * interlinear text conventions, i.e. a String #RxxGyyBzz, where
     * x,y and z represent the RGB values of the color in hexadecimal numbers */
    static String convertColor(java.awt.Color color){
        String result="#";
        String r = Integer.toHexString(color.getRed());
        if (r.length()==1) {r="0" + r;}
        String g = Integer.toHexString(color.getGreen());
        if (g.length()==1) {g="0" + g;}
        String b = Integer.toHexString(color.getBlue());
        if (b.length()==1) {b="0" + b;}
        result += "R" + r + "G" + g + "B" + b;
        return result;        
    }

}
