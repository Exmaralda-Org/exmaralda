/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author thomas
 */
public class TokenListTableModel extends AbstractTableModel {

    List<String> theTokens;
    AbstractTokenList tokenList;

    public TokenListTableModel(AbstractTokenList tokenList) {
        this.tokenList = tokenList;
        theTokens = tokenList.getTokens(AbstractTokenList.ALPHABETICALLY_SORTED);
    }

    public AbstractTokenList getTokenList() {
        return tokenList;
    }

    public void setTokenList(AbstractTokenList tokenList) {
        this.tokenList = tokenList;
    }



    @Override
    public int getRowCount() {
        return theTokens.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String token = theTokens.get(rowIndex);
        if (columnIndex==0){
            return token;
        } else if (columnIndex==1){
            return tokenList.getTokenCount(token);
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex==0){
            return String.class;
        } else if (columnIndex==1){
            return Integer.class;
        }
        return super.getColumnClass(columnIndex);
    }



}
