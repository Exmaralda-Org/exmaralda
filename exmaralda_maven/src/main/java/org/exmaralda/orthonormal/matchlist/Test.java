/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.orthonormal.matchlist;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class Test implements MatchListListener {

    String REFERENCE_DIR = "Z:\\FOLK_DGD_2\\transcripts\\FOLK_FLN";
    String MATCH_LIST = "C:\\Users\\Schmidt\\Desktop\\ARNE\\gehen.xml";
    String MATCH_LIST_OUT = "C:\\Users\\Schmidt\\Desktop\\ARNE\\gehen_augmented.xml";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Test().doit();
    }

    private void doit() {
        try {
            MatchList ml = new MatchList();
            ml.addMatchListListener(this);
            ml.read(new File(MATCH_LIST));
            ml.write(new File(MATCH_LIST_OUT));
        } catch (JDOMException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void processMatchListEvent(String description, double progress) {
        System.out.println(description + " / "  + progress);
    }

    @Override
    public void processMatchListEvent(File workingDirectory, Element contribution) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
