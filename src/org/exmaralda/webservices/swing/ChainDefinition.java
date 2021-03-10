/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices.swing;

import org.jdom.Element;

/**
 *
 * @author thomas.schmidt
 */
public final class ChainDefinition {

    String name;
    String path;
    String language;
    String description;
    
    ChainDefinition(Element e) {
        /*<chain-definition>
            <name>BBAW: Part-Of-Speech</name>
            <language>de</language>
            <path>/org/exmaralda/webservices/weblicht-chains/BBAW_PartOfSpeech_DE.xml</path>
            <description>To do</description>
        </chain-definition>*/
        setName(e.getChildText("name"));
        setLanguage(e.getChildText("language"));
        setPath(e.getChildText("path"));
        setDescription(e.getChildText("description"));        
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
    
    
}
