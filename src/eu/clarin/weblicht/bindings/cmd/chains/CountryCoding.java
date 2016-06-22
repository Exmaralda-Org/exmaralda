package eu.clarin.weblicht.bindings.cmd.chains;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-CountryCoding-clarin.eu.cr1.c_1290431694493", propOrder = {
    "value"
})
public class CountryCoding {

    @XmlValue
    private SimpleCountryCoding value;

    private CountryCoding() {
    }

    public SimpleCountryCoding getValue() {
        return value;
    }
}
