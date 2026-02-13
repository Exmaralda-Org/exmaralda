/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author thomas.schmidt
 */
public class GWDGWhisperConnector {
    
    /*
        curl -i 'https://saia.gwdg.de/v1/audio/<translations or transcriptions>' \
            --header 'Accept: /' \
            --header 'Authorization: Bearer <key>' \
            -H "Content-Type: multipart/form-data"\
            -F model="whisper-large-v2" \
            -F "file=@./<voice.wav, mp4, mp3 or flac>" \
            -F response_format=<vtt or text or srt>    
    */

    public String whisperWebServiceURL = "https://saia.gwdg.de/v1/audio/transcriptions"; 
    
    private String authKey = "";
    
    public static String DEFAULT_MODEL = "whisper-large-v2";

    public GWDGWhisperConnector(String authKey) {
        this.authKey = authKey;
    }
    
    public String callWhisperSimple(File audioFile) throws IOException, URISyntaxException {
        String result = callWhisper(audioFile, DEFAULT_MODEL, "json");    
        return result;
    }
    
    public String callWhisperRegular(File audioFile, String responseFormat) throws IOException, URISyntaxException {
        String result = callWhisper(audioFile, DEFAULT_MODEL, responseFormat);    
        return result;
    }


    public String callWhisper(File audioFile, 
            String modelID, 
            String responseFormat
    ) throws IOException, URISyntaxException{
        
        // setup a HTTP client
        //HttpClient httpClient = new DefaultHttpClient(); // deprecated!
        //CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        
        // construct a multipart entity that will be sent via POST to the web service
        //MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE); // deprecated!
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        //builder.addBinaryBody("file", audioFile);
        
        //builder.addPart("file", new FileBody(audioFile));
        
        builder.addBinaryBody(
                "file",
                Files.newInputStream(audioFile.toPath()),
                ContentType.APPLICATION_OCTET_STREAM,
                audioFile.getName()
        );
        
        builder.addTextBody("model", modelID);
        builder.addTextBody("response_format", responseFormat);
        
        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(whisperWebServiceURL);        
        //httpPost.addHeader("Authorization", bearer + " " + authKey);
        httpPost.addHeader("Authorization", "Bearer " + authKey);
        httpPost.addHeader("Accept-Charset", "utf-8");
        //httpPost.addHeader("Content-Type", "multipart/form-data");
        httpPost.setEntity(builder.build());
        
        /*System.out.println(httpPost.toString());
        for (Header h : httpPost.getAllHeaders()){
            System.out.println(h.getName() + " : " + h.getValue());            
        }*/
        
        CloseableHttpResponse response = httpClient.execute(httpPost);
        
        
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        
        if (statusCode==200 && result != null) {
            // there have been recent changes to the Whisper/OpenAI REST (speech-to-text/transcription) service, and it's now returning JSON in its responses. 
            String resultAsString = EntityUtils.toString(result, "utf-8");            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(resultAsString);
            String text = jsonNode.get("text").asText();
    
            EntityUtils.consume(result);
            httpClient.close();
            
            
            return text;
        } else {
            // something went wrong, throw an exception
            System.out.println(EntityUtils.toString(result, "utf-8"));
            String reason = statusLine.getReasonPhrase();
            System.out.println(statusLine.getStatusCode());
            System.out.println(reason);
            throw new IOException(reason);
        }
    }
    
    public static void main(String[] args) throws IOException, URISyntaxException{
        GWDGWhisperConnector whisperConnector = new GWDGWhisperConnector("01dbe7000e962583449e1b5081f8615c");
        //File audioFile = new File("C:\\Users\\bernd\\Dropbox\\work\\2023_CMC_MANNHEIM\\AUDIO-2023-09-11-19-43-34.mp3");
        //File audioFile = new File("C:\\Users\\bernd\\Dropbox\\work\\2023_CMC_MANNHEIM\\TEST.wav");
        File audioFile = new File("C:\\Users\\bernd\\Dropbox\\work\\ZZZ_ERLEDIGT\\2023_CMC_MANNHEIM\\AUDIO-2023-09-11-19-42-53.m4a");
        
        if (audioFile.exists()){
            //String result = whisperConnector.callWhisper(audioFile, "whisper-1", "", "json", 0.2, "en");            
            String result = whisperConnector.callWhisperSimple(audioFile);            
            System.out.println(result);
        }
    }

        
    
    
    
    
}
