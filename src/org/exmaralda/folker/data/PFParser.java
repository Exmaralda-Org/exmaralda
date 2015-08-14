/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.filter.ElementFilter;
import org.jdom.transform.XSLTransformer;

/**
 *
 * @author thomas
 */
public class PFParser extends AbstractParser {

    String PATTERNS_FILE_PATH = "/org/exmaralda/folker/data/PFPatterns.xml";

    Hashtable<String, String> minimalPatterns;
    String MINIMAL_TRANSFORMER_FILE_PATH = "/org/exmaralda/folker/data/PF_transformcontribution.xsl";
    XSLTransformer minimalTransformer;
    

    public PFParser() {
        try {
            PatternReader pr = new PatternReader(PATTERNS_FILE_PATH);

            minimalPatterns = pr.getAllPatterns(2);
            minimalTransformer = new XSLTransformer(new org.exmaralda.common.jdomutilities.IOUtilities().readDocumentFromResource(MINIMAL_TRANSFORMER_FILE_PATH));


        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void parseDocument(Document doc, int parseLevel){
        if (parseLevel==0) return;

        if (parseLevel==1){
            java.util.Iterator contributionIterator = doc.getDescendants(new org.jdom.filter.ElementFilter("contribution"));
            java.util.Vector<org.jdom.Element> contributions = new java.util.Vector<org.jdom.Element>();
            while (contributionIterator.hasNext()){
                Element con = (Element)(contributionIterator.next());
                contributions.add(con);
            }
            for (Element e : contributions){
                boolean isOrdered = true;
                java.util.List l = e.getChildren("segment");
                String lastEnd = null;
                for (Object o : l){
                    Element ev = (Element)o;
                    if ((lastEnd==null) || (ev.getAttributeValue("start-reference").equals(lastEnd))){
                        // everything ok
                        lastEnd = ev.getAttributeValue("end-reference");
                    } else {
                        isOrdered = false;
                        break;
                    }
                }
                if (isOrdered){
                    java.util.List l2 = e.removeContent(new org.jdom.filter.ElementFilter("segment"));
                    Element unparsed = new Element("unparsed");
                    for (Object o : l2){
                        Element ev = (Element)o;
                        unparsed.addContent(ev.getText());
                        if (l2.indexOf(o)<l2.size()-1){
                            // do not insert a timepoint for the last segment
                            Element timepoint = new Element("time");
                            timepoint.setAttribute("timepoint-reference", ev.getAttributeValue("end-reference"));
                            unparsed.addContent(timepoint);
                        }
                    }
                    e.addContent(unparsed);
                    e.setAttribute("parse-level", "1");
                }
            }
        } else if (parseLevel==2){
            java.util.Iterator unparsedIterator = doc.getDescendants(new org.jdom.filter.ElementFilter("unparsed"));
            java.util.Vector<org.jdom.Element> unparseds = new java.util.Vector<org.jdom.Element>();
            while (unparsedIterator.hasNext()){
                Element up = (Element)(unparsedIterator.next());
                unparseds.add(up);
            }
            for (Element unparsed : unparseds){
                Vector<PositionTimeMapping> timePositions = new Vector<PositionTimeMapping>();
                String text = "";
                boolean totalParseOK = true;
                for (Object c : unparsed.getContent()){
                    if (c instanceof org.jdom.Text){
                        String eventText = ((org.jdom.Text)c).getText();
                        /*if (!(eventText.matches(minimalPatterns.get("GAT_EVENT")))){
                            totalParseOK = false;
                            break;
                        }*/
                        text+=eventText;
                    } else {
                        Element e = (Element)c;
                        String timeID = e.getAttributeValue("timepoint-reference");
                        timePositions.addElement(new PositionTimeMapping(text.length(), timeID));
                    }
                }
                /*totalParseOK = totalParseOK && (text.matches(minimalPatterns.get("GAT_CONTRIBUTION")));
                if (!totalParseOK){
                    System.out.println("TOTAL PARSE FAILED");
                    continue;
                }*/
                try {
                    text = parseText(text, "PF_NON_PHO_2", minimalPatterns);
                    text = parseText(text, "PF_NON_PHO_3", minimalPatterns);
                    text = parseText(text, "PF_NON_PHO_1", minimalPatterns);
                    text = parseText(text, "PF_UNINTELLIGIBLE", minimalPatterns);
                    text = parseText(text, "PF_FALSE_START", minimalPatterns);
                    text = parseText(text, "PF_INLINE_OVERLAP", minimalPatterns);
                    text = parseText(text, "PF_WORD", minimalPatterns);
                    text = parseText(text, "PF_INTERRUPT", minimalPatterns);
                    text = parseText(text, "PF_HESITATE", minimalPatterns);
                    text = parseText(text, "PF_SPACE", minimalPatterns);
                    text = parseText(text, "PF_BOUNDARY", minimalPatterns);
                    text = parseText(text, "PF_PUNCTUATION", minimalPatterns);
                    //System.out.println(text);
                    List newContent = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + text +"</X>").getRootElement().removeContent();
                    Element contribution = unparsed.getParentElement();
                    contribution.removeContent();
                    contribution.setContent(newContent);

                    contribution.setAttribute("parse-level", "2");
                    insertTimeReferences(contribution, timePositions);
                    Vector v = new Vector();
                    v.addElement(contribution);
                    Element transformedContribution = (Element) (minimalTransformer.transform(v).get(0));
                    Element contributionParent = contribution.getParentElement();
                    contributionParent.setContent(contributionParent.indexOf(contribution), transformedContribution);
                    //contributionParent.setContent(contributionParent.indexOf(contribution), contribution);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        }
    }


    String parseText(String text, String patternName, Hashtable<String,String> patterns) throws JDOMException, IOException{
        String docString = "<X>" + text + "</X>";
        //System.out.println("=== " + docString);
        Element e = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(docString).getRootElement();
        String returnText = "";
        for (Object o : e.getContent()){
            if (!(o instanceof Text)) {
                returnText+=org.exmaralda.common.jdomutilities.IOUtilities.elementToString((Element)o);
                continue;
            }
            Pattern p = Pattern.compile(patterns.get(patternName));
            String thisText = ((Text)o).getText();
            Matcher m = p.matcher(thisText);
            int fromWhere = 0;
            while (m.find(fromWhere)){
                int i1 = m.start();
                int i2 = m.end();
                thisText = thisText.substring(0,i1)
                        + "<" + patternName + ">"
                        + thisText.substring(i1,i2)
                        + "</" + patternName + ">"
                        + thisText.substring(i2);
                m = p.matcher(thisText);
                fromWhere=i2 + 2*patternName.length() + 5;
            }
            returnText+=thisText;
        }
        return returnText;
    }

    private void insertTimeReferences(Element contribution, Vector<PositionTimeMapping> timePositions) {
        //System.out.println(org.exmaralda.common.jdomutilities.IOUtilities.elementToString(contribution));
        /*for (PositionTimeMapping ptm : timePositions){
            System.out.println(ptm.position + " / " + ptm.timeID);
        }*/
        Iterator i = contribution.getDescendants();
        Vector<Text> texts = new Vector<Text>();
        while (i.hasNext()){
            Object o = i.next();
            if (!(o instanceof Text)) continue;
            Text textElement = (Text)o;
            texts.add(textElement);
        }

        int timePositionCount = 0;
        int offsetCount = 0;
        for (Text textElement : texts){
            Vector<PositionTimeMapping> localTimePositions = new Vector<PositionTimeMapping>();
            if (timePositionCount>=timePositions.size()) break;
            int positionWanted = timePositions.elementAt(timePositionCount).position;
            String text = textElement.getText();
            while ((positionWanted>=0) && (offsetCount<=positionWanted) && (offsetCount+text.length()>=positionWanted)){
                localTimePositions.add(new PositionTimeMapping(positionWanted-offsetCount, timePositions.elementAt(timePositionCount).timeID));
                timePositionCount++;
                if (timePositionCount<timePositions.size()){
                    positionWanted = timePositions.elementAt(timePositionCount).position;
                } else {
                    positionWanted = -1;
                }
            }
            offsetCount+=text.length();
            if (localTimePositions.size()>0) {
                Element parent = textElement.getParentElement();
                int index = parent.indexOf(textElement);
                textElement.detach();
                Vector<Content> newContent = new Vector<Content>();
                int offsetCount2 = 0;
                for (PositionTimeMapping ptm : localTimePositions){
                    Text t = new Text(text.substring(offsetCount2, ptm.position));
                    newContent.addElement(t);
                    Element e = new Element("time");
                    e.setAttribute("timepoint-reference", ptm.timeID);
                    newContent.addElement(e);
                    offsetCount2=ptm.position;
                }
                if (offsetCount2<text.length()){
                    Text t = new Text(text.substring(offsetCount2));
                    newContent.addElement(t);
                }
                parent.addContent(index, newContent);
            }
        }
    }

}
