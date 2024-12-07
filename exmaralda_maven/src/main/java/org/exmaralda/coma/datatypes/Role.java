package org.exmaralda.coma.datatypes;

import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class Role extends ComaDatatype {

	String target; // attribute
	String type; // attribute
	Description description;

	public Role(Element e, Coma c) {
		super(e, c);
		datatype = "role";
		refresh();
	}

	public void refresh() {
		target = el.getAttributeValue("target");
		type = el.getAttributeValue("Type");
		if (el.getChild("Description") == null)
			el.addContent(new Element("Description"));
		description = (new Description(el.getChild("Description"), coma));
	}

	public String toString() {
		String s;
		try {
			s = "Role '"
					+ type
					+ "' in '"
					+ coma.getData().getElementById(target)
							.getAttributeValue("Name") + "'\n";
		} catch (Exception e) {
			s = "Role";
		}
		return s;
	}

	public String getTargetId() {
		return target;
	}

	public Description getDescription() {
		return description;
	}
	
	public String getType() {
		return type;
	}
}
