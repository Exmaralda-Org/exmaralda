package org.exmaralda.coma.dialogs;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

class SplashPanel extends JPanel implements ActionListener {
	private final JFrame owner;

	SplashPanel(final JFrame owner) {
		this.owner = owner;
		setOpaque(false);
		setBackground(new Color(100, 100, 100, 200));
		JButton btClose = new JButton("Close");
		add(btClose);
		setSize(200, 150);
		JLayeredPane layeredPane = owner.getLayeredPane();
		layeredPane.add(this, JLayeredPane.POPUP_LAYER);
		setVisible(false);
		btClose.addActionListener(this);
		owner.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				setVisible(isVisible());
			}
		});
	}

	@Override
	protected void paintComponent(final Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void actionPerformed(final ActionEvent e) {
		setVisible(false);
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (isVisible()) {
			int wO = owner.getWidth();
			int hO = owner.getHeight() - 30;
			setLocation((wO - getWidth()) / 2, (hO - getHeight()) / 2);
		}
	}
}