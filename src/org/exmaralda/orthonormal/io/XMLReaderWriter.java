/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.orthonormal.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import org.exmaralda.common.corpusbuild.GUID;
import org.exmaralda.orthonormal.data.NormalizedFolkerTranscription;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.ElementFilter;
import org.jdom.xpath.XPath;
/**
 *
 * @author thomas
 */
public class XMLReaderWriter {

    public static NormalizedFolkerTranscription readNormalizedFolkerTranscription(String s) throws JDOMException, IOException {
        Document d = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromString(s);
        removeRedundantTimeElements(d);
        NormalizedFolkerTranscription t = new NormalizedFolkerTranscription(d, false);
        //t.setMediaPath(resolveMediaPath(d,f));
        return t;
    }

    public static NormalizedFolkerTranscription readNormalizedFolkerTranscription(File f) throws JDOMException, IOException {
        return readNormalizedFolkerTranscription(f, false);
    }
    
    public static NormalizedFolkerTranscription readNormalizedFolkerTranscription(File f, boolean removeIdentical) throws JDOMException, IOException {
        Document d = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
        removeRedundantTimeElements(d);
        NormalizedFolkerTranscription t = new NormalizedFolkerTranscription(d, removeIdentical);
        t.setMediaPath(resolveMediaPath(d,f));
        return t;
    }

    public static NormalizedFolkerTranscription readFolkerTranscription(File f) throws JDOMException, IOException {
        Document d = org.exmaralda.common.jdomutilities.IOUtilities.readDocumentFromLocalFile(f.getAbsolutePath());
        setUpDocument(d);
        NormalizedFolkerTranscription t = new NormalizedFolkerTranscription(d);
        t.setMediaPath(resolveMediaPath(d,f));
        return t;
    }

    private static String resolveMediaPath(Document d, File f) throws JDOMException {
            Attribute a = (Attribute)XPath.selectSingleNode(d, "//recording/@path");
            if (a!=null){
                String relativePath = a.getValue();
                System.out.println("Testing " + new File(relativePath).toURI());
                try{
                    //if (!(new File(relativePath).toURI().isAbsolute())){
                    if (!(new File(relativePath).isAbsolute())){
                        URI uri2 = f.getParentFile().toURI();
                        System.out.println("************** The path " + relativePath + " is not absolute");
                        System.out.println("************** Resolving " + relativePath + " relative to " + f.getAbsolutePath());
                        URI checkURI = new URI(relativePath);
                        URI absoluteURI = uri2.resolve(relativePath);
                        return new File(absoluteURI).getAbsolutePath();
                    } else {
                        System.out.println("************** The path " + relativePath + " is absolute");
                        return relativePath;
                    }
                } catch (IllegalArgumentException ex){
                    System.out.println("************** The path " + relativePath + " cannot be resolved");
                    return relativePath;
                } catch (URISyntaxException ex){
                    System.out.println("************** The path " + relativePath + " cannot be resolved");
                    return relativePath;
                } 
            }
        return null;
    }

    private static void setUpDocument(Document d) throws IOException, JDOMException {
        if (XPath.selectSingleNode(d, "//contribution[not(@parse-level='2' or @parse-level='3')]")!=null){
            throw new IOException("Das Dokument ist nicht vollständig geparst.");
        }
        d.getRootElement().setAttribute("id", new GUID().makeID());
        Iterator i = d.getDescendants(new ElementFilter("w"));
        int count = 0;
        while (i.hasNext()){
            count++;
            Element wordElement = (Element)(i.next());
            wordElement.setAttribute("id", "w" + Integer.toString(count));
        }
    }

    // added 28-01-2014
    // remove redundant time elements introduced in DGD2 publication
    private static void removeRedundantTimeElements(Document d) throws JDOMException {
        List l1 = XPath.selectNodes(d, "//contribution/descendant::time[not(following-sibling::*) and (@timepoint-reference=ancestor::contribution/@end-reference)]");
        for (Object o : l1){
            Element t = (Element)o;
            t.detach();
        }
        List l2 = XPath.selectNodes(d, "//contribution/descendant::time[not(preceding-sibling::*) and (@timepoint-reference=ancestor::contribution/@start-reference)]");
        for (Object o : l2){
            Element t = (Element)o;
            t.detach();
        }
    }

}
