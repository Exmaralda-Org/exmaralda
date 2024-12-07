/*
 * BookmarksAction.java
 *
 * Created on 11. November 2004, 14:04
 */

package org.exmaralda.partitureditor.partiture.timelineActions;

import org.exmaralda.partitureditor.jexmaraldaswing.BookmarksDialog;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class BookmarksAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of BookmarksAction
     * @param t
     * @param icon */
    public BookmarksAction(PartitureTableWithActions t, javax.swing.ImageIcon icon) {
        super("Bookmarks...", icon, t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("BookmarksAction!");
        table.commitEdit(true);
        bookmarks();
        table.transcriptionChanged = true;        
    }
    
    private void bookmarks(){
        Timeline tl = table.getModel().getTranscription().getBody().getCommonTimeline();
        BookmarksDialog dialog = new BookmarksDialog(table.parent, false, tl);
        dialog.addSearchResultListener(table);
        dialog.setLocationRelativeTo(table.parent);
        dialog.setVisible(true);
        //dialog.show();
    }
    
}
