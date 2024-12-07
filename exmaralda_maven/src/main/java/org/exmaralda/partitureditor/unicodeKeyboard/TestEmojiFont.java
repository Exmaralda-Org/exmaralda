/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.unicodeKeyboard;

import java.awt.Font;

/**
 *
 * @author thomas.schmidt
 */
public class TestEmojiFont {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new TestEmojiFont().doit();
    }

    private void doit() {
        Font font = new Font("Segoe UI Emoji", Font.PLAIN, 12);
        //Font font = new Font("Arial Unicde MS", Font.PLAIN, 12);
        
        for (char i=0; i<Character.MAX_CODE_POINT; i++){
            char c = '\ud83d';
            c = i;
            String name = Character.getName(c);
            if (font.canDisplay(c)){
            //if (font.canDisplay('A')){        
                System.out.println("Can display " + name);
            } else {
                //System.out.println("Cannot display " + name);            
            }
        }
            
    }
    
}
