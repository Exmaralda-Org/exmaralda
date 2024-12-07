/*
 * TestClipboard.java
 *
 * Created on 16. Februar 2007, 11:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

/**
 *
 * @author thomas
 */
public class TestClipboard {
    
    /** Creates a new instance of TestClipboard */
    public TestClipboard() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //java.awt.datatransfer.StringSelection ss = new java.awt.datatransfer.StringSelection("a<b>c</b>d");
        HTMLSelection ss = new HTMLSelection("<html>a<b>c</b>d</html>");
        new javax.swing.JFrame().getToolkit().getSystemClipboard().setContents(ss,null);

    }
    
}
