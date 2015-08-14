/*
 * File:     WAVCuePoint.java
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

package org.exmaralda.folker.timeview;


/**
 * This class contains the information of one Cue Point in the tail of a
 * WAV-file, mainly the position (i.e. time) and a possible label (null, if
 * not specified in the file)  Created on Mar 16, 2004
 *
 * @author Alexander Klassmann
 * @version Mar 16, 2004
 */
public class ELANWaveCuePoint {
    /** Holds value of property DOCUMENT ME! */
    private final int ID;

    /** Holds value of property DOCUMENT ME! */
    private final int position;

    /** Holds value of property DOCUMENT ME! */
    private final int chunkStart;

    /** Holds value of property DOCUMENT ME! */
    private final int blockStart;

    /** Holds value of property DOCUMENT ME! */
    private final int sampleOffset;
    private String label = null;
    private String note = null;

    /**
     * Creates a new WAVCuePoint instance
     *
     * @param ID DOCUMENT ME!
     * @param position DOCUMENT ME!
     * @param chunkStart DOCUMENT ME!
     * @param blockStart DOCUMENT ME!
     * @param sampleOffset DOCUMENT ME!
     */
    public ELANWaveCuePoint(int ID, int position, int chunkStart, int blockStart,
        int sampleOffset) {
        this.ID = ID;
        this.position = position;
        this.chunkStart = chunkStart;
        this.blockStart = blockStart;
        this.sampleOffset = sampleOffset;
    }

    /**
     * DOCUMENT ME!
     *
     * @param label DOCUMENT ME!
     */
    protected void setLabel(String label) {
        this.label = label;
    }

    /**
     * DOCUMENT ME!
     *
     * @param note DOCUMENT ME!
     */
    protected void setNote(String note) {
        this.note = note;
    }

    /**
     * returns unique identification value (int!)
     *
     * @return int
     */
    public int getID() {
        return ID;
    }

    /**
     * returns play order position
     *
     * @return int
     */
    public int getPosition() {
        return position;
    }

    /**
     * returns byte Offset of DataChunk
     *
     * @return int
     */
    public int getChunkStart() {
        return chunkStart;
    }

    /**
     * returns Byte Offset to sample of First Channel
     *
     * @return int
     */
    public int getBlockStart() {
        return blockStart;
    }

    /**
     * returns Byte Offset to sample byte of First Channel
     *
     * @return int
     */
    public int getSampleOffset() {
        return sampleOffset;
    }

    /**
     * returns the label
     *
     * @return String
     */
    public String getLabel() {
        return label;
    }

    /**
     * returns the note
     *
     * @return String
     */
    public String getNote() {
        return note;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "ID            : " + ID + "\nPosition      : " + position +
        "\nChunk Start   : " + chunkStart + "\nBlock Start   : " + blockStart +
        "\nSample Offset : " + sampleOffset +
        ((label != null) ? ("\nLabel         : " + label) : "") +
        ((note != null) ? ("\nNote          : " + note) : "") + "\n";
    }
}
