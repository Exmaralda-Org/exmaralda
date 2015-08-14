package org.exmaralda.coma.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.exmaralda.coma.datatypes.Speaker;
import org.exmaralda.coma.root.Coma;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class RoleEditorDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox targetBox;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			RoleEditorDialog dialog = new RoleEditorDialog(null, null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public RoleEditorDialog(Coma c, Speaker speaker) {
		setTitle("Edit roles for speaker: " + speaker.getPseudo());
		setBounds(100, 100, 800, 600);
		setLocationRelativeTo(c);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.CENTER);
			panel.setLayout(new BorderLayout(0, 0));
			{
				JPanel panel_1 = new JPanel();
				panel.add(panel_1, BorderLayout.NORTH);
				panel_1.setLayout(new GridLayout(2, 0, 0, 0));
				{
					JLabel lblNewLabel = new JLabel("Role target:");
					panel_1.add(lblNewLabel);
				}
				{
					targetBox = new JComboBox(speaker
							.getRoleTargets().values().toArray());
					targetBox.addPropertyChangeListener(new PropertyChangeListener() {
						public void propertyChange(PropertyChangeEvent arg0) {
							roleChanged();
						}
					});
					panel_1.add(targetBox);
				}
				{
					JLabel lblNewLabel_1 = new JLabel("Role type:");
					panel_1.add(lblNewLabel_1);
				}
				{
					JComboBox comboBox = new JComboBox();
					comboBox.setEditable(true);
					panel_1.add(comboBox);
				}
			}
			{
				JPanel descPanel = new JPanel();
				panel.add(descPanel, BorderLayout.CENTER);
				// descPanel.add(new InnerDescriptionPanel(new Description(c),
				// true));
			}

		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						cancel();
					}
				});
				buttonPane.add(cancelButton);
			}
		}
	}

	protected void roleChanged() {
		
		getTargetBox().getSelectedItem();
		
		// change or create InnerDescriptionTable
		// change rollType-Dropdown

		// TODO Auto-generated method stub
		
	}

	protected void cancel() {
		this.dispose();
	}

	protected JComboBox getTargetBox() {
		return targetBox;
	}
}
