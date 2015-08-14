/**
 *
 */
package org.exmaralda.teide.xml;

import java.io.File;

import org.exmaralda.teide.ui.TeideException;

/**
 * @author woerner
 *
 */
public class Transformer {

	public Transformer() {
		super();
	}

	public String convertToHTML(File tf, File inputFile) {
		String html = "";
		StylesheetFactory sf = new StylesheetFactory(true);
		try {
			if (tf.exists()) {
				try {
					html = sf.applyExternalStylesheetToExternalXMLFile(tf
							.getAbsolutePath(), inputFile.getPath());
				} catch (Exception e) {
					html = "<html><body><pre>" + e.getLocalizedMessage()
							+ "</pre></body></html>";
					System.out.println(e.getLocalizedMessage());
					throw new TeideException(e.getLocalizedMessage());
				}
			} else {
				html = sf.applyInternalStylesheetToExternalXMLFile(
						"/org/exmaralda/teide/resources/teiHeader.xsl",
						inputFile.getPath());
			}
			//			status(STATUS_OK);

		} catch (Exception err) {
			System.out.println(err.getLocalizedMessage());
			//error compiling stylesheet
			return "";
			/*			throw (new TeideException(
								inputFile.getPath()
										+ "</h2><p style='background-color: #ffff00;'>Failed to compile the stylesheet "
										+ tf.getName())); */
		}
		return html;
	}

}
