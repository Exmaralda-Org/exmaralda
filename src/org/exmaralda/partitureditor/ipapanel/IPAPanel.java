/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ipaPanel.java
 *
 * Created on 17.11.2009, 09:10:16
 */

package org.exmaralda.partitureditor.ipapanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardEvent;
import org.exmaralda.partitureditor.unicodeKeyboard.UnicodeKeyboardListener;
import org.jdom.Element;
import org.jdom.JDOMException;


/**
 *
 * @author thomas
 */
public class IPAPanel extends javax.swing.JPanel implements java.awt.event.MouseListener {

    String pathToVowelsImage = "/org/exmaralda/partitureditor/ipapanel/vowels2.png";
    javax.swing.ImageIcon vowelsImage;
    String pathToSuprasegmentalsImage = "/org/exmaralda/partitureditor/ipapanel/suprasegmentals.png";
    javax.swing.ImageIcon suprasegmentalsImage;
    String pathToConsonantsImage = "/org/exmaralda/partitureditor/ipapanel/consonants.png";
    javax.swing.ImageIcon consonantsImage;
    String pathToDiacriticsImage = "/org/exmaralda/partitureditor/ipapanel/diacritics.png";
    javax.swing.ImageIcon diacriticsImage;

    String pathToVowelsDefinition = "/org/exmaralda/partitureditor/ipapanel/vowels.xml";
    org.jdom.Document vowelsDefinition;
    String pathToSuprasegmentalsDefinition = "/org/exmaralda/partitureditor/ipapanel/suprasegmentals.xml";
    org.jdom.Document suprasegmentalsDefinition;
    String pathToConsonantsDefinition = "/org/exmaralda/partitureditor/ipapanel/consonants.xml";
    org.jdom.Document consonantsDefinition;
    String pathToDiacriticsDefinition = "/org/exmaralda/partitureditor/ipapanel/diacritics.xml";
    org.jdom.Document diacriticsDefinition;
    String fontName = "Arial Unicode MS";


    /** Creates new form ipaPanel */
    public IPAPanel() {
        boolean thisIsAMac = System.getProperty("os.name").substring(0,3).equalsIgnoreCase("mac");
        try {
            for (String f : java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()){
                if (f.equals("Doulos SIL")){
                    fontName = "Doulos SIL";
                    break;
                }
            }

            // get the background images
            long t0 = System.currentTimeMillis();
            vowelsImage = new javax.swing.ImageIcon(getClass().getResource(pathToVowelsImage));
            suprasegmentalsImage = new javax.swing.ImageIcon(getClass().getResource(pathToSuprasegmentalsImage));
            consonantsImage = new javax.swing.ImageIcon(getClass().getResource(pathToConsonantsImage));
            diacriticsImage = new javax.swing.ImageIcon(getClass().getResource(pathToDiacriticsImage));
            long t1 = System.currentTimeMillis();

            System.out.println("TIME: " + Long.toString(t1-t0));

            initComponents();
            
            // read the button definitions
            long t2 = System.currentTimeMillis();
            vowelsDefinition = new IOUtilities().readDocumentFromResource(pathToVowelsDefinition);
            suprasegmentalsDefinition = new IOUtilities().readDocumentFromResource(pathToSuprasegmentalsDefinition);
            consonantsDefinition = new IOUtilities().readDocumentFromResource(pathToConsonantsDefinition);
            diacriticsDefinition = new IOUtilities().readDocumentFromResource(pathToDiacriticsDefinition);
            long t3 = System.currentTimeMillis();
            System.out.println("TIME2: " + Long.toString(t3-t2));

            // add the buttons
            long t4 = System.currentTimeMillis();
            for (Object o : vowelsDefinition.getRootElement().getChildren("phoneme")){
                Element phoneme = (Element)o;
                int x = Integer.parseInt(phoneme.getAttributeValue("x"));
                int y = Integer.parseInt(phoneme.getAttributeValue("y"));
                IPAButton button = new IPAButton(phoneme, fontName, 14.0f);
                button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        buttonPressed(e);
                    }
                });
                button.addMouseListener(this);
                vowelsPanel.add(button);
                button.setBounds((int)Math.round(x)-5, (int)Math.round(y)-5, 30, 30);
            }

            for (Object o : suprasegmentalsDefinition.getRootElement().getChildren("phoneme")){
                Element phoneme = (Element)o;
                int x = Integer.parseInt(phoneme.getAttributeValue("x"));
                int y = Integer.parseInt(phoneme.getAttributeValue("y"));
                IPAButton button = new IPAButton(phoneme, fontName, 14.0f);
                button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        buttonPressed(e);
                    }
                });
                button.addMouseListener(this);
                suprasegmentalsPanel.add(button);
                button.setBounds((int)Math.round(x)-2, (int)Math.round(y)-2, 28, 26);
            }

            for (Object o : consonantsDefinition.getRootElement().getChildren("phoneme")){
                Element phoneme = (Element)o;
                int x = Integer.parseInt(phoneme.getAttributeValue("x"));
                int y = Integer.parseInt(phoneme.getAttributeValue("y"));
                IPAButton button = new IPAButton(phoneme, fontName, 12.0f);
                button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        buttonPressed(e);
                    }
                });
                button.addMouseListener(this);
                consonantsPanel.add(button);
                if (thisIsAMac){
                    button.setBounds((int)Math.round(x)-4, (int)Math.round(y)-4, 28, 28);
                } else {
                    button.setBounds((int)Math.round(x)-2, (int)Math.round(y)-2, 24, 24);
                }
            }

            for (Object o : diacriticsDefinition.getRootElement().getChildren("phoneme")){
                Element phoneme = (Element)o;
                int x = Integer.parseInt(phoneme.getAttributeValue("x"));
                int y = Integer.parseInt(phoneme.getAttributeValue("y"));
                IPAButton button = new IPAButton(phoneme, fontName, 14.0f);
                button.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        buttonPressed(e);
                    }
                });
                button.addMouseListener(this);
                diacriticsPanel.add(button);
                button.setBounds((int)Math.round(x)-2, (int)Math.round(y)-2, 48, 24);
            }
            long t5 = System.currentTimeMillis();
            System.out.println("TIME3: " + Long.toString(t5-t4));


        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void buttonPressed(ActionEvent e) {
        if (e.getSource() instanceof IPAButton){
            IPAButton b = (IPAButton)(e.getSource());
            fireUnicodeKeyPressed(b.character);
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        vowelsSuprasegmentalPanel = new javax.swing.JPanel();
        vowelsPanel = new ImagePanel(vowelsImage);
        suprasegmentalsPanel = new ImagePanel(suprasegmentalsImage);
        consonantsPanel = new ImagePanel(consonantsImage);
        diacriticsPanel = new ImagePanel(diacriticsImage);
        languagePanel = new javax.swing.JPanel();
        languageComboBox = new javax.swing.JComboBox();
        descriptionPanel = new javax.swing.JPanel();
        detailsPanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();
        magnifyPanel = new javax.swing.JPanel();
        magnifyLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        vowelsSuprasegmentalPanel.setLayout(new java.awt.BorderLayout());

        vowelsPanel.setBackground(new java.awt.Color(255, 255, 255));
        vowelsPanel.setLayout(null);
        vowelsSuprasegmentalPanel.add(vowelsPanel, java.awt.BorderLayout.CENTER);

        suprasegmentalsPanel.setBackground(new java.awt.Color(255, 255, 255));
        suprasegmentalsPanel.setLayout(null);
        vowelsSuprasegmentalPanel.add(suprasegmentalsPanel, java.awt.BorderLayout.EAST);

        tabbedPane.addTab("Vowels / Suprasegmentals", vowelsSuprasegmentalPanel);

        consonantsPanel.setLayout(null);
        tabbedPane.addTab("Consonants (pulmonic)", consonantsPanel);

        diacriticsPanel.setLayout(null);
        tabbedPane.addTab("Diacritics", diacriticsPanel);

        add(tabbedPane, java.awt.BorderLayout.CENTER);

        languagePanel.setLayout(new javax.swing.BoxLayout(languagePanel, javax.swing.BoxLayout.LINE_AXIS));

        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "English", "German", "French" }));
        languageComboBox.setEnabled(false);
        languagePanel.add(languageComboBox);

        add(languagePanel, java.awt.BorderLayout.NORTH);

        descriptionPanel.setLayout(new java.awt.BorderLayout());

        detailsPanel.setLayout(new javax.swing.BoxLayout(detailsPanel, javax.swing.BoxLayout.LINE_AXIS));

        descriptionLabel.setFont(new java.awt.Font("Tahoma", 0, 14));
        descriptionLabel.setForeground(java.awt.SystemColor.activeCaption);
        detailsPanel.add(descriptionLabel);

        descriptionPanel.add(detailsPanel, java.awt.BorderLayout.CENTER);

        magnifyPanel.setLayout(new javax.swing.BoxLayout(magnifyPanel, javax.swing.BoxLayout.LINE_AXIS));

        magnifyLabel.setBackground(new java.awt.Color(255, 255, 153));
        magnifyLabel.setFont(new java.awt.Font(fontName, java.awt.Font.BOLD, 48));
        magnifyLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        magnifyLabel.setText(" ");
        magnifyLabel.setMaximumSize(new java.awt.Dimension(130, 65));
        magnifyLabel.setMinimumSize(new java.awt.Dimension(130, 65));
        magnifyLabel.setOpaque(true);
        magnifyLabel.setPreferredSize(new java.awt.Dimension(130, 65));
        magnifyPanel.add(magnifyLabel);

        descriptionPanel.add(magnifyPanel, java.awt.BorderLayout.WEST);

        add(descriptionPanel, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel consonantsPanel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JPanel descriptionPanel;
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JPanel diacriticsPanel;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JPanel languagePanel;
    private javax.swing.JLabel magnifyLabel;
    private javax.swing.JPanel magnifyPanel;
    private javax.swing.JPanel suprasegmentalsPanel;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JPanel vowelsPanel;
    private javax.swing.JPanel vowelsSuprasegmentalPanel;
    // End of variables declaration//GEN-END:variables

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
       if (e.getSource() instanceof IPAButton){
           IPAButton b = (IPAButton)(e.getSource());
           magnifyLabel.setText(b.getText());
           descriptionLabel.setText(b.description);
       }
    }

    public void mouseExited(MouseEvent e) {
        magnifyLabel.setText(null);
        descriptionLabel.setText(null);
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


}
