package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"person", "address", "email", "organisation", "telephone", "website"})
public class Contact extends AbstractComponent {

    @XmlElement(name = "Person")
    private List<String> person;
    @XmlElement(name = "Address")
    private List<String> address;
    @XmlElement(name = "Email")
    private List<String> email;
    @XmlElement(name = "Organisation")
    private List<StringBinding> organisation;
    @XmlElement(name = "Telephone")
    private List<String> telephone;
    @XmlElement(name = "Website")
    @XmlSchemaType(name = "anyURI")
    private List<String> website;

    public List<String> getPerson() {
        return this.person;
    }

    public List<String> getAddress() {
        return this.address;
    }

    public List<String> getEmail() {
        return this.email;
    }

    public List<StringBinding> getOrganisation() {
        return this.organisation;
    }

    public List<String> getTelephone() {
        return this.telephone;
    }

    public List<String> getWebsite() {
        return this.website;
    }
}
