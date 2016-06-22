package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class ResourceType {

    @XmlValue
    private SimpleResourceType value;
    @XmlAttribute(name = "mimetype")
    private String mimetype;

    private ResourceType() {
    }

    public ResourceType(SimpleResourceType value, String mimetype) {
        this.value = value;
        this.mimetype = mimetype;
    }

    public SimpleResourceType getValue() {
        return value;
    }

    public String getMimetype() {
        return mimetype;
    }
}
