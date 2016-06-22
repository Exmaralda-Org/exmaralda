package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-SegmentationUnit-clarin.eu.cr1.c_1290431694523")
@XmlEnum
public enum SimpleSegmentationUnit {

    @XmlEnumValue("graphem")
    GRAPHEM("graphem"),
    @XmlEnumValue("phonem")
    PHONEM("phonem"),
    @XmlEnumValue("morphem")
    MORPHEM("morphem"),
    @XmlEnumValue("syllable")
    SYLLABLE("syllable"),
    @XmlEnumValue("lexeme")
    LEXEME("lexeme"),
    @XmlEnumValue("phrase")
    PHRASE("phrase"),
    @XmlEnumValue("orthographic sentence")
    ORTHOGRAPHIC_SENTENCE("orthographic sentence"),
    @XmlEnumValue("grammatical sentence")
    GRAMMATICAL_SENTENCE("grammatical sentence"),
    @XmlEnumValue("paragraph")
    PARAGRAPH("paragraph"),
    @XmlEnumValue("text unit")
    TEXT_UNIT("text unit"),
    @XmlEnumValue("text")
    TEXT("text"),
    @XmlEnumValue("other")
    OTHER("other"),
    @XmlEnumValue("discourse unit")
    DISCOURSE_UNIT("discourse unit");
    private final String value;

    private SimpleSegmentationUnit(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleSegmentationUnit fromValue(String v) {
        for (SimpleSegmentationUnit c : SimpleSegmentationUnit.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
