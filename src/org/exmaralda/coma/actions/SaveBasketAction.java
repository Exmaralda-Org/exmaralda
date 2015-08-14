/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import org.exmaralda.coma.dialogs.OneDirFileSelector;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.coma.root.Ui;
import org.jdom.Document;

/**
 * coma2/org.sfb538.coma2.fileActions/SaveAction.java action for save- and
 * save-as commands
 * 
 * @author woerner
 * 
 */
public class SaveBasketAction extends ComaAction {
	private static final long serialVersionUID = 4601685287146653101L;

	boolean saveAs = false;
	Coma coma;

	private OneDirFileSelector fs = null;

	public SaveBasketAction(Coma c, javax.swing.ImageIcon icon) {

		super(Ui.getText("cmd.SaveBasketAction"), icon, c);
		coma = c;
	}

	public SaveBasketAction(Coma c) {
		this(c, null);
		coma = c;
	}

	public SaveBasketAction(Coma c, boolean sa) {
		this(c, null);
		coma = c;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (!coma.getData().getOpenFile().exists()) {
			JOptionPane.showMessageDialog(coma, Ui
					.getText("err.noCorpusLoaded"));
			return;
		}
		if (coma.getData().getBasket().size()<1) {
			JOptionPane.showMessageDialog(coma, Ui
					.getText("err.basketEmpty"));
			return;
		}
		DateFormat dateFormat = new SimpleDateFormat("ddMMHHmmss");
		Date date = new Date();
		File basketFile = new File(
				coma.getData().getOpenFile().getPath()
						.substring(
								0,
								coma.getData().getOpenFile().getPath()
										.lastIndexOf("."))
						+ "_basket_" + dateFormat.format(date) + ".coma");
		File corpDir = coma.getData().getOpenFile().getParentFile();
		fs  = new OneDirFileSelector(coma, Ui
				.getText("cmd.SaveBasketAction"), corpDir, basketFile, ".coma");
		if (fs.showOneDirFileSelector()) {
			File tf = fs.getSelectedFile();
			System.out.println(tf);
			System.out.println(coma.getData().getOpenFile());
			if (tf.getAbsolutePath().equals(coma.getData().getOpenFile().getAbsolutePath())) {
				JOptionPane.showMessageDialog(coma, Ui
						.getText("err.dontOverwriteCorpus"));

				return;
			} else {

				try {
					basketFile = tf;
					basketFile.createNewFile();
					if (basketFile.canWrite()) {
						saveAsCorpus(basketFile);
					} else {
						JOptionPane.showMessageDialog(coma, Ui
								.getText("err.cannotWrite"));

					}
				} catch (IOException e) {
					System.out.println("cannot write!");
				}
			}
		} else {
			System.out.println("unclear!");
		}
	}

	private void saveAsCorpus(File saveFile) {
		Document saveDoc = coma.buildCorpusFromBasket();
		System.out.println("save document as");
		ComaXMLOutputter outputter = new ComaXMLOutputter();
		try {
			FileOutputStream out = new FileOutputStream(saveFile);
			outputter.output(saveDoc, out);
			out.close();
			JOptionPane.showMessageDialog(coma, Ui.getText("savedAs")
					+ saveFile.getPath());
			// , "Saved the basket as "+saveFile.getPath());
		} catch (IOException e) {
			coma.status("saving failed: " + e);
		}
	}
}
