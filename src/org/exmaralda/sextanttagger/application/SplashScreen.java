package org.exmaralda.sextanttagger.application;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.exmaralda.sextanttagger.resources.ResourceHandler;


public class SplashScreen extends JPanel implements MouseListener {
	Container cont;

	public SplashScreen(Container c, boolean autoHide) {
		cont = c;
		setOpaque(false);
		setLayout(new BorderLayout());

		ImageIcon ii = new ImageIcon(
				new ResourceHandler().image("splashscreen.png"));
		setSize(cont.getSize());
		setLocation(0, 0);
		JLabel l = new JLabel(ii);
		addMouseListener(this);
		add(l, BorderLayout.CENTER);
		JLayeredPane layeredPane = ((JFrame) c).getLayeredPane();
		layeredPane.add(this, JLayeredPane.POPUP_LAYER);
		setVisible(true);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		this.setVisible(false);
		((JFrame) cont).getLayeredPane().remove(this);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

	}
}
