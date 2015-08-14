/*
 * Created on 06.02.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import org.exmaralda.coma.actions.AboutAction;
import org.exmaralda.coma.actions.ClearRecentFileListAction;
import org.exmaralda.coma.actions.CopyDebugInfoAction;
import org.exmaralda.coma.actions.CreateCorpusFromTranscriptionsAction;
import org.exmaralda.coma.actions.DumpCorpusAction;
import org.exmaralda.coma.actions.HarmonizeDescriptionKeysAction;
import org.exmaralda.coma.actions.ImportBasicTranscriptionAction;
import org.exmaralda.coma.actions.ImportSpeakerAction;
import org.exmaralda.coma.actions.IntroduceRolesAction;
import org.exmaralda.coma.actions.MergeComaDocumentAction;
import org.exmaralda.coma.actions.NewAction;
import org.exmaralda.coma.actions.OpenAction;
import org.exmaralda.coma.actions.OpenTemplatesAction;
import org.exmaralda.coma.actions.OutputAction;
import org.exmaralda.coma.actions.PrefsAction;
import org.exmaralda.coma.actions.QuitAction;
import org.exmaralda.coma.actions.RefreshTranscriptionStatsAction;
import org.exmaralda.coma.actions.SaveAction;
import org.exmaralda.coma.actions.SaveBasketAction;
import org.exmaralda.coma.actions.SaveTemplatesAction;
import org.exmaralda.coma.actions.TranscriptionSearchAndReplaceAction;
import org.exmaralda.coma.actions.TreeTaggerAction;
import org.exmaralda.coma.actions.UpdateCheckAction;
import org.exmaralda.coma.actions.UpdateRecordingsAction;

/**
 * coma2/org.sfb538.coma2.toolbars/FileBar.java
 * 
 * @author woerner
 */
public class ComaMenuBar extends JMenuBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Coma coma;
	JMenu fileMenu;

	public static Preferences prefs = Ui.prefs;

	public javax.swing.AbstractAction newAction;
	private JMenu editMenu;

	public ComaMenuBar(Coma c) {
		coma = c;
		JMenu fileMenu = new JMenu(Ui.getText("menu.fileMenu"));

		// VIEW MENU
		JMenu viewmenu = new JMenu(Ui.getText("menu.viewMenu"));
		JCheckBoxMenuItem showSpeakers = new JCheckBoxMenuItem(
				Ui.getText("menu.viewMenu.showSpeakers"));
		showSpeakers.setSelected(prefs.getBoolean("menu.viewMenu.showSpeakers",
				false));
		showSpeakers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("menu.viewMenu.showSpeakers",
						((JCheckBoxMenuItem) e.getSource()).isSelected());
				coma.refreshDisplay(true);
			}
		});
		viewmenu.add(showSpeakers);
		
		
		JCheckBoxMenuItem showMillis = new JCheckBoxMenuItem(
				Ui.getText("menu.viewMenu.showMillis"));
		showMillis.setSelected(prefs.getBoolean("menu.viewMenu.showSpeakers",
				false));
		showMillis.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("menu.viewMenu.showMillis",
						((JCheckBoxMenuItem) e.getSource()).isSelected());
				coma.refreshDisplay(true);
			}
		});
		viewmenu.add(showMillis);
		
		
		
		JCheckBoxMenuItem showMachineTags = new JCheckBoxMenuItem(
				Ui.getText("menu.viewMenu.showMachineTags"));
		showMachineTags.setSelected(prefs.getBoolean(
				"menu.viewMenu.showMachineTags", false));
		showMachineTags.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("menu.viewMenu.showMachineTags",
						((JCheckBoxMenuItem) e.getSource()).isSelected());
				coma.refreshDisplay(true);

			}
		});
		viewmenu.add(showMachineTags);

		JCheckBoxMenuItem showLangNames = new JCheckBoxMenuItem(
				Ui.getText("menu.viewMenu.showLangNames"));
		showLangNames.setSelected(prefs.getBoolean(
				"menu.viewMenu.showLangNames", false));
		showLangNames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("menu.viewMenu.showLangNames",
						((JCheckBoxMenuItem) e.getSource()).isSelected());
			}
		});
		viewmenu.add(showLangNames);

		// a group of radio button menu items
		viewmenu.addSeparator();
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem colored = new JRadioButtonMenuItem(
				Ui.getText("menu.viewMenu.colored"));
		colored.setSelected(prefs.get("menu.viewMenu.colorMode", "colored")
				.equals("colored"));
		colored.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.put("menu.viewMenu.colorMode", "colored");
				coma.refreshDisplay(true);
			}
		});
		group.add(colored);
		viewmenu.add(colored);

		JRadioButtonMenuItem bw = new JRadioButtonMenuItem(
				Ui.getText("menu.viewMenu.bw"));
		bw.setSelected(!prefs.get("menu.viewMenu.colorMode", "colored").equals(
				"colored"));
		bw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.put("menu.viewMenu.colorMode", "bw");
				coma.refreshDisplay(true);
			}
		});
		group.add(bw);
		viewmenu.add(bw);
		// MAINTENANCE MENU
		JMenu maintenanceMenu = new JMenu(Ui.getText("menu.maintenanceMenu"));
		maintenanceMenu.add(new JLabel("<html><b>"
				+ Ui.getText("transcriptions") + "</b></html>"));
		maintenanceMenu
				.add(new org.exmaralda.coma.actions.SegmentTranscriptionAction(
						Ui.getText("cmd.segmentTranscriptions"), null, coma));
		maintenanceMenu
				.add(new org.exmaralda.coma.actions.CheckStructureErrorsAction(
						Ui.getText("cmd.checkStructureErrors"), null, coma));
		maintenanceMenu
				.add(new org.exmaralda.coma.actions.CheckSegmentationErrorsAction(
						Ui.getText("cmd.checkSegmentationErrors"), null, coma));

		maintenanceMenu.add(new org.exmaralda.coma.actions.CheckSpeakersAction(
				Ui.getText("cmd.checkSpeakers"), null, coma));

		maintenanceMenu.add(new TranscriptionSearchAndReplaceAction(coma));
		maintenanceMenu.addSeparator();
		maintenanceMenu.add(new JLabel("<html><b>" + Ui.getText("metadata")
				+ "</b></html>"));
		maintenanceMenu.add(new RefreshTranscriptionStatsAction(coma));
		maintenanceMenu.add(new UpdateRecordingsAction(coma));// .setEnabled(false);
		maintenanceMenu.add(new HarmonizeDescriptionKeysAction(coma));
		maintenanceMenu.add(new DumpCorpusAction(coma));

		// maintenanceMenu.add(new UpdateTranscriptionDescriptionsAction(coma));
		// einmal sollte ja vielleicht reichen!

		// ANALYSIS MENU
		JMenu analysisMenu = new JMenu(Ui.getText("menu.analysisMenu"));
		analysisMenu.add(new org.exmaralda.coma.actions.ExaktSearchAction(Ui
				.getText("cmd.searchInExakt"), null, coma));
		analysisMenu.add(new org.exmaralda.coma.actions.CorpusStatisticsAction(
				Ui.getText("cmd.corpusStatistics"), null, coma));
		analysisMenu.add(new org.exmaralda.coma.actions.CorpusWordListAction(Ui
				.getText("cmd.wordList"), null, coma));
		analysisMenu.add(new org.exmaralda.coma.actions.SpeakerSpecificWordListAction(Ui
				.getText("cmd.speakerSpecificWordList"), null, coma));

		// TOOLS MENU
		JMenu toolsmenu = new JMenu(Ui.getText("name.toolsMenu"));
		toolsmenu.add(new TreeTaggerAction(Ui.getText("cmd.treeTag"), null,
				coma));
/*		ROLES!
 * 		toolsmenu.add(new IntroduceRolesAction(coma));
 */
		// toolsmenu.add(new DumpCorpusAction(coma));

		// ATTENTION: added by the evil TS!!!!!
		// toolsmenu.add(new org.exmaralda.coma.actions.ExaktSearchAction(Ui
		// .getText("cmd.searchInExakt"), null, coma));
		// toolsmenu
		// .add(new org.exmaralda.coma.actions.CheckStructureErrorsAction(
		// Ui.getText("cmd.checkStructureErrors"), null, coma));
		// toolsmenu
		// .add(new org.exmaralda.coma.actions.CheckSegmentationErrorsAction(
		// Ui.getText("cmd.checkSegmentationErrors"), null, coma));
		// toolsmenu.add(new
		// org.exmaralda.coma.actions.CorpusStatisticsAction(Ui
		// .getText("cmd.corpusStatistics"), null, coma));
		// END: addition by the evil TS
		//
		// toolsmenu.add(new RefreshTranscriptionStatsAction(coma));
		// toolsmenu.addSeparator();
		// toolsmenu.add(new HarmonizeDescriptionKeysAction(coma));
		// toolsmenu.add(new HarmonizeDescriptionValuesAction(coma));
		JMenu helpmenu = new JMenu(Ui.getText("name.helpMenu"));
		helpmenu.add(new HelpAction(coma));
		helpmenu.add(new UpdateCheckAction(coma));
		helpmenu.add(new CopyDebugInfoAction(coma));

		helpmenu.addSeparator();
		helpmenu.add(new AboutAction(coma));
//		helpmenu.add(new EasterEggAction(coma));
		Ui.getText("");

		// aboutness
		add(getFileMenu());
		add(getEditMenu());
		add(viewmenu);
		add(toolsmenu);
		add(analysisMenu);
		add(maintenanceMenu);
		add(helpmenu);
	}

	private JMenu getEditMenu() {
		if (this.editMenu == null) {
			this.editMenu = new JMenu(Ui.getText("name.editMenu"));
		}
		editMenu.removeAll();
		editMenu.add(new PrefsAction(coma));
		return editMenu;
	}

	private JMenu getFileMenu() {
		if (this.fileMenu == null) {
			this.fileMenu = new JMenu(Ui.getText("name.fileMenu"));
		}
		fileMenu.removeAll();
		fileMenu.add(new NewAction(coma));
		fileMenu.add(new OpenAction(coma));
		fileMenu.add(new SaveAction(coma));
		fileMenu.add(new SaveAction(coma, true));
		fileMenu.addSeparator();
		File of = new File("");
		int count = 0;
		if (coma.getData().getRecentFiles().size() > 0) {
			for (File f : coma.getData().getRecentFiles()) {
				if (!f.equals(of)) {
					fileMenu.add(new OpenAction(coma, f)).setToolTipText(
							f.getAbsolutePath());
					count++;
				}
				of = f;
			}
		}
		if (count > 0) {
			fileMenu.add(new ClearRecentFileListAction(coma));
			fileMenu.addSeparator();
		}
		fileMenu.add(new SaveBasketAction(coma));
		fileMenu.addSeparator();
		fileMenu.add(new OpenTemplatesAction(coma));
		fileMenu.add(new SaveTemplatesAction(coma));
		fileMenu.add(new SaveTemplatesAction(coma, true));
		JCheckBoxMenuItem autoload = new JCheckBoxMenuItem(
				Ui.getText("cmd.autoLoadTemplates")
						+ ": "
						+ ((new File(prefs.get("defaultTemplatesFile", ""))
								.exists()) ? new File(prefs.get(
								"defaultTemplatesFile", "")).getName() : ""));
		autoload.setSelected(prefs.getBoolean("cmd.autoLoadTemplates", false));
		autoload.setEnabled(new File(prefs.get("defaultTemplatesFile", ""))
				.exists());
		autoload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prefs.putBoolean("cmd.autoLoadTemplates",
						((JCheckBoxMenuItem) e.getSource()).isSelected());
			}
		});
		fileMenu.add(autoload);
		fileMenu.addSeparator();
		fileMenu.add(new MergeComaDocumentAction(coma));
		fileMenu.add(new ImportBasicTranscriptionAction(coma));
		fileMenu.add(new ImportSpeakerAction(coma));
		fileMenu.addSeparator();
		fileMenu.add(new CreateCorpusFromTranscriptionsAction(coma));
		fileMenu.addSeparator();
		fileMenu.add(new OutputAction(coma));
		fileMenu.addSeparator();
		fileMenu.add(new QuitAction(coma));
		return fileMenu;
	}

	public void updateFileMenu() {
		fileMenu = getFileMenu();
		this.repaint();
		this.revalidate();

	}
}
