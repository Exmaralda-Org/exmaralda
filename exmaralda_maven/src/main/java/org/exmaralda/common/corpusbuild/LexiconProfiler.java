/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.IOException;
import java.util.Iterator;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTier;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class LexiconProfiler extends AbstractSegmentedTranscriptionProcessor {

    String speaker;
    String ageAttribute;
    String wordName;
    
    XPath ageXpath;

    Document resultDocument = new Document();
    Element resultRoot = new Element("lexicon-profile");
    boolean allSmall;

    public LexiconProfiler(String corpusPath, String speaker, String ageAttribute, String wordName, boolean allSmall) {
        super(corpusPath);
        this.speaker = speaker;
        this.ageAttribute = ageAttribute;
        this.wordName = wordName;
        this.allSmall = allSmall;
        resultDocument.setRootElement(resultRoot);
        try {
            ageXpath = XPath.newInstance("descendant::Key[@Name='" + ageAttribute + "']");
        } catch (JDOMException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void processTranscription(SegmentedTranscription st) {
        try {
            Element thisProfile = new Element("profile");
            resultRoot.addContent(thisProfile);
            String age = ((Element) (ageXpath.selectSingleNode(currentElement.getParentElement().getParentElement()))).getText();
            thisProfile.setAttribute("age", age);
            thisProfile.setAttribute("speaker", speaker);
            for (int pos=0; pos<st.getBody().getNumberOfTiers(); pos++){
                        SegmentedTier tier = (SegmentedTier) (st.getBody().elementAt(pos));
                        if (tier.getSpeaker()==null) continue;
                        if (!(st.getHead().getSpeakertable().getSpeakerWithID(tier.getSpeaker()).getAbbreviation().equals(speaker))) continue;
                        Document d = IOUtilities.readDocumentFromString(tier.toXML());
                        Iterator i = d.getDescendants(new org.jdom.filter.AbstractFilter(){

                            public boolean matches(Object o) {
                                if (!(o instanceof Element)) return false;
                                Element e = (Element)o;
                                if ((e.getName().equals("ts")) && (e.getAttributeValue("n").equals(wordName))) return true;
                                return false;
                            }

                        });
                        while (i.hasNext()){
                            Element w = (Element)(i.next());
                            Element w1 = new Element("word");
                            if (allSmall){
                                w1.setText(w.getText().toLowerCase());
                            } else {
                                w1.setText(w.getText());
                            }
                            thisProfile.addContent(w1);
                        }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void write(String name) throws IOException {
        IOUtilities.writeDocumentToLocalFile(name, resultDocument);
    }

}
