package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Description extends StringBinding {

    @XmlAttribute(name = "type")
    private String type;

    public Description() {
    }

    public Description(String value) {
        super(value);
    }

    public Description(String type, String value) {
        super(value);
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
