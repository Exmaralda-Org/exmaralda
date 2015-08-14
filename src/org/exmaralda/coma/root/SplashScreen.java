package org.exmaralda.coma.root;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import mpi.eudico.util.ErrOutLogFileHandler;

public class SplashScreen extends JPanel implements MouseListener {
	Coma coma;

	public SplashScreen(Coma c, boolean useTimer) {
		coma = c;
		try {
			setOpaque(false);
		} catch (IllegalArgumentException e) {
//			LXDE can't handle transparency...
			e.printStackTrace();
			System.err.println("LXDE, eh?");
		}
		setLayout(new BorderLayout());
		ImageIcon ii = IconFactory.createImageIcon("splash.png");
		setSize(coma.getSize());
		setLocation(0, 0);
		JLabel l = new JLabel(ii);
		addMouseListener(this);
		add(l, BorderLayout.CENTER);
		JLayeredPane layeredPane = c.getLayeredPane();
		layeredPane.add(this, JLayeredPane.POPUP_LAYER);
		if (useTimer) {
			Timer timer = new Timer(2000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					close();
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
		setVisible(true);
	}

	protected void close() {
		this.setVisible(false);
		coma.getLayeredPane().remove(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		close();
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
