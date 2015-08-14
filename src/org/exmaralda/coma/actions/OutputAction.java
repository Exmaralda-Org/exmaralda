/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.exmaralda.coma.resources.ResourceHandler;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.Ui;
import org.exmaralda.common.helpers.ExmaraldaFileFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;

/**
 * coma2/org.sfb538.coma2.fileActions/SaveAction.java action for save- and
 * save-as commands
 * 
 * @author woerner
 * 
 */
public class OutputAction extends ComaAction implements ListSelectionListener {
	private static final long serialVersionUID = 4601685287146653101L;

	boolean saveAs = false;
	Coma coma;

	private JList stylesheetNames;

	private HashMap<String, Element> stylesheets;

	private JTextArea stylesheetDescriptionLabel;

	private JRadioButton corpusRadio;

	private JRadioButton basketRadio;

	private ButtonGroup scopeGroup;

	private JPanel scopePanel;

	private File outFile;

	private JLabel outputFilenameLabel;

	private JButton optionsButton;

	private JButton cancelButton;

	private JButton OKButton;

	private JDialog d;

	private JPanel descriptionPanel;

	private JPanel xslSelectPanel;

	private JLabel xslPathLabel;

	protected File xslFile = new File("");

	public OutputAction(Coma c, javax.swing.ImageIcon icon) {

		super(Ui.getText("cmd.OutputAction"), icon, c);
		coma = c;
	}

	public OutputAction(Coma c) {
		this(c, null);
		coma = c;
	}

	public OutputAction(Coma c, boolean sa) {
		this(c, null);
		coma = c;
	}

	public void actionPerformed(ActionEvent actionEvent) {
		FileSystemView filesys = FileSystemView.getFileSystemView();
		File[] roots = filesys.getRoots(); // http://littletutorials.com/2008/03/10/getting-file-system-details-in-java/
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMM_hhmm");
		outFile = new File(filesys.getHomeDirectory() + "/output_"
				+ sdf.format(cal.getTime()) + ".html");

		SAXBuilder builder = new SAXBuilder(
				"org.apache.xerces.parsers.SAXParser");
		boolean read = false;
		Document stylesheetDoc = null;
		try {
			stylesheetDoc = builder.build(new ResourceHandler()
					.stylesheetsList());
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		stylesheets = new HashMap<String, Element>();
		System.out.println(stylesheetDoc);
		Element r = stylesheetDoc.getRootElement();
		stylesheetNames = new JList(new DefaultListModel());
		stylesheetNames.addListSelectionListener(this);

		for (Element e : (List<Element>) r.getChildren()) {
			stylesheets.put(e.getChildText("name"), e);
			((DefaultListModel) stylesheetNames.getModel()).addElement(e
					.getChildText("name"));
		}
		System.out.println(outFile);
		//
		JDialog outputDialog = getOutputDialog();
		outputDialog.setVisible(true);
	}

	private JDialog getOutputDialog() {

		d = new JDialog();
		d.setLayout(new BorderLayout());
		JPanel filenamePanel = new JPanel();
		filenamePanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("outputFile")));
		outputFilenameLabel = new JLabel(outFile.getAbsolutePath());
		filenamePanel.add(outputFilenameLabel);
		JButton fileSelectButton = new JButton(Ui.getText("browse"));
		fileSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				outFile = selectFile();
				outputFilenameLabel.setText(outFile.getAbsolutePath());
			}
		});
		filenamePanel.add(fileSelectButton);
		d.add(filenamePanel, BorderLayout.NORTH);
		stylesheetNames.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("outputStylesheet")));
		d.add(new JScrollPane(stylesheetNames), BorderLayout.WEST);
		descriptionPanel = new JPanel(new CardLayout());
		stylesheetDescriptionLabel = new JTextArea("");
		stylesheetDescriptionLabel.setPreferredSize(new Dimension(300, 200));
		stylesheetDescriptionLabel.setFont(new Font("Sans-Serif", Font.PLAIN,
				12));
		stylesheetDescriptionLabel.setLineWrap(true);
		stylesheetDescriptionLabel.setWrapStyleWord(true);
		JScrollPane descriptionScroller = new JScrollPane(
				stylesheetDescriptionLabel);
		stylesheetDescriptionLabel.setBorder(BorderFactory
				.createTitledBorder(Ui.getText("description")));
		d.add(descriptionPanel, BorderLayout.CENTER);
		descriptionPanel.add(stylesheetDescriptionLabel, "DESCRIPTION");
		xslSelectPanel = new JPanel();
		xslPathLabel = new JLabel("path to XSL-File");
		JButton xslSelectButton = new JButton(Ui.getText("browse"));
		xslSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				xslFile = selectFile();
				xslPathLabel.setText(outFile.getAbsolutePath());
				if (xslFile.exists())
					OKButton.setEnabled(true);
			}
		});
		xslSelectPanel.add(xslPathLabel);
		xslSelectPanel.add(xslSelectButton);
		descriptionPanel.add(xslSelectPanel, "CUSTOMXSL");
		scopeGroup = new ButtonGroup();
		scopePanel = new JPanel();
		scopePanel.setLayout(new BoxLayout(scopePanel, BoxLayout.Y_AXIS));

		scopePanel.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("scope")));
		scopeGroup.add(corpusRadio = new JRadioButton(Ui
				.getText("scopewholeCorpus")));
		scopeGroup
				.add(basketRadio = new JRadioButton(Ui.getText("scopeBasket")));
		scopePanel.add(corpusRadio);
		scopePanel.add(basketRadio);
		corpusRadio.setSelected(true);
		d.add(scopePanel, BorderLayout.EAST);

		JPanel okCancelPanel = new JPanel();
		okCancelPanel.setLayout(new BoxLayout(okCancelPanel,
				BoxLayout.LINE_AXIS));
		okCancelPanel.add(optionsButton = new JButton(Ui.getText("options")));
		optionsButton.setEnabled(false);
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// not implemented
			}
		});
		okCancelPanel.add(Box.createHorizontalGlue());
		okCancelPanel.add(OKButton = new JButton(Ui.getText("OK")));
		OKButton.setEnabled(false);
		OKButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				transform();
				d.dispose();
			}
		});
		okCancelPanel.add(cancelButton = new JButton(Ui.getText("cancel")));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				d.dispose();
				return;
			}
		});
		d.add(okCancelPanel, BorderLayout.SOUTH);
		d.setModal(true);
		d.pack();
		d.setLocationRelativeTo(coma);
		stylesheetNames.setSelectedIndex(0);
		return d;
	}

	protected void transform() {
		StreamSource source;
		if (stylesheets.get(stylesheetNames.getSelectedValue().toString())
				.getChildText("filename").equals("")) { // custom stylesheet
			if (xslFile.exists()) {
				source = new StreamSource(xslFile);
			} else {
				OKButton.setEnabled(false);
				xslPathLabel.setText("");
				return;
			}
		} else {
			source = new StreamSource(
					new ResourceHandler().OutputXslStream(stylesheets.get(
							stylesheetNames.getSelectedValue().toString())
							.getChildText("filename")));
		}
		if (outFile.getName().length() > 0) {
			System.setProperty("javax.xml.transform.TransformerFactory",
					"net.sf.saxon.TransformerFactoryImpl");
			TransformerFactory factory = TransformerFactory.newInstance();
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(outFile);
				Transformer transformer = factory.newTransformer(source);
				transformer.transform(
						new JDOMSource((basketRadio.isSelected() ? coma
								.buildCorpusFromBasket() : coma.getData()
								.getDocument())), new StreamResult(fos));
				fos.close();
				Desktop.getDesktop().browse(outFile.toURI());
			} catch (TransformerConfigurationException e1) {
				JOptionPane.showMessageDialog(coma, e1.getLocalizedMessage(),
						Ui.getText("error"), JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected File selectXSL() {
		File file = new File("");
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(outFile.getParentFile());
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Stylesheet",
				new String[] { "xsl", "xslt" }, true));
		int returnVal = fc.showSaveDialog(d);
		if (fc.getSelectedFile() != null) {
			file = fc.getSelectedFile();

		}
		return file;

	}

	protected File selectFile() {
		File file = new File("");
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(outFile.getParentFile());
		fc.addChoosableFileFilter(new ExmaraldaFileFilter("Stylesheet-Output",
				new String[] { "html", "txt" }, true));
		int returnVal = fc.showSaveDialog(d);
		if (fc.getSelectedFile() != null) {
			file = fc.getSelectedFile();

		}
		return file;

	}

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		if (stylesheets.get(stylesheetNames.getSelectedValue().toString())
				.getChildText("filename").equals("")) {
			optionsButton.setEnabled(false);
			CardLayout cl = (CardLayout) (descriptionPanel.getLayout());
			cl.show(descriptionPanel, "CUSTOMXSL");
			if (!xslFile.exists())
				OKButton.setEnabled(false);
		} else {
			CardLayout cl = (CardLayout) (descriptionPanel.getLayout());
			cl.show(descriptionPanel, "DESCRIPTION");
			if (stylesheetNames.getSelectedIndex() > -1) {
				OKButton.setEnabled(true);
				stylesheetDescriptionLabel.setText(stylesheets.get(
						stylesheetNames.getSelectedValue().toString())
						.getChildText("description"));
				if (stylesheets
						.get(stylesheetNames.getSelectedValue().toString())
						.getChild("options").getChildren().size() > 0) {
					optionsButton.setEnabled(true);
				} else {
					optionsButton.setEnabled(false);
				}
			} else {
				OKButton.setEnabled(false);
				optionsButton.setEnabled(false);
			}
		}
	}
}
