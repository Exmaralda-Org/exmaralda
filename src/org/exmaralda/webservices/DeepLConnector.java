/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
//import org.apache.http.client.HttpClient;

/**
 *
 * @author thomas.schmidt
 */
public class DeepLConnector {
    
    /*
    curl https://api.deepl.com/v2/translate \
	-d auth_key=[yourAuthKey] \
	-d "text=Hello, world!"  \
	-d "target_lang=DE"
    */
    
    /*
    curl https://api-free.deepl.com/v2/translate \
	-d auth_key=[yourAuthKey] \
	-d "text=Hello, world!"  \
	-d "target_lang=DE"    
    */
    
    //public String deepLWebServiceURL = "https://api.deepl.com/v2/translate"; // "professional" API
    public String deepLWebServiceURL = "https://api-free.deepl.com/v2/translate"; // free API
    
    private String authKey = "";
    public static enum API_TYPE {FREE, PROFESSIONAL};

    public DeepLConnector(String authKey) {
        this.authKey = authKey;
    }
    
    public String[] callDeepL(String sourceText, String sourceLanguage, 
            String targetLanguage, 
            String formality,
            API_TYPE apiType) throws IOException, URISyntaxException, InterruptedException{

        String urlString = "https://api-free.deepl.com/v2/translate";
        if (apiType==API_TYPE.PROFESSIONAL){
            urlString = "https://api.deepl.com/v2/translate";        
        }
        
        java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("text", sourceText);
        parameters.put("target_lang", targetLanguage);
        if (!("not_supported").equals(formality)){
            parameters.put("formality", formality);
        }
        
        if (!("auto_detect").equals(sourceLanguage)){
            parameters.put("source_lang", sourceLanguage);
        }
        
        String formData = parameters.entrySet()
            .stream()
            .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                          URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
            .collect(Collectors.joining("&"));        
        
        java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
            .uri(URI.create(urlString))
            .header("Authorization", "DeepL-Auth-Key " + authKey) // API Key in the Header
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(java.net.http.HttpRequest.BodyPublishers.ofString(formData))
            .build();   
        
        java.net.http.HttpResponse<String> response = client.send(request, 
                java.net.http.HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonRoot = objectMapper.readTree(responseBody);        

            /*
            {
            "translations": [
            {
            "detected_source_language": "DE",
            "text": "\"Beta Cinema.\""
            }
            ]
            }
             */
            JsonNode translationNode = jsonRoot.path("translations");
            String translation = translationNode.findPath("text").textValue();
            translation = translation.replaceAll(" {2,}", " ").replaceAll("^ ", "");
            String detected_source_language = translationNode.findPath("detected_source_language").textValue();
            String[] result = {translation, detected_source_language};

            return result;
        } else {
            throw new RuntimeException("Failed: HTTP error code: " + response.statusCode());
        }        
        
    }


    public String[] callDeepL_GET(String sourceText, String sourceLanguage, 
            String targetLanguage, 
            String formality,
            API_TYPE apiType) throws IOException, URISyntaxException{
        
        //https://api-free.deepl.com/v2/translate?auth_key=f8ff5708-f449-7a57-65b0-6ac4524cf64c:fx&text=%22so%20ein%20K%C3%A4se%22&target_lang=en
        
        String urlString = "https://api-free.deepl.com/v2/translate";
        if (apiType==API_TYPE.PROFESSIONAL){
            urlString = "https://api.deepl.com/v2/translate";        
        }
        
        urlString+="?auth_key=" + authKey;
        
        String quoteEscaped = URLEncoder.encode("\"", StandardCharsets.UTF_8.toString());
        
        //urlString+="&text=" + quoteEscaped + URLEncoder.encode(sourceText, StandardCharsets.UTF_8.toString()) + quoteEscaped;
        urlString+="&text=" + URLEncoder.encode(sourceText, StandardCharsets.UTF_8.toString());
        urlString+="&target_lang=" + targetLanguage;
        if (!("not_supported").equals(formality)){
            urlString+="&formality=" + formality;
        }
        
        if (!("auto_detect").equals(sourceLanguage)){
            urlString+="&source_lang=" + sourceLanguage + "";
        }
        
        //System.out.println("Calling " + urlString);
        
            
        URI jsonUrl = new URI(urlString);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonRoot = objectMapper.readTree(jsonUrl.toURL());        
        
        /*
        {
        "translations": [
        {
        "detected_source_language": "DE",
        "text": "\"Beta Cinema.\""
        }
        ]
        }
         */
        JsonNode translationNode = jsonRoot.path("translations");
        String translation = translationNode.findPath("text").textValue();
        translation = translation.replaceAll(" {2,}", " ").replaceAll("^ ", "");
        String detected_source_language = translationNode.findPath("detected_source_language").textValue();
        String[] result = {translation, detected_source_language};
        
        return result;
    }
    
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException{
        DeepLConnector deepLConnector = new DeepLConnector("c391e45c-ad35-4134-85e6-bc0f4f2fc4a9");
        String[] translation = deepLConnector.callDeepL("Ich möchte gerne einen Käse kaufen", "de", "fr", "more", DeepLConnector.API_TYPE.PROFESSIONAL);
        System.out.println(translation[0] + " " + translation[1]);
    }

        
    
    
    
    
}
