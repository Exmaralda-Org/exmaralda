package org.exmaralda.coma.datatypes;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.exmaralda.coma.root.Coma;
import org.jdom.Content;
import org.jdom.Element;

public class Setting extends ComaDatatype {
	private Vector<Speaker> speaker = new Vector<Speaker>();

	Description description;

	public Setting(Element e, Coma c) {
		super(e, c);
		description = (e.getChild("Description") != null) ? new Description(e
				.getChild("Description"), coma) : new Description(coma, this);
		List spks = e.getChildren("Person");
		Iterator i = spks.iterator();
		while (i.hasNext()) {
			speaker.add(new Speaker(coma, (Element) i.next(), true));
		}

	}

	public Content toElement() {
		Element e = new Element("Setting");
		return null;
	}

	public String toHTML(boolean rowsOnly, boolean withSpeakers) {

		String html = "<tr><td class='newblock' colspan='2'><b>Setting</b></td></tr>";
		/*
		 * for (Map.Entry m : key.entrySet() {
		 * html+="<tr><td>"+m.getKey().toString
		 * ()+"</td><td>"+m.getValue().toString()+"</td></tr>"; }
		 */
		html += description.toHTML(true);
		if (!rowsOnly) {
			html = "<table width='100%' border='1'>" + html + "</table>";
		}
		return html;
	}

	public Vector<Speaker> getSpeakers() {
		return speaker;
	}


}
