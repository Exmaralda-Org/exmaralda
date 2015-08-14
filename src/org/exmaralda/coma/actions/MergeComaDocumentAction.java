/*
 * Created on 06.02.2004 by woerner... nope.
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.BasicTranscription2COMA;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.Document;
import org.jdom.JDOMException;

public class MergeComaDocumentAction extends ComaAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8591680248178770537L;

	public MergeComaDocumentAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.MergeComaDocument"), icon, c);
	}

	public MergeComaDocumentAction(Coma coma) {
		this(coma, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (coma.getData().getOpenFile() != null) {
			JFileChooser jfc = new JFileChooser();
			String[] exts = { "coma", "xml" };
			jfc.setFileFilter(new ParameterFileFilter(exts,
					"COMA files (*.coma, *.xml)"));
			jfc.setDialogTitle(Ui.getText("cmd.MergeComaDocument"));
			jfc.setCurrentDirectory(coma.getData().getOpenFile());
			int retValue = jfc.showOpenDialog(coma.dataPanel);
			if (retValue == JFileChooser.APPROVE_OPTION) {
                            try {
                                Document comaDocument = coma.getData().getDocument();
                                Document mergeDocument = FileIO.readDocumentFromLocalFile(jfc.getSelectedFile());
                                int countCom = mergeDocument.getRootElement().getChild("CorpusData").getChildren("Communication").size();
                                int countSpk = mergeDocument.getRootElement().getChild("CorpusData").getChildren("Speaker").size();
                                List l = mergeDocument.getRootElement().getChild("CorpusData").removeContent();
                                comaDocument.getRootElement().getChild("CorpusData").addContent(l);
                                String text = countCom + " communications and\n" + countSpk + " speakers merged into the current document.";                                
                                JOptionPane.showMessageDialog(coma, text);
                                coma.dataPanel.updateLists();                                
                            } catch (JDOMException ex) {
                                ex.printStackTrace();
        			JOptionPane.showMessageDialog(coma, ex.getLocalizedMessage());
                            } catch (IOException ex) {
                                ex.printStackTrace();
        			JOptionPane.showMessageDialog(coma, ex.getLocalizedMessage());
                            }
			}

		} else {
			JOptionPane.showMessageDialog(coma, Ui.getText("err.noCorpusLoaded"));
		}
	}
}
