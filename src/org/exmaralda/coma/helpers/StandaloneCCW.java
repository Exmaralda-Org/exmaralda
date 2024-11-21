package org.exmaralda.coma.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.prefs.Preferences;

import org.exmaralda.coma.models.TranscriptionMetadata;
import org.exmaralda.coma.root.ComaXMLOutputter;
import org.exmaralda.common.corpusbuild.TranscriptionSegmentor;
import org.exmaralda.partitureditor.jexmaralda.JexmaraldaException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.xml.sax.SAXException;

public class StandaloneCCW {

    private File comaFile;
    private String corpusName;
    private HashMap<File, TranscriptionMetadata> transcriptions = new HashMap<>();
    private HashMap<String, HashMap<String, String>> speakerMetadata = new HashMap<>();
    private HashMap<String, String> metadataFields = new HashMap<>();
    private HashMap<File, File> segmentedTranscriptions;
    private HashMap<File, File> fromSegmentation = new HashMap<>();
    private HashMap<String, String> recMeta = new HashMap<>();
    private HashMap<String, String> transMeta = new HashMap<>();
    private HashMap<String, String> commMeta = new HashMap<>();
    private String speakerDistinction;
    private HashSet<String> speakerMetaFields;
    private HashMap<String, String> actualDescription;
    private HashMap<String, Integer> commNames = new HashMap<>();
    private HashMap<String, Vector<File>> comms = new HashMap<>();
    private HashMap<String, HashSet<String>> commSpeakers = new HashMap<>();
    private HashMap<String, String> speakerNames = new HashMap<>();
    private HashMap<String, Vector<String>> speakers = new HashMap<>();
    private HashSet<File> files = new HashSet<>();
    private static final Preferences prefs = Preferences.userRoot().node("org.exmaralda.coma");

    public StandaloneCCW(String corpusName, File comaFile) {
        this.corpusName = corpusName;
        this.comaFile = comaFile;
    }

    public void addTranscription(File transcriptionFile) {
        TranscriptionMetadata metadata = new TranscriptionMetadata(transcriptionFile, false);
        if (metadata.isValid()) {
            transcriptions.put(transcriptionFile, metadata);
            for (String speaker : metadata.getSpeakers().keySet()) {
                speakerMetadata.put(speaker, metadata.getSpeakers().get(speaker));
            }
        }
    }

    public void setSpeakerDistinction(String distinction) {
        this.speakerDistinction = distinction;
    }

    public void setMetadataFields(HashMap<String, String> metadataFields) {
        this.metadataFields = metadataFields;
    }

    public void setCommMeta(HashMap<String, String> commMeta) {
        this.commMeta = commMeta;
    }

    public void setRecMeta(HashMap<String, String> recMeta) {
        this.recMeta = recMeta;
    }

    public void setTransMeta(HashMap<String, String> transMeta) {
        this.transMeta = transMeta;
    }

    public void createCorpus() throws IOException, JexmaraldaException, SAXException {
        Document comaDocument = getComaDocument();
        FileOutputStream out = new FileOutputStream(comaFile);
        ComaXMLOutputter outputter = new ComaXMLOutputter();
        outputter.output(comaDocument, out);
        out.close();
    }

    private Document getComaDocument() {
        Element newCorp = new Element("Corpus");
        Document comaCorpus = new Document(newCorp);
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        comaCorpus.setRootElement(newCorp);
        newCorp.setAttribute("Name", corpusName);
        newCorp.setAttribute("Id", new GUID().makeID());
        newCorp.setAttribute("noNamespaceSchemaLocation", "http://www.exmaralda.org/xml/comacorpus.xsd", xsi);
        String sdx = "";
        if (speakerDistinction.startsWith("ud_")) {
            sdx = "//speaker/ud-speaker-information/ud-information[@attribute-name=\"" + speakerDistinction.substring(3) + "\"]";
        } else if (speakerDistinction.startsWith("@")) {
            sdx = "//speaker/" + speakerDistinction.substring(1);
        } else {
            sdx = "//speaker/" + speakerDistinction;
        }
        newCorp.setAttribute("uniqueSpeakerDistinction", sdx);
        Element corpusDataElement = new Element("CorpusData");
        newCorp.addContent(corpusDataElement);

        int count = 0;
        Vector<File> workFiles = new Vector<>(transcriptions.keySet());
        if (segmentedTranscriptions != null) {
            for (File f : segmentedTranscriptions.keySet()) {
                File keyFile = new File(new TranscriptionMetadata(f, false).getMachineTags().get("EXB-SOURCE"));
                fromSegmentation.put(keyFile, f);
            }
        }

        for (File f : workFiles) {
            count++;
            if (commNames.get("one file -> one communication") == null) {
                Vector<File> v = new Vector<>();
                v.add(f);
                if (fromSegmentation.get(f) != null) {
                    v.add(fromSegmentation.get(f));
                }
                comms.put("unnamed" + count, v);
            } else {
                String key = transcriptions.get(f).getMetadata().get(commNames.get("one file -> one communication"));
                String cName = (key == null) ? "unnamed" + count : transcriptions.get(f).getMetadata().get(commNames.get("one file -> one communication"));
                if (comms.get(cName) == null) {
                    Vector<File> v = new Vector<>();
                    v.add(f);
                    if (fromSegmentation.get(f) != null) {
                        v.add(fromSegmentation.get(f));
                    }
                    comms.put(cName, v);
                } else {
                    comms.get(cName).add(f);
                    if (fromSegmentation.get(f) != null) {
                        comms.get(cName).add(fromSegmentation.get(f));
                    }
                }
            }
        }

        for (String c : comms.keySet()) {
            commSpeakers.put(c, new HashSet<>());
            for (File f : comms.get(c)) {
                for (String sp : transcriptions.get(f).getSpeakers().keySet()) {
                    String spName = transcriptions.get(f).getSpeakers().get(sp).get(speakerDistinction);
                    if (speakers.get(spName) == null) {
                        speakerNames.put(spName, "S" + new GUID().makeID());
                        Vector<String> v = new Vector<>();
                        v.add(sp);
                        speakers.put(spName, v);
                    } else {
                        speakers.get(spName).add(sp);
                    }
                    commSpeakers.get(c).add(spName);
                }
            }
        }

        for (String c : comms.keySet()) {
            HashSet<String> commLanguages = new HashSet<>();
            Element commElement = new Element("Communication");
            commElement.setAttribute("Name", c);
            commElement.setAttribute("Id", "C" + new GUID().makeID());
            Element commDescriptionElement = new Element("Description");
            if (!commMeta.isEmpty()) {
                actualDescription = new HashMap<>();
                for (File trFile : comms.get(c)) {
                    TranscriptionMetadata cMeta = transcriptions.get(trFile);
                    for (String metaKey : cMeta.getMetadata().keySet()) {
                        if (metaKey.startsWith("@language-used")) {
                            commLanguages.add(cMeta.getMetadata().get(metaKey));
                        }
                        if (commMeta.containsKey(metaKey)) {
                            actualDescription.put(commMeta.get(metaKey), cMeta.getMetadata().get(metaKey));
                        }
                    }
                }
                for (String k : new TreeMap<>(actualDescription).keySet()) {
                    Element key = new Element("Key");
                    key.setAttribute("Name", k);
                    key.setText(actualDescription.get(k));
                    commDescriptionElement.addContent(key);
                }
            }
            commElement.addContent(commDescriptionElement);
            Element se = new Element("Setting");
            for (String sp : commSpeakers.get(c)) {
                Element pe = new Element("Person");
                pe.setText(speakerNames.get(sp));
                se.addContent(pe);
            }
            commElement.addContent(se);

            HashMap<String, HashSet<File>> mediaFiles = new HashMap<>();
            Element recDescriptionElement = new Element("Description");
            for (File f : comms.get(c)) {
                if (!transcriptions.get(f).getMediaFiles().isEmpty()) {
                    for (String s : transcriptions.get(f).getMediaFiles()) {
                        if (mediaFiles.get(s) == null) {
                            mediaFiles.put(s, new HashSet<>());
                        }
                        mediaFiles.get(s).add(f);
                    }
                }
                actualDescription = new HashMap<>();
                if (!recMeta.isEmpty()) {
                    TranscriptionMetadata cMeta = transcriptions.get(f);
                    if (cMeta != null) {
                        for (String metaKey : cMeta.getMetadata().keySet()) {
                            if (recMeta.containsKey(metaKey)) {
                                actualDescription.put(recMeta.get(metaKey), cMeta.getMetadata().get(metaKey));
                            }
                        }
                    }
                }
            }
            for (String k : new TreeMap<>(actualDescription).keySet()) {
                Element key = new Element("Key");
                key.setAttribute("Name", k);
                key.setText(actualDescription.get(k));
                recDescriptionElement.addContent(key);
            }
            if (!mediaFiles.isEmpty()) {
                Element rec = new Element("Recording");
                rec.setAttribute("Id", "R" + new GUID().makeID());
                File nf = new File(mediaFiles.keySet().iterator().next());
                int whereDot = nf.getName().lastIndexOf('.');
                if (whereDot > 0 && whereDot <= nf.getName().length() - 2) {
                    rec.addContent(new Element("Name").setText(nf.getName().substring(0, whereDot)));
                } else {
                    rec.addContent(new Element("Name").setText(nf.getName()));
                }
                for (String mf : mediaFiles.keySet()) {
                    HashSet<String> relPaths = new HashSet<>();
                    HashSet<File> absFiles = new HashSet<>();
                    for (File trf : mediaFiles.get(mf)) {
                        File actualMediaFile = null;
                        if (new File(mf).exists()) {
                            actualMediaFile = new File(mf);
                        } else if (new File(trf.getParent() + File.separator + mf).exists()) {
                            actualMediaFile = new File(trf.getParent() + File.separator + mf);
                        }
                        String relativePath = getRelativePath(comaFile, actualMediaFile);
                        if ((!relPaths.contains(relativePath)) && (!absFiles.contains(actualMediaFile))) {
                            Element media = new Element("Media");
                            media.setAttribute("Id", "M" + new GUID().makeID());
                            Element newDescType = new Element("Key");
                            newDescType.setAttribute("Name", "Type");
                            newDescType.setText("digital");
                            Element newDesc = new Element("Description");
                            newDesc.addContent(newDescType);
                            media.addContent(newDesc);
                            Element newNSLink = new Element("NSLink");
                            newNSLink.setText((relativePath == null ? mf : relativePath));
                            media.addContent(newNSLink);
                            rec.addContent(media);
                            relPaths.add(relativePath);
                            absFiles.add(actualMediaFile);
                        }
                    }
                }
                rec.addContent(recDescriptionElement);
                commElement.addContent(rec);
            }

            for (File f : comms.get(c)) {
                Element tr = new Element("Transcription");
                tr.setAttribute("Id", transcriptions.get(f).getId());
                tr.addContent(new Element("Name").setText(f.getName().substring(0, f.getName().lastIndexOf("."))));
                tr.addContent(new Element("Filename").setText(f.getName()));
                tr.addContent(new Element("NSLink").setText(getRelativePath(null, f)));
                Element transDescription = new Element("Description");
                tr.addContent(transDescription);
                Element k = new Element("Key");
                k.setAttribute("Name", "segmented");
                k.setText(transcriptions.get(f).isSegmented() ? "true" : "false");
                transDescription.addContent(k);
                for (String mtk : transcriptions.get(f).getMachineTags().keySet()) {
                    Element key = new Element("Key");
                    key.setAttribute("Name", "# " + mtk);
                    key.setText(transcriptions.get(f).getMachineTags().get(mtk));
                    transDescription.addContent(key);
                }
                if (!transMeta.isEmpty()) {
                    actualDescription = new HashMap<>();
                    TranscriptionMetadata cMeta = transcriptions.get(f);
                    for (String metaKey : cMeta.getMetadata().keySet()) {
                        if (transMeta.containsKey(metaKey)) {
                            actualDescription.put(transMeta.get(metaKey), cMeta.getMetadata().get(metaKey));
                        }
                    }
                    for (String ks : actualDescription.keySet()) {
                        Element key = new Element("Key");
                        key.setAttribute("Name", ks);
                        key.setText(actualDescription.get(ks));
                        transDescription.addContent(key);
                    }
                }
                tr.addContent(new Element("Availability"));
                tr.getChild("Availability").addContent(new Element("Available").setText("false"));
                tr.getChild("Availability").addContent(new Element("ObtainingInformation"));
                commElement.addContent(tr);
            }

            for (String l : commLanguages) {
                Element lang = new Element("Language");
                lang.addContent(new Element("LanguageCode").setText(l));
                commElement.addContent(lang);
            }
            corpusDataElement.addContent(commElement);
        }

        for (String sp : speakers.keySet()) {
            HashMap<String, String> myMeta = new HashMap<>();
            HashSet<String> langs = new HashSet<>();
            HashMap<String, Integer> myMetaCount = new HashMap<>();
            String sexValue = "male";
            HashMap<String, String> langHashMap = new HashMap<>();
            Vector<Element> languages = new Vector<>();
            int l1c = 0;
            int l2c = 0;
            for (String spS : speakers.get(sp)) {
                for (String spM : speakerMetadata.get(spS).keySet()) {
                    if (speakerMetadata.get(spS) != null) {
                        sexValue = speakerMetadata.get(spS).get("@sex");
                        if (spM.equals("@abbreviation")) {
                            myMeta.put(spM, speakerMetadata.get(spS).get(spM));
                        }
                        if (spM.startsWith("@l1")) {
                            if (langHashMap.containsKey("l1")) {
                                if (!(langHashMap.get("l1").equals(speakerMetadata.get(spS).get(spM)))) {
                                    Element lang = new Element("Language");
                                    langs.add(speakerMetadata.get(spS).get(spM));
                                    lang.addContent(new Element("LanguageCode").setText(speakerMetadata.get(spS).get(spM)));
                                    lang.addContent(new Element("Description"));
                                    lang.setAttribute("Type", "L1(" + l1c + ")");
                                    l1c++;
                                    languages.add(lang);
                                }
                            } else {
                                langHashMap.put("l1", speakerMetadata.get(spS).get(spM));
                                langs.add(speakerMetadata.get(spS).get(spM));
                                Element lang = new Element("Language");
                                lang.addContent(new Element("LanguageCode").setText(speakerMetadata.get(spS).get(spM)));
                                lang.addContent(new Element("Description"));
                                lang.setAttribute("Type", "L1");
                                languages.add(lang);
                            }
                        }
                        if (spM.startsWith("@l2")) {
                            if (langHashMap.containsKey("l2")) {
                                if (!langs.contains(speakerMetadata.get(spS).get(spM))) {
                                    langHashMap.put("l2", speakerMetadata.get(spS).get(spM));
                                    langs.add(speakerMetadata.get(spS).get(spM));
                                    Element lang = new Element("Language");
                                    lang.addContent(new Element("LanguageCode").setText(speakerMetadata.get(spS).get(spM)));
                                    lang.addContent(new Element("Description"));
                                    lang.setAttribute("Type", "L2(" + l2c + ")");
                                    l2c++;
                                    languages.add(lang);
                                }
                            } else {
                                langHashMap.put("l2", speakerMetadata.get(spS).get(spM));
                                langs.add(speakerMetadata.get(spS).get(spM));
                                Element lang = new Element("Language");
                                lang.addContent(new Element("LanguageCode").setText(speakerMetadata.get(spS).get(spM)));
                                lang.addContent(new Element("Description"));
                                lang.setAttribute("Type", "L2");
                                languages.add(lang);
                            }
                        }
                        if (speakerMetadata.get(spS).get(spM) != null) {
                            if (!(spM.startsWith("@"))) {
                                if (myMetaCount.get(spM) == null) {
                                    myMetaCount.put(spM, 0);
                                    myMeta.put(spM, speakerMetadata.get(spS).get(spM));
                                } else {
                                    boolean difValue = true;
                                    if ((myMeta.get(spM).equals(speakerMetadata.get(spS).get(spM)))) {
                                        difValue = false;
                                    }
                                    for (int i = 0; i < myMetaCount.get(spM); i++) {
                                        if ((myMeta.get(spM + i).equals(speakerMetadata.get(spS).get(spM)))) {
                                            difValue = false;
                                        }
                                    }
                                    if (difValue) {
                                        myMeta.put(spM + myMetaCount.get(spM), speakerMetadata.get(spS).get(spM));
                                        myMetaCount.put(spM, myMetaCount.get(spM) + 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Element spe = new Element("Speaker");
            spe.setAttribute("Id", speakerNames.get(sp));
            spe.addContent(new Element("Sigle").setText(sp));
            spe.addContent(new Element("Pseudo").setText(myMeta.get("pseudo")));
            spe.addContent(new Element("Sex").setText(sexValue));
            Element spD = new Element("Description");
            for (String ms : myMeta.keySet()) {
                Element spDK = new Element("Key");
                spDK.setAttribute("Name", (ms.startsWith("ud_") ? ms.substring(3) : ms));
                spDK.setText(myMeta.get(ms));
                spD.addContent(spDK);
            }
            spe.addContent(spD);
            for (Element lang : languages) {
                spe.addContent(lang);
            }
            corpusDataElement.addContent(spe);
        }
        return comaCorpus;
    }

    private String getRelativePath(File from, File to) {
        if (from == null) {
            from = comaFile;
        }
        if (to == null) {
            return null;
        }
        URI fromURI = from.getParentFile().toURI();
        URI toURI = to.toURI();
        URI relativeURI = fromURI.relativize(toURI);
        return relativeURI.toString();
    }

    public void doSegmentation(int sa, String suff, File td, int eh, boolean we, File ep, String customFSM) throws IOException, JexmaraldaException, SAXException {
        Vector<File> workFiles = new Vector<>(transcriptions.keySet());
        Vector<File> basicTr = new Vector<>();
        for (File tr : workFiles) {
            if (!transcriptions.get(tr).isSegmented()) {
                basicTr.add(tr);
            }
        }
        TranscriptionSegmentor segmentor = new TranscriptionSegmentor(basicTr.toArray(new File[0]));
        segmentor.doSegmentation(sa, suff, td, eh, we, ep, customFSM);
        segmentedTranscriptions = segmentor.getSegmentedTranscriptions();
        for (File f : segmentedTranscriptions.keySet()) {
            TranscriptionMetadata tm = new TranscriptionMetadata(f, true);
            transcriptions.put(f, tm);
            for (String myS : tm.getSpeakers().keySet()) {
                speakerMetadata.put(myS, tm.getSpeakers().get(myS));
            }
        }
    }
}
