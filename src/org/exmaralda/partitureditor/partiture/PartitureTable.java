/*
 * PartitureTable.java
 *
 * Created on 10. August 2001, 16:32
 */

package org.exmaralda.partitureditor.partiture;

import org.exmaralda.partitureditor.jexmaralda.TierFormat;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import com.klg.jclass.table.*;
import java.awt.Color;
import java.util.*;

/**
 * implements the abstract methods of the parent class and provides the interface
 * to the BasicTranscriptionTableModel, i.e. to the basic transcription
 * also contains the different panels, i.e.
 * a) the virtual keyboard
 * b) the media panel
 * c) the segmentation panel
 * d) the link panel
 * also contains the large text field
 * @author  Thomas Schmidt, thomas.schmidt@uni-hamburg.de
 * @version 1.0
 */
public class PartitureTable extends AbstractPartitureTable implements org.exmaralda.partitureditor.linkPanel.LinkPanelListener {

    /** determines if the formatting property "EMPTY-EDITOR is applied 
     *  to empty events (true) or if empty events are formatted like the rest
     *  of the tier (false) */
    public boolean diffBgCol = true;
    
    /** determines whether the transcription is locked (i.e. not editable) */
    boolean locked = false;
    /** determines whether the last column is locked */
    public boolean protectLastColumn=false;
    
    /** the table model that populates the table by communication with
     *  a basic transcription */
    private BasicTranscriptionTableModel tableModel;

    /** the link panel */
    public org.exmaralda.partitureditor.linkPanel.LinkPanelDialog linkPanelDialog;
    /** the virtual keyboard */
    public org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardDialog keyboardDialog;
    /** the audio panel */
    public org.exmaralda.partitureditor.sound.AudioPanel mediaPanelDialog;
    /** the segmentation panel */
    //public org.exmaralda.partitureditor.segmentationPanel.SegmentationPanel segmentationPanel;
    /** the praat panel */
    public org.exmaralda.partitureditor.praatPanel.PraatPanel praatPanel;
    /** the annotation dialog */
    public org.exmaralda.partitureditor.annotation.AnnotationDialog annotationDialog;
    /** the IPA Panel */
    public org.exmaralda.partitureditor.ipapanel.IPADialog ipaPanel;
    /** the quick media open dialog */
    public org.exmaralda.partitureditor.jexmaraldaswing.QuickMediaOpenDialog quickMediaOpenDialog;
    
    /** the scale constant */
    public int scaleConstant = +2;
    /** the frame end position
     *  if set to -2, no frame end is set */
    private int frameEndPosition=-2;   
    /** the fallback font for the virtual keyboard and the
     *  font for the text field on top of the partitur */
    public String generalPurposeFontName = "Arial Unicode MS";
    
    /** the text field on top of the partitur */    
    public LargeTextField largeTextField;
    
    /** true if the user has made changes to the current transcription */
    public boolean transcriptionChanged = false;
    /** true if the user has made changes to the current format table */
    public boolean formatChanged = false;
    
    //private Timer timer = new Timer();

    public TimelineItemTableCellRenderer timelineItemTableCellRenderer;
    //public boolean protectLastColumn = true;

    /** the start row of the current user selection */
    public int selectionStartRow;
    /** the end row of the current user selection */
    public int selectionEndRow;
    /** the start column of the current user selection */
    public int selectionStartCol;
    /** the end column of the current user selection */
    public int selectionEndCol;



    
    /** Creates new PartitureTable */
    public PartitureTable() {

        super();


        // initialize large text field
        //largeTextField = new javax.swing.JTextField();
        largeTextField = new LargeTextField(this);
        largeTextField.setFont(new java.awt.Font(generalPurposeFontName, java.awt.Font.PLAIN, 10));
               
        // initialize table model
        tableModel = new BasicTranscriptionTableModel();
        tableModel.addTableDataListener(this);
        setDataSource(tableModel);        
                
        // set table properties
        setSelectionPolicy(JCTableEnum.SELECT_RANGE);   
        setAllowCellResize(JCTableEnum.RESIZE_COLUMN);
        setHorizSBTrack(JCTableEnum.TRACK_ROW);        
        setTraverseCycle(false);
        setTrackSize(new java.awt.Dimension(200,20));
        setTrackBackground(java.awt.Color.orange);
        setCellBorderWidth(1);
        setVertSBDisplay(JCTableEnum.SCROLLBAR_AS_NEEDED);
        setAutoScroll(JCTableEnum.AUTO_SCROLL_COLUMN);
        
        try {
            // set the cell editor for strings
            setCellEditor(Class.forName("java.lang.String"), Class.forName("org.exmaralda.partitureditor.partiture.PartitureCellStringEditor"));
        } catch (ClassNotFoundException e){e.printStackTrace();}        
    }
    
//************** GET METHOD *******************************************       
    
    /** returns the table model */
    public BasicTranscriptionTableModel getModel(){
        return tableModel;
    }
        
    /** returns true iff the table is not currently locked
     *  for editing */
    @Override
    public boolean isEditable(int row, int col){
        super.isEditable(row,col);
        // added 22-03-2009
        boolean dontEditLast = protectLastColumn && (col==tableModel.getNumColumns()-1);
        return ((!dontEditLast) && (!locked) && (!tableModel.getShowSpecialCharacters()));
    }
    
    /** locks or unlocks the partitur 
     *  i.e. makes it uneditable or editable */
    public void setLocked (boolean l){
        locked = l;
    }
    
    /** returns the lock status of the partitur */
    public boolean isLocked(){
        return locked;
    }
    
    /** returns the position of the last tier inside the frame
     *  i.e. the last tier that will appear inside the partitur
     * frame in a visualisation */
    public int getFrameEndPosition(){
        return frameEndPosition;
    }
    
    /** sets the position of the last tier inside the frame
     *  i.e. the last tier that will appear inside the partitur
     * frame in a visualisation */
    public void setFrameEndPosition(int row){
        frameEndPosition = row;
        getModel().fireRowLabelsFormatChanged();   
    }
    
//************** IMPLEMENTATION OF ABSTRACT METHODS DEFINED IN PARENT CLASS *******************************************    
//************** (METHODS HANDLING CHANGES IN THE MODEL)  *******************************************    

    /**Reset all data */
    public void resetData () {  
        selectionStartRow=0;
        selectionEndRow=0;
        selectionStartCol=0;
        selectionEndCol=0;
        showAllTiers();
        System.out.println("Data reset started at " + new java.util.Date().toString());
        progressBar.setString("Resetting data...");
        progressBar.setValue(0);
        setAllSpans();
        progressBar.setValue(50);
        resetFormat(false);
        progressBar.setValue(100);
        setHorizSBTrack(JCTableEnum.TRACK_ROW);        
        setTrackCursor(true);

        //setNewSelection(0,0,false);
        
        System.out.println("Data reset done at " + new java.util.Date().toString());
    }

   /** prepare the reset, i.e. cancel editing activities etc. */
    void prepareReset(){
        setTrackCursor(false);
        setCursor(java.awt.Cursor.WAIT_CURSOR);
        cancelEdit(true);
        traverse(0,0,false,false);
        setHorizSBTrack(JCTableEnum.TRACK_LIVE);                
    }

    /** change the value in the specified cell */
    void changeValue(int row, int col){
        transcriptionChanged = true;
        cancelEdit(true);
        try {linkPanelDialog.getLinkPanel().setEvent(getModel().getEvent(row,col), row, col);}
        catch(JexmaraldaException je){} // should never get here?            
    }

    /** add a row at the specified position */
    void addRow(int row){
        if (row<tableModel.getNumColumns()-1){ // i.e. this is not the last row --> push the spans downwards
            progressBar.setString("Pushing tiers...");
            for (int r=getModel().getNumRows(); r>=row+1; r--){
                for (int c=0; c<getModel().getNumColumns(); c++){
                        JCCellRange range = this.getSpannedRange(r-1,c);
                        if (range!=null){
                            JCCellRange newRange = new JCCellRange(r,range.start_column,r,range.end_column);
                            this.removeSpannedRange(range);
                            this.addSpannedRange(newRange);
                        }            
                }
            }
        }

        for (int c=0; c<getModel().getNumColumns(); c++){   // set the spans of the new row
            changeCellSpan(row, c);
        }

        formatRowLabels();
        formatRow(row);
    }
    
    /** remove the row at the specified position */
    void removeRow(int row){
        progressBar.setString("Removing tier...");

        for (int c=0; c<getModel().getNumColumns(); c++){ // remove the spans belonging to this row
           JCCellRange range = this.getSpannedRange(row,c);
           if (range!=null){ this.removeSpannedRange(range); }
        }

        for (int r=row; r<getModel().getNumRows(); r++){    // "suck" the spans of the rows below upwards
            for (int c=0; c<getModel().getNumColumns(); c++){
                    JCCellRange range = this.getSpannedRange(r+1,c);
                    if (range!=null){
                        JCCellRange newRange = new JCCellRange(r,range.start_column,r,range.end_column);
                        this.removeSpannedRange(range);
                        this.addSpannedRange(newRange);
                    }            
            }
        }

        formatRowLabels();

    }
          
    
    /** swap the specified rows */
    void exchangeRows(int row1, int row2) {        
        progressBar.setString("Swapping tiers...");
        Vector rangeVector = new Vector();
        for (int col=0; col<getModel().getNumColumns(); col++){ //remove all spans from the first row, but store them in a vector
            JCCellRange range = this.getSpannedRange(row1,col);
            if (range!=null){
                rangeVector.addElement(range);
                this.removeSpannedRange(range);
            }
        }
        for (int col=0; col<getModel().getNumColumns(); col++){ //copy all spans from the second row to the first row
            JCCellRange range = this.getSpannedRange(row2,col);
            if (range!=null){
                JCCellRange newRange = new JCCellRange(row1,range.start_column, row1, range.end_column);
                this.removeSpannedRange(range);
                this.addSpannedRange(newRange);
            }
        }
        
        for (int pos=0; pos<rangeVector.size(); pos++){ // copy the spans stored in the Vector to the second row and then fuck off
            JCCellRange range = (JCCellRange)rangeVector.elementAt(pos);
            JCCellRange newRange = new JCCellRange(row2,range.start_column, row2, range.end_column);
            this.addSpannedRange(newRange);
        }
        
        formatRowLabels();
        formatRow(row1);
        formatRow(row2);        
    }

    /** add a column at the specified position */
    void addColumn(int col){
        
        if (col==tableModel.getNumColumns()-1){ // i.e. this is the last column
            setPixelWidth(col, 200);
        }
        
        else { // there is a "hot" range including the added column
            progressBar.setString("Pushing timeline...");
            int previousClearCut = getModel().lower(col-1); // determine lower bound of hot range
            int nextClearCut = getModel().upper(col); // determine upper bound of hot range
            for (int c = getModel().getNumColumns()-1; c>=nextClearCut-1; c--){   // push spans behind hot range one column to the right
                for (int row=0; row<getModel().getNumRows(); row++){
                    JCCellRange range = this.getSpannedRange(row,c);
                    if ((range!=null) && (range.start_column >=c)){
                        JCCellRange newRange = new JCCellRange(row,range.start_column+1,row,range.end_column+1);
                        this.removeSpannedRange(range);
                        this.addSpannedRange(newRange);
                    }
                }                
            }
            
            for (int c = previousClearCut; c<nextClearCut; c++){    // remove all spans in the hot range
                for (int row=0; row<getModel().getNumRows(); row++){
                    JCCellRange range = this.getSpannedRange(row,c);
                    if (range!=null){
                        this.removeSpannedRange(range);
                    }
                }                
            }
            for (int c = previousClearCut; c<nextClearCut; c++){    // set cell spans in the hot area anew
                for (int row=0; row<getModel().getNumRows(); row++){
                   changeCellSpan(row,c);
                }
            }
        }

        for (int row=0; row<tableModel.getNumRows(); row++){
            formatCell(row,col);
        }            
    }
       
    /** does nothing */
    void removeColumn(int col) {
    }

    /** set the selection to the specified position, begin edit at this position if required 
     *  this method is overriden in PartitureTableWithActions */
    void setNewSelection(int row, int col, boolean beginEdit){        
        clearSelection();
        makeVisible(row,col);
        setSelection(row,col,row,col);        
        if (beginEdit && isEditable(row, col)) {
            beginEdit(row,col);            
        }
    }
    
    /**
     * Scrolls the event in the tier with the given tierID at the timepoint with the given
     * TLI into the visible area of the scrollport
     */    
    public void makeVisible(String tierID, String timeID){
        int row = getModel().getTranscription().getBody().lookupID(tierID);
        int col = getModel().getTranscription().getBody().getCommonTimeline().lookupID(timeID);
        makeVisible(row,col);
        // added 02-07-2008
        setLeftColumn(col);
        this.setSelection(row,col,row,col);
    }
    
    public void makeVisible(org.exmaralda.partitureditor.jexmaralda.Event event){
        if (event.getUDEventInformation().containsAttribute("Tier-ID")){
            makeVisible(event.getUDEventInformation().getValueOfAttribute("Tier-ID"), event.getStart());
        }
    }
    
    public void makeVisible(double time){
        int col = getModel().getTranscription().getBody().getCommonTimeline().getPositionForTime(time);
        setLeftColumn(col);
    }
        
//************** METHODS FOR FORMATTING *******************************************    
    
    /** makes all hidden tiers reappear */
    public void showAllTiers(){
        setRowHidden(JCTableEnum.ALLCELLS, false);
        getModel().fireAreaChanged(0,getModel().getNumColumns());
        sizeAllRowHeights();
    }

    /** reset the format of the whole table, i.e. set formats and calculate cell widths */
    public void resetFormat(boolean updatePG) {
        progressBar.setString("Resetting format...");
        if (updatePG) {
            progressBar.setMaximum(30+5*getModel().getNumRows());
            progressBar.setValue(0);
        }
        formatRowLabels();
        progressBar.setValue(progressBar.getValue()+15);            
        formatColumnLabels();
        if (!updatePG) {progressBar.setValue(progressBar.getValue()+15-getModel().getNumRows());}
        for (int row=0; row<getModel().getNumRows();row++){
            progressBar.setString("Formatting tier " + row + "...");
            if (updatePG) progressBar.setValue(progressBar.getValue()+row*5);            
            else progressBar.setValue(progressBar.getValue()+1);            
            formatRow(row);
        }
        changeArea(0,getModel().getNumColumns());
        if (updatePG) {
            progressBar.setMaximum(100);
            progressBar.setValue(100);
            progressBar.setString("Done.");
        }
    }
    
    /** format the row labels */
    void formatRowLabels(){
        progressBar.setString("Formatting row labels...");
        TierFormat labelFormat = tableModel.getRowLabelFormat();
        
        CellStyleModel style = getUniqueCellStyle(0, JCTableEnum.LABEL);
        style.setHorizontalAlignment(labelFormat.getAlignment());
        style.setFont(labelFormat.getFont(scaleConstant));    
        style.setBackground(labelFormat.getBgcolor());
        style.setForeground (labelFormat.getTextcolor());
        style.setClipHints(JCTableEnum.SHOW_NONE);
        
        // added in version 1.2.6.
        JCCellBorder border = new JCCellBorder(JCTableEnum.BORDER_ETCHED_OUT);
        style.setCellBorder(border);
        
        setCellStyle(JCTableEnum.ALLCELLS,JCTableEnum.LABEL,style);      
        
        // added in Version 1.2.6.
        if ((frameEndPosition >= 0) && (frameEndPosition < getModel().getNumRows())){
            CellStyleModel outsideFrameStyle = getUniqueCellStyle(0, JCTableEnum.LABEL);         
            outsideFrameStyle.setCellBorderColor(java.awt.Color.black);
            JCCellBorder noBorder = new JCCellBorder(JCTableEnum.BORDER_ETCHED_IN);
            outsideFrameStyle.setCellBorder(noBorder);
            for (int pos=frameEndPosition+1; pos<getModel().getNumRows(); pos++){
                setCellStyle(pos,JCTableEnum.LABEL,outsideFrameStyle);      
            }
        }
        
        sizeColumnWidth(JCTableEnum.LABEL);
        
        for (int row=0; row<getModel().getNumRows(); row++){
            sizeRowHeight(row);
        }
    }



    /** format the column labels */
    void formatColumnLabels(){ 
        progressBar.setString("Formatting column labels...");
        TierFormat labelFormat = tableModel.getColumnLabelFormat();

        CellStyleModel style = getUniqueCellStyle(JCTableEnum.LABEL,0);
        style.setHorizontalAlignment(labelFormat.getAlignment());
        style.setFont(labelFormat.getFont(scaleConstant));    
        style.setBackground(labelFormat.getBgcolor());
        style.setForeground (labelFormat.getTextcolor());
        style.setClipHints(JCTableEnum.SHOW_NONE);

        setCellStyle(JCTableEnum.LABEL,JCTableEnum.ALLCELLS,style);

        if (protectLastColumn){
            CellStyleModel lastItemStyle =
                getUniqueCellStyle(JCTableEnum.LABEL, getModel().getNumColumns()-1);
            lastItemStyle.setBackground(Color.RED);
            //lastItemStyle.setHorizontalAlignment(CellStyleModel.RIGHT);
            setCellStyle(JCTableEnum.LABEL, getModel().getNumColumns()-1,lastItemStyle);

            // added 19-01-2010: penultimate column is something special too
            CellStyleModel penultItemStyle =
                getUniqueCellStyle(JCTableEnum.LABEL, getModel().getNumColumns()-2);
            penultItemStyle.setBackground(Color.YELLOW);
            setCellStyle(JCTableEnum.LABEL, getModel().getNumColumns()-2,penultItemStyle);

        }

        sizeRowHeight(JCTableEnum.LABEL);
    }

    
    /** format the specified cell */
    void formatCell(int row, int col){
        TierFormat tierFormat = tableModel.getFormat(row);
        TierFormat emptyFormat = tableModel.getEmptyFormat();
        CellStyleModel style = getUniqueCellStyle(row,col);
        style.setHorizontalAlignment(tierFormat.getAlignment());
        style.setFont(tierFormat.getFont(scaleConstant));    
        style.setForeground (tierFormat.getTextcolor());
        style.setClipHints(JCTableEnum.SHOW_NONE);
        if (getModel().containsEvent(row, col)){
            style.setBackground(tierFormat.getBgcolor());
        }
        else if (this.diffBgCol) {
            style.setBackground(emptyFormat.getBgcolor());
        }            
        if (getModel().containsLink(row,col) && diffBgCol){
            style.setCellBorderColorMode(JCTableEnum.USE_CELL_BORDER_COLOR);            
            style.setCellBorderColor(java.awt.Color.red);
        } else {
            style.setCellBorderColorMode(JCTableEnum.BASE_ON_BACKGROUND);
        }
        setCellStyle(row,col,style);          
    }
    
    /** format the specified row */
    void formatRow(int row){
        TierFormat tierFormat = tableModel.getFormat(row);
        JCCellStyle style = new JCCellStyle();
        style.setHorizontalAlignment(tierFormat.getAlignment());
        style.setFont(tierFormat.getFont(scaleConstant));    
        style.setBackground(tierFormat.getBgcolor());
        style.setForeground (tierFormat.getTextcolor());
        style.setClipHints(JCTableEnum.SHOW_NONE);
        style.setCellBorderColorMode(JCTableEnum.BASE_ON_BACKGROUND);
        setCellStyle(row,JCTableEnum.ALLCELLS,style);  
        sizeRowHeight(row);

        if (diffBgCol){
            JCCellStyle emptyStyle = (JCCellStyle)style.clone();
            java.awt.Color emptyColor = tableModel.getEmptyFormat().getBgcolor();
            emptyStyle.setBackground(emptyColor);

            JCCellStyle linkStyle = (JCCellStyle)style.clone();
            linkStyle.setCellBorderColorMode(JCTableEnum.USE_CELL_BORDER_COLOR);
            linkStyle.setCellBorderColor(java.awt.Color.red);

            int noEvents = tableModel.getTranscription().getBody().getTierAt(row).getNumberOfEvents();
            int noTLIs = tableModel.getTranscription().getBody().getCommonTimeline().getNumberOfTimelineItems();
            
            if (noEvents > (noTLIs/2)){
                for (int col=0; col<tableModel.getNumColumns(); col++){
                    if (!getModel().containsEvent(row, col)){
                        setCellStyle(row,col,emptyStyle);
                    } else if (getModel().containsLink(row, col)){
                        setCellStyle(row,col,linkStyle);
                    }            
                }
            } else {
                setCellStyle(row,JCTableEnum.ALLCELLS,emptyStyle);  
                for (int col=0; col<tableModel.getNumColumns(); col++){
                    if (getModel().containsEvent(row, col)){
                        setCellStyle(row,col,style);
                    }
                    if (getModel().containsLink(row, col)){
                        setCellStyle(row,col,linkStyle);
                    }            
                }
            }
            formatCell(row,0);  // necessary, otherwise first cells will not get correct border color!?!?
        }
    }
    
 
    /** set the cell span of the specified cell */
    void changeCellSpan(int row, int col) {
        JCCellRange oldRange = getSpannedRange(row,col);
        int currentSpan = tableModel.getCellSpan(row,col);        
        if (oldRange!=null){    // i.e. cell(row,col) is currently in a spanned range
            int oldSpan = oldRange.end_column - oldRange.start_column + 1;
            if (oldRange.start_column==col){ // i.e. the spanned range "belongs" to this cell
                if (oldSpan!=currentSpan){ // i.e. the span of the range has changed
                    if (currentSpan>1){
                        oldRange.reshape(row,col,row,col+currentSpan-1);
                    }
                    else {  // i.e. the span is 1
                        removeSpannedRange(oldRange);
                    }
                }                
            }
        }
        else { // i.e. cell(row,col) is currently not in a spanned range
            if (currentSpan>1){ // i.e. it SHOULD be in a spanned range
                JCCellRange newRange = new JCCellRange(row,col,row,col+currentSpan-1);
                addSpannedRange(newRange);
            }
        }
    }    

    /** recalculate cell widths between the specified columns */
    void changeArea(int col1, int col2) {
        progressBar.setString("Calculating widths...");
        //timer.setStart();
        for (int col=col1; col<col2; col++){
            sizeColumnWidth(col);
        }
        //timer.out("Sizing cells of span 1");
        //timer.setStart();
        int[] rows = getIndicesOfVisibleRows();
        for (int col=col1; col<col2; col++){
            for (int pos=0; pos<rows.length;pos++){
                int row = rows[pos];
                int span = tableModel.getCellSpan(row,col);
                if (span > 1){
                    int requiredWidth = tableModel.getCellWidth(row,col, scaleConstant);
                    int currentWidth = getCombinedPixelWidth(col, span);
                    if (requiredWidth > currentWidth){
                        increaseWidth(col, span, (requiredWidth - currentWidth));
                    }
                }
            }
        }                   
        //timer.out("Sizing cells of span >1");
    }
    
    /** remove and set anew all cell spans */
    private void setAllSpans(){
        progressBar.setString("Setting spans...");
        this.clearSpannedRanges();
        for (int row=0; row<tableModel.getNumRows(); row++){    // set new cell spans
            for (int col=0; col< tableModel.getNumColumns(); col++){
                int span = tableModel.getCellSpan(row,col);
                if (span>1){
                    JCCellRange range = new JCCellRange(row,col,row,col+span-1);
                    addSpannedRange(range);
                }
            }
        }        
    }
    
    //********** CONVENIENCE METHODS FOR RESIZING ********************


    /** recalaculates the width of the specified column */
    public void sizeColumnWidth(int col){
        if(col==-1) {  // i.e. column is row label column
            setPixelWidth(col, JCTableEnum.VARIABLE);
            setPixelWidth(col, JCTableEnum.AS_IS);
            setPixelWidth(col, getColumnPixelWidth(col));
            return;
        }
        if (getModel().timeProportional){
            setPixelWidth(col, getModel().getCellWidth(0, col, 0));
            return;
        }
        if (col==tableModel.getNumColumns()-1){ // i.e. this is the last column
            setPixelWidth(col, 200);
        }      
        else if (col>=0){
            int[] rows = getIndicesOfVisibleRows();
            // changed 14-01-2009, this is the minimum width for a cell
            int maxWidth = 40;
            for (int pos=0; pos<rows.length; pos++){
                if (getModel().getCellSpan(rows[pos],col)==1){
                    int width = getModel().getCellWidth(rows[pos],col,this.scaleConstant);
                    maxWidth=Math.max(width, maxWidth);
                }
            }
            setPixelWidth(col,maxWidth);
        }
    }
    
    /** recalaculates the height of the specified row */
    private void sizeRowHeight(int row){
        int height = getModel().getRowHeight(row, scaleConstant);
        setPixelHeight(row, Math.max(10,Math.round(height*1.10F))+4);
    }
    
    /** recalculates the height of all rows */
    void sizeAllRowHeights(){
        for (int row=0; row<getNumRows(); row++){
            sizeRowHeight(row);
        }
    }
        
    /** returns the actual (not the required!) width of
     * all coluumns from col to col+span-1 */
    public int getCombinedPixelWidth(int col, int span){
        int result = 0;
        for (int pos=col; pos<col+span; pos++){
            result+=getColumnPixelWidth(pos);
        }
        return result;
    }
       
    /** increases the width of the specified column by the value of increase */
    private void increaseWidth(int col, int span, int increase){
        int increaseEach = Math.round(increase/span);
        for (int pos=col; pos < col+span; pos++){
            int currentWidth = getColumnPixelWidth(pos);
            setPixelWidth(pos, currentWidth + increaseEach);
        }
    }

    //************** UTILITY METHODS FOR SUBCLASS *******************************************    
    
    /** returns the indeces of visible rows */
    public int[] getIndicesOfVisibleRows(){
        Vector resultVector = new Vector();
        for (int row=0; row<getModel().getNumRows(); row++){
            if (!isRowHidden(row)){
                resultVector.addElement(new Integer(row));
            }
        }
        int[] result = new int[resultVector.size()];
        for (int pos=0; pos<resultVector.size(); pos++){
            result[pos] = ((Integer)resultVector.elementAt(pos)).intValue();
        }
        return result;
    }
    
    /** for Link Panel listener: if a link has been added or
     *  removed, the corresponding cell must be reformatted */
    public void linkChanged(int row, int col) {
        formatCell(row,col);        
    }    


    
}