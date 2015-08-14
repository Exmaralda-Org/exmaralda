/*
 * ShowPartiturListener.java
 *
 * Created on 3. Juni 2005, 08:17
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package org.exmaralda.zecke;

/**
 *
 * @author thomas
 */
public interface ShowPartiturListener extends java.util.EventListener {
    
    public void showPartitur(ShowPartiturEvent event);
    
}
