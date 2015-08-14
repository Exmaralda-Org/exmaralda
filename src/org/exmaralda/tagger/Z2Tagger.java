/*
 * Created on 25.02.2005 by woerner
 */
package org.exmaralda.tagger;

/**
 * Ludger/z2tagger/Z2Tagger.java
 * @author woerner
 * 
 */
public class Z2Tagger {
	private static TaggerFrame tagger;

	public static void main(String[] args) {
		tagger = new TaggerFrame();
		tagger.show();
	}
}
