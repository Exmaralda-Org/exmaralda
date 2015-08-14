/**
 * 
 */
package org.exmaralda.teide.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

/**
 * @author woerner
 *
 */
public class TeideFileWriter {

	private File file;

	public TeideFileWriter(File f) {
		file = f;
	}

	public void write(String s) {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "UTF8"));
			out.write(s);
			out.close();
		} catch (UnsupportedEncodingException e) {
			System.out.println("Konnte Datei nicht erstellen (Encodingfehler)");
		} catch (IOException e) {
			System.out.println("Konnte Datei nicht erstellen");
		}

	}

	public File selectFile(JComponent window) {
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(file);
		chooser.showSaveDialog(window);
		File f = chooser.getSelectedFile();
		file = f;
		return file;
	}

}
