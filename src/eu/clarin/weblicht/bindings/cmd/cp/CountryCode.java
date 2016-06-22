package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-Code-clarin.eu.cr1.c_1271859438104", propOrder = {
    "value"
})
public class CountryCode {

    @XmlValue
    protected SimpleCountryCode value;

    public SimpleCountryCode getValue() {
        return value;
    }
}
