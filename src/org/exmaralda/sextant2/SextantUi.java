package org.exmaralda.sextant2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SextantUi extends javax.swing.JFrame {
	private JLabel statusLabel;
	private JEditorPane outputTextArea;
	private JButton validateButton;
	private JButton visualizeButton;
	private JScrollPane jScrollPane2;
	private JList concList;
	private JScrollPane jScrollPane1;
	private JList transcriptionList;
	private JButton openTransButton;
	private JPanel filePanel;
	private JList annotationList;
	private JPanel infoPanel;
	private JButton convertButton;
	private JPanel ButtonPanel;
	private SextantApp app;
	private StringBuffer htmlBuffer = new StringBuffer("");

	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				SextantUi inst = new SextantUi();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}

	public SextantUi() {
		super();
		initGUI();
	}

	public SextantUi(SextantApp app) {
		super();
		this.app = app;
		initGUI();
	}

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setTitle("sextant 0.2");
			{
				statusLabel = new JLabel();
				getContentPane().add(statusLabel, BorderLayout.SOUTH);
				statusLabel.setText("OK");
				statusLabel.setBorder(new LineBorder(
						new java.awt.Color(0, 0, 0), 1, false));
				statusLabel.setOpaque(true);
			}
			{
				jScrollPane1 = new JScrollPane();
				getContentPane().add(jScrollPane1, BorderLayout.CENTER);
				jScrollPane1.setPreferredSize(new java.awt.Dimension(238, 118));
				//START >>  jScrollPane2
				jScrollPane2 = new JScrollPane();
				jScrollPane1.setViewportView(jScrollPane2);
				jScrollPane2.setPreferredSize(new java.awt.Dimension(235, 331));
				{
					outputTextArea = new JEditorPane();
					jScrollPane2.setViewportView(outputTextArea);
					outputTextArea.setEditorKit(new HTMLEditorKit());
					outputTextArea.setText("sextant");
					outputTextArea.setPreferredSize(new java.awt.Dimension(232, 328));
					outputTextArea.setEditable(false);
				}
				//END <<  jScrollPane2
			}
			{
				ButtonPanel = new JPanel();
				BoxLayout ButtonPanelLayout = new BoxLayout(ButtonPanel,
						javax.swing.BoxLayout.Y_AXIS);
				ButtonPanel.setLayout(ButtonPanelLayout);
				getContentPane().add(ButtonPanel, BorderLayout.WEST);
				ButtonPanel.setPreferredSize(new java.awt.Dimension(133, 118));
				{
					validateButton = new JButton();
					ButtonPanel.add(validateButton);
					validateButton.setText("validate");
					validateButton.setPreferredSize(new java.awt.Dimension(108,
							22));
					validateButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
app.validate();						}
					});
				}
				{
					visualizeButton = new JButton();
					ButtonPanel.add(visualizeButton);
					visualizeButton.setText("visualize");
					visualizeButton.setPreferredSize(new java.awt.Dimension(
							117, 22));
					visualizeButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							app.visualize();						}
					});
				}
				{
					convertButton = new JButton();
					ButtonPanel.add(convertButton);
					convertButton.setText("convert");
					convertButton.setPreferredSize(new java.awt.Dimension(96,
							22));
					convertButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
app.convert();						}
					});
				}
			}
			{
				infoPanel = new JPanel();
				BoxLayout infoPanelLayout = new BoxLayout(infoPanel,
						javax.swing.BoxLayout.Y_AXIS);
				infoPanel.setLayout(infoPanelLayout);
				getContentPane().add(infoPanel, BorderLayout.EAST);
				infoPanel.setPreferredSize(new java.awt.Dimension(145, 334));
				{
					ListModel transcriptionListModel = new DefaultComboBoxModel();
					transcriptionList = new JList();
					infoPanel.add(transcriptionList);
					transcriptionList.setModel(transcriptionListModel);
					transcriptionList.setBorder(BorderFactory
							.createTitledBorder("transcriptions"));
					transcriptionList
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					transcriptionList
							.addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent evt) {
									listValueChanged(evt);
								}
							});
				}
				{
					ListModel annotationListModel = new DefaultComboBoxModel();
					annotationList = new JList();
					infoPanel.add(annotationList);
					annotationList.setModel(annotationListModel);
					annotationList.setBorder(BorderFactory
							.createTitledBorder("annotations"));
					annotationList
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					annotationList
							.addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent evt) {
									listValueChanged(evt);
								}
							});

				}
				// START >> concList
				ListModel concListModel = new DefaultComboBoxModel();
				concList = new JList();
				infoPanel.add(concList);
				concList.setModel(concListModel);
				concList.setBorder(BorderFactory
						.createTitledBorder("concordances"));
				concList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				concList.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent evt) {
						listValueChanged(evt);
					}
				});

				// END << concList
			}
			{
				filePanel = new JPanel();
				getContentPane().add(filePanel, BorderLayout.NORTH);
				{
					openTransButton = new JButton();
					filePanel.add(openTransButton);
					openTransButton.setText("open");
					openTransButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							app.open();
						}
					});
				}
			}
			setButtons(false,false,false);
			pack();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void listValueChanged(ListSelectionEvent evt) {
		JList l = (JList) evt.getSource();
		if (((JList) evt.getSource()).getSelectedIndex() > -1) {
			if (l == transcriptionList) {
				annotationList.removeSelectionInterval(0, annotationList
						.getMaxSelectionIndex());
				concList.removeSelectionInterval(0, concList
						.getMaxSelectionIndex());
				app.setSelectedItem("transcription",(File)((JList) evt.getSource()).getModel().getElementAt(((JList) evt.getSource()).getSelectedIndex()));
				setButtons(false,false,true);

			}
			if (l == annotationList) {
				transcriptionList.removeSelectionInterval(0, transcriptionList
						.getMaxSelectionIndex());
				concList.removeSelectionInterval(0, concList
						.getMaxSelectionIndex());
				app.setSelectedItem("annotation",(File)((JList) evt.getSource()).getModel().getElementAt(((JList) evt.getSource()).getSelectedIndex()));
				setButtons(true,false,true);
			}
			if (l == concList) {
				annotationList.removeSelectionInterval(0, annotationList
						.getMaxSelectionIndex());
				transcriptionList.removeSelectionInterval(0, transcriptionList
						.getMaxSelectionIndex());
				app.setSelectedItem("concordance",(File)((JList) evt.getSource()).getModel().getElementAt(((JList) evt.getSource()).getSelectedIndex()));
				setButtons(false,true,false);
			}
		}
	}

	private void setButtons(boolean v, boolean c, boolean vi) {
		validateButton.setEnabled(v);
		convertButton.setEnabled(c);
		visualizeButton.setEnabled(vi);
		
	}

	public void status(String string, boolean success) {
		htmlBuffer.append(success ? "<p>" : "<p style='color: red;'>");
		htmlBuffer.append(string+"</p>");
		outputTextArea.setText(htmlBuffer.toString());
		statusLabel.setText(success ? "ok" : "error!");
		statusLabel.setBackground(success ? Color.GREEN : Color.RED);
	}

	public void updateAnns(Vector<File> annotations) {
		annotationList.setListData(annotations);
	}

	public void updateTrans(Vector<File> transcriptions) {
		transcriptionList.setListData(transcriptions);
	}

}
