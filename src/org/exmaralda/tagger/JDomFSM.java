/*
 * Created on 14.03.2005 by woerner
 */
package org.exmaralda.tagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

/**
 * Ludger/z2tagger/JDomFSM.java
 * 
 * @author woerner
 * 
 */
public class JDomFSM {
    private Document fsmDoc;
    private HashMap whoIsWho;
    private String delimiter;
    private HashSet ignores;

    /**
     * @throws IOException
     * @throws JDOMException
     * 
     */
    public JDomFSM(File f) throws JDOMException, IOException {
	delimiter = "";
	SAXBuilder parser = new SAXBuilder();
	fsmDoc = parser.build(f);
	ignores = new HashSet();
	parse(fsmDoc);
    }

    /**
     * @param fsmDoc2
     */
    private void parse(Document fsmDoc2) {
	Iterator i;
	String csNow;
	List charSets = fsmDoc2.getRootElement().getChildren("char-set");
	List ignoreSet = fsmDoc2.getRootElement().getChild("ignore")
		.getChildren();
	if (!ignoreSet.isEmpty()) {
	    i = ignoreSet.iterator();
	    while (i.hasNext()) {
		ignores.add(((Element) i.next()).getText());
	    }
	}
	whoIsWho = new HashMap();
	i = charSets.iterator();
	while (i.hasNext()) {
	    Element csElement = (Element) i.next();
	    System.out.println("charset:" + csElement.getAttributeValue("id"));
	    csNow = csElement.getAttributeValue("id");
	    List chars = csElement.getChildren();
	    Iterator ci = chars.iterator();
	    while (ci.hasNext()) {
		Element myElm = (Element) ci.next();
		System.out.println("char:" + myElm.getText());
		if (myElm.getText() != null) {
		    whoIsWho.put((String) myElm.getText(), (String) csNow);
		    delimiter += myElm.getText();
		}
	    }
	}
	System.out.println("delim:" + delimiter);
    }

    public List getOutput(Text text) {
	List l = new ArrayList();
	StringTokenizer st = new StringTokenizer(text.getText(), delimiter,
		true);
	System.out.println(text.getText());
	while (st.hasMoreTokens()) {
	    String myToken = st.nextToken();
	    System.out.println(">" + myToken);
	    if (whoIsWho.containsKey(myToken)) {
		String whatIsIt = (String) whoIsWho.get(myToken);
		if (whatIsIt.equals("SENTENCE_DELIMITER")) {
		    Element c = new Element("c");
		    c.setText(myToken);
		    c.setAttribute("type", "EndS");
		    l.add(c);
		} else if (whatIsIt.equals("OTHER_PUNCTUATION")) {
		    Element c = new Element("c");
		    c.setText(myToken);
		    c.setAttribute("type", "MidS");
		}
	    } else {
		if (!myToken.equals(text.getText())) {
		    Element w = new Element("w");
		    w.setText(myToken);
		    l.add(w);
		    System.out.println("<"+w);
		}
	    }
	}
	return l;
    }

    public HashSet tagsToIgnore() {
	return ignores;
    }

}