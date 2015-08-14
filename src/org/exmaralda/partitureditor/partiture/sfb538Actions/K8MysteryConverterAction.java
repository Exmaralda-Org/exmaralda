/*
 * MakeSyllableStructureTierAction.java
 *
 * Created on 22. April 2004, 11:48
 */

package org.exmaralda.partitureditor.partiture.sfb538Actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.convert.K8MysteryConverter;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class K8MysteryConverterAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    /** Creates a new instance of MakeSyllableStructureTierAction */
    public K8MysteryConverterAction(PartitureTableWithActions t) {
        super("[SFB 538] K8: Import mysterious file", t);
    }
    
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("K8MysteryConverterAction!");
        table.commitEdit(true);
        importit();
        table.transcriptionChanged = true;        
    }
    
    private void importit(){
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(new ParameterFileFilter("txt", "Mysterious text file (*.txt)"));
        int value = jfc.showOpenDialog(table);
        if (value==JFileChooser.APPROVE_OPTION){
            try {
                File mystery = jfc.getSelectedFile();
                K8MysteryConverter converter = new K8MysteryConverter(mystery);
                BasicTranscription bt = converter.convert();
                table.getModel().setTranscription(bt);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(table, ex.getLocalizedMessage());
            }
        }
    }

}
