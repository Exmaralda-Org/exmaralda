/*
 * AbstractListTranscriptionTable.java
 *
 * Created on 23. Juni 2008, 14:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import java.awt.Font;
import org.exmaralda.folker.data.EventListTranscription;
import org.exmaralda.folker.data.Timepoint;
import javax.swing.table.*;
import org.exmaralda.folker.utilities.FOLKERInternationalizer;

/**
 *
 * @author thomas
 */
public abstract class AbstractListTranscriptionTable extends javax.swing.JTable {
    
    public TimepointTableCellRenderer timeCellRenderer;
    public SpeakerCellEditor speakerCellEditor;

    public String checkRegex = ".*";

    boolean syntaxControl = true;
    boolean timeControl = true;

    // added 22-02-2010
    Font mainFont = null;

    
    /** Creates a new instance of AbstractListTranscriptionTable */
    public AbstractListTranscriptionTable() {
    }


    public void setFont(String fontName){
        if (fontName.length()>0){
            mainFont = new Font(fontName, Font.PLAIN, 13);
            reformat();
        } else {
            mainFont = null;
        }
    }

    public String getCheckRegex() {
        return checkRegex;
    }

    public void setCheckRegex(String checkRegex) {
        this.checkRegex = checkRegex;
    }

    public void setSyntaxControlActivated(boolean activated){
        syntaxControl = activated;
        reformat();
        /*AbstractListTranscriptionTableModel tmodel = (AbstractListTranscriptionTableModel)getModel();
        for (int row=0; row<this.getRowCount()-tmodel.ADDITIONAL_ROWS; row++){
            tmodel.fireTableCellUpdated(row, 5);
        }*/

    }
            
    public void setTimeControlActivated(boolean activated){
        timeControl = activated;
        reformat();
        /*AbstractListTranscriptionTableModel tmodel = (AbstractListTranscriptionTableModel)getModel();
        for (int row=0; row<this.getRowCount()-tmodel.ADDITIONAL_ROWS; row++){
            tmodel.fireTableCellUpdated(row, 6);
        }*/
    }

    void reformat(){
        if (mainFont==null){
            setFont(getFont().deriveFont(13.0f));
        } else {
            setFont(mainFont);
        }
        
        TableColumnModel cmodel = getColumnModel();
                
        CountingTableCellRenderer countingTableCellRenderer = new CountingTableCellRenderer();
        cmodel.getColumn(0).setCellRenderer(countingTableCellRenderer);        
        
        timeCellRenderer = new TimepointTableCellRenderer();
        cmodel.getColumn(1).setCellRenderer(timeCellRenderer);
        cmodel.getColumn(2).setCellRenderer(timeCellRenderer);
        
        SpeakerTableCellRenderer speakerCellRenderer = new SpeakerTableCellRenderer();
        cmodel.getColumn(3).setCellRenderer(speakerCellRenderer);
        
        CheckTableCellRenderer ctcr = new CheckTableCellRenderer();
        ctcr.setActive(syntaxControl);
        cmodel.getColumn(5).setCellRenderer(ctcr);

        CheckTableCellRenderer ctcr2 = new CheckTableCellRenderer();
        ctcr2.setActive(timeControl);
        cmodel.getColumn(6).setCellRenderer(ctcr2);

        cmodel.getColumn(1).setPreferredWidth(200);
        cmodel.getColumn(2).setPreferredWidth(200);
        cmodel.getColumn(3).setPreferredWidth(150);
        cmodel.getColumn(4).setPreferredWidth(2000);
        
        cmodel.getColumn(0).setHeaderValue("");
        cmodel.getColumn(1).setHeaderValue(FOLKERInternationalizer.getString("gui.start"));
        cmodel.getColumn(2).setHeaderValue(FOLKERInternationalizer.getString("gui.end"));
        cmodel.getColumn(3).setHeaderValue(FOLKERInternationalizer.getString("gui.speaker"));
        cmodel.getColumn(4).setHeaderValue(FOLKERInternationalizer.getString("gui.transcriptiontext"));
        cmodel.getColumn(5).setHeaderValue(FOLKERInternationalizer.getString("gui.syntax"));
        cmodel.getColumn(6).setHeaderValue(FOLKERInternationalizer.getString("gui.time"));

        updateSpeakerlist();
    }


    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (dataModel instanceof AbstractListTranscriptionTableModel){
            reformat();
        }
    }

    
    public void updateSpeakerlist(){
        EventListTranscription elt = ((AbstractListTranscriptionTableModel)getModel()).getTranscription();
        
        TableColumnModel cmodel = getColumnModel();
        speakerCellEditor = new SpeakerCellEditor(elt.getSpeakerlist());
        cmodel.getColumn(3).setCellEditor(speakerCellEditor);          
    }

    public abstract Timepoint getVisiblePosition();

    public void selectNextRow() {        
        int currentSelection = getSelectedRow();
        int nextSelection = 0;
        if (currentSelection > -1 && currentSelection < getModel().getRowCount() - 1) {
            nextSelection = currentSelection + 1;
        }
        getSelectionModel().setSelectionInterval(nextSelection, nextSelection);
        scrollRectToVisible(this.getCellRect(Math.min(nextSelection+2, getModel().getRowCount()-1), 0, true));
    }

    
}
