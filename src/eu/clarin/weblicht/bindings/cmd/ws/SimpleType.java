package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-Type-clarin.eu.cr1.c_1302702320457")
@XmlEnum
public enum SimpleType {

    @XmlEnumValue("Unknown")
    UNKNOWN("Unknown"),
    @XmlEnumValue("Unspecified")
    UNSPECIFIED("Unspecified"),
    @XmlEnumValue("audio")
    AUDIO("audio"),
    @XmlEnumValue("video")
    VIDEO("video"),
    @XmlEnumValue("image")
    IMAGE("image"),
    @XmlEnumValue("document")
    DOCUMENT("document"),
    @XmlEnumValue("drawing")
    DRAWING("drawing"),
    @XmlEnumValue("text")
    TEXT("text");
    private final String value;

    private SimpleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleType fromValue(String v) {
        for (SimpleType c : SimpleType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
