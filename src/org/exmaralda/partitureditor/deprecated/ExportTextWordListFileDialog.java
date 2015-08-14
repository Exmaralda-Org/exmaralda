/*
 * ExportHTMLWordListFileDialog.java
 *
 * Created on 27. September 2002, 13:53
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.interlinearText.*;

/**
 *
 * @author  Thomas
 */
public class ExportTextWordListFileDialog extends AbstractXMLSaveAsDialog {
    
    SegmentList sl;
    Timeline timeline;
    
    /** Creates a new instance of ExportHTMLTurnListDialog */
    public ExportTextWordListFileDialog(SegmentList s, String startDirectory, Timeline tl) {
        super(false);
        sl = s;
        timeline = tl;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        try {
            setCurrentDirectory(new File(startDirectory));
        }
        catch (Throwable dummy){}  
        
    }

    public void checkExtension(){
        setFilename(new String(getSelectedFile().toString()));
        if (getFilename().indexOf('.')<getFilename().lastIndexOf(getSelectedFile().separatorChar)){
            setFilename(getFilename() + ".html");
        }
    }
    
    public boolean saveText(java.awt.Component parent){
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
            String theFilename = getFilename();
            
            System.out.println("started writing document...");
           
            // write segmentlist TXT
            sl.writeTextToFile(theFilename, timeline);
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
}
