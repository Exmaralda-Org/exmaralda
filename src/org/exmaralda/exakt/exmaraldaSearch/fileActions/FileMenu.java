/*
 * FileMenu.java
 *
 * Created on 9. Februar 2007, 11:37
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch.fileActions;

import javax.swing.*;
import org.exmaralda.exakt.exmaraldaSearch.swing.*;

/**
 *
 * @author thomas
 */
public class FileMenu extends javax.swing.JMenu {
    
    EXAKT exaktFrame;
    
    
    /** Creates a new instance of FileMenu */
    public FileMenu(EXAKT ef) {
        setText("File");
        exaktFrame = ef;
        add(exaktFrame.openCorpusAction);
        add(exaktFrame.openRemoteCorpusAction);
        add(exaktFrame.openDBCorpusAction);
        addSeparator();
        add(exaktFrame.generateCorpusAction);
        add(exaktFrame.generateFolkerCorpusAction).setToolTipText("Generate a corpus from FOLKER transcriptions");
        add(exaktFrame.generateCHATCorpusAction).setToolTipText("Generate a corpus from CHAT transcriptions");
        add(exaktFrame.generateEAFCorpusAction).setToolTipText("Generate a corpus from ELAN annotation files");
        add(exaktFrame.generateTranscriberCorpusAction).setToolTipText("Generate a corpus from Transcriber files");
        addSeparator();
    }

    public void setupOpenRecentMenu(java.util.Vector<String[]> recentFiles){
        //openRecentMenu.removeAll();
        int fileCount = 0;
        int pos=0;
        java.util.HashSet hs = new java.util.HashSet();
        while ((fileCount<5) && (pos<recentFiles.size())){
            String filename = recentFiles.elementAt(pos)[1];
            if (!hs.contains(filename)){
                if (new java.io.File(filename).exists()){
                    add(new OpenRecentAction(exaktFrame, filename)).setToolTipText(filename);
                    hs.add(filename);
                    fileCount++;
                }
            }
            pos++;
        }
        //addSeparator();
    }

    
}
