package org.exmaralda.sextanttagger.launch;

import java.io.File;

import javax.swing.UIManager;

import org.exmaralda.sextanttagger.application.TaggerApplication;
import org.exmaralda.sextanttagger.ui.TaggerUI;

public class SextantLauncher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getProperty("mrj.version") == null) { // windows
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception exc) {
				System.err.println("failed setting system look&feel");
			}
		} else { // mac
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name",
					"Sextant");
			try {
				UIManager
						.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
			} catch (Exception e) {
				System.err.println("failed setting system look&feel");
			}
		}

		TaggerApplication app = new TaggerApplication();
		TaggerUI frame = new TaggerUI(app);
		frame.setApp(app);
		app.setUi(frame);
		frame.setVisible(true);
		if (args.length > 0) {
			if (args[0].endsWith(".exa")) {
				if (new File(args[0]).exists()) {
					app.openTranscription(new File(args[0]));
				}
			}
		}
	}

}
