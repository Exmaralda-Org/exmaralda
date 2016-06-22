package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-MetadataScheme-9-clarin.eu.cr1.c_1320657629669")
@XmlEnum
public enum SimpleMetadataScheme {

    OLAC,
    CMDI;

    public String value() {
        return name();
    }

    public static SimpleMetadataScheme fromValue(String v) {
        return valueOf(v);
    }
}
