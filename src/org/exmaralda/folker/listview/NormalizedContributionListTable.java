/*
 * EventListTableView.java
 *
 * Created on 7. Mai 2008, 16:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.folker.listview;

import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author thomas
 */
public class NormalizedContributionListTable extends JTable  {
   
    public NormalizedContributionTableCellRenderer textAreaCellRenderer;
    public boolean USE_MULTILINE_CELL_RENDERER = true;
    

    /** Creates a new instance of EventListTableView */
    public NormalizedContributionListTable() {
        //setCellSelectionEnabled(true);
        
        setGridColor(new java.awt.Color(102, 204, 255));
        setIntercellSpacing(new java.awt.Dimension(0, 2));
        setShowHorizontalLines(false);        
        //setSelectionMode(getSelectionModel().SINGLE_SELECTION);
        setSelectionMode(getSelectionModel().SINGLE_SELECTION);
        setRowHeight(20);

    }        
       
    public void setFont(String fontName) {
        /*super.setFont(fontName);
        if (mainFont!=null){
            textAreaCellRenderer.setFont(mainFont);
        }
        reformat();*/
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if (dataModel instanceof NormalizedContributionListTableModel){
            reformat();
        }
    }


    void reformat(){
        
        TableColumnModel cmodel = getColumnModel();

        CountingTableCellRenderer countingTableCellRenderer = new CountingTableCellRenderer();
        cmodel.getColumn(0).setCellRenderer(countingTableCellRenderer);

        TimepointTableCellRenderer timeCellRenderer = new TimepointTableCellRenderer();
        cmodel.getColumn(1).setCellRenderer(timeCellRenderer);
        cmodel.getColumn(2).setCellRenderer(timeCellRenderer);

        SpeakerTableCellRenderer speakerCellRenderer = new SpeakerTableCellRenderer();
        speakerCellRenderer.isForFOLKER = false;
        cmodel.getColumn(3).setCellRenderer(speakerCellRenderer);

        textAreaCellRenderer = new NormalizedContributionTableCellRenderer();
        cmodel.getColumn(4).setCellRenderer(textAreaCellRenderer);

        cmodel.getColumn(1).setPreferredWidth(150);
        cmodel.getColumn(2).setPreferredWidth(150);
        cmodel.getColumn(3).setPreferredWidth(150);
        cmodel.getColumn(4).setPreferredWidth(2000);

        cmodel.getColumn(0).setHeaderValue("");
        cmodel.getColumn(1).setHeaderValue("Start");
        cmodel.getColumn(2).setHeaderValue("Ende");
        cmodel.getColumn(3).setHeaderValue("Sprecher");
        cmodel.getColumn(4).setHeaderValue("Transkriptionstext");
        
    }


    

    
}
