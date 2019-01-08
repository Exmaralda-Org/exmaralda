/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.exmaralda.exakt.utilities.FileIO;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/**
 *
 * @author Thomas_Schmidt
 */
public class BASWebServiceResult {
    
    /*
        <WebServiceResponseLink>
            <success>true</success>
            <downloadLink>https://clarin.phonetik.uni-muenchen.de:443/BASWebServices/data/2019.01.03_14.51.16_76BEC32A3AA44B83114C598BA451A1F4/IS--_E_00021_SE_01_T_01.par</downloadLink>
            <output/>
            <warnings/>
        </WebServiceResponseLink>            
    */
        
    boolean success;
    String downloadLink = "";
    
    public BASWebServiceResult(String xml) throws JDOMException, IOException{
        // read the XML result string
        Document doc = FileIO.readDocumentFromString(xml);
        // check if success == true
        Element successElement = (Element) XPath.selectSingleNode(doc, "//success");
        success =  (successElement!=null) && successElement.getText().equals("true");
        
        if (success){
            Element downloadLinkElement = (Element) XPath.selectSingleNode(doc, "//downloadLink");
            downloadLink = downloadLinkElement.getText();
        }       
    }
    
    public boolean isSuccess(){
        return success;
    }
    
    public String getDownloadLink(){
        return downloadLink;
    }
    
    public String getDownloadText() throws IOException{
        StringBuilder result = new StringBuilder();
        
        HttpClient client = HttpClientBuilder.create().build(); // new DefaultHttpClient();
        HttpGet request = new HttpGet(downloadLink);
        request.addHeader("http.protocol.content-charset", "UTF-8");

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
