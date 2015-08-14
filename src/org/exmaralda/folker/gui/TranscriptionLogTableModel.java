/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.data.TranscriptionHead;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class TranscriptionLogTableModel extends AbstractTableModel {

    TranscriptionHead transcriptionHead;
    ArrayList<Element> logElements = new ArrayList<Element>();
    
    public TranscriptionLogTableModel(TranscriptionHead th) {
       transcriptionHead = th;
       //System.out.println(IOUtilities.elementToString(th.getHeadElement()));
       logElements.addAll(th.getHeadElement().getChild("transcription-log").getChildren("log-entry"));
    }

    
    @Override
    public int getRowCount() {
        return logElements.size();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public String getColumnName(int column) {
        switch (column){
            case 0 : return "Start";
            case 1 : return "Ende";
            case 2 : return "Benutzer";
            default : return "Eintrag";
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Element logElement = logElements.get(rowIndex);
        // <log-entry start="2014/01/15 13:46:47" end="2014/01/15 13:46:47" who="system">transcription-log created</log-entry>        
        switch (columnIndex){
            case 0 : return logElement.getAttributeValue("start");
            case 1 : return logElement.getAttributeValue("end");
            case 2 : return logElement.getAttributeValue("who");
            default : return logElement.getText();
        }
    }
    
    
    
}
