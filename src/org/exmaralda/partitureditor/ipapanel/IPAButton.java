/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.ipapanel;

import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;
import org.jdom.Element;

/**
 *
 * @author thomas
 */
public class IPAButton extends JButton {

    String character;
    String display;
    String description;

    public IPAButton(Element phoneme, String fontName, float fontsize) {
        character = phoneme.getChildText("unicode");
        display = phoneme.getChildText("display");
        if (display!=null){
            setText(display);
        } else {
            setText(character);
        }
        setMargin(new Insets(1,1,1,1));

        //setFont(new java.awt.Font("Arial Unicode MS", 1, 48));
        setFont(new java.awt.Font(fontName, 1, 48));
        setFont(this.getFont().deriveFont(Font.BOLD, fontsize));
        // make square buttons on MAC OS
        putClientProperty("JButton.buttonType", "square");

        description = phoneme.getChildText("description");
        setToolTipText(description);
    }

}
