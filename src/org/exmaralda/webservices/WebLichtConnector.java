/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jdom.JDOMException;

/**
 *
 * @author Schmidt
 */
public class WebLichtConnector {
    
    // API KEY : ANcfAGdl166b++9ygC72clo2Xh1gLTqyikEjShc1KMAI
    // Make an http post request with the multipart/form-data encoding (eg with curl):
    // chains: downloaded from WebLicht in step 1.
    // content: input to the chain.
    // apikey: key obtained in step 2.
    // URL: https://weblicht.sfs.uni-tuebingen.de/WaaS/api/1.0/chain/process
    // result: output of last service in the chain
    // curl -X POST -F chains=@chains.xml -F content=@inputFile -F apikey=apiKey URL > result    
    
    public String webLichtURL = "https://weblicht.sfs.uni-tuebingen.de/WaaS/api/1.0/chain/process";
    
    // callMAUS with the given text file, audio file and additional parameters
    // return the String that represents the resulting text grid
    public String callWebLicht(File tcfFile, File chainFile, String apiKey) throws IOException, JDOMException{
        
        // setup a HTTP client
        HttpClient httpClient = new DefaultHttpClient();
        
        // construct a multipart entity that will be sent via POST to the web service
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);        
                        
        // add the TCF file
        FileBody fileBody = new FileBody(tcfFile, "application/xml");
        entity.addPart("content", fileBody);

        // add the chain file
        FileBody fileBody2 = new FileBody(chainFile, "application/xml");
        entity.addPart("chains", fileBody2);
        
        // add the API key
        entity.addPart("apikey", new StringBody(apiKey));

        //System.out.println("All entities added. ");
        
        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(webLichtURL);
        httpPost.setEntity(entity);
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        if (statusCode==200 && result != null) {
            String resultAsString = EntityUtils.toString(result, "UTF-8");            
            // read the XML result string
            //Document doc = FileIO.readDocumentFromString(resultAsString);
            //Element downloadLinkElement = (Element) XPath.selectSingleNode(doc, "//downloadLink");
            //String downloadLink = downloadLinkElement.getText();
            
            // now we have the download link - just need to get the content as text
            //String praatTextGridString = downloadText(downloadLink);
            
            result.consumeContent();
            httpClient.getConnectionManager().shutdown();        
            return resultAsString;
        } else {
            // something went wrong, throw an exception
            String reason = statusLine.getReasonPhrase();            
            System.out.println("Status code: " + statusLine.getStatusCode());
            System.out.println("Reason: " + reason);
            System.out.println("===============================");
            if (response.getEntity()!=null){
                System.out.println(response.getEntity().toString());
                throw new IOException(reason + ": " + response.getEntity().toString());
            }
            
            throw new IOException(reason);
        }

        
        
    }

    
    
    
}
