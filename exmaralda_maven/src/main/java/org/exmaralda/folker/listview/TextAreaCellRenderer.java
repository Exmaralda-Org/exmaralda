/*
 * TextAreaRenderer.java
 *
 * Created on 14. Maerz 2008, 15:34
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

// Code from http://www.roseindia.net/javatutorials/JTable_in_JDK.shtml


package org.exmaralda.folker.listview;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class TextAreaCellRenderer extends JTextArea implements TableCellRenderer {
  
    private final DefaultTableCellRenderer adaptee =  new DefaultTableCellRenderer();
    private int ADDITIONAL_HEIGHT = 5;

    //private boolean replaceSpaces = true;
  
  /** map from table to map of rows to map of column heights */
  private final Map cellSizes = new HashMap();

  public TextAreaCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
  }

  public Component getTableCellRendererComponent(
                                            JTable table, Object obj, boolean isSelected,
                                            boolean hasFocus, int row, int column) {
        //String celltext = (String)obj;
        // added 12-05-2009: indicate space symbols
        /*if (replaceSpaces){
            celltext = celltext.replace(' ', '\u00B7');
        }*/

        // set the colours, etc. using the standard for that platform
        adaptee.getTableCellRendererComponent(table, obj,
        isSelected, hasFocus, row, column);
        if (!isSelected){
            adaptee.setBackground(new java.awt.Color(240, 240, 240));
        }
        setForeground(adaptee.getForeground());
        setBackground(adaptee.getBackground());
        setBorder(adaptee.getBorder());
        setFont(adaptee.getFont());
        setText(adaptee.getText());

        // This line was very important to get it working with JDK1.4
        TableColumnModel columnModel = table.getColumnModel();
        setSize(columnModel.getColumn(column).getWidth(), 100000);
        int height_wanted = (int) getPreferredSize().height + ADDITIONAL_HEIGHT;
        if (height_wanted != table.getRowHeight(row)) {
            table.setRowHeight(row, height_wanted);
        }
        return this;
  }

}
