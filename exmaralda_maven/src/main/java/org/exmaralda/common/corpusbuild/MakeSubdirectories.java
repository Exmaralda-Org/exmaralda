/*
 * MakeSubDirectories.java
 *
 * Created on 21. November 2006, 15:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import java.io.*;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class MakeSubdirectories extends AbstractBasicTranscriptionProcessor {
    
    /** Creates a new instance of MakeSubDirectories */
    public MakeSubdirectories(String corpusName) {
        super(corpusName);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String corpusFilename = "S:\\Korpora\\DOLMETSCHEN-SSHC\\Publikation\\0.1\\new.coma";
            //MakeSubdirectories msd = new MakeSubdirectories(args[0]);
            MakeSubdirectories msd = new MakeSubdirectories(corpusFilename);
            msd.doIt();
            //msd.output(args[1]);
            msd.output("S:\\Korpora\\DOLMETSCHEN-SSHC\\Publikation\\0.1\\Subdirectory_Messages.txt");
        } catch (JexmaraldaException ex) {
            ex.printStackTrace();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void processTranscription(BasicTranscription bt) {
        File f = new File(this.currentFilename);
        String exportDirectoryName = f.getParent() + System.getProperty("file.separator") + "export";
        File d = new File(exportDirectoryName);
        boolean created = d.mkdir();
        outappend(exportDirectoryName + " " );
        if (!created){ out.append("NOT ");}
        out.append("created.\n");

        String presentationDirectoryName = f.getParent() + System.getProperty("file.separator") + "presentation";
        File d2 = new File(presentationDirectoryName);
        created = d2.mkdir();
        outappend(presentationDirectoryName + " ");
        if (!created){ out.append("NOT ");}
        out.append("created.\n");
    }
    
}
