/*
 * ApplyFormatStylesheetAction.java
 *
 * Created on 6. Oktober 2003, 15:36
 */

package org.exmaralda.partitureditor.partiture.formatActions;

import java.io.IOException;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.partiture.*;
import org.xml.sax.SAXException;

/**
 *
 * @author  thomas
 */
public class ApplyBuiltInFormatStylesheetAction extends org.exmaralda.partitureditor.partiture.AbstractTableAction {
    
    String pathToXSL;
    
    /** Creates a new instance of ApplyFormatStylesheetAction
     * @param t
     * @param name
     * @param pathToXSL */
    public ApplyBuiltInFormatStylesheetAction(PartitureTableWithActions t, String name, String pathToXSL) {
        super(name, t);
        this.pathToXSL = pathToXSL;
    }
    
    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        System.out.println("applyBuiltInFormatStylesheetAction!");
        table.commitEdit(true);
        applyStylesheet();   
        table.saveTierFormatTable = true;
    }
    
    private void applyStylesheet(){
        // simply apply the built in stylesheet
        try {
           org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf 
                   = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory();

           String formatTableString = sf.applyInternalStylesheetToString(pathToXSL, 
                                                                         table.getModel().getTranscription().toXML());
           TierFormatTable tft = new TierFormatTable();
           tft.TierFormatTableFromString(formatTableString);
           table.getModel().setTierFormatTable(tft);            
           table.formatChanged = true;
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException e){
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(this.table, e.getMessage());
           // should never get here if build was correct
        } 
   }
    
}
