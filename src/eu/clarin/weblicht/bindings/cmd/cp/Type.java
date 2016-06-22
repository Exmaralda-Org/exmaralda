package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "complextype-Type-clarin.eu.cr1.c_1320657629668", propOrder = {
    "value"
})
public class Type {

    @XmlValue
    private SimpleType value;

    public SimpleType getValue() {
        return value;
    }

}
