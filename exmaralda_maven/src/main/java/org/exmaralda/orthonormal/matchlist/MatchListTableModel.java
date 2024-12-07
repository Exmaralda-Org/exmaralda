/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.matchlist;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import org.exmaralda.orthonormal.utilities.WordUtilities;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MatchListTableModel extends AbstractTableModel {

    MatchList matchList = new MatchList();

    public MatchListTableModel(MatchList matchList) {
        this.matchList = matchList;        
    }
    
    

    @Override
    public int getRowCount() {
        return matchList.getSize();
    }

    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Element match = (Element) matchList.getElementAt(rowIndex);
            String matchID = match.getAttributeValue("match");
            Element matchToken = (Element) XPath.selectSingleNode(match, "descendant::w[@id='" + matchID + "']");
            switch(columnIndex){
                case 0 : 
                    // 01-03-2023, issue #340
                    return WordUtilities.getWordText(matchToken, true);
                case 1 :     
                    String n = matchToken.getAttributeValue("n");
                    if (n!=null) return n;
                    return "";
                case 2 :     
                    String lemma = matchToken.getAttributeValue("lemma");
                    if (lemma!=null) return lemma;
                    return "";
                case 3 :     
                    String pos = matchToken.getAttributeValue("pos");
                    if (pos!=null) return pos;
                    return "";                    
            }
        } catch (JDOMException ex) {
            return "#Error";
        }
        return "";
    }

    @Override
    public String getColumnName(int column) {
        switch(column){
            case 0 : return "Word";
            case 1 : return "Normal";
            case 2 : return "Lemma";
            case 3 : return "POS";
        }
        return super.getColumnName(column); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
