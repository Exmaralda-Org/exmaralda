/*
 * AbstractPartitureTable.java
 *
 * Created on 25. Oktober 2001, 11:18
 */

package org.exmaralda.partitureditor.partiture;

import com.klg.jclass.table.*;

/**
 * implements the listener-interface and provides abstract versions of the methods
 * needed for dealing with table events
 * @author  Thomas Schmidt, thomas.schmidt@uni-hamburg.de
 * @version 1.0
 */
public abstract class AbstractPartitureTable extends JCTable implements JCTableDataListener  {

    /** the progress bar */
    public javax.swing.JProgressBar progressBar;


    /** Creates new AbstractPartitureTable */
    public AbstractPartitureTable() {
        progressBar = new javax.swing.JProgressBar();        
        progressBar.setPreferredSize(new java.awt.Dimension(120,25));
        progressBar.setMaximumSize(new java.awt.Dimension(300,25));
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
    }

    /** processes change events in the data model */
    public void dataChanged(final com.klg.jclass.table.JCTableDataEvent evt) {
        setEnabled(false);
        int command = evt.getCommand();
        switch(command){

            case JCTableDataEvent.RESET :
                resetData();
                break;
                
            case JCTableDataEvent.CHANGE_VALUE : 
                changeValue(evt.getRow(), evt.getColumn());
                break;
                
            case JCTableDataEvent.ADD_ROW :
                addRow(evt.getRow());
                break;

            case JCTableDataEvent.REMOVE_ROW :
                removeRow(evt.getRow());
                break;
                
            case JCTableDataEvent.ADD_COLUMN :
                addColumn(evt.getColumn());
                break;
            
            case JCTableDataEvent.REMOVE_COLUMN :
                removeColumn(evt.getColumn());
                break;
                
            case JCTableDataEvent.CHANGE_ROW_LABEL :
                formatRowLabels();
                break;
                
            case JCTableDataEvent.CHANGE_COLUMN_LABEL :
                formatColumnLabels();
                break;
                
            case AbstractTranscriptionTableModel.RESET_APPROACHING :
                prepareReset();
                break;       

            case AbstractTranscriptionTableModel.SELECTION_CHANGED :
                if (evt.getNumAffected()==1){
                    setNewSelection(evt.getRow(), evt.getColumn(),true);
                } else {
                    setNewSelection(evt.getRow(), evt.getColumn(),false);
                }                    
                break;
                
            case AbstractTranscriptionTableModel.FORMAT_RESET :
                resetFormat(true);
                break;

            case AbstractTranscriptionTableModel.CELL_FORMAT_CHANGED :
                formatCell(evt.getRow(),evt.getColumn());
                break;                
                
            case AbstractTranscriptionTableModel.ROW_FORMAT_CHANGED :
                formatRow(evt.getRow());
                break;

            case AbstractTranscriptionTableModel.ROW_LABEL_FORMAT_CHANGED :
                formatRowLabels();
                break;

            case AbstractTranscriptionTableModel.COLUMN_LABEL_FORMAT_CHANGED :
                formatColumnLabels();
                break;
                
            case AbstractTranscriptionTableModel.ROWS_SWAPPED :
                exchangeRows(evt.getRow(),evt.getNumAffected());
                break;
                
            case AbstractTranscriptionTableModel.AREA_CHANGED :
                changeArea(evt.getColumn(),evt.getDestination());
                break;
            
            case AbstractTranscriptionTableModel.CELL_SPAN_CHANGED :
                changeCellSpan(evt.getRow(), evt.getColumn());
        }
        setEnabled(true);        
        progressBar.setString("Done.");
    }

    /** to be implemented in the subclass */
    abstract void resetData ();
        
    /** to be implemented in the subclass */
    abstract void prepareReset();

    /** to be implemented in the subclass */
    abstract void changeValue(int row, int col);
    /** to be implemented in the subclass */
    abstract void changeCellSpan(int row, int col);
    /** to be implemented in the subclass */
    abstract void changeArea(int col1, int col2);
    
    /** to be implemented in the subclass */
    abstract void addRow(int row);    
    /** to be implemented in the subclass */
    abstract void removeRow(int row);
    /** to be implemented in the subclass */
    abstract void exchangeRows(int row1, int row2);
    
    /** to be implemented in the subclass */
    abstract void addColumn(int col);
    /** to be implemented in the subclass */
    abstract void removeColumn(int col);
    
    /** to be implemented in the subclass */
    abstract void resetFormat(boolean updatePG);
    /** to be implemented in the subclass */
    abstract void formatRowLabels();
    /** to be implemented in the subclass */
    abstract void formatColumnLabels();
    /** to be implemented in the subclass */
    abstract void formatRow(int row);
    /** to be implemented in the subclass */
    abstract void formatCell(int row, int cell);

    /** to be implemented in the subclass */
    abstract void setNewSelection(int row, int col, boolean beginEdit);
    
   
}