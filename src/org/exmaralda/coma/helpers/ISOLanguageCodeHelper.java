package org.exmaralda.coma.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import org.exmaralda.coma.resources.ResourceHandler;

public class ISOLanguageCodeHelper {
	private static HashMap<String, String> languageNames;
	private static HashMap<String, String> languageCodes;

	/**
	 * @param args
	 */
	static {
		String line;
		languageNames = new HashMap<String, String>();
		languageCodes = new HashMap<String, String>();
		BufferedReader input = new BufferedReader(new InputStreamReader(
				new ResourceHandler().languageCodes()));
		try {
			while ((line = input.readLine()) != null) {
				String[] cols = line.split("\\t");
				Vector<String> colVector = new Vector<String>(Arrays
						.asList(cols));
				if (colVector.size() == 7)
					colVector.add("");
				// 0 = Code, 6 = Name
				languageNames.put(colVector.get(6), colVector.get(0));
				languageCodes.put(colVector.get(0), colVector.get(6));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Vector<String> getLanguageNames() {
		return new Vector<String>(languageNames.keySet());
	}

	public static String getLanguageCode(String name) {
		return (languageNames.get(name) == null ? "???" : languageNames
				.get(name));
	}

	public static String getLanguageName(String code) {
		return (languageCodes.get(code) == null ? "" : languageCodes.get(code));
	}

}
