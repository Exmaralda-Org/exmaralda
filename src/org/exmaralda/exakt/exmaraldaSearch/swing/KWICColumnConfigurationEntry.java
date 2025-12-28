/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.exakt.exmaraldaSearch.swing;

/**
 *
 * @author bernd
 */
public class KWICColumnConfigurationEntry{
    
    public String name;
    public String cl;
    public String type;
    
    public String[] values;

    public KWICColumnConfigurationEntry(String name, String cl, String type) {
        this.name = name;
        this.cl = cl;
        this.type = type;
    }
    
}
