/*
 * AddBookmarkAction.java
 *
 * Created on 11. November 2004, 11:22
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.jexmaraldaswing.AddBookmarkDialog;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class AddBookmarkAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of AddBookmarkAction
     * @param t
     * @param icon */
    public AddBookmarkAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Add bookmark...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("addBookmarkAction!");
        table.commitEdit(true);
        addBookmark();
        table.transcriptionChanged = true;        
    }
    
    private void addBookmark(){
        TimelineItem timelineItem = table.getModel().getTimelineItem(table.selectionStartCol);
        AddBookmarkDialog dialog = new AddBookmarkDialog(table.parent, true, timelineItem);
        dialog.setLocationRelativeTo(table.parent);
        dialog.setVisible(true);
        dialog.requestFocus();
        if (dialog.change){
            table.getModel().editBookmark(table.selectionStartCol, dialog.getTimelineItem());
            String bookmark = dialog.getTimelineItem().getBookmark();
            table.status("Added bookmark '" + bookmark + "'");
        }
    }
    

    
}
