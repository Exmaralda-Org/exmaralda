package eu.clarin.weblicht.bindings.cmd.chains;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlType(name = "simpletype-LifeCycleStatus-clarin.eu.cr1.c_1290431694495")
@XmlEnum
public enum SimpleLifeCycleStatus {

    @XmlEnumValue("planned")
    PLANNED("planned"),
    @XmlEnumValue("development")
    DEVELOPMENT("development"),
    @XmlEnumValue("released")
    RELEASED("released"),
    @XmlEnumValue("production")
    PRODUCTION("production"),
    @XmlEnumValue("withdrawn")
    WITHDRAWN("withdrawn"),
    @XmlEnumValue("retired")
    RETIRED("retired"),
    @XmlEnumValue("superseded")
    SUPERSEDED("superseded"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown"),
    @XmlEnumValue("archived")
    ARCHIVED("archived"),
    @XmlEnumValue("published")
    PUBLISHED("published");
    private final String value;

    private SimpleLifeCycleStatus(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleLifeCycleStatus fromValue(String v) {
        for (SimpleLifeCycleStatus c : SimpleLifeCycleStatus.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
