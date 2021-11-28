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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

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
        
        if (!("auto_detect").equals("sourceLanguage")){
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
        String detected_source_language = translationNode.findPath("detected_source_language").textValue();
        String[] result = {translation, detected_source_language};
        
        return result;
    }
    
    public static void main(String[] args) throws IOException, URISyntaxException{
        DeepLConnector deepLConnector = new DeepLConnector("f8ff5708-f449-7a57-65b0-6ac4524cf64c:fx");
        String[] translation = deepLConnector.callDeepL("Ich möchte gerne einen Käse kaufen", "de", "fr", "more", DeepLConnector.API_TYPE.FREE);
        System.out.println(translation[0] + " " + translation[1]);
    }

        
    
    
    
    
}
