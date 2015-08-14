/*
 * File:     WAVHeader.java
 * Project:  MPI Linguistic Application
 * Date:     12 December 2007
 *
 * Copyright (C) 2001-2008  Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/*
 * $Id: WAVHeader.java 8519 2007-04-05 15:29:38Z klasal $
 */
package org.exmaralda.folker.timeview;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Reads the Header of a wav-file. If used standalone, it takes the name of the
 * wav-file as first argument.
 *
 * @author Alexander Klassmann
 * @version april 2003
 */
public class ELANWaveHeader {
    /** Holds value of property DOCUMENT ME! */
    public static final short WAVE_FORMAT_UNCOMPRESSED = 0;

    /** Holds value of property DOCUMENT ME! */
    public static final short WAVE_FORMAT_PCM = 1;

    /** Holds value of property DOCUMENT ME! */
    public static final short WAVE_FORMAT_IEEE_FLOAT = 3;

    /** Holds value of property DOCUMENT ME! */
    public static final short WAVE_FORMAT_ALAW = 6;

    /** Holds value of property DOCUMENT ME! */
    public static final short WAVE_FORMAT_MULAW = 7;

    /** Holds value of property DOCUMENT ME! */
    public static final String[] formatDescriptions = {
        "Unknown", "PCM (uncompressed)", "MS ADPCM", "IEEE float", "",
        "IBM CVSD", "8-bit ITU-T G.711 A-law", "8-bit ITU-T G.711 \u00B5-law"
    };
    private HashMap infos = new HashMap();
    private ELANWaveCuePoint[] cuePoints = new ELANWaveCuePoint[0];
    private ELANWaveCueSection[] cueSections = new ELANWaveCueSection[0];
    private char[] dID = new char[4];
    private char[] fID = new char[4];
    private short[] formatSpecific;
    private char[] riff = new char[4];
    private char[] wID = new char[4];
    private int dLen;
    private int fLen;
    private int headerSize;
    private int nAvgBytesPerSec;
    private int nSamplesPerSec;
    private int rLen;
    private long fileSize;
    private short nBlockAlign;
    private short nChannels;
    private short wFormatTag;

    /**
     * Creates a new WAVHeader object.
     *
     * @param fileName Location of the wav file.
     */
    public ELANWaveHeader(String fileName) {
        try {
            read(new RandomAccessFile(fileName, "r"));
        } catch (FileNotFoundException fne) {
            System.out.println("File " + fileName + " not found.");

            return;
        }
    }

    /**
     * Creates a new WAVHeader object.
     *
     * @param soundFile The wav file.
     */
    public ELANWaveHeader(RandomAccessFile soundFile) {
        read(soundFile);
    }

    /**
     * Returns the compression code, one of the following:
     *
     * <ul>
     * <li>
     * 0 - Unknown
     * </li>
     * <li>
     * 1 - PCM/uncompressed
     * </li>
     * <li>
     * 2 - Microsoft ADPCM
     * </li>
     * <li>
     * 6 - ITU G.711 a-law
     * </li>
     * <li>
     * 7 - ITU G.711 mu-law
     * </li>
     * <li>
     * 17 - IMA ADPCM
     * </li>
     * <li>
     * 20 - ITU G.723 ADPCM
     * </li>
     * <li>
     * 49 - GSM 6.10
     * </li>
     * <li>
     * 64 - ITU G.721 ADPCM
     * </li>
     * <li>
     * 80 - MPEG
     * </li>
     * </ul>
     *
     *
     * @return The compression code.
     */
    public short getCompressionCode() {
        return wFormatTag;
    }

    /**
     * returns cue points which may be present in the tail of the file
     *
     * @return WAVCuePoint[]
     */
    public ELANWaveCuePoint[] getCuePoints() {
        return cuePoints;
    }

    /**
     * returns cue section which may be present in the tail of the file
     *
     * @return WAVCueSection[]
     */
    public ELANWaveCueSection[] getCueSections() {
        return cueSections;
    }

    /**
     * Returns the size of the data (in bytes)
     *
     * @return int
     */
    public int getDataLength() {
        return dLen;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Returns the sample frequency (e.g. 44100 for CD)
     *
     * @return int
     */
    public int getFrequency() {
        return nSamplesPerSec;
    }

    /**
     * Returns the size of the header (in bytes)
     *
     * @return int
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * returns summary of info in wav trail
     *
     * @return String
     */
    public String getInfo() {
        StringBuffer info = new StringBuffer();

        for (Iterator iter = infos.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            info.append("\n" + key + " : " + infos.get(key));
        }

        return info.toString();
    }

    /**
     * Returns the number of channels (1 = mono; 2 = stereo)
     *
     * @return short
     */
    public short getNumberOfChannels() {
        return nChannels;
    }

    /**
     * Returns the size (in bytes) of a single sample
     *
     * @return short
     */
    public short getSampleSize() {
        return nBlockAlign;
    }

    /**
     * For standalone use. First parameter has to be the filename of the
     * wav-file
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                ELANWaveHeader wavHeader = new ELANWaveHeader(args[0]);
                System.out.println(wavHeader);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the header of the specified wav-file and sets the attributes of
     * this WAVHeader instance.
     *
     * @param soundFile DOCUMENT ME!
     */
    public void read(RandomAccessFile soundFile) {
        try {
            fileSize = soundFile.length();

            byte[] b;

            //read first 12 bytes (3 groups of 4 bytes: RIFF identifier, size, RIFF type ("WAVE"))
            b = new byte[12];
            soundFile.read(b);

            for (int i = 0; i < 4; i++) {
                riff[i] = (char) b[i];
            }

            rLen = getInt(b[4], b[5], b[6], b[7]);

            for (int i = 0; i < 4; i++) {
                wID[i] = (char) b[8 + i];
            }

            headerSize = 12;

            //read header chunks until reaching "data" identifier
            String chunkID;
            int chunkDataSize;

            while (true) {
                //read chunk header consisting of identifier (4 bytes) and specification of chunk data length (4 bytes)
                b = new byte[8];
                soundFile.read(b);
                headerSize += 8;

                chunkID = getString(b, 4);
                chunkDataSize = getInt(b[4], b[5], b[6], b[7]);

                if ("data".equalsIgnoreCase(chunkID)) { //end of header reached, so break from loop
                    dID = chunkID.toCharArray();
                    dLen = chunkDataSize;

                    break;
                }

                //read contents of chunk
                b = new byte[chunkDataSize];
                soundFile.read(b);

                if ("fmt ".equals(chunkID)) {
                    fID = chunkID.toCharArray();
                    fLen = chunkDataSize;

                    //assign instance attributes
                    //1. parse common fmt bytes
                    wFormatTag = getShort(b[0], b[1]);
                    nChannels = getShort(b[2], b[3]);
                    nSamplesPerSec = getInt(b[4], b[5], b[6], b[7]);
                    nAvgBytesPerSec = getInt(b[8], b[9], b[10], b[11]);
                    nBlockAlign = getShort(b[12], b[13]);

                    //2. parse format-specific bytes
                    int fslength = fLen - 14;
                    formatSpecific = new short[fslength / 2];

                    for (int i = 0; i < fslength; i += 2) {
                        formatSpecific[i / 2] = getShort(b[14 + i], b[15 + i]);
                    }
                } else {
                    // removed 05-08-2010
                    // this goes wrong with some wave files
                    // leading to enourmous sizes of the log file
                    // see (mail from Claudia Scharioth)
                    //System.out.println(chunkID +
                    //    " header found - ignoring contents...");
                }

                headerSize += chunkDataSize;
            }

            if (fileSize > (28 + fLen + dLen)) {
                readCues(soundFile);
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    /**
     * Returns the whole header information in table form
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer output = new StringBuffer("File size: " + fileSize +
                " Bytes");

        //NOTE: byte numbers shown in this toString() method are only correct whe just fmt and data header are present!!!
        try {
            output.append("\n00-03 Letters                 : ").append(riff);
            output.append("\n04-07 Length of rdata chunk   : ").append(rLen);
            output.append("\n================ rdata chunk ===================");
            output.append("\n08-11 Letters                 : ").append(wID);
            output.append("\n=============== format chunk ================");
            output.append("\n12-15 Letters                 : ").append(fID);
            output.append("\n16-19 Length of rest of chunk : ").append(fLen);
            output.append("\n20-21 WAV Format Tag          : ").append(wFormatTag);
            output.append("\n22-23 Number of channels      : ").append(nChannels);
            output.append("\n24-27 Sample frequency        : ").append(nSamplesPerSec);
            output.append("\n28-31 nAvgBytesPerSec         : ").append(nAvgBytesPerSec);
            output.append("\n32-33 nBlockAlign             : ").append(nBlockAlign);

            for (int i = 0; i < formatSpecific.length; i++) {
                output.append("\n").append((34 + (i * 2)) + "-" +
                    (35 + (i * 2)));
                output.append(" Format specific data    : ").append(formatSpecific[i]);
            }

            output.append("\n================ data chunk =================");
            output.append("\n" + (20 + fLen) + "-" + (23 + fLen)).append(" Letters                 : ");
            output.append(dID).append("\n" + (24 + fLen) + "-" + (27 + fLen));
            output.append(" Length of following data: ")
                  .append(dLen + "\n" + (28 + fLen) + "-" + (28 + fLen + dLen))
                  .append(" (data)");

            if (cuePoints.length > 0) {
                output.append("\n================= cue Chunk =================");

                for (int i = 0; i < cuePoints.length; i++) {
                    output.append("\nCue point " + i + ":\n" + cuePoints[i]);
                }
            }

            if (cueSections.length > 0) {
                output.append("\n==================list chunk ================");
                output.append("\n============ labeled text chunk ===========");

                for (int i = 0; i < cueSections.length; i++) {
                    output.append("\nCue section " + i + ":\n" +
                        cueSections[i]);
                }
            }

            output.append(getInfo());
        } catch (NullPointerException e) {
            e.printStackTrace();

            return "";
        }

        return output.toString();
    }

    private static ELANWaveCuePoint[] getCuePoints(RandomAccessFile soundFile)
        throws IOException {
        byte[] b = new byte[4];
        soundFile.read(b); //cueChunkDataSize
        soundFile.read(b);

        int numCuePoints = getInt(b);
        ELANWaveCuePoint[] cuePoints = new ELANWaveCuePoint[numCuePoints];
        int ID;
        int position;
        String dataChunkID;
        int chunkStart;
        int blockStart;
        int sampleOffset;

        for (int i = 0; i < cuePoints.length; i++) {
            soundFile.read(b);
            ID = getInt(b);
            soundFile.read(b);
            position = getInt(b);
            soundFile.read(b);
            dataChunkID = getString(b);
            soundFile.read(b);
            chunkStart = getInt(b);
            soundFile.read(b);
            blockStart = getInt(b);
            soundFile.read(b);
            sampleOffset = getInt(b);

            if ("data".equals(dataChunkID)) {
                cuePoints[i] = new ELANWaveCuePoint(ID, position, chunkStart,
                        blockStart, sampleOffset);
            } else {
                System.out.println("Warning: Reading of cue points failed!");
                System.out.println(
                    "Cannot handle Cue Point with Data Chunk ID '" +
                    dataChunkID + "'");

                return new ELANWaveCuePoint[0];
            }
        }

        return cuePoints;
    }

    private static ELANWaveCueSection getCueSection(RandomAccessFile soundFile,
        ELANWaveCuePoint[] cuePoints) throws IOException {
        byte[] b = new byte[4];
        byte[] s = new byte[2];
        byte[] t;
        ELANWaveCueSection cueSection = null;

        int cuePointID;
        int sampleLength;
        String purposeID;
        short country;
        short language;
        short dialect;
        short codePage;
        String label;

        long seek = soundFile.getFilePointer();
        soundFile.read(b);

        int chunkDataSize = getInt(b);

        soundFile.read(b);
        cuePointID = getInt(b);

        soundFile.read(b);
        sampleLength = getInt(b);

        soundFile.read(b);
        purposeID = getString(b);

        soundFile.read(s);
        country = getShort(s);

        soundFile.read(s);
        language = getShort(s);

        soundFile.read(s);
        dialect = getShort(s);

        soundFile.read(s);
        codePage = getShort(s);

        if ((chunkDataSize - 20 - 1) >= 0) {
            t = new byte[chunkDataSize - 20 - 1];

            // minus cuePoint & language bytes minus string end char
            soundFile.read(t);
            label = getString(t);
        } else {
            label = "";
        }

        for (int i = 0; i < cuePoints.length; i++) {
            if (cuePoints[i].getID() == cuePointID) {
                cueSection = new ELANWaveCueSection(cuePoints[i], sampleLength,
                        purposeID, country, language, dialect, codePage, label);

                break;
            }
        }

        seek += (chunkDataSize + 4);
        seek += (seek % 2); //add 1 if uneven
        soundFile.seek(seek);

        return cueSection;
    }

    private static String getInfo(RandomAccessFile soundFile)
        throws IOException {
        String info = "";
        byte[] b = new byte[4];
        soundFile.read(b);

        int chunkDataSize = getInt(b);

        if (chunkDataSize > 0) {
            byte[] t = new byte[chunkDataSize];
            soundFile.read(t);
            info = getString(t);
        }

        soundFile.seek(soundFile.getFilePointer() +
            (soundFile.getFilePointer() % 2));

        return info;
    }

    private static int getInt(byte[] bytes) {
        return getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    private static int getInt(byte b1, byte b2, byte b3, byte b4) {
        return (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) |
        ((b4 & 0xff) << 24);
    }

    private static short getShort(byte[] s) {
        return getShort(s[0], s[1]);
    }

    private static short getShort(byte b1, byte b2) {
        return (short) ((b1 & 0xff) | ((b2 & 0xff) << 8));
    }

    private static String getString(byte[] bytes) {
        return getString(bytes, bytes.length);
    }

    private static String getString(byte[] bytes, int nrOfBytes) {
        char[] asChar = new char[nrOfBytes];

        for (int i = 0; i < nrOfBytes; i++) {
            asChar[i] = (bytes[i] > 32) ? (char) bytes[i] : ' ';
        }

        return new String(asChar);
    }

    private void readAssociatedDataList(RandomAccessFile soundFile) {
        ArrayList cueSectionList = new ArrayList();
        byte[] b = new byte[4];

        try {
            while (soundFile.getFilePointer() < soundFile.length()) {
                soundFile.read(b);

                if ("ltxt".equals(getString(b))) {
                    ELANWaveCueSection cueSection = getCueSection(soundFile,
                            cuePoints);

                    if (cueSection != null) {
                        cueSectionList.add(cueSection);
                    }
                } else if ("labl".equals(getString(b))) {
                    readCuePointLabels(soundFile, cuePoints);
                } else if ("note".equals(getString(b))) {
                    readCuePointNotes(soundFile, cuePoints);
                } else {
                    break;
                }
            }

            cueSections = (ELANWaveCueSection[]) cueSectionList.toArray(new ELANWaveCueSection[0]);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void readCuePointLabels(RandomAccessFile soundFile,
        ELANWaveCuePoint[] cuePoints) throws IOException {
        byte[] b = new byte[4];
        byte[] t;
        String label;
        long seek = soundFile.getFilePointer();
        soundFile.read(b);

        int chunkDataSize = getInt(b);
        soundFile.read(b);

        int cuePointID = getInt(b);

        if ((chunkDataSize - 4 - 1) >= 0) {
            t = new byte[chunkDataSize - 4 - 1];

            // minus cuePoint bytes minus string end byte (&x00)
            soundFile.read(t);
            label = getString(t);

            for (int i = 0; i < cuePoints.length; i++) {
                if (cuePoints[i].getID() == cuePointID) {
                    cuePoints[i].setLabel(label);

                    break;
                }
            }
        }

        seek += (chunkDataSize + 4);
        seek += (seek % 2); //add 1 if uneven
        soundFile.seek(seek);
    }

    private static void readCuePointNotes(RandomAccessFile soundFile,
        ELANWaveCuePoint[] cuePoints) throws IOException {
        byte[] b = new byte[4];
        byte[] t;
        String label;
        long seek = soundFile.getFilePointer();
        soundFile.read(b);

        int chunkDataSize = getInt(b);
        soundFile.read(b);

        int cuePointID = getInt(b);

        if ((chunkDataSize - 4 - 1) >= 0) {
            t = new byte[chunkDataSize - 4 - 1];

            // minus cuePoint bytes minus string end byte (&x00)
            soundFile.read(t);
            label = getString(t);

            for (int i = 0; i < cuePoints.length; i++) {
                if (cuePoints[i].getID() == cuePointID) {
                    cuePoints[i].setNote(label);

                    break;
                }
            }
        }

        seek += (chunkDataSize + 4);
        seek += (seek % 2); //add 1 if uneven
        soundFile.seek(seek);
    }

    private void readCues(RandomAccessFile soundFile) {
        byte[] b = new byte[4];

        try {
            soundFile.seek(28 + fLen + dLen);

            int listChunkSize = 0;

            while (soundFile.getFilePointer() < soundFile.length()) {
                soundFile.read(b);

                if ("list".equals(getString(b).toLowerCase())) {
                    soundFile.read(b);
                    listChunkSize = getInt(b);
                }

                if ("cue ".equals(getString(b))) {
                    cuePoints = getCuePoints(soundFile);

                    continue;
                } else if ("adtl".equals(getString(b))) {
                    readAssociatedDataList(soundFile);

                    continue;
                } else if ("info".equals(getString(b).toLowerCase())) {
                    long endOfChunk = (soundFile.getFilePointer() +
                        listChunkSize) - 4;

                    while (soundFile.getFilePointer() < endOfChunk) {
                        soundFile.read(b);
                        infos.put(getString(b), getInfo(soundFile));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
