/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.xpath.XPath;

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

    public DeepLConnector(String authKey) {
        this.authKey = authKey;
    }
    
    public String callDeepL(String sourceString, String targetLanguage) throws IOException{
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addTextBody("text", sourceString);
        builder.addTextBody("target_lang", targetLanguage);

        HttpPost httpPost = new HttpPost(deepLWebServiceURL);        
        httpPost.setEntity(builder.build());
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        if (statusCode==200 && result != null) {
            String resultAsString = EntityUtils.toString(result);
            
            // Now we have a json string like so
            // {
            // "translations": [{
	    // 	"detected_source_language":"EN",
            // "text":"Hallo, Welt!"
	    // }]
        }
            
        EntityUtils.consume(result);
        httpClient.close();
            
        
        return "";
        
    }
    
    
    
    
}
