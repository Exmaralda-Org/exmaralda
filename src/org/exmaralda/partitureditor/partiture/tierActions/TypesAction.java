/*
 * EditTierAction.java
 *
 * Created on 17. Juni 2003, 14:29
 */

package org.exmaralda.partitureditor.partiture.tierActions;

import java.util.HashMap;
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
        int tiersInThatCategory = table.getModel().getTranscription().getBody().getTiersOfCategory(tier.getCategory()).length;
        int optionChosen = 0;
        if (tiersInThatCategory > 1){
            String[] options = {"For the currently selected tier", "For all " + Integer.toString(tiersInThatCategory) + " tiers with category '" + tier.getCategory() + "'"};
            optionChosen = JOptionPane.showOptionDialog(table, "For which tier(s) do you want to calculate types?", 
                    "Options for type calculation", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        }
        Map<String, Integer> typesTable = tier.getTypesTable();
        if (optionChosen==1){
            for (int pos=0; pos<table.getModel().getTranscription().getBody().getNumberOfTiers(); pos++){
                if (pos==row) continue;
                Tier thisTier = table.getModel().getTranscription().getBody().getTierAt(pos);
                if (!(thisTier.getCategory().equals(tier.getCategory()))) continue;
                Map<String, Integer> thisTypesTable = thisTier.getTypesTable();
                for (String type : thisTypesTable.keySet()){
                    if (!(typesTable.containsKey(type))){
                        typesTable.put(type, thisTypesTable.get(type));
                    } else {
                        typesTable.put(type, typesTable.get(type) + thisTypesTable.get(type));
                    }
                }
            }
        }
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
                            int col = table.getModel().getTranscription().getBody().getCommonTimeline().lookupID(event.getStart());
                            table.getModel().fireValueChanged(row, col);
                            count++;
                        }
                    }
                    if (optionChosen==1){
                        for (int pos=0; pos<table.getModel().getTranscription().getBody().getNumberOfTiers(); pos++){
                            if (pos==row) continue;
                            Tier thisTier = table.getModel().getTranscription().getBody().getTierAt(pos);
                            if (!(thisTier.getCategory().equals(tier.getCategory()))) continue;
                            for (int pos2=0 ; pos2<thisTier.getNumberOfEvents(); pos2++){
                                Event event = thisTier.getEventAt(pos2);
                                if (sourceType.equals(event.getDescription())){
                                    event.setDescription(targetType);
                                    int col = table.getModel().getTranscription().getBody().getCommonTimeline().lookupID(event.getStart());
                                    table.getModel().fireValueChanged(pos, col);
                                    count++;
                                }
                            }
                        }
                        
                    }
                }
            }
            if (count>0){
                String where = tier.getDisplayName();
                if (optionChosen==1){
                    where = "tiers of category '" + tier.getCategory() + "'";
                }
                JOptionPane.showMessageDialog(table, Integer.toString(count) + " entries changed in " + where + ".");
            }
        }
    }
    
    
}
