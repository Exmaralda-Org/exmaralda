/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.partiture.undo;

/**
 *
 * @author thomas
 */
public class RestoreCellInfo {

    public int row;
    public int col;
    public String text;

    public RestoreCellInfo(int row, int col, String text) {
        this.row = row;
        this.col = col;
        this.text = text;
    }



}
