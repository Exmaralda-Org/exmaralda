/*
 * Created on 03.11.2004 by woerner
 */
package org.exmaralda.coma.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.exmaralda.coma.actions.SaveBasketAction;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ElementListRenderer;
import org.exmaralda.coma.root.IconFactory;
import org.exmaralda.coma.root.Ui;

/**
 * coma2/org.sfb538.coma2.panels/BasketPanel.java
 * 
 * @author woerner
 * 
 */
public class BasketPanel extends JPanel implements ActionListener {

    private JPanel eastPanel = null;

    private JPanel northPanel = null;

    private JPanel southPanel = null;

    private JScrollPane listScrollPane = null;

    private JList trList = null;

    private JLabel nrOfTranscriptionsLabel = null;

    private JButton saveAsButton = null;

    private JButton emptyBasketButton = null;

    private Coma coma;

    private JButton removeTranscriptionButton;

    private DefaultListModel trListModel;

    /**
     * This method initializes
     * 
     */
    public BasketPanel(Coma c) {
	super();
	coma = c;
	initialize();
    }

    /**
     * This method initializes this
     * 
     */
    private void initialize() {
	trListModel = new DefaultListModel();
	trList = new JList();
	trList.setModel(trListModel);

	trList.setCellRenderer(new ElementListRenderer());
	trList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

	this.setLayout(new BorderLayout());
	this.setSize(new java.awt.Dimension(654, 308));
	this.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		"Korb", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null));
	this.add(getEastPanel(), java.awt.BorderLayout.EAST);
	this.add(getNorthPanel(), java.awt.BorderLayout.NORTH);
	this.add(getSouthPanel(), java.awt.BorderLayout.SOUTH);
	this.add(getListScrollPane(), java.awt.BorderLayout.CENTER);

    }

    public void actionPerformed(ActionEvent e) {
	// TODO Auto-generated method stub

    }

    /**
     * This method initializes eastPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getEastPanel() {
	if (eastPanel == null) {
	    eastPanel = new JPanel();
	}
	return eastPanel;
    }

    /**
     * This method initializes northPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getNorthPanel() {
	if (northPanel == null) {
	    nrOfTranscriptionsLabel = new JLabel();
	    nrOfTranscriptionsLabel.setText(Ui.getText("basketEmpty"));
	    nrOfTranscriptionsLabel
		    .setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
	    nrOfTranscriptionsLabel.setFont(new java.awt.Font("Arial",
		    java.awt.Font.BOLD, 24));
	    northPanel = new JPanel();
	    northPanel.setLayout(new BorderLayout());
	    northPanel
		    .setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
	    northPanel.setBorder(javax.swing.BorderFactory.createLineBorder(
		    java.awt.Color.black, 1));
	    northPanel.add(nrOfTranscriptionsLabel, java.awt.BorderLayout.WEST);
	    /**
	     * northPanel .add(getEmptyBasketButton(),
	     * java.awt.BorderLayout.CENTER);
	     * northPanel.add(getRemoveTranscriptionButton(),
	     * BorderLayout.EAST);
	     */
	}
	return northPanel;
    }

    /**
     * This method initializes southPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSouthPanel() {
	if (southPanel == null) {
	    southPanel = new JPanel();
	    southPanel.setLayout(new FlowLayout());
	    southPanel.setBorder(javax.swing.BorderFactory.createLineBorder(
		    java.awt.Color.black, 1));
	    southPanel.add(getRemoveTranscriptionButton(), null);
	    southPanel.add(getEmptyBasketButton(), null);
	    southPanel.add(getSaveAsButton(), null);
	}
	return southPanel;
    }

    /**
     * This method initializes listScorllPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getListScrollPane() {
	if (listScrollPane == null) {
	    listScrollPane = new JScrollPane();
	    listScrollPane.setViewportView(trList);
	}
	return listScrollPane;
    }

    /**
     * This method initializes jList
     * 
     * @return javax.swing.JList
     */
    private JList getJList() {
	if (trList == null) {
	}
	return trList;
    }

    /**
     * This method initializes saveAsButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSaveAsButton() {
	if (saveAsButton == null) {
	    saveAsButton = new JButton(new SaveBasketAction(coma,IconFactory.createImageIcon("document-save-as.png")));
	    saveAsButton.putClientProperty("JButton.buttonType", "segmented");
	    saveAsButton.putClientProperty("JButton.segmentPosition", "last");
	}
	return saveAsButton;
    }

    /**
     * This method initializes emptyBasketButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getEmptyBasketButton() {
	if (emptyBasketButton == null) {
	    emptyBasketButton = new JButton(IconFactory
		    .createImageIcon("edit-clear.png"));
	    emptyBasketButton.putClientProperty("JButton.buttonType",
		    "segmented");
	    emptyBasketButton.putClientProperty("JButton.segmentPosition",
		    "middle");
	    emptyBasketButton
		    .setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
	    emptyBasketButton.setText(Ui.getText("cmd.emptyBasket"));
	    emptyBasketButton
		    .addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    emptyBasket();
			}
		    });
	}
	return emptyBasketButton;
    }

    private JButton getRemoveTranscriptionButton() {
	if (removeTranscriptionButton == null) {
	    removeTranscriptionButton = new JButton(IconFactory
		    .createImageIcon("list-remove.png"));

	    removeTranscriptionButton
		    .setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
	    removeTranscriptionButton.setText(Ui
		    .getText("cmd.removeTranscriptionFromBasket"));
	    removeTranscriptionButton
		    .addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
			    removeSelected();
			}
		    });
	    removeTranscriptionButton.putClientProperty("JButton.buttonType",
		    "segmented");
	    removeTranscriptionButton.putClientProperty(
		    "JButton.segmentPosition", "first");
	}
	return removeTranscriptionButton;
    }

    protected void removeSelected() {
	for (int i : trList.getSelectedIndices()) {
	    coma.getData().getBasket().remove(trList.getModel().getElementAt(i));
	}
	updateDisplay();
    }

    protected void emptyBasket() {
	coma.getData().clearBasket();
	updateDisplay();
	coma.basketUpdated();

    }

    public void updateDisplay() {
	int basketSize = coma.getData().getBasket().size();
	if (basketSize > 0) {
	    nrOfTranscriptionsLabel.setText(Ui.getText("basketContains") + " "
		    + coma.getData().getBasket().size() + " "
		    + ((basketSize == 1) ? Ui.getText("transcription") :

		    Ui.getText("transcriptions")) + ".");
	    saveAsButton.setEnabled(true);
	    emptyBasketButton.setEnabled(true);
	    trListModel.clear();
	    for (Object e : coma.getData().getBasket()) {
		trListModel.addElement(e);
	    }
	} else {
	    nrOfTranscriptionsLabel.setText(Ui.getText("basketEmpty"));
	    trListModel.clear();
	    saveAsButton.setEnabled(false);
	    emptyBasketButton.setEnabled(false);
	}

    }

    /**
	 * 
	 */

} // @jve:decl-index=0:visual-constraint="10,10"