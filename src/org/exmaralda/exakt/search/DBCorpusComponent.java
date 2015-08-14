/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.exakt.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * this class doesn't delegate the search to searchable segments
 * and the corpus component is the whole corpus
 *
 * @author hanna
 */
public class DBCorpusComponent implements CorpusComponentInterface {

    private Connection conn;
    private String corpusName;
    private String pre = "ex_";
    private HashMap<String, String> additionalDataKeys;

    public DBCorpusComponent(Connection conn, String corpusName) {
        this.conn = conn;
        this.corpusName = corpusName;
        additionalDataKeys = new HashMap<String, String>();
        additionalDataKeys.put("tier-id", "tier_id");
        additionalDataKeys.put("speaker", "speaker");
        additionalDataKeys.put("start", "id_attr");
    }

    public boolean hasNext() {
        return false;
    }

    public SearchableSegmentInterface next() {

        return null;
    }

    public void reset() {
    }

    public SearchableSegmentInterface getSearchableSegment(SearchableSegmentLocatorInterface id) {
        return null;
    }

    public int getNumberOfSearchableSegments() {
        return 1;
    }

    public String getIdentifier() {
        return null;
    }

    public SearchResultList search(SearchParametersInterface searchParameters) {

        SearchResultList returnValue = null;

        if (searchParameters instanceof RegularExpressionSearchParameters) {
            RegularExpressionSearchParameters sp = (RegularExpressionSearchParameters) searchParameters;
            returnValue = new SearchResultList();

            if (searchParameters.getSearchType() == SearchParametersInterface.DEFAULT_SEARCH) {
                if (searchParameters.getCategory().equals("v")) {
                    try {

                        //sql query
                        String searchSQL = "SELECT cdata, tier_id, id_attr, rel_path, speaker, seg_id, " + pre + "segment.segment_id, " + pre + "segment.transcription_guid FROM (((" + pre + "segment INNER JOIN " + pre + "timeline_item ON " + pre + "segment.tli_s = " + pre + "timeline_item.tli_id) INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.transcription_guid = " + pre + "segment.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid WHERE " + pre + "corpus.name = ? AND " + pre + "segment.segmentation = 'SpeakerContribution_Event' AND " + pre + "segment.name = 'sc' AND " + pre + "segment.tier_category = ? AND PREG_RLIKE(?, cdata);";

                        PreparedStatement prepStmt = conn.prepareStatement(searchSQL);
                        prepStmt.setString(1, corpusName);
                        String category = sp.getCategory();
                        prepStmt.setString(2, category);
                        String searchExpression = "/" + sp.getSearchExpressionAsString().replace("\\", "\\\\") + "/u";
                        prepStmt.setString(3, searchExpression);
                        ResultSet result = prepStmt.executeQuery();

                        //for all rows with one or more matches, for each match a search result is added to the list
                        while (result.next()) {
                            String searchString = result.getString("cdata");
                            Matcher matcher = sp.getPattern().matcher(searchString);
                            matcher.reset();

                            while (matcher.find()) {
                                DBSearchableSegmentLocator segmentLocator = new DBSearchableSegmentLocator(result.getString("transcription_guid"), result.getString("segment_id"), result.getString("rel_path"), "//ts[@id='" + result.getString("seg_id") + "']");

                                Object[][] adls = sp.getAdditionalDataLocators();
                                String[][] additionalData = new String[adls.length][2];
                                int index = 0;
                                for (Object[] a : adls) {
                                    String attribute = (String) a[0];
                                    String value = (additionalDataKeys.containsKey(attribute)) ? result.getString(additionalDataKeys.get(attribute)) : "#undefined";
                                    additionalData[index][0] = attribute;
                                    additionalData[index][1] = value;
                                    index++;
                                }

                                SimpleSearchResult ssr = new SimpleSearchResult(searchString, matcher.start(), matcher.end(), sp.getContextLimit(), segmentLocator, additionalData);
                                returnValue.addSearchResult(ssr);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(DBCorpusComponent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    try {

                        //sql query
                        String searchSQL = "SELECT cdata, tier_id, id_attr, rel_path, speaker, seg_id, " + pre + "segment.segment_id, " + pre + "segment.transcription_guid FROM (((" + pre + "segment INNER JOIN " + pre + "timeline_item ON " + pre + "segment.tli_s = " + pre + "timeline_item.tli_id) INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.transcription_guid = " + pre + "segment.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid WHERE " + pre + "corpus.name = ? AND " + pre + "segment.tier_category = ? AND PREG_RLIKE(?, cdata);";

                        PreparedStatement prepStmt = conn.prepareStatement(searchSQL);
                        prepStmt.setString(1, corpusName);
                        String category = sp.getCategory();
                        prepStmt.setString(2, category);
                        String searchExpression = "/" + sp.getSearchExpressionAsString().replace("\\", "\\\\") + "/u";
                        prepStmt.setString(3, searchExpression);
                        ResultSet result = prepStmt.executeQuery();

                        //for all rows with one or more matches, for each match a search result is added to the list
                        while (result.next()) {
                            String searchString = result.getString("cdata");
                            Matcher matcher = sp.getPattern().matcher(searchString);
                            matcher.reset();

                            while (matcher.find()) {
                                DBSearchableSegmentLocator segmentLocator = new DBSearchableSegmentLocator(result.getString("transcription_guid"), result.getString("segment_id"), result.getString("rel_path"), "//ts[@id='" + result.getString("seg_id") + "']");

                                Object[][] adls = sp.getAdditionalDataLocators();
                                String[][] additionalData = new String[adls.length][2];
                                int index = 0;
                                for (Object[] a : adls) {
                                    String attribute = (String) a[0];
                                    String value = (additionalDataKeys.containsKey(attribute)) ? result.getString(additionalDataKeys.get(attribute)) : "#undefined";
                                    additionalData[index][0] = attribute;
                                    additionalData[index][1] = value;
                                    index++;
                                }

                                SimpleSearchResult ssr = new SimpleSearchResult(searchString, matcher.start(), matcher.end(), sp.getContextLimit(), segmentLocator, additionalData);
                                returnValue.addSearchResult(ssr);
                            }
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(DBCorpusComponent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else if (searchParameters.getSearchType() == SearchParametersInterface.ANNOTATION_SEARCH) {
                try {

                    //sql query
                    String searchSQL = "SELECT parent.cdata AS sc_text, " + pre + "segment.char_e, " + pre + "annotation_segment.annotation_id, " + pre + "annotation_segment.seg_id AS anno_id, " + pre + "annotation_segment.cdata AS annotation_text, " + pre + "segment.cdata AS segment_text, " + pre + "segment.tier_id, id_attr, rel_path, " + pre + "segment.speaker, " + pre + "segment.char_s, " + pre + "segment.transcription_guid FROM ((((((" + pre + "segment INNER JOIN " + pre + "timeline_item ON " + pre + "segment.tli_s = " + pre + "timeline_item.tli_id) INNER JOIN " + pre + "segmented_transcription ON " + pre + "segmented_transcription.transcription_guid = " + pre + "segment.transcription_guid) INNER JOIN " + pre + "communication ON " + pre + "communication.communication_guid = " + pre + "segmented_transcription.communication_guid) INNER JOIN " + pre + "corpus ON " + pre + "corpus.corpus_guid = " + pre + "communication.corpus_guid) INNER JOIN " + pre + "segment_has_annotation ON " + pre + "segment_has_annotation.segment_id = " + pre + "segment.segment_id) INNER JOIN " + pre + "annotation_segment ON " + pre + "segment_has_annotation.annotation_id = " + pre + "annotation_segment.annotation_id) INNER JOIN " + pre + "segment AS parent ON " + pre + "segment.parent = parent.segment_id WHERE " + pre + "corpus.name = ? AND " + pre + "annotation_segment.name = ? AND PREG_RLIKE(?, " + pre + "annotation_segment.cdata) ORDER BY " + pre + "annotation_segment.annotation_id, " + pre + "timeline_item.nr;";

                    PreparedStatement prepStmt = conn.prepareStatement(searchSQL);
                    prepStmt.setString(1, corpusName);
                    String annotationCategory = sp.getCategory();
                    prepStmt.setString(2, annotationCategory);
                    String searchExpression = "/" + sp.getSearchExpressionAsString().replace("\\", "\\\\") + "/u";
                    prepStmt.setString(3, searchExpression);
                    ResultSet result = prepStmt.executeQuery();

                    while (result.next()) {
                        String annotationText = result.getString("annotation_text");
                        String annotationId = result.getString("annotation_id");
                        String searchString = result.getString("sc_text");
                        int matchStart = result.getInt("char_s") - 1;
                        int matchEnd = result.getInt("char_e");

                        Object[][] adls = sp.getAdditionalDataLocators();
                        String[][] additionalData = new String[adls.length + 1][2];
                        int index = 0;
                        for (Object[] a : adls) {
                            String attribute = (String) a[0];
                            String value = (additionalDataKeys.containsKey(attribute)) ? result.getString(additionalDataKeys.get(attribute)) : "#undefined";
                            additionalData[index][0] = attribute;
                            additionalData[index][1] = value;
                            index++;
                        }

                        additionalData[adls.length][0] = "";
                        additionalData[adls.length][1] = annotationText;

                        DBSearchableSegmentLocator segmentLocator = new DBSearchableSegmentLocator(result.getString("transcription_guid"), result.getString("anno_id"), result.getString("rel_path"), "//ta[@id='" + result.getString("anno_id") + "']");

                        while (result.next() && annotationId.equals(result.getString("annotation_id"))) {
                            annotationId = result.getString("annotation_id");
                            matchEnd = result.getInt("char_e");
                        }
                        result.previous();

                        AnnotationSearchResult asr = new AnnotationSearchResult();
                        asr.init(searchString, matchStart, matchEnd, sp.getContextLimit(), segmentLocator, additionalData);
                        returnValue.addSearchResult(asr);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(DBCorpusComponent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return returnValue;
    }
}
