/*
 * Created on 03.11.2004 by woerner
 */
package org.exmaralda.coma.root;

import org.exmaralda.coma.helpers.GUID;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * coma2/org.sfb538.coma2/CorpusBasket.java
 * @author woerner
 * 
 */
public class CorpusBasket extends Document {
	public CorpusBasket() {
		super();
		Namespace xsi = Namespace.getNamespace("xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		Element newCorp = new Element("Corpus");
		newCorp.setAttribute("Name", "unnamed root");
		newCorp.setAttribute("Id", new GUID().makeID());
		newCorp
				.setAttribute("noNamespaceSchemaLocation", "comacorpus.xsd",
						xsi);
		newCorp.setAttribute("Type", "CoMa-Basket");
		newCorp.addContent(new Element("DBNode"));
		this.setRootElement(newCorp);
	}
}