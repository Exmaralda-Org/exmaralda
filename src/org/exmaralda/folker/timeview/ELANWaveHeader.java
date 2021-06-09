/*
 * $Id$
 */
package org.exmaralda.folker.timeview;


/*
    changed this 09-06-2021 because of issue #268
*/

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Reads the Header of a WAV file. If used standalone, it takes the name of the
 * WAV file as first argument. Access to the file is based on the facilities of
 * {@code RandomAccessFile}.
 *
 * @author Alexander Klassmann
 * @version april 2003
 */
public class ELANWaveHeader {
    /** constant for WAVE uncompressed format */
    public static final short WAVE_FORMAT_UNCOMPRESSED = 0;

    /** constant for WAVE PCM (uncompressed) format */
    public static final short WAVE_FORMAT_PCM = 1;

    /** constant for WAVE IEEE float format */
    public static final short WAVE_FORMAT_IEEE_FLOAT = 3;

    /** constant for WAVE a-law format */
    public static final short WAVE_FORMAT_ALAW = 6;

    /** constant for WAVE mu-law format */
    public static final short WAVE_FORMAT_MULAW = 7;
    /** constant for WAVE Format Extensible */
    public static final short WAVE_FORMAT_EXTENSIBLE = -2; //= 65534 unsigned  

    /** array of WAVE (sub)format descriptions */
    public static final String[] formatDescriptions = {
            "Unknown", "PCM (uncompressed)", "MS ADPCM", "IEEE float", "",
            "IBM CVSD", "8-bit ITU-T G.711 A-law",
            "8-bit ITU-T G.711 \u00B5-law", "WAVE Format Extensible"
        };
    private HashMap<String, String> infos = new HashMap<String, String>();
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
    private long rLenLong;
    private long fileSize;
    private short nBlockAlign;
    private short nChannels;
    private short wFormatTag;
    // HS added the bits per sample value that appears before the extension part of fmt in PCM files
    private short wBitsPerSample;
    private short cbSize; // extension size
    // HS fact chunk additions
    private char[] factID;
    private int factLen;
    private int dwSampleLength;
    // HS March 2017 the size of the data chunk is encoded as unsigned int. The old implementation 
    // in dLen implies a maximum of 2 GB. In order to support the 4 GB of the Wave format, use a long. 
    private long dataLengthLong = 0L;
    // HS Dec 2019 for files that exceed 4GB the first four bytes read "RF64" instead of "RIFF",
    // the 32-bit chunk size field at offset 4 in the file is set to -1, and a "ds64" chunk is
    // inserted before the "fmt" chunk, containing the 64-bits size(s) of the DATA chunk(s)
    private char[] ds64ID;
    private int    ds64Len;
    private long   ds64SampleCount;
    private long   ds64TableLength;
    
    /**
     * Creates a new WAVHeader object.
     *
     * @param fileName location of the WAV file
     */
    public ELANWaveHeader(String fileName) {
        try {
            read(new RandomAccessFile(fileName, "r"));
        } catch (FileNotFoundException fne) {
            System.out.println("File " + fileName + " not found.");

            return;
    	} catch (IOException e) {
    		setInvalid();    		
        }
    }

    /**
     * Creates a new WAVHeader object.
     *
     * @param soundFile The wav file
     */
    public ELANWaveHeader(RandomAccessFile soundFile) {
    	try {
    		read(soundFile);
    	} catch (IOException e) {
    		setInvalid();    		
    	}
    }

    /**
     * Set all members to innocuous values, in case there was
     * no valid file to read;
     */
    private void setInvalid() {
    	headerSize = 0;
    	nChannels = 0;
    	wFormatTag = 0;
    	dLen = 0;
    	dataLengthLong = 0L;
    	fileSize = 0;
    	nSamplesPerSec = 1;
    	nBlockAlign = 1;
        infos = new HashMap<String, String>();
        cuePoints = new ELANWaveCuePoint[0];
        cueSections = new ELANWaveCueSection[0];
    }
    
    /**
     * Returns the compression code.
     * The code is one of the following:
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
     * Returns a string representation of the format and/or compression type 
     * of the file.
     * 
     * @param compressionCode the code as returned by {@link #getCompressionCode()}
     * @return a string representation, a description of the detected compression
     */
    public String getCompressionString(short compressionCode) {
    	switch (compressionCode) {
    	case 0:
    		return formatDescriptions[0];
    	case 1:
    		return formatDescriptions[1];
    	case 2:
    		return formatDescriptions[2];
    	case 6:
    		return formatDescriptions[6];
    	case 7:
    		return formatDescriptions[7];
    	case 17:
    		return "IMA ADPCM";
    	case 20:
    		return "ITU G.723 ADPCM";
    	case 49:
    		return "GSM 6.10";
    	case 64:
    		return "ITU G.721 ADPCM";
    	case 80:
    		return "MPEG";
    	case -2:// 0xfffe or, unsigned, 65534
    		return formatDescriptions[8];
    		default:
    			return formatDescriptions[0];	
    	}    	
    }

    /**
     * Returns cue points which may be present in the tail of the file.
     *
     * @return an array of {@code WAVCuePoint}, may be empty
     */
    public ELANWaveCuePoint[] getCuePoints() {
        return cuePoints;
    }

    /**
     * Returns cue sections which may be present in the tail of the file.
     *
     * @return an array of {@code WAVCueSection}, may be empty
     */
    public ELANWaveCueSection[] getCueSections() {
        return cueSections;
    }

    /**
     * Returns the size of the data (in bytes).
     *
     * @return the length of the data chuck as a long
     */
    public long getDataLength() {
        return dataLengthLong;
    }

    /**
     * Returns the size of the file in bytes.
     * 
     * @return the size of the file in bytes
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Returns the sample frequency (e.g. 44100 Hz for CD).
     *
     * @return the number of samples per second
     */
    public int getFrequency() {
        return nSamplesPerSec;
    }

    /**
     * Returns the size of the header (in bytes)
     *
     * @return the size of the header in bytes
     */
    public int getHeaderSize() {
        return headerSize;
    }

    /**
     * Returns a summary of the info chunk in the tail of the WAV file.
     *
     * @return a multiple line string containing the info key - value pairs 
     */
    public String getInfo() {
        StringBuilder info = new StringBuilder();

        for (Iterator<String> iter = infos.keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            info.append("\n" + key + " : " + infos.get(key));
        }

        return info.toString();
    }

    /**
     * Returns the number of channels (1 = mono; 2 = stereo)
     *
     * @return the number of channels
     */
    public short getNumberOfChannels() {
        return nChannels;
    }

    /**
     * Returns the size (in bytes) of a single sample or all channels.
     *
     * @return the bytes per sample value (also known as BlockAlign)
     */
    public short getSampleSize() {
        return nBlockAlign;
    }

    /**
     * For standalone use, prints the header information. 
     * First parameter has to be the filename of the WAV file.
     *
     * @param args the file path
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
     * Reads the header of the specified WAV file and sets the attributes of
     * this WAVHeader instance.
     *
     * @param soundFile the {@code RandomAccessFile} for the WAV file
     * 
     * @throws IOException any IO exception
     * @throws InvalidHeaderException if the file is not a (proper) WAV file or 
     * an inconsistency is encountered in the header 
     */
    public void read(RandomAccessFile soundFile) throws IOException, InvalidHeaderException {
            fileSize = soundFile.length();

            byte[] b;
            int rc;

            //read first 12 bytes (3 groups of 4 bytes: RIFF identifier, size, RIFF type ("WAVE"))
            b = new byte[12];
            rc = soundFile.read(b);
            if (rc != b.length) {
            	throw new InvalidHeaderException();
            }

            for (int i = 0; i < 4; i++) {
                riff[i] = (char) b[i];
            }
            
            String riffMark = new String(riff);
            if (! "RIFF".equals(riffMark) && ! "RF64".equals(riffMark)) {
            	throw new InvalidHeaderException("neither RIFF nor RF64 marker found");
            }

            rLen = getInt(b[4], b[5], b[6], b[7]);// if -1, rLen will be defined in ds64 chunk

            for (int i = 0; i < 4; i++) {
                wID[i] = (char) b[8 + i];
            }
            if (! "WAVE".equals(new String(wID))) {
            	throw new InvalidHeaderException("WAVE marker not found");
            }

            headerSize = 12;

            //read header chunks until reaching "data" identifier
            String chunkID;
            int chunkDataSize;

            while (true) {
                //read chunk header consisting of identifier (4 bytes) and specification of chunk data length (4 bytes)
                b = new byte[8];
                rc = soundFile.read(b);
                if (rc != b.length) {
                	throw new InvalidHeaderException();
                }
                headerSize += 8;

                chunkID = getString(b, 4);
                chunkDataSize = getInt(b[4], b[5], b[6], b[7]);
                
                if ("data".equalsIgnoreCase(chunkID)) { //end of header reached, so break from loop
                    dID = chunkID.toCharArray();
                    dLen = chunkDataSize;
                    
                    if (chunkDataSize == -1) {
                    	// rf64 data size
                    } else if (chunkDataSize < 0) {
                    	// if the size > 2Gb the integer value will be negative. Try a long value                    
                    	//System.out.println(String.format("Chunk size: %d  %#x", chunkDataSize, chunkDataSize));
                    	//System.out.println(Long.toString(Long.decode(String.format("%#x", chunkDataSize))));
                    	try {
                    		// quick fix as a workaround to convert to a long. Java 1.8 has parseUnsignedLong(String s)
                    		dataLengthLong = Long.decode(String.format("%#x", chunkDataSize));
                    	} catch (NumberFormatException nfe) {
                    		throw new InvalidHeaderException(nfe);
                    	}
                    	if (dataLengthLong < 0) {
                    		throw new InvalidHeaderException("Failed to detect the data chunk size");
                    	} /*else if (dataLengthLong > 4294967295L) {
                    		throw new InvalidHeaderException("File too big, > 4GB");
                    	}*/            	
                    } else {
                    	dataLengthLong = dLen;
                    }
                    
                    break;
                }

                if (chunkDataSize < 0) {
                	throw new InvalidHeaderException("Cannot read the size of the data size chunk");
                }
                //read contents of chunk. 
                b = new byte[chunkDataSize];
	            rc = soundFile.read(b);
	            if (rc != b.length) {
	            	throw new InvalidHeaderException();
	            }
              

                if ("fmt ".equals(chunkID)) {
                	if (chunkDataSize < 14) {
                    	throw new InvalidHeaderException("Failed to read the fmt section");
                	}
                    fID = chunkID.toCharArray();
                    fLen = chunkDataSize;// fLen should be 16, 18 or 40  (not 20 like for some IMA ADPCM?)

                    //assign instance attributes
                    //1. parse common fmt bytes
                    wFormatTag = getShort(b[0], b[1]);
                    nChannels = getShort(b[2], b[3]);
                    nSamplesPerSec = getInt(b[4], b[5], b[6], b[7]);
                    nAvgBytesPerSec = getInt(b[8], b[9], b[10], b[11]);
                    nBlockAlign = getShort(b[12], b[13]);

                    //2. parse format-specific bytes
                    int index = 14;
                    int fslength = fLen - index;
                    // HS for PCM files the wBitsPerSample should be set
                    if  (fslength >= 2) {
                    	wBitsPerSample = getShort(b[14], b[15]);
                    	fslength -= 2;
                    	index += 2;
                    }
                    // if chunkDataSize > 16 the next two bytes should be cbSize, the size of the extension
                    // with value 0 or 22 (not 2 like for some IMA ADPCM files?)
                    formatSpecific = new short[fslength / 2];

                    for (int i = 0; i < fslength; i += 2) {
                        formatSpecific[i / 2] = getShort(b[index + i], b[index + 1 + i]);
                    }
                } else if ("fact".equals(chunkID)){
                	factID = chunkID.toCharArray();
                	factLen = chunkDataSize;
                	dwSampleLength = getInt(b[0], b[1], b[2], b[3]);
                	//System.out.println(fID + " " + fLen + " dwSampleLength: " + dwSampleLength);
                } else if ("ds64".equals(chunkID)) {
                	// RF64 variant, supports files > 4GB
                	ds64ID = chunkID.toCharArray();
                	// chunkDataSize should be at least 28
                	if (chunkDataSize < 28) {
                		throw new InvalidHeaderException(
                				String.format("ds64 chunk data size is invalid (< 28): %s", chunkDataSize));
                	}
                	ds64Len = chunkDataSize;
                	// following sizes are 64bits; 3 fields, 
                	// each consisting of 2 32bits values, high and low 4 byte
                	rLenLong = getLong(b[0], b[1], b[2], b[3],
                			b[4], b[5], b[6], b[7]);
                	dataLengthLong = getLong(b[8], b[9], b[10], b[11],
                			b[12], b[13], b[14], b[15]);
                	ds64SampleCount = getLong(b[16], b[17], b[18], b[19],
                			b[20], b[21], b[22], b[23]);
                	ds64TableLength = getInt(b[24], b[25], b[26], b[27]);
                	headerSize += ds64TableLength;// add to the header size?
                } else if ("r64m".equals(chunkID)) {
                	System.out.println("r64m marker chunk not supported (ignored)");
                	// the header size will be updated, contents ignored
                } else if ("JUNK".equals(chunkID)) {
                	// the chunk data size will be added to the header size, 
                	// this content is ignored
                } else {
                    System.out.println(chunkID +
                        " header found - ignoring contents...");
                }

                headerSize += chunkDataSize;
            }
            
            if (fileSize > (28 + fLen + dataLengthLong)) {
            	try {
            		readCues(soundFile);
            	} catch (IOException e) {
            		// Ignore errors in the cues.
            	}
            }
        //System.out.println(toString());
    }

    /**
     * Returns the whole header information in table form.
     *
     * @see java.lang.Object#toString()
     */
    @Override
	public String toString() {
        StringBuilder output = new StringBuilder("File size: " + fileSize +
                " Bytes");
        output.append("\nHeader Size: "+ headerSize);

        //NOTE: byte numbers shown in this toString() method are only correct when just fmt and data header are present!!!
        try {
            output.append("\n00-03 Letters                 : ").append(riff);
            output.append("\n04-07 Length of rdata chunk   : ").append(rLen);
            output.append("\n================= rdata chunk ==================");
            output.append("\n08-11 Letters                 : ").append(wID);
            if (ds64ID != null) {
            	output.append("\n================== ds64 chunk ==================");
            	output.append("\n12-15 Letters                 : ").append(ds64ID);
            	output.append("\n16-19 Length of rest of chunk : ").append(ds64Len);
            	output.append("\n20-27 Length of rfdata chunk  : ").append(rLenLong);
            	output.append("\n28-35 Length of data chunk    : ").append(dataLengthLong);
            	output.append("\n36-43 Sample count            : ").append(ds64SampleCount);
            	output.append("\n44-47 Table length            : ").append(ds64TableLength);
            	if (ds64TableLength > 0) {
            		output.append("\n44-   Table data              : ");
            	}
            }
            output.append("\n================= format chunk =================");
            output.append("\n12-15 Letters                 : ").append(fID);
            output.append("\n16-19 Length of rest of chunk : ").append(fLen);
            output.append("\n20-21 WAV Format Tag          : ").append(wFormatTag);
            output.append("\n22-23 Number of channels      : ").append(nChannels);
            output.append("\n24-27 Sample frequency        : ").append(nSamplesPerSec);
            output.append("\n28-31 nAvgBytesPerSec         : ").append(nAvgBytesPerSec);
            output.append("\n32-33 nBlockAlign             : ").append(nBlockAlign);
            output.append("\n34-35 wBitsPerSample          : ").append(wBitsPerSample);
            
            for (int i = 0; i < formatSpecific.length; i++) {
                output.append("\n").append((36 + (i * 2)) + "-" +
                    (37 + (i * 2)));
                output.append(" Format specific data    : ").append(formatSpecific[i]);
            }
            //HS 07-2011 added info from "fact" chunk in header
            if (factID != null) {
            	output.append("\n================== fact chunk ==================");
                output.append("\n" + (20 + fLen) + "-" + (23 + fLen) + " Letters                 : ").append(factID);
                output.append("\n" + (24 + fLen) + "-" + (27 + fLen) + " Length of rest of chunk : ").append(factLen);
                output.append("\n" + (28 + fLen) + "-" + (31 + fLen) + " dwSampleLength          : ").append(dwSampleLength);
            }
            
            output.append("\n================== data chunk ==================");
            output.append("\n" + (20 + fLen + factLen) + "-" + (23 + fLen + factLen)).append(" Letters                 : ");
            output.append(dID).append("\n" + (24 + fLen + factLen) + "-" + (27 + fLen + factLen));
            output.append(" Length of following data: ")
                  .append(dataLengthLong + "\n" + (28 + fLen + factLen) + "-" + (28 + fLen + factLen + dataLengthLong))
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
        int rc;
        
        rc = soundFile.read(b); //cueChunkDataSize
        if (rc != b.length) {
        	throw new InvalidHeaderException();
        }

        int numCuePoints = getInt(b, soundFile);
        ELANWaveCuePoint[] cuePoints = new ELANWaveCuePoint[numCuePoints];
        int ID;
        int position;
        String dataChunkID;
        int chunkStart;
        int blockStart;
        int sampleOffset;

        for (int i = 0; i < cuePoints.length; i++) {
            ID = getInt(b, soundFile);
            position = getInt(b, soundFile);
            dataChunkID = getString(b, soundFile);
            chunkStart = getInt(b, soundFile);
            blockStart = getInt(b, soundFile);
            sampleOffset = getInt(b, soundFile);

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

        int chunkDataSize = getInt(b, soundFile);

        cuePointID = getInt(b, soundFile);

        sampleLength = getInt(b, soundFile);

        purposeID = getString(b, soundFile);

        country = getShort(s, soundFile);

        language = getShort(s, soundFile);

        dialect = getShort(s, soundFile);

        codePage = getShort(s, soundFile);

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

        int chunkDataSize = getInt(b, soundFile);

        if (chunkDataSize > 0) {
            byte[] t = new byte[chunkDataSize];
            info = getString(t, soundFile);
        }

        soundFile.seek(soundFile.getFilePointer() +
            (soundFile.getFilePointer() % 2));

        return info;
    }

    /**
     * Compose the first 4 bytes from the array little-endian to an integer.
     * However, RIFF chunk sizes are really unsigned integers, so we won't
     * be able to handle files of 2 GB or larger.
     * @return
     */
    private static int getInt(byte[] bytes) {
        return getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    private static int getInt(byte[] bytes, RandomAccessFile file) throws IOException {
    	int rc = file.read(bytes);
    	if (rc != bytes.length) {
    		throw new InvalidHeaderException();
    	}
        return getInt(bytes[0], bytes[1], bytes[2], bytes[3]);
    }

    /**
     * Compose the byte parameters little-endian to an integer.
     * @param b1 first and least-significant byte
     * @param b2
     * @param b3
     * @param b4 last and most-significant byte
     * 
     * @return the {@code int} value based on the bytes 
     */
    private static int getInt(byte b1, byte b2, byte b3, byte b4) {
        return (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) |
        ((b4 & 0xff) << 24);
    }
    
    /**
     * 32-bits / 4 bytes version of creating a long value. 
     * @param b1 least significant byte
     * @param b2
     * @param b3
     * @param b4 most significant byte
     * 
     * @return the composed value of the 4 bytes as a {@code long}
     */
    private static long getLong(byte b1, byte b2, byte b3, byte b4) {
    	return (long) (b1 & 0xff) | ((b2 & 0xff) << 8) | ((b3 & 0xff) << 16) |
    	        ((b4 & 0xff) << 24);
    }

    /**
     * 64-bits / 8 bytes version of creating a long
     * @return the corresponding value of the 8 bytes as a {@code long}
     */
    private static long getLong(byte b1, byte b2, byte b3, byte b4, 
    		byte b5, byte b6, byte b7, byte b8) {
  	
    	return (getLong(b1, b2, b3, b4) & 0xffffffff) | (getLong(b5, b6, b7, b8) & 0xffffffff) << 32;
    }
    
    private static short getShort(byte[] s) {
        return getShort(s[0], s[1]);
    }

    private static short getShort(byte[] bytes, RandomAccessFile file) throws IOException {
    	int rc = file.read(bytes);
    	if (rc != bytes.length) {
    		throw new InvalidHeaderException();
    	}
        return getShort(bytes[0], bytes[1]);
    }

    private static short getShort(byte b1, byte b2) {
        return (short) ((b1 & 0xff) | ((b2 & 0xff) << 8));
    }

    private static String getString(byte[] bytes) {
        return getString(bytes, bytes.length);
    }

    private static String getString(byte[] bytes, RandomAccessFile file) throws IOException {
    	int rc = file.read(bytes);
    	if (rc != bytes.length) {
    		throw new InvalidHeaderException();
    	}
        return getString(bytes, bytes.length);
    }

    private static String getString(byte[] bytes, int nrOfBytes) {
        char[] asChar = new char[nrOfBytes];

        for (int i = 0; i < nrOfBytes; i++) {
            asChar[i] = (bytes[i] > 32) ? (char) bytes[i] : ' ';
        }

        return new String(asChar);
    }

    private void readAssociatedDataList(RandomAccessFile soundFile)
    		throws InvalidHeaderException, IOException {
        ArrayList<ELANWaveCueSection> cueSectionList = new ArrayList<ELANWaveCueSection>();
        byte[] b = new byte[4];

            while (soundFile.getFilePointer() < soundFile.length()) {
            	String chunkId = getString(b, soundFile);

                if ("ltxt".equals(chunkId)) {
                    ELANWaveCueSection cueSection = getCueSection(soundFile,
                            cuePoints);

                    if (cueSection != null) {
                        cueSectionList.add(cueSection);
                    }
                } else if ("labl".equals(chunkId)) {
                    readCuePointLabels(soundFile, cuePoints);
                } else if ("note".equals(chunkId)) {
                    readCuePointNotes(soundFile, cuePoints);
                } else {
                    break;
                }
            }

            cueSections = cueSectionList.toArray(new ELANWaveCueSection[0]);
    }

    private static void readCuePointLabels(RandomAccessFile soundFile,
        ELANWaveCuePoint[] cuePoints) throws IOException {
        byte[] b = new byte[4];
        byte[] t;
        String label;
        long seek = soundFile.getFilePointer();

        int chunkDataSize = getInt(b, soundFile);

        int cuePointID = getInt(b, soundFile);

        if ((chunkDataSize - 4 - 1) >= 0) {
            t = new byte[chunkDataSize - 4 - 1];

            // minus cuePoint bytes minus string end byte (&x00)
            label = getString(t, soundFile);

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

        int chunkDataSize = getInt(b, soundFile);

        int cuePointID = getInt(b, soundFile);

        if ((chunkDataSize - 4 - 1) >= 0) {
            t = new byte[chunkDataSize - 4 - 1];

            // minus cuePoint bytes minus string end byte (&x00)
            label = getString(t, soundFile);

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

    private void readCues(RandomAccessFile soundFile) throws IOException {
        byte[] b = new byte[4];

            soundFile.seek(28 + fLen + dataLengthLong);

            int listChunkSize = 0;

            while (soundFile.getFilePointer() < soundFile.length()) {
            	//System.out.println("fp1: " + soundFile.getFilePointer() + " l: " + soundFile.length());
                String chunkId = getString(b, soundFile);

                if ("list".equals(chunkId.toLowerCase())) {
                    listChunkSize = getInt(b, soundFile);
                }

                if ("cue ".equals(chunkId)) {
                    cuePoints = getCuePoints(soundFile);

                    continue;
                } else if ("adtl".equals(chunkId)) {
                    readAssociatedDataList(soundFile);

                    continue;
                } else if ("info".equals(chunkId.toLowerCase())) {
                    long endOfChunk = (soundFile.getFilePointer() +
                        listChunkSize) - 4;
                    // HS May 2008: sometimes endOfChunk is greater then the length of the file
                    while (soundFile.getFilePointer() < endOfChunk && soundFile.getFilePointer() < soundFile.length()) {
                    	//System.out.println("fp2: " + soundFile.getFilePointer() + " eoc: " + endOfChunk);
                        String str = getString(b, soundFile);
                        infos.put(str, getInfo(soundFile));
                    }
                } else {
                	// Unknown chunk. Probably the end of the cues...
                	break;
                }
            }
    }
    
    @SuppressWarnings("serial")
	private static class InvalidHeaderException extends IOException {

		public InvalidHeaderException() {
			super();
		}

		public InvalidHeaderException(String message) {
			super(message);
		}

		public InvalidHeaderException(Throwable cause) {
			super(cause);
		}
    	
    };
}
