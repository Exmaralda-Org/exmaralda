/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.tokenlist;


import javax.swing.RowFilter;



/**
 *
 * @author thomas
 */
public class TokenListTableRowFilter extends RowFilter{

    String regex;

    public TokenListTableRowFilter(String r) {
        regex = r;
    }

    @Override
    public boolean include(Entry entry) {
        return entry.getStringValue(0).matches(regex);
    }

}
