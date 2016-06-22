package eu.clarin.weblicht.bindings.cmd.cp;

import eu.clarin.weblicht.bindings.cmd.AbstractRefBinding;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"clarinOrganisationInfo", "administrativeContact", "technicalContact"})
public class OrganisationalInformation extends AbstractRefBinding {

    @XmlElement(name = "ClarinOrganisationInfo", required = true)
    private ClarinOrganisationInfo clarinOrganisationInfo;
    @XmlElement(name = "AdministrativeContact", required = true)
    private OrganisationalInformation.AdministrativeContact administrativeContact;
    @XmlElement(name = "TechnicalContact", required = true)
    private OrganisationalInformation.TechnicalContact technicalContact;

    public ClarinOrganisationInfo getClarinOrganisationInfo() {
        return clarinOrganisationInfo;
    }

    public OrganisationalInformation.AdministrativeContact getAdministrativeContact() {
        return administrativeContact;
    }

    public OrganisationalInformation.TechnicalContact getTechnicalContact() {
        return technicalContact;
    }

    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"contact"})
    public static class AdministrativeContact extends AbstractRefBinding {

        @XmlElement(name = "Contact", required = true)
        private Contact contact;

        public Contact getContact() {
            return contact;
        }
    }

    @XmlAccessorType(value = XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {"contact"})
    public static class TechnicalContact extends AbstractRefBinding {

        @XmlElement(name = "Contact", required = true)
        private Contact contact;

        public Contact getContact() {
            return contact;
        }
    }
}
