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
@XmlType(name = "", propOrder = {"centerBasicInformation", "centerExtendedInformation"})
public class CenterProfile extends AbstractRefBinding {
    @XmlElement(name = "CenterBasicInformation", required = true)
    private CenterBasicInformation centerBasicInformation;
    @XmlElement(name = "CenterExtendedInformation", required = true)
    private CenterExtendedInformation centerExtendedInformation;

    public CenterBasicInformation getCenterBasicInformation() {
        return centerBasicInformation;
    }

    public CenterExtendedInformation getCenterExtendedInformation() {
        return centerExtendedInformation;
    }
    
}
