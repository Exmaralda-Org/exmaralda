/*
 * KicktionaryAuthenticator.java
 *
 * Created on 20. November 2006, 16:48
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.exmaralda.exakt.utilities;

import java.net.PasswordAuthentication;

/**
 *
 * @author thomas
 */
public class KicktionaryAuthenticator extends java.net.Authenticator {
    
    /** Creates a new instance of KicktionaryAuthenticator */
    public KicktionaryAuthenticator() {
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        //PasswordAuthentication retValue = new PasswordAuthentication("thomas.schmidt", "Bernd!Moos".toCharArray());
        PasswordAuthentication retValue = new PasswordAuthentication("scientist", "science!".toCharArray());
        return retValue;
    }
    
}
