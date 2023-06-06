 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.texgut.application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.bounce.text.xml.XMLEditorKit;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.texgut.data.EAFCreator;
import org.exmaralda.texgut.data.ELANChecker;
import org.exmaralda.texgut.data.ELANMessage;
import org.exmaralda.texgut.data.ELANMessage.ELANMessageType;
import org.exmaralda.texgut.data.ELANMessageListCellRenderer;
import org.exmaralda.texgut.data.ELANMessageListModel;
import org.exmaralda.texgut.gui.NewEAFDialog;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class ApplicationControl implements ListDataListener, ListSelectionListener {

    ApplicationFrame applicationFrame;   
    Preferences preferences;
    
    List<ELANMessage> messageList = new ArrayList<>();
    ELANMessageListModel messageListModel = new ELANMessageListModel(messageList);
    
    ApplicationControl(ApplicationFrame af) {
        applicationFrame = af;
        preferences = java.util.prefs.Preferences.userRoot().node(applicationFrame.getPreferencesNode());
    }
    
    void setupConsole(){
        applicationFrame.consoleList.setModel(messageListModel);
        applicationFrame.consoleList.setCellRenderer(new ELANMessageListCellRenderer());
        console(new ELANMessage(ELANMessageType.MESSAGE, "Ready to order."));  
        messageListModel.addListDataListener(this);
        applicationFrame.consoleList.getSelectionModel().addListSelectionListener(this);
    }

    public void assignActions() {
        
    }

    public void retrieveSettings() {
        System.out.println("Retrieving settings");
        // window location
        int windowLocationX = Integer.parseInt(preferences.get("window-location-x", "0"));
        int windowLocationY = Integer.parseInt(preferences.get("window-location-y", "0"));
        applicationFrame.setLocation(windowLocationX, windowLocationY);
        
        String audioFolder = preferences.get("audio-folder", System.getProperty("user.dir"));
        applicationFrame.audioFolderTextField.setText(audioFolder);

        String transcriptFolder = preferences.get("transcript-folder", System.getProperty("user.dir"));
        applicationFrame.transcriptFolderTextField.setText(transcriptFolder);

    }
    
    public void storeSettings(){
        System.out.println("Storing settings");
        // window location
        preferences.put("window-location-x", Integer.toString(applicationFrame.getLocationOnScreen().x));        
        preferences.put("window-location-y", Integer.toString(applicationFrame.getLocationOnScreen().y));        
        
        preferences.put("audio-folder", applicationFrame.audioFolderTextField.getText());
        preferences.put("transcript-folder", applicationFrame.transcriptFolderTextField.getText());
    }

    void newEAF() {
        try {
            NewEAFDialog dialog = new NewEAFDialog(applicationFrame, true);
            dialog.setLocationRelativeTo(applicationFrame);
            dialog.setVisible(true);
            
            if (!(dialog.approved)) return;
            
            String filepath = dialog.getTranscriptionFilepath();
            File file = new File(filepath);
            if (file.exists()){
                String message = "<html>File <b>" + file.getAbsolutePath() + "</b> exists.<br/> Overwrite?</html>";
                int decision = JOptionPane.showConfirmDialog(applicationFrame, message, "Overwrite file?", JOptionPane.YES_NO_OPTION);
                if (decision==JOptionPane.NO_OPTION) return;
            }
            
            String[] speakerNumbers = dialog.getSpeakerNumbers();
            String[] interviewerNumbers = dialog.getInterviewerNumbers();
            String audioPath = dialog.getAudioFilepath();
            
            EAFCreator eafCreator = new EAFCreator(speakerNumbers, interviewerNumbers, audioPath, file);
            Document eafDocument = eafCreator.createEAF();
            
            boolean madeOne = file.getParentFile().mkdirs();
            if (madeOne){
                console(new ELANMessage("Directories created: " + file.getParent()));             
            }
            FileIO.writeDocumentToLocalFile(file, eafDocument);
            console(new ELANMessage(ELANMessageType.MESSAGE, file.getAbsolutePath() + " written.", file));
            
        } catch (JDOMException | IOException | SAXException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(applicationFrame, "Error: " + ex.getMessage());
            console(new ELANMessage(ELANMessageType.ERROR, "Error: " + ex.getMessage()));             
            
        }
    }

    private void console(ELANMessage message) {
        messageListModel.addMessage(message);
    }
    
    private void consoleNew() {
        messageListModel.addMessage(new ELANMessage(ELANMessageType.FORMAT, "------------"));                
    }
    

    void checkEAF() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(applicationFrame.transcriptFolderTextField.getText()));
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Choose EAF file or directory to check");
        FileFilter ff = new FileFilter(){
            @Override
            public boolean accept(File file) {
                return (file.isDirectory() || file.getName().toUpperCase().endsWith(".EAF"));
            }

            @Override
            public String getDescription() {
                return "Directories and EAF files";
            }            
        };
        fc.setFileFilter(ff);
        int approved = fc.showOpenDialog(applicationFrame);
        if (!(approved==JFileChooser.APPROVE_OPTION)) return;
        
        File f = fc.getSelectedFile();
        ELANChecker elanChecker = new ELANChecker();
        if (f.isDirectory()){
            Thread checkerThread = new Thread(){
                @Override
                public void run() {
                    consoleNew();
                    console(new ELANMessage("Checking directory: " + f.getAbsolutePath()));    
                    List<File> allEAFFiles = findFiles(f, ".eaf");
                    console(new ELANMessage(Integer.toString(allEAFFiles.size()) + " EAF files found. "));    
                    int totalMessagesCount = 0;
                    int count = 1;
                    for (File eafFile : allEAFFiles){
                        consoleNew();
                        console(new ELANMessage(ELANMessageType.MESSAGE, "Checking file " + Integer.toString(count) + " of " + Integer.toString(allEAFFiles.size()) + ": " + eafFile.getAbsolutePath(), eafFile));       
                        List<ELANMessage> messages = elanChecker.checkFile(eafFile);
                        totalMessagesCount+=messages.size();
                        for (ELANMessage m : messages){
                            console(m);
                        }
                        if (messages.isEmpty()){
                            console(new ELANMessage(ELANMessageType.MESSAGE, " *** No messages for " + eafFile.getName(), eafFile));                                     
                        }        
                        count++;
                    }
                    console(new ELANMessage("Directory checked. Total number of messages " + totalMessagesCount));    
                }                
            };
            checkerThread.start();
        } else {
            consoleNew();
            console(new ELANMessage(ELANMessageType.MESSAGE, "Checking file: " + f.getAbsolutePath(), f));       
            List<ELANMessage> messages = elanChecker.checkFile(f);
            for (ELANMessage m : messages){
                console(m);
            }
            if (messages.isEmpty()){
                console(new ELANMessage(ELANMessageType.MESSAGE, " *** No messages for " + f.getName(), f));                                     
            }
        }
        
        
    }

    private List<File> findFiles(File f, String suffix) {
        List<File> result = new ArrayList<>();
        File[] files = f.listFiles((File dir, String name) -> name.toUpperCase().endsWith(suffix.toUpperCase()));
        result.addAll(Arrays.asList(files));
        
        File[] directories = f.listFiles((File dir) -> dir.isDirectory());
        
        for (File dir : directories){
            result.addAll(findFiles(dir, suffix));
        }
        return result;
    }
    
    int updateCounter = 0;

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (updateCounter % 20 == 0){
            try{
                SwingUtilities.invokeLater(new Runnable(){
                    @Override
                    public void run() {
                        applicationFrame.consoleList.ensureIndexIsVisible(messageListModel.getSize()-1);
                    }
                    
                });
            } catch (Exception ex){

            }
        }
        updateCounter++;
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int index = e.getFirstIndex();
        if (index<0 || index>messageList.size()-1) return;
        ELANMessage message = this.messageList.get(index);
        applicationFrame.openInELANButton.setEnabled(message.file!=null);
        applicationFrame.xmlButton.setEnabled(message.file!=null);
    }

    void openFileForMessageAtIndex(int selectedIndex) {
        File file = this.messageList.get(selectedIndex).file;
        if (file!=null){
            try {
                System.out.println("Opening " + file.getAbsolutePath());
                Desktop.getDesktop().open(file);
            } catch (IOException ex) {
                Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(applicationFrame, "Could not open " + file.getAbsolutePath() + ":\n" + ex.getMessage());
            }
        }
    }

    void removeNonErrorMessages() {
        List<ELANMessage> remaining = new ArrayList<>();
        for (ELANMessage mess : messageList){
            if(mess.type==ELANMessageType.ERROR || mess.type==ELANMessageType.WARNING){
                remaining.add(mess);
            }
        }
        this.messageList = remaining;
        this.messageListModel = new ELANMessageListModel(remaining);
        applicationFrame.consoleList.setModel(messageListModel);
    }

    void showXMLForMessageAtIndex(int selectedIndex) {
        File file = this.messageList.get(selectedIndex).file;
        if (file!=null){
            try {
                Document doc = FileIO.readDocumentFromLocalFile(file);
                //String xmlString = IOUtilities.documentToString(doc);
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
                StringWriter stringWriter = new StringWriter();
                xmlOutputter.output(doc, stringWriter);
                String xmlString = stringWriter.toString();
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                
                final JEditorPane xmlEditorPane = new JEditorPane();
                XMLEditorKit xmlEditorKit = new XMLEditorKit(true);
                xmlEditorKit.setWrapStyleWord(true);
                xmlEditorPane.setFont(xmlEditorPane.getFont().deriveFont(11.0f));
                xmlEditorPane.setEditorKit(xmlEditorKit);
                xmlEditorPane.setText(xmlString);
                xmlEditorPane.setPreferredSize(new Dimension(800, 400));
                xmlEditorPane.setEditable(false);
                xmlEditorPane.setCaretPosition(0);
                xmlEditorPane.setBackground(new Color(230,230,230));
                
                JScrollPane scrollPane = new JScrollPane();
                scrollPane.setViewportView(xmlEditorPane);
                
                
                JDialog xmlDialog = new JDialog(applicationFrame, true);
                xmlDialog.setTitle(file.getName());
                xmlDialog.getContentPane().add(scrollPane);
                xmlDialog.pack();
                xmlDialog.setLocationRelativeTo(applicationFrame);
                xmlDialog.setVisible(true);
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(ApplicationControl.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }

    void clearConsole() {
        List<ELANMessage> fresh = new ArrayList<>();
        fresh.add(new ELANMessage("All neat and tidy."));
        this.messageList = fresh;
        this.messageListModel = new ELANMessageListModel(fresh);
        applicationFrame.consoleList.setModel(messageListModel);
    }
    
    
}
