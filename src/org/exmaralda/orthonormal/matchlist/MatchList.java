/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.matchlist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class MatchList {
 
    Document matchListDocument;
    ArrayList<MatchListListener> listeners = new ArrayList<MatchListListener>();
    List l = new ArrayList();
    
    public MatchList(){        
    }
    
    public void addMatchListListener(MatchListListener listener){
        listeners.add(listener);
    }
    
    public void fireMatchListEvent(Element contribution, File workingDir){
        for (MatchListListener listener : listeners){
            listener.processMatchListEvent(workingDir, contribution);
        }
    }
    
    public void read(File f) throws JDOMException, IOException{
        matchListDocument = FileIO.readDocumentFromLocalFile(f);
        l = matchListDocument.getRootElement().getChildren("contribution");
    }
    
    public void write(File f) throws IOException{
        FileIO.writeDocumentToLocalFile(f, matchListDocument);
    }
    
    public int getSize(){
        return l.size();
    }
    
    public Object getElementAt(int index){
        return l.get(index);
    }

    
}
