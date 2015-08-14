package org.exmaralda.coma.helpers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class URIHelper {

	public static File getFile(File openFile, String uriString) {
		System.out.println("1:" + openFile + " ** " + uriString);
		URI uri;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e1) {
			System.out.println("NULL: keine URI");
			try {
				uri = new File(uriString).toURI();
			} catch (Exception e2) {
				System.out.println("NULL: keine URI");
				return null;
			}
		}
		File f;
		if (uri.isAbsolute()) {
			System.out.println("2: Absolut.");
			f = new File(uri);
		} else {
			System.out.println("2: Relativ.");
			URI u;
			try {
				u = new URI(openFile.getParentFile().toURI() + "" + uri);
				f = new File(u);
				System.out.println("3: Absolution: " + u);
			} catch (URISyntaxException e) {
				f = new File("");
				System.out.println("3: Absolution verweigert!");
			}
		}
		System.out.println("RESULT: " + f.getAbsolutePath() + ""
				+ (f.exists() ? "existiert" : "existiert nicht"));
		return (f.exists() ? f : null);

	}

	public static URI getURI(File openFile, String uriString) {
		System.out.println("1:" + openFile + " ** " + uriString);
		URI uri;
		try {
			uri = new URI(uriString);
		} catch (URISyntaxException e1) {
			System.out.println("NULL: keine URI");
			try {
				uri = new File(uriString).toURI();
			} catch (Exception e2) {
				System.out.println("NULL: keine URI");
				return null;
			}
		}
		return uri;
	}
}
