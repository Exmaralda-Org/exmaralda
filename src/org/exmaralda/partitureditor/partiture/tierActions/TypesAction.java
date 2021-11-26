/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import java.util.Map;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaraldaswing.TypesDialog;
import org.exmaralda.partitureditor.partiture.PartitureTableWithActions;


/**
 *
 * @author  thomas
 */
// issue #295
public class TypesAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of EditTierAction
     * @param t */
    public TypesAction(PartitureTableWithActions t) {
        super("Types...", t);
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("typesAction!");
        table.commitEdit(true);
        types();
        table.transcriptionChanged = true;        
    }
    
    private void types(){
        int row = table.selectionStartRow;
        Tier tier = table.getModel().getTranscription().getBody().getTierAt(row);
        Map<String, Integer> typesTable = tier.getTypesTable();
        TypesDialog typesDialog = new TypesDialog(table.parent, true);
        typesDialog.setData(typesTable, tier);
        typesDialog.setLocationRelativeTo(table);
        typesDialog.setVisible(true);
        if (typesDialog.approved){
            Map<String,String> mappings = typesDialog.getMappings();
            int count = 0;
            for (String sourceType : mappings.keySet()){
                String targetType = mappings.get(sourceType);
                if (!(sourceType.equals(targetType))){
                    for (int pos=0 ; pos<tier.getNumberOfEvents(); pos++){
                        Event event = tier.getEventAt(pos);
                        if (sourceType.equals(event.getDescription())){
                            event.setDescription(targetType);
                            count++;
                        }
                    }
                }
            }
            if (count>0){
                JOptionPane.showMessageDialog(table, Integer.toString(count) + " entries changed in " + tier.getDisplayName() + ".");
                table.getModel().fireRowChanged(row);
            }
        }
    }
    
    
}
