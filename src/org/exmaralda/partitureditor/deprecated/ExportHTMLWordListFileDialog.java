/*
 * ExportHTMLWordListFileDialog.java
 *
 * Created on 27. September 2002, 13:53
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.interlinearText.HTMLParameters;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.jexmaralda.SegmentList;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.interlinearText.*;

/**
 *
 * @author  Thomas
 */
public class ExportHTMLWordListFileDialog extends AbstractXMLSaveAsDialog {
    
    SegmentList sl;
    InterlinearText it;    
    HTMLParameters param;    
    
    /** Creates a new instance of ExportHTMLTurnListDialog */
    public ExportHTMLWordListFileDialog(SegmentList s, InterlinearText i, HTMLParameters p, String startDirectory) {
        super(false);
        sl = s;
        it = i;
        param = p;        
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        setDialogTitle("Choose a HTML file for export");        
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
    
    public boolean saveHTML(java.awt.Component parent){
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
            if (param.getWidth()>0) {
                it.trim(param);
            }
            String nakedFileName = getFilename().substring(0,getFilename().lastIndexOf('.'));
            String transcriptFileName = nakedFileName + "_t.html";
            String segmentListFileName = nakedFileName + "_l.html";
            
            System.out.println("started writing document...");
            FileOutputStream fos = new FileOutputStream(getFilename());
            fos.write("<html><head></head>".getBytes());
            fos.write("<frameset cols=\"80%,20%\">".getBytes());
            fos.write("<frame src=\"".getBytes());
            fos.write(transcriptFileName.substring(transcriptFileName.lastIndexOf(System.getProperty("file.separator"))+1).getBytes());
            fos.write("\" name=\"IT\">".getBytes());
            fos.write("<frame src=\"".getBytes());
            fos.write(segmentListFileName.substring(segmentListFileName.lastIndexOf(System.getProperty("file.separator"))+1).getBytes());
            fos.write("\" name=\"WL\">".getBytes());
            fos.write("<noframes></noframes></frameset></html>".getBytes());
            fos.close();
            System.out.println("document written.");            
            
            // write IT HTML
            it.writeHTMLToFile(transcriptFileName,param);                                

            // write segmentlist HTML
            sl.writeHTMLToFile(segmentListFileName,"IT", transcriptFileName);
        }
        catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    
}
