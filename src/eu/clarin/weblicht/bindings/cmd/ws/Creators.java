package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.Descriptions;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Creators extends AbstractComponent {

    @XmlElement(name = "Descriptions")
    private Descriptions descriptions;
    @XmlElement(name = "Creator", required = true)
    private List<Creator> creators;

    Creators() {
    }

    public Descriptions getDescriptions() {
        return descriptions;
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public Creator getFirstCreator() {
        Creator creator;
        if (creators == null || creators.isEmpty()) {
            creator = new Creator();
            creators = Collections.singletonList(creator);
        } else {
            creator = creators.get(0);
        }
        return creator;
    }

    @Override
    public Creators copy() {
        Creators c = (Creators) super.copy();
        c.descriptions = copy(descriptions);
        c.creators = copy(creators);
        return c;
    }
}
