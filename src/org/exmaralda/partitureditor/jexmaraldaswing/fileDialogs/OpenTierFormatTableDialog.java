/*
 * OpenTierFormatTableDialog.java
 *
 * Created on 9. August 2001, 14:51
 */

package org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs;

import org.exmaralda.partitureditor.jexmaralda.TierFormatTable;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import java.net.*;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.xml.sax.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class OpenTierFormatTableDialog extends AbstractXMLOpenDialog {

    private TierFormatTable tierFormatTable;
    
    /** Creates new OpenTierFormatTableDialog */
    public OpenTierFormatTableDialog() {
        super();
        setDialogTitle("Open a format table from file");
        changeFileFilter();
    }
    
    /** Creates new OpenTierFormatTableDialog and sets start directory */
    public OpenTierFormatTableDialog(String startDirectory){
        super(startDirectory);
        setDialogTitle("Open a format table from file");
        changeFileFilter();
    }    
    
    public TierFormatTable getTierFormatTable(){
        return tierFormatTable;
    }
    
    public boolean openTierFormatTable(java.awt.Component parent){
        int returnVal = showOpenDialog(parent);
        if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            setFilename(new String(getSelectedFile().toString()));
            try {
                tierFormatTable = new TierFormatTable(getFilename());
                success=true;
            }
            catch (SAXException se){
                showSAXErrorDialog(se, parent);
            }
/*            catch (JexmaraldaException je){
                showJexmaraldaErrorDialog(je, parent);
            }*/
        }
        else {
            success=false;
        }         
        return success;
    }
    
    private void changeFileFilter() {
        String[] suff = {"exf", "xml"};
        ParameterFileFilter formatFileFilter = new ParameterFileFilter(suff, "EXMARaLDA Format Table (*.exf, *.xml)");
        this.setFileFilter(formatFileFilter);
    }
    

}