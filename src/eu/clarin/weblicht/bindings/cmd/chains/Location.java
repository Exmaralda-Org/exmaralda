package eu.clarin.weblicht.bindings.cmd.chains;

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
@XmlType(name = "", propOrder = {"address", "region", "continentName", "country"})
public class Location extends AbstractComponent {

    @XmlElement(name = "Address")
    private List<StringBinding> address;
    @XmlElement(name = "Region")
    private List<StringBinding> region;
    @XmlElement(name = "ContinentName")
    private List<StringBinding> continentName;
    @XmlElement(name = "Country", required = true)
    private Country country;

    private Location() {
    }

    public List<StringBinding> getAddress() {
        return this.address;
    }

    public List<StringBinding> getRegion() {
        return this.region;
    }

    public List<StringBinding> getContinentName() {
        return this.continentName;
    }

    public Country getCountry() {
        return country;
    }
}
