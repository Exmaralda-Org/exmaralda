package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-Quality-clarin.eu.cr1.c_1302702320457")
public class Quality {

    @XmlValue
    private String value;

    private Quality() {
    }

    public String getValue() {
        return value;
    }
}
