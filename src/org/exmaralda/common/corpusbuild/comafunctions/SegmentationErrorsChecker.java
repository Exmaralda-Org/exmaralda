/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild.comafunctions;

import java.net.URISyntaxException;
import java.util.Vector;
import org.exmaralda.common.corpusbuild.AbstractCorpusChecker;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.xml.sax.SAXException;

/**
 *
 * @author thomas
 */
public class SegmentationErrorsChecker extends AbstractCorpusChecker {

    AbstractSegmentation segmentation;

    public SegmentationErrorsChecker(String segmentationName){
        this(segmentationName, "");
    }
    
    public SegmentationErrorsChecker(String segmentationName, String customFSMPath){
        super();
        if (customFSMPath==null || customFSMPath.length()==0){
            if (segmentationName.equals("HIAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation();
            } else if (segmentationName.equals("GAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation();
            } else if (segmentationName.equals("cGAT_MINIMAL")) {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation();
            } else if (segmentationName.equals("CHAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation();
            } else if (segmentationName.equals("CHAT_MINIMAL")) {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation();
            } else if (segmentationName.equals("DIDA")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation();
            } else if (segmentationName.equals("IPA")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.IPASegmentation();
            } else {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation();
            }
        } else {
            if (segmentationName.equals("HIAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation(customFSMPath);
            } else if (segmentationName.equals("GAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation(customFSMPath);
            } else if (segmentationName.equals("cGAT_MINIMAL")) {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation(customFSMPath);
            } else if (segmentationName.equals("CHAT")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation(customFSMPath);
            } else if (segmentationName.equals("CHAT_MINIMAL")) {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation(customFSMPath);
            } else if (segmentationName.equals("DIDA")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation(customFSMPath);
            } else if (segmentationName.equals("IPA")){
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.IPASegmentation(customFSMPath);
            } else {
                segmentation = new org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation(customFSMPath);
            }            
        }

    }

    @Override
    public void processTranscription(BasicTranscription bt, String currentFilename) throws URISyntaxException, SAXException {
        Vector v = segmentation.getSegmentationErrors(bt);
        for (Object o : v){
            FSMException fsme = (FSMException)o;
            String text = fsme.getMessage();
            addError(currentFilename, fsme.getTierID(), fsme.getTLI(), text);
        }
    }

}
