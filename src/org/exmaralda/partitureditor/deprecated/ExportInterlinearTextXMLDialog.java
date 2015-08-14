/*
 * ExportInterlinearTextXMLDialog.java
 *
 * Created on 27. Juni 2002, 09:46
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.ItConverter;
import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.*;
import org.exmaralda.partitureditor.interlinearText.*;
import org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportInterlinearTextXMLDialog extends SaveBasicTranscriptionAsDialog{

    private org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel settingsPanel;
    private TierFormatTable tierFormatTable;
    private PrintParameters printParameters;
    private RTFParameters rtfParameters;
    private HTMLParameters htmlParameters;

    /** Creates new ExportInterlinearTextXMLDialog */
    public ExportInterlinearTextXMLDialog(String startDirectory, BasicTranscription t, 
                                          TierFormatTable tft,
                                          PrintParameters pp,
                                          RTFParameters rp,
                                          HTMLParameters hp) {
        super(startDirectory, t, false);
        setFileFilter(new ParameterFileFilter("xml", "Extensible Markup Language"));
        setDialogTitle("Export an interlinear text to an XML file");        
        settingsPanel = new org.exmaralda.partitureditor.interlinearText.swing.ChooseSettingsForXMLExportPanel();
        setAccessory(settingsPanel);        
        tierFormatTable = tft;
        printParameters = pp;
        rtfParameters = rp;
        htmlParameters = hp;
    }
    
    public boolean exportInterlinearTextXML(java.awt.Component parent) {
        boolean proceed = false;
        while (!proceed){
            int returnVal = showSaveDialog(parent);
            if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                checkExtension();
                proceed=checkOverwrite(parent);
            }
            else {success = false; return success;}
        }
        try {
            InterlinearText it = ItConverter.BasicTranscriptionToInterlinearText(getTranscription(), tierFormatTable);
            short param = settingsPanel.getSelection();
            if (param == ChooseSettingsForXMLExportPanel.USE_PRINT_SETTINGS) {it.trim(printParameters);}
            else if (param == ChooseSettingsForXMLExportPanel.USE_RTF_SETTINGS) {it.trim(rtfParameters);}
            else if (param == ChooseSettingsForXMLExportPanel.USE_HTML_SETTINGS) {
                if (htmlParameters.getWidth()>0){
                    it.trim(htmlParameters);
                } else {
                    it.calculateOffsets();
                }
            }        
            it.writeXMLToFile(getFilename());
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
        
    }

    @Override
    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(File.separatorChar)){
            filename = filename + ".xml";
        }
    }


}
