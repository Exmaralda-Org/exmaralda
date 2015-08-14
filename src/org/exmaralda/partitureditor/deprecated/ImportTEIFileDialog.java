/*
 * ImportTEIFileDialog.java
 *
 * Created on 12. August 2004, 17:45
 */

package org.exmaralda.partitureditor.deprecated;

import java.io.IOException;
import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.convert.TEIConverter;
import org.jdom.JDOMException;
import org.xml.sax.*;

import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;

/**
 *
 * @author  thomas
 */
public class ImportTEIFileDialog extends OpenBasicTranscriptionDialog {
    
    /** Creates a new instance of ImportTEIFileDialog */
    public ImportTEIFileDialog(String startDirectory) {
        super(startDirectory);
        ParameterFileFilter teiFilter = new ParameterFileFilter("xml", "Extensible Markup Language");
        setFileFilter(teiFilter);
        setDialogTitle("Import a TEI file");           
    }
    
    public boolean importTEI(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            try {
                setFilename(new String(getSelectedFile().toString()));
                TEIConverter tc = new TEIConverter();
                setTranscription(tc.readTEIFromFile(getFilename()));
                success = true;
            } catch (SAXException ex) {
                ex.printStackTrace();
            } catch (JDOMException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else {
            success=false;
        }         
        return success;
    }
    

    
    
}
