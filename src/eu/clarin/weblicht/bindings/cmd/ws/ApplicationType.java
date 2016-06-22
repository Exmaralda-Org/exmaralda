package eu.clarin.weblicht.bindings.cmd.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-ApplicationType-clarin.eu.cr1.c_1320657629647", propOrder = {
    "value"
})
public class ApplicationType {

    @XmlValue
    private SimpleApplicationType value;

    private ApplicationType() {
    }

    public ApplicationType(SimpleApplicationType value) {
        this.value = value;
    }

    public SimpleApplicationType getValue() {
        return value;
    }

    public void setValue(SimpleApplicationType value) {
        this.value = value;
    }
}
