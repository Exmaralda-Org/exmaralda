package org.exmaralda.coma.helpers;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.exmaralda.coma.importer.ExmaraldaPartitur;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.ComaData;
import org.jdom.Element;
import org.jdom.filter.ElementFilter;

public class transcriptionUpdater {

	private File comaFile;

	public void updateDescriptions(Coma coma) {
		ComaData data = coma.getData();
		comaFile = data.getOpenFile();
		Iterator asdf = data.getDocument().getDescendants(
				new ElementFilter("Transcription"));

		while (asdf.hasNext()) {
			Element element = (Element) asdf.next();
			if (element.getChild("Filename") != null) {
				if (new File(absolutized(element.getChild("NSLink").getText()))
						.exists()) {
					ExmaraldaPartitur myPartiture = new ExmaraldaPartitur(
							new File(absolutized(element.getChild("NSLink")
									.getText())), coma.prefs.getBoolean(
									"prefs.writeCIDsWhenUpdating", true));
					if (myPartiture.isSegmented()) {
						System.out.println(element.getChild("Filename")
								.getText());
						List descKeys;
						Element myKey;
						HashMap segments = myPartiture.getSegmentCount();
						Iterator sI = segments.entrySet().iterator();
						boolean set = false;
						while (sI.hasNext()) {
							Map.Entry entry = (Map.Entry) sI.next();
							Object key = entry.getKey();
							Object value = entry.getValue();
							System.out.println(key + "=" + value);

							// System.out.println(key+"="+value);
							descKeys = element.getParentElement().getChild(
									"Description").getChildren();
							Iterator keyI = descKeys.iterator();
							while (keyI.hasNext()) {
								myKey = (Element) keyI.next();
								if (myKey.getAttributeValue("Name") == "count_"
										+ key) {
									myKey.setText(value.toString());
									set = true;
								}
							}
							if (!set) {
								System.out.println("Einfügen!");
								Element newKey = new Element("Key");
								newKey.setAttribute("Name", "count_" + key);
								newKey.setText(value.toString());
								element.getChild("Description").addContent(
										newKey);
							}
							set = false;

						}
					}
				}
			}
		}

	}

	public String absolutized(String relPathString) {
		URI comaDocParent = comaFile.getParentFile().toURI();
		URI abs = comaDocParent.resolve(relPathString);
		return new File(abs).getAbsolutePath();
	}
}
