/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaraldaswing;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas.schmidt
 */
public class AutoBackupTableModel extends AbstractTableModel {

    File[] backupFiles;
    
    public AutoBackupTableModel(String path, String prefix) {
        backupFiles = new File(path).listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(prefix) && name.endsWith(".exb"));
            }            
        });        
    }

    
    
    
    @Override
    public int getRowCount() {
        return backupFiles.length;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        File f = backupFiles[rowIndex];
        switch (columnIndex){
            case 0 : return f.getName();
            case 1 : 
            {
                try {
                    BasicTranscription bt = new BasicTranscription(f.getAbsolutePath());
                    if (bt.getHead().getMetaInformation().getUDMetaInformation().containsAttribute("AUTO-BACKUP-FROM")){
                        String from = bt.getHead().getMetaInformation().getUDMetaInformation().getValueOfAttribute("AUTO-BACKUP-FROM");
                        return from;
                    } else {
                        return "---";
                    }
                } catch (SAXException | JexmaraldaException ex) {
                    Logger.getLogger(AutoBackupTableModel.class.getName()).log(Level.SEVERE, null, ex);
                }
                return "?";
            }
            case 2 :
                Date date = new Date(f.lastModified());
                return date.toString();
        }
        return "";
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0 : return "Auto backup name";
            case 1 : return "Backup of";
            case 2 : return "Date / Time";
        }
        return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
    }

    File getFileAt(int selectedRow) {
        return backupFiles[selectedRow];
    }
    

    
}
