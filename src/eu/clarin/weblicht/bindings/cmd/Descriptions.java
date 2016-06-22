package eu.clarin.weblicht.bindings.cmd;

import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"descriptions"})
public class Descriptions extends AbstractComponent {

    @XmlElement(name = "Description", required = true)
    private List<Description> descriptions;

    private Descriptions() {
    }

    public Descriptions(Description... descriptions) {
        this.descriptions = Arrays.asList(descriptions);
    }

    public List<Description> getDescriptions() {
        return this.descriptions;
    }

    @Override
    public Descriptions copy() {
        Descriptions descrs = (Descriptions) super.copy();
        descrs.descriptions = shallowCopy(descriptions);
        return descrs;
    }
}
