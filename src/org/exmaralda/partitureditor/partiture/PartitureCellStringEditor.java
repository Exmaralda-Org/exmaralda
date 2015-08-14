/*
 * PartitureCellStringEditor.javaf
 *
 * Created on 15. November 2001, 12:19
 */

package org.exmaralda.partitureditor.partiture;

import java.awt.event.KeyEvent;
import org.exmaralda.partitureditor.partiture.menus.EventPopupMenu;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * The editor used in the PartiturTable. Implements UnicodeKeyboardListener
 * so that it can react to events on the virtual keyboard
 * @author  Thomas
 * @version 
 */
public class PartitureCellStringEditor extends com.klg.jclass.cell.editors.JCStringCellEditor 
                                       implements org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardListener {

    private int x;
    private int y;
    private boolean clicked;
    private EventPopupMenu popupMenu;
    
    private boolean thisIsAMac = false;

    
    /** Creates new PartitureCellStringEditor */
    public PartitureCellStringEditor() {
        super();
        setRequestFocusEnabled(true);     
        thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        if (thisIsAMac){
            if (getText().length()==0){
                //System.out.println("Now Must Catch!!!");
                setCaretPosition(0);
                moveCaretPosition(0);
            }
        }

    }

    
    
    public void setPopupMenu(EventPopupMenu menu){
        popupMenu = menu;
    }

    /*public void performUnicodeKeyboardAction(org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent event) {
        this.replaceSelection(event.getText());
        this.getTopLevelAncestor().requestFocus();
        this.requestFocus(); 
    }*/
        
    @Override
    public void performUnicodeKeyboardAction(org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent event) {
       final int memorizeCaret = this.getCaretPosition();
       final int selectionLength = this.getSelectionEnd() - this.getSelectionStart();
       final String text = event.getText();
        
       
       this.replaceSelection(text);
       this.getTopLevelAncestor().requestFocus();
       // changed 08-10-2013 - otherwise, no focus on Windows (see email by Juliane Gall)
       if (!thisIsAMac){
           this.requestFocus(); 
       } else {
           // 18-06-2012
           // this is a bug fix provided by Torben Schinke (Oldenburg)
           // on the MAC, the whole cell is selected after a virtual keyboard event
           // putting the focus management in a threaded process seems to remedy this
           if (this.getTopLevelAncestor() instanceof JFrame){
               SwingUtilities.invokeLater(new Runnable() {
                   @Override
                   public void run() {
                       JFrame parentFrame = (JFrame)PartitureCellStringEditor.this.getTopLevelAncestor();
                       parentFrame.toFront();
                       PartitureCellStringEditor.this.requestFocus();
                       SwingUtilities.invokeLater(new Runnable() {
                           @Override
                           public void run() {
                                PartitureCellStringEditor.this.setCaretPosition(memorizeCaret + text.length() - selectionLength);
                           }
                       });
                   }
               });
           }
       }
    }
    
    @Override
    public void initialize(java.awt.AWTEvent ev, com.klg.jclass.cell.JCCellInfo info, Object o){
        super.initialize(ev,info,o);
        if (ev instanceof java.awt.event.MouseEvent){
            java.awt.event.MouseEvent me = (java.awt.event.MouseEvent)ev;
            x = me.getX();
            y = me.getY();
            clicked=true;
        } else {
            clicked=false;
        }        
    }
    
    void positionCursor(){    
        if (clicked){
            //java.awt.Point p1 = this.getLocation();
            //java.awt.Point p2 = this.getLocationOnScreen();            
            int cx = this.getX();
            int cy = this.getY();
            int px = this.getParent().getX();
            int py = this.getParent().getY();                       
            java.awt.Point point = new java.awt.Point(x-cx-px,y-cy-py);
            int offset = viewToModel(point);
            if ((offset>=0) && (offset<this.getText().length())){
                this.setCaretPosition(offset);            
            } 
        } /* else {
            // this was an attempt to keep the MAC from selecting the input
         * after a cell was selected via Tab (see mail from Ines Rehbein)
         * it does not work- bugger all
            if (thisIsAMac){
                System.out.println("Trying to clear the selection");
                setCaretPosition(Math.max(getText().length(),0));
                moveCaretPosition(Math.max(getText().length(),0));
            }            
        } */
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        super.mouseReleased(e);
        if (this.popupMenu == null) return;
        if (e.isPopupTrigger()){
            popupMenu.show((java.awt.Component)e.getSource(), e.getX(), e.getY());
            
        }
    }

    @Override
    public void mousePressed(MouseEvent e){
        super.mousePressed(e);
        if (this.popupMenu == null) return;
        if (e.isPopupTrigger()){
            popupMenu.show((java.awt.Component)e.getSource(), e.getX(), e.getY());
            
        }
    }    

    
}