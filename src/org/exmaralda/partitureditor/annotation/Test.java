/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.partitureditor.annotation;

import java.io.File;
import java.io.IOException;
import org.jdom.JDOMException;

/**
 *
 * @author thomas
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AnnotationSpecification as = new AnnotationSpecification();
            //as.read(new File("C:\\Dokumente und Einstellungen\\thomas\\Desktop\\ANNOTATION\\annotation-specification-hu.xml"));
            
            UDPOSMapping m = new UDPOSMapping(UDPOSMapping.TagSet.STTS_2_0);
            for (String key : m.keySet()){
                System.out.println(key + " --> " + m.get(key));
            }
        } catch (JDOMException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
