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
import javax.swing.text.html.HTMLEditorKit;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Element;

//public class NormalizedContributionTableCellRenderer extends JTextArea implements TableCellRenderer {
public class NormalizedContributionTableCellRenderer extends JEditorPane implements TableCellRenderer {
  
    private final DefaultTableCellRenderer adaptee =  new DefaultTableCellRenderer();
    private int ADDITIONAL_HEIGHT = 5;
    StylesheetFactory ssf = new StylesheetFactory(true);
    String XSL_PATH = "/org/exmaralda/folker/data/parsedContribution2Text.xsl";

    //private boolean replaceSpaces = true;
  
  /** map from table to map of rows to map of column heights */
  //private final Map cellSizes = new HashMap();

  javax.swing.text.StyledEditorKit sek = new javax.swing.text.StyledEditorKit();



  public NormalizedContributionTableCellRenderer() {
        //setLineWrap(true);
        //setWrapStyleWord(true);
      this.setContentType("text/html");
      HTMLEditorKit kit = new HTMLEditorKit();
      kit.getStyleSheet().addRule("b {color:red;}");
      kit.getStyleSheet().addRule("body {font-family:sans-serif;}");
      kit.getStyleSheet().addRule("i {color:#c0c0c0;}");
      setEditorKit(kit);

      //setEditorKit(sek);

  }

  @Override
  public Component getTableCellRendererComponent(
                                            JTable table, Object obj, boolean isSelected,
                                            boolean hasFocus, int row, int column) {
        //String celltext = ((Element)(obj)).getText();
        String celltext = "";
        Element element = (Element)(obj);
        try {
            celltext = ssf.applyInternalStylesheetToString(XSL_PATH, IOUtilities.elementToString(element));
            //System.out.println(celltext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // set the colours, etc. using the standard for that platform
        adaptee.getTableCellRendererComponent(table, celltext,
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
