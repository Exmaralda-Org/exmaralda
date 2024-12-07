/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.exmaraldaSearch;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.exmaralda.exakt.search.CorpusComponentInterface;
import org.exmaralda.exakt.search.DBCorpusComponent;
import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.exakt.search.SearchableSegmentLocatorInterface;

/**
 *
 * @author hanna
 */
public class COMADBCorpus implements COMACorpusInterface {

    private String pre = "ex_";
    private String undefined = "#undefined";
    private String corpusName = "";
    private String corpusPath = "";
    private int transcriptionsNumber;
    private int segmentChainsNumber;
    private HashSet<String> segmentationNames = new HashSet<String>();
    private HashSet<String> annotationNames = new HashSet<String>();
    private HashSet<String> descriptionNames = new HashSet<String>();
    private HashSet<String> segmentNames = new HashSet<String>();
    private HashMap<String, String> communicationAttributes = new HashMap<String, String>();
    private HashMap<String, String> speakerAttributes = new HashMap<String, String>();
    private HashMap<String, String> transcriptionAttributes = new HashMap<String, String>();
    private Connection conn;
    private boolean next;
    private DBCorpusComponent corpusComponent;
    private ArrayList<SearchListenerInterface> listenerList = new ArrayList<SearchListenerInterface>();

    /** Creates a new instance of COMADBCorpus */
    public COMADBCorpus() {
    }

    public void readCorpus(String corpusName, String connection, String usr, String pwd) throws ClassNotFoundException, SQLException {
        DBConnection cc = DBConnection.getInstance();
        conn = cc.getConnection(connection, usr, pwd);
        reallyReadCorpus(corpusName);
    }

    public void readCorpus(String corpusName, String driver, String url, String table, String usr, String pwd) throws ClassNotFoundException, SQLException {
        DBConnection cc = DBConnection.getInstance();
        conn = cc.getConnection(driver, url, table, usr, pwd);
        reallyReadCorpus(corpusName);
    }

    private void reallyReadCorpus(String corpusName) throws ClassNotFoundException, SQLException {

        this.corpusName = corpusName;

        //for the SQL queries
        String segmentData = "((" + pre + "segment INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.transcription_guid = " + pre + "segment.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid";
        String corpusPathSQL = "SELECT corpus_location FROM " + pre + "corpus WHERE " + pre + "corpus.name='" + corpusName + "';";
        String segmentationNamesSQL = "SELECT DISTINCT " + pre + "segment.segmentation FROM " + segmentData + " WHERE " + pre + "corpus.name='" + corpusName + "';";
        String annotationNamesSQL = "SELECT DISTINCT " + pre + "annotation_segment.name FROM ((" + pre + "annotation_segment INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.transcription_guid = " + pre + "annotation_segment.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';";
        String descriptionNamesSQL = "SELECT DISTINCT tier_category FROM " + segmentData + " WHERE " + pre + "corpus.name='" + corpusName + "' AND `tier_type` = 'd';";
        String segmentNamesSQL = "SELECT DISTINCT " + pre + "segment.name FROM " + segmentData + " WHERE " + pre + "corpus.name='" + corpusName + "';";
        String transcriptionsSQL = "SELECT COUNT(*) AS transcriptions FROM (" + pre + "segmented_transcription INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';";
        String segmentChainsNumberSQL = "SELECT COUNT(*) AS segmentchains FROM " + segmentData + " AND segmentation='SpeakerContribution_Event' AND " + pre + "segment.name='sc';";


        if (conn != null) {

            Statement query = conn.createStatement();

            //corpusPath
            ResultSet corpusPathResult = query.executeQuery(corpusPathSQL);

            while (corpusPathResult.next()) {
                corpusPath = corpusPathResult.getString("corpus_location");
             }

            fireCorpusInit(0, "Started reading " + corpusPath + ".");

            //transcriptions
            ResultSet transcriptionsResult = query.executeQuery(transcriptionsSQL);

            while (transcriptionsResult.next()) {
                transcriptionsNumber = transcriptionsResult.getInt("transcriptions");
             }

            //segmentChains
            ResultSet segmentChainsNumberResult = query.executeQuery(segmentChainsNumberSQL);

            while (segmentChainsNumberResult.next()) {
                segmentChainsNumber = segmentChainsNumberResult.getInt("segmentchains");
            }

            //segmentationNames
            ResultSet segmentationNamesResult = query.executeQuery(segmentationNamesSQL);

            while (segmentationNamesResult.next()) {
                segmentationNames.add(segmentationNamesResult.getString("segmentation"));
             }

            fireCorpusInit(0.25, "Collecting metadata for " + this.corpusName + ".");

            //annotationNames
            ResultSet annotationNamesResult = query.executeQuery(annotationNamesSQL);

            while (annotationNamesResult.next()) {
                annotationNames.add(annotationNamesResult.getString("name"));
            }

            //descriptionNames
            ResultSet descriptionNamesResult = query.executeQuery(descriptionNamesSQL);

            while (descriptionNamesResult.next()) {
                descriptionNames.add(descriptionNamesResult.getString("tier_category"));
            }

            //segmentNames
            ResultSet segmentNamesResult = query.executeQuery(segmentNamesSQL);

            while (segmentNamesResult.next()) {
                 segmentNames.add(segmentNamesResult.getString("name"));
            }

             fireCorpusInit(0.5, "Collecting metadata for " + this.corpusName + ".");

            //communicationAttributes - only the following from communcation, language (=none!) and location!
            communicationAttributes.put("Name*", "SELECT " + pre + "communication.name AS result FROM " + pre + "communication INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid WHERE transcription_guid = '%s';");
            communicationAttributes.put("City*", "SELECT city AS result FROM (" + pre + "communication INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "location_of_communication ON " + pre + "communication.communication_guid = " + pre + "location_of_communication.communication_guid WHERE transcription_guid = '%s';");
            communicationAttributes.put("Country*", "SELECT country AS result FROM (" + pre + "communication INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "location_of_communication ON " + pre + "communication.communication_guid = " + pre + "location_of_communication.communication_guid WHERE transcription_guid = '%s';");
            communicationAttributes.put("Date*", "SELECT period_start AS result FROM (" + pre + "communication INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "location_of_communication ON " + pre + "communication.communication_guid = " + pre + "location_of_communication.communication_guid WHERE transcription_guid = '%s';");
            communicationAttributes.put("Duration*", "SELECT period_duration AS result FROM (" + pre + "communication INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "location_of_communication ON " + pre + "communication.communication_guid = " + pre + "location_of_communication.communication_guid WHERE transcription_guid = '%s';");

            //the rest from the communication descriptions
            ResultSet communicationAttributesResult = query.executeQuery("SELECT DISTINCT attribute FROM ((" + pre + "communication INNER JOIN " + pre + "communication_desc_item ON " + pre + "communication.communication_guid = " + pre + "communication_desc_item.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid) WHERE " + pre + "corpus.name='" + corpusName + "'");

            while (communicationAttributesResult.next()) {
                String attribute = communicationAttributesResult.getString("attribute");
                if (attribute != null) {
                    communicationAttributes.put(attribute, "SELECT value AS result FROM ((" + pre + "communication INNER JOIN " + pre + "communication_desc_item ON " + pre + "communication.communication_guid = " + pre + "communication_desc_item.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid) INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.communication_guid = " + pre + "communication.communication_guid WHERE transcription_guid = '%s' AND attribute = '%s'");
                }
            }

            //speakerAttributes - only the following from speaker, language and location!
            speakerAttributes.put("Sigle*", "SELECT sigle AS result FROM " + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid WHERE transcription_guid = '%s' AND speaker = '%s';");
            speakerAttributes.put("Pseudo*", "SELECT pseudo AS result FROM " + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid WHERE transcription_guid = '%s' AND speaker = '%s';");
            speakerAttributes.put("Sex*", "SELECT sex AS result FROM " + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid WHERE transcription_guid = '%s' AND speaker = '%s';");

            //and these could return more than one result
            ResultSet speakerLocationTypesResult = query.executeQuery("SELECT DISTINCT " + pre + "location_of_speaker.type AS type FROM (((" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "location_of_speaker ON " + pre + "location_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid) INNER JOIN " + pre + "communication ON " + pre + "participation.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';");

            while (speakerLocationTypesResult.next()) {

                String type = speakerLocationTypesResult.getString("type");

                speakerAttributes.put(type + ":City*", "SELECT city AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "location_of_speaker ON " + pre + "location_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "location_of_speaker.type = '" + type + "' AND transcription_guid = '%s' AND speaker = '%s';");
                speakerAttributes.put(type + ":Country*", "SELECT country AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "location_of_speaker ON " + pre + "location_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "location_of_speaker.type = '" + type + "' AND transcription_guid = '%s' AND speaker = '%s';");
                speakerAttributes.put(type + ":Date*", "SELECT period_start AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "location_of_speaker ON " + pre + "location_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "location_of_speaker.type = '" + type + "' AND transcription_guid = '%s' AND speaker = '%s';");
                speakerAttributes.put(type + ":Duration*", "SELECT period_duration AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "location_of_speaker ON " + pre + "location_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "location_of_speaker.type = '" + type + "' AND transcription_guid = '%s' AND speaker = '%s';");
            }

            fireCorpusInit(0.75, "Collecting metadata for " + this.corpusName + ".");

            ResultSet speakerLanguageTypesResult = query.executeQuery("SELECT DISTINCT " + pre + "language_of_speaker.type AS type, " + pre + "language_of_speaker.code AS code FROM (((" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "language_of_speaker ON " + pre + "language_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid) INNER JOIN " + pre + "communication ON " + pre + "participation.communication_guid = " + pre + "communication.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';");

            while (speakerLanguageTypesResult.next()) {

                String type = speakerLanguageTypesResult.getString("type");
                String code = speakerLanguageTypesResult.getString("code");

                speakerAttributes.put("Language Type (" + code + ")", "SELECT " + pre + "language_of_speaker.type AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "language_of_speaker ON " + pre + "language_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "language_of_speaker.code = '" + code + "' AND transcription_guid = '%s' AND speaker = '%s';");
                speakerAttributes.put(type + ":Language Code", "SELECT code AS result, " + pre + "language_of_speaker.type AS type FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "language_of_speaker ON " + pre + "language_of_speaker.speaker_guid = " + pre + "speaker.speaker_guid WHERE " + pre + "language_of_speaker.type = '" + type + "' AND transcription_guid = '%s' AND speaker = '%s';");
            }

            //the rest from the speaker descriptions
            ResultSet speakerAttributesResult = query.executeQuery("SELECT DISTINCT attribute FROM (((" + pre + "speaker INNER JOIN " + pre + "speaker_desc_item ON " + pre + "speaker.speaker_guid = " + pre + "speaker_desc_item.speaker_guid) INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "participation.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';");

            while (speakerAttributesResult.next()) {
                String attribute = speakerAttributesResult.getString("attribute");
                if (attribute != null) {
                    speakerAttributes.put(attribute, "SELECT value AS result FROM (" + pre + "speaker INNER JOIN " + pre + "participation ON " + pre + "speaker.speaker_guid = " + pre + "participation.speaker_guid) INNER JOIN " + pre + "speaker_desc_item ON " + pre + "speaker.speaker_guid = " + pre + "speaker_desc_item.speaker_guid WHERE transcription_guid = '%s' AND speaker = '%s' AND attribute = '%s';");
                }
            }

            //transcriptionAttributes - only the following from transcription!
            transcriptionAttributes.put("Name*", "SELECT name AS result FROM " + pre + "segmented_transcription WHERE transcription_guid = '%s';");
            transcriptionAttributes.put("Filename*", "SELECT filename AS result FROM " + pre + "segmented_transcription WHERE transcription_guid = '%s';");

            //the rest from the speaker attributes
            ResultSet transcriptionAttributesResult = query.executeQuery("SELECT DISTINCT attribute FROM ((" + pre + "segmented_transcription INNER JOIN " + pre + "seg_trans_desc_item ON " + pre + "segmented_transcription.transcription_guid = " + pre + "seg_trans_desc_item.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "communication.corpus_guid = " + pre + "corpus.corpus_guid WHERE " + pre + "corpus.name='" + corpusName + "';");

            while (transcriptionAttributesResult.next()) {

                String attribute = transcriptionAttributesResult.getString("attribute");

                if (attribute != null) {
                    transcriptionAttributes.put(attribute, "SELECT value AS result FROM " + pre + "segmented_transcription INNER JOIN " + pre + "seg_trans_desc_item ON " + pre + "segmented_transcription.transcription_guid = " + pre + "seg_trans_desc_item.transcription_guid WHERE " + pre + "segmented_transcription.transcription_guid = '%s' AND attribute = '%s';");
                }
            }

            fireCorpusInit(1, "Collecting metadata for " + this.corpusName + ".");

            corpusComponent = new DBCorpusComponent(conn, corpusName);

            next = true;
        }
    }

    public boolean isWordSegmented() {
        for (String s : segmentNames) {
            if (s.endsWith(":w")) {
                return true;
            }
        }
        return false;
    }

    public String getWordSegmentName() {
        for (String s : segmentNames) {
            if (s.endsWith(":w")) {
                return s;
            }
        }
        return null;
    }

    public String getCorpusName() {
        return corpusName;
    }

    public String getCorpusPath() {
        return corpusPath;
    }

    public HashSet<String> getAnnotationNames() {
        return annotationNames;
    }

    public HashSet<String> getDescriptionNames() {
        return descriptionNames;
    }

    public HashSet<String> getSegmentationNames() {
        return segmentationNames;
    }

    public Set<String> getCommunicationAttributes() {
        return communicationAttributes.keySet();
    }

    //transcriptionLocator is the GUID, this should be "unique enough" across corpora
    public String getCommunicationData(String transcriptionLocator, String attributeName) {

        String value = undefined;
        String statement = (communicationAttributes.get(attributeName).contains("value")) ? String.format(communicationAttributes.get(attributeName), transcriptionLocator, attributeName) : String.format(communicationAttributes.get(attributeName), transcriptionLocator);

        try {
            Statement query = conn.createStatement();


            ResultSet communicationData = query.executeQuery(statement);

            while (communicationData.next()) {

                if (communicationData.getString("result") != null) {
                    value = communicationData.getString("result");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(COMADBCorpus.class.getName()).log(Level.SEVERE, null, ex);
        }


        return value;
    }

    public Set<String> getSpeakerAttributes() {
        return speakerAttributes.keySet();
    }

    //transcriptionLocator is the GUID, this should be "unique enough" across corpora
    public String getSpeakerData(String transcriptionLocator, String speakerID, String attributeName) {

        String value = undefined;
        String statement = (speakerAttributes.get(attributeName).contains("value")) ? String.format(speakerAttributes.get(attributeName), transcriptionLocator, speakerID, attributeName) : String.format(speakerAttributes.get(attributeName), transcriptionLocator, speakerID);

        if (statement != null) {
            try {
                Statement query = conn.createStatement();


                ResultSet speakerData = query.executeQuery(statement);

                while (speakerData.next()) {

                    if (speakerData.getString("result") != null) {
                        value = speakerData.getString("result");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(COMADBCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        return value;
    }

    public Set<String> getTranscriptionAttributes() {
        return transcriptionAttributes.keySet();
    }

    public String getTranscriptionData(String transcriptionLocator, String attributeName) {
        String value = undefined;
        String statement = (transcriptionAttributes.get(attributeName).contains("value")) ? String.format(transcriptionAttributes.get(attributeName), transcriptionLocator, attributeName) : String.format(transcriptionAttributes.get(attributeName), transcriptionLocator);

        if (statement != null) {
            try {
                Statement query = conn.createStatement();

                ResultSet transcriptionData = query.executeQuery(statement);

                while (transcriptionData.next()) {

                    if (transcriptionData.getString("result") != null) {
                        value = transcriptionData.getString("result");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(COMADBCorpus.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        return value;
    }

    public boolean hasNext() {
        return next;
    }

    //returns the whole corpus as "component"!
    public CorpusComponentInterface next() {
        next = false;
        return corpusComponent;
    }

    public void reset() {
        next = true;
    }

    public CorpusComponentInterface getCorpusComponent(SearchableSegmentLocatorInterface id) {
        return corpusComponent;
    }

    public int getNumberOfCorpusComponents() {
        return 1;
    }

    public int getNumberOfSearchableSegments() {
        return 0;
    }

    public void addSearchListener(SearchListenerInterface sli) {
        listenerList.add(sli);
    }

    public void exit() throws SQLException {
        conn.close();
    }

    public int getNumberOfTranscriptions() {
        return transcriptionsNumber;
    }

    public int getNumberOfSegmentChains() {
        return segmentChainsNumber;
    }

    protected void fireCorpusInit(double progress, String message) {
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
            SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS, progress, message);
            listenerList.get(i).processSearchEvent(se);
        }
    }

    public Connection getConnection(){
        return conn;
    }

    public void setPrefix(String pre){
        this.pre = pre;
    }

    public String getPrefix(){
        return pre;
    }

    public String getUniqueSpeakerIdentifier() {
        // need to change this maybe
        return "//speaker/abbreviation";
    }
      
}
