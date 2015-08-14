package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.exmaralda.coma.datatypes.Role;
import org.exmaralda.coma.datatypes.Speaker;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;
/**
 * Roles won't happen. Ever.
 *
 * @deprecated will not be finished
 */
@Deprecated
public class RoleDialog extends JDialog implements ListDataListener {

	private final JPanel contentPanel = new JPanel();
	private InnerDescriptionPanel descriptionPanel;
	private JList list;
	private Element spkElement;
	private Speaker speaker;
	private JLabel roleLabel;
	private JComboBox typeCombo;
	private Vector comboVector;
	private DefaultComboBoxModel comboBoxModel;
	private Speaker spk;
	private DefaultListModel roleModel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RoleDialog dialog = new RoleDialog(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RoleDialog(Coma c, Speaker s) {
		setModal(true);
		speaker = s;
		spkElement = (Element) s.getElement().clone();
		for (Role r : s.getRoles()) {
			c.getData().addRoleType(r.getType());
		}

		spk = new Speaker(c, spkElement, false);
		// clone speaker from main xml

		setBounds(100, 100, 614, 494);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.WEST);
			{
				 roleModel = new DefaultListModel();
				list = new JList(roleModel);
				for (Role r : spk.getRoles()) {
					roleModel.addElement(r);
				}
				list.addListSelectionListener(new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent arg0) {
						roleChanged();
					}
				});
				scrollPane.setViewportView(list);
				scrollPane.setBorder(new TitledBorder(null, "Roles",
						TitledBorder.LEADING, TitledBorder.TOP, null, null));

			}
		}
		{
			JPanel panel = new JPanel();
			panel.setBorder(new TitledBorder(null, "Properties",
					TitledBorder.LEADING, TitledBorder.TOP, null, Color.BLACK));
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				descriptionPanel = new InnerDescriptionPanel(spk.getRoles()
						.get(0).getDescription(), false);
				panel.add(descriptionPanel);
			}
			{
				roleLabel = new JLabel("New label");
				roleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
				panel.add(roleLabel, BorderLayout.NORTH);
			}
			{
				comboVector = new Vector(c.getData().getRoleTypes());
				comboBoxModel = new DefaultComboBoxModel(comboVector);
				typeCombo = new JComboBox(comboBoxModel);
				typeCombo.setEditable(true);
				typeCombo.setBorder(new TitledBorder(null, "Type",
						TitledBorder.LEADING, TitledBorder.TOP, null, null));
				panel.add(typeCombo, BorderLayout.SOUTH);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.SOUTH);
			panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			{
				JButton btnNewButton = new JButton("Remove Role");
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						removeRoles();
					}
				});
				panel.add(btnNewButton);
			}
			{
				JButton btnNewButton_1 = new JButton("Add Role");
				btnNewButton_1.setEnabled(false);
				btnNewButton_1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						addRole();
					}
				});
				panel.add(btnNewButton_1);
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.EAST);
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			{
				JCheckBox chckbxNewCheckBox = new JCheckBox("New check box");
				panel.add(chckbxNewCheckBox);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						OK();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				// getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void addRole() {
		// TODO Auto-generated method stub
		
	}

	protected void removeRoles() {
		System.out.println(roleModel.size());
		for (int i = roleModel.size(); i>0 ; i--) {
			System.out.println(">>"+i);
			if (list.isSelectedIndex(i)) {
				spk.removeRole((Role)roleModel.get(i));
				roleModel.remove(i);
			}
		}
		
	}


	protected void cancel() {
		this.dispose();
	}

	protected void OK() {
		descriptionPanel.updateDescription();
		speaker.getElement().removeChildren("role");
		List<Element> newRoles = spkElement.getChildren("role");
		for (Element nr : newRoles) {
			speaker.getElement().addContent((Element) nr.clone());
		}
		this.dispose();

	}

	protected void roleChanged() {
		if (list.getSelectedIndices().length == 1) {
			descriptionPanel.updateDescription();
			roleLabel.setText(list.getSelectedValue().toString());
			descriptionPanel.setDescription(((Role) list.getSelectedValue())
					.getDescription());
			typeCombo.setSelectedItem(((Role) list.getSelectedValue())
					.getType());
		} else {
			roleLabel.setText("multiple descriptions");

			// multi-description editing
		}
	}

	@Override
	public void contentsChanged(ListDataEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void intervalAdded(ListDataEvent arg0) {
		comboVector.add(typeCombo.getSelectedItem());
		// TODO Auto-generated method stub
		
	}

	@Override
	public void intervalRemoved(ListDataEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
