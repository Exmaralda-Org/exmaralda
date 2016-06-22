package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.net.URI;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractValue extends AbstractRefBinding {

    @XmlElement(name = "Value", required = true)
    private StringBinding value;
    @XmlElement(name = "Description")
    private StringBinding description;
    @XmlElement(name = "DataCategory")
    @XmlSchemaType(name = "anyURI")
    private URI dataCategory;

    protected AbstractValue() {
    }

    protected AbstractValue(StringBinding value) {
        this.value = value;
    }

    public StringBinding getValue() {
        return value;
    }

    public StringBinding getDescription() {
        return description;
    }

    public URI getDataCategory() {
        return dataCategory;
    }
}
