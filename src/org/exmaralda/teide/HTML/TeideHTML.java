/**
 *
 */
package org.exmaralda.teide.HTML;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.exmaralda.teide.Teide;

/**
 * @author woerner
 *
 */
public class TeideHTML {
	public static final String DATAROW1_COLOR = "#CCCCCC";

	public static final String DATAROW2_COLOR = "#EEEEEE";

	public static final String DATAROW3_COLOR = "#DDDDDD";

	public static final String COMMUNICATION_COLOR = "#547EA7";

	public static final String SPEAKER_COLOR = "#A75135";

	public static final String HEADER_COLOR = "#999999";

	public static final String DESCRIPTIONHEAD_COLOR = "#DFDFDF";

	public static final String scriptString() {
		String javascript = "";
		InputStream in = new BufferedInputStream(Teide.class
				.getResourceAsStream("HTML/javascript.txt"));

		String line;
		while ((line = readLine(in)) != null) {
			javascript += line;
		}
		return javascript;
	}

	public static final String styleString() {
		String style = "";
		InputStream in = new BufferedInputStream(Teide.class
				.getResourceAsStream("HTML/css.txt"));

		String line;
		while ((line = readLine(in)) != null) {
			style += line;
		}
		return style;
	}

	public static String readLine(InputStream in) {
		StringBuffer buf = new StringBuffer();

		int r;
		try {
			while ((r = in.read()) != -1) {
				if (r == '\r')
					continue;
				else if (r == '\n')
					break;
				else
					buf.append((char) r);
			}
		} catch (IOException err) {
			return "";
		}

		return r == -1 && buf.length() == 0 ? null : buf.toString();
	}
}
