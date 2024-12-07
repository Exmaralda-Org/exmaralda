/*
 * CheckKeys.java
 *
 * Created on 28. Februar 2007, 12:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.kwicSearch;


import java.util.Vector;
/**
 *
 * @author thomas
 */
public class CheckKeys {
    
    /** Creates a new instance of CheckKeys */
    public CheckKeys() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        Vector v3 = new Vector();
        
        Object o1 = new Object();
        Object o2 = new Object();
        Object o3 = new Object();
        
        v1.addElement(o1);
        v1.addElement(o2);
        v1.addElement(o3);
        
        v2.addElement(o1);
        v2.addElement(o2);
        
        v1.retainAll(v2);
        
        for (Object o : v1){
            System.out.println("V1 " + o.toString());
        }
        
        System.out.println("O1: " + o1.toString());
        System.out.println("O2: " + o2.toString());
        System.out.println("O3: " + o3.toString());
        

    
    }
    
}
