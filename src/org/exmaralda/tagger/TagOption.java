/*
 * Created on 07.03.2005 by woerner
 */
package org.exmaralda.tagger;

import java.util.Vector;
import org.jdom.Element;

/**
 * Ludger/z2tagger/TagOption.java
 * @author woerner
 * 
 */
public class TagOption {
	private String attName;
	private String newVal;
	private String replaceVal;
	private String key;
	private boolean replace;
	public TagOption() {
		newVal = replaceVal = attName = key = "";
		replace = false;
	}
	/**
	 * @param element
	 */
	public TagOption(Element element) {
		newVal = element.getAttributeValue("newVal");
		replaceVal = element.getAttributeValue("replaceVal");
		attName = element.getAttributeValue("att");
		key = element.getAttributeValue("key");
		replace = (element.getAttributeValue("replace").equals("true") ? true
				: false);
	}
	
	public TagOption(String aN, String rV, String nV, String k, boolean r) {
		attName =  aN;
		replaceVal = rV;
		newVal = nV;
		key = k;
		replace = r; 
	}
	/**
	 * @return Returns the add.
	 */
	public boolean isReplace() {
		return replace;
	}
	/**
	 * @param add The add to set.
	 */
	public void setAdd(boolean replace) {
		this.replace = replace;
	}
	/**
	 * @return Returns the attName.
	 */
	public String getAttName() {
		return attName;
	}
	/**
	 * @param attName The attName to set.
	 */
	public void setAttName(String attName) {
		this.attName = attName;
	}
	/**
	 * @return Returns the newVal.
	 */
	public String getNewVal() {
		return newVal;
	}
	/**
	 * @param newVal The newVal to set.
	 */
	public void setNewVal(String newVal) {
		this.newVal = newVal;
	}
	/**
	 * @return Returns the replaceVal.
	 */
	public String getReplaceVal() {
		return replaceVal;
	}
	/**
	 * @param replaceVal The replaceVal to set.
	 */
	public void setReplaceVal(String replaceVal) {
		this.replaceVal = replaceVal;
	}
	/**
	 * 
	 */
	/**
	 * @return Returns the key.
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key The key to set.
	 */
	public void setKey(String key) {
		this.key = key;
	}
}