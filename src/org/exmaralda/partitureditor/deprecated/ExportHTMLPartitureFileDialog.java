/*
 * ExportHTMLPartitureFileDialog.java
 *
 * Created on 3. April 2002, 12:42
 */

package org.exmaralda.partitureditor.deprecated;

import org.exmaralda.partitureditor.jexmaraldaswing.fileDialogs.*;
import org.exmaralda.partitureditor.interlinearText.HTMLParameters;
import org.exmaralda.partitureditor.interlinearText.InterlinearText;
import org.exmaralda.partitureditor.jexmaralda.*;
import java.io.*;
import org.exmaralda.partitureditor.interlinearText.*;

/**
 *
 * @author  Thomas
 * @version 
 */
public class ExportHTMLPartitureFileDialog extends AbstractXMLSaveAsDialog {

    InterlinearText it;    
    HTMLParameters param;
    HTMLFramesOptionPanel framesPanel;

    /** Creates new ExportHTMLPartitureFileDialog */
    public ExportHTMLPartitureFileDialog(InterlinearText i, HTMLParameters p) {
        super(false);
        framesPanel = new HTMLFramesOptionPanel();
        this.setAccessory(framesPanel);
        it = i;
        param = p;
        setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
        setFileFilter(new org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.HTMLFileFilter());        
        setDialogTitle("Set the HTML file for export");              
    }

    /** Creates new ExportRTFPartitureFileDialog */
    public ExportHTMLPartitureFileDialog(InterlinearText i, HTMLParameters p, String startDirectory) {
        this(i,p);
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
            if (!framesPanel.useFrames()){
                it.writeHTMLToFile(getFilename(),param);
            } else {
                // write frame html
                
                // write IT HTML
                String nakedFileName = getFilename().substring(0,getFilename().lastIndexOf('.'));
                String outputFileName = nakedFileName + "_p.html";
                param.linkTarget="LinkFrame";
                it.writeHTMLToFile(outputFileName,param);                                

                System.out.println("started writing document...");
                FileOutputStream fos = new FileOutputStream(getFilename());

                fos.write("<html><head></head>".getBytes());
                fos.write("<frameset rows=\"70%,30%\">".getBytes());
                fos.write("<frame src=\"".getBytes());
                fos.write(outputFileName.substring(outputFileName.lastIndexOf(System.getProperty("file.separator"))+1).getBytes());
                fos.write("\" name=\"IT\">".getBytes());
                fos.write("<frame name=\"LinkFrame\">".getBytes());
                fos.write("<noframes></noframes></frameset></html>".getBytes());
                
                fos.close();
                System.out.println("document written.");

            }
        } catch (IOException ioe) {
            showIOErrorDialog(ioe, parent);
        }
        return success;
    }
    

}
