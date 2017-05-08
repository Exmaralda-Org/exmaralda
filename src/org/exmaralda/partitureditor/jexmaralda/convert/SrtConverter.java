/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.partitureditor.jexmaralda.convert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.folker.utilities.TimeStringFormatter;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.ListTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.SpeakerContribution;
import org.exmaralda.partitureditor.jexmaralda.TimelineItem;
import org.exmaralda.partitureditor.jexmaralda.segment.SegmentedToListInfo;
import org.xml.sax.SAXException;

/**
 *
 * @author fsnv625
 */
public class SrtConverter {

    public SrtConverter() {

    }

    public String exportBasicTranscription(BasicTranscription t) throws JexmaraldaException {
        System.out.println(getSRT(t, false, false));
        return getSRT(t, false, false);
    }

    public void writeText(BasicTranscription bt, File file) throws IOException, SAXException, ParserConfigurationException, TransformerException, JexmaraldaException {
        String text = exportBasicTranscription(bt);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes());
        fos.close();
    }

    public String getSRT(BasicTranscription t, boolean frames, boolean plainText) throws JexmaraldaException {
        StringBuilder sb = new StringBuilder();
        //get a sorted listtranscription of the basictranscription
        SegmentedTranscription st = t.toSegmentedTranscription();
        SegmentedToListInfo info = new SegmentedToListInfo(st, SegmentedToListInfo.TURN_SEGMENTATION);
        ListTranscription lt = st.toListTranscription(info);
        lt.getBody().sort();   
        int index = 1;
        String lastStart = "";
        String lastEnd = "";
        //go trough every speakercontribution       
        for (int pos = 0; pos < lt.getBody().getNumberOfSpeakerContributions(); pos++) {
            SpeakerContribution sc = lt.getBody().getSpeakerContributionAt(pos);
            TimelineItem tliStart = lt.getBody().getCommonTimeline().getTimelineItemWithID(sc.getMain().getStart());
            TimelineItem tliEnd = lt.getBody().getCommonTimeline().getTimelineItemWithID(sc.getMain().getEnd());
            double startTime = tliStart.getTime();
            double endTime = tliEnd.getTime();
            String start = TimeStringFormatter.formatSeconds(startTime, true, 3).replace('.', ',');
            String end = TimeStringFormatter.formatSeconds(endTime, true, 3).replace('.', ',');
            if (frames) {
                String startMili = start.substring(start.indexOf(",") + 1);
                String endMili = end.substring(end.indexOf(",") + 1);
                int startFramesInt = (int) Math.floor(Double.parseDouble("0." + startMili) * 25.0);
                String startFrames = Integer.toString(startFramesInt);
                if (startFramesInt < 10) {
                    startFrames = "0" + startFrames;
                }
                int endFramesInt = (int) Math.floor(Double.parseDouble("0." + endMili) * 25.0);
                String endFrames = Integer.toString(endFramesInt);
                if (endFramesInt < 10) {
                    endFrames = "0" + endFrames;
                }
                start = start.substring(0, start.indexOf(",")) + ":" + startFrames;
                end = end.substring(0, end.indexOf(",")) + ":" + endFrames;
                if (start.equals(lastStart) && end.equals(lastEnd)) {
                    start = "";
                    end = "";
                } else {
                    lastStart = start;
                    lastEnd = end;
                }
            }
            if (!plainText) {
                sb.append(Integer.toString(index)).append(System.getProperty("line.separator"));
                sb.append(start).append(" --> ").append(end).append(System.getProperty("line.separator"));
                sb.append(sc.getMain().getDescription()).append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));
            } else {
                sb.append(start).append(" ").append(end).append(" ").append(sc.getMain().getDescription()).append(System.getProperty("line.separator"));
            }
            index++;
        }
        return sb.toString();
    }
}
