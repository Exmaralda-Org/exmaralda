/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.gui;

import java.awt.Font;
import javax.swing.JButton;

/**
 *
 * @author thomas
 */
public class VirtualKey extends JButton {

    public VirtualKey(String characters, String description, Font font){
        super();
        setText(characters);
        setToolTipText(description);
        setFont(font);


        // for the MAC!
        String os = System.getProperty("os.name").substring(0,3);
        if (os.equalsIgnoreCase("mac")){
            putClientProperty( "JButton.buttonType", "square" );
            setPreferredSize(new java.awt.Dimension(50, 24));
            setMinimumSize(new java.awt.Dimension(50, 24));
        } else {
            setPreferredSize(new java.awt.Dimension(30, 24));
            setMinimumSize(new java.awt.Dimension(24, 24));
        }


        setOpaque(false);
        //setBorder (new javax.swing.border.LineBorder(java.awt.Color.black));
        setMargin(new java.awt.Insets(2,2,2,2));
    }

}
