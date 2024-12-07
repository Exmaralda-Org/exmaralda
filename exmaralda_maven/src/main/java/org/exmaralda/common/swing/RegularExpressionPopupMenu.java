/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.swing;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 *
 * @author thomas
 */
class RegularExpressionPopupMenu extends JPopupMenu {

    RegularExpressionTextField regexTextField;
    
    public RegularExpressionPopupMenu(RegularExpressionTextField aThis) {
        regexTextField = aThis;

        this.setFont(this.getFont().deriveFont(10.0f));

        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.escapeRegex();}}).setText("Escape Regex");

        addSeparator();

        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection(".");}}).setText(". [Any character]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("\\w");}}).setText("\\w [Word character]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("\\d");}}).setText("\\d [Digit]");

        addSeparator();

        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("?");}}).setText("? [Once or not at all]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("*");}}).setText("* [Zero or more times]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("+");}}).setText("+ [One or more times]");

        addSeparator();

        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("^");}}).setText("^ [Beginning of input]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("$");}}).setText("$ [End of input]");
        add(new AbstractAction(){public void actionPerformed(ActionEvent e) {regexTextField.replaceSelection("\\b");}}).setText("\\b [Word boundary]");
    }

}
