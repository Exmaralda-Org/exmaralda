/*
 * Created on 06.02.2004 by woerner... nope.
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.BasicTranscription2COMA;
import org.exmaralda.partitureditor.jexmaraldaswing.fileFilters.ParameterFileFilter;
import org.jdom.Document;

public class ImportBasicTranscriptionAction extends ComaAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8591680248178770537L;

	public ImportBasicTranscriptionAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.ImportBasicTranscription"), icon, c);
	}

	public ImportBasicTranscriptionAction(Coma coma) {
		this(coma, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (coma.getData().getOpenFile() != null) {
			JFileChooser jfc = new JFileChooser();
			String[] exts = { "exb", "xml" };
			jfc.setFileFilter(new ParameterFileFilter(exts,
					"EXMARaLDA Basic Transcription"));
			jfc.setDialogTitle(Ui.getText("cmd.ImportBasicTranscription"));
			jfc.setCurrentDirectory(coma.getData().getOpenFile());
			int retValue = jfc.showOpenDialog(coma.dataPanel);
			if (retValue == JFileChooser.APPROVE_OPTION) {
				Document comaDocument = coma.getData().getDocument();
				try {
					Object[] result = BasicTranscription2COMA
							.importBasicTranscription(jfc.getSelectedFile(),
									comaDocument,
                                                                        coma.getData().getOpenFile());
					int yes = ((Integer) result[1]).intValue();
					int no = ((Integer) result[1]).intValue();
					String text = Ui.getText("msg.transcriptionImported")+": "
							+ (String) (result[0])
							+ "\n"
							+ (yes > 0 ? (yes + " "
									+ Ui.getText("msg.speakersImported") + "\n")
									: (""))
							+ (no > 0 ? no + " "
									+ Ui.getText("msg.speakersIgnored") : "");
					JOptionPane.showMessageDialog(coma, text);
					coma.dataPanel.updateLists();
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(coma, ex
							.getLocalizedMessage());
				}
			}

		} else {
			JOptionPane.showMessageDialog(coma, Ui
					.getText("err.noCorpusLoaded"));
		}
	}
}
