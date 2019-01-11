/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;

/*

----------------------------------------------------------------
runChunker
------------------
Description: The chunker is a preprocessing tool for the MAUS segmentation service that splits very long recordings into smaller 'chunks'. Since MAUS's runtime grows quite fast with input duration, it cannot be used on recordings that are longer than 3000 words. In this case, the chunker can presegment the recording into short "chunks". This chunk segmentation, which is NOT a semantically meaningful sentence or turn segmentation, can then be used to speed up the MAUS segmentation process. Like MAUS, the chunker accepts a mono WAVE or NIST/SPHERE signal and a BAS Partitur file containing a canonical transcription of the recording. This canonical transcription can be derived from an orthographic text using the G2P tool (runG2P). The chunker outputs a new BAS Partitur File that can be used as input to MAUS.

Example curl call is:
curl -v -X POST -H 'content-type: multipart/form-data' -F minanchorlength=3 -F aligner=hirschberg -F boost_minanchorlength=4 -F audio=@<filename> -F silenceonly=0 -F bpf=@<filename> -F boost=true -F force=false -F language=deu-DE -F insymbols=sampa -F minchunkduration=15 'https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runChunker'

Parameters:  [minanchorlength] [aligner] [boost_minanchorlength] audio [silenceonly] bpf [boost] [force] [language] [insymbols] [minchunkduration]

Parameter description: 
minanchorlength: [2, 8] 
The chunker performs speech recognition and symbolic alignment to find regions of correctly aligned words (so-called 'anchors'). 
Setting this parameter to a high value (e.g. 4-5) means that the chunker finds chunk boundaries with higher certainty. 
However, the total number of discovered chunk boundaries may be reduced as a consequence. 
A low value (e.g. 2) is likely to lead to a more fine-grained chunking result, but with lower confidence for individual chunk boundaries.

aligner: [hirschberg, fast] 
Symbolic aligner to be used. The "fast" aligner performs approximate alignment by splitting the alignment matrix into "windows" of size 5000*5000. The "hirschberg" aligner performs optimal matching. On recordings below the 1 hour mark, the choice of aligner does not make a big difference in runtime. On longer recordings, you can improve runtime by selecting the "fast" aligner. Note however that this choice increases the probability of errors on recordings with untranscribed stretches (such as long pauses, musical interludes, untranscribed speech). Therefore, the "hirschberg" aligner should be used on this kind of material.

boost_minanchorlength: [2, 8] 
If you are using the boost phase, you can set its minimum anchor length independently of the general minimum anchor length. 
Setting this parameter to a low value (e.g. 2-3) means that the boost phase has a greater chance of finding preliminary chunk boundaries, 
which is essential for speeding up the chunking process. 
On the other hand, high values (e.g. 5-6) lead to more conservative and more reliable chunking decisions. 
If boost is set to false, this option is ignored.

audio: Mono WAVE or NIST/SPHERE sound file or video file (MP4,MPEG) containing the speech signal to be segmented. PCM 16 bit resolution, any sampling rate.

silenceonly: If set to a value greater than 0, the chunker will only place chunk boundaries in regions 
where it has detected a silent interval of at least that duration (in ms). 
Else, silent intervals are prioritized, but not to the exclusion of word boundaries without silence. 
On speech that has few silent pauses (spontaneous speech or speech with background noise), 
setting this parameter to a number greater than 0 is likely to hinder the discovery of chunk boundaries. 
On careful and noise-free speech (e.g. audio books) on the other hand, setting this parameter to a sensible value (e.g. 200) may reduce chunkin errors.

bpf: Phonemic transcription of the utterance to be segmented. Format is a BAS Partitur Format (BPF) file with a KAN tier. The KAN tier contains a table with 3 columns and one line per word in the input. Column 1 is always 'KAN:'; column 2 is an integer starting with 0 denoting the word position within the input; column 3 contains the canonical pronunciation of the word coded in SAM-PA. The canonical pronunciation string may contain phoneme-separating blanks. For supported languages, the BPF can be derived using the G2P service (runG2P). See http://www.bas.uni-muenchen.de/forschung/Bas/BasFormatseng.html for detailed description of the BPF.

boost: [true, false] 
If set to true (the default), the chunker will start by running a so-called boost phase over the recording. This boost phase uses a phoneme-based decoder instead of speech recognition. Usually, the boost option reduces processing time. On noisy input or faulty transcriptions, the boost option can lead to an increase in errors. In this case (or if a previous run with boost set to 'true' has led to chunking errors), set this option to 'false'.

force: [true, false, rescue] 
If this parameter is set to true, the chunker will run in the experimental 'forced chunking' mode. While forced chunking is much more likely to return a fine-grained chunk segmentation, it is also more prone to chunking errors. As a compromise, you can also set this parameter to 'rescue'. In this case, the forced chunking algorithm is only invoked when the original algorithm has returned chunks that are too long for MAUS.

language: [aus-AU, eus-ES, eus-FR, cat-ES, nld-BE, nld-NL, eng-US, eng-AU, eng-GB, eng-NZ, eng-SC, ekk-EE, fin-FI, fra-FR, kat-GE, deu-DE, gsw-CH, gsw-CH-BE, gsw-CH-BS, gsw-CH-GR, gsw-CH-SG, gsw-CH-ZH, hun-HU, ita-IT, jpn-JP, sampa, ltz-LU, mlt-MT, nor-NO, pol-PL, por-PT, ron-RO, rus-RU, spa-ES] 
Language of the speech to be processed. This parameter defines the set of possible input phonemes and their acoustic models. RFC5646 sub-structure 'iso639-3 - iso3166-1 [- iso3166-2], e.g. 'eng-US' for American English. The language code 'sampa' (not RCFC5646) denotes a language independent SAM-PA variant of MAUS for which the SAM-PA symbols in the input BPF must be blank separated (e.g. /h OY t @/).

insymbols: [sampa, ipa] 
Defines the encoding of phonetic symbols in the input KAN tier. If set to 'sampa' (default), phonetic symbols are encoded in language specific SAM-PA (with some coding differences to official SAM-PA; use service runMAUSGetInventar with option LANGUAGE=sampa to get a list of symbols and their mapping to IPA). If set to 'ipa', the service expects blank-separated UTF-8 IPA.

minchunkduration: Lower bound for output chunk duration in seconds. Note that the chunker does not guarantee an upper bound on chunk duration.

Output: An XML response containing the tags "success", "downloadLink", "output" and "warning". "success" states whether the processing was successful or not, "downloadLink" specifies the location where the output BPF file is provided. The BPF contains the content of the input BPF (option "bpf") with an appended TRN tier. The TRN tier contains the discovered chunking of the signal. The output BPF can be used as an input BPF to runMAUS together with the option USETRN=true.
----------------------------------------------------------------

*/

/**
 *
 * @author Thomas_Schmidt
 */
public class BASChunkerConnector {
    
    public String chunkerURL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runChunker";
    
    public String callChunker(File bpfInFile, File audioFile, HashMap<String, Object> otherParameters) throws IOException, JDOMException{
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	System.out.println("Chunker called at " + dateFormat.format(date)); //2016/11/16 12:08:43        System.out.println("Chunker called at " + Date.);
        
        
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();       
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        if (otherParameters!=null){
            builder.addTextBody("language", (String) otherParameters.get("language"));
        }
        
        builder.addTextBody("aligner", "fast");
        builder.addTextBody("force", "rescue");
        builder.addTextBody("minanchorlength", "2");
        builder.addTextBody("boost_minanchorlength", "3");
        
        
        // add the text file
        builder.addBinaryBody("bpf", bpfInFile);
        
        // add the audio file
        builder.addBinaryBody("audio", audioFile);
        
        System.out.println("All parameters set. ");
        
        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(chunkerURL);        
        httpPost.setEntity(builder.build());
        
        System.out.println("URI: " + httpPost.getURI().toString());
        
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        if (statusCode==200 && result != null) {
            String resultAsString = EntityUtils.toString(result);
            
            /*
                <WebServiceResponseLink>
                    <success>true</success>
                    <downloadLink>https://clarin.phonetik.uni-muenchen.de:443/BASWebServices/data/2019.01.03_15.58.41_9D4EECAD0791F9E9ED16DF35E66D1485/IDS_ISW_Chunker_Test_16kHz_OHNE_ANFANG.par</downloadLink>
                    <output/>
                    <warnings/>
                </WebServiceResponseLink>
            */

            // read the XML result string
            Document doc = FileIO.readDocumentFromString(resultAsString);
            
            // check if success == true
            Element successElement = (Element) XPath.selectSingleNode(doc, "//success");
            if (!((successElement!=null) && successElement.getText().equals("true"))){
                String errorText = "Call to BASChunker was not successful: " + IOUtilities.elementToString(doc.getRootElement(), true);
                throw new IOException(errorText);                
            }
            
            Element downloadLinkElement = (Element) XPath.selectSingleNode(doc, "//downloadLink");
            String downloadLink = downloadLinkElement.getText();
            
            // now we have the download link - just need to get the content as text
            String bpfOutString = downloadText(downloadLink);
            
            

            EntityUtils.consume(result);
            httpClient.close();
            
            return bpfOutString;
            //return resultAsString;
        } else {
            // something went wrong, throw an exception
            String reason = statusLine.getReasonPhrase();
            throw new IOException(reason);
        }
        
        
        
    }
    
    private String downloadText(String downloadLink) throws IOException {
        
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
