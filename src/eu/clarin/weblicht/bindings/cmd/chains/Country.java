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
@XmlType(name = "", propOrder = {"countryName", "countryCoding"})
public class Country extends AbstractComponent {

    @XmlElement(name = "CountryName", required = true)
    private List<StringBinding> countryName;
    @XmlElement(name = "CountryCoding", required = true)
    private CountryCoding countryCoding;

    private Country() {
    }

    public List<StringBinding> getCountryName() {
        return this.countryName;
    }

    public CountryCoding getCountryCoding() {
        return countryCoding;
    }
}
