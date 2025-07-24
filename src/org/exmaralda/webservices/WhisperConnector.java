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
public class WhisperConnector {
    
    /*
        Create transcription
        POST

        https://api.openai.com/v1/audio/transcriptions

        Transcribes audio into the input language.

        Request body
        file
        file
        Required
        The audio file object (not file name) to transcribe, in one of these formats: flac, mp3, mp4, mpeg, mpga, m4a, ogg, wav, or webm.

        model
        string
        Required
        ID of the model to use. Only whisper-1 is currently available.

        prompt
        string
        Optional
        An optional text to guide the model's style or continue a previous audio segment. The prompt should match the audio language.

        response_format
        string
        Optional
        Defaults to json
        The format of the transcript output, in one of these options: json, text, srt, verbose_json, or vtt.

        temperature
        number
        Optional
        Defaults to 0
        The sampling temperature, between 0 and 1. Higher values like 0.8 will make the output more random, while lower values like 0.2 will make it more focused and deterministic. If set to 0, the model will use log probability to automatically increase the temperature until certain thresholds are hit.

        language
        string
        Optional
        The language of the input audio. Supplying the input language in ISO-639-1 format will improve accuracy and latency.

        Returns
        The transcriped text.    
    */

    public String whisperWebServiceURL = "https://api.openai.com/v1/audio/transcriptions"; 
    
    private String authKey = "";

    public WhisperConnector(String authKey) {
        this.authKey = authKey;
    }
    
    public String callWhisperSimple(File audioFile) throws IOException, URISyntaxException {
        String result = callWhisper(audioFile, "whisper-1", "", "json", 0.2, "en");    
        return result;
    }
    
    public String callWhisperRegular(File audioFile, String language, String prompt) throws IOException, URISyntaxException {
        String result = callWhisper(audioFile, "whisper-1", prompt, "json", 0.2, language);    
        // {"text":"Here we go, here's my voice message. So why do you need that?"}
        return result;
    }


    public String callWhisper(File audioFile, 
            String modelID, 
            String prompt, 
            String responseFormat,
            double temperature,
            String language) throws IOException, URISyntaxException{
        
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
        builder.addTextBody("prompt", prompt);
        builder.addTextBody("response_format", responseFormat);
        builder.addTextBody("temperature", Double.toString(temperature));
        if (!(language.equals(("--")))){
            builder.addTextBody("language", language);
        }
        
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
        WhisperConnector whisperConnector = new WhisperConnector("");
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
