package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import eu.clarin.weblicht.bindings.cmd.Descriptions;
import eu.clarin.weblicht.bindings.cmd.StringBinding;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.*;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "persons",
    "roles",
    "addresses",
    "emails",
    "departments",
    "organisations",
    "telephoneNumbers",
    "faxNumbers",
    "urls",
    "descriptions"
})
public class Contact extends AbstractComponent {

    @XmlElement(name = "Person")
    private List<StringBinding> persons;
    @XmlElement(name = "Role")
    private List<StringBinding> roles;
    @XmlElement(name = "Address")
    private List<StringBinding> addresses;
    @XmlElement(name = "Email")
    private List<StringBinding> emails;
    @XmlElement(name = "Department")
    private List<StringBinding> departments;
    @XmlElement(name = "Organisation")
    private List<StringBinding> organisations;
    @XmlElement(name = "TelephoneNumber")
    private List<StringBinding> telephoneNumbers;
    @XmlElement(name = "FaxNumber")
    private List<StringBinding> faxNumbers;
    @XmlElement(name = "Url")
    @XmlSchemaType(name = "anyURI")
    private List<URI> urls;
    @XmlElement(name = "Descriptions")
    private Descriptions descriptions;

    Contact() {
    }

    public List<StringBinding> getPersons() {
        return persons;
    }

    public List<StringBinding> getRoles() {
        return roles;
    }

    public List<StringBinding> getAddresses() {
        return addresses;
    }

    public List<StringBinding> getEmails() {
        return emails;
    }

    public StringBinding getEmail() {
        if (emails != null && !emails.isEmpty()) {
            return emails.get(0);
        } else {
            return null;
        }
    }

    public void setEmail(StringBinding email) {
        emails = Collections.singletonList(email);
    }

    public List<StringBinding> getDepartments() {
        return departments;
    }

    public List<StringBinding> getOrganisations() {
        return organisations;
    }

    public StringBinding getOrganisation() {
        if (organisations != null && !organisations.isEmpty()) {
            return organisations.get(0);
        } else {
            return null;
        }
    }

    public void setOrganisation(StringBinding organisation) {
        organisations = Collections.singletonList(organisation);
    }

    public List<StringBinding> getTelephoneNumbers() {
        return telephoneNumbers;
    }

    public List<StringBinding> getFaxNumbers() {
        return faxNumbers;
    }

    public List<URI> getUrls() {
        return urls;
    }

    public Descriptions getDescriptions() {
        return descriptions;
    }

    @Override
    public String toString() {
        if (persons != null && persons.size() == 1) {
            return persons.get(0).getValue();
        } else if (emails != null && emails.size() == 1) {
            return emails.get(0).getValue();
        } else if (organisations != null && organisations.size() == 1) {
            return organisations.get(0).getValue();
        } else if (urls != null && urls.size() == 1) {
            return urls.get(0).toString();
        } else {
            return null;
        }
    }

    @Override
    public Contact copy() {
        Contact contact = (Contact) super.copy();
        contact.persons = shallowCopy(persons);
        contact.roles = shallowCopy(roles);
        contact.addresses = shallowCopy(addresses);
        contact.emails = shallowCopy(emails);
        contact.departments = shallowCopy(departments);
        contact.organisations = shallowCopy(organisations);
        contact.telephoneNumbers = shallowCopy(telephoneNumbers);
        contact.faxNumbers = shallowCopy(faxNumbers);
        contact.urls = shallowCopy(urls);
        contact.descriptions = copy(descriptions);
        return contact;
    }
}
