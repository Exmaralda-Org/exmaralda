/*
 * LayoutPrototype.java
 *
 * Created on 10. Maerz 2008, 17:59
 */

package org.exmaralda.folker.experiment;

//import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import org.exmaralda.folker.experiment.transcription.DefaultEvent;
import org.exmaralda.folker.experiment.transcription.EXMARaLDABasicTranscription;
import org.exmaralda.folker.experiment.transcription.EventInterface;
import java.awt.event.ActionEvent;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.listview.TextAreaCellRenderer;
import org.exmaralda.folker.listview.CheckTableCellRenderer;
import org.exmaralda.folker.listview.RegularExpressionTextField;
import org.exmaralda.folker.listview.TimepointTableCellRenderer;
import org.exmaralda.folker.timeview.WaveFormViewer;
import org.xml.sax.SAXException;
import org.exmaralda.folker.timeview.AbstractTimeProportionalViewer;
import org.exmaralda.folker.timeview.TimeSelectionEvent;
import org.exmaralda.folker.timeview.TimeSelectionListener;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import java.io.*;
import org.exmaralda.partitureditor.sound.*;
import javax.swing.table.*;
import javax.swing.*;
import javax.swing.event.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.awt.*;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
@Deprecated
public class LayoutPrototype    extends javax.swing.JFrame 
                                implements TimeSelectionListener, 
                                ListSelectionListener,
                                TableModelListener {
    
    AudioPanel audioPanel;
    AbstractTimeProportionalViewer tpv;
    PartitureTableWithActions partitur;
    Playable player;
    //String fn = "T:\\TP-E5\\Mitarbeiter\\Hatice\\Digitalisierungsarbeit\\auch auf der externen Festplatte\\EFE10\\EFE10tk_Akin_0663\\EFE10tk_Akin_0663a_f_SKO_240201.wav";
    String fn = "T:\\TP-Z2\\DATEN\\BEISPIELE\\EXMARaLDA_DemoKorpus\\Arbeitsamt\\Helge_Schneider_Arbeitsamt.wav";
    String bt = "T:\\TP-Z2\\DATEN\\BEISPIELE\\EXMARaLDA_DemoKorpus\\Arbeitsamt\\Helge_Schneider_Arbeitsamt.xml";
    //String fn = "C:\\Dokumente und Einstellungen\\thomas\\Desktop\\A6M062.wav";
    //String bt = "T:\\TP-Z2\\DATEN\\K5\\0.5\\K5_Oeresund\\A6M062\\A6M062.xml";
    EventListTableModel tableModel;
    EXMARaLDABasicTranscription ebt;
    AddEventAction addEventAction;
    
    
    File wavePath = null;
    
    /** Creates new form LayoutPrototype */
    public LayoutPrototype() {
        initComponents();
        //try {
            //BasicTranscription trans = new BasicTranscription(bt);
            BasicTranscription trans = new BasicTranscription();

            trans = new BasicTranscription();
            trans.getBody().getCommonTimeline().completeTimes();

            ebt = new EXMARaLDABasicTranscription(trans);//
            tpv = new WaveFormViewer();                
            //tpv = new TierEventViewer(ebt);
            tableModel = new EventListTableModel(ebt);
            tableModel.addTableModelListener(this);            
            jTable1.setModel(tableModel);
            jTable1.getSelectionModel().addListSelectionListener(this);
        /*} catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        }*/
        //tpv.setSoundFile(fn);
        tpv.addTimeSelectionListener(this);

        player = new JMFPlayer();

        addEventAction = new AddEventAction(tableModel, tpv);
        tpv.getInputMap(tpv.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "addEvent");
        tpv.getActionMap().put("addEvent", addEventAction);  
        newEventButton.setAction(addEventAction);


        jScrollPane1.setViewportView(tpv);
        //jScrollPane2.setViewportBorder(new javax.swing.border.EmptyBorder(0,0,200,0));
        int ui = jScrollPane1.getHorizontalScrollBar().getUnitIncrement();
        System.out.println("Scroll " + ui);

        formatTable();

        partitur = new PartitureTableWithActions(this);
        partitur.getModel().setTranscription(trans);
        partiturPanel.add(partitur);
        
        this.pack();
        player.addPlayableListener(tpv);
        
    }
    
    
    void formatTable(){
        jTable1.setRowHeight(20);
        
        TableColumnModel cmodel = jTable1.getColumnModel();
        TextAreaCellRenderer textAreaRenderer = new TextAreaCellRenderer();
        TimepointTableCellRenderer timeCellRenderer = new TimepointTableCellRenderer();

        //cmodel.getColumn(3).setCellRenderer(textAreaRenderer);        
        cmodel.getColumn(0).setCellRenderer(timeCellRenderer);
        cmodel.getColumn(1).setCellRenderer(timeCellRenderer);
        //TextAreaCellEditor textEditor = new TextAreaCellEditor();
        //cmodel.getColumn(3).setCellEditor(textEditor);
        //JTextField tf = new JTextField();
        JTextField tf = new RegularExpressionTextField(org.exmaralda.folker.utilities.Constants.GAT_EVENT);
        tf.setFont(tf.getFont().deriveFont(14.0f));
        DefaultCellEditor dec1 = new DefaultCellEditor(tf);
        dec1.setClickCountToStart(1);        
        cmodel.getColumn(3).setCellEditor(dec1);
        
        JComboBox cb = new JComboBox();
        cb.setEditable(true);
        DefaultCellEditor dec2 = new DefaultCellEditor(cb);
        cmodel.getColumn(2).setCellEditor(dec2);          
        
        CheckTableCellRenderer ctcr = new CheckTableCellRenderer();
        cmodel.getColumn(4).setCellRenderer(ctcr);

        cmodel.getColumn(0).setPreferredWidth(150);
        cmodel.getColumn(1).setPreferredWidth(150);
        cmodel.getColumn(2).setPreferredWidth(150);
        cmodel.getColumn(3).setPreferredWidth(2000);
        
        cmodel.getColumn(0).setHeaderValue("Start");
        cmodel.getColumn(1).setHeaderValue("Ende");
        cmodel.getColumn(2).setHeaderValue("Sprecher");
        cmodel.getColumn(3).setHeaderValue("Transkriptionstext");
        cmodel.getColumn(4).setHeaderValue("Check");

    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        eventPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        partiturPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        playSelectionButton = new javax.swing.JButton();
        lastSecondButton = new javax.swing.JButton();
        tenSecondsPlusButton = new javax.swing.JButton();
        startLabel = new javax.swing.JLabel();
        endLabel = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        stopButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        goonButton = new javax.swing.JButton();
        newEventButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        openSoundButton = new javax.swing.JButton();
        openTranscriptionButton = new javax.swing.JButton();
        saveTranscriptionButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("FOLKER");
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(500, 200));
        jSplitPane1.setLeftComponent(jScrollPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        eventPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setFont(new java.awt.Font("Tahoma", 0, 12));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(800, 402));
        jTable1.setFont(new java.awt.Font("Arial", 0, 14));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Start", "Ende", "Sprecher", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(102, 204, 255));
        jTable1.setIntercellSpacing(new java.awt.Dimension(0, 0));
        jTable1.setShowHorizontalLines(false);
        jScrollPane2.setViewportView(jTable1);

        eventPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Ereignisse", eventPanel);

        partiturPanel.setMaximumSize(new java.awt.Dimension(10, 10));
        jTabbedPane1.addTab("Partitur", partiturPanel);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jTabbedPane1.addTab("Sprecherbeitr\u00e4ge", jPanel3);

        jPanel1.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        playSelectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sound/Start.gif")));
        playSelectionButton.setText("Auswahl");
        playSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playSelectionButtonActionPerformed(evt);
            }
        });

        jPanel4.add(playSelectionButton);

        lastSecondButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sound/Start.gif")));
        lastSecondButton.setText("Letzte Sekunde");
        lastSecondButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastSecondButtonActionPerformed(evt);
            }
        });

        jPanel4.add(lastSecondButton);

        tenSecondsPlusButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sound/Start.gif")));
        tenSecondsPlusButton.setText("Auswahl +5 sec");
        tenSecondsPlusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tenSecondsPlusButtonActionPerformed(evt);
            }
        });

        jPanel4.add(tenSecondsPlusButton);

        startLabel.setForeground(new java.awt.Color(0, 255, 0));
        startLabel.setText("0.0");
        jPanel4.add(startLabel);

        endLabel.setForeground(new java.awt.Color(255, 0, 51));
        endLabel.setText("0.0");
        jPanel4.add(endLabel);

        jSlider1.setValue(15);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jPanel4.add(jSlider1);

        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/sound/Stop.gif")));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        jPanel4.add(stopButton);

        jPanel1.add(jPanel4, java.awt.BorderLayout.NORTH);

        goonButton.setText("Weiter!");
        goonButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goonButtonActionPerformed(evt);
            }
        });

        jPanel6.add(goonButton);

        newEventButton.setText("Neues Ereignis");
        jPanel6.add(newEventButton);

        jPanel1.add(jPanel6, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        openSoundButton.setText("Neue Transkription...");
        openSoundButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSoundButtonActionPerformed(evt);
            }
        });

        jPanel5.add(openSoundButton);

        openTranscriptionButton.setText("Transkription \u00f6ffnen...");
        openTranscriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openTranscriptionButtonActionPerformed(evt);
            }
        });

        jPanel5.add(openTranscriptionButton);

        saveTranscriptionButton.setText("Transkription speichern...");
        saveTranscriptionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTranscriptionButtonActionPerformed(evt);
            }
        });

        jPanel5.add(saveTranscriptionButton);

        getContentPane().add(jPanel5, java.awt.BorderLayout.NORTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        player.stopPlayback();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void tenSecondsPlusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tenSecondsPlusButtonActionPerformed
        double end = tpv.getMilisecondForPixel(tpv.getSelectionEndPixel())/1000.0;
        player.setStartTime(tpv.getMilisecondForPixel(tpv.getSelectionStartPixel())/1000.0);
        player.setEndTime(Math.min(end+5.0, player.getTotalLength()));
        player.startPlayback();
    }//GEN-LAST:event_tenSecondsPlusButtonActionPerformed

    private void lastSecondButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastSecondButtonActionPerformed
        double end = tpv.getMilisecondForPixel(tpv.getSelectionEndPixel())/1000.0;
        player.setStartTime(Math.max(0,end-1.0));
        player.setEndTime(end);
        player.startPlayback();

    }//GEN-LAST:event_lastSecondButtonActionPerformed

    void openSound(File waveFile) throws IOException{
            tpv.setSoundFile(waveFile.getAbsolutePath());
            player.setSoundFile(waveFile.getAbsolutePath());
            setLastPath(waveFile);
            wavePath = waveFile;
            jScrollPane1.validate();
            
    }
    
    private void saveTranscriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTranscriptionButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ExmaraldaFileFilter());
        fc.setCurrentDirectory(getLastPath());
        int val = fc.showSaveDialog(this);
        if (val!=fc.APPROVE_OPTION) return;
        org.jdom.Document d = ebt.toXMLDocument();
        try {
            org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory sf 
                    = new org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory(true);
            String btstring = sf.applyInternalStylesheetToString("/transcription/EventList2Exmaralda.xsl", 
                    org.exmaralda.common.jdomutilities.IOUtilities.documentToString(d));
            BasicTranscription bt = new org.exmaralda.partitureditor.jexmaralda.BasicTranscription();
            bt.BasicTranscriptionFromString(btstring);
            bt.getHead().getMetaInformation().setReferencedFile(wavePath.getAbsolutePath());
            bt.writeXMLToFile(fc.getSelectedFile().getAbsolutePath(), "none");
            //jdomutilities.IOUtilities.writeDocumentToLocalFile(fc.getSelectedFile().getAbsolutePath(), d);
            setLastPath(fc.getSelectedFile());
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        } catch (TransformerException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        } catch (SAXException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        } catch (IOException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);            
        }

    }//GEN-LAST:event_saveTranscriptionButtonActionPerformed

    private void openTranscriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openTranscriptionButtonActionPerformed
        org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog d =
                new org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.OpenBasicTranscriptionDialog();
        d.setCurrentDirectory(getLastPath());
        if (d.openTranscription(this)){
            BasicTranscription bt = d.getTranscription();
            File f = new File(bt.getHead().getMetaInformation().getReferencedFile());
            try {
                openSound(f);
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorMessage(ex);
                return;
            }
            partitur.getModel().setTranscription(bt);
            partitur.resetFormat(true);
            
            ebt = new EXMARaLDABasicTranscription(bt);
            //tpv = new TierEventViewer(ebt);
            tableModel = new EventListTableModel(ebt);
            tableModel.addTableModelListener(this);
            jTable1.setModel(tableModel);
            jTable1.getSelectionModel().addListSelectionListener(this);
            tpv.addTimeSelectionListener(this);            
            
            addEventAction = new AddEventAction(tableModel, tpv);
            tpv.getInputMap(tpv.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "addEvent");
            tpv.getActionMap().put("addEvent", addEventAction);  
            newEventButton.setAction(addEventAction);

            //jScrollPane1.validate();
            jScrollPane1.revalidate();
                       
            formatTable();
            
            setLastPath(d.getSelectedFile());
        }
    }//GEN-LAST:event_openTranscriptionButtonActionPerformed

    private void openSoundButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSoundButtonActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.MediaFileFilter());
        fc.setCurrentDirectory(getLastPath());
        int val = fc.showOpenDialog(this);
        if (val!=fc.APPROVE_OPTION) return;
        File waveFile = fc.getSelectedFile();
        try {
            openSound(waveFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            displayErrorMessage(ex);
        }
    }//GEN-LAST:event_openSoundButtonActionPerformed

    private void playSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playSelectionButtonActionPerformed
        player.setStartTime(tpv.getMilisecondForPixel(tpv.getSelectionStartPixel())/1000.0);
        player.setEndTime(tpv.getMilisecondForPixel(tpv.getSelectionEndPixel())/1000.0);
        player.startPlayback();

    }//GEN-LAST:event_playSelectionButtonActionPerformed

    private void goonButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goonButtonActionPerformed
        tpv.shiftSelection();
    }//GEN-LAST:event_goonButtonActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        if (jSlider1.getValueIsAdjusting()) return;
        int v = jSlider1.getValue();
        int pixelsPerSecond = 20 + v*10;
        tpv.setPixelsPerSecond(pixelsPerSecond);
    }//GEN-LAST:event_jSlider1StateChanged
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try{
                    System.out.println("Setting system L&F : " + javax.swing.UIManager.getSystemLookAndFeelClassName());
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
                }           
                catch (Exception e) {
                        e.printStackTrace();        
                }
                new LayoutPrototype().setVisible(true);
            }
        });
    }

    public void processTimeSelectionEvent(TimeSelectionEvent event) {
        //System.out.println(event.getStartTime() + " / " + event.getEndTime());
        startLabel.setText(TimeStringFormatter.formatMiliseconds(event.getStartTime(),2));
        endLabel.setText(TimeStringFormatter.formatMiliseconds(event.getEndTime(),2));
        /*int rowIndex = ebt.findEvent(event.getStartTime());
        if (rowIndex!=-1){
            java.awt.Rectangle bounds = jTable1.getCellRect(rowIndex,0,true);
            jScrollPane2.getViewport().setViewPosition(bounds.getLocation());
        }                
        if (event.getType()==TimeSelectionEvent.SELECTION_CHANGED_WITH_RIGHT_MOUSE_BUTTON){
        }*/
        //audioPanel.setStartTime(event.getStartTime()/1000.0);
        //audioPanel.setEndTime(event.getEndTime()/1000.0);
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int[] index = jTable1.getSelectedRows();
        if (index.length<1) return;
        // added because of additional rows at the end of the table
        if (index[0]>=ebt.getNumberOfEvents()) return;
        if (index[index.length-1]>=ebt.getNumberOfEvents()) return;
        /*int pos = 1;
        while ((pos<=index.length) && (index[pos-1]<ebt.getNumberOfEvents())){pos++;}
        int lastIndex = index[pos-2];*/
        // end add        
        EventInterface ei1 = ebt.getEvent(index[0]);
        // changed because of additional rows at the end of the table
        //EventInterface ei2 = ebt.getEvent(index[lastIndex]);
        EventInterface ei2 = ebt.getEvent(index[index.length-1]);
        tpv.setSelectionInterval(ei1.getStartTime()*1000.0, ei2.getEndTime()*1000.0, true);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel endLabel;
    private javax.swing.JPanel eventPanel;
    private javax.swing.JButton goonButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton lastSecondButton;
    private javax.swing.JButton newEventButton;
    private javax.swing.JButton openSoundButton;
    private javax.swing.JButton openTranscriptionButton;
    private javax.swing.JPanel partiturPanel;
    private javax.swing.JButton playSelectionButton;
    private javax.swing.JButton saveTranscriptionButton;
    private javax.swing.JLabel startLabel;
    private javax.swing.JButton stopButton;
    private javax.swing.JButton tenSecondsPlusButton;
    // End of variables declaration//GEN-END:variables
    
    void displayErrorMessage(Exception ex){
        JOptionPane.showMessageDialog(this, ex.getLocalizedMessage());
    }

    public void tableChanged(TableModelEvent e) {
        if (e.getType()==e.INSERT){
            int row = e.getFirstRow();    
            Rectangle bounds = jTable1.getCellRect(row+tableModel.ADDITIONAL_ROWS,0,true);
            //jScrollPane2.getViewport().setViewPosition(bounds.getLocation());
            jTable1.scrollRectToVisible(bounds);
            jTable1.getSelectionModel().clearSelection();
            jTable1.requestFocus();
            jTable1.editCellAt(row, 3);            
            jTable1.getEditorComponent().requestFocus();
        }
    }
    
    File getLastPath(){
        java.util.prefs.Preferences settings = 
                java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.Folker");        
        String lp = settings.get("LastPath", System.getProperty("user.dir"));
        return new File(lp);        
    }
    
    void setLastPath(File f){
        java.util.prefs.Preferences settings = 
                java.util.prefs.Preferences.userRoot().node("org.sfb538.exmaralda.Folker");        
        settings.put("LastPath", f.getAbsolutePath());        
    }
}

class AddEventAction extends javax.swing.AbstractAction {
    
    EventListTableModel tableModel;
    AbstractTimeProportionalViewer tpv;
    
    public AddEventAction(EventListTableModel tm, AbstractTimeProportionalViewer tp){
        super("Neues Ereignis");
        tpv = tp;        
        tableModel = tm;
    }

    public void actionPerformed(ActionEvent e) {
        double start = tpv.getMilisecondForPixel(tpv.getSelectionStartPixel()) / 1000.0;
        double end = tpv.getMilisecondForPixel(tpv.getSelectionEndPixel()) / 1000.0;
        tpv.setSelectionAttached(true);
        DefaultEvent de = new DefaultEvent("", start, end, "", "", "", "");
        int insertedRow = tableModel.addEvent(de);        
    }
}
