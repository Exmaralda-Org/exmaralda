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
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**
 * @author woerner
 * 
 */
public class TeideFTPWriter {

	private File file;

	public TeideFTPWriter(File f) {
		file = f;
	}

	public void write(String s) {
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
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

	public void ftpFiles(File rootDir, URL url, String login, String password) {
		FTPClient ftp = new FTPClient();
		boolean error = false;
		try {
			int reply;
			ftp.connect("ftp.foobar.com");
			//   System.out.println("Connected to " + server + ".");
			System.out.print(ftp.getReplyString());

			// After connection attempt, you should check the reply code to verify
			// success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				System.err.println("FTP server refused connection.");
				System.exit(1);
			}
			// transfer files
			ftp.logout();
		} catch (IOException e) {
			error = true;
			e.printStackTrace();
		} finally {
			if (ftp.isConnected()) {
				try {
					ftp.disconnect();
				} catch (IOException ioe) {
					// do nothing
				}
			}
			System.exit(error ? 1 : 0);
		}
	}

}
