/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import com.fasterxml.jackson.databind.JsonNode;

/**
 *
 * @author thomas.schmidt
 */
public class FrazierADCCuePoint {

    int start;
    int end;
    String voice;
    String text;
    String people;
    String locations;
    String things;

    public FrazierADCCuePoint(JsonNode cuePointNode) {
        start = cuePointNode.get("start").asInt();
        end = cuePointNode.get("end").asInt();
        voice = cuePointNode.get("voice").asText();
        text = stripHTML(cuePointNode.get("text").asText());
        people = cuePointNode.get("people").asText();
        locations = cuePointNode.get("locations").asText();
        things = cuePointNode.get("things").asText();            
    }

    /*
        *text*
        Ist HTML mit einigen speziellen Tags, die für die Steuerung der
        Text-To-Speech Ausgabe genutzt werden. Hier wäre es ggf. sinnvoll, nur
        den Klartext zu nutzen.    
    */
    private String stripHTML(String asText) {
        //<abbr class="speech" title="Getränkekässten">
        return asText
                .replaceAll("<[A-Za-z]+( [A-Za-z]+=\"[A-Za-zÄÖÜäöüß0-9 ]+\")*>", "")
                .replaceAll("</[A-Za-z]+>", "")
                .replaceAll("&nbsp;", " ");
    }
    
    
    
    
}
