/*
 * AbstractTranscriptionTableModel.java
 *
 * Created on 11. Januar 2002, 11:47
 */

package org.exmaralda.partitureditor.partiture;

import com.klg.jclass.table.data.*;
import com.klg.jclass.table.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.util.*;

/**
 * implements the basic functionalities for communicating with a JCTable, i.e. implements
 * methods for translating between cells and labels in the table and events etc. in the basic transcription
 * redesigned for version 1.01 for better performance
 * @author  Thomas Schmidt, thomas.schmidt@uni-hamburg.de
 * @version 1.01 (essential changes for better performance!)
 */

public abstract class AbstractTranscriptionTableModel extends AbstractDataSource implements EditableTableDataModel {

    /** the current transcription */
    BasicTranscription transcription;
    
    // added 19-01-2010
    public boolean protectLastColumn = false;
    // added 03-09-2010
    public boolean proportionalPenultimate = true;

    /** the current format table */
    TierFormatTable formats;
    
    private boolean showSpecialChars = false;
    
    public String defaultFontName = "Times New Roman";
    
    public boolean timelineMode = false;

    // relocated in version 1.2.2. for better performance
    /** a dummy text field for font width calcualtions */
    javax.swing.JTextField dummyTextField = new javax.swing.JTextField();
    /** a dummy cell renderer for font width calculations */
    com.klg.jclass.cell.renderers.JCStringCellRenderer renderer = new com.klg.jclass.cell.renderers.JCStringCellRenderer();

    // constants for identifying events 
    /** user has changed selection */
    static final int SELECTION_CHANGED = 15;    
    /** a data reset is to be prepared */
    static final int RESET_APPROACHING = 16;    
    /** a row has been inserted */
    static final int ROW_INSERTED = 17;
    /** the format has been reset */
    static final int FORMAT_RESET = 18;
    /** the format of a specific cell has changed */
    static final int CELL_FORMAT_CHANGED = 19;
    /** the format of an entire row has changed */
    static final int ROW_FORMAT_CHANGED = 20;
    /** the format of the row labels has changed */
    static final int ROW_LABEL_FORMAT_CHANGED = 21;
    /** the format of the column labels has changed */
    static final int COLUMN_LABEL_FORMAT_CHANGED = 22;
    /** rows have been swapped */
    static final int ROWS_SWAPPED = 23;
    /** an area of the partitur has changed */
    static final int AREA_CHANGED = 24;
    /** a cell span has changed */
    static final int CELL_SPAN_CHANGED = 25;

    
    /** Creates new AbstractTranscriptionTableModel */
    public AbstractTranscriptionTableModel() {
        transcription = new BasicTranscription();
        formats = new TierFormatTable(transcription, defaultFontName);
    }

    // Get & Set Methods for the fields of this class
    
    /** returns the basic transcription that this model works with */
    public BasicTranscription getTranscription() {
        return transcription;
    }
    
    /** sets the basic transcription that this model works with */
    public void setTranscription(BasicTranscription t) {
        if (t.getTierFormatTable()==null){
            //System.out.println("NOT THE REAL THANG!");
            transcription = t;
            formats = new TierFormatTable(t, defaultFontName);
            System.out.println("Firing data reset... " + new java.util.Date().toString());
            fireDataReset();
        } else {
            //System.out.println("THE REAL THANG!");
            setTranscriptionAndTierFormatTable(t, t.getTierFormatTable());            
        }
    }
    
    /** sets transcription and format table in one go (this can be reasonable for performance reasons */
    public void setTranscriptionAndTierFormatTable(BasicTranscription t, TierFormatTable tft){
        transcription = t;
        formats = new TierFormatTable(transcription);   // first create a default format table
        formats.setTimelineItemFormat(tft.getTimelineItemFormat());
        String[] tierIDs = tft.getAllTierIDs();
        for (int pos=0; pos<tierIDs.length; pos++){     // then set the formats contained in the given format table (ensures that everything has a format
            try {formats.setTierFormat(tft.getTierFormatForTier(tierIDs[pos]));}
            catch (JexmaraldaException je){
                // should never get here
            }   
        }
        System.out.println("Firing data reset... " + new java.util.Date().toString());
        fireDataReset();
    }
    
    /** returns the format table that this model works with */
    public TierFormatTable getTierFormatTable() {
        return formats;
    }
    
    /** sets the format table that this model works with */
    public void setTierFormatTable(TierFormatTable tft) {
        formats = new TierFormatTable(transcription);   // first create a default format table
        formats.setTimelineItemFormat(tft.getTimelineItemFormat());
        String[] tierIDs = tft.getAllTierIDs();
        for (int pos=0; pos<tierIDs.length; pos++){     // then set the formats contained in the given format table (ensures that everything has a format
            try {formats.setTierFormat(tft.getTierFormatForTier(tierIDs[pos]));}
            catch (JexmaraldaException je){}            // should never get here
        }
        fireFormatReset();                              // CHANGE! in version 1.01
    }
    
    /** determines if space characters are to be shown */
    public void setShowSpecialCharacters(boolean show){
        showSpecialChars = show;
        for (int r=0; r<getNumRows(); r++){
            for (int c=0; c<getNumColumns(); c++){
                fireValueChanged(r,c);
            }
        }
    }

    /** returns true if space characters are currently shown, false otherwise */
    public boolean getShowSpecialCharacters(){
        return showSpecialChars;
    }
    
    // Implementation of methods specified by AbstractDataSource/EditableTableDataModel
    /** returns the content of the cell viz. the description of the corresponding event */
    public java.lang.Object getTableDataItem(int row, int col) {
        try {
            
            Event e = getEvent(row,col);

            // follows code for getting images into the partitur, may have to be revived some day
            /*if ((e.getMedium().equals("img")) && (e.getURL()!=null) && (e.getURL().length()>0)){
                ImageIcon image = new ImageIcon(e.getURL());
                if (image.getIconWidth()>0){
                    return image;
                }                
            }*/
            String rendered = e.getDescription();
            if (!showSpecialChars) {return rendered;}
            StringBuffer sb=new StringBuffer();
            for (int pos=0; pos<rendered.length(); pos++){
                if (rendered.charAt(pos)==' '){
                    sb.append("\u00B7");
                } else {
                    sb.append(rendered.charAt(pos));
                }
            }
            return sb.toString();
        } catch (JexmaraldaException je){     // i.e. there is no event, "the event is empty"
            return new String();
        }
    }
    
    /** returns the content of the columns label viz. the description of the corresponding timeline item 
     *  format is 'n' if no absolute time is assigned and
     *  format is 'n [xxx.y]' if an absolute time is assigned */
    public java.lang.Object getTableColumnLabel(int col) {
        if (col==-1) {return new String();}       
        TimelineItem tli = getTimelineItem(col);
        /*if (this.timelineMode){
            return tli;
        }*/
        if ((tli.getBookmark()==null) || timelineMode) {
            return tli.getDescription(col, formats.getTimelineItemFormat());
        }
        else {
            return tli.getBookmark();
        }
    }
    
    /** returns the content of the row label viz. the description of speaker and category of the corresponding tier */
    public java.lang.Object getTableRowLabel(int row) {
        Tier tier = transcription.getBody().getTierAt(row);
        // changed for Version 1.2.5.
        // return tier.getDescription(transcription.getHead().getSpeakertable());
        return tier.getDisplayName();
    }
    
    /** returns the number of rows viz. the number of tiers */
    public int getNumRows() {
        return transcription.getBody().getNumberOfTiers();
    }

    /** returns the number of columns viz. the length of the timeline */
    public int getNumColumns() {
        return transcription.getBody().getCommonTimeline().getNumberOfTimelineItems();
    }
    
    public String oldValue = null;
    public String newValue = null;
    public boolean valueHasChanged(){
        return ( ((oldValue==null) && (newValue!=null)) ||
                 ((oldValue!=null) && (!(oldValue.equals(newValue)))));
    }

    /** sets the event in the specified position to the specified object */
    public boolean setTableDataItem(final java.lang.Object v,int row,int col) {
        String value = (String)v;
        newValue = value;
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        boolean colAdded = false;
        try {   //works if there already is an event at this position
            boolean specialTreatmentForPenultimateColumn =
                    ((this.protectLastColumn) && (col+2==getNumColumns()));

            if (specialTreatmentForPenultimateColumn){
                // added 19-01-2010
                Timeline tl = transcription.getBody().getCommonTimeline();
                TimelineItem tli1 = tl.getTimelineItemAt(col);
                TimelineItem tli2 = tl.getTimelineItemAt(col+1);
                if ((tli1.getTime()>=0) && (tli2.getTime()>=0) && (tli2.getTime()-tli1.getTime()>5.0)){
                    TimelineItem tli3 = new TimelineItem();
                    tli3.setID(tl.getFreeID());
                    double addTime = 2.0;
                    if (proportionalPenultimate){
                        double spc = transcription.getBody().getSecondsPerCharacter(row);
                        if (spc>0){
                            addTime = Math.max(0.1,value.length()*spc);
                        }
                    }
                    tli3.setTime(tli1.getTime()+ addTime);
                    tl.insertAccordingToTime(tli3);
                    fireColumnsAdded(col+1, col+1);
                    fireColumnLabelsChanged();
                }
            }

            Event event = getEvent(row, col);
            oldValue = event.getDescription();
            event.setDescription(value);

        } catch (JexmaraldaException je){   //i.e. there is not yet an event at this position
            if (col+1 == getNumColumns()){ // i.e. this is the last column, so the timeline must be extended
                Timeline tl = transcription.getBody().getCommonTimeline();
                tl.addTimelineItem();
                
                // changed 25-02-2009: faute nantaise
                // have to make sure that adding an event at the end of a tier does
                // not give the timeline an illogical structure
                if (tl.getTimelineItemAt(col).getTime()>=0){
                    tl.getTimelineItemAt(col+1).setTime(tl.getTimelineItemAt(col).getTime());
                    double newTime1 = tl.getTimelineItemAt(col).getTime() - 1.0;
                    double newTime2 = tl.getTimelineItemAt(col-1).getTime() + 2.0;
                    if (newTime2 > 0){
                        tl.getTimelineItemAt(col).setTime(Math.min(newTime1, newTime2));
                    } else {
                        tl.getTimelineItemAt(col).setTime(newTime1);                        
                    }
                }

                fireColumnsAdded(col+1, col+1);
                colAdded = true;
            } 
            String tli2 = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+1).getID();
            oldValue = null;
            Event newEvent = new Event(tli, tli2, value);
            tier.addEvent(newEvent);
            fireCellFormatChanged(row,col);
        }
        fireValueChanged(row,col);
        fireAreaChanged(lower(col),upper(col));
        if (colAdded) {fireSelectionChanged(row, getNumColumns()-1, true);}
        try {
            if (getEvent(row, col).getDescription().length() == 0) {
                deleteEvent(row, col);
            }
        } catch (JexmaraldaException ex) {/* should not get here */ }
        return true;
    }
    
    /** returns the position of the column that holds
     * the timeline item with the specified ID */
    int getColumnNumber(String tliID){
        return transcription.getBody().getCommonTimeline().lookupID(tliID);
    }
    
    /** returns the position of the row that holds
     * the tier with the specified ID */
    int getRowNumber (String tierID){
        return transcription.getBody().lookupID(tierID);
    }
    
// *********** FIRE METHODS *****************************************
    
    /** notifies listeners that the content of all row labels has changed */
    public void fireRowLabelsChanged(){
        fireRowLabelChanged(JCTableEnum.ALLCELLS);
    }
           
    /** notifies listeners that the content of all column labels has changed */
    public void fireColumnLabelsChanged(){
       fireColumnLabelChanged(JCTableEnum.ALLCELLS);
    }

    /** notifies listeners that the selection must be changed */
    public void fireSelectionChanged(int row, int col, boolean beginEdit){
        int v=0;
        if (beginEdit){v=1;}
        JCTableDataEvent event = new JCTableDataEvent(this, row, col, v, 0, SELECTION_CHANGED);
        fireTableDataEvent(event);
    }
    
    /** notifies the listeners that a row has been inserted */
    public void fireRowInserted(int row){
        JCTableDataEvent event = new JCTableDataEvent(this, row, 0, 0, 0, ROW_INSERTED);
        fireTableDataEvent(event);
    }
    
    /** notifies the listeners that all data have been reset */
    public void fireDataReset(){
        JCTableDataEvent event = new JCTableDataEvent(this, 0, 0, 0, 0, RESET_APPROACHING);
        fireTableDataEvent(event);
        super.fireDataReset();    
    }

    /** notifies the listeners that all formats have been reset */
    public void fireFormatReset(){
        JCTableDataEvent event = new JCTableDataEvent(this, 0, 0, 0, 0, FORMAT_RESET);
        fireTableDataEvent(event);
    }
    
    /** notifies the listeners that the format of the row has been changed */
    public void fireRowFormatChanged(int row){
        JCTableDataEvent event = new JCTableDataEvent(this, row, 0, 0, 0, ROW_FORMAT_CHANGED);
        fireTableDataEvent(event);        
    }
    
    /** notifies the listeners that the format of the cell has been changed */
    public void fireCellFormatChanged(int row, int col){
        JCTableDataEvent event = new JCTableDataEvent(this, row, col, 0, 0, CELL_FORMAT_CHANGED);
        fireTableDataEvent(event);        
    }
    
    /** notifies the listeners that the format of the column labels has been changed */
    public void fireColumnLabelsFormatChanged(){
        JCTableDataEvent event = new JCTableDataEvent(this, 0, 0, 0, 0, COLUMN_LABEL_FORMAT_CHANGED);
        fireTableDataEvent(event);        
    }

    /** notifies the listeners that the format of the column labels has been changed */
    public void fireRowLabelsFormatChanged(){
        JCTableDataEvent event = new JCTableDataEvent(this, 0, 0, 0, 0, ROW_LABEL_FORMAT_CHANGED);
        fireTableDataEvent(event);        
    }

    /** notifies the listeners that the format of the column labels has been changed */
    public void fireRowsSwapped(int row1, int row2){
        JCTableDataEvent event = new JCTableDataEvent(this, row1, 0, row2, 0, ROWS_SWAPPED);
        fireTableDataEvent(event);        
    }

    /** notifies the listeners that an area of the table has to be resized */
    public void fireAreaChanged(int col1, int col2){
        JCTableDataEvent event = new JCTableDataEvent(this, 0, col1, 0, col2, AREA_CHANGED);
        fireTableDataEvent(event);        
    }
    
    /** notifies the listeners that an event has been added on the specified tier */
    public void fireEventAdded(int row, int col1, int col2){
        fireCellSpanChanged(row, col1);
        fireAreaChanged(lower(col1), upper(col2));
    }

    /** notifies the listeners that the cell span has changed */
    public void fireCellSpanChanged(int row, int col){
        JCTableDataEvent event = new JCTableDataEvent(this, row, col, 0, 0, CELL_SPAN_CHANGED);
        fireTableDataEvent(event);        
    }

    // Methods for getting values from the actual basic transcription    

    /** either returns the event corresponding to the position or
     * throws a JexmaraldaException if there is no such event */
    public Event getEvent(int row,int col) throws JexmaraldaException {
        Tier tier = transcription.getBody().getTierAt(row);
        TimelineItem tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col);
        if ((tli==null) || (tier==null)){
            throw new JexmaraldaException(77, "No tier or no tli");
        }
        return tier.getEventAtStartPoint(tli.getID());
    }
    
    /** returns the tier that is held by the specified row */
    public Tier getTier(int row) {
        return transcription.getBody().getTierAt(row);
    }
    /** returns the timeline item corresponding to the specified position */
    public TimelineItem getTimelineItem(int col) {
        return transcription.getBody().getCommonTimeline().getTimelineItemAt(col);
    }
    
    /** returns the text of the speaker contribution
     * that the event in the specified cell is a part of */
    public String getTurnText(int row, int col){
        if ((row>=this.getNumRows()) || (col>=this.getNumColumns())) return "";
        StringBuffer result = new StringBuffer();
        int startCol=col;
        while ((startCol>0) && (containsEvent(row, startCol-1))){
            startCol--;
        }
        while ((startCol<getNumColumns()) && containsEvent(row, startCol)){
            try{
                result.append(getEvent(row, startCol).getDescription());
                startCol+=getCellSpan(row,startCol);
            } catch (JexmaraldaException je) {}
        }
        return result.toString();
    }
    
    /** gets the tier IDs of the specified rows in the correct order*/
    String[] getIDsOfRows(int[] rows){
        Vector result = new Vector();
        for (int pos=0; pos<rows.length; pos++){
            Tier tier = transcription.getBody().getTierAt(rows[pos]);
            result.addElement(tier.getID());
        }
        return StringUtilities.stringVectorToArray(result);
    }
       
    public BasicTranscription getPartOfTranscription(int[] rows, int startCol, int endCol){
        return this.getPartOfTranscription(rows, startCol, endCol, false);
    }
    /** returns the part of the transcription between the two columns and the specified rows */
    public BasicTranscription getPartOfTranscription(int[] rows, int startCol, int endCol, boolean anchor){
        return transcription.getPartOfTranscription(getIDsOfRows(rows), getTimelineItem(startCol).getID(), getTimelineItem(endCol).getID(), anchor);
    }
    // ******************** BOOLEAN METHODS ************************************************
    
    /** returns true if the specified cell contains an event, false otherwise */
    public boolean containsEvent(int row,int col) {
        if ((row>=this.getNumRows()) || (col>=this.getNumColumns())) return false;
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        if (tier.containsEventAtStartPoint(tli)){
            return true;
        }
        return false;
    }
    
    /** returns true if the specified cell contains a link, false otherwise */
    boolean containsLink(int row, int col){
        if ((row>=this.getNumRows()) || (col>=this.getNumColumns())) return false;
        try {
            Event event = getEvent(row,col);
            if (!event.getMedium().equals("none")){
                return true;
            } else {
                return false;
            }
        } catch (JexmaraldaException je) {return false;}
    }
            

    /** returns true if the cell to the right of the specified cell is free, false otherwise
     * if the specified cell is on the right edge of the transcription, also returns true */
    boolean cellToTheRightIsFree(int row, int col){
        if ((row>=this.getNumRows()) || (col>=this.getNumColumns())) return false;
        Tier tier = transcription.getBody().getTierAt(row);
        int span = getCellSpan(row,col);
        if ((col+span) >= getNumColumns()){return true;}
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col+span).getID();
        return (!tier.containsEventAtStartPoint(tli));
    }
    
    /** returns true if the cell to the left of the specified cell is free, false otherwise
     * if the specified cell is on the left edge of the transcription, also returns true */
    boolean cellToTheLeftIsFree(int row, int col){
        if ((row>=this.getNumRows()) || (col>=this.getNumColumns())) return false;
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        Event eventBefore = tier.getFirstEventBeforeStartPoint(transcription.getBody().getCommonTimeline(),tli);
        return ((eventBefore==null) || (!eventBefore.getEnd().equals(tli)));
    }
    
    /** returns true if the time interval corresponding to the specified column 
     *  does not contain any events */
    boolean isGap(int col){
        if (col>=this.getNumColumns()) return false;
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        return transcription.getBody().isGap(tli);
    }

// ******************** METHODS FOR RESIZING ************************************************
    /** returns true if a clear cut at this position is possible
     *  i.e. if there are no cells that extend across this column */
    private boolean clearCutIsPossible(int col){
        if (col>=this.getNumColumns()) return false;
        for (int row=0; row<getNumRows(); row++){
           if (getCellSpan(row,col)==0){    // if this cell is in a span
                return false;}
        }
        return true;
    }
    
    /** returns the nearest column to the left of the 
     * specified column at which a clear cut is possible */
    public int lower (int col){
        int leftmostColumn = 0;
        for (int col2=col; col2>=0; col2--){
            if (clearCutIsPossible(col2)){
                leftmostColumn=col2;
                break;
            }
        }       
        return leftmostColumn;
    }
    
    /** returns the nearest column to the right of the 
     * specified column at which a clear cut is possible */
    public int upper (int col){
        int rightmostColumn = getNumColumns();
        for (int col2=col+1; col2<getNumColumns(); col2++){
            if (clearCutIsPossible(col2)){
                rightmostColumn=col2;
                break;
            }
        }     
        return rightmostColumn;
    }
    
    /** returns the required (not actual!) cell span of the specified cell 
     * i.e. how many intervals on the timeline does this event span? */
    public int getCellSpan(int row, int col){
        Timeline timeline = transcription.getBody().getCommonTimeline();
        try {
            Event event = getEvent(row,col);
            return timeline.calculateSpan(event.getStart(), event.getEnd());
        } catch (JexmaraldaException je){   // i.e. no event at this position
            String tli = timeline.getTimelineItemAt(col).getID();
            Tier tier = transcription.getBody().getTierAt(row);
            Event event = tier.getFirstEventBeforeStartPoint(timeline, tli);
            if (event==null) { return 1;}   // i.e. there is no event before this event
            else if (!timeline.before(tli,event.getEnd())) {return 1;}
            else {return 0;}
        }
    }
    
    public boolean timeProportional = false;
    private double pixelsPerSecond;

    public void setTimeProportional(boolean tp){
        setTimeProportional(tp, true);
    }

    public void setTimeProportional(boolean tp, boolean update){
        timeProportional = tp;
        if (tp){
            getTranscription().getBody().getCommonTimeline().completeTimes();
        }

        if (update){
            fireDataReset();
        }
    }
    
    public void setPixelsPerSecond(double pps){
        pixelsPerSecond = pps;
        if (timeProportional){
            fireDataReset();
        }
    }

    /** returns the required (not actual!) cell width of the specified cell
     * i.e. what is the width of the corresponding event description? */
    public int getCellWidth(int row, int col, int scaleFactor){
        if (!timeProportional){
            final int EXTRA_SPACE = 6;       // additional pixels for each cell
            try {
                Event event = getEvent(row,col);
                Tier tier = transcription.getBody().getTierAt(row);
                String description = event.getDescription();
                TierFormat tf = formats.getTierFormatForTier(tier.getID());
                return renderer.getWidth(dummyTextField.getFontMetrics(tf.getFont(scaleFactor)), description) + EXTRA_SPACE;
            } catch (JexmaraldaException je) {
                return 0;
            }
        } else {
            if (col==getTranscription().getBody().getCommonTimeline().getNumberOfTimelineItems()-1){
                return 7;
            }
            TimelineItem tli1 = getTranscription().getBody().getCommonTimeline().getTimelineItemAt(col);
            TimelineItem tli2 = getTranscription().getBody().getCommonTimeline().getTimelineItemAt(col+1);
            return (int)Math.round((tli2.getTime() - tli1.getTime())*pixelsPerSecond);
        }
    }

    /**
     * deletes the event
     */
    public void deleteEvent(int row, int col) {
        Tier tier = transcription.getBody().getTierAt(row);
        String tli = transcription.getBody().getCommonTimeline().getTimelineItemAt(col).getID();
        try {
            tier.removeEventAtStartPoint(tli);
            fireCellSpanChanged(row, col);
            fireCellFormatChanged(row, col);
            fireValueChanged(row, col);
            fireAreaChanged(lower(col), upper(col));
        } catch (JexmaraldaException je) {
        }
    }
    

    

    
    
}
