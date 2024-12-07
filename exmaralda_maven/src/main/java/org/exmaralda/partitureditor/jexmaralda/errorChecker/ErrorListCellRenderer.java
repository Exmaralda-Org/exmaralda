/*
 * PaidusUtteranceListCellRenderer.java
 *
 * Created on 24. September 2003, 15:40
 */

package org.exmaralda.partitureditor.jexmaralda.errorChecker;

import java.io.File;
import javax.swing.*;
import org.jdom.*;

/**
 *
 * @author  thomas
 */
public class ErrorListCellRenderer implements ListCellRenderer {
    
    JLabel result = new JLabel();
    
    /** Creates a new instance of PaidusUtteranceListCellRenderer */
    public ErrorListCellRenderer() {
    }
    
    @Override
    public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
         Element error = (Element)value;

         java.awt.Color bc = java.awt.Color.white;
         if (isSelected) {bc = java.awt.Color.lightGray;}
         java.awt.Color fc = java.awt.Color.black;
         if (error.getAttributeValue("done").equals("yes")){
             fc = java.awt.Color.gray;
         }

         String thisFile = error.getAttributeValue("file");
         
         result.setBackground(bc);
         result.setForeground(fc);
         result.setOpaque(true);

         result.setText(error.getText());
         if (index < list.getModel().getSize()-1){
             Element nextError = (Element)(list.getModel().getElementAt(index+1));
             String nextFile = nextError.getAttributeValue("file");
             if (!(thisFile.equals(nextFile))){
                 result.setText("<html><u>" + result.getText() + "</u></html>");
             }
         }
         
         //result.setSelected(error.getAttributeValue("done").equals("yes"));
         result.setToolTipText(thisFile);
         
         return result;        
    }
    
}
