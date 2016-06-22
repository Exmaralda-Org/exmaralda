package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-ApplicationType-clarin.eu.cr1.c_1320657629647")
@XmlEnum
public enum SimpleApplicationType {

    @XmlEnumValue("webService")
    WEB_SERVICE("webService");
    private final String value;

    SimpleApplicationType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleApplicationType fromValue(String v) {
        for (SimpleApplicationType c: SimpleApplicationType .values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
