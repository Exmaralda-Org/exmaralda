/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import org.exmaralda.coma.dialogs.TranscriptionSearchAndReplaceDialog;
import org.exmaralda.coma.importer.ExmaraldaPartitur;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 * coma2/org.sfb538.coma2.fileActions/newAction.java
 * 
 * @author woerner
 * 
 */
public class TranscriptionSearchAndReplaceAction extends ComaAction
		implements
			PropertyChangeListener {
	private ProgressMonitor progressMonitor;
	private List<Element> transcriptions;
	private SearchAndReplaceTask task;
	private String searchString;
	private String replaceString;
	private HashMap<String, String> result = new HashMap<String, String>();
	private String resultString = "";
	private boolean doBackups;
	private String xpath;

	public TranscriptionSearchAndReplaceAction(Coma c,
			javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.transcriptionSearchAndReplace"), icon, c);
	}

	public TranscriptionSearchAndReplaceAction(Coma c) {
		this(c, null);
	}

	public void actionPerformed(ActionEvent actionEvent) {
		if (data.getOpenFile().exists()) {
			Object[] options = {Ui.getText("OK"), Ui.getText("cancel")};
			int n = JOptionPane.showOptionDialog(coma,
					Ui.getText("msg.changesTranscriptionsWarning"),
					Ui.getText("warning"), JOptionPane.DEFAULT_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (n == JOptionPane.OK_OPTION) {

				TranscriptionSearchAndReplaceDialog t = new TranscriptionSearchAndReplaceDialog(
						coma);
				if (t.getResult() == JOptionPane.OK_OPTION) {
					doBackups = t.getCreateBackupFlag();
					searchString = t.getSearchString();
					replaceString = t.getReplaceString();
					xpath = t.getXPath();
					transcriptions = null;
					XPath trx;
					try {
						trx = XPath.newInstance("//Transcription");
						transcriptions = trx.selectNodes(data.getDataElement());
					} catch (JDOMException err) {
						err.printStackTrace();
					}
					progressMonitor = new ProgressMonitor(coma,
							Ui.getText("replacing"), "", 0, 100);
					progressMonitor.setProgress(0);
					task = new SearchAndReplaceTask();
					task.addPropertyChangeListener(this);
					task.execute();
				}
			} else {
				return;
			}
		} else {
			JOptionPane.showMessageDialog(coma,
					Ui.getText("err.noCorpusLoaded"));
		}
	}

	class SearchAndReplaceTask extends SwingWorker<Void, Void> {

		@Override
		public void done() {
			Toolkit.getDefaultToolkit().beep();
			progressMonitor.setProgress(0);
			progressMonitor.close();
			DefaultTableModel dtm = new DefaultTableModel();
			dtm.setColumnCount(2);
			dtm.setColumnIdentifiers(new String[]{"Transcription", ""});
			JTable t = new JTable(dtm);
			for (String k : result.keySet()) {
				dtm.addRow(new String[]{k, result.get(k)});
			}
			JScrollPane sp = new JScrollPane(t);
			JPanel panel = new JPanel(new BorderLayout());
			panel.setPreferredSize(new Dimension(400, 300));
			panel.add(sp, BorderLayout.CENTER);
			JOptionPane.showMessageDialog(coma, panel);

		}

		@Override
		public Void doInBackground() {
			int trcount = 0;
			int progress = 0;
			setProgress(0);
			Date date = new Date();
			SimpleDateFormat simpDate = new SimpleDateFormat("kk:mm:ss");
			String backupDate = simpDate.format(date);

			for (Element element : transcriptions) {
				if (isCancelled()) {
					break;
				}
				progress = Math.round((100 / (float) transcriptions.size())
						* trcount);
				trcount++;
				if (progress == 100) {
					progress = 99;
				}
				setProgress(progress);
				if (element.getChild("Filename") != null) {
					ExmaraldaPartitur myPartiture = null;
					int count = 1;
					File partitureFile = new File(coma.absolutized(element
							.getChild("NSLink").getText()));
					myPartiture = new ExmaraldaPartitur(partitureFile,
							coma.prefs.getBoolean(
									"prefs.writeCIDsWhenUpdating", true));
					while (!myPartiture.isValid() && count < 10) {
						// System.out.print(count + ",");
						myPartiture = new ExmaraldaPartitur(new File(
								coma.absolutized(element.getChild("NSLink")
										.getText())), coma.prefs.getBoolean(
								"prefs.writeCIDsWhenUpdating", true));
						count++;
					}
					if (!myPartiture.isSegmented()) {
						if (doBackups) {
							partitureFile
									.renameTo(new File(partitureFile
											.getAbsolutePath()
											+ ".backup"
											+ backupDate));
						}
						int changed = 0;
						int changedTranscriptions = 0;

						//JLabel j = new JLabel("asdf");
						XPath evx;
						try {
							evx = XPath.newInstance(xpath);
							List<Element> parts = evx.selectNodes(myPartiture
									.getDocument().getRootElement());
							for (Element e : parts) {
								String r = e.getText().replaceAll(searchString,
										replaceString);
								if (!r.equals(e.getText())) {
									changed++;
									System.out.println(e.getText() + " > " + r);
									e.setText(r);
								}

							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (changed > 0) {
							myPartiture.rewrite();
							result.put(myPartiture.getTranscriptionName(), ""
									+ changed);
						}

					}
				}
			}
			coma.xmlChanged();
			return null;
		}
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			System.out.println(progress);
			progressMonitor.setProgress(progress);
			String message = progress + "%";
			progressMonitor.setNote(message);
			if (progressMonitor.isCanceled() || task.isDone()) {
				Toolkit.getDefaultToolkit().beep();
				if (progressMonitor.isCanceled()) {
					task.cancel(true);
					System.out.println("task cancelled");
				} else {
					System.out.println("task completed");

				}
			}
		}
	}
}
