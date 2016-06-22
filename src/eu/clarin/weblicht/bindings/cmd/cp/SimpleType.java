package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-Type-clarin.eu.cr1.c_1320657629668")
@XmlEnum
public enum SimpleType {

    A,
    B,
    C,
    E;

    public String value() {
        return name();
    }

    public static SimpleType fromValue(String v) {
        return valueOf(v);
    }

}
