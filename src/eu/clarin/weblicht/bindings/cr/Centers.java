package eu.clarin.weblicht.bindings.cr;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Centers")
public class Centers {

    @XmlElement(name = "CenterProfile", required = true)
    private List<Center> centerProfiles;

    public List<Center> getCenterProfiles() {
        return centerProfiles;
    }
}
