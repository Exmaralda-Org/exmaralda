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
import java.io.IOException;
import javax.swing.text.html.HTMLEditorKit;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.jdom.Element;
import org.xml.sax.SAXException;

public class NormalizedContributionTableCellRenderer extends JEditorPane implements TableCellRenderer {
  
    private final int ADDITIONAL_HEIGHT = 5;
    StylesheetFactory ssf = new StylesheetFactory(true);
    String XSL_PATH = "/org/exmaralda/folker/data/parsedContribution2Text.xsl";
  
  public NormalizedContributionTableCellRenderer() {
      setContentType("text/html");
      
      HTMLEditorKit kit = new HTMLEditorKit();
      kit.getStyleSheet().addRule("body {font-family:sans-serif;}"); // here is the style for transcribed forms
      kit.getStyleSheet().addRule("var {color:rgb(126,192,238); font-style: normal; font-weight: normal;}"); // this is the style for transcribed forms with a normalization
      kit.getStyleSheet().addRule("b {color:rgb(255,165,0);}"); // this is the style for normalized forms
      kit.getStyleSheet().addRule("i {color:rgb(130,130,130);}"); // this is the style for non-word tokens
      setEditorKit(kit);

  }

  @Override
  public Component getTableCellRendererComponent(
                                            JTable table, Object obj, boolean isSelected,
                                            boolean hasFocus, int row, int column) {

       String html = "";
        
        Element element = (Element)(obj);
        try {
            String[][] parameters = {
                {"SELECTED", Boolean.toString(isSelected)}
            };
            html = ssf.applyInternalStylesheetToString(XSL_PATH, IOUtilities.elementToString(element), parameters);
            //System.out.println(celltext);
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            System.out.println(ex.getMessage());
        }
  

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            //setForeground(Color.WHITE);
        } else {
            setBackground(new Color(240, 240, 240));
            //setForeground(Color.BLACK);
        }

        setText(html);

        setOpaque(true);

        setFont(table.getFont());        
        

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
