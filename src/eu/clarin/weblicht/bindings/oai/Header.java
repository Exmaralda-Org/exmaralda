package eu.clarin.weblicht.bindings.oai;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author akislev
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"identifier", "dateStamp", "setSpec"})
public class Header {

    @XmlElement
    private String identifier;
    @XmlElement(name = "datestamp")
    private XMLGregorianCalendar dateStamp;
    @XmlElement(name = "setSpec")
    private String setSpec;

    private Header() {
    }

    public XMLGregorianCalendar getDateStamp() {
        return dateStamp;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSetSpec() {
        return setSpec;
    }

    @Override
    public String toString() {
        return "Header{" + "identifier=" + identifier + ", dateStamp=" + dateStamp + '}';
    }
}
