package org.exmaralda.aggronym.ui;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.swing.JFrame;

import org.exmaralda.aggronym.resources.ResourceHandler;

public class aggronymUI extends JFrame {
	static HashSet<String> dict;
	static HashSet<String> aggro;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		dict = new HashSet<String>();

		aggro = new HashSet<String>();
		if (args.length < 2) {
			System.out
					.println("Anwendung: aggro 'Text für das Akronym' akronymMaxLänge");
		}
		String text = args[0];
		int maxLength = new Integer(args[1]).intValue();
		System.out.println("Text:                   " + text);
		System.out.println("Minimale Akronym-Länge: 3");
		System.out.println("Maximale Akronym-Länge: " + maxLength);
		try {
			FileInputStream fstream = new FileInputStream(new File(
					new ResourceHandler().dict().toURI()));
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				dict.add((strLine.split("[{:\\[\\|(;]")[0]).toLowerCase());
			}
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		System.out.println("Wörter im Lexikon:      " + dict.size());
		System.out.println("Akronyme: ");

		text = removeSpaces(text);
		for (int i = 0; i < (text.length() - maxLength); i++) {
			for (int j = i + 1; j < (text.length() - maxLength)+1; j++) {
				for (int k = j + 1; k < (text.length() - maxLength)+2; k++) {
					aggro.add((text.charAt(i) + "" + text.charAt(j) + "" + text
							.charAt(k)).toLowerCase());

					for (int l = k + 1; l < (text.length() - maxLength)+3; l++) {
						aggro.add((text.charAt(i) + "" + text.charAt(j) + ""
								+ text.charAt(k) + "" + text.charAt(l))
								.toLowerCase());

					}

				}

			}
		}

		for (String s : aggro) {
			if (dict.contains(s)) {
				System.out.println("->"+s);
			}
		}
	}

	public static String removeSpaces(String s) {
		StringTokenizer st = new StringTokenizer(s, " ", false);
		String t = "";
		while (st.hasMoreElements())
			t += st.nextElement();
		return t;
	}

}
