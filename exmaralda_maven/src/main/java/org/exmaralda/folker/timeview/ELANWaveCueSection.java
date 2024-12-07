/*
 * File:     WAVCueSection.java
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
 * This class contains the information of one Cue Section in the tail of a
 * WAV-file: a reference to a Cue Point (i.e. time), duration, language and
 * text/label  Created on Mar 17, 2004
 *
 * @author Alexander Klassmann
 * @version Mar 17, 2004
 */
public class ELANWaveCueSection {
    /** Holds value of property DOCUMENT ME! */
    private final ELANWaveCuePoint cuePoint;

    /** Holds value of property DOCUMENT ME! */
    private final int sampleLength;

    /** Holds value of property DOCUMENT ME! */
    private final String purposeID;

    /** Holds value of property DOCUMENT ME! */
    private final short country;

    /** Holds value of property DOCUMENT ME! */
    private final short language;

    /** Holds value of property DOCUMENT ME! */
    private final short dialect;

    /** Holds value of property DOCUMENT ME! */
    private final short codePage;

    /** Holds value of property DOCUMENT ME! */
    private final String label;

    /**
     * Creates a new WAVCueSection instance
     *
     * @param cuePoint DOCUMENT ME!
     * @param sampleLength DOCUMENT ME!
     * @param purposeID DOCUMENT ME!
     * @param country DOCUMENT ME!
     * @param language DOCUMENT ME!
     * @param dialect DOCUMENT ME!
     * @param codePage DOCUMENT ME!
     * @param label DOCUMENT ME!
     */
    public ELANWaveCueSection(ELANWaveCuePoint cuePoint, int sampleLength,
        String purposeID, short country, short language, short dialect,
        short codePage, String label) {
        this.cuePoint = cuePoint;
        this.sampleLength = sampleLength;
        this.purposeID = purposeID;
        this.country = country;
        this.language = language;
        this.dialect = dialect;
        this.codePage = codePage;
        this.label = label;
    }

    /**
     * returns statring sample point
     *
     * @return WAVCuePoint
     */
    public ELANWaveCuePoint getCuePoint() {
        return cuePoint;
    }

    /**
     * returns Sample Length
     *
     * @return int
     */
    public int getSampleLength() {
        return sampleLength;
    }

    /**
     * returns purpose ID e.g. a value of "scrp" means script text, "capt"
     * means close-caption
     *
     * @return String
     */
    public String getPurposeID() {
        return purposeID;
    }

    /**
     * returns country code
     *
     * @return short
     */
    public short getCountry() {
        return country;
    }

    /**
     * returns language code
     *
     * @return short
     */
    public short getLanguage() {
        return language;
    }

    /**
     * returns dialect code
     *
     * @return short
     */
    public short getDialect() {
        return dialect;
    }

    /**
     * returns Code Page
     *
     * @return short
     */
    public short getCodePage() {
        return codePage;
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
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "Cue Point ID  : " + cuePoint.getID() + "\nSample Length : " +
        sampleLength + "\nPurpose ID    : " + purposeID + "\nCountry       : " +
        country + "\nLanguage      : " + language + "\nDialect       : " +
        dialect + "\nCode Page     : " + codePage + "\nLabel         : " +
        label + "\n";
    }
}
