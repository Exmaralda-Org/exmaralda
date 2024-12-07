/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.tagging;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author thomas
 */
public interface TreeTaggableDocument<O> {

    public int getNumberOfTaggableSegments();

    public int getNumberOfTokens();

    public List<String> getTokensAt(int pos) throws IOException;

    public List<String> getIDs() throws IOException;

    public String getBase();

    
}
