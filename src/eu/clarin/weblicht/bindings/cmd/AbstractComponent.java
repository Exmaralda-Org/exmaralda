package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractComponent extends AbstractRefBinding {

    @XmlAttribute(name = "ComponentId")
    @XmlSchemaType(name = "anyURI")
    private String componentId;

    protected AbstractComponent() {
    }

    public String getComponentId() {
        return componentId;
    }
}
