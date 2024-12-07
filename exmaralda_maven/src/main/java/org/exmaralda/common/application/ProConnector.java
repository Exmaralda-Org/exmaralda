/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.common.application;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bernd
 */
public class ProConnector {
    
    public static boolean isProPresent(){
        try {
            Class.forName("org.exmaralda.pro.ExmaraldaProIdentifierClass");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ProConnector.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
}
