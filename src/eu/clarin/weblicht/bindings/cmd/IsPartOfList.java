package eu.clarin.weblicht.bindings.cmd;

import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"isPartOf"})
public class IsPartOfList {

    @XmlElement(name = "IsPartOf")
    @XmlSchemaType(name = "anyURI")
    private List<String> isPartOf;

    private IsPartOfList() {
    }

    public List<String> getIsPartOf() {
        return this.isPartOf;
    }
}
