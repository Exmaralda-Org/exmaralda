/*
 * COMAKWICTableColumnModel.java
 *
 * Created on 5. Februar 2007, 14:50
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.swing;

import javax.swing.table.TableColumn;

/**
 *
 * @author thomas
 */
public class COMAKWICTableColumnModel extends javax.swing.table.DefaultTableColumnModel {
    
    /** Creates a new instance of COMAKWICTableColumnModel */
    public COMAKWICTableColumnModel() {

    }

    /*javax.swing.table.TableColumn selectionColumn = getColumnModel().getColumn(0);
    selectionColumn.setPreferredWidth(20);
    selectionColumn.sizeWidthToFit();
    javax.swing.table.TableColumn transcriptionColumn = getColumnModel().getColumn(1);
    transcriptionColumn.setPreferredWidth(150);    
    javax.swing.table.TableColumn speakerColumn = getColumnModel().getColumn(2);
    speakerColumn.setPreferredWidth(70);
    javax.swing.table.TableColumn leftContextColumn = getColumnModel().getColumn(3);
    leftContextColumn.setPreferredWidth(270);        
    javax.swing.table.TableColumn matchTextColumn = getColumnModel().getColumn(4);
    matchTextColumn.setPreferredWidth(70);        
    javax.swing.table.TableColumn rightContextColumn = getColumnModel().getColumn(5);
    rightContextColumn.setPreferredWidth(270);
    javax.swing.table.JTableHeader header = this.getTableHeader();
    header.setPreferredSize(new java.awt.Dimension(header.getWidth(), 30));*/

    
    @Override
    public TableColumn getColumn(int columnIndex) {
        TableColumn retValue;        
        retValue = super.getColumn(columnIndex);        
        switch (columnIndex){
            case 0 : retValue.setPreferredWidth(20); retValue.setWidth(20);
            case 1 : retValue.setPreferredWidth(150); retValue.setWidth(150);
            case 2 : retValue.setPreferredWidth(70); retValue.setWidth(70);
            case 3 : retValue.setPreferredWidth(270); retValue.setWidth(270);
            case 4 : retValue.setPreferredWidth(70); retValue.setWidth(70);
            case 5 : retValue.setPreferredWidth(270); retValue.setWidth(270);
        }
        return retValue;
    }
    
}
