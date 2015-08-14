package org.exmaralda.coma.root;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

public class FilterPanel extends JPanel {

	private JPanel F3 = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JScrollPane jScrollPane = null;
	private JPanel jPanel2 = null;
	private JRadioButton jRadioButton = null;
	private JRadioButton jRadioButton1 = null;
	private JRadioButton jRadioButton2 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	private JLabel jLabel2 = null;
	private JButton jButton = null;
	private JButton jButton1 = null;
	private JButton jButton2 = null;
	private JPanel jPanel3 = null;
	private JButton jButton3 = null;
	private JButton jButton4 = null;
	/**
	 * This is the default constructor
	 */
	public FilterPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(441, 114);
		this.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
		this.add(getJPanel3(), java.awt.BorderLayout.SOUTH);
	}

	/**
	 * This method initializes F3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getF3() {
		if (F3 == null) {
			jLabel = new JLabel();
			jLabel.setText("DArt=EFE4");
			jLabel.setBackground(new java.awt.Color(116,234,0));
			F3 = new JPanel();
			F3.setLayout(new BorderLayout());
			F3.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			F3.add(getJRadioButton(), java.awt.BorderLayout.WEST);
			F3.add(jLabel, java.awt.BorderLayout.CENTER);
			F3.add(getJButton(), java.awt.BorderLayout.EAST);
		}
		return F3;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new BoxLayout(getJPanel(), BoxLayout.Y_AXIS));
			jPanel.add(getF3(), null);
			jPanel.add(getJPanel1(), null);
			jPanel.add(getJPanel2(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel1() {
		if (jPanel1 == null) {
			jLabel1 = new JLabel();
			jLabel1.setText("Familie (Pseudo)=Tuzluk");
			jPanel1 = new JPanel();
			jPanel1.setLayout(new BorderLayout());
			jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			jPanel1.add(getJRadioButton1(), java.awt.BorderLayout.WEST);
			jPanel1.add(jLabel1, java.awt.BorderLayout.CENTER);
			jPanel1.add(getJButton1(), java.awt.BorderLayout.EAST);
		}
		return jPanel1;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJPanel());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jPanel2	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel2() {
		if (jPanel2 == null) {
			jLabel2 = new JLabel();
			jLabel2.setText("Language=TRK TK");
			jPanel2 = new JPanel();
			jPanel2.setLayout(new BorderLayout());
			jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray,1));
			jPanel2.add(getJRadioButton2(), java.awt.BorderLayout.WEST);
			jPanel2.add(jLabel2, java.awt.BorderLayout.CENTER);
			jPanel2.add(getJButton2(), java.awt.BorderLayout.EAST);
		}
		return jPanel2;
	}

	/**
	 * This method initializes jRadioButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton() {
		if (jRadioButton == null) {
			jRadioButton = new JRadioButton();
			jRadioButton.setSelected(true);
		}
		return jRadioButton;
	}

	/**
	 * This method initializes jRadioButton1	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton1() {
		if (jRadioButton1 == null) {
			jRadioButton1 = new JRadioButton();
			jRadioButton1.setSelected(true);
		}
		return jRadioButton1;
	}

	/**
	 * This method initializes jRadioButton2	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getJRadioButton2() {
		if (jRadioButton2 == null) {
			jRadioButton2 = new JRadioButton();
			jRadioButton2.setSelected(true);
		}
		return jRadioButton2;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setText("Remove");
			jButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			jButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		return jButton;
	}

	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setText("Remove");
		}
		return jButton1;
	}

	/**
	 * This method initializes jButton2	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton2() {
		if (jButton2 == null) {
			jButton2 = new JButton();
			jButton2.setText("Remove");
		}
		return jButton2;
	}

	/**
	 * This method initializes jPanel3	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel3() {
		if (jPanel3 == null) {
			jPanel3 = new JPanel();
			jPanel3.add(getJButton3(), null);
			jPanel3.add(getJButton4(), null);
		}
		return jPanel3;
	}

	/**
	 * This method initializes jButton3	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton3() {
		if (jButton3 == null) {
			jButton3 = new JButton();
			jButton3.setText("Add Filter");
		}
		return jButton3;
	}

	/**
	 * This method initializes jButton4	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton4() {
		if (jButton4 == null) {
			jButton4 = new JButton();
			jButton4.setText("Remove all");
		}
		return jButton4;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
