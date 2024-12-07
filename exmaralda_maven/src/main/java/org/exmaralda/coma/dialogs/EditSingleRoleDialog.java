package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.exmaralda.coma.datatypes.Description;
import org.exmaralda.coma.panels.DataPanel;
import org.exmaralda.coma.panels.MixedDescriptionPanel;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaData;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class EditSingleRoleDialog extends JDialog {

	private Coma coma;
	private ComaData data;
	private JTextField typeTF;
	private MixedDescriptionPanel dp;
	public boolean returnValue;

	public EditSingleRoleDialog(Coma c, DataPanel dataPanel) {
		super((Frame) c, Ui.getText("cmd.editRoles"));
		setModal(true);
		coma = c;
		data = coma.getData();
		this.setLayout(new BorderLayout());
		Element pEl = data.getSelectedPersons().get(0);
		Element cEl = data.getSelectedCommunications().get(0);
		JLabel titleLabel = new JLabel("<html><h2>Role:"
				+ pEl.getChild("Sigle").getText()
				+ "("
				+ pEl.getChild("Pseudo").getText()
				+ ") in "
				+ cEl.getAttributeValue("Name")
						+"</h2></html>"

		);
		this.add(titleLabel,BorderLayout.NORTH);
		this.add(new JLabel("ROLE"), BorderLayout.CENTER);

		JPanel rolePanel = new JPanel();
		rolePanel.setLayout(new BoxLayout(rolePanel, BoxLayout.Y_AXIS));
		rolePanel
				.setBorder(BorderFactory.createTitledBorder(Ui.getText("role")));
		typeTF = new JTextField("Type");
		rolePanel.add(typeTF);
		
//		if (pEl.getChildren("role").size()))
		Description d = new Description(pEl.getChild("Description"),coma);
		
		JPanel p = new JPanel(new BorderLayout());
		InnerDescriptionPanel dp = new InnerDescriptionPanel(d, true);

		
		Vector<Element> descs = new Vector<Element>();
		for (Element e : data.getSelectedPersons().values()) {
			if (e.getChild("role") != null)
				descs.add(e.getChild("role").getChild("Description"));
		}
	//	dp = new MixedDescriptionPanel(descs);
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
