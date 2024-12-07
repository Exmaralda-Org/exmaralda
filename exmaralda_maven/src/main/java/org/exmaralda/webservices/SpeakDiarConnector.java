/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*

runSpeakDiar
------------------
Description: This services reads a media file (sound, video) and performs a speaker diarization (SD) based on the pyannote python library. https://github.com/pyannote, paper: https://arxiv.org/abs/1911.01255. This wrapper uses the pre-trained pyannote RNN models for both speaker change detection and speaker diarization. The models have been trained on the AMI corpus (http://groups.inf.ed.ac.uk/ami/corpus/). The speaker embedding model has also been pre-trained by pyannote on the voxceleb corpus (embedding: https://arxiv.org/abs/2003.14021, corpus: http://www.robots.ox.ac.uk/~vgg/data/voxceleb/). If no number of speakers is supplied, the system defaults to the pyannote implementation of affinity propagation clustering. If the number of speakers is known, the system has been extended to employ spectral clustering, with the normalized maximum eigengap approach described in https://arxiv.org/pdf/2003.02405.pdf. The input signal is first classified frame-by-frame, then the resulting label chain is smoothed to result in realistic segments of speech and silence; the options 'minSilenceLength', 'minVoiceLength' and 'preference' can be used to adjust the process (see descriptions of options). The resulting diarization is encoded in a single segmentation tier ('SPD', overlapping speakers are not detected!) which is returned in the selected format (option 'OUTFORMAT'). If a BPF file is input (option 'TEXT'), the SPD tier is appended to the input BPF; if the BPF contains a word segmentation (ORT/MAU), the SPD segments are matched against the word segments and a SPK tier is created. The option 'speakMatch' can be used to re-label the standard speaker labels 'S1', 'S2', .... For details about the BAS Partitur Format (BPF) see http://www.bas.uni-muenchen.de/forschung/Bas/BasFormatseng.html. 
            

Example curl call is:
curl -v -X POST -H 'content-type: multipart/form-data' -F SIGNAL=@<filename> -F speakMatch= -F speakNumber=0 -F OUTFORMAT=bpf -F SAMPLERATE=1 -F preference=-2.97 -F TEXT=@<filename> -F minSilenceLength=200 -F minVoicedLength=200 'https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runSpeakDiar'

Parameters:  SIGNAL [speakMatch] [speakNumber] [OUTFORMAT] [SAMPLERATE] [preference] [TEXT] [minSilenceLength] [minVoicedLength]

Parameter description: 
SIGNAL: Required input SIGNAL: sound or video file containing the speech signal to 
                be speaker diarized. Although the mimetype of this input file is restricted to RIFF AUDIO 
		audio/x-wav (extension wav), all media formats that are supported by BAS WebService 
		AudioEnhance are accepted.

speakMatch: Option speakMatch: if set to a list of comma separated names (e.g.
		speakMatch='Anton,Berta,Charlie', the corresponding speaker labels found by the 
		service in the order of appearance are replaced by these names (e.g. 'S1' to 'Anton',
		'S2' to 'Berta' etc.). This allows the user to create SD annotation using her self
		defined speaker labels, if the user knows the order of appearance; it is obvious
		that this feature only makes sense in single file processing, since the speaker labels
		and the order of appearance differ from one recording to the next; the suggested mode
		of operation is to run the service in batch mode over all recordings with speakMatch="",
		then inspect manually the resulting annotation and define speaker labels in the order of 
		appearance for each recording, and then run the service in single file mode for each 
		recording again with the corresponding speakMatch list. If the speakMatch option contains 
		a comma separated list of value pairs like 'S1:Anton', only the speaker labels listed 
		on the lefthand side of each pair are patched, e.g. for speakMatch='S3:Charlie,S6:Florian'
		only the third and sixth appearing speaker are renamed to Charlie and Florian respectively.
                

speakNumber: [0.0, 999999.0] 
Option speakNumber restricts the number of detected speakers to the 
		given number. If set to 0 (default), the SD method determines the number automatically.
	        

OUTFORMAT: [bpf, exb, csv, TextGrid, emuDB, eaf, tei] 
Option 'Output format' (OUTFORMAT): Defines the possible output formats: TextGrid - a praat compatible
                TextGrid file; bpf - a (input) BPF file with new (or replaced) tier(s) SPD (and SPK if BPF was input); csv - a spreadsheet
                (CSV table) that contains the most important information; 
                emuDB - an Emu compatible *_annot.json file;
                eaf - an ELAN compatible annotation file; exb - an EXMARaLDA compatible annotation file;
                tei - Iso TEI document (XML).
                For a description of BPF see
                http://www.bas.uni-muenchen.de/forschung/Bas/BasFormatseng.html.
                for a description of Emu see https://github.com/IPS-LMU/emuR.
                Note 1: using 'emuDB' will first produce only single annotation file *_annot.json;
                in the WebMAUS interface (https://clarin.phonetik.uni-muenchen.de/BASWebServices) you can process
                more than one file and than download a zipped Emu database; in this case don't forget to change the default
                name of the emuDB 'MAUSOUTPUT' using the R function emuR::rename_emuDB().
                Note 2: if you need the same result in more than one format, select 'bpf' to produce a BPF file, and then
                convert this file with the service runAnnotConv ('AnnotConv') into the desired formats.
                Note 3: some format conversions are not loss-less; select 'bpf' to be sure that no information is lost.
                

SAMPLERATE: [0.0, 999999.0] 
Option SAMPLERATE of signal file: if the sample rate cannot be determined 
                automatically from SIGNAL, you can provide the 
		sampling rate via this option. Usually you can leave it to the default value of 1.
	        

preference: Preference for affinity propagation clustering. Higher
                        values causes more speakers detected (e.g. -1.0). Often set to median of
                        distances (here cosine dist between d-vectors), but
                        we tuned it to a fixed value on the development set
                        of the AMI corpus.
	        

TEXT: Optional BPF input: BAS Partitur Format (BPF) file (*.par or *.bpf) to which
		the SD result is appended to and copied to output (possibly converted to another format). 
		If the BPF contains a word segmentation (tier ORT/MAU), the service matches the SD result
		against the word segmentation and creates a word-wise SD labelling (SPK tier) based on 
		maximum overlap. 
		See http://www.bas.uni-muenchen.de/forschung/Bas/BasFormatseng.html for detailed 
		description of the BPF.

minSilenceLength: [0.0, 999999.0] 
Minimum length of silence intervalls in msec in the resulting diariziation.
	        

minVoicedLength: [0.0, 999999.0] 
Minimum length of voiced intervalls in msec to be classified as a speaker in the resulting diariziation.
	        

Output: A XML response containing the elements "success", "downloadLink", "output" and "warning".
                "success" states if the processing was successful or not, "downloadLink" specifies the location where
                the result file can be found which contains the speaker diarization result.
                The format of the annotation file depends on the option selected in 
                OUTFORMAT. "output" contains the output that is mostly useful during debugging errors and "warning" 
                lists warnings, if any occured during the processing. 
                

*/

package org.exmaralda.webservices;

/**
 *
 * @author thomas.schmidt
 */
public class SpeakDiarConnector {
    
}
