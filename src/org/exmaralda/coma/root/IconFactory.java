/*
 * Created on 04.06.2004 by woerner
 */
package org.exmaralda.coma.root;

import java.awt.Image;

import javax.swing.ImageIcon;

import org.exmaralda.coma.resources.ResourceHandler;

/**
 * 
 * @author woerner
 */
public class IconFactory extends ImageIcon {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8963749684965940775L;

	/**
	 * @param name
	 *              or path of the image file
	 */
	public IconFactory(Image arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public static ImageIcon createImageIcon(String path) {
		return createImageIcon(path, -1);
	}

	public static ImageIcon createImageIcon(String path, int iconIndex) {
		java.net.URL imgURL = new ResourceHandler().image(path);
		if (imgURL == null) {
			imgURL = new ResourceHandler().image("noicon.png");
			System.err.println("\n\nCouldn't find file: " + path);
		}
		return new ImageIcon(imgURL);
	}
}