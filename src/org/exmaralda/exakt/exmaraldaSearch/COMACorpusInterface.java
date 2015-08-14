/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.exakt.exmaraldaSearch;

import java.util.HashSet;
import java.util.Set;
import org.exmaralda.exakt.search.CorpusInterface;

/**
 *
 * @author thomas
 */
public interface COMACorpusInterface extends CorpusInterface {

    public boolean isWordSegmented();

    public String getWordSegmentName();

    public int getNumberOfTranscriptions();
    
    public int getNumberOfSegmentChains();

    public String getUniqueSpeakerIdentifier();
    
    String getCorpusName();

    String getCorpusPath();

    
    /** returns all names of annotation tiers in this corpus */
    HashSet<String> getAnnotationNames();

    /** returns all names of description tiers in this corpus */
    HashSet<String> getDescriptionNames();

    /** returns all names of segmenations in this corpus */
    HashSet<String> getSegmentationNames();

    /** returns all communication metadata attributes in this corpus */
    Set<String> getCommunicationAttributes();

    /** returns the communication metadata value of the given attribute name for the given transcription */
    String getCommunicationData(String transcriptionLocator, String attributeName);

    /** returns all speaker metadata attributes in this corpus */
    Set<String> getSpeakerAttributes();

    /** returns the speaker metadata value of the given attribute name for the given transcription */
    String getSpeakerData(String transcriptionLocator, String speakerID, String attributeName);

    /** returns all transcription metadata attributes in this corpus */
    Set<String> getTranscriptionAttributes();

    /** returns the transcription metadata value of the given attribute name for the given transcription */
    String getTranscriptionData(String transcriptionLocator, String attributeName);







}
