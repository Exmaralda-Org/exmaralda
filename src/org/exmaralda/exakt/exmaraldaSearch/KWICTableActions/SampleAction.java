/*
 * WordWiseReversedSortAction.java
 *
 * Created on 19. Februar 2007, 13:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.KWICTableActions;

import java.awt.event.ActionEvent;
import javax.swing.JOptionPane;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMAKWICTable;
import org.exmaralda.exakt.exmaraldaSearch.swing.COMASearchResultListTableModel;

/**
 *
 * @author thomas
 */
public class SampleAction extends AbstractKWICTableAction {
    
    /** Creates a new instance of WordWiseReversedSortAction */
    public SampleAction(COMAKWICTable t, String title) {
        super(t,title);
    }

    public void actionPerformed(ActionEvent e) {
        COMASearchResultListTableModel model = table.getWrappedModel();
        int howMany = 100;
        if (model.getRowCount()<200) howMany = 50;
        if (model.getRowCount()<50) howMany = 10;
        if (model.getRowCount()<10) howMany = model.getRowCount();
        String numberString = JOptionPane.showInputDialog(table.getTableHeader(), "Sample size:" , Integer.toString(howMany));
        try{
            howMany = Integer.parseInt(numberString);
            table.getWrappedModel().sample(Math.abs(howMany));
            table.setCellEditors();
        } catch (NumberFormatException nfe){
            JOptionPane.showMessageDialog(table, numberString + " could not be parsed as number.");
            nfe.printStackTrace();
        }
    }
    
}
