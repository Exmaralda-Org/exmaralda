package org.exmaralda.common.helpers;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.exmaralda.common.ExmaraldaApplication;

@Deprecated
public class FileSelector {
	public static File chooseFile(Frame parent, final String title,
			File startDir, File selectedFile, boolean save,
			final Map<String, String> filetypes, ExmaraldaApplication app) {
		final boolean isMac = (System.getProperty("mrj.version") != null);
		File file = null;
		if (isMac) {
			FileDialog dialog = new FileDialog(parent, title,
					(save ? FileDialog.SAVE : FileDialog.LOAD));
			if (startDir != null) {
				dialog.setDirectory(startDir.getPath());
			}
			if (app.getPreferencesNode() != null) {
				dialog.setDirectory(Preferences.userRoot().node(app.getPreferencesNode())
						.get("recentDir", null));
			}
			dialog.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return true;
					// return (filetypes
					// .contains(name.toLowerCase().split("\\.")[name
					// .toLowerCase().split("\\.").length]));
				}
			});
			dialog.setVisible(true);
			if (dialog.getFile() != null) {
				file = new File(dialog.getDirectory() + dialog.getFile());
			}

		} else {
			JFileChooser fc = new JFileChooser();
			for (String extension : filetypes.keySet()) {
				fc.addChoosableFileFilter(new ExmaraldaFileFilter(filetypes
						.get(extension), new String[] { extension }, true));
			}
			int dialogStatus = fc.showOpenDialog(parent);
			if (dialogStatus == 0) {
				file = fc.getSelectedFile();
			}
		}
		if (file != null) {
			Preferences.userRoot().node(app.getPreferencesNode()).put("recentDir",
					file.getParentFile().getAbsolutePath());
		}
		return file;
	}

	public static Map<String, String> getComaFileMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("coma", "Coma-Files");
		return map;
	}
}
