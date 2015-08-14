/*
 * PartiturMenuBar.java
 *
 * Created on 9. Maerz 2004, 09:31
 */

package org.exmaralda.partitureditor.partiture.menus;

import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;

/**
 *
 * @author  thomas
 */
public class PartiturMenuBar extends javax.swing.JMenuBar {
    
    /** the file menu */
    public FileMenu fileMenu;
    /** the edit menu */
    public EditMenu editMenu;
    /** the view menu */
    public ViewMenu viewMenu;
    /** the transcription menu */
    public TranscriptionMenu transcriptionMenu;
    /** the tier menu */
    public TierMenu tierMenu;
    /** the event menu */
    public EventMenu eventMenu;
    /** the timeline menu */
    public TimelineMenu timelineMenu;
    /** the format menu */
    public FormatMenu formatMenu;
    /** the segmentation menu */
    //public SegmentationMenu segmentationMenu;
    public CLARINMenu clarinMenu;
    
    /** project specific menus */
    public SFB538Menu sfb538Menu;
    public SinMenu sinMenu;
    public ODTSTDMenu odtstdMenu;
            
    /** Creates a new instance of PartiturMenuBar */
    public PartiturMenuBar(PartitureTableWithActions table) {
        
        fileMenu = new FileMenu(table);
        editMenu = new EditMenu(table);
        viewMenu = new ViewMenu(table);
        transcriptionMenu = new TranscriptionMenu(table);
        tierMenu = new TierMenu(table);
        eventMenu = new EventMenu(table);
        timelineMenu = new TimelineMenu(table);
        formatMenu = new FormatMenu(table);       
        //segmentationMenu = new SegmentationMenu(table);
        clarinMenu = new CLARINMenu(table);
        
        sfb538Menu = new SFB538Menu(table);
        sinMenu = new SinMenu(table);
        odtstdMenu = new ODTSTDMenu(table);
        
        add(fileMenu);
        add(editMenu);            
        add(viewMenu);
        add(transcriptionMenu);
        add(tierMenu);
        add(eventMenu);
        add(timelineMenu);
        add(formatMenu);
        add(clarinMenu);
        //add(segmentationMenu);

        add(sfb538Menu);
        add(sinMenu);
        add(odtstdMenu);
        
        
    }
    
}
