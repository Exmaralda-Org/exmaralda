package org.exmaralda.coma.root;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.exmaralda.coma.panels.PrefsPanel;

public class PrefsWindow extends JFrame {
	Coma coma;

	public PrefsWindow(Coma c) {
		coma = c;
		setLayout(new BorderLayout());
		// ImageIcon ii = IconFactory.createImageIcon("splash.png");
		setSize(coma.getSize());

		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(new JScrollPane(new PrefsPanel(coma)),
				BorderLayout.CENTER);
		setSize(new Dimension(coma.getWidth()/2,coma.getHeight()/2));
		setLocationRelativeTo(coma);
		setVisible(true);
	}

}
