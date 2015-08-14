/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ApplicationFrame.java
 *
 * Created on 21.09.2010, 10:53:59
 */

package org.exmaralda.tei.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.folker.utilities.HTMLDisplayDialog;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.CHATConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.ELANConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.exmaralda.partitureditor.jexmaralda.convert.TranscriberConverter;
import org.exmaralda.partitureditor.partiture.BrowserLauncher;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter; 

/**
 *
 * @author thomas
 */
public class ApplicationFrame extends javax.swing.JFrame {

    // TEST: EXMARaLDA_FRESH
    
    DropPanel dropPanel = new DropPanel();
    FileDrop fileDrop;
    DefaultListModel listModel = new DefaultListModel();
    int done = 0;
    int all = 0;
    LineBorder dragBorder = new LineBorder(Color.RED, 3, true);

    ImageIcon inactiveIcon;
    ImageIcon activeIcon;



    /** Creates new form ApplicationFrame */
    public ApplicationFrame() {
        initComponents();
        inactiveIcon = new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/tei/swing/droptarget.png"));
        activeIcon = new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/tei/swing/droptarget_active.png"));
        mainPanel.add(dropPanel, java.awt.BorderLayout.WEST);
        teiFilesList.setModel(listModel);
        pack();
        fileDrop = new  FileDrop( dropPanel,
                dragBorder,
                new FileDrop.Listener(){
                @Override
                public void  filesDropped( java.io.File[] files )
                    {
                        // handle file drop
                           handleFileDrop(files);
                    }   // end filesDropped
        }); // end FileDrop.Listener
    }

   void message(final String s){
       int index=0;
       while (index<s.length()){
         String schnippel = s.substring(index, Math.min(index+75, s.length()));
         messagesTextArea.append(schnippel + "\n");
         index+=75;
       }
       messagesTextArea.setCaretPosition(messagesTextArea.getText().length()-1);
   }

   void updateProgress(String s){
       done++;
       progressBar.setValue((int)Math.round(((double)done/all)*100));
       progressBar.setString(s);
       if (done==all){
           progressBar.setString("Done.");
           dropPanel.setIcon(inactiveIcon);
           message("***** DONE *****");
       }
       if ((teiFilesList.getModel().getSize()>0) || (teiFilesList.isVisible())){
           final int lastIndex = teiFilesList.getModel().getSize()-1;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        teiFilesList.getSelectionModel().setSelectionInterval(lastIndex, lastIndex);
                    }
                });
               teiFilesList.scrollRectToVisible(teiFilesList.getCellBounds(lastIndex, lastIndex));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
       }
   }

   Vector<File> getFilesFromDirectory(File dir) {
       Vector<File> result = new Vector<File>();
       File[] fs = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith("exb") 
                        || name.toLowerCase().endsWith("eaf")
                        || name.toLowerCase().endsWith("trs")
                        || name.toLowerCase().endsWith("flk")
                        || name.toLowerCase().endsWith("cha")
                        );
            }
        });
        for (File f : fs){
            result.addElement(f);
        }
        File[] dirs = dir.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
            
        });        
        for (File d : dirs){
            result.addAll(getFilesFromDirectory(d));
        }
       return result;
   }

   void handleFileDrop(final File[] files){
        Vector<File> allFiles = new Vector<File>();
        for (File f : files){
            if (f.isDirectory()){
                message("[Directory "+ f.getName() + "]");
                Vector<File> filesinthis = getFilesFromDirectory(f);
                for (File ff : filesinthis){
                    allFiles.add(ff);
                    message(ff.getName() + " added to list.");                    
                }
            } else {
                int index = f.getName().lastIndexOf(".");
                if (index<0){
                    message("["+ f.getName() + "]" + " Cannot determine file type (missing suffix)");
                } else {
                    String suffix = f.getName().substring(index+1).toLowerCase();
                    if (("exb".equals(suffix) || "eaf".equals(suffix)|| "trs".equals(suffix) || "cha".equals(suffix) || "flk".equals(suffix))){
                        allFiles.add(f);
                        message(f.getName() + " added to list.");
                    } else {
                        message(f.getName() + " not added to list (suffix " + suffix +  " not recognized).");
                    }
                }
            }
        }
        done = 0;
        all = allFiles.size();
        dropPanel.setIcon(activeIcon);

        /*if (f.isDirectory()){
            message("[Directory "+ f.getName() + "]");
            updateProgress(f.getName());
            return;
        }*/
        // to do : recurse into subdirectories

        for (final File f : allFiles){
            Thread t = new Thread(){

                @Override
                public void run() {
                        // Determine input type
                        int index = f.getName().lastIndexOf(".");
                        String suffix = f.getName().substring(index+1);

                        BasicTranscription bt = null;
                        try {
                            if ("exb".equalsIgnoreCase(suffix)){
                                    bt = new BasicTranscription(f.getAbsolutePath());
                                    message(f.getName() + " read as EXMARaLDA basic transcription");
                            } else if ("eaf".equalsIgnoreCase(suffix)){
                                    ELANConverter elanConverter = new ELANConverter();
                                    bt = elanConverter.readELANFromFile(f.getAbsolutePath());
                                    message(f.getName() + " read as ELAN annotation file");
                            } else if ("cha".equalsIgnoreCase(suffix)){
                                    CHATConverter chatConverter = new CHATConverter(f);
                                    bt = chatConverter.convert();
                                    message(f.getName() + " read as CHAT transcription file");
                            } else if ("flk".equalsIgnoreCase(suffix)){
                                    bt = org.exmaralda.folker.io.EventListTranscriptionXMLReaderWriter.readXMLAsBasicTranscription(f);
                                    message(f.getName() + " read as FOLKER transcription file");
                            }  else if ("trs".equalsIgnoreCase(suffix)){
                                    TranscriberConverter transcriberConverter = new TranscriberConverter();
                                    bt = transcriberConverter.readTranscriberFromFile(f.getAbsolutePath());
                                    message(f.getName() + " read as Transcriber file");
                            } else {
                                message("["+ f.getName() + "]" + " File suffix " + suffix + " not recognized");
                                updateProgress(f.getName());
                                return;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            message("["+ f.getName() + "] " + ex.getLocalizedMessage());
                            updateProgress(f.getName());
                            return;
                        }

                        // Determine output name
                        File directory;
                        if (sameDirectory.isSelected()){
                            directory = f.getParentFile();
                        } else {
                            directory = new File(otherDirectoryTextField.getText());
                        }
                        String name = f.getName().substring(0,index) + suffixTextField.getText();
                        File output = new File(directory, name);
                        String OUTPUT_NAME = output.getAbsolutePath();

                        // convert
                        TEIConverter teiConverter = new TEIConverter();
                        try {
                            bt.normalize();
                            switch (parseMethodComboBox.getSelectedIndex()){
                                case 0 :
                                    //teiConverter.writeGenericTEIToFile(bt, OUTPUT_NAME);                                    
                                    teiConverter.writeGenericISOTEIToFile(bt, OUTPUT_NAME);
                                    break;
                                case 1 : // cGAT
                                    //teiConverter.writeFOLKERTEIToFile(bt, OUTPUT_NAME);
                                    teiConverter.writeFOLKERISOTEIToFile(bt, OUTPUT_NAME);
                                    break;
                                case 2 : // HIAT
                                    try {
                                        //teiConverter.writeNewHIATTEIToFile(bt, OUTPUT_NAME);
                                        teiConverter.writeHIATISOTEIToFile(bt, OUTPUT_NAME);
                                    } catch (FSMException fsm){
                                        message("["+ f.getName() + "]" + " Parse error.");
                                        teiConverter.writeGenericTEIToFile(bt, OUTPUT_NAME);
                                    }
                            }

                            String SHORT_ON = OUTPUT_NAME;
                            if (SHORT_ON.length()>35){
                                SHORT_ON = "..." + SHORT_ON.substring(SHORT_ON.length()-35);
                            }
                            message("["+ f.getName() + "]" + " TEI file written to " + SHORT_ON);
                            listModel.addElement(new File(OUTPUT_NAME));
                            updateProgress(f.getName());
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            message("["+ f.getName() + "] " + OUTPUT_NAME + " could not be written:");
                            message(ex.getLocalizedMessage());
                            updateProgress(f.getName());
                        }
                }
            };
            t.start();
        }
    }

   void displayHelp(){
        HTMLDisplayDialog dialog = new HTMLDisplayDialog(this, false);
        dialog.setTitle("TEI Drop Help");
        dialog.setSize(600, 400);
        dialog.setPreferredSize(dialog.getSize());
        dialog.setLocationRelativeTo(this);
        try {
            dialog.loadInternalResource("/org/exmaralda/tei/documentation.html");
            dialog.setVisible(true);
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        settingsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        parseMethodComboBox = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        sameDirectory = new javax.swing.JRadioButton();
        otherDirectory = new javax.swing.JRadioButton();
        otherDirectoryTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        suffixTextField = new javax.swing.JTextField();
        mainPanel = new javax.swing.JPanel();
        messageAndProgressPanel = new javax.swing.JPanel();
        messageScrollPane = new javax.swing.JScrollPane();
        messagesTextArea = new javax.swing.JTextArea();
        listScrollPane = new javax.swing.JScrollPane();
        teiFilesList = new javax.swing.JList();
        lowerPanel = new javax.swing.JPanel();
        operationsPanel = new javax.swing.JPanel();
        showXMLButton = new javax.swing.JButton();
        showHTMLButton = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        progressBar = new javax.swing.JProgressBar();
        dropletToggleButton = new javax.swing.JToggleButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TEI Drop");
        setAlwaysOnTop(true);

        settingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        settingsPanel.setLayout(new javax.swing.BoxLayout(settingsPanel, javax.swing.BoxLayout.Y_AXIS));

        jPanel1.setAlignmentX(0.0F);
        jPanel1.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel1.setText("Parse method: ");
        jLabel1.setMaximumSize(new java.awt.Dimension(150, 20));
        jLabel1.setMinimumSize(new java.awt.Dimension(150, 20));
        jLabel1.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel1.add(jLabel1);

        parseMethodComboBox.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        parseMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "cGAT", "HIAT" }));
        parseMethodComboBox.setMaximumSize(new java.awt.Dimension(90, 24));
        parseMethodComboBox.setMinimumSize(new java.awt.Dimension(63, 24));
        parseMethodComboBox.setPreferredSize(new java.awt.Dimension(68, 24));
        jPanel1.add(parseMethodComboBox);

        settingsPanel.add(jPanel1);

        jPanel4.setAlignmentX(0.0F);
        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel2.setText("Write output to...");
        jLabel2.setMaximumSize(new java.awt.Dimension(150, 20));
        jLabel2.setMinimumSize(new java.awt.Dimension(150, 20));
        jLabel2.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel4.add(jLabel2);

        buttonGroup1.add(sameDirectory);
        sameDirectory.setFont(new java.awt.Font("Tahoma", 1, 14));
        sameDirectory.setSelected(true);
        sameDirectory.setText("...the same...");
        jPanel4.add(sameDirectory);

        buttonGroup1.add(otherDirectory);
        otherDirectory.setFont(new java.awt.Font("Tahoma", 1, 14));
        otherDirectory.setText("...a separate directory: ");
        jPanel4.add(otherDirectory);

        otherDirectoryTextField.setFont(new java.awt.Font("Tahoma", 1, 14));
        otherDirectoryTextField.setAlignmentX(1.0F);
        otherDirectoryTextField.setMaximumSize(new java.awt.Dimension(300, 20));
        otherDirectoryTextField.setMinimumSize(new java.awt.Dimension(200, 20));
        otherDirectoryTextField.setPreferredSize(new java.awt.Dimension(250, 20));
        jPanel4.add(otherDirectoryTextField);

        browseButton.setText("Browse...");
        browseButton.setAlignmentX(0.5F);
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });
        jPanel4.add(browseButton);

        settingsPanel.add(jPanel4);

        jPanel5.setAlignmentX(0.0F);
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14));
        jLabel3.setText("Suffix for output: ");
        jLabel3.setMaximumSize(new java.awt.Dimension(150, 20));
        jLabel3.setMinimumSize(new java.awt.Dimension(150, 20));
        jLabel3.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel5.add(jLabel3);

        suffixTextField.setFont(new java.awt.Font("Tahoma", 1, 14));
        suffixTextField.setText("_TEI.xml");
        suffixTextField.setMaximumSize(new java.awt.Dimension(180, 23));
        suffixTextField.setPreferredSize(new java.awt.Dimension(120, 23));
        jPanel5.add(suffixTextField);

        settingsPanel.add(jPanel5);

        getContentPane().add(settingsPanel, java.awt.BorderLayout.NORTH);

        mainPanel.setLayout(new java.awt.BorderLayout());

        messageAndProgressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Messages", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N
        messageAndProgressPanel.setLayout(new java.awt.BorderLayout());

        messageScrollPane.setPreferredSize(new java.awt.Dimension(400, 120));

        messagesTextArea.setBackground(new java.awt.Color(0, 0, 0));
        messagesTextArea.setColumns(20);
        messagesTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        messagesTextArea.setForeground(new java.awt.Color(0, 153, 0));
        messagesTextArea.setRows(5);
        messageScrollPane.setViewportView(messagesTextArea);

        messageAndProgressPanel.add(messageScrollPane, java.awt.BorderLayout.CENTER);

        mainPanel.add(messageAndProgressPanel, java.awt.BorderLayout.CENTER);

        listScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Converted files", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        teiFilesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                teiFilesListValueChanged(evt);
            }
        });
        listScrollPane.setViewportView(teiFilesList);

        mainPanel.add(listScrollPane, java.awt.BorderLayout.SOUTH);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        lowerPanel.setLayout(new java.awt.BorderLayout());

        operationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        showXMLButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/tei/swing/xmldoc.gif"))); // NOI18N
        showXMLButton.setToolTipText("Show XML");
        showXMLButton.setEnabled(false);
        showXMLButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showXMLButton.setMaximumSize(new java.awt.Dimension(60, 60));
        showXMLButton.setMinimumSize(new java.awt.Dimension(60, 60));
        showXMLButton.setPreferredSize(new java.awt.Dimension(60, 60));
        showXMLButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showXMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showXMLButtonActionPerformed(evt);
            }
        });
        operationsPanel.add(showXMLButton);

        showHTMLButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/32x32/mimetypes/text-html.png"))); // NOI18N
        showHTMLButton.setToolTipText("Show HTML in Browser");
        showHTMLButton.setEnabled(false);
        showHTMLButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showHTMLButton.setMaximumSize(new java.awt.Dimension(60, 60));
        showHTMLButton.setMinimumSize(new java.awt.Dimension(60, 60));
        showHTMLButton.setPreferredSize(new java.awt.Dimension(60, 60));
        showHTMLButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showHTMLButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHTMLButtonActionPerformed(evt);
            }
        });
        operationsPanel.add(showHTMLButton);

        lowerPanel.add(operationsPanel, java.awt.BorderLayout.CENTER);

        jPanel7.setLayout(new java.awt.BorderLayout());

        progressBar.setMaximumSize(new java.awt.Dimension(300, 19));
        progressBar.setString("Waiting for input...");
        progressBar.setStringPainted(true);
        jPanel7.add(progressBar, java.awt.BorderLayout.CENTER);

        dropletToggleButton.setText("Reduce to droplet");
        dropletToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropletToggleButtonActionPerformed(evt);
            }
        });
        jPanel7.add(dropletToggleButton, java.awt.BorderLayout.EAST);

        helpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/exmaralda/folker/tangoicons/tango-icon-theme-0.8.1/16x16/apps/help-browser.png"))); // NOI18N
        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        jPanel7.add(helpButton, java.awt.BorderLayout.WEST);

        lowerPanel.add(jPanel7, java.awt.BorderLayout.SOUTH);

        getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Choose directory");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int ret = jfc.showOpenDialog(this);
        if (ret==JFileChooser.APPROVE_OPTION){
            otherDirectoryTextField.setText(jfc.getSelectedFile().getAbsolutePath());
            otherDirectory.setSelected(true);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void teiFilesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_teiFilesListValueChanged
        enableOperations(teiFilesList.getSelectedIndex()>=0);
    }//GEN-LAST:event_teiFilesListValueChanged

    private void showXMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showXMLButtonActionPerformed
        File f = (File)(teiFilesList.getSelectedValue());
        Document teidoc;
        try {
            teidoc = IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
            return;
        }
        Format format = Format.getPrettyFormat();
        XMLOutputter outputter = new XMLOutputter(format);
        // Get the text pane's document
        JTextPane textPane = new JTextPane();
        StyledDocument doc = (StyledDocument) textPane.getDocument();
        // Create a style object and then set the style attributes
        Style style = doc.addStyle("StyleName", null);
        // Font family
        StyleConstants.setFontFamily(style, "Courier");
        // Font size
        StyleConstants.setFontSize(style, 12);
        // Foreground color
        StyleConstants.setForeground(style, Color.black);
        try {
            doc.insertString(doc.getLength(), outputter.outputString(teidoc), style);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        JDialog dialog = new JDialog(this, true);
        dialog.setTitle(f.getName());
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(new JScrollPane(textPane), BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        textPane.setCaretPosition(0);
        dialog.setVisible(true);

    }//GEN-LAST:event_showXMLButtonActionPerformed

    private void dropletToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropletToggleButtonActionPerformed
        boolean hide = !(dropletToggleButton.isSelected());
        if (!hide){
            dropletToggleButton.setText("Expand");
        } else {
            dropletToggleButton.setText("Reduce to droplet");
        }
        messageAndProgressPanel.setVisible(hide);
        settingsPanel.setVisible(hide);
        operationsPanel.setVisible(hide);
        listScrollPane.setVisible(hide);
        pack();
    }//GEN-LAST:event_dropletToggleButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        this.displayHelp();
    }//GEN-LAST:event_helpButtonActionPerformed

    private void showHTMLButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHTMLButtonActionPerformed
        try {
            File f = (File) (teiFilesList.getSelectedValue());
            File tempHTML = File.createTempFile("teidrophtml", ".html");
            tempHTML.deleteOnExit();
            String htmlString = new StylesheetFactory(true).applyInternalStylesheetToExternalXMLFile("/org/exmaralda/tei/xml/tei2html.xsl", f.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(tempHTML);
            fos.write(htmlString.getBytes("UTF-8"));
            fos.close();
            System.out.println("document written.");
            BrowserLauncher.openURL(tempHTML.toURI().toURL().toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(rootPane, ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_showHTMLButtonActionPerformed

    void enableOperations(boolean enable){
        showXMLButton.setEnabled(enable);
        showHTMLButton.setEnabled(enable);
    }
    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        try{
            System.out.println("Setting system L&F : " + javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
                e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ApplicationFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JToggleButton dropletToggleButton;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane listScrollPane;
    private javax.swing.JPanel lowerPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel messageAndProgressPanel;
    private javax.swing.JScrollPane messageScrollPane;
    private javax.swing.JTextArea messagesTextArea;
    private javax.swing.JPanel operationsPanel;
    private javax.swing.JRadioButton otherDirectory;
    private javax.swing.JTextField otherDirectoryTextField;
    private javax.swing.JComboBox parseMethodComboBox;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JRadioButton sameDirectory;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JButton showHTMLButton;
    private javax.swing.JButton showXMLButton;
    private javax.swing.JTextField suffixTextField;
    private javax.swing.JList teiFilesList;
    // End of variables declaration//GEN-END:variables

}
