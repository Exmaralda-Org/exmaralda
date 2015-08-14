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
import org.jdom.xpath.XPath;

/**
 *
 * @author thomas
 */
public class GATParser extends AbstractParser {

    String PATTERNS_FILE_PATH = "/org/exmaralda/folker/data/Patterns.xml";

    Hashtable<String, String> minimalPatterns;
    String MINIMAL_TRANSFORMER_FILE_PATH = "/org/exmaralda/folker/data/transformcontribution.xsl";
    XSLTransformer minimalTransformer;
    
    Hashtable<String, String> basicPatterns;
    String BASIC_TRANSFORMER_FILE_PATH = "/org/exmaralda/folker/data/transformcontribution_basic.xsl";
    XSLTransformer basicTransformer;

    public GATParser() {
        this("default");
    }
    
    public GATParser(String languageCode) {
        try {
            PatternReader pr = new PatternReader(PATTERNS_FILE_PATH);

            minimalPatterns = pr.getAllPatterns(2, languageCode);
            minimalTransformer = new XSLTransformer(new org.exmaralda.common.jdomutilities.IOUtilities().readDocumentFromResource(MINIMAL_TRANSFORMER_FILE_PATH));

            basicPatterns = pr.getAllPatterns(3, languageCode);
            basicTransformer = new XSLTransformer(new org.exmaralda.common.jdomutilities.IOUtilities().readDocumentFromResource(BASIC_TRANSFORMER_FILE_PATH));

        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    
    @Override
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
                        if (!(eventText.matches(minimalPatterns.get("GAT_EVENT")))){
                            totalParseOK = false;
                            break;
                        }
                        text+=eventText;
                    } else {
                        Element e = (Element)c;
                        String timeID = e.getAttributeValue("timepoint-reference");
                        timePositions.addElement(new PositionTimeMapping(text.length(), timeID));
                    }
                }
                totalParseOK = totalParseOK && (text.matches(minimalPatterns.get("GAT_CONTRIBUTION")));
                if (!totalParseOK){
                    //System.out.println("TOTAL PARSE FAILED");
                    continue;
                }
                try {
                    text = parseText(text, "GAT_NON_PHO", minimalPatterns);
                    text = parseText(text, "GAT_PAUSE", minimalPatterns);
                    text = parseText(text, "GAT_BREATHE", minimalPatterns);
                    text = parseText(text, "GAT_UNCERTAIN", minimalPatterns);
                    // removed on 06-03-2009
                    //text = parseText(text, "GAT_UNINTELLIGIBLE");
                    text = parseText(text, "GAT_WORD", minimalPatterns);
                    text = parseText(text, "GAT_WORDBOUNDARY", minimalPatterns);
                    List newContent = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + text +"</X>").getRootElement().removeContent();
                    Element contribution = unparsed.getParentElement();
                    contribution.removeContent();
                    contribution.setContent(newContent);

                    List l = contribution.getChildren("GAT_UNCERTAIN");
                    java.util.Vector<org.jdom.Element> uncertains = new java.util.Vector<org.jdom.Element>();
                    for (Object o : l){
                        Element uc = (Element)(o);
                        uncertains.add(uc);
                    }
                    for (Element uc : uncertains){
                        String ucText = uc.getText();
                        ucText = parseText(ucText, "GAT_ALTERNATIVE", minimalPatterns);
                        ucText = parseText(ucText, "GAT_WORD", minimalPatterns);
                        ucText = parseText(ucText, "GAT_WORDBOUNDARY", minimalPatterns);
                        List newContent2 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + ucText +"</X>").getRootElement().removeContent();
                        uc.removeContent();
                        uc.setContent(newContent2);
                    }
                    
                    Iterator i2 = contribution.getDescendants(new ElementFilter("GAT_ALTERNATIVE"));
                    java.util.Vector<org.jdom.Element> alternatives = new java.util.Vector<org.jdom.Element>();
                    while (i2.hasNext()){
                        Element al = (Element)(i2.next());
                        alternatives.add(al);
                    }
                    for (Element al : alternatives){
                        String alText = al.getText();
                        alText = parseText(alText, "GAT_WORD", minimalPatterns);
                        alText = parseText(alText, "GAT_WORDBOUNDARY", minimalPatterns);
                        List newContent2 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + alText +"</X>").getRootElement().removeContent();
                        al.removeContent();
                        al.setContent(newContent2);
                    }
                    contribution.setAttribute("parse-level", "2");
                    insertTimeReferences(contribution, timePositions);
                    Vector v = new Vector();
                    v.addElement(contribution);
                    Element transformedContribution = (Element) (minimalTransformer.transform(v).get(0));
                    Element contributionParent = contribution.getParentElement();
                    contributionParent.setContent(contributionParent.indexOf(contribution), transformedContribution);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    continue;
                }
            }
        } else if (parseLevel==3){
            // TODO
            // parseDocument(doc, 2);
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
                        if (!(eventText.matches(basicPatterns.get("GAT_EVENT")))){
                            totalParseOK = false;
                            break;
                        }
                        text+=eventText;
                    } else {
                        Element e = (Element)c;
                        String timeID = e.getAttributeValue("timepoint-reference");
                        timePositions.addElement(new PositionTimeMapping(text.length(), timeID));
                    }
                }
                if (unparsed.getParentElement().getAttribute("speaker-reference")!=null){
                    //totalParseOK = totalParseOK && (text.matches(basicPatterns.get("GAT_CONTRIBUTION")));
                    // changed 28-03-2012 replace empty boundaries with pipe symbol boundary
                    totalParseOK = totalParseOK && 
                            ((text.matches(basicPatterns.get("GAT_CONTRIBUTION")))
                            ||(text.replaceAll(basicPatterns.get("GAT_EMPTY_BOUNDARY"), "| ").matches(basicPatterns.get("GAT_CONTRIBUTION"))));
                } else {
                    totalParseOK = totalParseOK && (text.matches(basicPatterns.get("GAT_NO_SPEAKER_CONTRIBUTION")));
                }
                if (!totalParseOK){
                    //System.out.println("TOTAL PARSE FAILED");
                    continue;
                }
                try {

                    // make sure angle brackets do not interfere with the XML parsing
                    text = text.replaceAll("<", "\u2329").replaceAll(">", "\u232A");

                    text = parseText(text, "GAT_PSEUDO_PHRASE_BOUNDARY", basicPatterns);

                    text = parseText(text, "GAT_NON_PHO", basicPatterns);
                    text = parseText(text, "GAT_PAUSE", basicPatterns);
                    text = parseText(text, "GAT_BREATHE", basicPatterns);

                    // patterns specific to basic transcription
                    text = parseText(text, "GAT_COMMENT_START_ESCAPED", basicPatterns);
                    text = parseText(text, "GAT_COMMENT_END_ESCAPED", basicPatterns);
                    text = parseText(text, "GAT_PHRASE_BOUNDARY", basicPatterns);
                    text = parseText(text, "GAT_LATCHING", basicPatterns);
                    // end patterns specific to basic transcription


                    text = parseText(text, "GAT_UNCERTAIN", basicPatterns);
                    // removed on 06-03-2009
                    //text = parseText(text, "GAT_UNINTELLIGIBLE");
                    
                    text = parseText(text, "GAT_WORD", basicPatterns);
                    text = parseText(text, "GAT_WORDBOUNDARY", basicPatterns);


                    List newContent = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + text +"</X>").getRootElement().removeContent();
                    Element contribution = unparsed.getParentElement();
                    contribution.removeContent();
                    contribution.setContent(newContent);

                    List l = contribution.getChildren("GAT_UNCERTAIN");
                    java.util.Vector<org.jdom.Element> uncertains = new java.util.Vector<org.jdom.Element>();
                    for (Object o : l){
                        Element uc = (Element)(o);
                        uncertains.add(uc);
                    }
                    for (Element uc : uncertains){
                        String ucText = uc.getText();
                        ucText = parseText(ucText, "GAT_ALTERNATIVE", basicPatterns);
                        ucText = parseText(ucText, "GAT_WORD", basicPatterns);
                        ucText = parseText(ucText, "GAT_WORDBOUNDARY", basicPatterns);
                        List newContent2 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + ucText +"</X>").getRootElement().removeContent();
                        uc.removeContent();
                        uc.setContent(newContent2);
                    }

                    Iterator i2 = contribution.getDescendants(new ElementFilter("GAT_ALTERNATIVE"));
                    java.util.Vector<org.jdom.Element> alternatives = new java.util.Vector<org.jdom.Element>();
                    while (i2.hasNext()){
                        Element al = (Element)(i2.next());
                        alternatives.add(al);
                    }
                    for (Element al : alternatives){
                        String alText = al.getText();
                        alText = parseText(alText, "GAT_WORD", basicPatterns);
                        alText = parseText(alText, "GAT_WORDBOUNDARY", basicPatterns);
                        List newContent2 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + alText +"</X>").getRootElement().removeContent();
                        al.removeContent();
                        al.setContent(newContent2);
                    }


                    // take care of accent markup and lengthening...
                    Iterator i3 = contribution.getDescendants(new ElementFilter("GAT_WORD"));
                    java.util.Vector<org.jdom.Element> words = new java.util.Vector<org.jdom.Element>();
                    while (i3.hasNext()){
                        Element w = (Element)(i3.next());
                        words.add(w);
                    }
                    for (Element w : words){
                        String wText = w.getText();
                        wText = parseText(wText, "GAT_STRONG_ACCENT_SYLLABLE", basicPatterns);
                        wText = parseText(wText, "GAT_ACCENT_SYLLABLE", basicPatterns);
                        wText = parseText(wText, "GAT_LENGTHENING", basicPatterns);
                        List newContent3 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + wText +"</X>").getRootElement().removeContent();
                        w.removeContent();
                        w.setContent(newContent3);
                    }
                    // ... and of lengthening inside accent syllables
                    Iterator i4 = contribution.getDescendants(new ElementFilter("GAT_ACCENT_SYLLABLE").or(new ElementFilter("GAT_STRONG_ACCENT_SYLLABLE")));
                    java.util.Vector<org.jdom.Element> syllables = new java.util.Vector<org.jdom.Element>();
                    while (i4.hasNext()){
                        Element s = (Element)(i4.next());
                        syllables.add(s);
                    }
                    for (Element s : syllables){
                        String sText = s.getText();
                        sText = parseText(sText, "GAT_LENGTHENING", basicPatterns);
                        List newContent4 = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString("<X>" + sText +"</X>").getRootElement().removeContent();
                        s.removeContent();
                        s.setContent(newContent4);
                    }

                    contribution.setAttribute("parse-level", "3");
                    insertTimeReferences(contribution, timePositions);

                    // transform the pseudo markup into the target markup...
                    Vector<Element> v = new Vector<Element>();
                    v.addElement(contribution);
                    Element transformedContribution = (Element) (basicTransformer.transform(v).get(0));

                    //... hierarchize it...
                    List content = transformedContribution.removeContent();
                    Element currentLine = new Element("line");
                    for (Object c : content){
                        Element e = (Element)c;
                        currentLine.addContent(e);
                        if (e.getName().equals("boundary") && e.getAttributeValue("type").equals("final")){
                            transformedContribution.addContent(currentLine);
                            currentLine = new Element("line");
                        }
                    }
                    // this is for speakerless contributions which do not have to end with a boundary
                    if (currentLine.getContentSize()>0){
                            transformedContribution.addContent(currentLine);
                    }

                    //... and put it in the right place in the document
                    Element contributionParent = contribution.getParentElement();
                    contributionParent.setContent(contributionParent.indexOf(contribution), transformedContribution);
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
    
    public boolean isFullyParsedOnLevel(Document doc, int level) throws JDOMException{
        return (XPath.selectSingleNode(doc, "//contribution[not(@parse-level='" + Integer.toString(level) + "')]")==null);     
    }

}
