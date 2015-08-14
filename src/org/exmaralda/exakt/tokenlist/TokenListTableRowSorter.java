/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;

import java.util.Comparator;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author thomas
 */
public class TokenListTableRowSorter extends TableRowSorter<TokenListTableModel> {

    TokenListTableRowSorter(TokenListTableModel model) {
        super(model);
    }

    @Override
    public Comparator getComparator(int column) {
        if (column==0){
            return String.CASE_INSENSITIVE_ORDER;
        } else if (column==1){
            return new Comparator<Integer>() {
                public int compare(Integer o1, Integer o2) {
                    return o1.compareTo(o2);
                }
            };
        }
        return super.getComparator(column);
    }

}
