package org.exmaralda.sextant2;

public class SextantLaunch {

	private static SextantUi ui;
	private static SextantApp app;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		app = new SextantApp();
		ui = new SextantUi(app);
		app.setUi(ui);
		ui.setVisible(true);
	}

}
