/*
 * this.java
 *
 * Created on 1. Juli 2003, 15:25
 */

package org.exmaralda.partitureditor.partiture.menus;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;
import org.exmaralda.partitureditor.partiture.*;
import org.exmaralda.common.helpers.Internationalizer;

/**
 *
 * @author  thomas
 */
public class ViewMenu extends AbstractTableMenu {
    
    private final javax.swing.JCheckBoxMenuItem showGridCheckBoxMenuItem;

    public JCheckBoxMenuItem showSpecialCharsCheckBoxMenuItem;   
    public JCheckBoxMenuItem useDifferentEmptyColorCheckBoxMenuItem;
    public JCheckBoxMenuItem showKeyboardCheckBoxMenuItem;
    public JCheckBoxMenuItem showLinkPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showMediaPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showPraatPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showAnnotationPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showIPAPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showMultimodalPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showSVGPanelCheckBoxMenuItem;
    //public JCheckBoxMenuItem showQuickMediaOpenCheckBoxMenuItem;
    //public JCheckBoxMenuItem showSegmentationPanelCheckBoxMenuItem;
    public JCheckBoxMenuItem showToolbarCheckBoxMenuItem;
    public JCheckBoxMenuItem showLargeTextFieldCheckBoxMenuItem;

    public ButtonGroup buttonGroup;
    public JRadioButtonMenuItem characterProportionalRadioButton;
    public JRadioButtonMenuItem timeProportionalRadioButton;

    /** Creates a new instance of ViewMenu
     * @param t */
    public ViewMenu(PartitureTableWithActions t) {

        super(t);
        
        this.setText("View");
        this.setMnemonic(java.awt.event.KeyEvent.VK_V);

        // view menu and submenus
        showKeyboardCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showKeyboardCheckBoxMenuItem.setSelected(true);
        showKeyboardCheckBoxMenuItem.setText(Internationalizer.getString("Keyboard"));
        showKeyboardCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showKeyboardCheckBoxMenuItemActionPerformed(evt);
            }
        });
        

        showLinkPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showLinkPanelCheckBoxMenuItem.setSelected(true);
        showLinkPanelCheckBoxMenuItem.setText(Internationalizer.getString("Link panel"));
        showLinkPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLinkPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        

        showMediaPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showMediaPanelCheckBoxMenuItem.setSelected(true);
        showMediaPanelCheckBoxMenuItem.setText(Internationalizer.getString("Audio/Video panel"));
        showMediaPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMediaPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        
        /*showQuickMediaOpenCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showQuickMediaOpenCheckBoxMenuItem.setSelected(false);
        showQuickMediaOpenCheckBoxMenuItem.setText(Internationalizer.getString("Quick media open panel"));
        showQuickMediaOpenCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showQuickMediaOpenCheckBoxMenuItemActionPerformed(evt);
            }
        });*/

        showPraatPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showPraatPanelCheckBoxMenuItem.setSelected(true);
        showPraatPanelCheckBoxMenuItem.setText(Internationalizer.getString("Praat panel"));
        showPraatPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPraatPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });

        showMultimodalPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showMultimodalPanelCheckBoxMenuItem.setSelected(true);
        showMultimodalPanelCheckBoxMenuItem.setText(Internationalizer.getString("Multimodal panel"));
        showMultimodalPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMultimodalPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });

        showSVGPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showSVGPanelCheckBoxMenuItem.setSelected(true);
        showSVGPanelCheckBoxMenuItem.setText(Internationalizer.getString("SVG panel"));
        showSVGPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSVGPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });
        
        showAnnotationPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showAnnotationPanelCheckBoxMenuItem.setSelected(true);
        showAnnotationPanelCheckBoxMenuItem.setText(Internationalizer.getString("Annotation panel"));
        showAnnotationPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAnnotationPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });

        showIPAPanelCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showIPAPanelCheckBoxMenuItem.setSelected(true);
        showIPAPanelCheckBoxMenuItem.setText(Internationalizer.getString("IPA panel"));
        showIPAPanelCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showIPAPanelCheckBoxMenuItemActionPerformed(evt);
            }
        });

        add(showMediaPanelCheckBoxMenuItem).setToolTipText("The panel for displaying video and further operations on media files");
        add(showPraatPanelCheckBoxMenuItem).setToolTipText("The panel for sending messages between the Partitur-Editor and Praat");
        add(showLinkPanelCheckBoxMenuItem).setToolTipText("The panel for displaying and operating on externally linked files");
        addSeparator();
        add(showAnnotationPanelCheckBoxMenuItem).setToolTipText("The panel to support annotation according to an annotation scheme");
        addSeparator();
        add(showKeyboardCheckBoxMenuItem).setToolTipText("Virtual keyboards with 'special' symbols");
        add(showIPAPanelCheckBoxMenuItem).setToolTipText("Virtual keyboard with symbols from the International Phonetic Alphabet");
        addSeparator();
        add(showMultimodalPanelCheckBoxMenuItem).setToolTipText("The panel with annotation symbols for non-verbal actions, facial expression etc.");
        add(showSVGPanelCheckBoxMenuItem).setToolTipText("The panel for working with external SVG files");
        //add(showQuickMediaOpenCheckBoxMenuItem);
        //add(showSegmentationPanelCheckBoxMenuItem);
        addSeparator();
        
        showToolbarCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showToolbarCheckBoxMenuItem.setSelected(true);
        showToolbarCheckBoxMenuItem.setText(Internationalizer.getString("Show toolbar"));
        showToolbarCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showToolbarCheckBoxMenuItemActionPerformed(evt);
            }
        });
        
        add(showToolbarCheckBoxMenuItem).setToolTipText("Show or hide the toolbar");
        
        showLargeTextFieldCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showLargeTextFieldCheckBoxMenuItem.setSelected(true);
        showLargeTextFieldCheckBoxMenuItem.setText(Internationalizer.getString("Show large text field"));
        showLargeTextFieldCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLargeTextFieldCheckBoxMenuItemActionPerformed(evt);
            }
        });
        add(showLargeTextFieldCheckBoxMenuItem).setToolTipText("Show or hide the text field in the upper part of the screen");

        addSeparator();


        showGridCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showGridCheckBoxMenuItem.setSelected(true);
        showGridCheckBoxMenuItem.setText(Internationalizer.getString("Show grid"));
        showGridCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showGridCheckBoxMenuItemActionPerformed(evt);
            }
        });
        
        this.add(showGridCheckBoxMenuItem).setToolTipText("Show or hide the grid lines in the partitur");

        showSpecialCharsCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        showSpecialCharsCheckBoxMenuItem.setSelected(false);
        showSpecialCharsCheckBoxMenuItem.setText(Internationalizer.getString("Show special characters"));
        showSpecialCharsCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSpecialCharsCheckBoxMenuItemActionPerformed(evt);
            }
        });
        showSpecialCharsCheckBoxMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        this.add(showSpecialCharsCheckBoxMenuItem).setToolTipText("Make whitespace visible in the event text");

        useDifferentEmptyColorCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        useDifferentEmptyColorCheckBoxMenuItem.setSelected(table.diffBgCol);
        useDifferentEmptyColorCheckBoxMenuItem.setText(Internationalizer.getString("Color empty events"));
        useDifferentEmptyColorCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useDifferentEmptyColorCheckBoxMenuItemActionPerformed(evt);
            }
        });
        this.add(useDifferentEmptyColorCheckBoxMenuItem).setToolTipText("Whether or not to use a different background color for empty events");

        addSeparator();

        add(table.changeScaleConstantAction).setToolTipText("Increase or decrease the displayed font size in the Partitur");

        addSeparator();

        buttonGroup = new ButtonGroup();
        
        characterProportionalRadioButton = new javax.swing.JRadioButtonMenuItem();
        characterProportionalRadioButton.setSelected(true);
        characterProportionalRadioButton.setText(Internationalizer.getString("Text proportional"));
        characterProportionalRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proportionChanged();
            }
        });
        buttonGroup.add(characterProportionalRadioButton);
        add(characterProportionalRadioButton).setToolTipText("Events are as large as the text in them");;
        
        timeProportionalRadioButton = new javax.swing.JRadioButtonMenuItem();
        timeProportionalRadioButton.setSelected(false);
        timeProportionalRadioButton.setText(Internationalizer.getString("Time proportional"));
        timeProportionalRadioButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proportionChanged();
            }
        });
        buttonGroup.add(timeProportionalRadioButton);
        add(timeProportionalRadioButton).setToolTipText("Event sizes are proportional to the length of the corresponding time interval");

        setProportionButtonsVisible(false);
    }
    
    private void proportionChanged(){
        boolean proportional = timeProportionalRadioButton.isSelected();
        table.getModel().setTimeProportional(proportional);
        PartiturEditor pe = (PartiturEditor)(table.getTopLevelAncestor());
        pe.setTimeViewBuffer(proportional);
        
    }

    private void showMediaPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        if (showMediaPanelCheckBoxMenuItem.isSelected()){
            table.mediaPanelDialog.show();
        } else {
            table.mediaPanelDialog.hide();
        }
    }
    
    /*private void showQuickMediaOpenCheckBoxMenuItemActionPerformed(ActionEvent evt) {
        table.quickMediaOpenDialog.setVisible(showQuickMediaOpenCheckBoxMenuItem.isSelected());
    }*/
    
    
    private void showKeyboardCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        if (showKeyboardCheckBoxMenuItem.isSelected()){
            table.keyboardDialog.show();
        } else {
            table.keyboardDialog.setVisible(false);
        }
    }
    
    private void showLinkPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        if (showLinkPanelCheckBoxMenuItem.isSelected()){
            table.linkPanelDialog.show();
        } else {
            table.linkPanelDialog.setVisible(false);
        }
    }
    
    private void showPraatPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        if (showPraatPanelCheckBoxMenuItem.isSelected()){
            table.praatPanel.setVisible(true);
        } else {
            table.praatPanel.setVisible(false);
        }
    }

    private void showAnnotationPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        table.annotationDialog.setVisible(showAnnotationPanelCheckBoxMenuItem.isSelected());
    }

    private void showIPAPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        table.ipaPanel.setVisible(showIPAPanelCheckBoxMenuItem.isSelected());
    }

    private void showMultimodalPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        table.multimodalDialog.setVisible(showMultimodalPanelCheckBoxMenuItem.isSelected());
    }

    private void showSVGPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        table.svgDialog.setVisible(showSVGPanelCheckBoxMenuItem.isSelected());
    }

    /*private void showSegmentationPanelCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        if (showSegmentationPanelCheckBoxMenuItem.isSelected()){
            table.segmentationPanel.show();
        } else {
            table.segmentationPanel.hide();
        }
    }*/

    private void showSpecialCharsCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        table.showSpecialCharacters();
    }
    
    private void useDifferentEmptyColorCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        table.useDifferentEmptyColor();
    }
    
    private void showGridCheckBoxMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
      if (showGridCheckBoxMenuItem.isSelected()){
          table.setCellBorderWidth(1);
          table.gridIsHidden = false;
      } else {
          table.setCellBorderWidth(0);
          table.gridIsHidden = true;
      }
    }

   private void showToolbarCheckBoxMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
      if (table.getTopLevelAncestor() instanceof PartiturEditor){
          PartiturEditor pe = (PartiturEditor)(table.getTopLevelAncestor());
          pe.toolBarPanel.setVisible(showToolbarCheckBoxMenuItem.isSelected());
      }
   }
    
   private void showLargeTextFieldCheckBoxMenuItemActionPerformed (java.awt.event.ActionEvent evt) {
      if (table.getTopLevelAncestor() instanceof PartiturEditor){
          PartiturEditor pe = (PartiturEditor)(table.getTopLevelAncestor());
          pe.largeTextFieldPanel.setVisible(showLargeTextFieldCheckBoxMenuItem.isSelected());
      }
   }

    public void setProportionButtonsVisible(boolean b) {
        characterProportionalRadioButton.setVisible(b);
        timeProportionalRadioButton.setVisible(b);
    }

}
