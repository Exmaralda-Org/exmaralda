/*
 * UnicodeKeyboardPanel.java
 *
 * Created on 15. November 2001, 12:09
 */

package org.exmaralda.partitureditor.unicodeKeyboard;

import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class UnicodeKeyboardPanel extends javax.swing.JPanel implements javax.swing.event.ChangeListener {


    private static String[] BUILT_IN_CHARACTER_SETS = {"/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT.xml", 
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_ODTSTD.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_ODTSTD_Extended.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Romanic.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Germanic.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Turkish.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Polish.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Arabic.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/HIAT_Persian.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/KgSR.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/DIDA.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/GAT.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/GAT_Persian.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/TEI.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/IPA.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/iConc.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/BrailleIntonation.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/Greek.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/Cyrillic.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/Russian_Transliteration.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/Armenian.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/Diacritics.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/SyncWriterConversion.xml",
                                                       "/org/exmaralda/partitureditor/unicodeKeyboard/Charsets/MiscSymbols.xml"
                                                       };
    
    private static String[] BUILT_IN_CHARACTER_SET_NAMES = {"HIAT", 
                                                            "HIAT for ODT-STD",
                                                            "HIAT for ODT-STD + Common annotations",
                                                            "HIAT + Roman languages supplement",
                                                            "HIAT + Germanic languages supplement",
                                                            "HIAT + Turkish supplement",
                                                            "HIAT + Polish supplement",
                                                            "HIAT + Arabic supplement",
                                                            "HIAT + Persian supplement",
                                                            "KgSR",
                                                            "DIDA",
                                                            "GAT",
                                                            "GAT + Persian supplement",
                                                            "TEI",
                                                            "International Phonetic Alphabet",
                                                            "iConc prosodic notation system",
                                                            "Braille Intonation",
                                                            "Greek Alphabet",
                                                            "Cyrillic Alphabet",
                                                            "HIAT + Russian Transliteration",
                                                            "Armenian Alphabet",
                                                            "Combining Diacritics",
                                                            "SyncWriter Conversion",
                                                            "Miscellaneous Symbols"
                                                           };
    
    private static String[] EXTERNAL_CHARACTER_SETS;
    
    private static String[] ALL_CHARACTER_SET_NAMES;
    
    private Hashtable charSets;
    
    KeyPanel keyPanel;
    
    /** Creates new form UnicodeKeyboardPanel */
    public UnicodeKeyboardPanel(String[] externalCharsets, String defaultFontName) {
        //System.out.println("HHHHHHAAAAAAAAAAAALLLLLLLLLLLOOOOOOOOOOO");
        //keyPanel.requestDefaultFocus();
        charSets = new Hashtable();
        keyPanel = new KeyPanel(defaultFontName);
        String filename = BUILT_IN_CHARACTER_SETS[0];
        UnicodeKeyboardSaxReader reader = new UnicodeKeyboardSaxReader();
        try{
            UnicodeKeyboard keyboard = reader.readFromStream(filename);
            keyPanel.setKeys(keyboard);
        } catch (SAXException se){
             System.out.println("Exception reading internal keyboard " + filename);
             System.out.println (se.getLocalizedMessage());
        }
        
        EXTERNAL_CHARACTER_SETS = externalCharsets;
        Vector externalCharsetNames = new Vector();
        for (int pos=0; pos<externalCharsets.length; pos++){
            filename = externalCharsets[pos];
            try{
                UnicodeKeyboard extKeyboard = reader.readFromFile(filename);
                externalCharsetNames.add(extKeyboard.name);
            } catch (SAXException se){
                 System.out.println("Exception reading external keyboard " + filename);
                 System.out.println (se.getLocalizedMessage());
            }
        }

        ALL_CHARACTER_SET_NAMES = new String[BUILT_IN_CHARACTER_SET_NAMES.length + externalCharsetNames.size()];
        for (int pos=0; pos<BUILT_IN_CHARACTER_SET_NAMES.length; pos++){
            ALL_CHARACTER_SET_NAMES[pos] = BUILT_IN_CHARACTER_SET_NAMES[pos];
        }
        for (int pos=0; pos<externalCharsetNames.size(); pos++){
            ALL_CHARACTER_SET_NAMES[pos + BUILT_IN_CHARACTER_SET_NAMES.length] = (String)(externalCharsetNames.elementAt(pos));
        }
        initComponents ();
        
        sizeSlider.addChangeListener(this);
    }
    
    public void setGeneralPurposeFontName(String fontName){
        keyPanel.setGeneralPurposeFont(fontName);
    }
    
    
    public void addListener(UnicodeKeyboardListener l){
        keyPanel.addUnicodeListener(l);
    }
    
    public void removeListener(UnicodeKeyboardListener l){
        keyPanel.removeUnicodeListener(l);
    }
    
    public void removeAllListeners(){
        keyPanel.removeAllListeners();
    }

    public void setKeyboard(String virtKeyboard) {
        charSetComboBox.setSelectedItem(virtKeyboard);
        repaint();
    }

    public String getKeyboard(){
        return (String)(charSetComboBox.getSelectedItem());
    }

    public int getKeySize(){
        //return keyPanel.getKeySize();
        return sizeSlider.getValue();
    }

    public void setKeySize(int p){
        //keyPanel.changeKeySize(p);
        sizeSlider.setValue(p-1);
        sizeSlider.setValue(p);

    }


    public void stateChanged(ChangeEvent e) {
        //if (sizeSlider.getValueIsAdjusting()) return;
        int v = sizeSlider.getValue();
        keyPanel.changeKeySize(v);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        charSetComboBox = new javax.swing.JComboBox(ALL_CHARACTER_SET_NAMES);
        jScrollPane1 = new javax.swing.JScrollPane(keyPanel);
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        sizeSlider = new javax.swing.JSlider();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setMaximumSize(new java.awt.Dimension(600, 400));
        setMinimumSize(new java.awt.Dimension(20, 20));
        setPreferredSize(new java.awt.Dimension(300, 250));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(199, 50));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        charSetComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
        charSetComboBox.setMaximumSize(new java.awt.Dimension(32767, 27));
        charSetComboBox.setMinimumSize(new java.awt.Dimension(126, 27));
        charSetComboBox.setPreferredSize(new java.awt.Dimension(130, 27));
        charSetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                charSetComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(charSetComboBox);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setMaximumSize(null);
        jScrollPane1.setMinimumSize(null);
        jScrollPane1.setPreferredSize(null);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/partitureditor/partiture/Icons/EditScaleConstant.gif"))); // NOI18N
        jLabel1.setToolTipText("Move the slider up to enlarge display size");
        jPanel2.add(jLabel1);

        sizeSlider.setMajorTickSpacing(10);
        sizeSlider.setMaximum(200);
        sizeSlider.setMinimum(80);
        sizeSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setToolTipText("Key size");
        sizeSlider.setValue(100);
        sizeSlider.setPreferredSize(new java.awt.Dimension(25, 100));
        jPanel2.add(sizeSlider);

        add(jPanel2, java.awt.BorderLayout.WEST);
    }// </editor-fold>//GEN-END:initComponents

  private void charSetComboBoxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_charSetComboBoxActionPerformed
// Add your handling code here:
    int index = charSetComboBox.getSelectedIndex();
    UnicodeKeyboardSaxReader reader = new UnicodeKeyboardSaxReader();
    UnicodeKeyboard keyboard = new UnicodeKeyboard();
    try{
        if (index<UnicodeKeyboardPanel.BUILT_IN_CHARACTER_SETS.length){
            keyboard = reader.readFromStream(BUILT_IN_CHARACTER_SETS[index]);
        } else {
            keyboard = reader.readFromStream(EXTERNAL_CHARACTER_SETS[index-BUILT_IN_CHARACTER_SETS.length]);
        }
        keyPanel.setKeys(keyboard);
        sizeSlider.setValue(100);
    } catch (SAXException se){
        se.printStackTrace();
        //javax.swing.JOptionPane errorDialog = new javax.swing.JOptionPane();
        String text = new String("Error reading keyboard" + System.getProperty("line.separator"));
        text+=se.getLocalizedMessage() + System.getProperty("line.separator");
        JOptionPane.showMessageDialog(this, text);
    }
  }//GEN-LAST:event_charSetComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox charSetComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider sizeSlider;
    // End of variables declaration//GEN-END:variables

}