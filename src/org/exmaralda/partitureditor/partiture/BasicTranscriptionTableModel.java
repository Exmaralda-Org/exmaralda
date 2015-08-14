/*
 * BasicTranscriptionTableModel.java
 *
 * Created on 10. August 2001, 15:25
 */

package org.exmaralda.partitureditor.partiture;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.jexmaraldaswing.*;

/**
 * extends the basic functionalities of the abstract model by methods for manipulating events, tiers etc.
 * in the basic transcription
 * @author  Thomas Schmidt, thomas.schmidt@uni-hamburg.de
 * @version 1.01 (essential changes for better performance!)
 */
public class BasicTranscriptionTableModel extends AbstractTranscriptionTableModel {

     
    public boolean INTERPOLATE_WHEN_SPLITTING = true;
    
    /** Creates new BasicTranscriptionTableModel */
    public BasicTranscriptionTableModel() {
        super();
    }

    /** resets the transcription to "zero" */
    public void resetTranscription(){
        transcription = new BasicTranscription();
        setTimeProportional(false, false);
        try {
            Speaker speaker = new Speaker();
            speaker.setID("SPK0");
            speaker.setAbbreviation("X");
            transcription.getHead().getSpeakertable().addSpeaker(speaker);
            transcription.getBody().getCommonTimeline().addTimelineItem();
            transcription.getBody().getCommonTimeline().addTimelineItem();
            Tier tier = new Tier("TIE0","SPK0","v","t");
            transcription.getBody().addTier(tier);
            transcription.makeDisplayNames();
            formats = new TierFormatTable(transcription, defaultFontName);
        }
        catch (JexmaraldaException je) {}
        fireDataReset();
    }
    
//******** METHODS FOR GETTING AND SETTING FORMATS *********
    
    /** returns the format of the specified row */
    public TierFormat getFormat(int row){
        Tier tier = transcription.getBody().getTierAt(row);
        String tierID = tier.getID();
        try {return formats.getTierFormatForTier(tierID);}
        catch (JexmaraldaException je) {}   // should not get here
        return new TierFormat();
    }

    /** sets the format of the specified row */
    public void setFormat(int row, TierFormat format){
        //TierFormat oldFormat = getFormat(row);
        formats.setTierFormat(format);
        fireRowFormatChanged(row);
        fireAreaChanged(0,getNumColumns());
    }             
        
    /** sets the format of the specified row */
    public void setFormats(int startRow, int endRow, TierFormat format){
        for (int row=startRow; row<=endRow; row++){
            TierFormat oldFormat = getFormat(row);
            TierFormat newFormat = format.makeCopy();
            newFormat.setTierref(oldFormat.getTierref());
            formats.setTierFormat(newFormat);
            fireRowFormatChanged(row);
        }
        fireAreaChanged(0,getNumColumns());
    }             

    /** returns the format for empty cells */
    public TierFormat getEmptyFormat(){
        try {return formats.getTierFormatForTier("EMPTY-EDITOR");}
        catch (JexmaraldaException je) {return new TierFormat();}   //should never get here
    }
    
    /** returns the format for column labels */
    public TierFormat getColumnLabelFormat(){
        try {return formats.getTierFormatForTier("COLUMN-LABEL");}
        catch (JexmaraldaException je) {return new TierFormat();}
    }        
    
    /** sets the format for column labels */
    public void setColumnLabelFormat(TierFormat format){
        formats.setTierFormat(format);
        fireColumnLabelsFormatChanged();
    }

    /** sets the format for the timeline */
    public void setTimelineItemFormat(TimelineItemFormat tlif){
        formats.setTimelineItemFormat(tlif);
        fireColumnLabelsChanged();
    }        

    /** returns the format for row labels */
    public TierFormat getRowLabelFormat(){
        try {return formats.getTierFormatForTier("ROW-LABEL");}
        catch (JexmaraldaException je) {return new TierFormat();}   // should never get here
    }    
    
    /** sets the format for row labels */
    public void setRowLabelFormat(TierFormat format){
        formats.setTierFormat(format);
        fireRowLabelsFormatChanged();
    }

    /** returns the required (not actual!) height of the specified row */
    public int getRowHeight(int row, int scaleFactor){
        TierFormat tf;
        TierFormat rlf = getRowLabelFormat();
        if (row>=0){
            tf = getFormat(row);
        } else {
            tf = getColumnLabelFormat();
        }
        if (tf.getProperty("row-height-calculation","Generous").equals("Fixed")){
            return Short.parseShort(tf.getProperty("fixed-row-height","10"));
        }
        int rowHeight = dummyTextField.getFontMetrics(tf.getFont(scaleFactor)).getHeight();
        int labelHeight=0;
        if (!tf.getProperty("row-height-calculation","Generous").equals("Miserly")){
            labelHeight = dummyTextField.getFontMetrics(rlf.getFont(scaleFactor)).getHeight();
        }
        if (row<0) {labelHeight=0;}
        return Math.max(rowHeight, labelHeight);
    }    
    
//******* METHODS FOR MANIPULATING TIERS *********************   
    
    /** sets the tier properties anew */
    public void editTier(int row, Tier newTier){
        try{
            Tier tier = transcription.getBody().getTierWithID(newTier.getID());
            tier.setSpeaker(newTier.getSpeaker());
            tier.setType(newTier.getType());
            tier.setCategory(newTier.getCategory());
            tier.setDisplayName(newTier.getDisplayName());
            tier.setUDTierInformation(newTier.getUDTierInformation());
            fireRowLabelChanged(row);
        }
        catch (JexmaraldaException je){}            
    }
    
    public void editTiers(BasicTranscription bt){
        for (int row=0; row<bt.getBody().getNumberOfTiers(); row++){
            editTier(row,bt.getBody().getTierAt(row));
        }
    }
    
    /** reorders the tiers according to the given order */
    public void changeTierOrder(String[] tierOrder){
        try {transcription.getBody().reorderTiers(tierOrder);}
        catch (JexmaraldaException je) {}   // should never get here
        fireDataReset();
    }
    
    /** inserts a tier BEFORE the specified row */
    public void insertTier(Tier tier, int row){        
        try {
            Tier tierAfter = transcription.getBody().getTierAt(row);
            transcription.getBody().insertTierBeforeTierWithID(tier, tierAfter.getID());
            TierFormat newFormat = new TierFormat(tier.getType(), tier.getID(), this.defaultFontName);
            formats.setTierFormat(newFormat);
            fireRowsAdded(row,row);
        } catch (JexmaraldaException je) {} // should never get here               
    }
    
    /** removes the tier at the specified position */
    public void removeTier(int row){
        Tier tier = transcription.getBody().getTierAt(row);
        try{ 
            transcription.getBody().removeTierWithID(tier.getID());
            fireRowDeleted(row,1);
        } catch (JexmaraldaException je){} // should never get here
        fireSelectionChanged(Math.max(0,row-1),-1,false);
    }
    
    /** removes the tiers at the specified position */
    public void removeTiers(int startRow, int endRow){
        for (int i=startRow; i<=endRow; i++){
            Tier tier = transcription.getBody().getTierAt(startRow);
            try{ 
                transcription.getBody().removeTierWithID(tier.getID());
                fireRowDeleted(startRow,1);
            } catch (JexmaraldaException je){} // should never get here
        }
        fireSelectionChanged(Math.max(0,startRow-1),-1,false);
    }

    /** moves the tier at the specified position one row upwards, i.e.
        swaps this tier with the one above */
    public void moveTierUp(int row){
        transcription.getBody().swapTiers(row-1,row);
        fireRowsSwapped(row-1,row);
        fireSelectionChanged(row-1,-1,false);
    }
    
    /** remove all empty events in tier that is held by the specified row */
    public void removeEmptyEvents(int row){
        transcription.getBody().getTierAt(row).removeEmptyEvents();
        fireDataReset();
    }
    
    /** adds a tier at the end */
    public void addTier(Tier tier){
        try {
            transcription.getBody().addTier(tier);
            TierFormat newFormat = new TierFormat(tier.getType(), tier.getID(),this.defaultFontName);
            formats.setTierFormat(newFormat);
            fireRowsAdded(getNumRows()-1,getNumRows()-1);
        } catch (JexmaraldaException je) {}

    }

    //******* METHODS FOR MANIPULATING EVENTS *********************   
    
    public int findNextEvent(int row, int col){       
        if (row<0) return -1;
        Tier tier = this.getTier(row);
        for (int pos=col+1; pos<getNumColumns(); pos++){
            String tli = getTimelineItem(pos).getID();
            if (tier.containsEventAtStartPoint(tli)){
                return pos;
            }
        }
        return -1;
    }
    
    //added 05-11-2009: enable merging on several consecutive tiers
    public void merge(int selectionStartRow, int selectionEndRow, int startColumn, int endColumn) {
        for (int row=selectionStartRow; row<=selectionEndRow; row++){
            Tier tier = transcription.getBody().getTierAt(row);
            Timeline timeline = transcription.getBody().getCommonTimeline();
            String mergedStart = timeline.getTimelineItemAt(startColumn).getID();
            int spanOfLast = getCellSpan(row, endColumn);
            String mergedEnd = new String();
            // Changes on 06-Oct-2003 (Version 1.2.6.)
            // Suspecting this is the cause of bug no. 154
            // Merge can sometimes be larger than the timeline
            if (timeline.getNumberOfTimelineItems() > (endColumn + spanOfLast)){
                mergedEnd = timeline.getTimelineItemAt(endColumn+spanOfLast).getID();
            } else {
                mergedEnd = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
            }
            String description = new String();

            // added 01-12-2009: don't perform any merges when an event
            // spans across the range that is to be merged
            Event previousEvent = tier.getFirstEventBeforeStartPoint(timeline, mergedStart);
            boolean b1 = ((previousEvent!=null) && (timeline.lookupID(previousEvent.getEnd())>timeline.lookupID(mergedStart)));
            Event lastEvent = tier.getFirstEventBeforeStartPoint(timeline, mergedEnd);
            boolean b2 = ((lastEvent!=null) && (timeline.lookupID(lastEvent.getEnd())>timeline.lookupID(mergedEnd)));
            boolean anEventSpansAcrossTheMergeRange = b1 || b2;

            if (!anEventSpansAcrossTheMergeRange){
                Event mergedEvent = new Event(mergedStart, mergedEnd, description);
                try {
                    for (int col = startColumn; col<=endColumn; col++){ // delete the source events
                        if (containsEvent(row,col)){
                                Event currentEvent = getEvent(row,col);
                                description += currentEvent.getDescription();
                                String tli = timeline.getTimelineItemAt(col).getID();
                                tier.removeEventAtStartPoint(tli);
                                fireCellSpanChanged(row,col);
                                fireCellFormatChanged(row,col);
                                //fireValueChanged(row,col); //not necessary, because the span of the new cell will make sure that all these cells are updated???
                         }
                    }
                    mergedEvent.setDescription(description);
                    tier.addEvent(mergedEvent);
                    fireCellFormatChanged(row,startColumn);
                    fireValueChanged(row,startColumn);
                    fireCellSpanChanged(row,startColumn);
                }
                catch (JexmaraldaException je){}
            }
        }
        fireSelectionChanged(selectionStartRow,startColumn,false);
        fireAreaChanged(lower(startColumn), upper(endColumn));
    }

    /** merges the cells between startColumn and endColumn of the specified row 
     *  or: combines the corresponding events*/
    public void merge(int row, int startColumn, int endColumn){
        Tier tier = transcription.getBody().getTierAt(row);
        Timeline timeline = transcription.getBody().getCommonTimeline();
        String mergedStart = timeline.getTimelineItemAt(startColumn).getID();
        int spanOfLast = getCellSpan(row, endColumn);
        String mergedEnd = new String();
        // Changes on 06-Oct-2003 (Version 1.2.6.)
        // Suspecting this is the cause of bug no. 154
        // Merge can sometimes be larger than the timeline
        if (timeline.getNumberOfTimelineItems() > (endColumn + spanOfLast)){
            mergedEnd = timeline.getTimelineItemAt(endColumn+spanOfLast).getID();
        } else {            
            mergedEnd = timeline.getTimelineItemAt(timeline.getNumberOfTimelineItems()-1).getID();
        }
        String description = new String();
        Event mergedEvent = new Event(mergedStart, mergedEnd, description);
        try {
            for (int col = startColumn; col<=endColumn; col++){ // delete the source events
                if (containsEvent(row,col)){
                        Event currentEvent = getEvent(row,col);
                        description += currentEvent.getDescription();
                        String tli = timeline.getTimelineItemAt(col).getID();
                        tier.removeEventAtStartPoint(tli);
                        fireCellSpanChanged(row,col);
                        fireCellFormatChanged(row,col);
                        //fireValueChanged(row,col); //not necessary, because the span of the new cell will make sure that all these cells are updated???
                 }
            }
            mergedEvent.setDescription(description);
            tier.addEvent(mergedEvent);
            fireCellFormatChanged(row,startColumn);
            fireValueChanged(row,startColumn);    
            fireCellSpanChanged(row,startColumn);
            fireSelectionChanged(row,startColumn,false);
            fireAreaChanged(lower(startColumn), upper(endColumn));
        }
        catch (JexmaraldaException je){}                                                
    }
    
    /** splits the cell in two cells
      * or: splits the corresponding event */
    public void split(int row, int col, int textPos, java.awt.Frame parent){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            String leftHalf = oldEvent.getDescription().substring(0,textPos);
            String rightHalf = oldEvent.getDescription().substring(textPos);
            int span = getCellSpan(row,col);
            if (span==1){
                String tli3 = oldEvent.getEnd();
                double time1 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli).getTime();
                double time2 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli3).getTime();

                // added 05-MARCH-2009 to make sure that no timepoints with identical times are created
                if ((INTERPOLATE_WHEN_SPLITTING) &&  ((time1>=0) && (time2>=0)) && ((time2-time1)<=0.1)){
                    String text = "Cannot split: Resulting events would be shorter than 0.05 seconds.";
                    JOptionPane.showMessageDialog(parent, text);
                    return;
                }

                String tli2 = transcription.getBody().getCommonTimeline().insertTimelineItemAfter(tli);
                
                // added 19-JUNE-2008
                if (this.INTERPOLATE_WHEN_SPLITTING){
                    if ((time1>=0) && (time2>=0)){
                        TimelineItem newTLI = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli2);
                        newTLI.setType("intp");
                        // changed 05-MARCH-2009 to make sure that no timepoints with identical times are created
                        // changed 01-JULY-2010 to avoid division by zero
                        double splitRatio = 0.5;
                        if (oldEvent.getDescription().length()>0){
                            splitRatio = ((double)textPos/oldEvent.getDescription().length());
                        }
                        double newTime = time1 + (time2-time1)*0.5;
                        if (time2-time1>0.1){
                            newTime = Math.max(time1+0.051, Math.min(time2-0.051, time1 + (time2-time1)*splitRatio));
                        }
                        newTLI.setTime(newTime);
                    }
                }
                
                tier.removeEventAtStartPoint(tli);
                Event leftEvent = new Event(tli,tli2,leftHalf, oldEvent.getMedium(), oldEvent.getURL());
                Event rightEvent = new Event (tli2, tli3, rightHalf, oldEvent.getMedium(), oldEvent.getURL());
                tier.addEvent(leftEvent);
                tier.addEvent(rightEvent);
                fireColumnsAdded(col+1,col+1);
                fireValueChanged(row,col);
                fireCellSpanChanged(row,col);
                fireCellFormatChanged(row,col+1);
                fireValueChanged(row,col+1);
                fireCellSpanChanged(row,col+1);
                fireAreaChanged(lower(col),upper(col+1));
                fireSelectionChanged(row,col,true);                                
            }
            else if (span==2){
                String tli2 = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+1).getID();
                String tli3 = oldEvent.getEnd();                   
                tier.removeEventAtStartPoint(tli);
                Event leftEvent = new Event(tli,tli2,leftHalf,oldEvent.getMedium(), oldEvent.getURL());
                Event rightEvent = new Event (tli2, tli3, rightHalf,oldEvent.getMedium(), oldEvent.getURL());
                tier.addEvent(leftEvent);
                tier.addEvent(rightEvent);
                fireCellSpanChanged(row,col);
                fireValueChanged(row,col);
                fireValueChanged(row,col+1);
                fireCellSpanChanged(row,col+1);
                fireCellFormatChanged(row,col+1);
                fireAreaChanged(lower(col),upper(col+1));
                fireSelectionChanged(row,col,true);
            }
            else { // i.e. cell span > 2
                String tli3 = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span).getID();
                Timeline timeline = transcription.getBody().getCommonTimeline().getTimelineBetween(tli,tli3);
                ChooseTimelineItemDialog dialog = new ChooseTimelineItemDialog(parent, true, timeline, col+1);
                dialog.setLocationRelativeTo(parent);
                if (dialog.chooseTimelineItem()){
                    String tli2 = dialog.getTimelineItem().getID();
                    tier.removeEventAtStartPoint(tli);
                    Event leftEvent = new Event(tli,tli2,leftHalf,oldEvent.getMedium(), oldEvent.getURL());
                    Event rightEvent = new Event (tli2, tli3, rightHalf,oldEvent.getMedium(), oldEvent.getURL());
                    tier.addEvent(leftEvent);
                    tier.addEvent(rightEvent);
                    fireValueChanged(row,col);
                    fireCellSpanChanged(row,col);
                    int span2 = getCellSpan(row,col);
                    fireCellFormatChanged(row,col+span2);
                    fireValueChanged(row,col+span2);
                    fireCellSpanChanged(row,col+span2);
                    fireAreaChanged(lower(col),upper(col+span2));
                    fireSelectionChanged(row,col,true);                    
                } // end if
            } // end else
        } catch (JexmaraldaException je){je.printStackTrace();}        
    }

    
    /** underlines the selected text in the specified event */
    public void underline(int row, int col, int textStartPos, int textEndPos, boolean useDiacritics, String category){
        if (textStartPos==textEndPos) return;
        int span = getCellSpan(row,col);
        if (span>1) return;
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            String leftPart = event.getDescription().substring(0,textStartPos);
            String middlePart = event.getDescription().substring(textStartPos, textEndPos);
            String rightPart = event.getDescription().substring(textEndPos);
            
            if (useDiacritics){
                // underline using a diacritic
                // remove all
                middlePart = middlePart.replaceAll("\\u0332", "");
                String newMiddlePart = "";
                // insert new
                for (int pos=0; pos<middlePart.length(); pos++){
                    newMiddlePart+= middlePart.charAt(pos) + "\u0332";
                }
                event.setDescription(leftPart + newMiddlePart + rightPart);
                fireValueChanged(row,col);
                // done!
                return;
            }
            
            // underline in a separate tier
            
            if ((leftPart.length()==0) && (rightPart.length()==0)){
                // do nothing
            }
            else if (leftPart.length()==0){
                split(row, col, textEndPos, null);
            } else if (rightPart.length()==0){
                split(row, col, textStartPos, null);                
            } else {
                doubleSplit(row, col, textStartPos, textEndPos);
            }
            
            
            Tier followingTier = null;
            if (row<getNumRows()-1){
                followingTier = getTier(row+1);
            }
            if ((followingTier==null) 
                || (!(followingTier.getCategory().equals(category))) 
                || (!(followingTier.getSpeaker().equals(tier.getSpeaker())))){
                // an annotation tier must be generated
                followingTier = new Tier();
                followingTier.setSpeaker(tier.getSpeaker());
                followingTier.setCategory(category);
                followingTier.setType("a");
                followingTier.setID(transcription.getBody().getFreeID());
                followingTier.setDisplayName("");
                if (row<this.getNumRows()-1){
                    insertTier(followingTier, row+1);
                } else {
                    addTier(followingTier);
                }
            }
            
            int newCol = col;
            if (leftPart.length()==0) newCol--;
            //newCol = Math.max(0,newCol);
            String tli1 = transcription.getBody().getCommonTimeline().getTimelineItemAt(newCol+1).getID();
            String tli2 = transcription.getBody().getCommonTimeline().getTimelineItemAt(newCol+2).getID();
            followingTier.removeEventsAtStartPoint(tli1);
            Event underlineEvent = new Event();
            underlineEvent.setDescription("-");
            underlineEvent.setStart(tli1);
            underlineEvent.setEnd(tli2);
            followingTier.addEvent(underlineEvent);
            fireAreaChanged(Math.max(0,newCol), newCol+2);
            fireValueChanged(row+1,newCol+1);
            
            
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
            return;
        }
        
    }

    /** splits the cell in two cells
    * or: double splits the corresponding event */
    public void doubleSplit(int row, int col, int textStartPos, int textEndPos){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            int span = getCellSpan(row,col);
            if (span==1){
                String leftPart = oldEvent.getDescription().substring(0,textStartPos);
                String middlePart = oldEvent.getDescription().substring(textStartPos, textEndPos);
                String rightPart = oldEvent.getDescription().substring(textEndPos);
                //System.out.println(leftPart + " / " + middlePart + " / " + rightPart);
                
                String tli2 = transcription.getBody().getCommonTimeline().insertTimelineItemAfter(tli);
                String tli3 = transcription.getBody().getCommonTimeline().insertTimelineItemAfter(tli2);
                String tli4 = oldEvent.getEnd();
                
                // added 14-JAN-2009
                if (this.INTERPOLATE_WHEN_SPLITTING){
                    double time1 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli).getTime();
                    double time2 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli4).getTime();
                    if ((time1>=0) && (time2>=0)){
                        TimelineItem newTLI1 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli2);
                        newTLI1.setType("intp");
                        double newTime1 = time1 + (time2-time1)*((double)textStartPos/oldEvent.getDescription().length());
                        newTLI1.setTime(newTime1);

                        TimelineItem newTLI2 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli3);
                        newTLI2.setType("intp");
                        double newTime2 = time1 + (time2-time1)*((double)textEndPos/oldEvent.getDescription().length());
                        newTLI2.setTime(newTime2);
                    }
                }

                tier.removeEventAtStartPoint(tli);
                
                Event leftEvent = new Event(tli,tli2,leftPart, oldEvent.getMedium(), oldEvent.getURL());
                Event middleEvent = new Event (tli2, tli3, middlePart, oldEvent.getMedium(), oldEvent.getURL());
                Event rightEvent = new Event (tli3, tli4, rightPart, oldEvent.getMedium(), oldEvent.getURL());
                
                tier.addEvent(leftEvent);
                tier.addEvent(middleEvent);
                tier.addEvent(rightEvent);
                
                fireColumnsAdded(col+1,col+1);
                fireColumnsAdded(col+2,col+2);
                
                fireValueChanged(row,col);
                fireCellSpanChanged(row,col);
                
                fireCellFormatChanged(row,col+1);
                fireValueChanged(row,col+1);
                fireCellSpanChanged(row,col+1);
                
                fireCellFormatChanged(row,col+2);
                fireValueChanged(row,col+2);
                fireCellSpanChanged(row,col+2);

                fireAreaChanged(lower(col),upper(col+3));
                fireSelectionChanged(row,col,true);                                
            }
            else {  // i.e. the span of this cell is bigger than 1
                // do nothing (for now...)
            } // end else
        } catch (JexmaraldaException je){je.printStackTrace();}        
    }
    
    
    /** moves the corresponding cell one column to the right
        or: shifts the corresponding event forwards in the timeline */
    public void moveRight(int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            if (transcription.getBody().getCommonTimeline().isLastTimelineItem(oldEvent.getEnd())){
                transcription.getBody().getCommonTimeline().addTimelineItem();
                fireColumnsAdded(getNumColumns()-1, getNumColumns()-1);
            }
            int span = getCellSpan(row,col);

            String newStart = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+1).getID();
            String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span+1).getID();
            Event newEvent = new Event(newStart, newEnd, oldEvent.getDescription(),oldEvent.getMedium(), oldEvent.getURL());

            tier.removeEventAtStartPoint(tli);  // remove old element
            fireValueChanged(row,col);
            fireCellFormatChanged(row,col);
            fireCellSpanChanged(row,col);
            
            tier.addEvent(newEvent);    // add new element
            fireValueChanged(row,col+1); 
            fireCellFormatChanged(row,col+1);
            fireCellSpanChanged(row,col+1);

            fireAreaChanged(lower(col),upper(col+span));
            
            fireSelectionChanged(row,col+1,false);
        } catch (JexmaraldaException je){je.printStackTrace();}         // should never get here           
    }
    
    /** moves the corresponding cell one column to the right
        or: shifts the corresponding event backwards in the timeline */
    public void moveLeft (int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            if (col==0){
                transcription.getBody().getCommonTimeline().insertTimelineItemBefore(oldEvent.getStart());
                fireColumnsAdded(0,0);
                col++;
            }
            int span = getCellSpan(row,col);
            String newStart = transcription.getBody().getCommonTimeline().getTimelineItemAt(col-1).getID();
            String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span-1).getID();
            Event newEvent = new Event(newStart, newEnd, oldEvent.getDescription(),oldEvent.getMedium(), oldEvent.getURL());
            
            tier.removeEventAtStartPoint(tli);  // remove old element
            fireValueChanged(row,col);
            fireCellFormatChanged(row,col);
            fireCellSpanChanged(row,col);
            
            tier.addEvent(newEvent);    // add new event
            fireValueChanged(row,col-1);                    
            fireCellFormatChanged(row,col-1);
            fireCellSpanChanged(row,col-1);
            
            fireAreaChanged(lower(col-1),upper(col+span));
            fireSelectionChanged(row,col-1,false);
        } catch (JexmaraldaException je){}            // should never get here        
    }
    
    /** extends the cell one column to the right
        or: lets the corresponding event end one timepoint later*/
    public void extendRight(int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            if (transcription.getBody().getCommonTimeline().isLastTimelineItem(event.getEnd())){
                transcription.getBody().getCommonTimeline().addTimelineItem();
                fireColumnsAdded(col+1, col+1);
            }
            int span = getCellSpan(row,col);
            String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span+1).getID();
            event.setEnd(newEnd);
            fireCellSpanChanged(row,col);
            fireAreaChanged(lower(col),upper(col+span));
        } catch (JexmaraldaException je){}            // should never get here        
    }
    
    /** extends the cell one column to the left
        or: lets the corresponding event begin one timepoint earlier*/
    public void extendLeft(int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            if (col==0){
                transcription.getBody().getCommonTimeline().insertTimelineItemBefore(oldEvent.getStart());
                fireColumnsAdded(0,0);
                col++;
            }
            String newStart = transcription.getBody().getCommonTimeline().getTimelineItemAt(col-1).getID();
            Event newEvent = new Event(newStart, oldEvent.getEnd(), oldEvent.getDescription(),oldEvent.getMedium(), oldEvent.getURL());

            tier.removeEventAtStartPoint(tli);  // remove old element
            fireValueChanged(row,col);
            fireCellFormatChanged(row,col);
            fireCellSpanChanged(row,col);
            
            tier.addEvent(newEvent);    // add new event
            fireValueChanged(row,col-1);                
            fireCellFormatChanged(row,col-1);
            fireCellSpanChanged(row,col-1);
            
            fireAreaChanged(lower(col-1),upper(col));
            fireSelectionChanged(row,col-1,false);
        } catch (JexmaraldaException je){}                    
    }

    /** or: lets the corresponding event end one timepoint earlier*/    
    public void shrinkRight(int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            int span = getCellSpan(row,col);
            String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span-1).getID();
            event.setEnd(newEnd);
            fireCellSpanChanged(row,col);
            fireAreaChanged(lower(col),upper(col+span));
            fireSelectionChanged(row,col,false);
        } catch (JexmaraldaException je){}            // should never get here        
    }
    
    /** sets the span of the event corresponding to the specified cell */
    public void setCellSpan(int row, int col, int span){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span).getID();
            event.setEnd(newEnd);
        } catch (JexmaraldaException je){}            // should never get here        
    }
    
    /** or: lets the corresponding event begin one timepoint later*/    
    public void shrinkLeft(int row, int col){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event oldEvent = tier.getEventAtStartPoint(tli);
            int span = getCellSpan(row,col);
            String newStart = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+1).getID();
            Event newEvent = new Event(newStart, oldEvent.getEnd(), oldEvent.getDescription(),oldEvent.getMedium(), oldEvent.getURL());
            tier.removeEventAtStartPoint(tli);  //remove old element
            fireValueChanged(row,col);
            fireCellFormatChanged(row,col);
            fireCellSpanChanged(row,col);

            tier.addEvent(newEvent);    // add new element
            fireValueChanged(row,col+1);                  
            fireCellFormatChanged(row,col+1);
            fireCellSpanChanged(row,col+1);
           
            fireAreaChanged(lower(col), upper(col+1));
            fireSelectionChanged(row,col+1,false);                    
        } catch (JexmaraldaException je){}                        // should never get here
    }
    
    /** shifts the characters after textPos to following event */
    public void shiftRight(int row, int col, int textPos){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            if (transcription.getBody().getCommonTimeline().isLastTimelineItem(event.getEnd())){
                transcription.getBody().getCommonTimeline().addTimelineItem();
                fireColumnsAdded(col+1, col+1);
            }

            String remainingString = new String(event.getDescription().substring(0, textPos));  
            String shiftedString = new String(event.getDescription().substring(textPos));
            event.setDescription(remainingString);
            fireValueChanged(row,col);

            int span = getCellSpan(row,col);
            if (cellToTheRightIsFree(row,col)){              
                String newEnd = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span+1).getID();
                Event eventAfter = new Event(event.getEnd(),newEnd,shiftedString);
                tier.addEvent(eventAfter);
                fireValueChanged(row,col+span);
                fireCellFormatChanged(row,col+span);
            }
            else {
                Event eventAfter = tier.getEventAtStartPoint(event.getEnd());
                String newDescription = shiftedString + eventAfter.getDescription();
                eventAfter.setDescription(newDescription);
                fireValueChanged(row,col+span);
            }                    

            fireAreaChanged(lower(col),upper(col+span));            
            fireSelectionChanged(row,col,true);
         } catch (JexmaraldaException je){} // should never get here
    }
    
    /** shifts the characters before textPos to the previous event */
    public void shiftLeft(int row, int col, int textPos){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            if (col==0){
                transcription.getBody().getCommonTimeline().insertTimelineItemBefore(event.getStart());
                fireColumnsAdded(0,0);
                col++;
            }
            
            String remainingString = new String(event.getDescription().substring(textPos));
            String shiftedString = new String(event.getDescription().substring(0, textPos)); 
            event.setDescription(remainingString);
            fireValueChanged(row,col);

            if (cellToTheLeftIsFree(row,col)){              
                String newStart = transcription.getBody().getCommonTimeline().getTimelineItemAt(col-1).getID();
                Event eventBefore = new Event(newStart,event.getStart(),shiftedString);
                tier.addEvent(eventBefore);
                fireValueChanged(row,col-1);
                fireCellFormatChanged(row,col-1);
                fireAreaChanged(lower(col-1),upper(col));                            
            }
            else {
                Event eventBefore = tier.getFirstEventBeforeStartPoint(transcription.getBody().getCommonTimeline(),event.getStart());
                String newDescription =  eventBefore.getDescription() + shiftedString;
                eventBefore.setDescription(newDescription);
                int span = transcription.getBody().getCommonTimeline().calculateSpan(eventBefore.getStart(),eventBefore.getEnd());
                fireValueChanged(row,col-span);
                fireAreaChanged(lower(col-span),upper(col));        
                //System.out.println("changed area begins at " + lower(col-span) + " and ranges to " + upper(col));
            }                    

            fireSelectionChanged(row,col,true);
        } catch (JexmaraldaException je){}            // should never get here
    }
    
    /** opens a dialog for editing properties of the specified event */
    public boolean editEvent(int row, int col, java.awt.Frame parent, String generalPurposeFont){
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            Event event = tier.getEventAtStartPoint(tli);
            EditEventDialog dialog = new EditEventDialog(parent, true, event);
            //dialog.setFont(getFormat(row).getFont());
            if (dialog.editEvent()){
                event.setDescription(dialog.getEvent().getDescription());
                event.setUDEventInformation(dialog.getEvent().getUDEventInformation());
                fireValueChanged(row,col);
                fireAreaChanged(lower(col),upper(col));
                return true;
            }
        } catch (JexmaraldaException je){}            // should never get here
        return false;
    }
        
//******* METHODS FOR MANIPULATING THE TIMELINE *********************   

    /** inserts a new timeline item to the left of the specified column */
    public void insertTimelineItem(int col){
       Timeline timeline = transcription.getBody().getCommonTimeline();
       String tli = timeline.getTimelineItemAt(col).getID();     
       String previousTLI = "";
       if (col>0){
        previousTLI = timeline.getTimelineItemAt(col-1).getID();
       }
       try{
            timeline.insertTimelineItemBefore(tli);
            String newTli = timeline.getTimelineItemAt(col).getID();

            // added 14-01-2009
            if ((this.INTERPOLATE_WHEN_SPLITTING) && (previousTLI.length()>0)){
                double time1 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(tli).getTime();
                double time2 = transcription.getBody().getCommonTimeline().getTimelineItemWithID(previousTLI).getTime();
                if ((time1>=0) && (time2>=0)){
                    TimelineItem newTLI = transcription.getBody().getCommonTimeline().getTimelineItemWithID(newTli);
                    newTLI.setType("intp");
                    double newTime = time1 + (time2-time1)*((double)0.5);
                    newTLI.setTime(newTime);
                }
            }

            fireColumnsAdded(col,col);
            int lower = lower(col-1);
            int upper = upper(col);
            for (int row=0; row<transcription.getBody().getNumberOfTiers(); row++){
                Tier tier = transcription.getBody().getTierAt(row);
                Event event = tier.getFirstEventBeforeStartPoint(timeline,tli);
                if ((event!=null) && (event.getEnd().equals(tli))){
                    event.setEnd(newTli);
                    int col2 = timeline.lookupID(event.getStart());
                    lower=Math.min(lower,lower(col2));
                    fireValueChanged(row,col2);                    
                    fireCellSpanChanged(row,col2);
                    fireValueChanged(row, col);
                    fireCellFormatChanged(row,col);
                }
            }            
            fireAreaChanged(lower,upper);
        } catch (JexmaraldaException je) {} // should never get here
    }
    
    /** inserts new timeline item with the specified absolute time value */
    public int insertTimelineItem(double time, double tolerance){
       Timeline timeline = transcription.getBody().getCommonTimeline();
       if (timeline.findTimelineItem(time, tolerance)>=0) {
           System.out.println("there is already a TLI at " + time);
           return timeline.findTimelineItem(time, tolerance);
       }
       TimelineItem newTLI = new TimelineItem(timeline.getFreeID(), time);
       timeline.insertAccordingToTime(newTLI);
       int insertedIndex = timeline.lookupID(newTLI.getID());
       fireColumnsAdded(insertedIndex,insertedIndex);
       fireColumnLabelChanged(insertedIndex);
       int lower = lower(insertedIndex-1);
       int upper = upper(insertedIndex);
       fireAreaChanged(lower,upper);       
       return insertedIndex;
    }

    public int[] insertInterval(double start, double end, double tolerance) {
       int[] result = new int[2];
       Timeline timeline = transcription.getBody().getCommonTimeline();
       int insertedIndex1;
       int insertedIndex2;
       if (timeline.findTimelineItem(start, tolerance)>=0) {
           insertedIndex1 = timeline.findTimelineItem(start, tolerance);
       } else {
           TimelineItem newTLI = new TimelineItem(timeline.getFreeID(), start);
           timeline.insertAccordingToTime(newTLI);
           insertedIndex1 = timeline.lookupID(newTLI.getID());
           fireColumnsAdded(insertedIndex1,insertedIndex1);
       }
       if (timeline.findTimelineItem(end, tolerance)>=0) {
           insertedIndex2 = timeline.findTimelineItem(end, tolerance);
       } else {
           TimelineItem newTLI = new TimelineItem(timeline.getFreeID(), end);
           timeline.insertAccordingToTime(newTLI);
           insertedIndex2 = timeline.lookupID(newTLI.getID());
           fireColumnsAdded(insertedIndex2,insertedIndex2);
       }
       fireColumnLabelChanged(insertedIndex1);
       fireColumnLabelChanged(insertedIndex2);
       int lower = lower(insertedIndex1-1);
       int upper = upper(insertedIndex2);
       fireAreaChanged(lower,upper);
       result[0] = insertedIndex1;
       result[1] = insertedIndex2;
       return result;
    }

    
    /** throws out all timeline items whose absolute
     *  time values do not fit into a monotonously increasing sequence */
    public void makeTimelineConsistent(){
        transcription.getBody().getCommonTimeline().makeConsistent();
        fireColumnLabelsChanged();        
    }
    
    /** removes all timeline items that are neither the start nor the
     * end point of any event */
    public void removeUnusedTimelineItems(){
        int before = transcription.getBody().getCommonTimeline().getNumberOfTimelineItems();
        transcription.getBody().removeUnusedTimelineItems();
        int after = transcription.getBody().getCommonTimeline().getNumberOfTimelineItems();
        if (before!=after){
            fireDataReset();
        }
    }

    public void removeUnusedTimelineItems(int selectionStartCol, int selectionEndCol) {
        int before = transcription.getBody().getCommonTimeline().getNumberOfTimelineItems();
        transcription.getBody().removeUnusedTimelineItems(selectionStartCol, selectionEndCol);
        int after = transcription.getBody().getCommonTimeline().getNumberOfTimelineItems();
        if (before!=after){
            fireDataReset();
        }
    }

    
    /** removes the absolute time values that have come about
     *  by interpolation */
    public void removeInterpolatedTimes(){
        transcription.getBody().getCommonTimeline().removeInterpolatedTimes();
        fireColumnLabelsChanged();
    }
    
    public void removeTimes() {
        transcription.getBody().getCommonTimeline().removeTimes();
        fireColumnLabelsChanged();
    }
    
    
    /** sets the timeline item at the specified position to a new value */
    public void editTimelineItem(int col, TimelineItem newTimelineItem){
        TimelineItem tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col);
        tli.setTime(newTimelineItem.getTime());
        tli.setType("");
        fireColumnLabelChanged(col);
    }
    
    public void editBookmark(int col, TimelineItem newTimelineItem){
        TimelineItem tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col);
        tli.setBookmark(newTimelineItem.getBookmark());
        fireColumnLabelChanged(col);        
    }

    public void confirmTimelineItems(int startCol, int endCol) {
        for (int c=startCol; c<=endCol; c++){
            TimelineItem tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(c);
            tli.setType("");
            fireColumnLabelChanged(c);
        }
    }

    
    /** interpolates absolute time values on the timeline */
    public void completeTimeline(){
        completeTimeline(true);
    }

    /** interpolates absolute time values on the timeline */
    public void completeTimeline(boolean linear){
        transcription.getBody().getCommonTimeline().completeTimes(linear, getTranscription());
        fireColumnLabelsChanged();
    }

    public void anchorTimeline(double first, double last) {
        boolean itemAdded = transcription.getBody().getCommonTimeline().anchorTimeline(first, last);
        if (itemAdded){
            this.fireColumnsAdded(getNumColumns()-1, getNumColumns()-1);
        }
        fireColumnLabelsChanged();
    }

    
    /** shifts absolute time values on the timeline */
    public void shiftTimes(double amount){
        transcription.getBody().getCommonTimeline().shiftAbsoluteTimes(amount);
        fireColumnLabelsChanged();        
    }
    
    /** scales absolute time values on the timeline */
    public void scaleTimes(double factor) {
        transcription.getBody().getCommonTimeline().scaleAbsoluteTimes(factor);
        fireColumnLabelsChanged();        
    }
    
    
    /** removes the gap at the specified position */
    public void removeGap(int col){
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        transcription.getBody().removeGap(tli);
        fireDataReset();
        fireSelectionChanged(0,col,false);
    }
    
    /** removes all gaps from the transcription */
    public void removeAllGaps(){
        transcription.getBody().removeAllGaps();
        fireDataReset();
    }

    public void smoothTimeline(double threshhold) {
        transcription.getBody().smoothTimeline(threshhold);
        fireDataReset();
    }


    public int normalizeWhitespace() {
        int count=0;
        for (int tierno=0; tierno<transcription.getBody().getNumberOfTiers(); tierno++){
            Tier tier = getTier(tierno);
            for (int eventno=0; eventno<tier.getNumberOfEvents(); eventno++){
                Event event = tier.getEventAt(eventno);
                String text = event.getDescription();
                String changedText = text.replaceAll("\\s{2,}", " ");
                if (!text.equals(changedText)){
                    count++;
                    event.setDescription(changedText);
                    fireValueChanged(tierno, getColumnNumber(event.getStart()));
                }
            }
        }
        if (count>0){
            fireFormatReset();
        }
        return count;
    }


    // recalculates the length of pauses for all events that contain only a pause description
    // assumes GAT syntax for pauses, i.e. (6.3)
    public int updatePauses() {
        int count=0;
        for (int tierno=0; tierno<transcription.getBody().getNumberOfTiers(); tierno++){
            Tier tier = getTier(tierno);
            for (int eventno=0; eventno<tier.getNumberOfEvents(); eventno++){
                Event event = tier.getEventAt(eventno);
                String text = event.getDescription();
                if (text.matches("\\(\\d{1,2}\\.\\d{1,2}\\) ?")){
                    try {
                        double start = transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getStart()).getTime();
                        double end = transcription.getBody().getCommonTimeline().getTimelineItemWithID(event.getEnd()).getTime();
                        if ((start < 0) || (end < 0)) {
                            continue;
                        }
                        double pauseLength = (end - start) * 1000.0;
                        String newPauseText = "(" + Double.toString(Math.round(pauseLength / 10.0) / 100.0) + ") ";
                        if (!text.trim().equals(newPauseText.trim())) {
                            count++;
                            event.setDescription(newPauseText);
                            fireValueChanged(tierno, getColumnNumber(event.getStart()));
                        }
                    } catch (JexmaraldaException ex) {
                        // do nothing
                        ex.printStackTrace();
                    }
                }                                
            }
        }
        return count;
    }

    /** for FOLKER search */
    public int[] findInEvents(Pattern p, int startTier, int startTLI, int startPos2) {
        int[] result = new int[4];
        Event e;
        try {
            e = getEvent(startTier, startTLI);
        } catch (JexmaraldaException ex) {
            e = null;
        }
        if (e!=null){
            String searchString = e.getDescription();
            Matcher m = p.matcher(searchString);
            if (m.find(startPos2)){
                result[0] = startTier;
                result[1] = startTLI;
                result[2] = m.start();
                result[3] = m.end();
                return result;
            }
        }
        if (startTier<getNumRows()-1){
            return findInEvents(p, startTier+1, startTLI, 0);
        }
        if (startTLI<getNumColumns()-1){
            return findInEvents(p, 0, startTLI+1, 0);
        }
        return null;
    }

    public void replaceRegion(BasicTranscription region) {
        BasicBody into = getTranscription().getBody();
        BasicBody insert = region.getBody();

        Timeline tl = insert.getCommonTimeline();
        String startID = tl.getTimelineItemAt(0).getID();
        String endID = tl.getTimelineItemAt(tl.getNumberOfTimelineItems()-1).getID();
        int start = into.getCommonTimeline().lookupID(startID);
        int end = into.getCommonTimeline().lookupID(endID);

        // remove all events between the two tlis
        for (int row = 0; row<into.getNumberOfTiers(); row++){
            Tier tier = into.getTierAt(row);
            for (int pos=start; pos<end; pos++){
                String tliID = into.getCommonTimeline().getTimelineItemAt(pos).getID();
                if (!(tier.containsEventAtStartPoint(tliID))) continue;
                int col = into.getCommonTimeline().lookupID(tliID);
                try {
                    tier.removeEventAtStartPoint(tliID);
                    /*fireCellSpanChanged(row, col);
                    fireCellFormatChanged(row, col);
                    fireValueChanged(row, col);
                    fireAreaChanged(lower(col), upper(col));*/
                } catch (JexmaraldaException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // remove the timeline items
        into.getCommonTimeline().removeTimelineInterval(start, end+1);
        //fireColumnsDeleted(start, end+1);

        for (int pos=0; pos<tl.getNumberOfTimelineItems(); pos++){
            TimelineItem tli = tl.getTimelineItemAt(pos);
            into.getCommonTimeline().insertTimelineItemAt(tli, start+pos);
            //fireColumnsAdded(start+pos, start+pos);
        }

        // insert the new events
        for (int row = 0; row<into.getNumberOfTiers(); row++){
            Tier tier = into.getTierAt(row);
            Tier regionTier = insert.getTierAt(row);
            for (int pos=0; pos<regionTier.getNumberOfEvents(); pos++){
                Event event = regionTier.getEventAt(pos);
                tier.addEvent(event);
                int col = into.getCommonTimeline().lookupID(event.getStart());
                /*fireEventAdded(row, col , into.getCommonTimeline().lookupID(event.getEnd()));
                fireCellSpanChanged(row, col);
                fireCellFormatChanged(row, col);
                fireValueChanged(row, col);
                fireAreaChanged(lower(col), upper(col));*/
            }
        }

        /*fireAreaChanged(into.getCommonTimeline().lookupID(startID), into.getCommonTimeline().lookupID(endID));
        fireFormatReset();
        fireColumnLabelsChanged();
        fireColumnLabelsFormatChanged();*/

        fireDataReset();
    }





}