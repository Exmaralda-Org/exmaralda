package org.exmaralda.coma.datatypes;

import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.exmaralda.coma.helpers.ComaHTML;
import org.exmaralda.coma.root.Coma;
import org.exmaralda.coma.root.Ui;
import org.jdom.Element;

public class Speaker extends ComaDatatype {

	private Element speaker;

	private String id = ""; // att

	private String sigle = "";

	private String pseudo = "";

	private String sex = ""; // xs:anySimpleType

	private Vector<Location> location = new Vector<Location>();

	private Description description;

	private Vector<Language> language = new Vector<Language>();

	private Vector<String> relatedPerson = new Vector<String>();

	private boolean okcancel;

	private Vector<Role> roles = new Vector<Role>();

	public Vector<Role> getRoles() {
		return roles;
	}

	public void setRoles(Vector<Role> roles) {
		this.roles = roles;
	}

	public HashMap<Role, Communication> getRoleTargets() {
		HashMap rt = new HashMap<Role, Communication>();
		for (Role r : roles) {
			System.out.println(r.getTargetId());
			rt.put(r,
					new Communication(coma.getData().getElementById(
							r.getTargetId()), coma));
		}
		return rt;
	}

	public Speaker(Coma c) {
		super(new Element("Speaker"), c);

	}

	public Speaker(Coma c, Element f, boolean link) { // link == true for
		// IDRef-Elements
		super(f, c);

		if (link) {
			if (coma.getSpeakerIndex().get(f.getText()) == null) {
				el = null;
				// geht nicht...
				// coma.dataPanel.deadSpeaker(f.getText());
			} else {
				el = new Element("Speaker");
				el = coma.getSpeakerIndex().get(f.getText());
				if (el.getChild("Id") == null) {
					// In einem Setting ist ein Sprecher verlinkt, der nicht
					// mehr existiert.
				}
			}
		}
		if (el != null) {
			id = el.getAttributeValue("Id");
			sigle = el.getChildText("Sigle");
			pseudo = el.getChildText("Pseudo");
			sex = el.getChildText("Sex");
			List elms = el.getChildren();
			Iterator i = elms.iterator();
			while (i.hasNext()) {
				Element elm = (Element) i.next();
				if (elm.getName().equals("Location")) {
					location.add(new Location(elm, coma));
				} else if (elm.getName().equals("Language")) {
					language.add(new Language(elm, coma));

				} else if (elm.getName().equals("RelatedPerson")) {
					relatedPerson.add(elm.getText());
				} else if (elm.getName().equals("Description")) {
					description = new Description(elm, coma);

				} else if (elm.getName().equals("role")) {
					roles.add(new Role(elm, coma));
				}
			}
		}
	}

	@Override
	public String toHTML(boolean onlyRows) {
		Collections.sort(location);
		String html = "<tr><td colspan='2' class='speaker'>Speaker: "
				+ ComaHTML.hilightSearchResult(sigle, coma.getData()
						.getSearchTerm())
				+ " ("

				+ "<a href=\"#\" id=\"column:"
				+ getXPath()
				+ "/Pseudo\">"

				+ (pseudo.length() > 0 ? ComaHTML.hilightSearchResult(pseudo,
						coma.getData().getSearchTerm()) : "no pseudo")
				+ "</a>, "

				+ "<a href=\"#\" id=\"column:"
				+ getXPath()
				+ "/Sex\">Sex</a>: "
				+ (sex.length() > 0 ? getFilterHTML("[Sex=\'" + sex + "\'", sex)
						: "") + ")" + getEditHTML() + "</td></tr>";
		html += (description != null ? description.toHTML(true) : "");
		html += ComaHTML.SEPARATOR;
		html += "<tr><td 'location' colspan='2'>"
				+ (location.size() > 0 ? location.size() : "No ")
				+ ((location.size() == 1) ? " Location" : " Locations")
				+ getAddHTML("Location") + "</td></tr>";

		for (Location l : location) {
			html += l.toHTML(true);
		}
		html += ComaHTML.SEPARATOR;

		html += "<tr><td class='newblock' colspan='2'>";

		switch (language.size()) {
		case 0:
			html += "No Languages";
			break;
		case 1:
			html += "Languages";
			break;
		default:
			html += language.size() + " Languages";
		}

		html += getAddHTML("Language") + "</td></tr>";
		for (Language l : language) {
			html += l.toHTML(true);
		}
		if (roles.size() > 0) {
			html += "<tr><td class='roles' colspan='2'>This speaker has roles assigned "
					+ "<a href=\"#\" id=\"show:"
					+ editableId
					+ "\">show</a>|"
					+ "<a href=\"#\" id=\"editroles:"
					+ editableId
					+ "\">edit</a></td></tr>";
		}
		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;
	}

	public String toHTML(Period ageDate, boolean onlyRows) {
		String html = "<tr><td colspan='2' class='speaker'>Speaker: " + sigle
				+ " (" + pseudo + ") " + getEditHTML() + "</td></tr>";
		html += (description != null ? description.toHTML(true) : "");
		html += ComaHTML.SEPARATOR;

		for (Location l : location) {

			html += (ageDate == null ? l.toHTML(true) : l.toHTML(true, ageDate));

		}
		html += ComaHTML.SEPARATOR;

		if (language.size() > 0) {
			html += "<tr><td class='newblock' colspan='2'>Language(s)</td></tr>";
		}
		for (Language l : language) {
			html += l.toHTML(true);
		}

		html = (!onlyRows) ? ComaHTML.tableWithStylesStart(html) : html;
		return html;

	}

	@Override
	public void edit() {
		boolean changed = false;
		final JDialog d = new JDialog(coma, true);
		d.getContentPane().setLayout(new GridLayout(0, 2));
		JTextField stf = new JTextField(sigle);
		JTextField ptf = new JTextField(pseudo);
		JTextField xtf = new JTextField(sex);
		d.getContentPane().add(new JLabel(Ui.getText("sigle")));
		d.getContentPane().add(stf);
		d.getContentPane().add(new JLabel(Ui.getText("pseudo")));
		d.getContentPane().add(ptf);
		d.getContentPane().add(
				new JLabel(Ui.getText("sex") + "(male/female/unknown) "));
		d.getContentPane().add(xtf);
		d.getContentPane().add(Box.createHorizontalGlue());
		d.getContentPane().add(Box.createHorizontalGlue());
		okcancel = false;
		JButton ob = new JButton(Ui.getText("OK"));
		ob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okcancel = true;
				d.dispose();
			}
		});

		JButton cb = new JButton(Ui.getText("cancel"));
		cb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				okcancel = false;
				d.dispose();
			}
		});
		d.getContentPane().add(ob);
		d.getContentPane().add(cb);
		d.pack();
		d.setLocation(MouseInfo.getPointerInfo().getLocation());
		d.setVisible(true);
		if (okcancel) {
			if (stf.getText().length() > 0 && !sigle.equals(stf.getText())) {
				el.getChild("Sigle").setText(stf.getText());
				sigle = stf.getText();
				changed = true;
			}
			if (ptf.getText().length() > 0 && !pseudo.equals(ptf.getText())) {
				el.getChild("Pseudo").setText(ptf.getText());
				pseudo = ptf.getText();
				changed = true;
			}

			if (xtf.getText().length() > 0 && !sex.equals(xtf.getText())) {
				el.getChild("Sex").setText(xtf.getText());
				sex = xtf.getText();
				changed = true;
			}
			if (changed) {

				coma.dataPanel.updateSelectedElement();
				coma.updateValueDisplay();
				coma.dataPanel.updateLists(true);
			}
		}
	}

	@Override
	public void add(String whichType) {
		if (whichType.equals("Language")) {
			Language.create(coma, this);
		}
		if (whichType.equals("Location")) {
			Location.create(coma, this);
		}
	}

	public Element toLinkElement() {
		Element e = new Element("Person");
		e.setText(id);
		return e;
	}

	public void showTargets() {
		HashSet<Element> commTargets = new HashSet<Element>();
		String resultText = "<html><table border=1><tr><th>type</th><th>target</th></tr>";
		for (Role r : roles) {
			commTargets.add(coma.getData().getElementById(r.target));
			resultText += "<tr><td>" + r.type + "</td>";
			resultText += "<td>" + r.target + "</td></tr>";
			System.out.println(r);
		}
		coma.showInfoDialog(resultText + "</table></html>");
		coma.dataPanel.setSelectionFromElements(commTargets);
	}

	public String getRoleCount() {
		return "" + roles.size();
	}

	public String getPseudo() {
		return pseudo;
	}

	public void removeRole(Role rl) {
		System.out.println(speaker);
		System.out.println(rl);
		System.out.println(rl.getElement());
		el.removeContent(rl.getElement());
		roles.remove(rl);
	}

}
