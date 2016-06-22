package eu.clarin.weblicht.bindings.cr;

import java.net.URI;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"centerName", "id", "link"})
public class Center {

    @XmlElement(name = "Centername", required = true)
    private String centerName;
    @XmlElement(name = "Center_id", required = true)
    private String id;
    @XmlElement(name = "Center_id_link", required = true)
    private URI link;

    public String getCenterName() {
        return centerName;
    }

    public String getId() {
        return id;
    }

    public URI getLink() {
        return link;
    }
}
