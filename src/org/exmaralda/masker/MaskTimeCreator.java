/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.exmaralda.masker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.exmaralda.folker.data.TranscriptionHead;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.Event;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.Tier;
import org.exmaralda.partitureditor.jexmaralda.Timeline;
import org.jdom.Element;

/**
 *
 * @author Schmidt
 */
public class MaskTimeCreator {

    public static double[][] createTimesFromFOLKERTranscriptionHead(TranscriptionHead transcriptionHead) {
        // <mask-segment start="7.083320386057005" end="7.91109665078053">Schonmaskiert</mask-segment>
        List l = transcriptionHead.getHeadElement().getChild("mask").getChildren("mask-segment");
        double[][] result = new double[l.size()][2];
        int i=0;
        for (Object o : l){
            Element e = (Element)o;
            String start = e.getAttributeValue("start");
            String end = e.getAttributeValue("end");
            result[i][0] = Double.parseDouble(start);
            result[i][1] = Double.parseDouble(end);
            i++;
        }
        result = cleanUp(result);
        return result;
    }
    
    public static double[][] createTimesFromEXMARaLDATier(BasicTranscription bt, String tierID) throws JexmaraldaException{
        Tier tier = bt.getBody().getTierWithID(tierID);
        Timeline tl = bt.getBody().getCommonTimeline().makeCopy();
        tl.makeConsistent();
        tl.completeTimes(false, bt);
        double[][] result = new double[tier.getNumberOfEvents()][2];        
        for (int pos=0; pos<tier.getNumberOfEvents(); pos++){
            Event e = tier.getEventAt(pos);
            String start = e.getStart();
            String end = e.getEnd();
            double startTime = tl.getTimelineItemWithID(start).getTime();
            double endTime = tl.getTimelineItemWithID(end).getTime();
            result[pos][0] = startTime;
            result[pos][1] = endTime;
        }
        return result;
    }

    private static double[][] cleanUp(double[][] input) {
        Arrays.sort(input, new Comparator<double[]>(){
            @Override
            public int compare(double[] d1, double[] d2) {
                if (d1[0]!=d2[0]){
                    return Double.compare(d1[0], d2[0]);
                } else {
                    return Double.compare(d2[1], d1[1]);                    
                }
            }            
        });
        ArrayList<double[]> list = new ArrayList<double[]>();
        list.addAll(Arrays.asList(input));
        
        for (int i=1; i<list.size(); i++){
            double[] previousEntry = list.get(i-1);
            double[] thisEntry = list.get(i);
            
            if (thisEntry[0]<=previousEntry[1]){
                // this one starts before the previous one ends
                if (thisEntry[1]<=previousEntry[1]){
                    // and it also ends before the previous one ends
                    // so it is fully included
                    // it can go
                    list.remove(i);
                    i--;
                } else {
                    // the two partly overlap
                    // set the previous end to this one's end
                    previousEntry[1] = thisEntry[1];
                    list.remove(i);
                    i--;                    
                }
            }
        }
        
        return list.toArray(new double[list.size()][2]);
    }
    
}
