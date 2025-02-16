/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch;

/**
 *
 * @author bernd
 */
public class AdvancedSortDirective {

    
    public enum SORT_TYPE {ALPHABET, NUMBER};
    public enum SORT_DIRECTION {ASCENDING, DESCENDING};
    
    public int col;
    public SORT_TYPE sortType;
    public SORT_DIRECTION sortDirection;
    

    public AdvancedSortDirective(int col, SORT_TYPE sortType, SORT_DIRECTION sortDirection) {
        this.col = col;
        this.sortType = sortType;
        this.sortDirection = sortDirection;        
    }


}
