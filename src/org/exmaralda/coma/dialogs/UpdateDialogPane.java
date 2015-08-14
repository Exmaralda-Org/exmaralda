package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Ui;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

/**
 * @author kai released = the last released major version (i.e. 1.6) latest =
 *         the latest released (possibly preview) version (i.e. 1.6.115)
 * 
 */
public class UpdateDialogPane extends JDialog {

	private JButton visitButton;
	private String dlFile;
	private JButton getFileButton;
	private JButton disposeButton;
	private boolean updateAvailable;
	private boolean previewAvailable;
	private String prFile;
	private String latestVerString;
	private String releasedVerString;

	public UpdateDialogPane(Frame owner, String version) {
		super(owner);
		System.out.println(version);
		updateAvailable = false;
		previewAvailable = false;
		this.setLayout(new BorderLayout());
		String text = "";
		this.setTitle(Ui.getText("updateCheck.windowTitle"));
		JTextPane textArea = new JTextPane();
		textArea.setBorder(BorderFactory.createLoweredBevelBorder());
		textArea.setEditorKit(new HTMLEditorKit());
		textArea.setEditable(false);
		this.add(textArea, BorderLayout.CENTER);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		this.add(buttonPanel, BorderLayout.SOUTH);
		Document changes = getDocument();
		disposeButton = new JButton(Ui.getText("OK"));
		disposeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		if (changes != null) {
			Element root = changes.getRootElement();
			XPath xp;
			try {
				xp = XPath.newInstance("//change[@tool='coma'][1]");
				Element lts = (Element) xp.selectSingleNode(root);
				xp = XPath
						.newInstance("//change[@tool='coma'][@release='true']");
				Element ltr = (Element) xp.selectSingleNode(root);
				latestVerString = lts.getAttributeValue("version");
				releasedVerString = ltr.getAttributeValue("version");

			} catch (Exception e1) {
				e1.printStackTrace();
				// darn!
			}
		}

		if (latestVerString == null) { // remote info not found
			text = Ui.getText("updateCheck.downloadFailed");

		} else {
			int installedMajor = new Integer(version.split("\\.")[0]
					+ version.split("\\.")[1]);
			System.out.println("Installed Major:" + installedMajor);
			int releaseMajor = new Integer(releasedVerString.split("\\.")[0]
					+ releasedVerString.split("\\.")[1]);
			System.out.println("Latest Release:" + releaseMajor);
			int lastestMajor = new Integer(latestVerString.split("\\.")[0]
					+ latestVerString.split("\\.")[1]);
			System.out.println("Latest Preview:" + installedMajor);
			int installedMinor = (version.split("\\.").length > 2
					? new Integer(version
							.substring(version.lastIndexOf(".") + 1))
					: 0);
			int releasedMinor = (releasedVerString.split("\\.").length > 2
					? new Integer(releasedVerString.substring(releasedVerString
							.lastIndexOf(".") + 1))
					: 0);
			int latestMinor = (latestVerString.split("\\.").length > 2
					? new Integer(latestVerString.substring(latestVerString
							.lastIndexOf(".") + 1))
					: 0);
			Ui.prefs.putLong("updateLastChecked", System.currentTimeMillis()); // set
			previewAvailable = false;

			if (installedMajor < releaseMajor) {
				updateAvailable = true;
			}

			if (installedMinor < latestMinor) {
				previewAvailable = true;
			}

			if (updateAvailable || previewAvailable) {
				text = "<image src=\""
						+ ComaHTML
								.getImagePath("software-update-available.png")
						+ "\" border=\"0\""
						+ "><h3>"
						+ (updateAvailable ? "Update " : " ")
						+ ((updateAvailable && previewAvailable) ? " & " : "")
						+ (previewAvailable ? "Preview " : " ")
						+ Ui.getText("available")
						+ "!</h3>"
						+ Ui.getText("updateCheck.yourVersion")
						+ ": "
						+ version
						+ "<br>"
						+ Ui.getText("updateCheck.latestReleaseVersion")
						+ ": "
						+ releasedVerString
						+ (updateAvailable
								? (" (" + Ui.getText("new") + "!)")
								: "")
						+ "<br>"
						+ Ui.getText("updateCheck.latestPreviewVersion")
						+ ": "
						+ latestVerString
						+ (previewAvailable
								? (" (" + Ui.getText("new") + "!)")
								: "") + "<br>";
			} else {
				text = Ui.getText("updateCheck.noUpdate").replace("@v",
						"<b>" + version + "</b>");
				disposeButton.setText(Ui.getText("updateCheck.thankGod"));
			}
			if (updateAvailable || previewAvailable) {
				visitButton = new JButton(Ui.getText("updateCheck.visitPage"));
				visitButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop
									.getDesktop()
									.browse(
											new URI(
													updateAvailable
															? "http://www.exmaralda.org/downloads.html"
															: "http://www.exmaralda.org/previews2.html"));
							dispose();
						} catch (Exception e1) {
							e1.printStackTrace();
							visitButton.setText(Ui
									.getText("updateCheck.pageUnreachable"));
							visitButton.setEnabled(false);

						}
					}
				});
				buttonPanel.add(visitButton);
			} else {
				text = Ui.getText("updateCheck.noUpdate").replace("@v",
						"<b>" + version + "</b>");
				disposeButton.setText(Ui.getText("updateCheck.thankGod"));
			}
		}
		textArea
				.setText("<html><body style='font-family: sans-serif, arial; padding-left: 5px;'>"
						+ text + "<p>&nbsp;</p></body></html>");
		buttonPanel.add(disposeButton);
		pack();
	}

	private Document getDocument() {
		SAXBuilder builder = new SAXBuilder(
				"org.apache.xerces.parsers.SAXParser");
		Document updateDoc;
		try {
			updateDoc = builder
					.build("http://www.exmaralda.org/update/changes.xml");
			return updateDoc;
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out
					.println("could not check for updates - maybe your internet-connection is not available.");
		}
		return null;
	}

	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

}