/**
 *
 */
package org.exmaralda.teide.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Vector;
import java.util.prefs.Preferences;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.SoftBevelBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import org.exmaralda.teide.HTML.TeideHTML;
import org.exmaralda.teide.file.TeideFileWriter;
import org.exmaralda.teide.models.FileListTableModel;
import org.exmaralda.teide.models.StylesheetListTableModel;
import org.exmaralda.teide.xml.StylesheetFactory;

/**
 * @author woerner
 *
 */
public class PublishWizard extends JFrame implements PropertyChangeListener {
	public static Preferences prefs = Preferences.userRoot().node(
			"org.exmaralda.teide");

	private ProgressMonitor progressMonitor;

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel cardPanel = null;

	private JPanel taskPanel = null;

	private JPanel files = null;

	private JPanel stylesheets = null;

	private JPanel target = null;

	private JPanel publish = null;

	private JButton filesButton = null;

	private JButton stylesheetsButton = null;

	private JButton targetButton = null;

	private JButton publishCardButton = null;

	private JLabel filesLabel = null;

	private JLabel stylesheetsLabel = null;

	private JLabel displayLabel = null;

	private JLabel targetLabel = null;

	private JLabel summaryLabel = null;

	private JTable stylesheetsTable = null;

	private JButton cancelButton = null;

	private JButton publishButton = null;

	private JEditorPane summaryEditorPane = null;

	private JButton savePublishSettingsButton = null;

	private JTable filesTable = null;

	private JButton selectDisplayButton = null;

	private JButton resetFilesButton = null;

	private JLabel displayFileLabel = null;

	private JButton selectTargetDirButton = null;

	private JLabel targetDirLabel = null;

	// -my vars -----------------------------------------------------------------------------------------------------------------------

	private File rootDir;

	private File displayStylesheet;

	private File targetDir;

	private HashMap<File, Boolean> inputFiles = new HashMap<File, Boolean>();

	private TEIDEFileFilter tff;

	private DetermineFilesTask task;

	private PublishCorpusTask pTask;

	private DefaultTableModel filesTableModel;

	private Vector<File> stylesheetVector;

	private StylesheetListTableModel stylesheetsTableModel;

	private JLabel publishLabel;

	private JPanel displaySSPanel = null;

	private JPanel display;

	/**
	 * This is the default constructor
	 */
	/*	public PublishWizard() {
			super();
			initialize();
		}
		*/

	public PublishWizard(TEIDEFileFilter filter, File root, Vector<File> ssv) {
		super();
		rootDir = root;
		tff = filter;
		stylesheetVector = ssv;
		initialize();
	}

	/**
	 * This method initializes this
	 *
	 * @return void
	 */
	private void initialize() {
		task = new DetermineFilesTask();
		progressMonitor = new ProgressMonitor(PublishWizard.this,
				"Selecting Files", "", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToDecideToPopup(0);
		task.addPropertyChangeListener(this);
		task.execute();

		this.setSize(828, 346);
		this.setContentPane(getJContentPane());
		this.setTitle("publish corpus");

	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			System.out.println(evt.getSource());
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			String message = String.format("Completed %d%%.\n", progress);
			progressMonitor.setNote(message);
			if (evt.getSource() == DetermineFilesTask.class) {
				if (progressMonitor.isCanceled() || task.isDone()) {
					System.out
							.println(progressMonitor.isCanceled() ? "cancelled"
									: "");
					System.out.println(task.isDone() ? "task done" : "");
					Toolkit.getDefaultToolkit().beep();
					if (progressMonitor.isCanceled()) {
						task.cancel(true);
						System.out.println("KÄNZEL!");
					} else {
					}
				}
			} else if (evt.getSource() == PublishCorpusTask.class) {

				if (progressMonitor.isCanceled() || pTask.isDone()) {
					System.out
							.println(progressMonitor.isCanceled() ? "cancelled"
									: "");
					System.out.println(task.isDone() ? "task done" : "");
					Toolkit.getDefaultToolkit().beep();
					if (progressMonitor.isCanceled()) {
						pTask.cancel(true);
						System.out.println("KÄNZEL!");
					} else {
					}
				}
			}
		}

	}

	/**
	 * This method initializes jContentPane
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getCardPanel(), BorderLayout.CENTER);
			jContentPane.add(getTaskPanel(), BorderLayout.WEST);
		}
		return jContentPane;
	}

	/**
	 * This method initializes cardPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getCardPanel() {
		if (cardPanel == null) {
			cardPanel = new JPanel();
			cardPanel.setLayout(new CardLayout());
			cardPanel.add(getFiles(), getFiles().getName());
			cardPanel.add(getStylesheets(), getStylesheets().getName());
			cardPanel.add(getTarget(), getTarget().getName());
			cardPanel.add(getSummary(), getSummary().getName());
		}
		return cardPanel;
	}

	/**
	 * This method initializes taskPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTaskPanel() {
		if (taskPanel == null) {
			taskPanel = new JPanel();
			taskPanel
					.setLayout(new BoxLayout(getTaskPanel(), BoxLayout.Y_AXIS));
			taskPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
			taskPanel.add(getFilesButton(), null);
			taskPanel.add(getStylesheetsButton(), null);
			taskPanel.add(getTargetButton(), null);
			taskPanel.add(getSummaryButton(), null);
			taskPanel.add(Box.createVerticalGlue());
			taskPanel.add(getCancelButton(), null);
		}
		return taskPanel;
	}

	/**
	 * This method initializes files
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getFiles() {
		if (files == null) {
			filesLabel = new JLabel();
			filesLabel.setText("select files to include");
			filesLabel.setAlignmentX(0.5F);
			files = new JPanel();
			files.setLayout(new BorderLayout());
			files.setName("files");
			files.add(filesLabel, BorderLayout.NORTH);
			files.add(new JScrollPane(getFilesTable()), BorderLayout.CENTER);
			files.add(getResetFilesButton(), BorderLayout.SOUTH);
		}
		return files;
	}

	/**
	 * This method initializes stylesheets
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getStylesheets() {
		if (stylesheets == null) {
			stylesheetsLabel = new JLabel();
			stylesheetsLabel.setText("select stylesheets to apply");
			stylesheets = new JPanel();
			stylesheets.setLayout(new BorderLayout());
			stylesheets.setName("stylesheets");
			stylesheets.add(stylesheetsLabel, BorderLayout.NORTH);

			stylesheets.add(getDisplaySSPanel(), BorderLayout.SOUTH);
			stylesheets.add(new JScrollPane(getStylesheetsTable()),
					BorderLayout.CENTER);
		}
		return stylesheets;
	}

	/**
	 * This method initializes display
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDisplay() {
		if (display == null) {
			display = new JPanel();
			displayFileLabel = new JLabel();
			displayFileLabel.setBackground(Color.white);
			displayFileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			displayFileLabel.setText("no description-stylesheet selected");
			displayLabel = new JLabel();
			displayLabel.setText("description-stylesheet:");
			//		display.add(getDisplayInnerPanel(), BorderLayout.CENTER);
		}
		return display;
	}

	/**
	 * This method initializes target
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getTarget() {
		if (target == null) {
			targetDirLabel = new JLabel();
			targetDirLabel.setBounds(new Rectangle(14, 98, 379, 16));
			targetDirLabel.setHorizontalAlignment(SwingConstants.RIGHT);
			targetDirLabel.setText("no target directory selected");
			targetLabel = new JLabel();
			targetLabel.setText("select target directory");
			targetLabel.setBounds(new Rectangle(0, 0, 493, 16));
			target = new JPanel();
			target.setLayout(null);
			target.setName("target");
			target.add(targetLabel, null);
			target.add(getSelectTargetDirButton(), null);
			target.add(targetDirLabel, null);
		}
		return target;
	}

	/**
	 * This method initializes summary
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getSummary() {
		if (publish == null) {
			publishLabel = new JLabel();
			publishLabel.setText("publish");
			publish = new JPanel();
			publish.setLayout(new BorderLayout());
			publish.setName("publish");
			publish.add(publishLabel, BorderLayout.NORTH);
			publish.add(getPublishButton(), BorderLayout.SOUTH);
			publish.add(getSummaryEditorPane(), BorderLayout.CENTER);
			publish.add(getSavePublishSettingsButton(), BorderLayout.EAST);
		}
		return publish;
	}

	/**
	 * This method initializes filesButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getFilesButton() {
		if (filesButton == null) {
			filesButton = new JButton();
			filesButton.putClientProperty("JButton.buttonType", "textured");
			filesButton.setText("files");
			filesButton.setMaximumSize(new Dimension(120, 29));
			filesButton.setBackground(new Color(204, 255, 204));
			filesButton.setMinimumSize(new Dimension(120, 29));
			filesButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					((CardLayout) cardPanel.getLayout()).show(cardPanel,
							"files");
				}
			});
		}
		return filesButton;
	}

	/**
	 * This method initializes stylesheetsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getStylesheetsButton() {
		if (stylesheetsButton == null) {
			stylesheetsButton = new JButton();
			stylesheetsButton.putClientProperty("JButton.buttonType",
					"textured");

			stylesheetsButton.setText("stylesheets");
			stylesheetsButton.setMinimumSize(new Dimension(120, 29));
			stylesheetsButton.setMaximumSize(new Dimension(120, 29));
			stylesheetsButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							((CardLayout) cardPanel.getLayout()).show(
									cardPanel, "stylesheets");
						}
					});
		}
		return stylesheetsButton;
	}

	/**
	 * This method initializes targetButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getTargetButton() {
		if (targetButton == null) {
			targetButton = new JButton();
			targetButton.putClientProperty("JButton.buttonType", "textured");

			targetButton.setText("target");
			targetButton.setMinimumSize(new Dimension(120, 29));
			targetButton
					.setIcon(new ImageIcon(
							getClass()
									.getResource(
											"/org/exmaralda/teide/resources/icons/dialog-error-16x16-status.png")));
			targetButton.setMaximumSize(new Dimension(120, 29));
			targetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					((CardLayout) cardPanel.getLayout()).show(cardPanel,
							"target");
				}
			});
		}
		return targetButton;
	}

	/**
	 * This method initializes summaryButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSummaryButton() {
		if (publishCardButton == null) {
			publishCardButton = new JButton();
			publishCardButton.putClientProperty("JButton.buttonType",
					"textured");

			publishCardButton.setText("publish");
			publishCardButton.setMinimumSize(new Dimension(120, 29));
			publishCardButton.setMaximumSize(new Dimension(120, 29));
			publishCardButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							((CardLayout) cardPanel.getLayout()).show(
									cardPanel, "publish");
						}
					});
		}
		publishCardButton.setEnabled(false);
		return publishCardButton;

	}

	/**
	 * This method initializes stylesheetsTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getStylesheetsTable() {
		if (stylesheetsTable == null) {
			stylesheetsTableModel = new StylesheetListTableModel();
			stylesheetsTable = new JTable(stylesheetsTableModel);
			stylesheetsTable.setGridColor(Color.GRAY);
		}
		return stylesheetsTable;
	}

	/**
	 * This method initializes cancelButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton();
			cancelButton.putClientProperty("JButton.buttonType", "bevel");

			cancelButton.setText("cancel");
			cancelButton.setMinimumSize(new Dimension(120, 29));
			cancelButton.setMaximumSize(new Dimension(120, 29));
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancel();
				}
			});

		}
		return cancelButton;
	}

	/**
	 *
	 */
	protected void cancel() {
		this.dispose();
	}

	/**
	 * This method initializes publishButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getPublishButton() {
		if (publishButton == null) {
			publishButton = new JButton();
			publishButton.setText("publish corpus");
			publishButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					publish();
				}

			});
		}
		return publishButton;
	}

	/**
	 * This method initializes summaryEditorPane
	 *
	 * @return javax.swing.JEditorPane
	 */
	private JEditorPane getSummaryEditorPane() {
		if (summaryEditorPane == null) {
			summaryEditorPane = new JEditorPane();
		}
		return summaryEditorPane;
	}

	/**
	 * This method initializes savePublishSettingsButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSavePublishSettingsButton() {
		if (savePublishSettingsButton == null) {
			savePublishSettingsButton = new JButton();
			savePublishSettingsButton.setText("save settings");
		}
		return savePublishSettingsButton;
	}

	/**
	 * This method initializes filesTable
	 *
	 * @return javax.swing.JTable
	 */
	private JTable getFilesTable() {
		if (filesTable == null) {
			filesTableModel = new FileListTableModel();
			filesTable = new JTable(filesTableModel);
			filesTable.setGridColor(Color.GRAY);
		}
		return filesTable;
	}

	/**
	 * This method initializes selectDisplayButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSelectDisplayButton() {
		if (selectDisplayButton == null) {
			selectDisplayButton = new JButton();
			selectDisplayButton.setText("...");
			selectDisplayButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(prefs.get(
							"teiWalker.RecentStylesheetDir", "/"), null);
					//		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc.setDialogTitle(Loc
							.getText("message.selectMiniStylesheet"));
					int dialogStatus = fc.showOpenDialog(PublishWizard.this);

					if (dialogStatus == 0) {
						displayStylesheet = fc.getSelectedFile();
						displayFileLabel.setText(displayStylesheet.getName());
					} else {
						displayStylesheet = null;
					}
				}
			});
		}

		return selectDisplayButton;
	}

	/**
	 * This method initializes resetFilesButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getResetFilesButton() {
		if (resetFilesButton == null) {
			resetFilesButton = new JButton();
			resetFilesButton.setText("reset list");
			resetFilesButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fillInputTable();
				}
			});
		}
		return resetFilesButton;
	}

	/**
	 * This method initializes selectTargetDirButton
	 *
	 * @return javax.swing.JButton
	 */
	private JButton getSelectTargetDirButton() {
		if (selectTargetDirButton == null) {
			selectTargetDirButton = new JButton();
			selectTargetDirButton.setBounds(new Rectangle(405, 91, 75, 29));
			selectTargetDirButton.setText("...");
			selectTargetDirButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					JFileChooser fc = new JFileChooser(new File(FileSystemView
							.getFileSystemView().getHomeDirectory().getPath()),
							null);
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int dialogStatus = fc.showSaveDialog(PublishWizard.this);
					if (dialogStatus == 0) {
						targetDir = fc.getSelectedFile();
						publishCardButton.setEnabled(true);
						targetButton.setIcon(null);
						targetDirLabel.setText(targetDir.getAbsolutePath());
					}
				}
			});
		}
		return selectTargetDirButton;
	}

	public URI getRelativePath(File from, File to) {

		URI fromURI = from.toURI();
		URI toURI = to.toURI();
		URI relativeURI = fromURI.relativize(toURI);
		return relativeURI;

	}

	class DetermineFilesTask extends SwingWorker<Void, Void> {
		float fileCount = 0;

		float count = 0;

		@Override
		public Void doInBackground() {
			determineFileCount(rootDir);
			int progress = 0;
			setProgress(0);
			createFileList(rootDir);
			setProgress(100);
			return null;
		}

		/**
		 * @param rootDir
		 */
		private void determineFileCount(File f) {
			fileCount++;
			if (f.isDirectory()) {
				for (File ff : f.listFiles()) {
					determineFileCount(ff);
				}
			}
		}

		@Override
		public void done() {
			progressMonitor.setProgress(0);
			progressMonitor.close();
			fillInputTable();
		}

		private void createFileList(File f) {
			count++;
			int pro = Math.round((100 / fileCount) * count);
			pro = (pro == 100) ? 99 : pro;
			setProgress(Math.round(pro));
			if (f.isDirectory()) {
				inputFiles.put(f, false);
				for (File ff : f.listFiles()) {
					createFileList(ff);
				}
			} else {
				if (tff.accept(f)) {
					inputFiles.put(f, true);
				} else {
					inputFiles.put(f, false);
				}
			}
		}
	}

	/**
	 *
	 */
	public void fillInputTable() {
		Vector<Vector<Object>> aussen = new Vector();
		Vector<String> header = new Vector<String>();
		header.add("Include");
		header.add("File");
		for (File f : inputFiles.keySet()) {
			Vector innen = new Vector<Object>();
			innen.add(inputFiles.get(f));
			innen.add(f);
			aussen.add(innen);
		}
		filesTableModel.setDataVector(aussen, header);
		filesTableModel.fireTableDataChanged();

		// Stylesheets
		Vector<Vector<Object>> aussenS = new Vector<Vector<Object>>();
		Vector<String> headerS = new Vector<String>();
		headerS.add("Include");
		headerS.add("Stylesheet");
		headerS.add("Name");
		for (File f : stylesheetVector) {
			Vector innen = new Vector<Object>();
			innen.add(new Boolean(true));
			innen.add(f);
			innen.add(f.getName());
			aussenS.add(innen);
		}
		stylesheetsTableModel.setDataVector(aussenS, headerS);
		stylesheetsTableModel.fireTableDataChanged();

	}

	private void publish() {
		progressMonitor = new ProgressMonitor(PublishWizard.this,
				"Publishing Corpus", "", 0, 100);
		progressMonitor.setProgress(0);
		progressMonitor.setMillisToDecideToPopup(200);

		pTask = new PublishCorpusTask();
		pTask.addPropertyChangeListener(this);
		pTask.execute();

	}

	class PublishCorpusTask extends SwingWorker<Void, Void> {
		String error = "<html><body>";

		String html = "";

		StylesheetFactory sf = new StylesheetFactory(true);

		String index = "";

		float fileCount = 0;

		float count = 0;

		@Override
		public Void doInBackground() {
			int progress = 0;
			setProgress(0);
			// eine hasmap mit allen zu transformierenden dateien und einem string für's html machen
			progressMonitor.setNote("getting file list");
			setProgress(1);
			HashMap<File, String> filesToTransform = new HashMap<File, String>();
			Vector<Vector<Object>> o = filesTableModel.getDataVector();
			for (Vector<Object> v : o) {
				if ((Boolean) v.get(0)) {
					filesToTransform.put((File) v.get(1), "");
				}
			}
			// eine hashmap mit allen stylesheets und ihren namen machen
			HashMap<File, String> styleSheetsToUse = new HashMap<File, String>();
			Vector<Vector<Object>> o2 = stylesheetsTableModel.getDataVector();
			for (Vector<Object> v : o2) {
				if ((Boolean) v.get(0)) {
					styleSheetsToUse.put((File) v.get(1), (String) v.get(2));
				}
			}
			for (File xmlFile : filesToTransform.keySet()) {
				count++;
				try {
					Thread.sleep(1L);
				} catch (InterruptedException err1) {
					done();
				}
				int pro = Math.round((100 / filesToTransform.size()) * count);
				pro = (pro == 100) ? 99 : pro;
				setProgress(Math.round(pro));

				String fileIndex = "";
				String dirName = xmlFile.getName().replace(".", "_");

				String sourcepath = "";
				String taregtpath = "";

				//	zielverzeichnis anlegen
				File fileTargetDir = new File(targetDir + File.separator
						+ getRelativePath(rootDir, xmlFile.getParentFile())
						+ File.separator + dirName);
				fileTargetDir.mkdirs();
				if (fileTargetDir.exists() == false) {
					error += "<p>creating <b>" + targetDir + File.separator
							+ getRelativePath(rootDir, xmlFile.getParentFile())
							+ File.separator + dirName + "</b> failed</p>";

				} else { // es geht nur mit angelegtem verzechnis weiter!
					// originaldatei kopieren...
					if (copyFile(xmlFile.getAbsolutePath(), targetDir
							+ File.separator
							+ getRelativePath(rootDir, xmlFile.getParentFile())
							+ dirName + File.separator + xmlFile.getName()) == false) {
						error += "<p>copying <b>" + xmlFile.getName()
								+ "</b> failed</p>";
					} else {
						File copiedFile = new File(targetDir
								+ File.separator
								+ getRelativePath(rootDir, xmlFile
										.getParentFile()) + dirName
								+ File.separator + xmlFile.getName());
						// display-stylesheet probieren

						String ds = "";
						if ((displayStylesheet != null)
								&& (displayStylesheet.exists())) {
							try {
								ds = sf
										.applyExternalStylesheetToExternalXMLFile(
												displayStylesheet
														.getAbsolutePath(),
												copiedFile.getAbsolutePath());
							} catch (Exception err) {
								error += "<p>" + err.getMessage() + "</p>";
								ds = "";
							}
						}
						if (ds.length() > 0) {
							index += ds;
							fileIndex += ds;
						} else { // hat nicht geklappt - nur dateiname als überschrift
							fileIndex += "<h3>" + copiedFile.getName()
									+ "</h3>";
							index += "<h3>" + copiedFile.getName() + "</h3>";
						}
						fileIndex += "<p><strong><a href='"
								+ copiedFile.getName() + "'>"
								+ copiedFile.getName() + "</a></strong>";

						index += "<p><strong><a href='"
								+ getRelativePath(targetDir, copiedFile) + "'>"
								+ copiedFile.getName() + "</a></strong>";
						// an die Transformationen!
						index += "<ul>";
						fileIndex += "<ul>";
						for (File trStylesheet : styleSheetsToUse.keySet()) {
							try {
								String transResult;
								transResult = sf
										.applyExternalStylesheetToExternalXMLFile(
												trStylesheet.getAbsolutePath(),
												copiedFile.getAbsolutePath());

								String transformedFilename = copiedFile
										.getParent()
										+ File.separator

										+ copiedFile.getName()
												.replace(".", "_")

										+ trStylesheet.getName().substring(
												0,
												trStylesheet.getName()
														.toUpperCase().indexOf(
																".XSL"))
										+ ".html";
								TeideFileWriter writer = new TeideFileWriter(
										new File(transformedFilename));
								writer.write(transResult);
								fileIndex += "<li>";
								index += "<li>";
								index += "<a href='"
										+ URLDecoder.decode(getRelativePath(
												targetDir,
												new File(transformedFilename))
												.toString()) + "'>";
								fileIndex += "<a href='" + transformedFilename
										+ "'>";

								if (styleSheetsToUse.get(trStylesheet).length() > 0) {
									fileIndex += styleSheetsToUse
											.get(trStylesheet);
									index += styleSheetsToUse.get(trStylesheet);
								} else {
									fileIndex += transformedFilename;
									index += transformedFilename;
								}
								index += "</a></li>";
								fileIndex += "</a></li>";

							} catch (Exception err) {
								error += "<p>" + err.getMessage() + "</p>";
							}
						}
						fileIndex += "</ul>";
						index += "</ul>";
						fileIndex = "<html><head><title>Korpus</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
								+ TeideHTML.scriptString()
								+ TeideHTML.styleString()
								+ "</head><body>"
								+ fileIndex + "</body></html>";
						TeideFileWriter writer = new TeideFileWriter(new File(
								fileTargetDir.getAbsolutePath()
										+ File.separator + "index.html"));
						writer.write(fileIndex);
					}
				}
			}
			System.out.println(error);
			System.out.println(index);
			index = "<html><head><title>Korpus</title><meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
					+ TeideHTML.scriptString()
					+ TeideHTML.styleString()
					+ "</head><body>" + index + "</body></html>";
			TeideFileWriter writer = new TeideFileWriter(new File(targetDir
					+ File.separator + "index.html"));
			writer.write(index);

			setProgress(100);
			return null;
		}

		public void done() {
			progressMonitor.setProgress(100);
			progressMonitor.close();
			fillInputTable();
		}
	}

	private static boolean copyFile(String src, String dest) {
		try {
			File inputFile = new File(src);
			File outputFile = new File(dest);

			FileInputStream in = new FileInputStream(inputFile);
			FileOutputStream out = new FileOutputStream(outputFile);

			FileChannel inc = in.getChannel();
			FileChannel outc = out.getChannel();

			inc.transferTo(0, inc.size(), outc);

			inc.close();
			outc.close();

			in.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;// fuck!

		}
	}

	/**
	 * This method initializes displaySSPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getDisplaySSPanel() {
		if (displaySSPanel == null) {
			displaySSPanel = new JPanel();
			displaySSPanel.setLayout(new FlowLayout());
			displayLabel = new JLabel();
			displayLabel.setText("description-stylesheet:");

			displaySSPanel.add(displayLabel, null);
			displayFileLabel = new JLabel();
			displayFileLabel.setText("no description-stylesheet selected");

			displaySSPanel.add(displayFileLabel, null);
			displaySSPanel.add(getSelectDisplayButton(), null);
		}
		return displaySSPanel;
	}

} //  @jve:decl-index=0:visual-constraint="10,10"
