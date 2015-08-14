/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTable;
import org.jdom.Element;
import org.jdom.filter.AbstractFilter;

/**
 *
 * @author thomas
 */
public class WordListTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {

    AbstractFilter textFilter = new AbstractFilter(){
        @Override
        public boolean matches(Object o) {
            return ((o instanceof org.jdom.Text) && ((org.jdom.Text)o).getTextTrim().length()>0);
        }
    };

    String getWordText(Element wordElement){
        Iterator i = wordElement.getDescendants(textFilter);
        StringBuilder result = new StringBuilder();
        while (i.hasNext()){
            result.append(((org.jdom.Text)(i.next())).getText());
        }
        return result.toString();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Element word = (Element)value;
        String text = "";
        if (column==0){
            //text = word.getText();
            text = getWordText(word);
        } else if (column==1){
            if (word.getAttribute("n")!=null){
                text = word.getAttributeValue("n");
            }
        } else if (column==2){
            if (word.getAttribute("lemma")!=null){
                text = word.getAttributeValue("lemma");
            }
        } else if (column==3){
            if (word.getAttribute("pos")!=null){
                text = word.getAttributeValue("pos");
            }
        } else if (column==4){
            if (word.getAttribute("p-pos")!=null){
                text = word.getAttributeValue("p-pos");
            }
        }
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        label.setFont(label.getFont().deriveFont(12.0f));
        
        //added 19-12-2012: check if this is in the dereko wordlist, if not, make it red
        if (column==0 && (!isSelected)){
            if("n".equals(word.getAttributeValue("i"))){
                label.setBackground(Color.RED);
            } else {
                label.setBackground(Color.WHITE);
            }
        }
        
        if ((column==1) && (!isSelected)){
            label.setForeground(Color.BLUE);
        } 
        if (column==4) {
            String p = text;
            if (p.contains(" ")){
                p = p.substring(0, p.indexOf(" "));
            } else if (p.length()==0){
                p = "0.0";
            }
            if (Double.parseDouble(p)<0.5){            
                label.setForeground(Color.red);
            } else {
                label.setForeground(Color.BLACK);                
            }

        }
        return label;
    }



}
