package eu.clarin.weblicht.bindings.cmd.ws;

import eu.clarin.weblicht.bindings.cmd.AbstractComponent;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"contact"})
public class Creator extends AbstractComponent {

    @XmlElement(name = "Contact", required = true)
    private Contact contact;

    Creator() {
    }

    public Contact getContact() {
        if (contact == null) {
            contact = new Contact();
        }
        return contact;
    }

    @Override
    public Creator copy() {
        Creator c = (Creator) super.copy();
        c.contact = copy(contact);
        return c;
    }
}