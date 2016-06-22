package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
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
@XmlType(name = "", propOrder = {"name", "type", "status", "description", "country"})
public class CenterBasicInformation extends AbstractComponent {

    @XmlElement(name = "Name", required = true)
    private List<StringBinding> name;
    @XmlElement(name = "Type", required = true)
    private Type type;
    @XmlElement(name = "Status")
    private String status;
    @XmlElement(name = "Description", required = true)
    private String description;
    @XmlElement(name = "Country", required = true)
    private Country country;

    public List<StringBinding> getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public Country getCountry() {
        return country;
    }
}
