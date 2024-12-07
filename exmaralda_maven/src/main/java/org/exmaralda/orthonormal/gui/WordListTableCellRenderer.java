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
        //return result.toString();
        // 01-03-2023 changed for #340
        return result.toString().replaceAll("\\s", "");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Element word = (Element)value;
        String text = "";
        switch (column) {
            // added 19-02-2019, issue #179
            case 0 :
                text = Integer.toString(row+1);
                break;
            case 1:
                //text = word.getText();
                text = getWordText(word);
                break;
            case 2:
                if (word.getAttribute("n")!=null){
                    text = word.getAttributeValue("n");
                }   break;
            case 3:
                if (word.getAttribute("lemma")!=null){
                    text = word.getAttributeValue("lemma");
                }   break;
            case 4:
                if (word.getAttribute("pos")!=null){
                    text = word.getAttributeValue("pos");
                }   break;
            case 5:
                if (word.getAttribute("p-pos")!=null){
                    text = word.getAttributeValue("p-pos");
                }   break;
            default:
                break;
        }
        JLabel label = (JLabel) super.getTableCellRendererComponent(table, text, isSelected, hasFocus, row, column);
        label.setFont(label.getFont().deriveFont(12.0f));
        
        if (column==0){
            label.setBackground(Color.LIGHT_GRAY);            
        }
        //added 19-12-2012: check if this is in the dereko wordlist, if not, make it red
        if (column==0+1 && (!isSelected)){
            if("n".equals(word.getAttributeValue("i"))){
                label.setBackground(Color.RED);
            } else {
                label.setBackground(Color.WHITE);
            }
        }
        
        if ((column==1+1) && (!isSelected)){
            label.setForeground(Color.BLUE);
        } 
        if (column==4+1) {
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
