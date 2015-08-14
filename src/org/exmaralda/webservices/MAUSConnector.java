/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Schmidt
 */
public class MAUSConnector {
    
    //String webMausURL = "http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUSBasicGerman";
    public String webMausURL = "http://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUSBasic";
    
    // callMAUS with the given text file, audio file and additional parameters
    // return the String that represents the resulting text grid
    public String callMAUS(File textFile, File audioFile, HashMap<String, Object> otherParameters) throws IOException, JDOMException{
        
        // setup a HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        
        // construct a multipart entity that will be sent via POST to the web service
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);        
        
        if (otherParameters!=null){
            // add the other parameters
            //for (String[] parameter : otherParameters){
            //    entity.addPart(parameter[0], new StringBody(parameter[1]));            
            //}
            entity.addPart("LANGUAGE", new StringBody((String) otherParameters.get("LANGUAGE")));
        }
        
        
        // add the text file
        FileBody fileBody = new FileBody(textFile, "application/octet-stream");
        entity.addPart("TEXT", fileBody);

        // ad the audio file
        FileBody fileBody2 = new FileBody(audioFile, "application/octet-stream");
        entity.addPart("SIGNAL", fileBody2);

        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(webMausURL);
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        if (statusCode==200 && result != null) {
            String resultAsString = EntityUtils.toString(result);
            // should look something like this
            /* <WebServiceResponseLink>
                <success>true</success>
                <downloadLink>http://clarin.phonetik.uni-muenchen.de:80/BASWebServices//data////2014.08.08_12.22.07_8B379380523B78A43F1BFC622390561E//DS--_E_00064_SE_01_A_01_DF_01_537.TextGrid</downloadLink>
                <output>/usr/local/bin/maus OUTIPA=false LANGUAGE=deu
                    OUT=/var/lib/tomcat7/webapps/BASWebServices//data//2014.08.08_12.22.07_8B379380523B78A43F1BFC622390561E//DS--_E_00064_SE_01_A_01_DF_01_537.TextGrid
                    MINPAUSLEN=5 USETRN=false
                    SIGNAL=/var/lib/tomcat7/webapps/BASWebServices//data//2014.08.08_12.22.07_8B379380523B78A43F1BFC622390561E//DS--_E_00064_SE_01_A_01_DF_01_537.WAV
                    STARTWORD=0 ENDWORD=999999 INSPROB=0.0 OUTFORMAT=TextGrid
                    BPF=/var/lib/tomcat7/webapps/BASWebServices//data//2014.08.08_12.22.07_8B379380523B78A43F1BFC622390561E//DS--_E_00064_SE_01_A_01_DF_01_537.par
                    INSKANTEXTGRID=true MAUSSHIFT=10.0 CANONLY=false INSORTTEXTGRID=true</output>
                <warnings/>
            </WebServiceResponseLink> */
            
            
            // read the XML result string
            Document doc = FileIO.readDocumentFromString(resultAsString);
            Element downloadLinkElement = (Element) XPath.selectSingleNode(doc, "//downloadLink");
            String downloadLink = downloadLinkElement.getText();
            
            // now we have the download link - just need to get the content as text
            String praatTextGridString = downloadText(downloadLink);
            
            result.consumeContent();
            httpClient.getConnectionManager().shutdown();        
            return praatTextGridString;
        } else {
            // something went wrong, throw an exception
            String reason = statusLine.getReasonPhrase();
            throw new IOException(reason);
        }

        
        
    }

    private String downloadText(String downloadLink) throws IOException {
        
        StringBuilder result = new StringBuilder();
        
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
        HttpGet request = new HttpGet(downloadLink);

        // add request header
        //request.addHeader("User-Agent", "");
        //request.addHeader(Content-Type: text/html; charset=UTF-8)
        HttpResponse response = client.execute(request);

        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));
            String sCurrentLine;
            while ((sCurrentLine = rd.readLine()) != null) {
              result.append(sCurrentLine);
              result.append("\n");
            }
        } else {
            // something went wrong, throw an exception
            String reason = statusLine.getReasonPhrase();
            throw new IOException(reason);
        }

        
        return result.toString();        
    }
    
    
    
}
