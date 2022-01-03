/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

/**
 *
 * @author thomas.schmidt
 */
public interface WebServiceProgressListener {
    
    public void processProgress(String message, double progress);
    
    
}
