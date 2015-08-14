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
public class ConfigurableAuthenticator extends java.net.Authenticator {
    

    String username = "";
    String password = "";

    /** Creates a new instance of KicktionaryAuthenticator */
    public ConfigurableAuthenticator(String un, String pw) {
        username = un;
        password = pw;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        PasswordAuthentication retValue = new PasswordAuthentication(username, password.toCharArray());
        return retValue;
    }
    
}
