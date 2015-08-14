/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.exmaralda.common.corpusbuild;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Vector;

import org.exmaralda.exakt.search.SearchEvent;
import org.exmaralda.exakt.search.SearchListenerInterface;
import org.exmaralda.partitureditor.fsm.FSMException;
import org.exmaralda.partitureditor.jexmaralda.BasicTranscription;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.exmaralda.partitureditor.jexmaralda.SegmentedTranscription;
import org.exmaralda.partitureditor.jexmaralda.segment.AbstractSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATMinimalSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.CHATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.DIDASegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.GenericSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.HIATSegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.IPASegmentation;
import org.exmaralda.partitureditor.jexmaralda.segment.cGATMinimalSegmentation;
import org.xml.sax.SAXException;

/**
 * 
 * @author thomas
 */
public class TranscriptionSegmentor {

	public final static int ERRORS_IGNORE = 0;
	public final static int ERRORS_CANCEL = 1;
	public final static int ERRORS_FAILSAFE = 2;

	AbstractSegmentation segmentation;

	// parameters
	File[] basicTranscriptions = new File[0];
	int segmentationAlgorithm = AbstractSegmentation.HIAT_SEGMENTATION;
        String customFSMPath = "";
	String suffix = "_s";
	File targetDirectory = null;
	int errorHandling = ERRORS_FAILSAFE;
	boolean writeErrorList;
	File errorListPath;
	CheckSegmentation segmentationChecker = new CheckSegmentation();

	// result
	HashMap<File, File> segmentedTranscriptions = new HashMap<File, File>();
	boolean isSegmenting = false;

	java.awt.Component progressMonitorParent = null;
	javax.swing.ProgressMonitor progressMonitor;

	private Vector<SearchListenerInterface> listenerList = new Vector<SearchListenerInterface>();

	public TranscriptionSegmentor(File[] bt) {
		this(bt, null);
		System.out.println("TranscriptionSegmentor: " + bt.length
				+ " basic transcriptions handed over for segmentation.");
	}

	public TranscriptionSegmentor(File[] bt, java.awt.Component pmp) {
		basicTranscriptions = bt;
		progressMonitorParent = pmp;
	}

	public HashMap<File, File> getSegmentedTranscriptions() {
		return segmentedTranscriptions;
	}

	public boolean doSegmentation(int sa, String suff, File td, int eh,
			boolean we, File ep) throws SAXException, JexmaraldaException,
			IOException {
            return doSegmentation(sa,suff,td,eh,we,ep, "");
        }
        
	public boolean doSegmentation(int sa, String suff, File td, int eh,
			boolean we, File ep, String cfsm) throws SAXException, JexmaraldaException,
			IOException {
		segmentationAlgorithm = sa;
		suffix = suff;
		targetDirectory = td;
		errorHandling = eh;
		writeErrorList = we;
		errorListPath = ep;
                customFSMPath = cfsm;
		return doSegmentation();
	}

	public boolean doSegmentation() throws SAXException, JexmaraldaException,
			IOException {
		switch (segmentationAlgorithm) {
		case AbstractSegmentation.NO_SEGMENTATION:
			segmentation = null;
			break;
		case AbstractSegmentation.HIAT_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new HIATSegmentation();
                        } else {
                            segmentation = new HIATSegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.GAT_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new GATSegmentation();
                        } else {
                            segmentation = new GATSegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.DIDA_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new DIDASegmentation();
                        } else {
                            segmentation = new DIDASegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.CHAT_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new CHATSegmentation();
                        } else {
                            segmentation = new CHATSegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.IPA_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new IPASegmentation();
                        } else {
                            segmentation = new IPASegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.GENERIC_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new GenericSegmentation();
                        } else {
                            segmentation = new GenericSegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.GAT_MINIMAL_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new cGATMinimalSegmentation();
                        } else {
                            segmentation = new cGATMinimalSegmentation(customFSMPath);                            
                        }
			break;
		case AbstractSegmentation.CHAT_MINIMAL_SEGMENTATION:
			if (customFSMPath.length()==0){
                            segmentation = new CHATMinimalSegmentation();
                        } else {
                            segmentation = new CHATMinimalSegmentation(customFSMPath);                            
                        }
			break;
		}
		// create target directory if it ain't bloody there
		if ((targetDirectory != null) && (!targetDirectory.exists())) {
			targetDirectory.mkdir();
		}

		int count = 0;
		// now do the real job
		for (File f : basicTranscriptions) {
			count++;
			if (progressMonitor != null) {
				progressMonitor.setProgress(count);
			}
			double prog = (double) count
					/ (double) (basicTranscriptions.length);
			System.out.println("Segmenting " + f.getName());
			fireCorpusInit(prog, "Segmenting " + f.getName());

			BasicTranscription bt = new BasicTranscription(f.getAbsolutePath());
			SegmentedTranscription st = null;
			if (segmentationAlgorithm == AbstractSegmentation.NO_SEGMENTATION) {
				st = bt.toSegmentedTranscription();
				st.setEXBSource(f.getAbsolutePath());
			} else {
				try {
					st = segmentation.BasicToSegmented(bt);
					st.setEXBSource(f.getAbsolutePath());
				} catch (FSMException ex) {
					ex.printStackTrace();
					switch (errorHandling) {
					case ERRORS_IGNORE:
						System.out.println("Ignoring error");
						continue; // just continue
					case ERRORS_CANCEL:
						System.out.println("Canceling because of error");
						// need to delete the ones that were already written
						for (File ff : segmentedTranscriptions.keySet()) {
							ff.delete();
						}
						return false;
					case ERRORS_FAILSAFE:
						System.out
								.println("Switched to default because of error");
						st = bt.toSegmentedTranscription();
						st.setEXBSource(f.getAbsolutePath());
					}
					if (writeErrorList) {
						try {
							segmentationChecker.processTranscription(bt,
									f.getAbsolutePath());
						} catch (URISyntaxException ex1) {
							ex1.printStackTrace();
						}
					}
				}
			}

			// now the segmented transcription is there... need to write it
			// change 01-07-2009: take care not to schnibbel ze schdring falsch
			int index = f.getName().lastIndexOf(".");
			if (index < 0) {
				index = f.getName().length() - 1;
			}
			String outFilename = f.getName().substring(0, index);
			outFilename += suffix + ".exs";
			String outPath = "";
			if (targetDirectory == null) {
				outPath = f.getParent();
			} else {
				outPath = targetDirectory.getAbsolutePath();
			}
			String segPath = outPath + System.getProperty("file.separator")
					+ outFilename;
			org.exmaralda.partitureditor.jexmaralda.segment.SegmentCountForMetaInformation
					.count(st);
			System.out.println("Writing segmented transcription " + segPath);
			st.writeXMLToFile(segPath, "none");
			segmentedTranscriptions.put(new File(segPath), f);
		}
		if (writeErrorList) {
			try {
				segmentationChecker.output(errorListPath.getAbsolutePath());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}

	public void addSearchListener(SearchListenerInterface sli) {
		listenerList.addElement(sli);
	}

	protected void fireCorpusInit(double progress, String message) {
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listenerList.size() - 1; i >= 0; i -= 1) {
			SearchEvent se = new SearchEvent(SearchEvent.CORPUS_INIT_PROGRESS,
					progress, message);
			listenerList.elementAt(i).processSearchEvent(se);
		}
	}

}
