/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.sbcsae.convertStep1;

/**
 *
 * @author thomas
 */
public class DoTransform {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        TRN2XML.main(null);
        Mapper.main(null);
        Timestamps2Timeline.main(null);
        Step3_to_Exmaralda.main(null);
    }

}
