/*
 * KeyPanel.java
 *
 * Created on 15. November 2001, 10:28
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

import java.awt.Font;
import java.util.HashSet;

/**
 *
 * @author  Thomas
 * @version 
 */
public class KeyPanel extends javax.swing.JPanel {

    public Font generalPurposeFont = new Font("Arial Unicode MS", Font.PLAIN, 12);
    private HashSet allFontNames = new HashSet();
    UnicodeKeyboard keyboard;
    
    /*public static String[] STANDARD_KEYS = {"\u00C0","\u00C1","\u00C2","\u00C3","\u00C4","\u00C5","\u00C6","\u00C7",
                                            "\u00C8","\u00C9","\u00CA","\u00CB","\u00CC","\u00CD","\u00CE","\u00CF",
                                            "\u00D0","\u00D1","\u00D2","\u00D3","\u00D4","\u00D5","\u00D6","\u00D7",
                                            "\u00D8","\u00D9","\u00DA","\u00DB","\u00DC","\u00DD","\u00DE","\u00DF",
                                            "\u00E0","\u00E1","\u00E2","\u00E3","\u00E4","\u00E5","\u00E6","\u00E7",
                                            "\u00E8","\u00E9","\u00EA","\u00EB","\u00EC","\u00ED","\u00EE","\u00EF",
                                            "\u00F0","\u00F1","\u00F2","\u00F3","\u00F4","\u00F5","\u00F6","\u00F7",
                                            "\u00F8","\u00F9","\u00FA","\u00FB","\u00FC","\u00FD","\u00FE","\u00FF"};*/

    private String[] keyMap;
    private javax.swing.JButton[] keys = new javax.swing.JButton[0];
    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    
    
    /** Creates new form KeyPanel */
    public KeyPanel(String generalPurposeFontName) {
        generalPurposeFont  = new Font(generalPurposeFontName, Font.PLAIN, 12);
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] allFonts = ge.getAvailableFontFamilyNames();   
        for (int pos=0; pos<allFonts.length; pos++){allFontNames.add(allFonts[pos]);}                   
        initComponents();
    }
     
    public void setKeys(UnicodeKeyboard k){
        keyboard = k;
        keyMap = keyboard.getKeyMap();
        this.removeAll();
        keys = new javax.swing.JButton[keyboard.size()];
        for (int i=0; i<keyboard.size(); i++){
            keys[i] = new javax.swing.JButton();
            keys[i].setBorder (new javax.swing.border.LineBorder(java.awt.Color.black));
            keys[i].setMargin(new java.awt.Insets(2,2,2,2));
            keys[i].setBackground(java.awt.Color.white);
            keys[i].addActionListener (new java.awt.event.ActionListener () {
                public void actionPerformed (java.awt.event.ActionEvent evt) {
                    keyPressed (evt);
                }
            });
            
            UnicodeKey key = (UnicodeKey)(keyboard.elementAt(i));
            String text = key.content;
            boolean canDisplayAll = true;
            for (char c : text.toCharArray()){
                if (!key.font.canDisplay(c)){
                    canDisplayAll = false;
                    break;
                }
            }
            if (canDisplayAll && isFontAvailable(key.font)){
                    keys[i].setFont(key.font);
            } else {
                canDisplayAll = true;
                for (char c : text.toCharArray()){
                    if (!generalPurposeFont.canDisplay(c)){
                        canDisplayAll = false;
                        break;
                    }
                }
                if (canDisplayAll){
                    keys[i].setFont(generalPurposeFont);
                }
            }
            keys[i].setText(text);
            keys[i].setEnabled(true);           
            keys[i].setPreferredSize (new java.awt.Dimension(Math.max(25,stringWidth(keys[i].getFont(),text)+6),25));
            keys[i].setMinimumSize (new java.awt.Dimension(25, 25));
            keys[i].setToolTipText(key.description);
            this.add(keys[i]);
        }        
        this.setPreferredSize(this.getPreferredSize());
        this.revalidate();
    }
    

    public void changeKeySize(int percentage){
        for (int i=0; i<keyboard.size(); i++){
            String text = keys[i].getText();
            int height = Math.round((percentage/100f)*25);
            int width = Math.round((percentage/100f)*(stringWidth(keys[i].getFont(),text)+6));
            keys[i].setMaximumSize (new java.awt.Dimension(Math.max(height,width),height));
            keys[i].setPreferredSize (new java.awt.Dimension(Math.max(height,width),height));
            keys[i].setMinimumSize (new java.awt.Dimension(height, height));       
            keys[i].setFont(keys[i].getFont().deriveFont((float)(percentage/100f)*12));
        }

    }
    
    public void setGeneralPurposeFont(String fontName){
        this.generalPurposeFont = new Font(fontName, Font.PLAIN, 12);
    }
    
    @Override
    public java.awt.Dimension getPreferredSize(){
        if (keys!=null){
            java.awt.Dimension dim = new java.awt.Dimension();                    
            dim.setSize(200, (keys.length/7)*25 + 50);
            return dim;
        }
        return super.getPreferredSize();
        //System.out.println("Preferred Size is now " + dim.width + " / " + dim.height);
    }
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents

    }//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    void keyPressed(java.awt.event.ActionEvent event){
        javax.swing.JButton button = (javax.swing.JButton)event.getSource();
        String text = button.getText();
        fireUnicodeKeyPressed(text);
    }
    
    public void addUnicodeListener(UnicodeKeyboardListener l) {
         listenerList.add(UnicodeKeyboardListener.class, l);
    }
    
    public void removeUnicodeListener(UnicodeKeyboardListener l) {
         listenerList.remove(UnicodeKeyboardListener.class, l);
    }
    public void removeAllListeners(){
        listenerList = new javax.swing.event.EventListenerList();
    }
    
    
    protected void fireUnicodeKeyPressed(String t) {
         // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
             if (listeners[i]==UnicodeKeyboardListener.class) {
                UnicodeKeyboardEvent event = new UnicodeKeyboardEvent(t);
                ((UnicodeKeyboardListener)listeners[i+1]).performUnicodeKeyboardAction(event);             
            }
         }
    }
    
    private boolean isFontAvailable(Font font){
        return allFontNames.contains(font.getName());        
    }
    
     static int stringWidth(java.awt.Font font, String text){
        if (text.length()==0) {return 0;}
        java.awt.font.TextLayout textLayout = new java.awt.font.TextLayout(text, font, new java.awt.font.FontRenderContext(null, false, true));
        return Math.round(textLayout.getAdvance());
    }
   

}