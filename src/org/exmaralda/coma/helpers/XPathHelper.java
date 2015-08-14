/**
 * 
 */
package org.exmaralda.coma.helpers;

/**
 * @author woerner
 *
 */
public class XPathHelper {
	
	public static String getInnerCondition(String in) {
		if (in.contains("[")) {
			return in.substring(in.lastIndexOf("=\'") + 2, in
					.lastIndexOf("'"));
			
		} else {
			return in.substring(in.lastIndexOf("/")+1);
		}
	}

}
