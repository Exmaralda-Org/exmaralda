/*
 * AbstractTextSaveAsDialog.java
 *
 * Created on 22. Maerz 2004, 15:28
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import java.io.*;

/**
 *
 * @author  thomas
 */
public abstract class AbstractTextSaveAsDialog extends AbstractXMLSaveAsDialog {
    
    private JComboBox encodingComboBox;
    private JPanel accessoryPanel;
    private String[] encodingNames = {"[System-Default]", "7-Bit-ASCII", "ISO-8859-1 (Standard Latin)", "UTF-8", "UTF-16 (Big Endian)", "UTF-16 (Little Endian)"};
    private String[] encodings = {"", "US-ASCII", "ISO-8859-1", "UTF-8", "UTF-16BE", "UTF-16LE"};

    /** Creates a new instance of AbstractTextSaveAsDialog */
    public AbstractTextSaveAsDialog() {
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.TextFileFilter());       
        initAccessory();
    }
    
    private void initAccessory(){
        accessoryPanel = new JPanel();
        encodingComboBox = new JComboBox(encodingNames);
        accessoryPanel.setBorder(new javax.swing.border.TitledBorder("Char encoding"));
        accessoryPanel.add(encodingComboBox);
        org.exmaralda.common.helpers.Internationalizer.internationalizeComponentToolTips(accessoryPanel);
        this.setAccessory(accessoryPanel);        
    }
    
    public void writeText(String text) throws IOException {
        String filename = this.getFilename();
        System.out.println("started writing document...");
        FileOutputStream fos = new FileOutputStream(new File(filename));
        if (encodingComboBox.getSelectedIndex()==0){
            fos.write(text.getBytes());
        } else {
            fos.write(text.getBytes(encodings[encodingComboBox.getSelectedIndex()]));
        }
        fos.close();
        System.out.println("document written.");                       
    }
    
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            filename = filename + ".txt";
        }
    }
    
    
    
}
