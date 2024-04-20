/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.exmaralda.partitureditor.jexmaralda.segment;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.exmaralda.common.jdomutilities.IOUtilities;
import org.exmaralda.exakt.utilities.FileIO;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.convert.StylesheetFactory;
import org.exmaralda.partitureditor.jexmaralda.sax.SegmentedTranscriptionSaxReader;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 *
 * @author bernd
 */
public class InelEventBasedSegmentation extends AbstractSegmentation {

    String XSL = "/org/exmaralda/partitureditor/jexmaralda/xsl/InelEventBasedSegmentation.xsl";
    
    @Override
    public Vector getSegmentationErrors(BasicTranscription bt) throws SAXException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public SegmentedTranscription BasicToSegmented(BasicTranscription bt) throws SAXException, FSMException {
        try {
            SegmentedTranscription plainSegmented = bt.toSegmentedTranscription();
            String plainSegmentedXML = plainSegmented.toXML();
            StylesheetFactory stylesheetFactory = new StylesheetFactory(true);
            String inelSegmentedXML = stylesheetFactory.applyInternalStylesheetToString(XSL, plainSegmentedXML);
            Document inelSegmentedDoc = IOUtilities.readDocumentFromString(inelSegmentedXML);
            File tempFile = File.createTempFile("INEL_SEGMENTED", ".exs");
            tempFile.deleteOnExit();
            FileIO.writeDocumentToLocalFile(tempFile, inelSegmentedDoc);
            SegmentedTranscription result = new SegmentedTranscriptionSaxReader().readFromFile(tempFile.getAbsolutePath());
            tempFile.delete();
            return result;
        } catch (ParserConfigurationException | IOException | TransformerException | JDOMException ex) {
            throw new SAXException(ex);
        }
    }
    
}
