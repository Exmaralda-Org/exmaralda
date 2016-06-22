package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "simpletype-WebServiceType-9-clarin.eu.cr1.c_1320657629669")
@XmlEnum
public enum SimpleWebServiceType {

    SOAP("SOAP"),
    REST("REST"),
    @XmlEnumValue("WebLicht")
    WEB_LICHT("WebLicht");
    private final String value;

    SimpleWebServiceType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SimpleWebServiceType fromValue(String v) {
        for (SimpleWebServiceType c : SimpleWebServiceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
