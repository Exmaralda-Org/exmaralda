package org.exmaralda.coma.datatypes;

//<xs:complexType name="AvailabilityType">
//
//	<xs:annotation>
//<xs:documentation>type for storing copyright/obtaining data</xs:documentation>
//</xs:annotation>
//
//	<xs:sequence>
//<xs:element name="Available" type="xs:boolean"/>
//<xs:element name="URL" type="xs:anyURI" minOccurs="0" maxOccurs="unbounded"/>
//<xs:element name="Copyright" type="xs:string" minOccurs="0"/>
//<xs:element name="ObtainingInformation" type="DescriptionType" minOccurs="0"/>
//</xs:sequence>
//</xs:complexType>

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.jdom.Element;

public class Availability extends ComaDatatype {
	boolean available;

	Vector<URL> url = new Vector<URL>();

	String copyright;

	Description obtainingInformation;

	public Availability(Element e, Coma c) {
		super(e, c);
		datatype = "Availability";
		refresh();
	}

	@Override
	public void refresh() {
		if (el.getChild("ObtainingInformation") == null) {
			//
		} else {
			obtainingInformation = new Description(el
					.getChild("ObtainingInformation"), coma);
		}
		List<Element> chs = el.getChildren();
		for (Element myElm : chs) {
			if (myElm.getName().equals("URL"))
				try {
					url.add(new URL(myElm.getText()));
				} catch (MalformedURLException err) {
					err.printStackTrace();
				}

			if (myElm.getName() == "Copyright")
				copyright = myElm.getText();
			if (myElm.getName() == "Available")
				available = (myElm.getText().equals("true") ? true : false);

		}

	}

	public Element toElement() {
		Element e = new Element("Availability");
		e.addContent(new Element("Available").setText((available ? "true"
				: "false")));
		e.addContent(new Element("Copyright").setText(copyright));
		return null;
	}

	public String toHTML(boolean onlyRows) {
		String html = "<tr><td class='newblock' colspan='2'>Transcription "
				+ (available ? "" : "un")
				+ "available</td></tr>";
		if (copyright!=null) {
			html += ("<tr><td>Copyright:</td><td>" + copyright + "</td></tr>");

		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

}
