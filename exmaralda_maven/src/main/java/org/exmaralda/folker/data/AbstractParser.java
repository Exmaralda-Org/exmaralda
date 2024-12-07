/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.folker.data;

import org.jdom.Document;

/**
 *
 * @author thomas
 */
public abstract class AbstractParser {


    public AbstractParser() {
    }


    public abstract void parseDocument(Document doc, int parseLevel);


}
