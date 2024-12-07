/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.folker.matchlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exmaralda.common.dialogs.ProgressBarDialog;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MatchList {
 
    Document matchListDocument;
    ArrayList<MatchListListener> listeners = new ArrayList<MatchListListener>();
    List l = new ArrayList();
    
    public MatchList(){        
        super();
    }
    
    public void addMatchListListener(MatchListListener listener){
        listeners.add(listener);
    }
    
    public void fireMatchListEvent(String description, double progress){
        for (MatchListListener listener : listeners){
            listener.processMatchListEvent(description, progress);
        }
    }
    
    public void read(File f) throws JDOMException, IOException{
        matchListDocument = FileIO.readDocumentFromLocalFile(f);
        l = matchListDocument.getRootElement().getChildren("match");
    }
    
    public void write(File f) throws IOException{
        FileIO.writeDocumentToLocalFile(f, matchListDocument);
    }
    
    public boolean isAugmented(){
        return ((matchListDocument!=null) && ("yes".equals(matchListDocument.getRootElement().getAttributeValue("augmented"))));
    }
    
    public void augment(File referenceDirectory) throws JDOMException, IOException{
        int count = 0;
        for (Object o : l){
            //  <match tra="FOLK_E_00002_SE_01_T_01_DF_01" con="c1028">und dann darfst du wieder gehen</match>
            Element e = (Element)o;
            String transcriptionName = e.getAttributeValue("tra");
            String contributionID = e.getAttributeValue("con");
            File referenceFile = new File(referenceDirectory, transcriptionName + ".fln");
            Document transcriptionDocument = FileIO.readDocumentFromLocalFile(referenceFile);
            this.fireMatchListEvent(transcriptionName + "/" + contributionID, (double)count/l.size());
            count++;
            //<contribution speaker-reference="CJ" start-reference="TLI_0" end-reference="TLI_1"
            //parse-level="2" speaker-dgd-id="FOLK_S_00030" id="c1" time="3.54">
            String xp = "//contribution[@id='" + contributionID + "']";
            Element contribution = (Element) XPath.selectSingleNode(transcriptionDocument, xp);
            if (contribution!=null){
                e.setAttribute("time", contribution.getAttributeValue("time"));
                if (contribution.getAttributeValue("speaker-reference")!=null){
                    e.setAttribute("speaker", contribution.getAttributeValue("speaker-reference"));
                }
            }
        }
        matchListDocument.getRootElement().setAttribute("augmented", "yes");
    }
    
    public int getSize(){
        return l.size();
    }
    
    public Object getElementAt(int index){
        return l.get(index);
    }

    
}
