package eu.clarin.weblicht.bindings.cmd.cp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"centerProfile"})
public class Components {

    @XmlElement(name = "CenterProfile", required = true)
    private CenterProfile centerProfile;

    public CenterProfile getCenterProfile() {
        return centerProfile;
    }
}
