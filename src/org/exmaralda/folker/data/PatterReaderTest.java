/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class PatterReaderTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            PatternReader pr = new PatternReader("/org/exmaralda/folker/data/Patterns.xml");
            String paddy = pr.getPattern(3, "GAT_EVENT", "default");
            System.out.println(paddy);
            Pattern p = java.util.regex.Pattern.compile(paddy);
            String text = "aber ABER was DENN. ";
            System.out.println(paddy);
            System.out.println(text);
            Matcher m = p.matcher(text);
            if (m.matches()){
                System.out.println("MATCH!");
            } else {
                System.out.println("NO MATCH!");
            }
        } catch (JDOMException ex) {
            Logger.getLogger(PatterReaderTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PatterReaderTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
