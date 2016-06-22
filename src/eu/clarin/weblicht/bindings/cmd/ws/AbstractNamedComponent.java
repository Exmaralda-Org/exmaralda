package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractNamedComponent extends AbstractComponent {

    @XmlElement(name = "Name", required = true)
    private StringBinding name;
    @XmlElement(name = "Description")
    private StringBinding description;

    protected AbstractNamedComponent() {
    }

    protected AbstractNamedComponent(StringBinding name) {
        this.name = name;
    }

    public StringBinding getName() {
        return name;
    }

    public StringBinding getDescription() {
        return description;
    }
}
