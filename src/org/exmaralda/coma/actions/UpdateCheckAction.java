/*
 * Created on 11.02.2005 by woerner
 */
package org.exmaralda.coma.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.HashMap;

import org.exmaralda.coma.dialogs.UpdateDialogPane;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaAction;
import org.exmaralda.coma.root.JarClassLoader;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

/**
 * coma2/org.sfb538.coma2.actions/UpdateCheckAction.java
 * @author woerner
 * 
 */
public class UpdateCheckAction extends ComaAction {
	private Element comaVersions;

	private Element stable;

	private HashMap version;

	private HashMap allVersions;

	public UpdateCheckAction(Coma c, javax.swing.ImageIcon icon) {
		super(Ui.getText("cmd.updateCheck"), icon, c);
	}

	public UpdateCheckAction(Coma c) {
		super(Ui.getText("cmd.updateCheck"), c);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		UpdateDialogPane udp = new UpdateDialogPane(coma, coma.getVersion());
		udp.setLocationRelativeTo(coma);
		udp.setVisible(true);

	}
@Deprecated
	
	private void updateComa(String dialogURL) {
		File jarFile = null;

		// get the location of the running jar

		ProtectionDomain pDomain = coma.getClass().getProtectionDomain();
		CodeSource codeSource = pDomain.getCodeSource();
		if (codeSource == null)
			System.out.println("ASDF!");
		URL loc = codeSource.getLocation();
		File jarDir = new File(loc.getFile()).getParentFile(); // dir to work in

		// load updated jar file

		try {
			URL jarURL = new URL(dialogURL);
			URLConnection connection = jarURL.openConnection();
			jarFile = new File(jarDir + "/ComaUpdate.jar");
			jarFile.createNewFile();
			FileOutputStream jarWriter = new FileOutputStream(jarFile);
			InputStream is = connection.getInputStream();
			byte[] buf = new byte[1028];
			int i = 0;
			while ((i = is.read(buf)) != -1)
				jarWriter.write(buf, 0, i);

			is.close();
			jarWriter.close();

			// load the exmaralda-updater jar file

			jarURL = new URL(
					"http://www1.uni-hamburg.de/exmaralda/Daten/2D-Download/Zubehoer/updater.jar");
			connection = jarURL.openConnection();
			jarFile = new File(jarDir + "/updater.jar");
			jarFile.createNewFile();
			jarWriter = new FileOutputStream(jarFile);
			is = connection.getInputStream();
			buf = new byte[1028];
			i = 0;
			while ((i = is.read(buf)) != -1)
				jarWriter.write(buf, 0, i);

			is.close();
			jarWriter.close();

		} catch (IOException ioe) {
			System.out.println("Error Reading from connection!!!");
			ioe.printStackTrace();
		}

		// invoke updater and close program
		try {
			URL pathToUpdater = jarFile.toURI().toURL();
			JarClassLoader cl = new JarClassLoader(pathToUpdater);
			String name = null;
			try {
				name = cl.getMainClassName();
			} catch (IOException e) {
				System.err.println("I/O error while loading JAR file:");
				e.printStackTrace();
			}
			if (name == null) {
				coma
						.status("Specified jar file does not contain a 'Main-Class'"
								+ " manifest attribute");
			}
			// Get arguments for the updater

			String[] newArgs = { loc.toString(), jarDir + "/ComaUpdate.jar",
					jarDir + "/updater.jar" };
			// Invoke application's main class
			try {
				cl.invokeClass(name, newArgs);
			} catch (ClassNotFoundException e) {
				coma.status("Class not found: " + name);
			} catch (NoSuchMethodException e) {
				coma.status("Class does not define a 'main' method: " + name);
			} catch (InvocationTargetException e) {
				e.getTargetException().printStackTrace();
				System.exit(1);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}


}