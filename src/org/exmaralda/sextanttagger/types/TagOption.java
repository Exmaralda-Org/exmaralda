package org.exmaralda.sextanttagger.types;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.jdom.Element;

public class TagOption implements Comparable<TagOption> {
	public static final int SYMBOL = 0;
	public static final int STRING = 1;

	private int index;
	private String key;
	private String value;
	private String shortcut;
	private int type;
	private Color color;

	public TagOption(Element fs, boolean isStructure) {
		this();
		for (Element f : (List<Element>) fs.getChildren("f")) {
			if (f.getAttributeValue("name").equals("sextantShortcut")) {
				shortcut = f.getChild("symbol").getAttributeValue("value");
			} else if (f.getAttributeValue("name").equals("sextantColor")) {
				color = new Color(new Integer(f.getChild("symbol")
						.getAttributeValue("value")));
			} else {
				key = f.getAttributeValue("name");
				if (((Element) f.getChildren().get(0)).getName().equals(
						"symbol")) {
					type = SYMBOL;
				} else {
					type = STRING;
				}
				value = ((Element) f.getChildren().get(0))
						.getAttributeValue("value");
			}
		}
	}

	public TagOption(Element f) {
		this();
		if (f.getAttributeValue("name").equals("sextantShortcut")) {
			shortcut = f.getChild("symbol").getAttributeValue("value");
		} else if (f.getAttributeValue("name").equals("sextantColor")) {
			color = new Color(new Integer(f.getChild("symbol")
					.getAttributeValue("value")));
		} else {
			key = f.getAttributeValue("name");
			if (((Element) f.getChildren().get(0)).getName().equals("symbol")) {
				type = SYMBOL;
			} else {
				type = STRING;
			}
			value = ((Element) f.getChildren().get(0))
					.getAttributeValue("value");
		}

	}

	public TagOption(String k, String v, Color c, String s, int t) {
		this();
		key = k;
		shortcut = s;
		value = v;
		type = t;
		color = c;
	}

	public TagOption() {
		index = 0;
		key = "";
		shortcut = "0;0";
		value = "";
		type = SYMBOL;
		color = new Color(Color.HSBtoRGB(new Random().nextFloat(), 1, 1));
	}

	public TagOption(String k, String v) {
		this();
		key = k;
		value = v;
	}

	public int getIndex() {
		return index;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color newColor) {
		color = newColor;
	}

	public void setShortcut(String string2) {
		shortcut = string2;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getShortcut() {
		return shortcut;
	}

	public int getType() {
		return type;
	}

	public void setValue(String text) {
		value = text;
	}

	public void setKey(String text) {
		key = text;
	}

	@Override
	public String toString() {
		String ts = "";
		ts += "Option: " + key + "=" + value;
		return ts;
	}

	@Override
	public int compareTo(TagOption o) {
		return (this.getKey() + "=" + this.getValue()).compareTo(o.getKey()
				+ "=" + o.getValue());
	}

	@Override
	public boolean equals(Object other) {
		boolean result = false;
		if (other instanceof TagOption) {
			TagOption that = (TagOption) other;
			result = (that.canEqual(this) && (this.getKey() + "=" + this
					.getValue()).equals(that.getKey() + "=" + that.getValue()));
		}
		return result;
	}

	@Override
	public int hashCode() {
		return ((this.getKey() + "=" + this.getValue()).hashCode());
	}

	public boolean canEqual(Object other) {
		return (other instanceof TagOption);
	}

}
