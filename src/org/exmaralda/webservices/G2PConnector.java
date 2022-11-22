/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.webservices;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jdom.JDOMException;

/* [https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/help]
----------------------------------------------------------------
runG2P
------------------
Description: Creates the canonical phonemic transcription based on the input txt and a trained statistical
            model based on a language specific lexicon.

Example curl call is:
curl -v -X POST -H 'content-type: multipart/form-data' -F except=@<filename> -F com=no -F align=no -F outsym=sampa -F stress=no -F lng=deu-DE -F syl=no -F embed=no -F iform=txt -F i=@<filename> -F imap=@<filename> -F tgrate=16000 -F lowercase=yes -F nrm=no -F oform=bpf -F featset=standard -F tgitem=ort 'https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runG2P'

Parameters:  [except] [com] [align] [outsym] [stress] [lng] [syl] [embed] [iform] i [imap] [tgrate] [lowercase] [nrm] [oform] [featset] [tgitem]

Parameter description: 
except: Option except: name of an exception dictionary file overwriting the g2p output. Format: 2 semicolon-separated columns word; transcription. Phonemes blank-separated. Example: sagt;z ' a x t.

com: [yes, no] 
Option com: yes/no decision whether <*> strings should be treated as annotation
                markers. If set to 'yes', then strings of this type are considered as annotation markers that are not
                processed but passed on to the output. The string * within the <*> 
                must not contain any white space characters.
                For oform='bpf' this means, that the markers appear in the ORT
                and KAN tier with a word index on their own. WebMAUS makes use of the markers < usb > (e.g.
                non-understandable word or other human noises) and < nib > (non-human noise)
                without the blanks between "usb", "nib" and the brackets "<" and ">" (which are
                needed for formatting reasons). All other markers <*> are modelled as silence, if you
                use runG2P for WebMAUS. Markers must not contain white spaces, and must be separated from word tokens
                by blanks. They do not need to be blank-separated from non-word tokens as punctuation.

align: [yes, no, maus] 
Option align: yes/no/sym decision whether or not the transcription is to be
                letter-aligned. Examples: if align is set to 'yes' the transcription for 'archaeopteryx' is 'A: _ k _ _
                I Q p t @ r I k+s', i.e. 'ar' is mapped to 'A: _', and 'x' to 'k+s'. If contained in the output,
                syllable boundaries and word stress are '+'-concatenated with the preceeding, resp. following symbol.
                'sym' causes a special symmetric alignment which is needed e.g. for MAUS rule training, i.e. word: a r
                c h a e o p t e r y x _; transcription: A: _ k _ _ I Q p t @ r I k s. Syllable boundaries and word
                stress are not part of the output of this 'sym' alignment. For the output formats 'tab', 'exttab',
                'lex', and 'extlex' also the aligned orthography is letter-splitted to account for multi-character
                letters in languages as Hungarian.

outsym: [sampa, x-sampa, maus-sampa, ipa, arpabet] 
Ouput phoneme symbol inventory. The language-specific SAMPA variant is the default. Alternatively, language independent X-SAMPA and IPA can be chosen. MAUS-SAMPA maps the output to a language-specific phoneme subset that WEBMAUS can process. ARPABET is supported for eng-US only.

stress: [yes, no] 
Option stress: yes/no decision whether or not word stress is to be added to the output
                transcription. Stress is marked by simple apostrophs that are inserted into the transcription with
                separating blanks.

lng: [cat, deu, eng, fin, hat, hun, ita, mlt, nld, nze, pol, sqi-SQ, aus-AU, eus-ES, eus-FR, cat-ES, nld-NL, eng-US, eng-AU, eng-GB, eng-NZ, ekk-EE, fin-FI, fra-FR, kat-GE, deu-DE, gsw-CH-BE, gsw-CH-BS, gsw-CH-GR, gsw-CH-SG, gsw-CH-ZH, gsw-CH, hat-HT, hun-HU, ita-IT, jpn-JP, gup-AU, ltz-LU, mlt-MT, nor-NO, pol-PL, ron-RO, rus-RU, slk-SK, spa-ES, guf-AU, und] 
Option lng: RCFC5646 locale code of the speech in the input parameters; defines the
                possible SAMPA phoneme symbol set in input; we use the RFC5646 sub-structure 'iso639-3 - iso3166-1 [ -
                iso3166-2], e.g. 'eng-US' for American English, 'deu-AT-1' for Austrian German spoken in
                'Oberoesterreich'; alternatively, Iso 639-3 char language code is supported; the code 'gsw-CH' denotes
                text written in Swiss German 'Dieth'; the code 'und' (undefined) allows the user to upload a customized 
                mapping from orthographic to phonologic form (see option '-imap'); for backwards compatibility some older non-standard codes are
                still supported: 'nze' stands for New Zealand English, 'use' for American English.

syl: [yes, no] 
Option syl: yes/no decision whether or not the output transcription is to be syllabified.
                Syllable boundaries '.' are inserted into the transcription with separating blanks.

embed: [no, maus] 
Option embed: Macro option for embedding G2P into WEBMAUS. If set to 'maus', it overwrites
                several basic options as follows: 'stress', 'syl', and 'align' are set to 'no'. 'oform' is set to 'bpfs'. 'outsym' is set to 'maus-sampa'.
                Small single letters are transcribed as word fragments instead of spelling.

iform: [txt, bpf, list, tcf, tg] 
Option iform: Accepted input formats for grapheme phoneme conversion. 'txt' indicates
                connected text input, which will be tokenized before the conversion. 'list' indicates a sequence of
                unconnected words, that does not need to be tokenized. Furthermore, 'list' requires a different
                part-of-speech tagging strategy than 'txt' for the extraction of the 'extended' feature set (see
                Parameter 'featset'). 'tcf' indicates, that the input format is TCF containing at least a tokenization
                dominated by the element 'tokens'. 'tg' indicates TextGrid input. Long and short format is supported.
                For TextGrid input additionally the name of the item containing the words to be transcribed is to be
                specified by the parameter 'tgname'. In combination with 'bpf' output format 'tg' input additionally
                requires the specification of the sample rate by the parameter 'tgrate'. Input format 'bpf' indicates
                BAS partitur file input containing an ORT tier to be transcribed.

i: Orthographic text of the utterance to be converted; words are white space separated;
                encoding is utf-8; can contain punctuation which is ignored in some output formats (see parameter
                oform). Numbers will internally be converted to letters.

imap: Customized mapping table from orthography to phonology. If the option 'lng' is set to 'und' (undefined), a G2P mapping table must be provided via this option. This mapping table is used then to translate the input text into phonological symbols. See http://www.bas.uni-muenchen.de/Bas/readme_g2p_mappingTable.txt for details about the format of the mapping table.

tgrate: [0, 999999] 
Option tgrate: Only needed, if 'iform' is 'tg' and oform is 'bpf(s)'. 
                Sample rate of the corresponding speech signal; needed to
                convert time values from TextGrid to sample values in BAS Partitur Format (BPF) file.
                If you don't know the sample rate, look in the Properties/Get Info list of the sound file.

lowercase: [yes, no] 
Option lowercase: yes/no decision whether orthographic input is treated case sensitive (no) or not (yes). Applies only, if the option 'lng' is set to 'und' and a customized mapping table is loaded via option 'imap'.

nrm: [yes, no] 
Text normalization. Currently available for German and English only.
                Detects and expands 22 non-standard word types. All output file types supported but
                not available for the following tokenized input types: bpf, TextGrid, and tcf. If
                switched off, only number expansion is carried out.

oform: [txt, tab, exttab, lex, extlex, bpf, bpfs, extbpf, extbpfs, tcf, exttcf, tg, exttg] 
Option oform: 'bpf' indicates the BAS Partitur Format (BPF) file with an ORT/KAN tier. The
                tiers contains a table with 3 columns and one line per word in the input. Column 1 is always 'ORT:/KAN:';
                column 2 is an integer starting with 0 denoting the word position within the input; column 3 contains
                for ORT the (possibly normalized) orthographic word, for KAN the canonical pronunciation of the word coded in 
                SAM-PA (or IPA); the latter does not contain blanks. oform 'bpfs'
                differs from 'bpf' only in that respect, that the phonemes in KAN are separated by blanks. In case of TextGrid
                input, both 'bpf' and 'bpfs' require the additional parameters 'tgrate' and 'tgitem'. Additionally, the content of
                the TextGrid tier 'tgitem' is stored as a word chunk segmentation in the BPF tier TRN. oform 'extbpf' or
                'extbpfs' extend the BPF output file by the tiers POS (part of speech, STTS tagset), KSS (full phonemic 
                transcript including e.g. lexical accent), TRL (orthographic transcript with punctuation), and MRP (morph
                segmentation and classes). oform 'txt'
                cause a replacement of the input words by their phonemic transcriptions; single line output without
                punctuation, where phonemes are separated by blanks and words by tabulators. oform 'tab' returns the grapheme
                phoneme conversion result in form of a table with two columns. The first column comprises the words,
                the second column their blank-separated transcriptions. oform 'exttab' results in a 5-column table. The
                columns contain from left to right: word, transcription, part of speech, morpheme segmentation, and
                morpheme class segmentation. oform 'lex' transforms the table to a lexicon, i.e. words are unique and
                sorted. oform 'extlex' provides the same information as 'exttab' in a unique and sorted manner. For all lex
                and tab outputs columns are separated by ';'. If option 'align' is switched on, the first (word) column is
                letter-segmented. oform 'tcf' creates either a tcf output file from scratch (in case iform
                is not 'tcf'), or a transcription tier is added to the input tcf file. If a tcf file is generated
                from scratch, it contains the elements 'text', 'tokens', and 'BAS_TRS' for the phonemic transcription. 
                oform 'exttcf' additionally adds the elements 'BAS_POS' (part of speech, STTS tagset), 'BAS_MORPH' (morph 
                segmentation), and 'BAS_MORPHCLASS' (morph classes). oform 'tg' and 'exttg' produce TextGrid output; 
                for this a TextGrid input (iform 'tg') is required. With oform 'tg' the tier 'BAS_TRS' (phonemic 
                transcript) is inserted to the TextGrid which runs parallel to the tier
                specified by the parameter 'tgitem'; words are separated by an '#' symbol. oform 'exttg' adds
                the tiers 'BAS_POS', 'BAS_MORPH', and 'BAS_MORPHCLASS' parallel to 'BAS_TRS'. Their content is
                the same as for oform 'exttcf'. The 'extended' oform versions 'exttab', 'extlex', 'exttcf', and 'exttg' 
                are only available for 
                languages deu|eng-*|aus|nze|use; for the other languages these formats are replaced by the corresponding
                non-extended format. While the output contains punctuation for 'exttab', 'tcf', 'exttcf', and 'exttg'
                for the other formats it is ignored.

featset: [standard, extended] 
Option featset: Feature set used for grapheme-phoneme conversion. The standard set is the
                default and comprises a letter window centered on the grapheme to be converted. The extended set
                additionally includes part of speech and morphological analyses. The extended set is currently
                available for German and British English only. For connected text the extended feature set generally
                generally yields better performance. However, if the input comprises a high amount of proper names
                provoking erroneous part of speech tagging and morphologic analyses, than the standard feature set is
                more robust.

tgitem: Option tgitem: Only needed, if 'iform' is 'tg'. Name of the TextGrid tier (item), that contains
                the words to be transcribed. In case of TextGrid output, this item is the reference for the added
                items.

Output: A XML response containing the elements "success", "downloadLink", "output"
                and "warning". "success" states if the processing was successful or not, "downloadLink" specifies the
                location where the file containing the phonemic transcription in SAM-PA (segmented in words) can be
                found (the format of the file depends on the option selected in oform), "output" contains the output that
                is mostly useful during debugging errors, and "warnings" contains any warnings that occured during the processing.
                The format of the output file depends on the value of input parameter oform.
----------------------------------------------------------------
*/


/**
 *
 * @author Thomas_Schmidt
 */
public class G2PConnector {
    
    public String g2pURL = "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runG2P";
    
    public String callG2P(File inFile, HashMap<String, Object> otherParameters) throws IOException, JDOMException{
        
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();       
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();  
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        
        if (otherParameters!=null){
            for (String parameterName : otherParameters.keySet()){
                String parameterValue = (String) otherParameters.get(parameterName);
                builder.addTextBody(parameterName, parameterValue);
            }
            //builder.addTextBody("lng", (String) otherParameters.get("lng"));
        }
        
        // add the text file
        //builder.addTextBody("iform", (String) otherParameters.get("iform"));
        builder.addBinaryBody("i", inFile);
        
        // construct a POST request with the multipart entity
        HttpPost httpPost = new HttpPost(g2pURL);        
        httpPost.setEntity(builder.build());
        System.out.println("Sending " + httpPost.toString());
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity result = response.getEntity();     
        
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();

        if (statusCode==200 && result != null) {
            String resultAsString = EntityUtils.toString(result);
            BASWebServiceResult basResult = new BASWebServiceResult(resultAsString);
            if (!(basResult.isSuccess())){
                String errorText = "Call to G2P was not successful: " + resultAsString;
                throw new IOException(errorText);                                
            }
            String bpfOutString = basResult.getDownloadText();

            EntityUtils.consume(result);
            httpClient.close();
            
            return bpfOutString;
        } else {
            // something went wrong, throw an exception
            String reason = statusLine.getReasonPhrase();
            throw new IOException(reason);
        }
    }        
    
    
}
