package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractDescribedComponent extends AbstractComponent {

    @XmlElement(name = "Descriptions")
    private Descriptions descriptions;

    protected AbstractDescribedComponent() {
    }

    protected AbstractDescribedComponent(Descriptions descriptions) {
        this.descriptions = descriptions;
    }

    public Descriptions getDescriptions() {
        return descriptions;
    }

    @Override
    public AbstractDescribedComponent copy() {
        AbstractDescribedComponent component = (AbstractDescribedComponent) super.copy();
        component.descriptions = copy(descriptions);
        return component;
    }
}
