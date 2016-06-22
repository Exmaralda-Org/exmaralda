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
@XmlType(name = "complextype-ResourceClass-clarin.eu.cr1.c_1290431694495", propOrder = {
    "value"
})
public class ResourceClass {

    private ResourceClass() {
    }

    public ResourceClass(SimpleResourceClass value) {
        this.value = value;
    }
    @XmlValue
    private SimpleResourceClass value;

    public SimpleResourceClass getValue() {
        return value;
    }
}
