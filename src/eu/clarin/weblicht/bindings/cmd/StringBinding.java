package eu.clarin.weblicht.bindings.cmd;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author akislev
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"value"})
public class StringBinding {

    @XmlValue
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String value;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    private String lang;

    public StringBinding() {
    }

    public StringBinding(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getLang() {
        return lang;
    }

    @Override
    public String toString() {
        return value;
    }
}
