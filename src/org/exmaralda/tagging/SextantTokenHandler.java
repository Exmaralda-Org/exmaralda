/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.IOException;
import java.util.List;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
//public class SextantTokenHandler implements org.annolab.tt4j.TokenHandler<String> {
public class SextantTokenHandler implements org.annolab.tt4j.ProbabilityHandler<String> {

    Document sextantDocument;
    int count=0;
    List<String> idList;
    org.jdom.Namespace xlinkNameSpace = org.jdom.Namespace.getNamespace("xlink", "http://www.w3.org/1999/xlink");

    public SextantTokenHandler() {
        try {
            //Element root = new Element("annotation");
            //sextantDocument = new Document(root);
            sextantDocument = new IOUtilities().readDocumentFromResource("/org/exmaralda/tagging/DummySextantAnnotation.exa");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    
    @Override
    public void token(String token, String pos, String lemma) {
        //System.out.println("HERE BE TOKEN!");
        /*<ann xlink:href="#Seg_4" id="MyTheory_s_exs_pos_5">
			<fs>
				<f name="a">
					<symbol value="1"/>
				</f>
			</fs>
        </ann>*/

        
        //System.out.println("---------------------------");
        //System.out.println("Token: " + token + " " + pos + " " + lemma);

        /*Element ann = new Element("ann");
        ann.setAttribute("id", "ann_" + Integer.toString(count));
        ann.setAttribute("href", "#" + idList.get(count), xlinkNameSpace);

        Element fs = new Element("fs");
        ann.addContent(fs);

        Element posElement = new Element("f");
        posElement.setAttribute("name", "pos");
        fs.addContent(posElement);

        Element posValue = new Element("symbol");
        posValue.setAttribute("value", pos);
        posElement.addContent(posValue);

        Element lemElement = new Element("f");
        lemElement.setAttribute("name", "lemma");
        fs.addContent(lemElement);

        Element lemValue = new Element("symbol");
        lemValue.setAttribute("value", lemma);
        lemElement.addContent(lemValue);

        sextantDocument.getRootElement().addContent(ann);

        count++;*/


    }

    void setIDList(List<String> ids) {
        idList = ids;
    }

    @Override
    public void probability(String pos, String lemma, double probability) {
        //System.out.println("Probability: " + pos + " " + lemma + " " + probability);
        //System.out.println("---------------------------");
        
        Element ann = new Element("ann");
        ann.setAttribute("id", "ann_" + Integer.toString(count));
        ann.setAttribute("href", "#" + idList.get(count), xlinkNameSpace);

        Element fs = new Element("fs");
        ann.addContent(fs);

        Element posElement = new Element("f");
        posElement.setAttribute("name", "pos");
        fs.addContent(posElement);

        Element posValue = new Element("symbol");
        posValue.setAttribute("value", pos);
        posElement.addContent(posValue);

        Element lemElement = new Element("f");
        lemElement.setAttribute("name", "lemma");
        fs.addContent(lemElement);

        Element lemValue = new Element("symbol");
        lemValue.setAttribute("value", lemma);
        lemElement.addContent(lemValue);

        Element probElement = new Element("f");
        probElement.setAttribute("name", "p-pos");
        fs.addContent(probElement);

        Element probValue = new Element("symbol");
        probValue.setAttribute("value", Double.toString(probability));
        probElement.addContent(probValue);

        sextantDocument.getRootElement().addContent(ann);

        count++;
    }



}
