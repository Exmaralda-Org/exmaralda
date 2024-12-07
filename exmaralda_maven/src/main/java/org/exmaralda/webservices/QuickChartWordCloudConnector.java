/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author thomas.schmidt
 */
public class QuickChartWordCloudConnector {
    
    /*
        text	Text of the word cloud	(required)
        format	Image output format - svg or png	svg
        width	Image width	600
        height	Image height	600
        backgroundColor	Background color of image
        (rgb, hsl, hex, or name value)	transparent
        fontFamily	Font family to use	serif
        fontWeight	Font weight to use. Note that not all fonts support all weights	normal
        loadGoogleFonts	Google Fonts to load	
        fontScale	Size of the largest font (roughly)	25
        scale	Frequency scaling method - linear, sqrt, or log	linear
        padding	Padding between words, in pixels	1
        rotation	Maximum angle of rotation for words	20
        maxNumWords	Maximum number of words to show.
        Note that fewer may be shown depending on size.	200
        minWordLength	Minimum character length of each word to include.	1
        case	Force words to this case - upper, lower, or none	lower
        colors	List of colors for words in JSON format, assigned randomly.
        e.g. ["red", "#00ff00", "rgba(0, 0, 255, 1.0)"]	random
        removeStopwords	If true, remove common words from the cloud	false
        cleanWords	If true, removes symbols and extra characters from words	true
        language	Two-letter language code of stopwords to remove (supported languages)	en
        useWordList	If true, treat text as a comma-separated list of words or phrases instead of trying to split the text on our side	false    
    */

    public String quickChartWordCloudURL = "https://quickchart.io/wordcloud"; 
    

    public QuickChartWordCloudConnector() {
    }
    

    public File callQuickChartWordCloud(String text, 
            String format, 
            boolean removeStopWords, 
            String language) throws IOException, URISyntaxException{
        
        // setup a HTTP client
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        String jsonString = "{"
                + "\"format\": \"" + format + "\","
                + "\"text\": \"" + text + "\","
                + "\"language\": \"" + language + "\","
                + "\"removeStopwords\": \"" + Boolean.toString(removeStopWords) + "\""
                + "}";
        StringEntity entity = new StringEntity(jsonString);
        
        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(quickChartWordCloudURL);        
        httpPost.addHeader("Content-Type", "application/json");
        httpPost.setEntity(entity);
        
        CloseableHttpResponse response = httpClient.execute(httpPost);
        
        
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        
        if (statusCode==200 && result != null) {
            
            byte[] pngByteArray = EntityUtils.toByteArray(result);

            File tempFile = File.createTempFile("WordCloudPNG", ".png");
            
            System.out.println(tempFile.getAbsolutePath());
            
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                  fos.write(pngByteArray);
            }
            
            EntityUtils.consume(result);
            httpClient.close();
            
            
            return tempFile;
        } else {
            // something went wrong, throw an exception
            //System.out.println(EntityUtils.toString(result, "utf-8"));
            String reason = statusLine.getReasonPhrase();
            System.out.println(statusLine.getStatusCode());
            System.out.println(reason);
            throw new IOException(reason);
        }
    }
    
    public static void main(String[] args) throws IOException, URISyntaxException{
        QuickChartWordCloudConnector connector = new QuickChartWordCloudConnector();
        
        String text = "Her I go again on my own going down the only road I've ever known. "
                + "Like a drifter I was born to walk alone. And here I go again."+
                "I don't know where I'm going " +
                    "But I sure know where I've been " +
                    "Hanging on the promises in songs of yesterday " +
                    "And I've made up my mind " +
                    "I ain't wasting no more time " +
                    "Here I go again, here I go again " +
                    "Though I keep searching for an answer " +
                    "I never seem to find what I'm looking for " +
                    "Oh, Lord, I pray you give me strength to carry on " +
                    "'Cause I know what it means " +
                    "To walk along the lonely street of dreams " +
                    "Here I go again on my own " +
                    "Going down the only road I've ever known " +
                    "Like a drifter, I was born to walk alone " +
                    "And I've made up my mind " +
                    "I ain't wasting no more time " +
                    "I'm just another heart in need of rescue " +
                    "Waiting on love's sweet charity " +
                    "And I'm gonna hold on for the rest of my days " +
                    "'Cause I know what it means " +
                    "To walk along the lonely street of dreams " +
                    "And here I go again on my own " +
                    "Going down the only road I've ever known " +
                    "Like a drifter, I was born to walk alone " +
                    "And I've made up my mind " +
                    "I ain't wasting no more time " +
                    "But here I go again " +
                    "Here I go again " +
                    "Here I go again " +
                    "Here I go " +
                    "'Cause I know what it means " +
                    "To walk along the lonely street of dreams " +
                    "And here I go again on my own " +
                    "Going down the only road I've ever known " +
                    "Like a drifter, I was born to walk alone " +
                    "And I've made up my mind " +
                    "I ain't wasting no more time " +
                    "And here I go again on my own " +
                    "Going down the only road I've ever known " +
                    "Like a drifter I was born to walk alone " +
                    "'Cause I know what it means " +
                    "To walk along the lonely street of dreams " +
                    "And here I go again on my own " +
                    "Going down the only road I've ever known " +
                    "Like a drifter, I was born to walk alone"
                ;
        File result = connector.callQuickChartWordCloud(text, "png", true, "en");            
        System.out.println(result.getAbsolutePath());
    }

        
    
    
    
    
}
