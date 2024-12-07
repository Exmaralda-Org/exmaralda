package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.exmaralda.coma.panels.DataPanel;
import org.exmaralda.coma.panels.MixedDescriptionPanel;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaData;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class SpeakerRolesDialog extends JDialog {

	private Coma coma;
	private ComaData data;
	private JTextField typeTF;
	private MixedDescriptionPanel dp;
	public boolean returnValue;

	public SpeakerRolesDialog(Coma c, DataPanel dataPanel) {
		super((Frame) c, Ui.getText("cmd.editRoles"));
		setModal(true);
		coma = c;
		data = coma.getData();
		this.setLayout(new BorderLayout());
		DefaultListModel cListModel = new DefaultListModel();
		JList cList = new JList(cListModel);
		cList.setEnabled(false);
		for (Element e : data.getSelectedCommunications().values()) {
			cListModel.addElement(e.getAttributeValue("Name"));
		}
		cList.setBorder(BorderFactory.createTitledBorder(Ui
				.getText("communication")));
		this.add(cList, BorderLayout.EAST);
		DefaultListModel pListModel = new DefaultListModel();
		JList pList = new JList(pListModel);
		pList.setEnabled(false);
		for (Element e : data.getSelectedPersons().values()) {
			pListModel.addElement(e.getChild("Sigle").getText() + "("
					+ e.getChild("Pseudo").getText() + ")");
		}
		pList.setBorder(BorderFactory.createTitledBorder(Ui.getText("speakers")));
		this.add(pList, BorderLayout.WEST);
		this.add(new JLabel("ROLE"), BorderLayout.CENTER);

		JPanel rolePanel = new JPanel();
		rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
		rolePanel
				.setBorder(BorderFactory.createTitledBorder(Ui.getText("role")));
		typeTF = new JTextField("Type");
		rolePanel.add(typeTF);
		Vector<Element> descs = new Vector<Element>();
		for (Element e : data.getSelectedPersons().values()) {
			if (e.getChild("role") != null)
				descs.add(e.getChild("role").getChild("Description"));
		}
		dp = new MixedDescriptionPanel(descs);
		rolePanel.add(dp);
		// rolePanel.add(new InnerDescriptionPanel(new Description(coma),
		// false));
		this.add(rolePanel, BorderLayout.CENTER);
		JPanel okp = new JPanel();
		JButton okButton = new JButton(Ui.getText("OK"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done(true);
			}
		});
		JButton cancelButton = new JButton(Ui.getText("cancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				done(false);
			}
		});
		okp.add(okButton);
		okp.add(cancelButton);
		this.add(okp, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(coma);
	}

	protected void done(boolean change) {
		if (change) {
			for (Element pe : data.getSelectedPersons().values()) {
				pe.addContent(getRoleElements());
			}
		}
		this.dispose();
	}

	public Vector<Element> getRoleElements() {
		Vector<Element> roleElements = new Vector<Element>();
		for (Element ce : data.getSelectedCommunications().values()) {
			Element e = new Element("role");
			e.setAttribute("target", ce.getAttributeValue("Id"));
			e.setAttribute("type", typeTF.getText());
			e.addContent(dp.getDescriptionElement());
			roleElements.add(e);
		}
		return roleElements;
	}
}
/*
 * <xs:element name="Language" type="LanguageType" minOccurs="0"
 * maxOccurs="unbounded"/> <xs:element name="role" type="roleType" minOccurs="0"
 * maxOccurs="unbounded"/>
 * 
 * 
 * 
 * <xs:complexType name="roleType"> <xs:annotation> <xs:documentation>Role of
 * Speakers (and potentially other datatypes)</xs:documentation>
 * </xs:annotation> <xs:sequence> <xs:choice maxOccurs="unbounded"> <xs:element
 * name="Description" type="DescriptionType" minOccurs="0"/> </xs:choice>
 * </xs:sequence> <xs:attribute name="Type" type="xs:string" use="optional"/>
 * <xs:attribute name="target" type="xs:IDREF" use="required"/>
 * </xs:complexType>
 */
