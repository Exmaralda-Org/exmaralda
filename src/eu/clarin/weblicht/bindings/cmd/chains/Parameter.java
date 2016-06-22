package eu.clarin.weblicht.bindings.cmd.chains;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class Parameter {

    @XmlAttribute(name = "value")
    private String value;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    private String lang;

    private Parameter() {
    }

    public Parameter(String name) {
        this.name = name;
    }

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getLang() {
        return lang;
    }
}
